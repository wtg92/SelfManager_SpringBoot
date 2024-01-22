package work;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson.JSON;
import manager.logic.elasticsearch.ElasticSearchInvoker;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.Test;

import javax.naming.directory.SearchResult;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.time.Duration;
import java.util.*;

public class DEBUG_ElasticSearch {

    private final static String USERNAME = "elastic";
    private final static String PWD = "IKNdUS=QBocqIGED*kn=";

    @Test
    public void test() throws  Exception{
        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint("c67252fbd1b27ab6f994e4a9a8d4f6f96be93357535b36de9e3609cb001cf6bf");

        String serverUrl = "https://localhost:9200";
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(USERNAME, PWD));

    // Create the low-level client
        RestClientBuilder builder =RestClient
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
                    restClient, new JacksonJsonpMapper());){


            // And create the API client
            ElasticsearchClient esClient = new ElasticsearchClient(transport);


            esClient.indices().create(c->c.index("products"));
            Map<String,Object> ob = new HashMap<>();
            ob.put("url","ssss");
            ob.put("name","nickname");

            IndexResponse response = esClient.index(i -> i
                    .index("products")
                    .id("bk-1")
                    .document(ob)
            );

            GetResponse<Map> r = esClient.get(g -> g
                            .index("products")
                            .id("bk-1"),
                    Map.class
            );
            System.out.println("Lovely"+JSON.toJSONString(r.source()));


            SearchResponse<Map> resp = esClient.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .match(t -> t
                                            .field("name")
                                            .query("我想吃飯")
                                    )
                            ),
                    Map.class
            );
            final List<Hit<Map>> hits = resp.hits().hits();
            hits.forEach(hit->{
                hit.source();
            });
        }



        while(true){

        }
    }
    ElasticSearchInvoker invoker = new ElasticSearchInvoker();

    @Test
    public void flow1() throws  Exception{

        invoker.createIndex(ElasticSearchInvoker.USER_PICS_INDEX);
    }



    @Test
    public void flow2() throws  Exception{

        File dir = new File("D:\\Project\\SelfManager_React\\selfm\\public\\userPics");
        final File[] files =dir.listFiles();

        List<String> tags = Arrays.asList("大海","海鸥","丑","好看","漂亮","人","男人","女人","冒险","海贼王");
        Random random = new Random();

        for(int i=0;i< files.length;i++){
            File one = files[i];
            Map<String,Object> obj = new HashMap<>();

            Set<String> tagsForUnit = new HashSet<>();
            tagsForUnit.add(tags.get(random.nextInt(tags.size())));
            tagsForUnit.add(tags.get(random.nextInt(tags.size())));

            obj.put("tags",tagsForUnit);
            obj.put("file_name",one.getName());
            String id = "user_pic"+i;
            invoker.createDocumentByMap(ElasticSearchInvoker.USER_PICS_INDEX,id,obj);
        }

    }

    @Test
    public void flow3(){
        invoker.doThings((cilent)->{
            final SearchResponse<Map> search =
                    cilent.search(s -> s
                            .index(ElasticSearchInvoker.USER_PICS_INDEX)
                            .size(5)
                            .query(q -> q
                                    .match(t -> t
                                            .field("tags")
                                            .query("我想看漂亮的海")
                                    )
                            ),
                    Map.class
            );
            System.out.println("总共命中"+search.hits().total().value());
            System.out.println("实际取出"+search.hits().hits().size());
        });
    }

}
