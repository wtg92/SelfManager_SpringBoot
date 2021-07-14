package manager.util;

import static manager.util.CacheUtil.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.exception.NoSuchElement;
import redis.clients.jedis.Jedis;

public class CacheUtilTest {
	
	@Before
	public void setUp() {
		TestUtil.initEnvironment();
	}
	
	@Test
	public void testInit() {
		System.out.println(POOL.isClosed());
	}
	
	
	
	@Test
	public void testConnection() {
		final String REDIS_IP = CommonUtil.getValFromPropertiesFileInResource("redis_ip");
		final int REDIS_PORT = CommonUtil.getIntValFromPropertiesFileInResource("redis_port");
		final String REDIS_PWD = CommonUtil.getValFromPropertiesFileInResource("redis_pwd");
		Jedis j = new Jedis(REDIS_IP, REDIS_PORT);
		j.auth(REDIS_PWD);
		System.out.println(j.ping());
		j.close();
	}
	
	@Test
	public void testBasic()throws Exception {
		assert keys("*").size() == 0;
		
		String val = "val";
		set("key1", val);
		set("key2", val);
		set("key3", val);
		set("key4", val);
		set("key5", val);
		set("key6", val);
		set("key7", val);
		
		String pattern = "key*";
		List<String> rlt = findKeys(pattern);
		assert rlt.stream().distinct().count() == rlt.size();
		
		
//		String s = get("null");
//		System.out.println(s);
//		set("a", "bb");
//		assertEquals("bb", get("a"));
	}
	
	
	@Test
	public void testMap() throws Exception {
		
		setMap("a","b","c");
		assertEquals("c", getMapVal("a", "b"));
		
		System.out.println(getMapVal("b", "c"));
		System.out.println(getMapVal("a", "c"));
	}
	
}
