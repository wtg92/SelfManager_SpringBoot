package manager.service.books;


import manager.data.career.MultipleItemsResult;
import manager.entity.general.books.BookPage;
import manager.entity.general.books.SharingBook;
import manager.solr.SolrOperator;
import manager.solr.SolrUtil;
import manager.solr.constants.SolrRequestParam;
import manager.system.SM;
import manager.system.SMCores;
import manager.system.SolrFields;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
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

    @Value("${solr.cores.bookPage}")
    private String BOOK_PAGE_CONFIG;

    public void insertBook(SharingBook book, Long userId){
        operator.insertDoc(book, SMCores.SHARING_BOOK,userId,SHARING_BOOK_CONFIG);
    }

    public void insertPage(BookPage page, Long userId){
        operator.insertDoc(page, SMCores.BOOK_PAGE,userId,BOOK_PAGE_CONFIG);
    }

    public MultipleItemsResult<SharingBook> getBooks(long userId, Integer state) {
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY,  FULL_BASE_QUERY);
        queryParamMap.put(SolrRequestParam.PURE_QUERY,  SolrFields.STATUS+":"+ state);
        queryParamMap.put(SolrRequestParam.QUERY_FIELDS, SolrUtil.getMultipleFieldParam(SolrFields.ID,SolrFields.CREATE_UTC
                ,SolrFields.UPDATE_UTC,SolrFields.NAME_MULTI,SolrFields.COMMENT_MULTI,SolrFields.DEFAULT_LANG,SolrFields.STYLE
                ,SolrFields.UPDATER_ID));
        queryParamMap.put(SolrRequestParam.QUERY_SORT, SolrFields.SEQ_WEIGHT+" "+SolrRequestParam.QUERY_DESC);
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, String.valueOf( SM.MAX_DB_LINES_IN_ONE_SELECTS));
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        return operator.query(SMCores.SHARING_BOOK,userId,queryParams,SHARING_BOOK_CONFIG,SharingBook.class);
    }

    public void updateBook(String id,Long creatorId,Long updaterId,Map<String,Object> updatingFields){
        operator.updateDocPartially(SMCores.SHARING_BOOK,id,creatorId,updaterId,updatingFields);
    }


    public SharingBook getBook(long loginId, String id) {
        return operator.getDocById(SMCores.SHARING_BOOK,loginId,id,SharingBook.class);
    }

    private final static String FULL_BASE_QUERY = "*:*";

    public MultipleItemsResult<BookPage> getPages(long loginId, String parentId) {
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put(SolrRequestParam.RELEVANT_QUERY,  FULL_BASE_QUERY);
        queryParamMap.put(SolrRequestParam.PURE_QUERY,  SolrFields.PARENT_IDS+":"+ parentId);
        queryParamMap.put(SolrRequestParam.QUERY_FIELDS, SolrUtil.getMultipleFieldParam(SolrFields.ID,SolrFields.CREATE_UTC
                ,SolrFields.UPDATE_UTC,SolrFields.NAME_MULTI,SolrFields.INDEXES
                ,SolrFields.UPDATER_ID));
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, String.valueOf(SM.MAX_DB_LINES_IN_ONE_SELECTS));
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        return operator.query(SMCores.BOOK_PAGE,loginId,queryParams,SHARING_BOOK_CONFIG,BookPage.class);

    }
}
