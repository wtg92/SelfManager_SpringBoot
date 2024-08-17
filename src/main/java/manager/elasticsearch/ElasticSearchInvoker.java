package manager.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
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
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

//@Component
public class ElasticSearchInvoker {

    public static String USER_PICS_INDEX = "user-pics";

    @Value("${elasticsearch.username}")
    private String USERNAME;
    @Value("${elasticsearch.password}")
    private String PWD;
    @Value("${elasticsearch.url}")
    private String serverUrl;
    @Value("${elasticsearch.p12path}")
    private String p12Path;

    SSLContext sslContext;

    @PostConstruct
    void init(){
        SSLContextBuilder sslBuilder = SSLContextBuilder.create();
        try {
            sslBuilder.loadTrustMaterial(new File(p12Path));
            sslContext = sslBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


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


    public void createIndex(String name){
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
