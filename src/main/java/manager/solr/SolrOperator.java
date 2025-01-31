package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.booster.UserIsolator;
import manager.data.career.MultipleItemsResult;
import manager.entity.SMSolrDoc;
import manager.solr.constants.SolrRequestParam;
import manager.system.DBConstants;
import manager.system.SolrFields;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
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

    public void deleteById(String id) {
        invoker.deleteById(DBConstants.E_SHARING_BOOK,id);
    }

    public <T> MultipleItemsResult<T> query(String core,Long userId,MapSolrParams queryParamMap, String configDir,Class<T> cls) {
        String coreName = UserIsolator.calculateCoreNamByUser(core,userId) ;
        initCoreIfNotExist(coreName,configDir);
        QueryResponse queryResponse = invoker.query(coreName, queryParamMap);
        MultipleItemsResult<T> rlt = new MultipleItemsResult<>();
        rlt.count=queryResponse.getResults().getNumFound();
        rlt.items=queryResponse.getBeans(cls);
        return rlt;
    }



}
