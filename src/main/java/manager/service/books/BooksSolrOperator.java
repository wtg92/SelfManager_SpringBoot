package manager.service.books;


import manager.data.career.MultipleItemsResult;
import manager.entity.general.books.PageNode;
import manager.entity.general.books.SharingBook;
import manager.solr.SolrOperator;
import manager.solr.SolrUtil;
import manager.solr.constants.SolrRequestParam;
import manager.system.SelfX;
import manager.system.SelfXCores;
import manager.system.SolrFields;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY,  FULL_BASE_QUERY);
        queryParamMap.put(SolrRequestParam.FILTER_QUERY_SEPERATELY,  SolrFields.STATUS+":"+ state);
        queryParamMap.put(SolrRequestParam.QUERY_FIELDS, SolrUtil.getMultipleFieldParam(SolrFields.ID,SolrFields.CREATE_UTC
                ,SolrFields.UPDATE_UTC,SolrFields.NAME_MULTI,SolrFields.COMMENT_MULTI,SolrFields.DEFAULT_LANG,SolrFields.STYLE
                ,SolrFields.UPDATER_ID));
        queryParamMap.put(SolrRequestParam.QUERY_SORT, SolrFields.SEQ_WEIGHT+" "+SolrRequestParam.QUERY_DESC);
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, String.valueOf( SelfX.MAX_DB_LINES_IN_ONE_SELECTS));
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        return operator.query(SelfXCores.SHARING_BOOK,userId,queryParams,SHARING_BOOK_CONFIG,SharingBook.class);
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

    public PageNode getPageNode(long loginId, String id) {
        return operator.getDocById(SelfXCores.PAGE_NODE,loginId,id,PageNode.class);
    }

    private final static String FULL_BASE_QUERY = "*:*";

    public MultipleItemsResult<PageNode> getPages(long loginId, String bookId, String parentId) {
        final Map<String, String[]> queryParamMap = new HashMap<>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY, new String[]{FULL_BASE_QUERY});

        queryParamMap.put(SolrRequestParam.FILTER_QUERY_SEPERATELY,  new String[]{SolrFields.PARENT_IDS+":"+ parentId,SolrFields.BOOK_ID+":"+bookId});
        queryParamMap.put(SolrRequestParam.QUERY_FIELDS,new String[]{SolrUtil.getMultipleFieldParam(SolrFields.ID,SolrFields.CREATE_UTC
                ,SolrFields.UPDATE_UTC,SolrFields.NAME_MULTI,SolrFields.INDEXES,SolrFields.PARENT_IDS
                ,SolrFields.CHILDREN_NUM
                ,SolrFields.UPDATER_ID
                ,SolrFields.IS_HIDDEN)});
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, new String[]{String.valueOf(SelfX.MAX_DB_LINES_IN_ONE_SELECTS)});
        MultiMapSolrParams queryParams = new MultiMapSolrParams(queryParamMap);
        return operator.query(SelfXCores.PAGE_NODE,loginId,queryParams, PAGE_NODE_CONFIG, PageNode.class);
    }

    public List<PageNode> getPageNodesByParentIdForDelete(long loginId, String bookId, String parentId) {
        final Map<String, String[]> queryParamMap = new HashMap<>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY, new String[]{FULL_BASE_QUERY});
        queryParamMap.put(SolrRequestParam.FILTER_QUERY_SEPERATELY,  new String[]{SolrFields.PARENT_IDS+":"+ parentId,SolrFields.BOOK_ID+":"+bookId});
        queryParamMap.put(SolrRequestParam.QUERY_FIELDS,new String[]{SolrUtil.getMultipleFieldParam(SolrFields.ID,SolrFields.INDEXES,SolrFields.PARENT_IDS)});
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, new String[]{String.valueOf(Integer.MAX_VALUE)});
        MultiMapSolrParams queryParams = new MultiMapSolrParams(queryParamMap);
        return operator.query(SelfXCores.PAGE_NODE,loginId,queryParams, PAGE_NODE_CONFIG, PageNode.class).items;
    }

    public long countPagesForSpecificParentId(String parentId,String bookId,long loginId) {
        final Map<String, String[]> queryParamMap = new HashMap<>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY, new String[]{FULL_BASE_QUERY});
        queryParamMap.put(SolrRequestParam.FILTER_QUERY_SEPERATELY,  new String[]{SolrFields.PARENT_IDS+":"+ parentId,SolrFields.BOOK_ID+":"+bookId});
        return operator.queryStatus(SelfXCores.PAGE_NODE,loginId,queryParamMap, PAGE_NODE_CONFIG).count;
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
