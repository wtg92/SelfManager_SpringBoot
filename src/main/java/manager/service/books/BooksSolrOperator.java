package manager.service.books;


import manager.booster.MultipleLangHelper;
import manager.data.MultipleItemsResult;
import manager.entity.general.books.PageNode;
import manager.entity.general.books.SharingBook;
import manager.solr.SolrOperator;
import manager.solr.SolrUtil;
import manager.solr.constants.SolrRequestParam;
import manager.solr.data.SolrSearchResult;
import manager.system.SelfX;
import manager.solr.SelfXCores;
import manager.solr.SolrFields;
import manager.system.books.SharingBookStatus;
import manager.util.ReflectUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接operator
 * 确定configDir等等
 * 确定用户逻辑DB逻辑
 */
@Component
public class BooksSolrOperator {

    @Resource
    private SolrOperator operator;

    @Value("${solr.cores.sharingBook}")
    private String SHARING_BOOK_CONFIG;

    @Value("${solr.cores.pageNode}")
    private String PAGE_NODE_CONFIG;

    public void insertBook(SharingBook book, Long userId){
        operator.insertDoc(book, SelfXCores.SHARING_BOOK,userId,SHARING_BOOK_CONFIG);
    }

    public void insertPage(PageNode page, Long userId){
        operator.insertDoc(page, SelfXCores.PAGE_NODE,userId, PAGE_NODE_CONFIG);
    }

    public MultipleItemsResult<SharingBook> getBooks(long userId, Integer state) {
        SolrQuery query = new SolrQuery();
        query.set(SolrRequestParam.RELEVANT_QUERY, FULL_BASE_QUERY);
        query.set(SolrRequestParam.FILTER_QUERY_SEPERATELY, SolrFields.STATUS + ":" + state);
        query.set(SolrRequestParam.QUERY_FIELDS, SolrUtil.getMultipleFieldParam(
                SolrFields.ID, SolrFields.CREATE_UTC, SolrFields.UPDATE_UTC, SolrFields.NAME_MULTI,
                SolrFields.COMMENT_MULTI, SolrFields.DEFAULT_LANG, SolrFields.STYLE, SolrFields.UPDATER_ID
        ));
        query.set(SolrRequestParam.QUERY_SORT, SolrFields.SEQ_WEIGHT + " " + SolrQuery.ORDER.desc);
        query.set(SolrRequestParam.QUERY_LIMIT, String.valueOf(SelfX.MAX_DB_LINES_IN_ONE_SELECTS));
        return operator.query(SelfXCores.SHARING_BOOK,userId,query,SHARING_BOOK_CONFIG,SharingBook.class);
    }

    public void updateBook(String id,Long creatorId,Long updaterId,Map<String,Object> updatingFields){
        operator.updateDocPartially(SelfXCores.SHARING_BOOK,id,creatorId,updaterId,updatingFields);
    }

    public void updatePageNode(String id, Long creatorId, Long updaterId, Map<String,Object> updatingFields){
        operator.updateDocPartially(SelfXCores.PAGE_NODE,id,creatorId,updaterId,updatingFields);
    }


    public SharingBook getBook(long loginId, String id) {
        return operator.getDocById(SelfXCores.SHARING_BOOK,loginId,id,SharingBook.class);
    }
    public SolrSearchResult<SharingBook> searchBooks(long loginId, String searchInfo, Integer pageNum, Boolean searchAllVersions,
                                                     List<String> searchVersions) {
        Class<SharingBook> cla = SharingBook.class;
        List<String> fieldNames = new ArrayList<>(searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.NAME))
                : searchVersions.stream().map((langVersion) -> MultipleLangHelper.getFiledName(SolrFields.NAME, langVersion)).toList())
                ;
        fieldNames.addAll(searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.COMMENT))
                : searchVersions.stream().map((langVersion)->MultipleLangHelper.getFiledName(SolrFields.COMMENT,langVersion)).toList());
        return operator.search(SelfXCores.SHARING_BOOK,loginId,fieldNames,searchInfo,pageNum,SHARING_BOOK_CONFIG,cla,
                (query)->{
                    /*
                     * 关闭的book 不查
                     */
                    query.addFilterQuery("-"+SolrFields.STATUS+":"+ SharingBookStatus.CLOSED);
                });
    }
    public PageNode getPageNode(long loginId, String id) {
        return operator.getDocById(SelfXCores.PAGE_NODE,loginId,id,PageNode.class);
    }

    private final static String FULL_BASE_QUERY = "*:*";

    public MultipleItemsResult<PageNode> getPageNodes(long loginId, String bookId, String parentId) {
        SolrQuery query =  buildPageNodesQuery(
                bookId, parentId,
                List.of(SolrFields.ID, SolrFields.CREATE_UTC, SolrFields.UPDATE_UTC, SolrFields.NAME_MULTI,
                        SolrFields.INDEXES, SolrFields.PARENT_IDS, SolrFields.CHILDREN_NUM,
                        SolrFields.UPDATER_ID, SolrFields.IS_HIDDEN),
                SelfX.MAX_DB_LINES_IN_ONE_SELECTS
        );
        return operator.query(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class);
    }

    public List<PageNode> getPageNodesByParentIdForDelete(long loginId, String bookId, String parentId) {
        SolrQuery query = buildPageNodesQuery(
                bookId, parentId,
                List.of(SolrFields.ID, SolrFields.INDEXES, SolrFields.PARENT_IDS),
                Integer.MAX_VALUE
        );
        return operator.query(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class).items;
    }

    private static SolrQuery buildPageNodesQuery(String bookId, String parentId, List<String> fields, int limit) {
        SolrQuery query = new SolrQuery();
        query.set(SolrRequestParam.RELEVANT_QUERY, FULL_BASE_QUERY);
        query.set(SolrRequestParam.QUERY_FIELDS, SolrUtil.getMultipleFieldParam(fields));
        query.set(SolrRequestParam.QUERY_LIMIT, String.valueOf(limit));

        query.add(SolrRequestParam.FILTER_QUERY_SEPERATELY, SolrFields.PARENT_IDS + ":" + parentId);
        query.add(SolrRequestParam.FILTER_QUERY_SEPERATELY, SolrFields.BOOK_ID + ":" + bookId);

        return query;
    }



    public long countPagesForSpecificParentId(String parentId,String bookId,long loginId) {
        final SolrQuery query = new SolrQuery();
        query.add(SolrRequestParam.RELEVANT_QUERY,FULL_BASE_QUERY);
        query.add(SolrRequestParam.FILTER_QUERY_SEPERATELY,SolrFields.PARENT_IDS+":"+ parentId);
        query.add(SolrRequestParam.FILTER_QUERY_SEPERATELY,SolrFields.BOOK_ID+":"+bookId);
        return operator.queryStatus(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG).count;
    }

    public void deleteBookById(long loginId, String id) {
        operator.deleteById(SelfXCores.SHARING_BOOK,loginId,id);
    }

    public void deletePageNodeById(long loginId, String id) {
        operator.deleteById(SelfXCores.PAGE_NODE,loginId,id);
    }

    public void deletePageNodesByBookId(long loginId, String bookId) {
        Map<String,Object> params = new HashMap<>();
        params.put(SolrFields.BOOK_ID,bookId);
        operator.deleteByFields(SelfXCores.PAGE_NODE,loginId,params);
    }



}
