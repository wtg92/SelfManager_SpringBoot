package manager;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DEBUG_COMMON {

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

}
