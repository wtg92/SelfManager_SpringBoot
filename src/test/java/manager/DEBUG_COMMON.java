package manager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import manager.solr.books.SharingBook;
import manager.entity.general.career.WorkSheet;
import manager.util.*;
import org.junit.Test;

import java.time.Duration;
import java.time.ZoneId;
import java.util.*;

public class DEBUG_COMMON {
    @Test
    public void testSecurity() throws Exception{
        String rlt =  SecurityBasis.AES.generateKeyAsBase64();
        System.out.println(rlt);
    }



    @Test
    public void testReflect(){
        SharingBook book = new SharingBook();
        book = ReflectUtil.setFiledValue(book,"comment_ch","Unbeliable");
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

    @Test
    public void testHex(){
        String s = "1A";
        String s2 = "FF";
        System.out.println(Integer.parseInt(s,16));
        System.out.println(Integer.parseInt(s2,16));
        System.out.println((byte)Integer.parseInt(s2,16));
        byte s3 = (byte)Integer.parseInt(s2,16);

    }

    @Test
    public void debugCache(){
        String str = "{\"content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<ws><items p_key=\\\"12\\\"><item id=\\\"1\\\" pl_item_id=\\\"36\\\" mode=\\\"2\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 09:47:00\\\" e_time=\\\"2021-05-24 10:39:00\\\" type=\\\"1\\\" val=\\\"52.0\\\" s_time_utc=\\\"1621817220000\\\" e_time_utc=\\\"1621820340000\\\">目标 Kafka\\n开始 \\n结束 想到了ngnix</item><item id=\\\"2\\\" pl_item_id=\\\"1\\\" mode=\\\"1\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 10:49:00\\\" e_time=\\\"2021-05-24 11:04:00\\\" type=\\\"1\\\" val=\\\"15.0\\\" s_time_utc=\\\"1621820940000\\\" e_time_utc=\\\"1621821840000\\\">目标 Ngnix  研究一会儿\\n开始 \\n结束</item><item id=\\\"3\\\" pl_item_id=\\\"41\\\" mode=\\\"4\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 11:05:00\\\" e_time=\\\"2021-05-24 11:31:00\\\" type=\\\"1\\\" val=\\\"26.0\\\" s_time_utc=\\\"1621821900000\\\" e_time_utc=\\\"1621823460000\\\">目标 Look The last 25minutes.\\n开始 \\n结束</item><item id=\\\"4\\\" pl_item_id=\\\"41\\\" mode=\\\"3\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 13:12:00\\\" e_time=\\\"2021-05-24 13:41:00\\\" type=\\\"1\\\" val=\\\"29.0\\\" s_time_utc=\\\"1621829520000\\\" e_time_utc=\\\"1621831260000\\\">目标 \\n开始 \\n结束</item><item id=\\\"5\\\" pl_item_id=\\\"36\\\" mode=\\\"3\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 13:45:00\\\" e_time=\\\"2021-05-24 13:54:00\\\" type=\\\"1\\\" val=\\\"9.0\\\" s_time_utc=\\\"1621831500000\\\" e_time_utc=\\\"1621832040000\\\">目标 \\n开始 \\n结束</item><item id=\\\"6\\\" pl_item_id=\\\"41\\\" mode=\\\"3\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 13:59:00\\\" e_time=\\\"2021-05-24 14:42:00\\\" type=\\\"1\\\" val=\\\"43.0\\\" s_time_utc=\\\"1621832340000\\\" e_time_utc=\\\"1621834920000\\\">目标 \\n开始 \\n结束</item><item id=\\\"7\\\" pl_item_id=\\\"41\\\" mode=\\\"1\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 14:51:00\\\" e_time=\\\"2021-05-24 14:58:00\\\" type=\\\"1\\\" val=\\\"7.0\\\" s_time_utc=\\\"1621835460000\\\" e_time_utc=\\\"1621835880000\\\">目标 \\n开始 \\n结束</item><item id=\\\"8\\\" pl_item_id=\\\"41\\\" mode=\\\"3\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 15:01:00\\\" e_time=\\\"2021-05-24 16:04:00\\\" type=\\\"1\\\" val=\\\"63.0\\\" s_time_utc=\\\"1621836060000\\\" e_time_utc=\\\"1621839840000\\\">目标 \\n开始 \\n结束</item><item id=\\\"9\\\" pl_item_id=\\\"36\\\" mode=\\\"1\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 16:35:00\\\" e_time=\\\"2021-05-24 17:01:00\\\" type=\\\"1\\\" val=\\\"26.0\\\" s_time_utc=\\\"1621841700000\\\" e_time_utc=\\\"1621843260000\\\">目标 继续看一会儿 Kafka\\n开始 \\n结束</item><item id=\\\"10\\\" pl_item_id=\\\"36\\\" mode=\\\"1\\\" f_add=\\\"false\\\" s_time=\\\"2021-05-24 17:14:00\\\" e_time=\\\"2021-05-24 17:32:00\\\" type=\\\"1\\\" val=\\\"18.0\\\" s_time_utc=\\\"1621844040000\\\" e_time_utc=\\\"1621845120000\\\">目标 \\n开始 \\n结束</item><item id=\\\"11\\\" pl_item_id=\\\"41\\\" mode=\\\"0\\\" f_add=\\\"false\\\" s_time=\\\"2024-08-01 11:00:33\\\" s_time_utc=\\\"1621821600000\\\" e_time=\\\"2024-08-01 11:00:33\\\" e_time_utc=\\\"0\\\" type=\\\"1\\\" val=\\\"0.0\\\">目標 \\n開始 \\n終了 </item></items><logs p_key=\\\"4\\\"><log id=\\\"1\\\" c_time=\\\"2021-05-24 09:41:01\\\" ac=\\\"14\\\" c_id=\\\"1\\\" params=\\\"工作日_1\\\"/><log id=\\\"2\\\" c_time=\\\"2021-05-24 13:41:47\\\" ac=\\\"15\\\" c_id=\\\"0\\\" params=\\\"1_2\\\"/><log id=\\\"3\\\" c_time_utc=\\\"1722477633432\\\" ac=\\\"15\\\" c_id=\\\"0\\\" params=\\\"2_5\\\"/></logs></ws>\",\"createTime\":\"2021-05-24 09:41:01\",\"createUtc\":1621849261000,\"dataVersion\":\"1\",\"date\":\"2021-05-24 08:00:00\",\"dateUtc\":1621810800000,\"id\":206,\"note\":\"想一下 想一下 想一下\\n工作是首要的...这样的安排是有隐患的...\\nSo 这样办 先把Kafka 的向导弄完\\n这个缓一下吧。\\n回归工作 回归工作 ！\",\"ownerId\":1,\"plan\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<pl><items p_key=\\\"44\\\"><item id=\\\"1\\\" val=\\\"0\\\" name=\\\"计算机\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"><item id=\\\"2\\\" val=\\\"0\\\" name=\\\"服务器相关\\\" mapping_val=\\\"0.8\\\" type=\\\"1\\\"/><item id=\\\"34\\\" val=\\\"0\\\" name=\\\"SpringBoot\\\" mapping_val=\\\"1.0\\\" type=\\\"1\\\"/><item id=\\\"35\\\" val=\\\"0\\\" name=\\\"SpringCloud\\\" mapping_val=\\\"1.0\\\" type=\\\"1\\\"/></item><item id=\\\"4\\\" val=\\\"0\\\" name=\\\"运动\\\" mapping_val=\\\"0.0\\\" type=\\\"2\\\"><item id=\\\"5\\\" val=\\\"0\\\" name=\\\"游泳\\\" mapping_val=\\\"2.0\\\" type=\\\"2\\\"/><item id=\\\"7\\\" val=\\\"0\\\" name=\\\"跳绳\\\" mapping_val=\\\"35.0\\\" type=\\\"1\\\"/><item id=\\\"8\\\" val=\\\"0\\\" name=\\\"骑车\\\" mapping_val=\\\"60.0\\\" type=\\\"1\\\"/><item id=\\\"10\\\" val=\\\"0\\\" name=\\\"走路\\\" mapping_val=\\\"120.0\\\" type=\\\"1\\\"/><item id=\\\"25\\\" val=\\\"0\\\" name=\\\"跑步\\\" mapping_val=\\\"40.0\\\" type=\\\"1\\\"/></item><item id=\\\"31\\\" val=\\\"0\\\" name=\\\"《换天记》\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"/><item id=\\\"36\\\" val=\\\"0\\\" name=\\\"工作Step\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"/><item id=\\\"39\\\" val=\\\"30\\\" name=\\\"写作\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"><item id=\\\"40\\\" val=\\\"0\\\" name=\\\"《低俗家庭》\\\" mapping_val=\\\"1.0\\\" type=\\\"1\\\"/><item id=\\\"41\\\" val=\\\"0\\\" name=\\\"《如何成为一流程序员？Java，JavaScript》\\\" mapping_val=\\\"1.0\\\" type=\\\"1\\\"/></item><item id=\\\"42\\\" val=\\\"0\\\" name=\\\"午睡\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"/><item id=\\\"43\\\" val=\\\"0\\\" name=\\\"午饭\\\" mapping_val=\\\"0.0\\\" type=\\\"1\\\"/></items></pl>\",\"planId\":18,\"state\":\"OVER_FINISHED\",\"tags\":[{\"createdBySystem\":true,\"name\":\"工作日\"},{\"createdBySystem\":true,\"name\":\"如何成为一流程序员\"},{\"createdBySystem\":true,\"name\":\"青岛贝塔\"}],\"timezone\":\"Asia/Shanghai\",\"updateTime\":\"2022-03-07 09:10:13\",\"updateUtc\":1722495257386,\"version\":91}\n";
        WorkSheet ws = JSON.parseObject(str,WorkSheet.class);
        System.out.println(ws.getPlan());

        Cache<Long, WorkSheet> Worksheet_Cache= generateSpecificEntityCache(111);
        Worksheet_Cache.get(ws.getId(),(e)->ws);
        WorkSheet ifPresent = Worksheet_Cache.getIfPresent(ws.getId());
        System.out.println(String.valueOf(ifPresent == null));
        System.out.println(ifPresent.getPlan());
    }

    private <T,V> Cache<V, T> generateSpecificEntityCache(int maxSize) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(Duration.ofMinutes(1))
                .expireAfterAccess(Duration.ofMinutes(1))
                .build();
    }

}
