package manager.solr;

import com.alibaba.fastjson2.JSON;
import manager.booster.CoreNameProducer;
import manager.entity.SMSolrDoc;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            throw SolrUtil.processSolrException(e);
        }
    }

    public CoreAdminResponse createCore(String name,String configSet){
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
            CoreAdminRequest.Create req = new CoreAdminRequest.Create();
            req.setCoreName(name);
            req.setDataDir("data/");
            req.setConfigSet(configSet);
            return (CoreAdminResponse)req.process(solrClient);
        }
        catch (Exception e) {
            throw SolrUtil.processSolrException(e);
        }
    }

    /*
     * 由于我要Java自动生成ID
     * 因此 万分之一的几率 生成的UUID会重复
     * 那么由主键的ID校验 来进行重设
     * 因此 需要在该函数中 设置ID
     * @param coreName
     * @param solrDoc
     * @return
     */
    public UpdateResponse insertDoc(String coreName,SMSolrDoc solrDoc) {
        int recursiveTime = 0;
        while(true){
            recursiveTime ++ ;
            solrDoc.setId(UUID.randomUUID().toString());
            try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL).build()) {
                UpdateRequest updateRequest = new UpdateRequest();
                SolrInputDocument doc = SolrUtil.binder.toSolrInputDocument(solrDoc);
                updateRequest.add(doc);
                return updateRequest.commit(solrClient,coreName);
            } catch (Exception e) {
                if(e.getMessage().contains("version conflict for") && recursiveTime < 3){
                    continue;
                }
                throw SolrUtil.processSolrException(e);
            }
        }

    }

    public SolrDocument getDocument(String coreName, String id) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            return solrClient.getById(id);
        } catch (Exception e) {
            throw SolrUtil.processSolrException(e);
        }
    }

    public void deleteById(String coreName, String id) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            solrClient.deleteById(id);
            solrClient.commit();
        } catch (Exception e) {
            throw SolrUtil.processSolrException(e);
        }
    }

    public void deleteByFields(String coreName, Map<String,Object> fieldsFilter ) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            solrClient.deleteByQuery(SolrUtil.buildQueryString(fieldsFilter));
            solrClient.commit();
        } catch (Exception e) {
            throw SolrUtil.processSolrException(e);
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
            throw SolrUtil.processSolrException(e);
        }
    }

    public void testSolrClient() {
        try (SolrClient solrClient = new HttpJdkSolrClient.Builder(baseURL+ CoreNameProducer.calculateCoreNameByUser(SelfXCores.SHARING_BOOK,(long)1)).build()) {
            SolrQuery query = new SolrQuery();
            query.setQuery("(name_arabic:哈~ OR comment_arabic:Java~)");  // 对 name_* 和 comment_* 进行模糊匹配
            query.setStart(0);
            query.setRows(10);  // 分页：获取前10条数据
            query.setSort("score", SolrQuery.ORDER.desc); // 按得分排序


            query.setHighlight(true);  // 启用高亮
            query.addHighlightField("name_arabic");  // 指定高亮字段
            query.setHighlightSimplePre("<mark>");  // 高亮前缀
            query.setHighlightSimplePost("</mark>");  // 高亮后缀
            query.setHighlightFragsize(100);  // 片段大小

            QueryResponse response = solrClient.query(query);
            SolrDocumentList docs = response.getResults();

            // 输出结果
            System.out.println(JSON.toJSONString(docs));
            System.out.println(JSON.toJSONString(response.getHighlighting()));
        } catch (Exception e) {
            throw SolrUtil.processSolrException(e);
        }
    }

    public QueryResponse query(String coreName, SolrQuery q) {
        try (SolrClient solrClient = generateSpecificClient(coreName)) {
            return solrClient.query(q);
        } catch (Exception e) {
            throw SolrUtil.processSolrException(e);
        }
    }


}
