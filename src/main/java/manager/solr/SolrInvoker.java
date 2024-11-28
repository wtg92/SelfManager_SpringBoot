package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.entity.SMSolrDoc;
import manager.entity.general.books.SharingBook;
import manager.entity.general.career.NoteBook;
import manager.solr.constants.SolrRequestParam;
import manager.system.SMDB;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.CoreStatus;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public UpdateResponse insertDoc(String coreName,SMSolrDoc book) {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.setParam(SolrRequestParam.PROCESSOR, SolrUtil.getAutoGenerateIdConfig());
            SolrInputDocument doc = SolrUtil.binder.toSolrInputDocument(book);
            updateRequest.add(doc);
            return updateRequest.commit(solrClient,coreName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public SolrDocument getDocument(String coreName, String id) {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            return solrClient.getById(coreName, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String coreName, String id) {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            solrClient.deleteById(coreName, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
