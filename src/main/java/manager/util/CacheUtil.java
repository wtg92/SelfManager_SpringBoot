package manager.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.exception.NoSuchElement;
import manager.system.NoSuchElementType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 *  该Util管理与redis服务器的通信，调用。与CacheConverter互不知晓
  *   基于redis的特性， 对于关联表的缓存:通常做一个方向的缓存   假如两个方向的使用率大体相当，那么就维护两套缓存吧，维护一致性即可。
  *   对于再复杂的关系表，此时将其视作实体，同样是一个方向，但存储的不是id串而是实体JSON串。
 *   
  *   理论上所有缓存数据都应设置最大存活时间，在每次读取时会刷新存活时间。
 * 
  *  经研究发现scan命令 会有并发问题（find方法） 因此对find方法 以及一切涉及到key值变化的操作 都应加sync修饰
 * 
 * 
 * @author 王天戈
 *
 */
public abstract class CacheUtil {

	public static JedisPool POOL = initPool();
	
	private static Logger logger = Logger.getLogger(CacheUtil.class.getName());
	
	public final static long ALIVE_SECONDS = CommonUtil.getIntValFromPropertiesFileInResource("redis_cache_alive_seconds");
	public final static long TEMP_ALIVE_SECONDS = CommonUtil.getIntValFromPropertiesFileInResource("redis_temp_cache_alive_seconds");

	private final static int SCANNER_COUNT = CommonUtil.getIntValFromPropertiesFileInResource("redis_scanner_count");
	
	
	private static JedisPool initPool() {
		final String REDIS_IP = CommonUtil.getValFromPropertiesFileInResource("redis_ip");
		final int REDIS_PORT = CommonUtil.getIntValFromPropertiesFileInResource("redis_port");
		final String REDIS_PWD = CommonUtil.getValFromPropertiesFileInResource("redis_pwd");
		final int REDIS_MAX_TOTAL = CommonUtil.getIntValFromPropertiesFileInResource("redis_pool_max_total");
		final int REDIS_MAX_IDLE = CommonUtil.getIntValFromPropertiesFileInResource("redis_pool_max_idle");
		final int REDIS_MAX_WAIT = CommonUtil.getIntValFromPropertiesFileInResource("redis_pool_max_wait");
		final boolean REDIS_TEST_ON_BORROW = CommonUtil.getBoolValFromPropertiesFileInResource("redis_test_on_borrow");
		final boolean REDIS_TEST_ON_RETURN  = CommonUtil.getBoolValFromPropertiesFileInResource("redis_test_on_return");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(REDIS_MAX_TOTAL);
		jedisPoolConfig.setMaxIdle(REDIS_MAX_IDLE);
		jedisPoolConfig.setMaxWaitMillis(REDIS_MAX_WAIT);
		jedisPoolConfig.setTestOnBorrow(REDIS_TEST_ON_BORROW);
		jedisPoolConfig.setTestOnReturn(REDIS_TEST_ON_RETURN);
		return new JedisPool(jedisPoolConfig, REDIS_IP, REDIS_PORT, 10000, REDIS_PWD);
	}
	
	private static ScanParams getScannerParam(String pattern) {
		ScanParams param = new ScanParams();
		param.count(SCANNER_COUNT);
		param.match(pattern);
		return param;
	}

	/*return null if key not exists*/
	public static String getOne(String key) throws NoSuchElement {
		try(Jedis jedis = getJedis()){
			String value = jedis.get(key);
			if(value == null) {
				throw new NoSuchElement();
			}
			resetTimeout(jedis,key, ALIVE_SECONDS);
			return value;
		}
	}
	
	public static List<String> getOnes(List<String> keys) {
		try(Jedis jedis = getJedis()){
			/*keys里有可能有失效的，但没关系，resetTimeout不会抛异常*/
			resetTimeoutGenerally(jedis, keys);
			return jedis.mget(keys.toArray(new String[0]));
		}
	}
	
	public static List<String> getOnesWithoutNull(List<String> keys) {
		return getOnes(keys).stream().filter(one->one != null).collect(toList());
	}
	

	public static String getMapVal(String key, String mapKey) throws NoSuchElement{
		try(Jedis jedis = getJedis();){
			String rlt = jedis.hget(key, mapKey);
			if(rlt==null)
				throw new NoSuchElement();
			
			resetTimeoutGenerally(jedis, key);
			return rlt;
		}
	}
	/**
	 *  和getMapVal的区别是，当发生NoSuchElement时，可以区分是key过期，还是mapKey过期
	 */
	public static String getMapValWithSpecificException(String key, String mapKey) throws NoSuchElement{
		try(Jedis jedis = getJedis()){
			String rlt = jedis.hget(key, mapKey);
			if(rlt != null) {
				resetTimeoutGenerally(jedis, key);
				return rlt;
			}
			
			if(jedis.exists(key)) {
				throw new NoSuchElement(NoSuchElementType.REDIS_MAP_KEY_NOT_EXISTS);
			}else {
				throw new NoSuchElement(NoSuchElementType.REDIS_KEY_NOT_EXISTS);
			}
		}
	}
	
	private static void resetTimeoutGenerally(Jedis jedis,List<String> keys) {
		for(String key : keys) {
			resetTimeout(jedis,key,ALIVE_SECONDS);
		}
	}
	
	private static void resetTimeoutGenerally(Jedis jedis,String key) {
		resetTimeout(jedis,key,ALIVE_SECONDS);
	}
	
	/*特殊情况使用该方法*/
	public static synchronized String setForever(String key,String value) {
		try(Jedis jedis = getJedis()){
			return jedis.set(key, value);
		}
	}
	/*key相同会覆盖*/
	public static void setTemp(String key, String value) {
		try(Jedis jedis = getJedis()){
			jedis.setex(key, TEMP_ALIVE_SECONDS, value);
		}
	}
	/*key相同会覆盖*/
	public static synchronized void set(String key, String value) {
		try(Jedis jedis = getJedis()){
			jedis.setex(key, ALIVE_SECONDS, value);
		}
	}



	/*key相同会覆盖*/
	public static synchronized void set(Map<String,String> ones) {
		try(Jedis jedis = getJedis()){
			ones.forEach((key,value)->{
				jedis.setex(key, ALIVE_SECONDS, value);
			});
		}
	}
	
	public static synchronized void setOnlyIfKeyExists(Map<String,String> ones) {
		try(Jedis jedis = getJedis()){
			ones.forEach((key,value)->{
				if(!jedis.exists(key))
					return;
				
				jedis.setex(key, ALIVE_SECONDS, value);
			});
		}
	}
	
	public static synchronized void setOnlyIfKeyExists(String key, String value) {
		try(Jedis jedis = getJedis()){
			if(!jedis.exists(key))
				return;
			
			jedis.setex(key, ALIVE_SECONDS, value);
		}
	}
	
	
	/**
	 * @return true 代表 mapKey 无旧值 ; false 代表 mapKey 有旧值且被覆盖。
	 * @throws NoSuchElement 
	 */
	public static boolean setMap(String key, String mapKey, String mapValue) throws NoSuchElement {
		try(Jedis jedis = getJedis();){
			if(!jedis.exists(key)) {
				throw new NoSuchElement(NoSuchElementType.REDIS_KEY_NOT_EXISTS);
			}
			
			Long rlt = jedis.hset(key, mapKey, mapValue);
			resetTimeoutGenerally(jedis, key);
			if(rlt == 1) {
				return true;
			}
			if(rlt ==0) {
				return false;
			}
			logger.log(Level.SEVERE,"redis 出现了bug : hset添加一个key 却返回了多个？ "+rlt);
			return false;
		}
	}
	
	/**
	 * @return 代表无旧值被添加的key数量
	 */
	public static Long setMap(String key,Map<String,String> maps) {
		try(Jedis jedis = getJedis();){
			Long rlt=  jedis.hset(key, maps);
			resetTimeoutGenerally(jedis, key);
			return rlt;
		}
	}
	public static boolean setMapOnlyIfKeyNotExists(String key, String mapKey, String mapVal) {
		try(Jedis jedis = getJedis();){
			if(jedis.exists(key))
				return false;
			
			jedis.hset(key, mapKey, mapVal);
			resetTimeoutGenerally(jedis, key);
			return true;
		}
	}
	
	public static boolean setMapOnlyIfKeyExists(String key, String mapKey, String mapVal) {
		try(Jedis jedis = getJedis();){
			if(!jedis.exists(key))
				return false;
			
			jedis.hset(key, mapKey, mapVal);
			resetTimeoutGenerally(jedis, key);
			return true;
		}
	}
	/**
	 * 
	 * @param pattern
	 * @return 不会包含null
	 */
	public synchronized static List<String> findValues(String pattern){
		Set<String> matchedKeys = new HashSet<String>();
		List<String> values = new ArrayList<String>();
		String startString = "0";
		String curStr = startString;
		try(Jedis jedis = getJedis()){
			while(true) {
				ScanResult<String> oneScanRlt = jedis.scan(curStr,getScannerParam(pattern));

				for(String key : oneScanRlt.getResult()) {
					if(matchedKeys.contains(key)) {
						continue;
					}
					
					String val = jedis.get(key);
					if(val == null) {
						continue;
					}
					
					values.add(val);
					matchedKeys.add(key);
				}
				
				if(oneScanRlt.getCursor().equals(startString)) {
					break;
				}
				curStr = oneScanRlt.getCursor();
			}
		}
		
		assert values.stream().allMatch(v->v!=null);
		
		return values;
	}
	
	
	/**
	  *  注意: find函数不会刷新AliveTime!因此有极小的可能性当findKeys时能找到，但当真正使用时，keys失效了，需要上层做处理
	 * @param pattern
	 * @author 王天戈
	 * @return
	 */
	public synchronized static List<String> findKeys(String pattern){
		Set<String> rlt = new HashSet<String>();
		String startString = "0";
		String curStr = startString;
		try(Jedis jedis = getJedis()){
			while(true) {
				ScanResult<String> oneScanRlt = jedis.scan(curStr,getScannerParam(pattern));

				rlt.addAll(oneScanRlt.getResult());
				
				if(oneScanRlt.getCursor().equals(startString)) {
					break;
				}
				curStr = oneScanRlt.getCursor();
			}
		}
		return new ArrayList<String>(rlt);
	}
	
	
	public static Long deleteOnes(List<String> keys) {
		try(Jedis jedis = getJedis()){
			return jedis.del(keys.toArray(new String[0]));
		}
	}
	
	public static boolean deleteOne(String key) {
		try(Jedis jedis = getJedis()){
			return jedis.del(key) == 1 ? true : false;
		}
	}

	/**
	  *  若存在，会刷新aliveTime;不存在，则不会修改缓存
	 */
	public static boolean exists(String key) {
		try(Jedis jedis = getJedis()){
			if(!jedis.exists(key))
				return false;
			
			resetTimeoutGenerally(jedis, key);
			return true;
		}
	}

	/**
	 * @return true means append  false means do nothing
	 */
	public static boolean appendOnlyIfExists(String key, String valForAppend) {
		try(Jedis jedis = getJedis()){
			if(!jedis.exists(key))
				return false;
			
			resetTimeoutGenerally(jedis, key);
			jedis.append(key, valForAppend);
			return true;
		}
		
	}
	
	/**
	 * 通过key 和offset 从指定的位置开始将原先value替换
	 *
	 * @param key
	 * @param offset
	 * @param str
	 * @return
	 */
	public static Long setrange(String key, int offset, String str) {
		Jedis jedis = getJedis();
		return jedis.setrange(key, offset, str);
	}


	/**
	 * 批量的设置key:value,也可以一个
	 *
	 * @param keysValues
	 * @return
	 */
	public static String mset(String... keysValues) {
		Jedis jedis = getJedis();
		return jedis.mset(keysValues);
	}

	/**
	 * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
	 *
	 * @param keysValues
	 * @return
	 */
	public static Long msetnx(String... keysValues) {
		Jedis jedis = getJedis();
		return jedis.msetnx(keysValues);
	}

	/**
	 * 设置key的值,并返回一个旧值
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getSet(String key, String value) {
		Jedis jedis = getJedis();
		return jedis.getSet(key, value);
	}

	/**
	 * 通过下标 和key 获取指定下标位置的 value
	 *
	 * @param key
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	public static String getrange(String key, int startOffset, int endOffset) {
		Jedis jedis = getJedis();
		return jedis.getrange(key, startOffset, endOffset);
	}

	/**
	 * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
	 *
	 * @param key
	 * @return
	 */
	public static Long incr(String key) {
		Jedis jedis = getJedis();
		return jedis.incr(key);
	}

	/**
	 * 通过key给指定的value加值,如果key不存在,则这是value为该值
	 *
	 * @param key
	 * @param integer
	 * @return
	 */
	public static Long incrBy(String key, long integer) {
		Jedis jedis = getJedis();
		return jedis.incrBy(key, integer);
	}

	/**
	 * 对key的值做减减操作,如果key不存在,则设置key为-1
	 *
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		Jedis jedis = getJedis();
		return jedis.decr(key);
	}

	/**
	 * 减去指定的值
	 *
	 * @param key
	 * @param integer
	 * @return
	 */
	public static Long decrBy(String key, long integer) {
		Jedis jedis = getJedis();
		return jedis.decrBy(key, integer);
	}

	/**
	 * 通过key获取value值的长度
	 *
	 * @param key
	 * @return
	 */
	public static Long strLen(String key) {
		Jedis jedis = getJedis();
		return jedis.strlen(key);
	}

	/**
	 * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
	 *
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Long hsetnx(String key, String field, String value) {
		Jedis jedis = getJedis();
		return jedis.hsetnx(key, field, value);
	}



	/**
	 * 通过key同时设置 hash的多个field
	 *
	 * @param key
	 * @param hash
	 * @return
	 */
	public static String hmset(String key, Map<String, String> hash) {
		Jedis jedis = getJedis();
		return jedis.hmset(key, hash);
	}


	private static boolean resetTimeout(Jedis jedis,String key,long seconds) {
		Long flag = jedis.expire(key, seconds);
		return flag == 1 ? true : false;
	}
	
	public static boolean resetTimeout(String key, int seconds) {
		try(Jedis jedis = getJedis()){
			return resetTimeout(jedis, key, seconds);
		}
	}

	/**
	 * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
	 *
	 * @param key
	 * @param fields 可以是 一个String 也可以是 String数组
	 * @return
	 */
	public static List<String> hmget(String key, String... fields) {
		Jedis jedis = getJedis();
		return jedis.hmget(key, fields);
	}

	/**
	 * 通过key给指定的field的value加上给定的值
	 *
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Long hincrby(String key, String field, Long value) {
		Jedis jedis = getJedis();
		return jedis.hincrBy(key, field, value);
	}

	/**
	 * 通过key和field判断是否有指定的value存在
	 *
	 * @param key
	 * @param field
	 * @return
	 */
	public static Boolean hexists(String key, String field) {
		Jedis jedis = getJedis();
		return jedis.hexists(key, field);
	}

	/**
	 * 通过key返回field的数量
	 *
	 * @param key
	 * @return
	 */
	public static Long hlen(String key) {
		Jedis jedis = getJedis();
		return jedis.hlen(key);
	}

	/**
	 * 通过key 删除指定的 field
	 *
	 * @param key
	 * @param fields 可以是 一个 field 也可以是 一个数组
	 * @return
	 */
	public static Long hdel(String key, String... fields) {
		Jedis jedis = getJedis();
		return jedis.hdel(key, fields);
	}

	/**
	 * 通过key返回所有的field
	 *
	 * @param key
	 * @return
	 */
	public static Set<String> hkeys(String key) {
		Jedis jedis = getJedis();
		return jedis.hkeys(key);
	}

	/**
	 * 通过key返回所有和key有关的value
	 *
	 * @param key
	 * @return
	 */
	public static List<String> hvals(String key) {
		Jedis jedis = getJedis();
		return jedis.hvals(key);
	}

	/**
	 * 通过key获取所有的field和value
	 *
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetall(String key) {
		Jedis jedis = getJedis();
		return jedis.hgetAll(key);
	}

	/**
	 * 通过key向list头部添加字符串
	 *
	 * @param key
	 * @param strs 可以是一个string 也可以是string数组
	 * @return 返回list的value个数
	 */
	public static Long lpush(String key, String... strs) {
		Jedis jedis = getJedis();
		return jedis.lpush(key, strs);
	}

	/**
	 * 通过key向list尾部添加字符串
	 *
	 * @param key
	 * @param strs 可以是一个string 也可以是string数组
	 * @return 返回list的value个数
	 */
	public static Long rpush(String key, String... strs) {
		Jedis jedis = getJedis();
		return jedis.rpush(key, strs);
	}


	/**
	 * 通过key设置list指定下标位置的value 如果下标超过list里面value的个数则报错
	 *
	 * @param key
	 * @param index 从0开始
	 * @param value
	 * @return 成功返回OK
	 */
	public static String lset(String key, Long index, String value) {
		Jedis jedis = getJedis();
		return jedis.lset(key, index, value);
	}

	/**
	 * 通过key从对应的list中删除指定的count个 和 value相同的元素
	 *
	 * @param key
	 * @param count 当count为0时删除全部
	 * @param value
	 * @return 返回被删除的个数
	 */
	public static Long lrem(String key, long count, String value) {
		Jedis jedis = getJedis();
		return jedis.lrem(key, count, value);
	}

	/**
	 * 通过key保留list中从strat下标开始到end下标结束的value值
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return 成功返回OK
	 */
	public static String ltrim(String key, long start, long end) {
		Jedis jedis = getJedis();
		return jedis.ltrim(key, start, end);
	}

	/**
	 * 通过key从list的头部删除一个value,并返回该value
	 *
	 * @param key
	 * @return
	 */
	public static synchronized String lpop(String key) {
		Jedis jedis = getJedis();
		return jedis.lpop(key);
	}

	/**
	 * 通过key从list尾部删除一个value,并返回该元素
	 *
	 * @param key
	 * @return
	 */
	synchronized static public String rpop(String key) {
		Jedis jedis = getJedis();
		return jedis.rpop(key);
	}

	/**
	 * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value 如果第一个list为空或者不存在则返回null
	 *
	 * @param srckey
	 * @param dstkey
	 * @return
	 */
	public static String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = getJedis();
		return jedis.rpoplpush(srckey, dstkey);
	}

	/**
	 * 通过key获取list中指定下标位置的value
	 *
	 * @param key
	 * @param index
	 * @return 如果没有返回null
	 */
	public static String lindex(String key, long index) {
		Jedis jedis = getJedis();
		return jedis.lindex(key, index);
	}

	/**
	 * 通过key返回list的长度
	 *
	 * @param key
	 * @return
	 */
	public static Long llen(String key) {
		Jedis jedis = getJedis();
		return jedis.llen(key);
	}

	/**
	 * 通过key获取list指定下标位置的value 如果start 为 0 end 为 -1 则返回全部的list中的value
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> lrange(String key, long start, long end) {
		Jedis jedis = getJedis();
		return jedis.lrange(key, start, end);
	}

	/**
	 * 通过key向指定的set中添加value
	 *
	 * @param key
	 * @param members 可以是一个String 也可以是一个String数组
	 * @return 添加成功的个数
	 */
	public static Long sadd(String key, String... members) {
		Jedis jedis = getJedis();
		return jedis.sadd(key, members);
	}

	/**
	 * 通过key删除set中对应的value值
	 *
	 * @param key
	 * @param members 可以是一个String 也可以是一个String数组
	 * @return 删除的个数
	 */
	public static Long srem(String key, String... members) {
		Jedis jedis = getJedis();
		return jedis.srem(key, members);
	}

	/**
	 * 通过key随机删除一个set中的value并返回该值
	 *
	 * @param key
	 * @return
	 */
	public static String spop(String key) {
		Jedis jedis = getJedis();
		return jedis.spop(key);
	}

	/**
	 * 通过key获取set中的差集 以第一个set为标准
	 *
	 * @param keys 可以 是一个string 则返回set中所有的value 也可以是string数组
	 * @return
	 */
	public static Set<String> sdiff(String... keys) {
		Jedis jedis = getJedis();
		return jedis.sdiff(keys);
	}

	/**
	 * 通过key获取set中的差集并存入到另一个key中 以第一个set为标准
	 *
	 * @param dstkey 差集存入的key
	 * @param keys   可以 是一个string 则返回set中所有的value 也可以是string数组
	 * @return
	 */
	public static Long sdiffstore(String dstkey, String... keys) {
		Jedis jedis = getJedis();
		return jedis.sdiffstore(dstkey, keys);
	}

	/**
	 * 通过key获取指定set中的交集
	 *
	 * @param keys 可以 是一个string 也可以是一个string数组
	 * @return
	 */
	public static Set<String> sinter(String... keys) {
		Jedis jedis = getJedis();
		return jedis.sinter(keys);
	}

	/**
	 * 通过key获取指定set中的交集 并将结果存入新的set中
	 *
	 * @param dstkey
	 * @param keys   可以 是一个string 也可以是一个string数组
	 * @return
	 */
	public static Long sinterstore(String dstkey, String... keys) {
		Jedis jedis = getJedis();
		return jedis.sinterstore(dstkey, keys);
	}

	/**
	 * 通过key返回所有set的并集
	 *
	 * @param keys 可以 是一个string 也可以是一个string数组
	 * @return
	 */
	public static Set<String> sunion(String... keys) {
		Jedis jedis = getJedis();
		return jedis.sunion(keys);
	}

	/**
	 * 通过key返回所有set的并集,并存入到新的set中
	 *
	 * @param dstkey
	 * @param keys   可以 是一个string 也可以是一个string数组
	 * @return
	 */
	public static Long sunionstore(String dstkey, String... keys) {
		Jedis jedis = getJedis();
		return jedis.sunionstore(dstkey, keys);
	}

	/**
	 * 通过key将set中的value移除并添加到第二个set中
	 *
	 * @param srckey 需要移除的
	 * @param dstkey 添加的
	 * @param member set中的value
	 * @return
	 */
	public static Long smove(String srckey, String dstkey, String member) {
		Jedis jedis = getJedis();
		return jedis.smove(srckey, dstkey, member);
	}

	/**
	 * 通过key获取set中value的个数
	 *
	 * @param key
	 * @return
	 */
	public static Long scard(String key) {
		Jedis jedis = getJedis();
		return jedis.scard(key);
	}

	/**
	 * 通过key判断value是否是set中的元素
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Boolean sismember(String key, String member) {
		Jedis jedis = getJedis();
		return jedis.sismember(key, member);
	}

	/**
	 * 通过key获取set中随机的value,不删除元素
	 *
	 * @param key
	 * @return
	 */
	public static String srandmember(String key) {
		Jedis jedis = getJedis();
		return jedis.srandmember(key);
	}

	/**
	 * 通过key获取set中所有的value
	 *
	 * @param key
	 * @return
	 */
	public static Set<String> smembers(String key) {
		Jedis jedis = getJedis();
		return jedis.smembers(key);
	}

	/**
	 * 通过key向zset中添加value,score,其中score就是用来排序的 如果该value已经存在则根据score更新元素
	 *
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public static Long zadd(String key, double score, String member) {
		Jedis jedis = getJedis();
		return jedis.zadd(key, score, member);
	}

	/**
	 * 通过key删除在zset中指定的value
	 *
	 * @param key
	 * @param members 可以 是一个string 也可以是一个string数组
	 * @return
	 */
	public static Long zrem(String key, String... members) {
		Jedis jedis = getJedis();
		return jedis.zrem(key, members);
	}

	/**
	 * 通过key增加该zset中value的score的值
	 *
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public static Double zincrby(String key, double score, String member) {
		Jedis jedis = getJedis();
		return jedis.zincrby(key, score, member);
	}

	/**
	 * 通过key返回zset中value的排名 下标从小到大排序
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Long zrank(String key, String member) {
		Jedis jedis = getJedis();
		return jedis.zrank(key, member);
	}

	/**
	 * 通过key返回zset中value的排名 下标从大到小排序
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Long zrevrank(String key, String member) {
		Jedis jedis = getJedis();
		return jedis.zrevrank(key, member);
	}

	/**
	 * 通过key将获取score从start到end中zset的value socre从大到小排序 当start为0 end为-1时返回全部
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Set<String> zrevrange(String key, long start, long end) {
		Jedis jedis = getJedis();
		return jedis.zrevrange(key, start, end);
	}

	/**
	 * 通过key返回指定score内zset中的value
	 *
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public static Set<String> zrangebyscore(String key, String max, String min) {
		Jedis jedis = getJedis();
		return jedis.zrevrangeByScore(key, max, min);
	}

	/**
	 * 通过key返回指定score内zset中的value
	 *
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public static Set<String> zrangeByScore(String key, double max, double min) {
		Jedis jedis = getJedis();
		return jedis.zrevrangeByScore(key, max, min);
	}

	/**
	 * 返回指定区间内zset中value的数量
	 *
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public static Long zcount(String key, String min, String max) {
		Jedis jedis = getJedis();
		return jedis.zcount(key, min, max);
	}

	/**
	 * 通过key返回zset中的value个数
	 *
	 * @param key
	 * @return
	 */
	public static Long zcard(String key) {
		Jedis jedis = getJedis();
		return jedis.zcard(key);
	}

	/**
	 * 通过key获取zset中value的score值
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public static Double zscore(String key, String member) {
		Jedis jedis = getJedis();
		return jedis.zscore(key, member);
	}

	/**
	 * 通过key删除给定区间内的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long zremrangeByRank(String key, long start, long end) {
		Jedis jedis = getJedis();
		return jedis.zremrangeByRank(key, start, end);
	}

	/**
	 * 通过key删除指定score内的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Long zremrangeByScore(String key, double start, double end) {
		Jedis jedis = getJedis();
		return jedis.zremrangeByScore(key, start, end);
	}

	/**
	 * 返回满足pattern表达式的所有key keys(*) 返回所有的key
	 *
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		Jedis jedis = getJedis();
		return jedis.keys(pattern);
	}

	/**
	 * 通过key判断值得类型
	 *
	 * @param key
	 * @return
	 */
	public static String type(String key) {
		Jedis jedis = getJedis();
		return jedis.type(key);
	}
	
	private static Jedis getJedis() {
		if(POOL == null)
			POOL = initPool();
			
		Jedis jedis = null;
        try {
            jedis = POOL.getResource();
        } catch (Exception e) {
        	e.printStackTrace();
            logger.log(Level.SEVERE,"获取Jedis实例异常:"+e.getMessage());
            if(jedis != null)
            	jedis.close();
        }
        return jedis;
	}
	
	public static void clearAllCache_ONLYFORTEST() {
		try(Jedis jedis = getJedis()){
			Set<String> existedAll = jedis.keys("*");
			if(existedAll.size()>0) {
				jedis.del(existedAll.toArray(new String[0])) ;
			}
		}
	}
}
