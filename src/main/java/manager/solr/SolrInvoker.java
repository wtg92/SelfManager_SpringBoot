package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.entity.general.career.NoteBook;
import manager.system.SMDB;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.CoreStatus;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
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

    public void addAutoIncrementalID(String core){
        String solrUrl = baseURL+core;

        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(solrUrl).build()) {


            Map<String,Object> params = new HashMap<>();
            params.put("name","id");
            params.put("type","string");
            params.put("indexed","true");
            params.put("stored","true");
            params.put("required","true");
            params.put("multiValued","false");
            SchemaRequest.AddField addField = new SchemaRequest.AddField(params);
            // 通过 Schema API 添加字段
            NamedList<Object> response = solrClient.request(addField);

            // 提交更改
            solrClient.commit();

            System.out.println("Schema updated successfully!");
//            System.out.println("Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public boolean coreExists(String coreName){
        return getCoreStatus(coreName) != null;
    }


    public NamedList<Object> getCoreStatus(String coreName){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CoreAdminRequest req = new CoreAdminRequest();
            req.setAction(CoreAdminParams.CoreAdminAction.STATUS);
            req.setIndexInfoNeeded(false);
            return ((CoreAdminResponse) req.process(solrClient)).getCoreStatus(coreName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createCore(String name,String configSet){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CoreAdminRequest.Create req = new CoreAdminRequest.Create();
            req.setCoreName(name);
            req.setDataDir("data/");
//            req.setInstanceDir(instanceDir);
//            req.setDataDir(dataDir);
//                req.setUlogDir(ulogDir);

//            req.setConfigName(configFile);

//                req.setSchemaName(schemaFile);
            req.setConfigSet(configSet);
            CoreAdminResponse res =  (CoreAdminResponse)req.process(solrClient);
            System.out.println(res.getCoreStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public boolean createCollection(String name){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CollectionAdminRequest.Create createRequest = CollectionAdminRequest.createCollection(
                    name,
                    "_default",
                    numShards,
                    numReplicas
            );
            CollectionAdminResponse process = createRequest.process(solrClient);
            return process.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String indexEntity(String coreOrCollectionName){
        String solrUrl = baseURL+coreOrCollectionName; // 替换为你的 collection URL

        // 创建 Http2SolrClient 实例
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(solrUrl).build()) {

            // 添加文档到 Solr collection
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "test001");
            doc.addField("name", "Sample Name");
            doc.addField("category", "example");

            UpdateResponse addResponse = solrClient.add(doc);

            solrClient.commit();  // 提交更改
            System.out.println("Document added with status: " + addResponse.getStatus());

            // 查询 Solr collection
            SolrParams queryParams = new ModifiableSolrParams()
                    .set("q", "name:Sample Name")
                    .set("rows", 10);

            solrClient.query(queryParams).getResults()
                    .forEach(result -> System.out.println("Found document: " + result));
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void invoke(String coreOrCollectionName){
        String solrUrl = baseURL+coreOrCollectionName; // 替换为你的 collection URL

        // 创建 Http2SolrClient 实例
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(solrUrl).build()) {

            // 添加文档到 Solr collection
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "test001");
            doc.addField("name", "Sample Name");
            doc.addField("category", "example");

            UpdateResponse addResponse = solrClient.add(doc);

            solrClient.commit();  // 提交更改
            System.out.println("Document added with status: " + addResponse.getStatus());

            // 查询 Solr collection
            SolrParams queryParams = new ModifiableSolrParams()
                    .set("q", "name:Sample Name")
                    .set("rows", 10);

            solrClient.query(queryParams).getResults()
                    .forEach(result -> System.out.println("Found document: " + result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String coreName, NoteBook book) {


    }
}
