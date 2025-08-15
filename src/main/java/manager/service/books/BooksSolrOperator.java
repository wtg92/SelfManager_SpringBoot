package manager.service.books;


import manager.booster.MultipleLangHelper;
import manager.data.MultipleItemsResult;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.solr.SolrOperator;
import manager.solr.SolrUtil;
import manager.solr.books.SharingLink;
import manager.solr.constants.SolrRequestParam;
import manager.solr.data.SolrSearchRequest;
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
import java.util.*;

/**
 * 连接operator
 * 确定configDir等等
 * 确定用户逻辑DB逻辑
 */
@Component
public class   BooksSolrOperator {

    @Resource
    private SolrOperator operator;

    @Value("${solr.cores.sharingBook}")
    private String SHARING_BOOK_CONFIG;

    @Value("${solr.cores.sharingLink}")
    private String SHARING_LINK_CONFIG;

    @Value("${solr.cores.pageNode}")
    private String PAGE_NODE_CONFIG;

    public String insertBook(SharingBook book, Long userId){
        return operator.insertDocInUserIsolation(book, SelfXCores.SHARING_BOOK,userId,SHARING_BOOK_CONFIG);
    }

    public String insertPage(PageNode page, Long userId){
        return operator.insertDocInUserIsolation(page, SelfXCores.PAGE_NODE,userId, PAGE_NODE_CONFIG);
    }
    public String insertLink(SharingLink link, long userId, Boolean isCommunityLink) {
        String config = SHARING_LINK_CONFIG;
        String coreName = SelfXCores.SHARING_LINK;
        return  isCommunityLink ? operator.insertDocDirectly(link, coreName,userId, config):
                operator.insertDocInUserIsolation(link, coreName,userId, config);
    }
    public SharingLink getLink(long loginId, Boolean isCommunityLink, String id) {
        String coreName = SelfXCores.SHARING_LINK;
        return  isCommunityLink ? operator.getDocByIdDirectly(coreName,id, SharingLink.class) :
                operator.getDocByIdInUserIsolation(coreName,loginId,id, SharingLink.class)
                ;
    }

    public void deleteLinkById(long loginId, Boolean isCommunityLink, String id) {
        String coreName = SelfXCores.SHARING_LINK;
        if(isCommunityLink){
            operator.deleteByIDDirectly(coreName,id);
        } else{
            operator.deleteByIdInUserIsolation(coreName,loginId,id);
        }
    }
    public void updateLink(String id, Boolean isCommunityLink, Long loginId, Long updaterId, Map<String,Object> updatingFields){
        String coreName = SelfXCores.SHARING_LINK;
        if(isCommunityLink){
            operator.updateDocPartiallyDirectly(coreName,id, updaterId, updatingFields);
        }else{
            operator.updateDocPartiallyInUserIsolation(coreName,id,loginId, updaterId, updatingFields);
        }
    }
    public MultipleItemsResult<SharingLink> getLinks(long loginId, String bookId, Boolean isCommunityLink) {
        String config = SHARING_LINK_CONFIG;
        String coreName = SelfXCores.SHARING_LINK;
        SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.addFilterQuery(SolrFields.USER_ID+":"+loginId,SolrFields.BOOK_ID+":"+bookId);
        query.setFields(
                SolrFields.CONTENT_ID,
                SolrFields.TYPE,
                SolrFields.ID, SolrFields.CREATE_UTC, SolrFields.UPDATE_UTC, SolrFields.NAME_MULTI,
                SolrFields.STATUS,
                SolrFields.DESC_MULTI, SolrFields.DEFAULT_LANG
        );
        query.set(SolrRequestParam.QUERY_LIMIT, String.valueOf(SelfX.MAX_DB_LINES_IN_ONE_SELECTS));
        return  isCommunityLink ? operator.queryDirectly(coreName,query, config, SharingLink.class) :
                operator.queryInUserIsolation(coreName,loginId,query, config, SharingLink.class)
                ;
    }

    public MultipleItemsResult<SharingBook> getBooks(long userId,List<Integer> states) {
        SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.addFilterQuery(SolrUtil.buildCollectionsQuery(SolrFields.STATUS,states));
        query.setFields(
                SolrFields.ID, SolrFields.CREATE_UTC, SolrFields.UPDATE_UTC, SolrFields.NAME_MULTI,
                SolrFields.STATUS,
                SolrFields.COMMENT_MULTI, SolrFields.DEFAULT_LANG, SolrFields.STYLE, SolrFields.UPDATER_ID
        );
        query.set(SolrRequestParam.QUERY_SORT, SolrFields.SEQ_WEIGHT + " " + SolrQuery.ORDER.desc);
        query.set(SolrRequestParam.QUERY_LIMIT, String.valueOf(SelfX.MAX_DB_LINES_IN_ONE_SELECTS));
        return operator.queryInUserIsolation(SelfXCores.SHARING_BOOK,userId,query,SHARING_BOOK_CONFIG,SharingBook.class);
    }

    public List<String> getBookIdsByState(long userId, List<Integer> states) {
        SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.addFilterQuery (SolrUtil.buildCollectionsQuery(SolrFields.STATUS,states));
        query.setFields(SolrUtil.getMultipleFieldParam(
                SolrFields.ID
        ));
        return operator.queryInUserIsolation(SelfXCores.SHARING_BOOK,userId,query,SHARING_BOOK_CONFIG,SharingBook.class)
                .items.stream().map(SharingBook::getId).toList();
    }

    public void updateBook(String id,Long creatorId,Long updaterId,Map<String,Object> updatingFields){
        operator.updateDocPartiallyInUserIsolation(SelfXCores.SHARING_BOOK,id,creatorId,updaterId,updatingFields);
    }

    public void updatePageNode(String id, Long creatorId, Long updaterId, Map<String,Object> updatingFields){
        operator.updateDocPartiallyInUserIsolation(SelfXCores.PAGE_NODE,id,creatorId,updaterId,updatingFields);
    }


    public SharingBook getBook(long loginId, String id) {
        return operator.getDocByIdInUserIsolation(SelfXCores.SHARING_BOOK,loginId,id,SharingBook.class);
    }
    public SolrSearchResult<SharingBook> searchBooks(long loginId,SolrSearchRequest searchRequest) {
        Class<SharingBook> cla = SharingBook.class;
        List<String> fieldNames = new ArrayList<>(searchRequest.searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.NAME))
                : searchRequest.searchVersions.stream().map((langVersion) -> MultipleLangHelper.getFiledName(SolrFields.NAME, langVersion)).toList())
                ;
        fieldNames.addAll(searchRequest.searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.COMMENT))
                : searchRequest.searchVersions.stream().map((langVersion)->MultipleLangHelper.getFiledName(SolrFields.COMMENT,langVersion)).toList());

        return operator.search(SelfXCores.SHARING_BOOK,loginId,fieldNames,SHARING_BOOK_CONFIG,cla,
                (query)->{
                    /*
                     * 关闭的book 不查
                     */
                    query.addFilterQuery("-"+SolrFields.STATUS+":"+ SharingBookStatus.CLOSED);
                    List<String> fields = ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.NAME));
                    fields.addAll(List.of(SolrFields.CREATE_UTC,
                            SolrFields.UPDATE_UTC,
                            SolrFields.DEFAULT_LANG,
                            SolrFields.ID,
                            SolrFields.SCORE));

                    query.setFields(fields.toArray(new String[0])
                    );
                },
                SharingBook::getId
                ,
                SharingBook::setScore
                ,searchRequest);
    }

    public SolrSearchResult<PageNode> searchPageNodes(long loginId, SolrSearchRequest searchRequest, List<String> closedBookIds) {
        Class<PageNode> cla = PageNode.class;
        List<String> fieldNames = new ArrayList<>(searchRequest.searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.NAME))
                : searchRequest.searchVersions.stream().map((langVersion) -> MultipleLangHelper.getFiledName(SolrFields.NAME, langVersion)).toList())
                ;
        fieldNames.addAll(searchRequest.searchAllVersions ? ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.CONTENT))
                : searchRequest.searchVersions.stream().map((langVersion)->MultipleLangHelper.getFiledName(SolrFields.CONTENT,langVersion)).toList());

        return operator.search(SelfXCores.PAGE_NODE,loginId,fieldNames,PAGE_NODE_CONFIG,cla,
                (query)->{
                    /*
                     * 关闭的book 不查
                     */
                    if(!closedBookIds.isEmpty()){
                        query.addFilterQuery("-"+SolrFields.BOOK_ID+":"+"("+(String.join(" OR ",closedBookIds))+")");
                    }
                    List<String> fields = ReflectUtil.getFiledNamesByPrefix(cla, MultipleLangHelper.getFiledPrefix(SolrFields.NAME));
                    fields.addAll(List.of(SolrFields.CREATE_UTC,
                            SolrFields.UPDATE_UTC,
                            SolrFields.BOOK_ID,
                            SolrFields.ID,
                            SolrFields.SCORE));
                    query.setFields(fields.toArray(new String[0]));
                },
                PageNode::getId
                ,
                PageNode::setScore
                ,searchRequest);
    }

    public PageNode getPageNode(long loginId, String id) {
        return operator.getDocByIdInUserIsolation(SelfXCores.PAGE_NODE,loginId,id,PageNode.class);
    }

    private final static String FULL_BASE_QUERY = "*:*";

    public MultipleItemsResult<PageNode> getPageNodes(long loginId, String bookId, String parentId) {
        SolrQuery query =  buildPageNodesQuery(
                List.of(SolrFields.PARENT_IDS + ":" + parentId,SolrFields.BOOK_ID + ":" + bookId),
                List.of(SolrFields.ID, SolrFields.CREATE_UTC, SolrFields.UPDATE_UTC, SolrFields.NAME_MULTI,
                        SolrFields.INDEXES, SolrFields.PARENT_IDS, SolrFields.CHILDREN_NUM,
                        SolrFields.UPDATER_ID, SolrFields.IS_HIDDEN),
                SelfX.MAX_DB_LINES_IN_ONE_SELECTS
        );
        return operator.queryInUserIsolation(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class);
    }

    public List<PageNode> getPageNodesByParentIdForCopy(long loginId, String bookId, String parentId) {
        SolrQuery query = buildPageNodesQuery(
                List.of(SolrFields.PARENT_IDS + ":" + parentId,SolrFields.BOOK_ID + ":" + bookId),
                List.of(SolrFields.ID,
                        SolrFields.TYPE,
                        SolrFields.PARENT_IDS,
                        SolrFields.INDEXES,
                        SolrFields.WITH_TODOs,
                        SolrFields.IS_HIDDEN,
                        SolrFields.VARIABLES,
                        SolrFields.FILE_IDS,
                        SolrFields.CONTENT_MULTI,
                        SolrFields.NAME_MULTI,
                        SolrFields.EDITOR_STATE_MULTI
                        ),
                /*
                 * 代表不加限制
                 */
                Integer.MAX_VALUE
        );
        return operator.queryInUserIsolation(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class).items;
    }
    
    public List<PageNode> getPageNodesByParentIdForDelete(long loginId, String bookId, String parentId) {
        SolrQuery query = buildPageNodesQuery(
                List.of(SolrFields.PARENT_IDS + ":" + parentId,SolrFields.BOOK_ID + ":" + bookId),
                List.of(SolrFields.ID, SolrFields.INDEXES, SolrFields.PARENT_IDS),
                /*
                 * 代表不加限制
                 */
                Integer.MAX_VALUE
        );
        return operator.queryInUserIsolation(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class).items;
    }

    public List<PageNode> getPageNodesByBookIdForDelete(long loginId, String bookId) {
        SolrQuery query = buildPageNodesQuery(
                List.of(SolrFields.BOOK_ID + ":" + bookId),
                List.of(SolrFields.ID, SolrFields.INDEXES, SolrFields.PARENT_IDS),
                /*
                 * 代表不加限制
                 */
                Integer.MAX_VALUE
        );
        return operator.queryInUserIsolation(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG, PageNode.class).items;
    }



    private static SolrQuery buildPageNodesQuery(List<String> filters,
                                                 List<String> fields,
                                                 int limit) {
        SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.setFields(fields.toArray(new String[0]));
        query.set(SolrRequestParam.QUERY_LIMIT, String.valueOf(limit));
        query.addFilterQuery(filters.toArray(new String[0]));
        return query;
    }



    public long countPagesForSpecificParentId(String parentId,String bookId,long loginId) {
        final SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.addFilterQuery (SolrFields.PARENT_IDS+":"+ parentId,SolrFields.BOOK_ID+":"+bookId);
        return operator.queryStatus(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG).count;
    }

    public long countPagesByBook(long loginId,String bookId){
        final SolrQuery query = new SolrQuery();
        query.setQuery(FULL_BASE_QUERY);
        query.addFilterQuery (SolrFields.BOOK_ID+":"+bookId);
        return operator.queryStatus(SelfXCores.PAGE_NODE,loginId,query, PAGE_NODE_CONFIG).count;
    }

    public void deleteBookById(long loginId, String id) {
        operator.deleteByIdInUserIsolation(SelfXCores.SHARING_BOOK,loginId,id);
    }

    public void deletePageNodeById(long loginId, String id) {
        operator.deleteByIdInUserIsolation(SelfXCores.PAGE_NODE,loginId,id);
    }





}
