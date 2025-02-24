package manager.service.books;


import com.alibaba.fastjson2.JSON;
import manager.SelfXManagerSpringbootApplication;
import manager.booster.MultipleLangHelper;
import manager.cache.CacheOperator;
import manager.data.career.MultipleItemsResult;
import manager.entity.general.books.PageNode;
import manager.entity.general.books.SharingBook;
import manager.exception.LogicException;
import manager.system.Language;
import manager.system.SMError;
import manager.system.SolrFields;
import manager.system.books.*;
import manager.system.career.BookStyle;
import manager.util.SecurityUtil;
import manager.util.locks.UserLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class BooksServiceImpl implements BooksService{

    @Resource
    private UserLockManager locker;

    @Resource
    private BooksSolrOperator operator;

    @Resource
    CacheOperator cache;

    private static final Logger log = LoggerFactory.getLogger(SelfXManagerSpringbootApplication.class);


    private static final Integer BOOK_DEFAULT_STATUS = SharingBookStatus.OPENED;

    private static final Integer BOOK_DEFAULT_DISPLAY_PATTERN = SharingBookDisplayPatterns.LIST;

    private static final Integer BOOK_DEFAULT_STYLE = BookStyle.GREEN.getDbCode();
    @Override
    public void createBook(long loginId,String name,String defaultLanguage,String comment) {
        /**
         * unnecessary but do not harm
         */
        if(Language.get(defaultLanguage)==Language.UNKNOWN){
            throw new LogicException(SMError.UNEXPECTED_ERROR,"IMPOSSIBLE lang " + defaultLanguage);
        }

        locker.lockByUserAndClass(loginId,()->{
            SharingBook book = new SharingBook();
            book.setDefaultLang(defaultLanguage);

            /**
             * 生成对应值
             */
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.NAME,defaultLanguage,name);
            book = MultipleLangHelper.setFiledValue(book, BooksMultipleFields.COMMENT,defaultLanguage,comment);

            /**
             * 确定一系列初始值
             */
            book.setStatus(BOOK_DEFAULT_STATUS);
            book.setDisplayPattern(BOOK_DEFAULT_DISPLAY_PATTERN);
            book.setSeqWeight(0);
            book.setStyle(BOOK_DEFAULT_STYLE);

            operator.insertBook(book,loginId);
        });
    }

    @Override
    public MultipleItemsResult<SharingBook> getBooks(long loginId,Integer state) {
        MultipleItemsResult<SharingBook> books = operator.getBooks(loginId, state);
        fill(books);
        return books;
    }

    private static void fill(MultipleItemsResult<SharingBook> books) {
        books.items.forEach(item->{
            item.setUpdaterEncodedId(SecurityUtil.encodeInfo(item.getUpdaterId()));
            item.setUpdaterId(null);
        });
    }

    @Override
    public void updateBookPropsSyncly(long loginId, String bookId, Map<String, Object> updatingAttrs) {
        locker.lockByUserAndClass(loginId,()->{
            cache.saveBook(loginId,bookId,()->operator.updateBook(bookId,loginId,loginId,updatingAttrs));
        });
    }

    @Override
    public void updatePageNodePropsSyncly(long loginId, String pageNodeId, Map<String, Object> updatingAttrs) {
        if(updatingAttrs.keySet().stream()
                .anyMatch(one->one.equals(SolrFields.PARENT_IDS)
                || one.equals(SolrFields.INDEXES)
                )){
            throw new LogicException(SMError.UNEXPECTED_ERROR);
        }
        locker.lockByUserAndClass(loginId,()->{
            cache.savePageNode(loginId,pageNodeId,()->operator.updatePageNode(pageNodeId,loginId,loginId,updatingAttrs));
        });
    }

    @Override
    public void changeNode(long loginId, String nodeId, List<String> parentIds, List<Double> indexes) {
        /**
         * 必须只管当下的
         */


        updatePageNodeParentIdsAndIndexesSyncly(loginId,nodeId,parentIds,indexes);
    }
    public void updatePageNodeParentIdsAndIndexesSyncly(long loginId, String nodeId, List<String> parentIds, List<Double> indexes) {
        checkPageNodeLegal(parentIds,indexes);
        locker.lockByUserAndClass(loginId,()->{
            Map<String,Object> param = new HashMap<>();
            param.put(SolrFields.PARENT_IDS,parentIds);
            param.put(SolrFields.INDEXES,indexes);
            cache.savePageNode(loginId,nodeId,()->operator.updatePageNode(nodeId,loginId,loginId,param));
        });
    }


    @Override
    public void closeBook(long loginId, String id) {
        //确定封存吗？（如果该笔记本不包含笔记页且不含备注，会被<em>直接删除</em>
        Map<String,Object> param = new HashMap<>();
        if(operator.countPagesForSpecificParentId
                (generateParentIdForRootPages(id),id,loginId) == 0){
            deleteBook(loginId,id);
        }else{
            param.put(SolrFields.STATUS,SharingBookStatus.CLOSED);
            updateBookPropsSyncly(loginId,id,param);
        }
    }

    @Override
    public SharingBook getBook(long loginId, String id) {
        return cache.getBook(loginId,id,()->operator.getBook(loginId,id));
    }

    @Override
    public PageNode getPageNode(long loginId, String id) {
        return cache.getPageNode(loginId,id,()->operator.getPageNode(loginId,id));
    }

    @Override
    public void createPage(long loginId,String bookId, String name, String lang, String parentId,Boolean isRoot,Double index) {
        locker.lockByUserAndClass(loginId,()->{
            PageNode page = new PageNode();
            page.setBookId(bookId);
            page.setParentIds(Collections.singletonList(generatePageParentId(bookId,parentId,isRoot)));
            page.setIndexes(Collections.singletonList(index));
            page = MultipleLangHelper.setFiledValue(page, BooksMultipleFields.NAME,lang,name);
            page.setWithTODOs(false);
            page.setIsHidden(false);
            page.setChildrenNum(0);
            page.setType(PageNodeType.PAGE);
            operator.insertPage(page,loginId);

            refreshPageChildrenNums(isRoot,parentId,bookId,loginId);
        });
    }

    private void refreshPageChildrenNums(Boolean isRoot, String id,String bookId,long loginId) {
        if(isRoot){
            return;
        }
        long count = operator.countPagesForSpecificParentId(generateParentIdForSubPages(id),bookId,loginId);
        Map<String,Object> params = new HashMap<>();
        params.put(SolrFields.CHILDREN_NUM,count);
        updatePageNodePropsSyncly(loginId,id,params);
    }

    private static String generatePageParentId(String bookId, String parentId, boolean isRoot){
        return isRoot ? generateParentIdForRootPages(bookId) : generateParentIdForSubPages(parentId);
    }

    private static String generateParentIdForRootPages(String bookId){
        return BooksConstants.SHARING_BOOK_PURE+ "_" + bookId;
    }
    private static String generateParentIdForSubPages(String parentId){
        return BooksConstants.PAGE_NODE_PURE + "_" + parentId;
    }

    @Override
    public MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId, Boolean isRoot) {
        return operator.getPages(loginId,bookId, generatePageParentId(bookId,parentId,isRoot));
    }

    /**
     * 曾经由于性能考虑而思考在一个会话内 进行递归操作
     * 但实际上 Solr没有会话机制
     * 所谓的Client只不过是一个制作HTTP的东西罢了
     * 没有所谓的状态概念
     * 因此这样就是最快的了
     */
    @Override
    public void deletePageNode(long loginId,String bookId,String parentId,Boolean isRoot,String pageId) {
        PageNode node = getPageNode(loginId,pageId);
        if(node == null){
            /**
             * 出现环了而已 我只要确保删掉所有的数据即可
             */
            return;
        }
        removeParentId(node,generatePageParentId(bookId,parentId,isRoot));
        locker.lockByUserAndClass(loginId,()->{
            if(node.getParentIds().isEmpty()){
                /**
                 * 对于树的操作 应该先删除子 再删除该节点 it's right
                 * 这里相反就是为了防止出现由于环导致的无限递归（尽管想定没有）
                 * 如果出现圆了 之后会由于空指针结束这个循环
                 */
                operator.deletePageNodeById(loginId,pageId);
                operator.getPageNodesByParentIdForDelete(loginId,bookId, generatePageParentId(bookId,pageId,false))
                        .forEach(one->{
                            deletePageNode(loginId,bookId,pageId,false,one.getId());
                        });
            }else{
                updatePageNodeParentIdsAndIndexesSyncly(loginId,pageId,node.getParentIds(),node.getIndexes());
            }
        });
    }



    @Override
    public void deleteBook(long loginId, String bookId) {
        locker.lockByUserAndClass(loginId,()->{
            operator.deleteBookById(loginId,bookId);
            operator.deletePageNodesByBookId(loginId,bookId);
        });
    }

    private static void checkPageNodeLegal(List<String> parentIds, List<Double> indexes){
        if(parentIds.stream().distinct().count() != parentIds.size()){
            throw new LogicException(SMError.INCONSISTENT_DB_ERROR," parentIds msg:"+parentIds.stream().distinct().count() +" vs "+parentIds.size());
        }
        if(parentIds.size() != indexes.size()){
            throw new LogicException(SMError.INCONSISTENT_DB_ERROR," indexes msg:"+indexes.size() +" vs "+parentIds.size());
        }
    }

    private static void removeParentId(PageNode node, String parentId) {

        int index = -1;
        List<String> parentIds = node.getParentIds();
        for(int i=0;i<parentIds.size();i++){
            String existedParentId = parentIds.get(i);
            if(existedParentId.equals(parentId)){
                index = i;
                break;
            }
        }

        if(index == -1){
            System.err.println(JSON.toJSONString(node));
            System.err.println(parentId);
            throw new LogicException(SMError.INCONSISTENT_DB_ERROR);
        }

        node.getParentIds().remove(index);
        node.getIndexes().remove(index);
    }
}
