package manager.service.books;


import com.alibaba.fastjson2.JSON;
import manager.SelfXManagerSpringbootApplication;
import manager.booster.MultipleLangHelper;
import manager.cache.CacheOperator;
import manager.data.MultipleItemsResult;
import manager.solr.SolrFields;
import manager.solr.data.SolrSearchResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.exception.LogicException;
import manager.booster.SecurityBooster;
import manager.service.FilesService;
import manager.system.*;
import manager.system.books.*;
import manager.system.books.BookStyle;
import manager.util.SelfXCollectionUtils;
import manager.util.locks.UserLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    FilesService filesService;

    private static final Logger log = LoggerFactory.getLogger(SelfXManagerSpringbootApplication.class);


    private static final Integer BOOK_DEFAULT_STATUS = SharingBookStatus.OPENED;

    private static final Integer BOOK_DEFAULT_DISPLAY_PATTERN = SharingBookDisplayPatterns.LIST;

    private static final Integer BOOK_DEFAULT_STYLE = BookStyle.GREEN.getDbCode();

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

    @Override
    public MultipleItemsResult<SharingBook> getBooks(long loginId, Integer state) {
        MultipleItemsResult<SharingBook> books = operator.getBooks(loginId, state);
        fill(books);
        return books;
    }

    private static void fill(MultipleItemsResult<SharingBook> books) {
        books.items.forEach(item -> {
            item.setUpdaterEncodedId(SecurityBooster.encodeUnstableCommonId(item.getUpdaterId()));
            item.setUpdaterId(null);
        });
    }

    @Override
    public void updateBookPropsSyncly(long loginId, String bookId, Map<String, Object> updatingAttrs) {
        locker.lockByUserAndClass(loginId, () -> {
            cache.saveBook(loginId, bookId, () -> operator.updateBook(bookId, loginId, loginId, updatingAttrs));
        });
    }

    @Override
    public void updatePageNodePropsSyncly(long loginId, String pageNodeId, Map<String, Object> updatingAttrs) {
        if (updatingAttrs.keySet().stream()
                .anyMatch(one -> one.equals(SolrFields.PARENT_IDS)
                        || one.equals(SolrFields.INDEXES)
                )) {
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

    @Override
    public void changeNode(long loginId, String nodeId, List<String> parentIds, List<Double> indexes) {
        /**
         * 必须只管当下的
         */
        updatePageNodeParentIdsAndIndexesSyncly(loginId, nodeId, parentIds, indexes);
    }

    public void updatePageNodeParentIdsAndIndexesSyncly(long loginId, String nodeId, List<String> parentIds, List<Double> indexes) {
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
            updateBookPropsSyncly(loginId, id, param);
        }
    }

    @Override
    public SharingBook getBook(long loginId, String id) {
        return cache.getBook(loginId, id, () -> operator.getBook(loginId, id));
    }

    @Override
    public PageNode getPageNode(long loginId, String id) {
        return cache.getPageNode(loginId, id, () -> operator.getPageNode(loginId, id));
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

    private void refreshPageChildrenNums(Boolean isRoot, String id, String bookId, long loginId) {
        if (isRoot) {
            return;
        }
        long count = operator.countPagesForSpecificParentId(generateParentIdForSubPages(id), bookId, loginId);
        Map<String, Object> params = new HashMap<>();
        params.put(SolrFields.CHILDREN_NUM, count);
        updatePageNodePropsSyncly(loginId, id, params);
    }

    private static String generatePageParentId(String bookId, String parentId, boolean isRoot) {
        return isRoot ? generateParentIdForRootPages(bookId) : generateParentIdForSubPages(parentId);
    }

    private static String generateParentIdForRootPages(String bookId) {
        return BooksConstants.SHARING_BOOK_PURE + "_" + bookId;
    }

    private static String generateParentIdForSubPages(String parentId) {
        return BooksConstants.PAGE_NODE_PURE + "_" + parentId;
    }

    @Override
    public MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId, Boolean isRoot) {
        return operator.getPageNodes(loginId, bookId, generatePageParentId(bookId, parentId, isRoot));
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
        PageNode node = getPageNode(loginId, pageId);
        if (node == null) {
            /**
             * 出现环了而已 我只要确保删掉所有的数据即可
             */
            return;
        }
        removeParentId(node, generatePageParentId(bookId, parentId, isRoot));
        locker.lockByUserAndClass(loginId, () -> {
            if (node.getParentIds().isEmpty()) {
                /**
                 * 对于树的操作 应该先删除子 再删除该节点 it's right
                 * 这里相反就是为了防止出现由于环导致的无限递归（尽管想定没有）
                 * 如果出现圆了 之后会由于空指针结束这个循环
                 */
                cache.deletePageNode(loginId, pageId, () -> operator.deletePageNodeById(loginId, pageId));
                operator.getPageNodesByParentIdForDelete(loginId, bookId, generatePageParentId(bookId, pageId, false))
                        .forEach(one -> {
                            deletePageNode(loginId, bookId, pageId, false, one.getId());
                        });
            } else {
                updatePageNodeParentIdsAndIndexesSyncly(loginId, pageId, node.getParentIds(), node.getIndexes());
            }
        });
    }


    @Override
    public void deleteBook(long loginId, String bookId) {
        locker.lockByUserAndClass(loginId, () -> {
            cache.deleteBook(loginId, bookId, () -> {
                operator.deleteBookById(loginId, bookId);
                operator.deletePageNodesByBookId(loginId, bookId);
            });
        });
    }

    @Override
    public SolrSearchResult<SharingBook> searchBooks(long loginId, String searchInfo, Integer pageNum, Boolean searchAllVersions,
                                                     List<String> searchVersions, Integer fragSize) {
        return operator.searchBooks(loginId, searchInfo, pageNum, searchAllVersions, searchVersions,fragSize);
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
