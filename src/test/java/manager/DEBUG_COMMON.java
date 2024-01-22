package manager;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DEBUG_COMMON {

    @Test
    public void test15() {
        JSONObject param = new JSONObject();
        param.put("a", "");
        Integer i = param.getInteger("a");
        System.out.println(i);
    }

    @Test
    public void test1(){
        Map<String,Integer> cache = new HashMap<>();

        cache.put("1",1);
        cache.put("2",1);

        System.out.println(cache.size());

        final Set<Map.Entry<String, Integer>> entries = cache.entrySet();
        System.out.println(entries.size());

        cache.entrySet().removeIf(one->one.getKey().equals("1"));
        System.out.println(cache.size());

    }


    @Test
    public void testTimeZone(){
        String str = "Asia/Tokyo";
        final ZoneId of = ZoneId.of(str);
        System.out.println(of.getId());
    }


    @Test
    public void testDefaultTimeZone(){
        System.out.println(ZoneId.systemDefault());
    }

}
