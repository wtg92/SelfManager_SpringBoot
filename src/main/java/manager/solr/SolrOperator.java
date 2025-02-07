package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.booster.UserIsolator;
import manager.data.career.MultipleItemsResult;
import manager.entity.SMSolrDoc;
import manager.solr.constants.SolrRequestParam;
import manager.solr.data.StatsResult;
import manager.system.SolrFields;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
     * @param doc
     * @param core
     * @param userId
     * @param configDir
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


    public <T> MultipleItemsResult<T> query(String core, Long userId, SolrParams queryParamMap, String configDir, Class<T> cls) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        QueryResponse queryResponse = invoker.query(coreName, queryParamMap);
        MultipleItemsResult<T> rlt = new MultipleItemsResult<>();
        rlt.count=queryResponse.getResults().getNumFound();
        rlt.items=queryResponse.getBeans(cls);
        return rlt;
    }

    /**
     * TODO 将来扩展 还有一个stats.field 可以设置
     * 假设数值 还可以求出最大值 最小值
     * @param core
     * @param userId
     * @param queryParamMap
     * @param configDir
     * @return
     */
    public StatsResult queryStatus(String core, Long userId, Map<String, String[]> queryParamMap, String configDir) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        queryParamMap.put(SolrRequestParam.STATS, new String[]{SolrRequestParam.TRUE});;
        queryParamMap.put(SolrRequestParam.QUERY_LIMIT, new String[]{String.valueOf(0)});
        MultiMapSolrParams queryParams = new MultiMapSolrParams(queryParamMap);
        QueryResponse queryResponse = invoker.query(coreName, queryParams);
        return transfer(queryResponse);
    }

    private StatsResult transfer(QueryResponse queryResponse) {
        StatsResult rlt = new StatsResult();
        rlt.count = queryResponse.getResults().getNumFound();
        return rlt;
    }


}
