package manager.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSONObject;
import manager.data.AjaxResult;
import manager.logic.elasticsearch.ElasticSearchInvoker;
import manager.system.Gender;
import manager.system.SM;
import manager.system.VerifyUserMethod;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/forWork")
public class ForWortController {

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
