package manager.solr;

import manager.booster.UserIsolator;
import manager.entity.SMSolrDoc;
import manager.solr.constants.SolrRequestParam;
import manager.system.SelfXCores;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类保持单纯 负责原子化操作
 * Solr的API有些问题 ：选择某一个core有两种办法：URL 或 api指定
 * 但是如commit没有api指定（API是collectionName）
 * 因此在选择core时 尽量使用URL方式
 */
@Component
public class SolrInvoker{

    @Value("${solr.url}")
    String baseURL;

    @Value("${solr.instanceDir}")
    String instanceDir;

    @Value("${solr.createCollections.numShards}")
    Integer numShards;

    @Value("${solr.createCollections.numReplicas}")
    Integer numReplicas;

    @Value("${solr.config.default}")
    String defaultConfig;


    public boolean coreExists(String coreName){
        return getCoreStatus(coreName).size() > 0;
    }


    public NamedList<Object> getCoreStatus(String coreName){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CoreAdminRequest req = new CoreAdminRequest();
            req.setAction(CoreAdminParams.CoreAdminAction.STATUS);
            req.setIndexInfoNeeded(false);
            return CoreAdminRequest.getStatus(coreName, solrClient).getCoreStatus(coreName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public CoreAdminResponse createCore(String name,String configSet){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CoreAdminRequest.Create req = new CoreAdminRequest.Create();
            req.setCoreName(name);
            req.setDataDir("data/");
            req.setConfigSet(configSet);
            return (CoreAdminResponse)req.process(solrClient);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public UpdateResponse insertDoc(String coreName,SMSolrDoc solrDoc) {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.setParam(SolrRequestParam.PROCESSOR, SolrUtil.getAutoGenerateIdConfig());
            SolrInputDocument doc = SolrUtil.binder.toSolrInputDocument(solrDoc);
            updateRequest.add(doc);
            return updateRequest.commit(solrClient,coreName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public SolrDocument getDocument(String coreName, String id) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            return solrClient.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String coreName, String id) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            solrClient.deleteById(id);
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteByFields(String coreName, Map<String,Object> fieldsFilter ) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            solrClient.deleteByQuery(SolrUtil.buildQueryString(fieldsFilter));
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private SolrClient generateSpecificClient(String coreName){
        return new HttpJdkSolrClient.Builder(baseURL+coreName).build();
    }
    public void updatePartialFields(String coreName, List<SolrInputDocument> docs){
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            for (SolrInputDocument doc : docs) {
                solrClient.add(doc);
            }
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void testSolrClient() {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL+UserIsolator.calculateCoreNamByUser(SelfXCores.SHARING_BOOK,(long)1)).build()) {
            final Map<String, String> queryParamMap = new HashMap<String, String>();

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "b4c85078-0d89-41d1-8fae-7f5d1178ec07"); // Mandatory: Unique identifier
            // Add the fields to update
            doc.addField("comment_ja",Map.of("set", "new value for field1")); // Atomic update
            doc.addField("status",Map.of("set", 2)); // Atomic update
            solrClient.add(doc);
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public QueryResponse query(String coreName, SolrParams queryParamMap) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            return solrClient.query(queryParamMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



}
