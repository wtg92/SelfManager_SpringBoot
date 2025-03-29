package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.booster.UserIsolator;
import manager.data.MultipleItemsResult;
import manager.entity.SMSolrDoc;
import manager.solr.constants.SolrConfig;
import manager.solr.constants.SolrRequestParam;
import manager.solr.data.SolrSearchResult;
import manager.solr.data.StatsResult;
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
            invoker.createCore(coreName,configDir);
            return true;
        }
        return false;
    }

    /**
     * UserIsolator.calculateCoreNamByUser放在这个方法里的原因是
     * 当传入coreName时 说明是分用户的 因此这段逻辑 放在这里是okay的
     */
    public void insertDoc(SMSolrDoc doc,String core,long userId,String configDir) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        doc.setCreateUtc(System.currentTimeMillis());
        doc.setUpdateUtc(System.currentTimeMillis());
        doc.setUpdaterId(userId);
        /*Solr setting means not existed in db*/
        doc.set_version_((long)-1);
        invoker.insertDoc(coreName, doc);
    }

    public void updateDocPartially(String core,String id,long userId,long updaterId,Map<String,Object> updatingFields){
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(SolrFields.ID, id);

        doc.addField(SolrFields.UPDATER_ID,Map.of(SolrRequestParam.SET,updaterId));
        doc.addField(SolrFields.UPDATE_UTC,Map.of(SolrRequestParam.SET,System.currentTimeMillis()));

        updatingFields.forEach((key,val)->{
            doc.addField(key,Map.of(SolrRequestParam.SET, val));
        });

        invoker.updatePartialFields(coreName,List.of(doc));
    }



    public <T> T getDocById(String core,long userId, String id, Class<T> cls) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        return JSON.parseObject(invoker.getDocument(coreName,id).jsonStr(),cls);
    }

    public void deleteById(String core,long userId, String id) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        invoker.deleteById(coreName,id);
    }

    public void deleteByFields(String core, long userId, Map<String, Object> params) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        invoker.deleteByFields(coreName,params);
    }


    public <T> MultipleItemsResult<T> query(String core, Long userId, SolrQuery query, String configDir, Class<T> cls) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        QueryResponse queryResponse = invoker.query(coreName, query);
        MultipleItemsResult<T> rlt = new MultipleItemsResult<>();
        rlt.count=queryResponse.getResults().getNumFound();
        rlt.items=queryResponse.getBeans(cls);
        return rlt;
    }


    public <T> SolrSearchResult<T> search(String core, Long userId, List<String> fieldNames
            , String searchInfo
            , int pageNum
            , String configDir
            , Class<T> cls
            , Integer fragSize
            , Consumer<SolrQuery> queryAdditionalFilter
            , Function<T,String> idGetter
            , BiConsumer<T,Float> scoreSetter
    ) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);

        SolrQuery query = new SolrQuery();
        query.setQuery(SolrUtil.buildSearchQuery(fieldNames,searchInfo));
        query.setStart((pageNum - 1) * SolrConfig.SEARCH_PAGE_SIZE);
        query.setRows(SolrConfig.SEARCH_PAGE_SIZE);
        query.setSort(SolrFields.SCORE, SolrQuery.ORDER.desc);

        query.setHighlight(true);
        fieldNames.forEach(query::addHighlightField);
        query.setIncludeScore(true);
        query.setHighlightSimplePre("<"+ SolrConfig.HIGHLIGHT_TAG+">");
        query.setHighlightSimplePost("</"+SolrConfig.HIGHLIGHT_TAG+">");
        query.setHighlightFragsize(fragSize == null ? SolrConfig.HIGHLIGHT_FRAGMENT_SIZE : fragSize);

        query.set("hl.requireFieldMatch", true);
        query.set("hl.mergeContiguous", true);
        query.set("hl.usePhraseHighlighter", true);
        query.set("hl.fragListBuilder", "weighted");
        query.set("hl.boundaryScanner", "sentence");
        /**
         * 最大
         */
        query.set("hl.snippets", 3);


        if(queryAdditionalFilter != null){
            queryAdditionalFilter.accept(query);
        }

        QueryResponse queryResponse = invoker.query(coreName, query);
        SolrSearchResult<T> rlt = new SolrSearchResult<>();
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
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
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
