package manager.service.books;


import com.alibaba.fastjson2.JSON;
import manager.SelfXManagerSpringbootApplication;
import manager.booster.MultipleLangHelper;
import manager.booster.CoreNameProducer;
import manager.booster.longRunningTasks.LongRunningTaskType;
import manager.booster.longRunningTasks.LongRunningTasksScheduler;
import manager.cache.CacheOperator;
import manager.data.MultipleItemsResult;
import manager.solr.data.SharingLinkDetail;
import manager.solr.data.SharingLinkPatchReq;
import manager.data.general.FinalHandler;
import manager.solr.SelfXCores;
import manager.solr.SolrFields;
import manager.solr.books.SharingLink;
import manager.solr.data.*;
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
import org.springframework.beans.factory.annotation.Value;
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
 * updatePageNodePropsInSync ----> 处理的是 更新页时 图片删除或新增 （条件：只有更新file_ids时） ----> 会处理文件的删除
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

    @Resource
    SharingLinksAgent sharingLinksAgent;
    @Resource
    LongRunningTasksScheduler longRunningTasksScheduler;

    @Value("${books.max_size_of_page}")
    private Integer MAX_SIZE_OF_ONE_BOOK;


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
        return getPageInternal(loginId, id);
    }

    private PageNode getPageInternal(long loginId, String id) {
        return cache.getPageNode(loginId, id, () -> operator.getPageNode(loginId, id));
    }

    private void checkBookMaxNumOfPage(long loginId, String bookId) {
        long maxPageNum = operator.countPagesByBook(loginId, bookId);
        if (maxPageNum > MAX_SIZE_OF_ONE_BOOK) {
            throw new LogicException(SelfXErrors.PAGES_SIZE_REACHED_MAX, maxPageNum, MAX_SIZE_OF_ONE_BOOK);
        }
    }

    @Override
    public String createLink(long loginId, String name, String defaultLanguage, String bookId, Boolean isCommunityLink) {
        /*
         * unnecessary but do not harm
         */
        if (Language.get(defaultLanguage) == Language.UNKNOWN) {
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR, "IMPOSSIBLE lang " + defaultLanguage);
        }
        FinalHandler<String> id = new FinalHandler<>();
        locker.lockByUserAndClass(loginId, () -> {
            SharingLink link = new SharingLink();
            link.setDefaultLang(defaultLanguage);
            /*
             * 生成对应值
             */
            link = MultipleLangHelper.setFiledValue(link, BooksMultipleFields.NAME, defaultLanguage, name);

            /**
             * 确定一系列初始值
             */
            link.setStatus(SharingLinkStatus.DRAFT);
            if (getBook(loginId, bookId) == null) {
                throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
            }

            link.setBookId(bookId);
            link.setUserId(loginId);

            link.setExtra(new SharingLinkExtra().toString());
            link.setSettings(new SharingLinkSettings().toString());

            SharingLinkPermission sharingLinkPermission = new SharingLinkPermission();
            sharingLinkPermission.readPerms.noLimit = true;
            link.setPerms(sharingLinkPermission.toString());

            link.setCopyNum(0);
            link.setLikesNum(0);
            link.setLikeUser(new ArrayList<>());
            link.setDislikesNum(0);
            link.setDislikeUsers(new ArrayList<>());
            link.setTags(new ArrayList<>());

            id.val = operator.insertLink(link, loginId, isCommunityLink);
        });
        return id.val;
    }

    private void checkLinkOperationsPerm(long loginId, Boolean isCommunityLink, String id) {
        SharingLink link = getLinkInternally(loginId, isCommunityLink, id);
        if (link.getUserId() != loginId) {
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
        }
    }

    @Override
    public MultipleItemsResult<SharingLink> getLinks(long loginId, String bookId, Boolean isCommunityLink) {
        MultipleItemsResult<SharingLink> links = operator.getLinks(loginId, bookId, isCommunityLink);

        links.items.forEach(link -> {
            if (link.getContentId() != null) {
                link.setContent(getPageInternal(loginId, link.getContentId()));
            }
            String url = sharingLinksAgent.generateURL(link.getId(), loginId, bookId, isCommunityLink);
            link.setSharingLink(url);
        });

        return links;
    }

    @Override
    public void deleteLink(long loginId, Boolean isCommunityLink, String id) {
        locker.lockByUserAndClass(loginId, () -> {
            checkLinkOperationsPerm(loginId, isCommunityLink, id);
            cache.deleteLink(loginId, isCommunityLink, id, () -> operator.deleteLinkById(loginId, isCommunityLink, id));
        });
    }

    @Override
    public SharingLink getLinkByOwner(long loginId, Boolean isCommunityLink, String id) {
        checkLinkOperationsPerm(loginId, isCommunityLink, id);
        return getLinkInternally(loginId, isCommunityLink, id);
    }

    @Override
    public void updateLink(long loginId, SharingLinkPatchReq param) {
        checkLinkOperationsPerm(loginId, param.isCommunityLink, param.id);
        SharingLink link = getLinkByOwner(loginId, param.isCommunityLink, param.id);
        if (!Objects.equals(link.getStatus(), SharingLinkStatus.DRAFT)) {
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR, link.getStatus());
        }

        updateLinkPropsInSync(loginId, param.isCommunityLink, param.id, SharingLinksAgent.transferSolrUpdateParams(param));
    }

    @Override
    public void switchLinkStatus(long loginId, Boolean isCommunityLink, String id, Integer status) {
        checkLinkOperationsPerm(loginId, isCommunityLink, id);
        Map<String, Object> params = new HashMap<>();
        params.put(SolrFields.STATUS, status);
        updateLinkPropsInSync(loginId, isCommunityLink, id, params);
    }

    @Override
    public SharingLinkDetail getLinkDetail(Long loginId, String encoding) {
        SharingLinkDetail detail = new SharingLinkDetail(encoding);
        //step1 --> get the link
        SharingLinksAgent.EncryptionParams params = sharingLinksAgent.decode(encoding);
        SharingLink link = getLinkByURLParams(params);
        sharingLinksAgent.fill(detail, loginId, link, params);

        return detail;
    }

    private SharingLink getLinkByURLParams(SharingLinksAgent.EncryptionParams params) {
        return getLinkInternally(params.loginId, params.isCommunityLink, params.id);
    }

    /*!!!由于 使用前必须校验权限*/
    private SharingLink getLinkInternally(long loginId, Boolean isCommunityLink, String id) {
        SharingLink link = cache.getLink(loginId, isCommunityLink, id, () ->
                {
                    SharingLink link1 = operator.getLink(loginId, isCommunityLink, id);
                    if (link1 == null) {
                        throw new LogicException(SelfXErrors.LINK_BECAME_NULL);
                    }
                    return link1;
                }
        );
        link.setDecodedPerm(SharingLinkPermission.analyze(link.getPerms()));
        link.setPerms(null);
        return link;
    }

    @Override
    public String createBook(long loginId, String name, String defaultLanguage, String comment) {
        /*
         * unnecessary but do not harm
         */
        if (Language.get(defaultLanguage) == Language.UNKNOWN) {
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR, "IMPOSSIBLE lang " + defaultLanguage);
        }
        FinalHandler<String> id = new FinalHandler<>();
        locker.lockByUserAndClass(loginId, () -> {
            SharingBook book = new SharingBook();
            book.setDefaultLang(defaultLanguage);

            /*
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
            id.val = operator.insertBook(book, loginId);
        });
        return id.val;
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
                cache.removeClosedBookIdsFromCache(loginId);
            }
        });
    }

    @Override
    public void updatePageNodeProps(long loginId, String bookId, String pageId, Map<String, Object> updatingAttrs) {
        updatePageNodePropsInSync(loginId, pageId, updatingAttrs);
    }


    private void updateLinkPropsInSync(long loginId, Boolean isCommunity, String id, Map<String, Object> updatingAttrs) {
        locker.lockByUserAndClass(loginId, () -> {
            Runnable savingNode = () -> cache.saveLink(loginId, isCommunity, id, () -> operator.updateLink(id, isCommunity, loginId, loginId, updatingAttrs));

            if (!updatingAttrs.containsKey(SolrFields.FILE_IDS)) {
                savingNode.run();
            } else {
//                List<String> toUpdate = updatingAttrs.get(SolrFields.FILE_IDS) == null ? null : (List<String>) updatingAttrs.get(SolrFields.FILE_IDS);
//                List<String> inDB = getPageNode(loginId, pageNodeId).getFileIds();
//                SelfXCollectionUtils.ComparisonResult<String> fileIdsComparisonResult = SelfXCollectionUtils.compareLists(toUpdate, inDB, String::equals);
//                /**
//                 * 对于处理fileRecords 需要在实际更新成功后再处理
//                 */
//                savingNode.run();
//                /**
//                 * 被更新掉的
//                 */
//                fileIdsComparisonResult.onlyInList2.forEach(encodedFileIds -> {
//                    long fileToDelete = securityBooster.getStableCommonId(encodedFileIds);
//                    filesService.deleteFileRecord(loginId, fileToDelete);
//                });
            }
        });
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

    private void updatePageNodeParentIdsAndIndexesInSyncThenRefreshPageChildrenNum(long loginId, String nodeId, List<String> parentIds, List<Double> indexes, String refreshParentId, String refreshBookId) {
        checkPageNodeLegal(parentIds, indexes);
        locker.lockByUserAndClass(loginId, () -> {
            Map<String, Object> param = new HashMap<>();
            param.put(SolrFields.PARENT_IDS, parentIds);
            param.put(SolrFields.INDEXES, indexes);
            cache.savePageNode(loginId, nodeId, () -> operator.updatePageNode(nodeId, loginId, loginId, param));

            refreshPageChildrenNums(refreshParentId, refreshBookId, loginId);
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
            cache.removeClosedBookIdsFromCache(loginId);
        }
    }


    @Override
    public String createPage(long loginId, String bookId, String name, String lang, String parentId, Double index) {
        checkBookMaxNumOfPage(loginId, bookId);
        FinalHandler<String> id = new FinalHandler<>();
        locker.lockByUserAndClass(loginId, () -> {
            PageNode page = new PageNode();
            page.setBookId(bookId);
            page.setParentIds(Collections.singletonList(generatePageParentId(bookId, parentId)));
            page.setIndexes(Collections.singletonList(index));
            page = MultipleLangHelper.setFiledValue(page, BooksMultipleFields.NAME, lang, name);
            page.setWithTODOs(false);
            page.setIsHidden(false);
            page.setChildrenNum(0);
            page.setType(PageNodeType.PAGE);
            page.setSrcType(SelfXDataSrcTypes.BY_USERS);
            page.setFileIds(new ArrayList<>());
            id.val = operator.insertPage(page, loginId);
            refreshPageChildrenNums(parentId, bookId, loginId);
        });
        return id.val;
    }

    private PageNode copyOnePageNodeAndRefreshPageNum(long loginId, PageNode copyTarget
            , long loginIdForCopyTarget
            , String copyDestBookId
            , String copyDestParentId
            , Double copyDestIndex) {
        PageNode clone = copyPureNodeFromOne(loginId, copyTarget, loginIdForCopyTarget);
        clone.setBookId(copyDestBookId);
        clone.setParentIds(Collections.singletonList(generatePageParentId(copyDestBookId, copyDestParentId)));
        clone.setIndexes(Collections.singletonList(copyDestIndex));
        clone.setChildrenNum(0);
        String copyId = operator.insertPage(clone, loginId);
        refreshPageChildrenNums(copyDestParentId, copyDestBookId, loginId);
        clone.setId(copyId);
        return clone;
    }

    /**
     * 需要复制什么？
     * 1.name
     * 2.CONTENT
     * 3.WithTODOs
     * 4.variables
     * 需要修改生成的有什么？
     * 1.filesId
     * 2.EDITOR_STATE   -------> 包含File -----> Replace 非法字符 反射
     * 需要和创建页面初始化一样的
     * 1.isHidden
     * 需要置空 从而上层决定的
     * 1.bookId
     * 2.parentIds
     * 3.indexes
     * 4.id
     * 5.childrenName
     */
    private PageNode copyPureNodeFromOne(long loginId, PageNode one, long loginIdForCopyTarget) {
        PageNode clone = one.clone();


        if (clone.getFileIds() != null && !clone.getFileIds().isEmpty()) {
            /*
            这个用来校验是否含有FileIds
            文本中 不允许出现特定的字符 -- 我认为该字段是读者输入的
         */
            List<String> contentsField = ReflectUtil.getFiledNamesByPrefix(PageNode.class, SolrFields.CONTENT);
            for (String fieldName : contentsField) {
                String stringFieldValue = ReflectUtil.getStringFieldValue(clone, fieldName);
                if (stringFieldValue != null) {
                    for (String fieldId : clone.getFileIds()) {
                        if (stringFieldValue.contains(fieldId)) {
                            throw new LogicException(SelfXErrors.ILLEGAL_STR, fieldId);
                        }
                    }
                }
            }
            /*
             * 校验成功 正式开始复制了
             */
            Map<String, String> copyingFileIdsMapper = new HashMap<>();
            for (String fieldId : clone.getFileIds()) {
                Map<String, Object> srcParams = new HashMap<>();
                srcParams.put("pageNodeLoginId", loginIdForCopyTarget);
                srcParams.put("nodeId", one.getId());
                /*
                 * 重新produce一个Field Record
                 * replace state里的file
                 */
                String copyingId = filesService.copyFileRecord(loginId, fieldId, srcParams);
                copyingFileIdsMapper.put(fieldId, copyingId);
            }

            List<String> editorStatesFields = ReflectUtil.getFiledNamesByPrefix(PageNode.class, SolrFields.EDITOR_STATE);

            for (String fieldName : editorStatesFields) {
                String stringFieldValue = ReflectUtil.getStringFieldValue(clone, fieldName);
                if (stringFieldValue != null) {
                    copyingFileIdsMapper.forEach((oldId, copyingId) -> {
                        ReflectUtil.setFiledValue(clone, fieldName, stringFieldValue.replaceAll(oldId, copyingId));
                    });
                }
            }

        }

        clone.setIsHidden(false);
        /*
         * 需要置空 从而上层决定的
         * 1.id
         * 2.bookId
         * 3.parentIds
         * 4.indexes
         * 5.childrenName
         */
        clone.setId(null);
        clone.setBookId(null);
        clone.setParentIds(null);
        clone.setIndexes(null);
        clone.setChildrenNum(null);

        return clone;
    }


    @Override
    public void copySinglePageNodeFromTheOwner(long loginId, String srcId, String bookId, String parentId, Double index) {
        checkBookMaxNumOfPage(loginId, bookId);
        bookStatusLocker.startCopying(loginId, bookId);
        try {
            locker.lockByUserAndClass(loginId, () -> {
                PageNode page = getPageNode(loginId, srcId);
                copyOnePageNodeAndRefreshPageNum(loginId, page, loginId, bookId, parentId, index);
            });
        } finally {
            bookStatusLocker.endCopying(loginId, bookId);
        }
    }

    /**
     * 同一本书
     * 1.移动同层 只相当于增加上级页
     * 2.非同层 相当于先增加上级页 再删除当前连接
     * <p>
     * 不同书
     * 1.复制页及子节点
     * 2.删除当前连接
     */
    @Override
    public void movePageNodeAndSub(long loginId, String srcId, String srcBookId, String srcParentId, String targetBookId, String targetParentId, Double targetIndex) {
        if (srcBookId.equals(targetBookId)) {
            addPageParentNode(loginId, srcId, targetBookId, targetParentId, targetIndex);
            if (!srcParentId.equals(targetParentId)) {
                deletePageNode(loginId, srcBookId, srcParentId, srcId);
            }
        } else {
            copyPageNodeAndSubFromTheOwner(LongRunningTaskType.MOVE_PAGE_AND_SUB_TO_DIFFERENT_BOOK, loginId, srcId, srcBookId, targetBookId, targetParentId, targetIndex);
            deletePageNode(loginId, srcBookId, srcParentId, srcId);
        }
    }

    private void copyPageNodeAndSubFromTheOwner(Integer longRunningTaskType, long loginId, String srcId, String srcBookId, String targetBookId, String targetParentId, Double targetIndex) {
        checkBookMaxNumOfPage(loginId, targetBookId);
        longRunningTasksScheduler.runAsyncTask(loginId, () -> {
            bookStatusLocker.startCopying(loginId, targetBookId);
            try {
                /*
                 * 第一个是一切的基础
                 */
                PageNode page = getPageNode(loginId, srcId);
                PageNode clone = copyOnePageNodeAndRefreshPageNum(loginId, page, loginId, targetBookId, targetParentId, targetIndex);
                //充当visited 功能
                Map<String, String> parentIdsMapper = new HashMap<>();
                parentIdsMapper.put(srcId, clone.getId());
                Deque<String> stack = new ArrayDeque<>();
                stack.push(srcId);

                int count = 0;
                while (!stack.isEmpty()) {
                    count++;
                    if (count > MAX_SIZE_OF_ONE_BOOK * 5) {
                        //加一层保险
                        throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
                    }


                    String curSrcPageIdForSub = stack.pop();
                    String generatedSrcPageIdForSub = generatePageParentId(srcBookId, curSrcPageIdForSub);
                    String mappedPageIdForSub = parentIdsMapper.get(curSrcPageIdForSub);

                    /*
                     * 由于进入循环前处理了第一次 因此该处理处理子节点
                     */
                    List<PageNode> subNodes = operator.getPageNodesByParentIdForCopy(loginId, srcBookId, generatedSrcPageIdForSub);
                    for (PageNode copyTarget : subNodes) {
                        String copySrcId = copyTarget.getId();
                        if (parentIdsMapper.containsValue(copySrcId)) {
                            //同一本书 复制自己或自己的子节点下 会出现再取又取到新加的情况 此时跳过
                            continue;
                        }

                        //子节点的 Index属性值保留以保存顺序关系
                        Double srcIndex = findIndexByParentId(copyTarget, generatedSrcPageIdForSub);

                        if (!parentIdsMapper.containsKey(copySrcId)) {
                            //并未复制过 则进行复制
                            PageNode cloneSub = copyOnePageNodeAndRefreshPageNum(loginId, copyTarget, loginId, targetBookId, mappedPageIdForSub, srcIndex);
                            //增加映射关系
                            //复制成功了 该节点进入下一个循环
                            stack.push(copySrcId);
                            parentIdsMapper.put(copySrcId, cloneSub.getId());
                        } else {
                            String copiedPageIdInThisProcess = parentIdsMapper.get(copySrcId);
                            //已复制过 则增加parentId
                            PageNode pageNode = getPageNode(loginId, copiedPageIdInThisProcess);
                            List<Double> indexes = pageNode.getIndexes();
                            indexes.add(srcIndex);
                            List<String> parentIds = pageNode.getParentIds();
                            parentIds.add(generatePageParentId(targetBookId, mappedPageIdForSub));
                            updatePageNodeParentIdsAndIndexesInSyncThenRefreshPageChildrenNum(loginId, pageNode.getId(), parentIds, indexes, mappedPageIdForSub, targetBookId);
                        }
                    }
                }
            } finally {
                bookStatusLocker.endCopying(loginId, targetBookId);
            }
        }, longRunningTaskType, new Object[]{srcBookId, targetBookId});
    }

    @Override
    public void copyPageNodeAndSubFromTheOwner(long loginId, String srcId, String srcBookId, String targetBookId, String targetParentId, Double targetIndex) {
        copyPageNodeAndSubFromTheOwner(LongRunningTaskType.COPY_PAGE_NODE_AND_SUB, loginId, srcId, srcBookId, targetBookId, targetParentId, targetIndex);
    }

    @Override
    public long getTotalPagesOfOwn(long loginId, String id) {
        return operator.countPagesByBook(loginId, id);
    }

    @Override
    public void emptyBookPages(long loginId, String id) {
        try {
            bookStatusLocker.startDeletingPage(loginId, id);
            locker.lockByUserAndClass(loginId, () -> {
                deleteAllPagesByBookId(loginId, id);
            });
        } finally {
            bookStatusLocker.endDeletingPage(loginId, id);
        }
    }

    private void deleteAllPagesByBookId(long loginId, String id) {
        List<PageNode> nodes = operator.getPageNodesByBookIdForDelete(loginId, id);
        nodes.forEach(one -> deleteSinglePageNodeWithFileIds(loginId, one));
    }

    private static Double findIndexByParentId(PageNode node, String generatedParentId) {
        int i = node.getParentIds().indexOf(generatedParentId);
        return i == -1 ? null : node.getIndexes().get(i);
    }


    @Override
    public void addPageParentNode(long loginId, String id, String bookId, String parentId, Double index) {
        locker.lockByUserAndClass(loginId, () -> {
            PageNode page = getPageNode(loginId, id);
            List<String> parentIds = page.getParentIds();
            if (parentIds.size() > SelfX.MAX_DB_LINES_IN_ONE_SELECTS) {
                throw new LogicException(SelfXErrors.PARENT_PAGE_REACH_OUT_MAX_LIMIT
                        , SelfX.MAX_DB_LINES_IN_ONE_SELECTS);
            }
            List<Double> indexes = page.getIndexes();
            String toAddParentId = generatePageParentId(bookId, parentId);

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

            updatePageNodeParentIdsAndIndexesInSyncThenRefreshPageChildrenNum(loginId, id, parentIds, indexes, parentId, bookId);
        });
    }


    private void refreshPageChildrenNums(String pageId, String bookId, long loginId) {
        if (pageId.isEmpty()) {
            /*
             * 说明是根节点
             */
            return;
        }
        long count = operator.countPagesForSpecificParentId(generateParentIdForSubPages(pageId), bookId, loginId);
        Map<String, Object> params = new HashMap<>();
        params.put(SolrFields.CHILDREN_NUM, count);
        updatePageNodePropsInSync(loginId, pageId, params);
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

    private static String generatePageParentId(String bookId, String parentId) {
        return parentId.isEmpty() ? generateParentIdForRootPages(bookId) : generateParentIdForSubPages(parentId);
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
    public MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId) {
        return operator.getPageNodes(loginId, bookId, generatePageParentId(bookId, parentId));
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
        String pageId;

        PageDeleteTask(String parentId, String pageId) {
            this.parentId = parentId;
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
    public void deletePageNode(long loginId, String bookId, String parentId, String pageId) {

        Deque<PageDeleteTask> stack = new ArrayDeque<>();
        PageDeleteTask originalTarget = new PageDeleteTask(parentId, pageId);
        stack.push(originalTarget);
        Set<String> visited = new HashSet<>(); // 避免环导致重复处理
        try {
            bookStatusLocker.startDeletingPage(loginId, bookId);
            locker.lockByUserAndClass(loginId, () -> {
                while (!stack.isEmpty()) {
                    PageDeleteTask task = stack.pop();
                    String currentPageId = task.pageId;

                    if (!visited.add(currentPageId)) {
                        continue; // 避免环
                    }

                    PageNode node = getPageNode(loginId, currentPageId);
                    if (node == null) {
                        log.error("理论上不该出现的deletePageNode出现损坏的环 ID → " + CoreNameProducer.calculateCoreNamByUser(SelfXCores.SHARING_BOOK, loginId));
                        continue;
                    }

                    removeParentId(node, generatePageParentId(bookId, task.parentId));

                    if (!node.getParentIds().isEmpty()) {
                        updatePageNodeParentIdsAndIndexesInSyncThenRefreshPageChildrenNum(loginId, currentPageId, node.getParentIds(), node.getIndexes(), task.parentId, bookId);
                        continue;
                    }

                    deleteSinglePageNodeWithFileIds(loginId, node);

                    List<PageNode> children = operator.getPageNodesByParentIdForDelete(
                            loginId, bookId, generatePageParentId(bookId, currentPageId)
                    );
                    for (PageNode child : children) {
                        stack.push(new PageDeleteTask(currentPageId, child.getId()));
                    }
                }
                /*
                 * 更新一下父节点的childrenNums
                 */
                refreshPageChildrenNums(parentId, bookId, loginId);

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

                    if (current == null) {
                        //此时有可能为null 说明之前被删掉了 很好 感觉上只有第一次会出现
                        continue;
                    }

                    allNodes.add(current);
                    /*
                     * 找到所有子
                     */
                    List<String> children = operator
                            .getPageNodesByParentIdForDelete(loginId, bookId, generatePageParentId(bookId, currentId))
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
        while (!queue.isEmpty()) {
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
        try {
            bookStatusLocker.startDeletingBook(loginId, bookId);
            locker.lockByUserAndClass(loginId, () -> {
                deleteAllPagesByBookId(loginId, bookId);
                cache.deleteBook(loginId, bookId, () -> operator.deleteBookById(loginId, bookId));
            });
        } finally {
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
        Function<String, ParentNode<?>> mapper = pId -> getOriginalParentNodes(pId, loginId);
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


    private ParentNode<?> getOriginalParentNodes(String pId, long loginId) {
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
