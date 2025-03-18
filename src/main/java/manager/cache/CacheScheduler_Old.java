package manager.cache;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static manager.cache.CacheConverter.createEntityKey;
import static manager.cache.CacheConverter.createPatternKey;
import static manager.cache.CacheConverter.createGeneralKey;
import static manager.cache.CacheConverter.createTempKeyByBiIdentifiers;
import static manager.cache.CacheConverter.parseRVal;
import static manager.cache.CacheConverter.parseRValInInt;
import static manager.util.CommonUtil.getBoolValFromPropertiesFileInResource;
import static manager.util.CommonUtil.getEntityTableName;
import static manager.util.CommonUtil.pretreatForString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.collections4.map.HashedMap;

import com.alibaba.fastjson2.JSON;

import manager.entity.SMEntity;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.exception.SMException;
import manager.system.SelfXErrors;
import manager.util.BiThrowableSupplier;
import manager.util.ThrowableConsumer;
import manager.util.ThrowableFunction;
import manager.util.ThrowableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  *  缓存基本策略：先看缓存是否有，有则直接返回，无则依赖generator取，加入缓存，返回经过字符串转化的结果（为了保持一致）
 *  
  *  关系表的双向缓存导致了一个特别讨厌的缺陷：一旦变化，需要同时更新两个方向的缓存，所幸设计的是使用删除key的手法更新，还好吧，但是逻辑调用时易漏
 *  
 *  
 *  对于保存：
 *  普遍的策略是：保存数据库-删除缓存。 本逻辑采用保存数据库-保存缓存。
 *  这样做的原因，是当发生了并发问题时，在保存方法内是可以通过DB的乐观锁异常得知的，此时save会做删除缓存的操作，然后抛出异常，迫使用户重新保存。
 *  这样就万无一失了，因为即便是普遍策略，也解决不了并发问题，即便是几率很小。
 *
 *  问题：
 *  TODO setTempMap 的功能好像并不是完全对的 本身Map的Temp 无法实现 ？？ 总之 这里还是使用的配置文件里的缓存时间而并非temp缓存时间
 *
 * @author 王天戈
 *
 */
public abstract class CacheScheduler_Old {
	private final static boolean USING_REDIS_CACHE = getBoolValFromPropertiesFileInResource("using_redis_cache");

	private static final Logger logger = LoggerFactory.getLogger(CacheScheduler_Old.class);

	public static <T extends SMEntity> T getOne(CacheMode mode, long identifier, Class<T> cla,
			ThrowableSupplier<T, DBException> generator) throws LogicException, DBException {
		
		if(!USING_REDIS_CACHE) {
			return generator.get();
		}
		
		String key = createEntityKey(mode,identifier,getEntityTableName(cla));
		try {
			String val = CacheUtil_OLD.getOne(key);
			return JSON.parseObject(val, cla);
		} catch (NoSuchElement e) {
			T t = generator.get();
			String jsonStr = JSON.toJSONString(t);
			CacheUtil_OLD.set(key,jsonStr );
			
			return JSON.parseObject(jsonStr, cla);
		}
	}
	
	/*initor 使用ThrowableSupplier 的原因是 由于该函数 通常应当是dao.insert 要返回一个id  */
	public static <T extends SMEntity> T getOneOrInitIfNotExists(CacheMode mode, long identifier, Class<T> cla,
                                                                 BiThrowableSupplier<T, DBException,NoSuchElement> generator, ThrowableSupplier<Long,DBException> initor) throws LogicException, DBException {
		
		if(!USING_REDIS_CACHE) {
			try {
				return generator.get();
			} catch (NoSuchElement e) {
				initor.get();
				try {
					return generator.get();
				} catch (NoSuchElement e1) {
					e1.printStackTrace();
					assert false;
					throw new LogicException(SelfXErrors.WRONG_INITIATOR,identifier);
				}
			}
		}
		
		String key = createEntityKey(mode,identifier,getEntityTableName(cla));
		try {
			String val = CacheUtil_OLD.getOne(key);
			return JSON.parseObject(val, cla);
		} catch (NoSuchElement e) {
			try {
				T t = generator.get();
				String jsonStr = JSON.toJSONString(t);
				CacheUtil_OLD.set(key,jsonStr );
				return JSON.parseObject(jsonStr, cla);
			} catch (NoSuchElement e1) {
				initor.get();
				try {
					T t = generator.get();
					String jsonStr = JSON.toJSONString(t);
					CacheUtil_OLD.set(key,jsonStr );
					return JSON.parseObject(jsonStr, cla);
				} catch (NoSuchElement e2) {
					e2.printStackTrace();
					assert false;
					throw new LogicException(SelfXErrors.WRONG_INITIATOR,identifier);
				}
			}
		}
	}
	
	
	public static<T> long countAll(CacheMode mode, Class<T> cla) throws LogicException {
		if(!USING_REDIS_CACHE) {
			return 0;
		}
		String tableName = getEntityTableName(cla);
		String patternKey = createPatternKey(mode,tableName);
		return CacheUtil_OLD.findKeys(patternKey).size();
	}
	
	
	public static <T> List<T> getOnes(CacheMode mode, List<Long> identifiers,Function<T, Long> identifierTranslator, Class<T> cla,
			ThrowableFunction<Long, T, DBException> oneGenerator) throws LogicException, DBException {
		
		if(identifiers.size() == 0)
			return new ArrayList<>();
		/*为实际环境做个保险*/
		List<Long> identifiersNoDup = identifiers.stream().distinct().collect(toList());
		assert identifiers.size() == identifiersNoDup.size() : "不应该传入相同的id";
		
		if(!USING_REDIS_CACHE) {
			/*不用缓存的情况是罕见情况，这时有性能浪费也没啥*/
			List<T> rlt = new ArrayList<>();
			for(int i=0;i < identifiersNoDup.size() ; i++) {
				rlt.add(oneGenerator.apply(identifiersNoDup.get(i)));
			}
			return rlt;
		}
		
		String tableName = getEntityTableName(cla);
		
		String patternKey = createPatternKey(mode,tableName);
		/*get all keys in cache*/
		List<String> existedKeysInCache = CacheUtil_OLD.findKeys(patternKey);
		List<String> keysForMatch = identifiersNoDup.stream().map(identifier->CacheConverter.createEntityKey(mode, identifier, tableName))
				.collect(toList());
		List<String> matchedExistedKeys = existedKeysInCache.stream().filter(key->keysForMatch.contains(key)).collect(toList());
		
		assert matchedExistedKeys.size() <= identifiersNoDup.size();

		List<T> rlt = new ArrayList<T>();
		
		if(matchedExistedKeys.size() > 0) {
			/*防止find key失效问题*/
			List<T> ones = CacheUtil_OLD.getOnesWithoutNull(matchedExistedKeys).stream()
						.map(js->JSON.parseObject(js, cla))
						.collect(toList());
			
			rlt.addAll(ones);
		}
		
		List<Long> deadIdentifiers = identifiersNoDup.stream().filter(identifier->
			!rlt.stream().anyMatch(one->identifierTranslator.apply(one).equals(identifier))
		).collect(toList());
		
		for(Long idenfierForAddToCache : deadIdentifiers) {
			T t = oneGenerator.apply(idenfierForAddToCache);
			String keyForOne = createEntityKey(mode,idenfierForAddToCache,tableName);
			String jsonStr = JSON.toJSONString(t);
			CacheUtil_OLD.set(keyForOne,jsonStr);
			rlt.add(JSON.parseObject(jsonStr, cla));
		}
		
		assert rlt.size() == identifiersNoDup.size();
		
		return rlt;
	}
	
	/**
	  *  用于一些Unique字段的读取，策略为：先从已有缓存（所有数据）里找，找不到 generator提供，然后根据mode重新放入缓存中（通常来说，缓存中不会以unique字段为key）
	 * example : 本来想的是处理登录 getUserByAccount 后来觉得没必要用缓存，暂时无例子，留在这 未来可能有用
	 * 
	 * @param identifierTranslator 实体类和unique字段的转化器，用来和identifier比较
	 * @param keyIdentifierGenerator 重新生成缓存时，生成key时的identifier转化器 当缓存中没有时，从generator得来，再加进缓存时用到
	 * @param generator 当缓存没有时，用来生成实体对象
	 * 
	 * TODO 这是架构的缺陷导致这个方法参数这么恶心 暂时没想出解决方法
	 * @throws NoSuchElement 
	 * 
	 */
	public static <T extends SMEntity> T findOne(CacheMode mode, Object identifier,Function<T, Object> identifierTranslator,
			Function<T, Integer> keyIdentifierGenerator,Class<T> cla, BiThrowableSupplier<T, DBException, NoSuchElement> generator) throws DBException, LogicException, NoSuchElement {
		if(!USING_REDIS_CACHE) {
			return generator.get();
		}
		
		String tableName = getEntityTableName(cla);
		String patternKey = createPatternKey(mode,tableName);
		List<T> allInCache = CacheUtil_OLD.findValues(patternKey).stream().map(js->JSON.parseObject(js, cla)).collect(toList());
		
		List<T> matchedObjs = allInCache.stream().filter(one->identifierTranslator.apply(one).equals(identifier)).collect(toList());
		if(matchedObjs.size() > 1) {
			throw new LogicException(SelfXErrors.INCONSISTENT_CACHE_ERROR,"Unique字段在缓存中有多份 "+ matchedObjs.size()+ " "+identifier.toString());
		}
		if(matchedObjs.size() == 1) {
			return matchedObjs.get(0);
		}
		
		T t = generator.get();
		String jsonStr = JSON.toJSONString(t);
		
		String key = createEntityKey(mode, keyIdentifierGenerator.apply(t), tableName);
		CacheUtil_OLD.set(key,jsonStr);
		
		return JSON.parseObject(jsonStr, cla);
	}

	public static<T extends SMEntity> void saveEntityAndDeleteCache(T one,ThrowableConsumer<T, DBException> updater) {
		if(!USING_REDIS_CACHE) {
			updater.accept(one);
			return;
		}

		updater.accept(one);

		String key = createEntityKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()));
		CacheUtil_OLD.deleteOne(key);
	}
	
	/**
	 * 加入缓存/刷新已有缓存
	 * 判断是否发生了并发危险，假设发生了这种危险，则删除缓存
	 *
	 * TODO 有精力或许应该想想 这样的缓存更新 有风险吗？
	 *
	 * 单个的更新 我暂且忍受他是更新缓存 而非删除的？  -- 2024.1.15
	 * @throws LogicException 
	 */
	public static<T extends SMEntity> void saveEntity(T one,ThrowableConsumer<T, DBException> updater) throws DBException, LogicException {
		if(!USING_REDIS_CACHE) {
			updater.accept(one);
			return;
		}
		
		String key = createEntityKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()));
		try {
			updater.accept(one);
		}catch(DBException e) {
			if(e.type == SelfXErrors.DB_SYNC_ERROR) {
				CacheUtil_OLD.deleteOne(key);
			}
			throw e;
		}
		String jsonStr = JSON.toJSONString(one);
		CacheUtil_OLD.set(key,jsonStr);
	}

	/**
	 * 之前这种逻辑是错误的
	 * 缓存原则上不该有更新缓存的逻辑
	 * 如果有 通常是上层逻辑耦合导致
	 * @param ones
	 * @param updater
	 * @param <T>
	 * @throws DBException
	 * @throws LogicException
	 */
	public static<T extends SMEntity> void saveInDBAndDeleteAllInCache(List<T> ones, ThrowableConsumer<T, DBException> updater) throws DBException, LogicException {
		if(!USING_REDIS_CACHE) {
			for(T one: ones) {
				updater.accept(one);	
			}
			return;
		}
		
		try {
			for(T one: ones) {
				updater.accept(one);	
			}
		}finally {
			if(ones.size() > 0) {
				CacheUtil_OLD.deleteOnes(ones.stream().map(one-> createEntityKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()))).collect(toList()));
			}
		}
	}
	
	/**
	 *  由于缓存策略（get时如果没有，则会重新设置缓存），当数据被修改时使用本方法，在下次取数据时，会自动更新缓存
	 * @return 成功删除的数量
	 */
	public static Long deleteRCachesIfExist(CacheMode mode, String tableName, List<Long> identifiers) {
		if(!USING_REDIS_CACHE || identifiers.size() == 0) {
			return (long)0 ;
		}
		
		List<String> keysForDelete = identifiers.stream()
				.map(identifier->CacheConverter.createEntityKey(mode, identifier, tableName))
				.collect(toList());
		
		return CacheUtil_OLD.deleteOnes(keysForDelete);
	}
	
	public static Long deleteRCachesIfExistByInt(CacheMode mode, String tableName, List<Integer> identifiers) {
		return deleteRCachesIfExist(mode, tableName, identifiers.stream().map(i->(long)i.intValue()).collect(toList()));
	}
	
	
	public static boolean deleteRCacheIfExists(CacheMode mode, String tableName, Long identifier) {
		if(!USING_REDIS_CACHE ) {
			return false;
		}
		
		String key = CacheConverter.createEntityKey(mode, identifier, tableName);
		
		return CacheUtil_OLD.deleteOne(key);
	}
	
	public static<T extends SMEntity> void deleteEntityByIdOnlyForCache(T entity) throws DBException {
		deleteEntityByIdOnlyForCache(entity.getId(), getEntityTableName(entity.getClass()));
	}
	
	public static void deleteEntityByIdOnlyForCache(long id,String tableName) throws DBException {
		if(!USING_REDIS_CACHE ) {
			return ;
		}
		String key = CacheConverter.createEntityKey(CacheMode.E_ID, id, tableName);
		CacheUtil_OLD.deleteOne(key);
	}
	
	public static<T extends SMEntity> void deleteEntityById(T entity,ThrowableConsumer<Long, DBException> deletor) throws DBException {
		assert entity.getId() != 0;
		deletor.accept(entity.getId());
		deleteEntityByIdOnlyForCache(entity);
	}


	public static boolean deleteTempKey(CacheMode mode, Object ...identifier) {
		String key = createGeneralKey(mode, identifier);
		if(!USING_REDIS_CACHE ) {
			redisSubstitute.remove(key);
			return true;
		}
		return CacheUtil_OLD.deleteOne(key);
	}
	/**
	 * example r_user_group  建立former 缓存    传入userId 得到groupsId
	 * @param mode
	 * @param tableName
	 * @param identifier
	 * @param generator
	 * @return
	 * @throws DBException
	 */
	public static List<Long>  getRIds(CacheMode mode, String tableName, long identifier, ThrowableSupplier<List<Long>, DBException> generator) throws DBException {
		if(!USING_REDIS_CACHE) {
			return generator.get();
		}
		String key = createEntityKey(mode, identifier, tableName);
		try {
			return parseRVal(CacheUtil_OLD.getOne(key));
		} catch (NoSuchElement e) {
			List<Long> val = generator.get();
			String rVal = CacheConverter.createRsVal(val);
			CacheUtil_OLD.set(key,rVal);
			return val;
		}
	}
	
	
	public static List<Integer> getRIdsInInt(CacheMode mode, String tableName, long identifier, ThrowableSupplier<List<Integer>, DBException> generator) throws DBException {
		if(!USING_REDIS_CACHE) {
			return generator.get();
		}
		String key = createEntityKey(mode, identifier, tableName);
		try {
			return parseRValInInt(CacheUtil_OLD.getOne(key));
		} catch (NoSuchElement e) {
			List<Integer> val = generator.get();
			String rVal = CacheConverter.createRsVal(val);
			CacheUtil_OLD.set(key,rVal);
			return val;
		}
	}
	
	/*根据成为缓存key值的identifier判断是否存在*/
	public static boolean existsByIdentifier(CacheMode mode, String tableName, long identifier,
			ThrowableSupplier<Boolean, DBException> judge) throws DBException {
		if(!USING_REDIS_CACHE) {
			return judge.get();
		}
		String key = createEntityKey(mode, identifier, tableName);
		boolean exists = CacheUtil_OLD.exists(key);
		if(exists)
			return true;
		
		return judge.get();
	}
	/**
	 *  对未成为key值组成部分的Unique字段判断是否存在
	 * 策略：先从缓存里找，如果exists，返回true，否则再从数据库中找 
	 */
	public static<T> boolean existsByField(CacheMode mode,Function<T, Object> fieldTranslator
			, Object valForMatch,Class<T> cla,ThrowableSupplier<Boolean, DBException> judgerForDb) throws DBException, LogicException {
		return exists(mode, cla, one->{
			Object translatedField = fieldTranslator.apply(one);
			assert valForMatch != null;
			if(translatedField == null) {
				return false;
			}
			
			return pretreatForString(fieldTranslator.apply(one)).equals(pretreatForString(valForMatch));
		},judgerForDb);
	}
	
	/**
	 * 有点恶心 但是在这一层 需要处理Null值得情况
	 */
	public static<T> boolean existsByBiFields(CacheMode mode,Function<T, Object> fieldTranslator1,
			Object val1ForMatch,Function<T, Object> fieldTranslator2,
			Object val2ForMatch,Class<T> cla,
			ThrowableSupplier<Boolean, DBException> judgerForDb) throws DBException, LogicException {
		
		return exists(mode, cla, one->{
			Object translatedField1 = fieldTranslator1.apply(one);
			Object translatedField2 = fieldTranslator2.apply(one);
			
			assert val1ForMatch != null;
			assert val2ForMatch != null;
			
			if(translatedField1 == null || translatedField2 == null) {
				return false;
			}
			
			return pretreatForString(translatedField1).equals(pretreatForString(val1ForMatch))
						&& pretreatForString(translatedField2).equals(pretreatForString(val2ForMatch));}, judgerForDb);
	}
	
	private static<T> boolean exists(CacheMode mode,
			Class<T> cla,
			Function<T,Boolean> judgerForCache,
			ThrowableSupplier<Boolean, DBException> judgerForDb) throws DBException {
		if(!USING_REDIS_CACHE) {
			return judgerForDb.get();
		}
		
		String tableName = getEntityTableName(cla);
		String patternKey = createPatternKey(mode,tableName);
		/*get all keys in cache*/
		List<String> existedKeysInCache = CacheUtil_OLD.findKeys(patternKey);
		if(existedKeysInCache.size() > 0) {
			/*防止find key失效问题*/
			List<T> onesInCache = CacheUtil_OLD.getOnesWithoutNull(existedKeysInCache).stream()
						.map(js->JSON.parseObject(js, cla))
						.collect(toList());
			
			if(onesInCache.stream().anyMatch(e->judgerForCache.apply(e))) {
				return true;
			}
		}
		return judgerForDb.get();
	}
	
	private static final Map<String,String> redisSubstitute = new HashedMap<String, String>();
	private static final Map<String,Map<String,String>> redisMapSubstitute = new HashedMap<String, Map<String,String>>();
	
	/**
	 * 为了保障redis的原子性设计的方法
	 * @return true 代表key在数据库中不存在，并且成功设置上了所需要的值 false 表明在数据库中已存在，没有set值
	 */
	public static boolean setTempMapOnlyIfKeyNotExists(CacheMode mode,String identifier,String mapKey,String mapVal) {
		String key = createGeneralKey(mode, identifier);
		if(!USING_REDIS_CACHE) {
			if(redisMapSubstitute.containsKey(key)) {
				Map<String,String> existed = redisMapSubstitute.get(key);
				existed.put(mapKey,mapVal);
				return false;
			}
			Map<String,String> init = new HashedMap<String, String>();
			init.put(mapKey, mapVal);
			redisMapSubstitute.put(key,init);
			System.out.println("redisMapSubstitute Added A Temp KEY:"+key);
			return true;
		}
		
		return CacheUtil_OLD.setMapOnlyIfKeyNotExists(key,mapKey,mapVal);
	}
	/**
	 * 为了保障redis的原子性设计的方法
	 * @return true 代表key在数据库中已存在，并且成功设置上了所需要的值 false 表明在数据库中未找到，没有set值
	 */
	public static boolean setTempMapOnlyIfKeyExists(CacheMode mode,String identifier,String mapKey,String mapVal) {
		
		String key = createGeneralKey(mode, identifier);
		
		if(!USING_REDIS_CACHE) {
			if(!redisMapSubstitute.containsKey(key)) {
				return false;
			}
			redisMapSubstitute.get(key).put(mapKey, mapVal);
			return true;
		}

		return CacheUtil_OLD.setMapOnlyIfKeyExists(key,mapKey,mapVal);
	}
	
	/**
	 * @throws NoSuchElement key not exists
	 */
	public static void setTempMap(CacheMode mode, String identifier,String mapKey,String mapVal) throws NoSuchElement{
		String key = createGeneralKey(mode, identifier);
		
		if(!USING_REDIS_CACHE) {
			if(!redisMapSubstitute.containsKey(key))
				throw new NoSuchElement();
			
			redisMapSubstitute.get(key).put(mapKey, mapVal);
			return;
		}

		CacheUtil_OLD.setMap(key, mapKey, mapVal);
	}
	
	public static void setTemp(CacheMode mode, String identifier,String value){
		String key = createGeneralKey(mode, identifier);
		
		if(!USING_REDIS_CACHE) {
			redisSubstitute.put(key, value);
			return;
		}
		
		CacheUtil_OLD.setTemp(key, value);
	}
	
	public static void setTempByBiIdentifiers(CacheMode mode, Object identifier1,Object identifier2,String value){
		String key = createTempKeyByBiIdentifiers(mode, pretreatForString(identifier1).toString(), pretreatForString(identifier2).toString());
		
		if(!USING_REDIS_CACHE) {
			redisSubstitute.put(key, value);
			return;
		}

		CacheUtil_OLD.setTemp(key, value);
	}
	
	
	
	
	/*通用需求:把一个实体加入缓存中，以id为key*/
	public static<T extends SMEntity> void putEntityToCacheById(T t) throws LogicException {
		if(!USING_REDIS_CACHE) {
			return;
		}
		String key = createEntityKey(CacheMode.E_ID,t.getId(),getEntityTableName(t.getClass()));
		CacheUtil_OLD.set(key, JSON.toJSONString(t));
	}
	
	public static<T extends SMEntity> void putEntitiesToCacheById(List<T> target) {
		if(!USING_REDIS_CACHE) {
			return;
		}
		
		Map<String,String> ones = target.stream().collect(toMap(t-> createEntityKey(CacheMode.E_ID,t.getId()
				,getEntityTableName(t.getClass())),t->JSON.toJSONString(t)));
		CacheUtil_OLD.set(ones);
	}
	
	
	public static boolean existsForTemp(CacheMode mode, String identifier) {
		String key = createGeneralKey(mode, identifier);
		
		if(!USING_REDIS_CACHE) {
			boolean existed = redisSubstitute.containsKey(key) || redisMapSubstitute.containsKey(key);
			return existed;
		}
		return CacheUtil_OLD.exists(key);
	}

	public static String getTempMapValWithoutReset(CacheMode mode, String identifier, String mapKey) throws NoSuchElement{
		String key = createGeneralKey(mode, identifier);
		
		if(!USING_REDIS_CACHE) {
			Map<String,String> tempEntry = redisMapSubstitute.get(key);
			if(!tempEntry.containsKey(mapKey))
				throw new NoSuchElement();
			
			return redisMapSubstitute.get(key).get(mapKey);
		}
	
		return CacheUtil_OLD.getMapVal(key,mapKey);
	}

	public static String getTempValOrInit(CacheMode mode,ThrowableSupplier<String, SMException> generator, Object ...identifiers) throws SMException {
		try {
			return getTempVal(mode, identifiers);
		} catch (NoSuchElement e) {
			String key = createGeneralKey(mode, identifiers);
			String val = generator.get();
			if(!USING_REDIS_CACHE) {
				redisSubstitute.put(key, val);
				return val;
			}
			CacheUtil_OLD.set(key, val);
			return val;
		}
	}

	@Deprecated
	public static String getTempValOrInit(CacheMode mode, Object identifier,ThrowableSupplier<Object, SMException> generator) throws SMException {
		try {
			return getTempVal(mode, identifier);
		} catch (NoSuchElement e) {
			String key = createGeneralKey(mode, pretreatForString(identifier).toString());
			String val = pretreatForString(generator.get()).toString();
			if(!USING_REDIS_CACHE) {
				redisSubstitute.put(key, val);
				return val;
			}
			CacheUtil_OLD.set(key, val);
			return val;
		}
	}
	
	public static String getTempVal(CacheMode mode, Object ...identifiers) throws DBException, NoSuchElement {
		String key = createGeneralKey(mode, identifiers);
		if(!USING_REDIS_CACHE) {
			if(!redisSubstitute.containsKey(key))
				throw new NoSuchElement();
			return redisSubstitute.get(key);
		}
		return CacheUtil_OLD.getOne(key);
	}
	
	
	public static String getTempValByBiIdentifiers(CacheMode mode, Object identifier1,Object identifier2) throws DBException, NoSuchElement {
		
		String key = createTempKeyByBiIdentifiers(mode, pretreatForString(identifier1).toString(),pretreatForString(identifier2).toString());
		
		if(!USING_REDIS_CACHE) {
			return redisSubstitute.get(key);
		}

		return CacheUtil_OLD.getOne(key);
	}

	public static boolean deleteTempValByBiIdentifiers(CacheMode mode, Object identifier1,Object identifier2) throws DBException, NoSuchElement {

		String key = createTempKeyByBiIdentifiers(mode, pretreatForString(identifier1).toString(),pretreatForString(identifier2).toString());

		if(!USING_REDIS_CACHE) {
			return redisSubstitute.remove(key) != null;
		}

		return CacheUtil_OLD.deleteOne(key);
	}
	
	
	public static void appendROnlyIfExists(CacheMode mode,String tableName, long theOneId,
			long theManyId) {
		appendRsOnlyIfExist(mode, tableName, theOneId, Arrays.asList(theManyId));
	}
	
	public static void appendRsOnlyIfExist(CacheMode mode,String tableName, Long theOneId,
			List<Long> theManyIds) {
		if(!USING_REDIS_CACHE) {
			return;
		}
		String key = createEntityKey(mode, theOneId, tableName);
		String valForAppend = CacheConverter.createRsValForAppend(theManyIds);
		CacheUtil_OLD.appendOnlyIfExists(key, valForAppend);
	}

	public static void clearAllCache_ONLYFORTEST() {
		if(!USING_REDIS_CACHE) {
			redisMapSubstitute.clear();
			redisSubstitute.clear();
			return;
		}
		
		CacheUtil_OLD.clearAllCache_ONLYFORTEST();
	}


}
