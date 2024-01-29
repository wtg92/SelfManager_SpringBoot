package manager.logic.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson2.JSON;
import manager.util.ThrowableConsumer;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class ElasticSearchInvoker {

    public static String USER_PICS_INDEX = "user-pics";

    private final String USERNAME = "elastic";
    private final String PWD = "IKNdUS=QBocqIGED*kn=";
    String serverUrl = "https://localhost:9200";
    SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint("c67252fbd1b27ab6f994e4a9a8d4f6f96be93357535b36de9e3609cb001cf6bf");




    public void doThings(ThrowableConsumer<ElasticsearchClient,Exception> consumer){
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(USERNAME, PWD));

        // Create the low-level client
        RestClientBuilder builder = RestClient
                .builder(HttpHost.create(serverUrl))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                                .setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        try(RestClient restClient = builder.build();
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());) {
            // And create the API client
            ElasticsearchClient esClient = new ElasticsearchClient(transport);
            consumer.accept(esClient);
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }


    public void createIndex(String name) throws  Exception{
        doThings((esClient)->{
            esClient.indices().create(c->c.index(name));
        });
    }

    public void createDocumentByMap(String index,String id,Map<String, Object> obj) {
        doThings((esClient)->{
            IndexResponse response = esClient.index(i -> i
                    .index(index)
                    .id(id)
                    .document(obj)
            );
        });
    }
}
