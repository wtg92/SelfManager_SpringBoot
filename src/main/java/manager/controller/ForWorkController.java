package manager.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.alibaba.fastjson2.JSONObject;
import manager.logic.elasticsearch.ElasticSearchInvoker;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/forWork")
public class ForWorkController {

    @Resource
    ElasticSearchInvoker invoker;

    @PostMapping("/searchUserPics")
    public Map<String,Object> getBasicInfo(@RequestBody JSONObject param) {
        Map<String,Object> rlt = new HashMap<>();
        invoker.doThings((cilent)->{
            final SearchResponse<Map> search =
                    cilent.search(s -> s
                                    .index(ElasticSearchInvoker.USER_PICS_INDEX)
                                    .size(5)
                                    .from(param.getInteger("start"))
                                    .query(q -> q
                                            .match(t -> t
                                                    .field("tags")
                                                    .query(param.getString("search"))
                                            )
                                    ),
                            Map.class
                    );
            rlt.put("totalHits",search.hits().total().value());
            rlt.put("data",search.hits().hits().stream().map(hit->{
                final Map source = hit.source();
                source.put("score",hit.score());
                return source;
            }));
        });
        return rlt;
    }

}
