package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.booster.CoreNameProducer;
import manager.data.MultipleItemsResult;
import manager.entity.SMSolrDoc;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.solr.constants.SolrConfig;
import manager.solr.constants.SolrRequestParam;
import manager.solr.data.SolrSearchRequest;
import manager.solr.data.SolrSearchResult;
import manager.solr.data.StatsResult;
import manager.system.SelfXErrors;
import manager.util.CommonUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 该类处理的问题：
 * a.Solr使用有一个特殊性：单独的数据库有的需要初始化
 */
@Component
public class SolrOperator {

    @Resource
    private SolrInvoker invoker;

    public boolean initCoreIfNotExist(String coreName,String configDir){
        if(!invoker.coreExists(coreName)){
            try{
                invoker.createCore(coreName,configDir);
            }catch(DBException e){
                if(e.type == SelfXErrors.CREATE_CORE_SYNC){
                    /*  前台异步调用接口导致 是一个正常的情况
                     *  给每一个用户单独的数据库 所以对于一个实体 第一次增删改查的时候 会先检验该数据库存在不存在 不存在的话 就创建 ----  然后当时的页面上有不管联的两个接口 一个是查询页总数 一个是加载根目录下的所有页面  由于异步导致两个接口 一开始都发现没有对应数据库 然后都创建 一个创建成功了 一个由于已经创建了 不能再创建了 因此报错
                     *  sleep的原因似乎是solr的原因 异步创建时 无法保证同步 因此会出现 a 创建->db操作 b 创建->db操作 a创建成功 b没有创建成功 直接db操作 aDB操作 此时会导致b操作找不到对应的core而报错
                     * */
                    CommonUtil.sleep(500);
                    return  false;
                }
                throw e;
            }
            return true;
        }
        return false;
    }

    public String insertDocInUserIsolation(SMSolrDoc doc, String core, long userId, String configDir) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        return insertDocDirectly(doc,coreName,userId,configDir);
    }

    public String insertDocDirectly(SMSolrDoc doc,String coreName,long userId,String configDir) {
        initCoreIfNotExist(coreName,configDir);
        doc.setCreateUtc(System.currentTimeMillis());
        doc.setUpdateUtc(System.currentTimeMillis());
        doc.setUpdaterId(userId);
        /*Solr setting means not existed in db*/
        doc.set_version_((long)-1);
        invoker.insertDoc(coreName, doc);
        return doc.getId();
    }

    public void updateDocPartiallyInUserIsolation(String core, String id, long userId, long updaterId, Map<String,Object> updatingFields){
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        updateDocPartiallyDirectly(coreName,id,updaterId,updatingFields);
    }

    public void updateDocPartiallyDirectly(String coreName,String id,long updaterId,Map<String,Object> updatingFields){
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(SolrFields.ID, id);

        doc.addField(SolrFields.UPDATER_ID,Map.of(SolrRequestParam.SET,updaterId));
        doc.addField(SolrFields.UPDATE_UTC,Map.of(SolrRequestParam.SET,System.currentTimeMillis()));

        updatingFields.forEach((key,val)->{
            doc.addField(key,Map.of(SolrRequestParam.SET, val));
        });

        invoker.updatePartialFields(coreName,List.of(doc));
    }

    public <T> T getDocByIdInUserIsolation(String core, long userId, String id, Class<T> cls) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        return getDocByIdDirectly(coreName,id,cls);
    }

    public <T> T getDocByIdDirectly(String coreName, String id, Class<T> cls) {
        SolrDocument document = invoker.getDocument(coreName, id);
        if(document == null){
            return null;
        }
        return JSON.parseObject(document.jsonStr(),cls);
    }

    public void deleteByIdInUserIsolation(String core, long userId, String id) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        deleteByIDDirectly(coreName,id);
    }

    public void deleteByIDDirectly(String coreName, String id) {
        invoker.deleteById(coreName,id);
    }

    public void deleteByFieldsInUserIsolation(String core, long userId, Map<String, Object> params) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        deleteByFieldsDirectly(coreName,params);
    }
    public void deleteByFieldsDirectly(String coreName, Map<String, Object> params) {
        invoker.deleteByFields(coreName,params);
    }

    public <T> MultipleItemsResult<T> queryInUserIsolation(String core, Long userId, SolrQuery query, String configDir, Class<T> cls) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        return queryDirectly(coreName,query,configDir,cls);
    }

    public <T> MultipleItemsResult<T> queryDirectly(String coreName, SolrQuery query, String configDir, Class<T> cls) {
        initCoreIfNotExist(coreName,configDir);
        QueryResponse queryResponse = invoker.query(coreName, query);
        MultipleItemsResult<T> rlt = new MultipleItemsResult<>();
        rlt.count=queryResponse.getResults().getNumFound();
        rlt.items=queryResponse.getBeans(cls);
        return rlt;
    }


    public <T> SolrSearchResult<T> search(String core, Long userId, List<String> fieldNames
            , String configDir
            , Class<T> cls
            , Consumer<SolrQuery> queryAdditionalFilter
            , Function<T,String> idGetter
            , BiConsumer<T,Float> scoreSetter
            , SolrSearchRequest searchRequest
    ) {
        String coreName = CoreNameProducer.calculateCoreNameByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);

        SolrQuery query = new SolrQuery();
        /*
         * 我自己管的
         */
        query.setStart((searchRequest.pageNum - 1) * SolrConfig.SEARCH_PAGE_SIZE);
        query.setRows(SolrConfig.SEARCH_PAGE_SIZE);
        query.setSort(SolrFields.SCORE, SolrQuery.ORDER.desc);
        query.addSort(SolrFields.UPDATE_UTC, SolrQuery.ORDER.desc);
        query.setIncludeScore(true);
        if(queryAdditionalFilter != null){
            queryAdditionalFilter.accept(query);
        }
        query.setTimeAllowed(SolrConfig.SEARCH_TIME_ALLOWED_OF_SECONDS * 1000);
        query.set("minExactCount", SolrConfig.SEARCH_MIN_EXACT_COUNT);

        /*
         * 用户填的
         */
        /* Search Parameters */
        query.setQuery(searchRequest.isLucene() ? SolrUtil.buildLuceneSearchQuery(fieldNames,searchRequest)
                 : searchRequest.searchInfo);
        if(!searchRequest.isLucene()){
            query.set("qf",String.join(" ",fieldNames));
        }

        query.set("defType", searchRequest.defType);
        query.set("q.op", searchRequest.separatorMode);
        query.set("sow",searchRequest.sow);
        if(!searchRequest.isLucene() && searchRequest.mm != null){
            query.set("mm",searchRequest.mm);
        }
        if(searchRequest.isEdismax() && searchRequest.mmAutoRelax != null){
            query.set("mm.autoRelax",searchRequest.mmAutoRelax);
        }

        query.setHighlight(searchRequest.applyHighlighting);

        if(searchRequest.applyHighlighting){
            fieldNames.forEach(query::addHighlightField);
            query.set("hl.requireFieldMatch", searchRequest.requireFieldMatch);
            query.set("hl.usePhraseHighlighter", searchRequest.usePhraseHighlighter);
            query.set("hl.highlightMultiTerm", searchRequest.highlightMultiTerm);
            if(searchRequest.snippets > 5) {
                throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
            }
            query.set("hl.snippets",searchRequest.snippets);
            query.setHighlightFragsize(searchRequest.fragSize == null ? SolrConfig.HIGHLIGHT_FRAGMENT_SIZE : searchRequest.fragSize);
            query.setHighlightSimplePre("<"+ SolrConfig.HIGHLIGHT_TAG+">");
            query.setHighlightSimplePost("</"+SolrConfig.HIGHLIGHT_TAG+">");

            /* Highlighting Parameters */
            query.set("hl.method", searchRequest.hlMethod);

            if(searchRequest.isUnifiedHL()){
                query.set("hl.fragsizeIsMinimum",searchRequest.hlFragsizeIsMinimum);
                if(!searchRequest.hlTagEllipsis.isEmpty()){
                    query.set("hl.tag.ellipsis",searchRequest.hlFragsizeIsMinimum);
                }
                if(!searchRequest.hlScoreK1.isEmpty()){
                    query.set("hl.score.k1",searchRequest.hlScoreK1);
                }
                if(!searchRequest.hlScoreB.isEmpty()){
                    query.set("hl.score.b",searchRequest.hlScoreB);
                }
                if(searchRequest.hlScorePivot != null){
                    query.set("hl.score.pivot",searchRequest.hlScorePivot);
                }
                query.set("hl.weightMatches",searchRequest.hlWeightMatches);
            }

            if(searchRequest.isOriginalHL()){
                query.set("hl.mergeContiguous", searchRequest.mergeContiguous);
            }
        }

        QueryResponse queryResponse = invoker.query(coreName, query);
        SolrSearchResult<T> rlt = new SolrSearchResult<>();
        rlt.partialResults = queryResponse.getResponseHeader().getBooleanArg("partialResults") != null && queryResponse.getResponseHeader().getBooleanArg("partialResults");
        rlt.numFoundExact = queryResponse.getResults().getNumFoundExact();
        rlt.count=queryResponse.getResults().getNumFound();
        rlt.items=queryResponse.getBeans(cls);
        SolrDocumentList list = queryResponse.getResults();
        rlt.items.forEach(item->{
            SolrDocument target = list.stream().filter(one -> one.get(SolrFields.ID).equals(idGetter.apply(item))).findAny().get();
            scoreSetter.accept(item,(Float)target.get(SolrFields.SCORE));
        });
        rlt.highlighting = queryResponse.getHighlighting();
        return rlt;
    }

    /**
     * TODO 将来扩展 还有一个stats.field 可以设置
     * 假设数值 还可以求出最大值 最小值
     */
    public StatsResult queryStatus(String core, Long userId, SolrQuery query, String configDir) {
        String coreName = CoreNameProducer.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        query.set(SolrRequestParam.STATS, SolrRequestParam.TRUE);
        query.set(SolrRequestParam.QUERY_LIMIT, "0");
        QueryResponse queryResponse = invoker.query(coreName, query);
        return transfer(queryResponse);
    }

    private static StatsResult transfer(QueryResponse queryResponse) {
        StatsResult rlt = new StatsResult();
        rlt.count = queryResponse.getResults().getNumFound();
        return rlt;
    }


}
