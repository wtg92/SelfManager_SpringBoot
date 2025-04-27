package manager.service.books;


import com.alibaba.fastjson2.JSON;
import manager.SelfXManagerSpringbootApplication;
import manager.booster.MultipleLangHelper;
import manager.booster.UserIsolator;
import manager.cache.CacheOperator;
import manager.data.MultipleItemsResult;
import manager.solr.SelfXCores;
import manager.solr.SolrFields;
import manager.solr.data.ParentNode;
import manager.solr.data.SolrSearchRequest;
import manager.solr.data.SolrSearchResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.exception.LogicException;
import manager.booster.SecurityBooster;
import manager.service.FilesService;
import manager.system.*;
import manager.system.books.*;
import manager.system.books.BookStyle;
import manager.util.ReflectUtil;
import manager.util.SelfXCollectionUtils;
import manager.util.locks.UserLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 明晰如何处理文件：
 * 文件的实际删除由FileService处理
 * 任何一个fileRecord都仅属于一个pageNode
 * 相关函数：
 * updatePageNodePropsSyncly ----> 处理的是 更新页时 图片删除或新增 （条件：只有更新file_ids时） ----> 会处理文件的删除
 * <p>
 * 当有操作节点的长操作时 就应该用book状态 锁住
 * 1.本身是有锁的 因此长时间的操作 会停滞操作 ，此时我需要一个没有锁的地方 通知前台用户 拦下来
 * 2.节点操作有本身的复杂性 处理节点及子节点时 我希望一次只处理一个
 */
@Service
public class BooksServiceImpl implements BooksService {

    @Resource
    private UserLockManager locker;

    @Resource
    private BooksSolrOperator operator;

    @Resource
    CacheOperator cache;

    @Resource
    SecurityBooster securityBooster;

    @Resource
    BookStatusLocker bookStatusLocker;

    @Resource
    FilesService filesService;

    private static final Logger log = LoggerFactory.getLogger(SelfXManagerSpringbootApplication.class);


    private static final Integer BOOK_DEFAULT_STATUS = SharingBookStatus.OPENED;

    private static final Integer BOOK_DEFAULT_DISPLAY_PATTERN = SharingBookDisplayPatterns.LIST;

    private static final Integer BOOK_DEFAULT_STYLE = BookStyle.GREEN.getDbCode();

    @Override
    public MultipleItemsResult<SharingBook> getBooks(long loginId, List<Integer> states) {
        MultipleItemsResult<SharingBook> books = operator.getBooks(loginId, states);
        return fill(loginId, books);
    }

    @Override
    public SharingBook getBook(long loginId, String id) {
        return fill(loginId, cache.getBook(loginId, id, () -> operator.getBook(loginId, id)));
    }

    @Override
    public PageNode getPageNode(long loginId, String id) {
        return cache.getPageNode(loginId, id, () -> operator.getPageNode(loginId, id));
    }


    @Override
    public void createBook(long loginId, String name, String defaultLanguage, String comment) {
        /**
         * unnecessary but do not harm
         */
        if (Language.get(defaultLanguage) == Language.UNKNOWN) {
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR, "IMPOSSIBLE lang " + defaultLanguage);
        }

        locker.lockByUserAndClass(loginId, () -> {
            SharingBook book = new SharingBook();
            book.setDefaultLang(defaultLanguage);

            /**
             * 生成对应值
             */
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.NAME, defaultLanguage, name);
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.COMMENT, defaultLanguage, comment);

            /**
             * 确定一系列初始值
             */
            book.setStatus(BOOK_DEFAULT_STATUS);
            book.setDisplayPattern(BOOK_DEFAULT_DISPLAY_PATTERN);
            book.setSeqWeight(0);
            book.setStyle(BOOK_DEFAULT_STYLE);
            operator.insertBook(book, loginId);
        });
    }


    private MultipleItemsResult<SharingBook> fill(long loginId, MultipleItemsResult<SharingBook> books) {
        books.items.forEach(one -> fill(loginId, one));
        return books;
    }

    private SolrSearchResult<SharingBook> fill(long loginId, SolrSearchResult<SharingBook> books) {
        books.items.forEach(one -> fill(loginId, one));
        return books;
    }


    private SharingBook fill(long loginId, SharingBook item) {
        item.setUpdaterEncodedId(SecurityBooster.encodeUnstableCommonId(item.getUpdaterId()));
        item.setUpdaterId(null);
        bookStatusLocker.fill(loginId, item);
        return item;
    }

    @Override
    public void updateBookPropsInSync(long loginId, String bookId, Map<String, Object> updatingAttrs) {
        locker.lockByUserAndClass(loginId, () -> {
            cache.saveBook(loginId, bookId, () -> operator.updateBook(bookId, loginId, loginId, updatingAttrs));
            if (updatingAttrs.containsKey(SolrFields.STATUS)) {
                cache.removeClosedBookIds(loginId);
            }
        });
    }

    @Override
    public void updatePageNodeProps(long loginId, String bookId, String pageId, Map<String, Object> updatingAttrs) {
        updatePageNodePropsInSync(loginId, pageId, updatingAttrs);
    }


    private void updatePageNodePropsInSync(long loginId, String pageNodeId, Map<String, Object> updatingAttrs) {
        if (updatingAttrs.containsKey(SolrFields.PARENT_IDS) || updatingAttrs.containsKey(SolrFields.INDEXES)) {
            /*
             * ParentNodes的更新必须用特定API
             */
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
        }
        locker.lockByUserAndClass(loginId, () -> {
            Runnable savingNode = () -> cache.savePageNode(loginId, pageNodeId, () -> operator.updatePageNode(pageNodeId, loginId, loginId, updatingAttrs));

            if (!updatingAttrs.containsKey(SolrFields.FILE_IDS)) {
                savingNode.run();
            } else {
                List<String> toUpdate = updatingAttrs.get(SolrFields.FILE_IDS) == null ? null : (List<String>) updatingAttrs.get(SolrFields.FILE_IDS);
                List<String> inDB = getPageNode(loginId, pageNodeId).getFileIds();
                SelfXCollectionUtils.ComparisonResult<String> fileIdsComparisonResult = SelfXCollectionUtils.compareLists(toUpdate, inDB, String::equals);
                /**
                 * 对于处理fileRecords 需要在实际更新成功后再处理
                 */
                savingNode.run();
                /**
                 * 被更新掉的
                 */
                fileIdsComparisonResult.onlyInList2.forEach(encodedFileIds -> {
                    long fileToDelete = securityBooster.getStableCommonId(encodedFileIds);
                    filesService.deleteFileRecord(loginId, fileToDelete);
                });
            }
        });
    }

    private void updatePageNodeParentIdsAndIndexesInSync(long loginId, String nodeId, List<String> parentIds, List<Double> indexes) {
        checkPageNodeLegal(parentIds, indexes);
        locker.lockByUserAndClass(loginId, () -> {
            Map<String, Object> param = new HashMap<>();
            param.put(SolrFields.PARENT_IDS, parentIds);
            param.put(SolrFields.INDEXES, indexes);
            cache.savePageNode(loginId, nodeId, () -> operator.updatePageNode(nodeId, loginId, loginId, param));
        });
    }


    @Override
    public void closeBook(long loginId, String id) {
        //确定封存吗？（如果该笔记本不包含笔记页且不含备注，会被<em>直接删除</em>
        Map<String, Object> param = new HashMap<>();
        if (operator.countPagesForSpecificParentId
                (generateParentIdForRootPages(id), id, loginId) == 0) {
            deleteBook(loginId, id);
        } else {
            param.put(SolrFields.STATUS, SharingBookStatus.CLOSED);
            updateBookPropsInSync(loginId, id, param);
            cache.removeClosedBookIds(loginId);
        }
    }


    @Override
    public void createPage(long loginId, String bookId, String name, String lang, String parentId, Boolean isRoot, Double index) {
        locker.lockByUserAndClass(loginId, () -> {
            PageNode page = new PageNode();
            page.setBookId(bookId);
            page.setParentIds(Collections.singletonList(generatePageParentId(bookId, parentId, isRoot)));
            page.setIndexes(Collections.singletonList(index));
            page = MultipleLangHelper.setFiledValue(page, BooksMultipleFields.NAME, lang, name);
            page.setWithTODOs(false);
            page.setIsHidden(false);
            page.setChildrenNum(0);
            page.setType(PageNodeType.PAGE);
            page.setSrcType(SelfXDataSrcTypes.BY_USERS);
            page.setFileIds(new ArrayList<>());
            operator.insertPage(page, loginId);

            refreshPageChildrenNums(isRoot, parentId, bookId, loginId);
        });
    }

    @Override
    public void copySinglePageNodeFromTheOwner(long loginId, String srcId, String targetId, String bookId, String parentId, Boolean isRoot, Double index) {
        locker.lockByUserAndClass(loginId, () -> {
//            PageNode page = getPageNode(loginId,srcId);
//
//            page.setId(null);
//            page.setBookId(bookId);
//            page.setParentIds();
//
//            List<Double> indexes = page.getIndexes();
//            String toAddParentId = generatePageParentId(bookId,parentId,isRoot);
//
//            /*
//             * 同一层级的话 相当于移动 只更换index即可
//             * */
//            int existingIndex = parentIds.indexOf(toAddParentId);
//            if (existingIndex != -1) {
//                indexes.set(existingIndex, index);
//            } else {
//                parentIds.add(toAddParentId);
//                indexes.add(index);
//            }
//
//            updatePageNodeParentIdsAndIndexesSyncly(loginId,id,parentIds,indexes);
//            refreshPageChildrenNums(isRoot, parentId, bookId, loginId);
        });
    }


    @Override
    public void addPageParentNode(long loginId, String id, String bookId, String parentId, Boolean isRoot, Double index) {
        locker.lockByUserAndClass(loginId, () -> {
            PageNode page = getPageNode(loginId, id);
            List<String> parentIds = page.getParentIds();
            if (parentIds.size() > SelfX.MAX_DB_LINES_IN_ONE_SELECTS) {
                throw new LogicException(SelfXErrors.PARENT_PAGE_REACH_OUT_MAX_LIMIT
                        , SelfX.MAX_DB_LINES_IN_ONE_SELECTS);
            }
            List<Double> indexes = page.getIndexes();
            String toAddParentId = generatePageParentId(bookId, parentId, isRoot);

            /*
             * 同一层级的话 相当于移动 只更换index即可
             * */
            int existingIndex = parentIds.indexOf(toAddParentId);
            if (existingIndex != -1) {
                indexes.set(existingIndex, index);
            } else {
                parentIds.add(toAddParentId);
                indexes.add(index);
            }

            updatePageNodeParentIdsAndIndexesInSync(loginId, id, parentIds, indexes);
            refreshPageChildrenNums(isRoot, parentId, bookId, loginId);
        });
    }

    private void refreshPageChildrenNums(Boolean isRoot, String id, String bookId, long loginId) {
        if (isRoot) {
            return;
        }
        long count = operator.countPagesForSpecificParentId(generateParentIdForSubPages(id), bookId, loginId);
        Map<String, Object> params = new HashMap<>();
        params.put(SolrFields.CHILDREN_NUM, count);
        updatePageNodePropsInSync(loginId, id, params);
    }

    private static boolean isBookParentId(String pId) {
        return pId.startsWith(PARENT_ID_OF_BOOK_PREFIX);
    }

    private static String extractPureParentId(String pId) {
        if (isBookParentId(pId)) {
            return pId.substring(PARENT_ID_OF_BOOK_PREFIX.length());
        } else {
            return pId.substring(PARENT_ID_OF_PAGE_NODE_PREFIX.length());
        }
    }

    private static String generatePageParentId(String bookId, String parentId, boolean isRoot) {
        return isRoot ? generateParentIdForRootPages(bookId) : generateParentIdForSubPages(parentId);
    }

    private static final String PARENT_ID_OF_BOOK_PREFIX = BooksConstants.SHARING_BOOK_PURE + "_";

    private static final String PARENT_ID_OF_PAGE_NODE_PREFIX = BooksConstants.PAGE_NODE_PURE + "_";


    private static String generateParentIdForRootPages(String bookId) {
        return PARENT_ID_OF_BOOK_PREFIX + bookId;
    }

    private static String generateParentIdForSubPages(String parentId) {
        return PARENT_ID_OF_PAGE_NODE_PREFIX + parentId;
    }

    @Override
    public MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId, Boolean isRoot) {
        return operator.getPageNodes(loginId, bookId, generatePageParentId(bookId, parentId, isRoot));
    }

    private void deleteSinglePageNodeWithFileIds(long loginId, PageNode node) {
        String pageId = node.getId();
        locker.lockByUserAndClass(loginId, () -> {
            cache.deletePageNode(loginId, pageId, () -> operator.deletePageNodeById(loginId, pageId));
            if (node.getFileIds() != null) {
                node.getFileIds().forEach(encodedFileIds -> {
                    long fileToDelete = securityBooster.getStableCommonId(encodedFileIds);
                    filesService.deleteFileRecord(loginId, fileToDelete);
                });
            }

        });
    }

    private static class PageDeleteTask {
        String parentId;
        Boolean isRoot;
        String pageId;

        PageDeleteTask(String parentId, Boolean isRoot, String pageId) {
            this.parentId = parentId;
            this.isRoot = isRoot;
            this.pageId = pageId;
        }
    }


    /**
     * 曾经由于性能考虑而思考在一个会话内 进行递归操作
     * 但实际上 Solr没有会话机制
     * 所谓的Client只不过是一个制作HTTP的东西罢了
     * 没有所谓的状态概念
     * 因此这样就是最快的了
     */
    @Override
    public void deletePageNode(long loginId, String bookId, String parentId, Boolean isRoot, String pageId) {

        Deque<PageDeleteTask> stack = new ArrayDeque<>();
        PageDeleteTask originalTarget = new PageDeleteTask(parentId, isRoot, pageId);
        stack.push(originalTarget);
        Set<String> visited = new HashSet<>(); // 避免环导致重复处理
        try {
            bookStatusLocker.startDeletingPage(loginId, bookId);
            locker.lockByUserAndClass(loginId, () -> {
                long counter = 0;
                while (!stack.isEmpty()) {
                    counter++;
                    if (counter > 100) {
                        System.err.println("算法错误");
                        throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
                    }
                    PageDeleteTask task = stack.pop();
                    String currentPageId = task.pageId;

                    if (!visited.add(currentPageId)) {
                        continue; // 避免环
                    }

                    PageNode node = getPageNode(loginId, currentPageId);
                    if (node == null) {
                        log.error("理论上不该出现的deletePageNode出现损坏的环 ID → " + UserIsolator.calculateCoreNamByUser(SelfXCores.SHARING_BOOK, loginId));
                        continue;
                    }

                    removeParentId(node, generatePageParentId(bookId, task.parentId, task.isRoot));

                    if (!node.getParentIds().isEmpty()) {
                        updatePageNodeParentIdsAndIndexesInSync(loginId, currentPageId, node.getParentIds(), node.getIndexes());
                        continue;
                    }

                    deleteSinglePageNodeWithFileIds(loginId, node);

                    List<PageNode> children = operator.getPageNodesByParentIdForDelete(
                            loginId, bookId, generatePageParentId(bookId, currentPageId, false)
                    );
                    for (PageNode child : children) {
                        stack.push(new PageDeleteTask(currentPageId, false, child.getId()));
                    }
                }
                /*
                 * --- 补充：处理孤立环 ---
                 * 到这里为止，按照正常父子路径能遍历到的节点都清理完了。
                 * 但是如果存在孤立环，需要额外探测处理。
                 */
                Set<String> visitedForIsolation = new HashSet<>();
                Queue<String> queue = new LinkedList<>();
                List<PageNode> allNodes = new ArrayList<>();

                queue.add(originalTarget.pageId);
                visitedForIsolation.add(originalTarget.pageId);

                while (!queue.isEmpty()) {
                    String currentId = queue.poll();

                    PageNode current = getPageNode(loginId, currentId);

                    if(current == null){
                        //此时有可能为null 说明之前被删掉了 很好 感觉上只有第一次会出现
                        continue;
                    }

                    allNodes.add(current);
                    /*
                     * 找到所有子
                     */
                    List<String> children =  operator
                            .getPageNodesByParentIdForDelete(loginId,bookId,generatePageParentId(bookId, currentId, false))
                            .stream().map(PageNode::getId).toList();

                    for (String childId : children) {
                        if (!visitedForIsolation.contains(childId)) {
                            queue.add(childId);
                            visitedForIsolation.add(childId);
                        }
                    }
                }
                /*
                 * 局部图的所有点
                 */
                Set<String> allNodeIds = allNodes.stream().map(PageNode::getId).collect(Collectors.toSet());
                boolean hasExternalParent = false;
                for (PageNode node : allNodes) {
                    for (String pId : node.getParentIds()) {
                        String pureParentId = extractPureParentId(pId);
                        if (!allNodeIds.contains(pureParentId)) {
                            hasExternalParent = true;
                            break;
                        }
                    }
                    if (hasExternalParent) {
                        break;
                    }
                }
                if (!hasExternalParent) {
                    // 说明是孤立子图，可以全部安全删除
                    for (PageNode node : allNodes) {
                        deleteSinglePageNodeWithFileIds(loginId, node);
                    }
                }
            });
        } finally {
            bookStatusLocker.endDeletingPage(loginId, bookId);
        }
    }

    public List<PageNode> findShortestPathToRoot(PageNode startNode, Function<String, PageNode> idToNodeMap) {
        Map<String, String> cameFrom = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(startNode.getId());
        visited.add(startNode.getId());

        String rootId = null;

        long counter = 0;

        while (!queue.isEmpty()) {
            counter++;
            if (counter > 100) {
                System.err.println("代码BUG");
                throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
            }

            String currentId = queue.poll();
            PageNode currentNode = idToNodeMap.apply(currentId);
            if (currentNode == null) {
                System.err.println("SHOULDN'T BE THIS" + idToNodeMap);
                continue;
            }
            // 如果是根节点（没有父ID）
            if (currentNode.getParentIds().stream().anyMatch(BooksServiceImpl::isBookParentId)) {
                rootId = currentId;
                break;
            }

            for (String processedId : currentNode.getParentIds()) {
                String parentId = extractPureParentId(processedId);
                if (!visited.contains(parentId)) {
                    visited.add(parentId);
                    cameFrom.put(parentId, currentId);
                    queue.offer(parentId);
                }
            }
        }

        // 从 rootId 回溯路径
        List<PageNode> path = new LinkedList<>();
        if (rootId != null) {
            String currentId = rootId;
            while (currentId != null) {
                PageNode node = idToNodeMap.apply(currentId);
                if (node != null) path.add(node);
                currentId = cameFrom.get(currentId);
            }
        }

        return path;
    }

    /**
     * 长时间操作 因此需要修改状态
     *
     * @param loginId
     * @param bookId
     */
    @Override
    public void deleteBook(long loginId, String bookId) {
        try{
            bookStatusLocker.startDeletingBook(loginId, bookId);
            locker.lockByUserAndClass(loginId, () -> {
                List<PageNode> nodes = operator.getPageNodesByBookIdForDelete(loginId, bookId);
                nodes.forEach(one -> deleteSinglePageNodeWithFileIds(loginId, one));
                cache.deleteBook(loginId, bookId, () -> operator.deleteBookById(loginId, bookId));
            });
        }finally {
            bookStatusLocker.endDeletingBook(loginId, bookId);
        }
    }

    @Override
    public SolrSearchResult<SharingBook> searchBooks(long loginId, SolrSearchRequest searchRequest) {
        return fill(loginId, operator.searchBooks(loginId, searchRequest));
    }


    @Override
    public SolrSearchResult<PageNode> searchPageNodes(long loginId, SolrSearchRequest searchRequest) {
        List<String> closedBookIds = cache.getClosedBookIds(loginId, () -> operator.getBookIdsByState(loginId, Collections.singletonList(SharingBookStatus.CLOSED)));
        SolrSearchResult<PageNode> pageNodeSolrSearchResult = operator.searchPageNodes(loginId, searchRequest, closedBookIds);

        List<String> bookFields = ReflectUtil.getFiledNamesByPrefix(SharingBook.class, MultipleLangHelper.getFiledPrefix(SolrFields.NAME));
        bookFields.addAll(Arrays.asList(SolrFields.ID, SolrFields.DEFAULT_LANG));

        pageNodeSolrSearchResult.items.forEach((node) -> {
            SharingBook book =
                    ReflectUtil.filterFields(getBook(loginId, node.getBookId()), bookFields);
            node.setBook(book);

        });
        return pageNodeSolrSearchResult;
    }

    @Override
    public List<ParentNode<?>> getAllParentNodes(long loginId, String id) {
        PageNode pageNode = getPageNode(loginId, id);
        List<String> parentIds = pageNode.getParentIds();
        Function<String, ParentNode<?>> mapper = pId -> getOriginalParentId(pId, loginId);
        return parentIds.stream().map(mapper).toList();
    }

    @Override
    public List<PageNode> calculatePath(long loginId, String id) {
        PageNode pageNode = getPageNode(loginId, id);
        return findShortestPathToRoot(
                pageNode,
                i -> getPageNode(loginId, i)
        );
    }


    private ParentNode<?> getOriginalParentId(String pId, long loginId) {
        if (isBookParentId(pId)) {
            ParentNode<SharingBook> parentNode = new ParentNode<>();
            parentNode.isBook = true;
            String bookId = extractPureParentId(pId);
            parentNode.base = getBook(loginId, bookId);
            return parentNode;
        } else {
            ParentNode<PageNode> parentNode = new ParentNode<>();
            parentNode.isBook = false;
            String pageId = extractPureParentId(pId);
            parentNode.base = getPageNode(loginId, pageId);
            return parentNode;
        }
    }

    private static void checkPageNodeLegal(List<String> parentIds, List<Double> indexes) {
        if (parentIds.stream().distinct().count() != parentIds.size()) {
            throw new LogicException(SelfXErrors.INCONSISTENT_DB_ERROR, " parentIds msg:" + parentIds.stream().distinct().count() + " vs " + parentIds.size());
        }
        if (parentIds.size() != indexes.size()) {
            throw new LogicException(SelfXErrors.INCONSISTENT_DB_ERROR, " indexes msg:" + indexes.size() + " vs " + parentIds.size());
        }
    }

    private static void removeParentId(PageNode node, String parentId) {

        int index = -1;
        List<String> parentIds = node.getParentIds();
        for (int i = 0; i < parentIds.size(); i++) {
            String existedParentId = parentIds.get(i);
            if (existedParentId.equals(parentId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.err.println(JSON.toJSONString(node));
            System.err.println(parentId);
            throw new LogicException(SelfXErrors.INCONSISTENT_DB_ERROR);
        }

        node.getParentIds().remove(index);
        node.getIndexes().remove(index);
    }
}
