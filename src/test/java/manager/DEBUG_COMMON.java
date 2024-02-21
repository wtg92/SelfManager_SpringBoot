package manager;

import com.alibaba.fastjson2.JSONObject;
import manager.util.RefiningUtil;
import manager.util.TimeUtil;
import manager.util.ZonedTimeUtils;
import org.junit.Test;

import java.time.ZoneId;
import java.util.*;

public class DEBUG_COMMON {
    @Test
    public void testTime3(){
        long m = Long.parseLong("1708441200000");
        Date date = new Date();
        date.setTime(m);
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        System.out.println(TimeUtil.parseTime(cl));
    }
    @Test
    public void testTime(){
        long t1 = System.currentTimeMillis();
        long t2 = Calendar.getInstance().getTime().getTime();
        System.out.println(t2-t1);
    }

    @Test
    public void testTime2(){
        System.out.println(ZonedTimeUtils.getCurrentDateUtc(RefiningUtil.getDefaultTimeZone()));
        System.out.println(ZonedTimeUtils.getCurrentDateUtc(RefiningUtil.getDefaultTimeZone()));
    }

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
