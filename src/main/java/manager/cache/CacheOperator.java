package manager.cache;

import com.alibaba.fastjson2.JSON;
import manager.entity.SMEntity;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.exception.SMException;
import manager.system.SMError;
import manager.util.*;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static manager.cache.CacheConverter.*;
import static manager.util.CommonUtil.*;


@Component
public class CacheOperator {
	
	
	@Resource CaffeineCollection caches;
	

	private static final Logger logger = LoggerFactory.getLogger(CacheOperator.class);


	public void set(String key, String value) {
		caches.Common_Cache.put(key,value);
	}

	public  <T extends SMEntity> T getOne(CacheMode mode, long identifier, Class<T> cla,
			ThrowableSupplier<T, DBException> generator) throws LogicException, DBException {
		String key = createKey(mode,identifier,getEntityTableName(cla));
		String val = caches.Common_Cache.get(key,(k)->JSON.toJSONString(generator.get()));
		return JSON.parseObject(val, cla);
	}
	
	/*initor 使用ThrowableSupplier 的原因是 由于该函数 通常应当是dao.insert 要返回一个id  */
	public <T extends SMEntity> T getOneOrInitIfNotExists(CacheMode mode, long identifier, Class<T> cla,
                                                                 BiThrowableSupplier<T, DBException,NoSuchElement> generator, ThrowableSupplier<Long,DBException> initor) throws LogicException, DBException {
		
		String key = createKey(mode,identifier,getEntityTableName(cla));

		String val = caches.Common_Cache.getIfPresent(key);
		if(val != null){
			return JSON.parseObject(val, cla);
		}

		try {
			return JSON.parseObject(caches.Common_Cache.get(key,(k)->JSON.toJSONString(generator.get())), cla);
		} catch (NoSuchElement e1) {
			initor.get();
			try {
				return JSON.parseObject(caches.Common_Cache.get(key,(k)->JSON.toJSONString(generator.get())), cla);
			} catch (NoSuchElement e2) {
				e2.printStackTrace();
				assert false;
				throw new LogicException(SMError.WRONG_INITIATOR,identifier);
			}
		}
	}
	private void deleteFromKey(String key){
		caches.Common_Cache.invalidate(key);
	}

	private void putKeyVal(String key,String val){
		caches.Common_Cache.put(key,val);
	}

	public <T extends SMEntity> void saveEntityAndDeleteCache(T one,ThrowableConsumer<T, DBException> updator) {
		updator.accept(one);
		String key = createKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()));
		deleteFromKey(key);
	}

	/**
	 *   这种东西 不应该被使用
	 *   假设性能要求真地高到需要刷新缓存的话
	 *   那不应该使用通用的缓存 而是单独为其配置一个缓存类
	 */
	public <T extends SMEntity> void saveEntity(T one,ThrowableConsumer<T, DBException> updator) throws DBException, LogicException {
		String key = createKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()));
		try {
			updator.accept(one);
		}catch(DBException e) {
			if(e.type == SMError.DB_SYNC_ERROR) {
				deleteFromKey(key);
			}
			throw e;
		}
		String jsonStr = JSON.toJSONString(one);
		putKeyVal(key,jsonStr);
	}

	public <T extends SMEntity> void deleteEntityByIdOnlyForCache(T entity) throws DBException {
		deleteEntityByIdOnlyForCache(entity.getId(), getEntityTableName(entity.getClass()));
	}

	public void deleteEntityByIdOnlyForCache(long id,String tableName) throws DBException {
		String key = CacheConverter.createKey(CacheMode.E_ID, id, tableName);
		deleteFromKey(key);
	}

	public <T extends SMEntity> void deleteEntityById(T entity,ThrowableConsumer<Long, DBException> deletor) throws DBException {
		assert entity.getId() != 0;
		deletor.accept(entity.getId());
		deleteEntityByIdOnlyForCache(entity);
	}


	public void deleteTempKey(CacheMode mode, Object ...identifier) {
		String key = createTempKey(mode, identifier);
		deleteFromKey(key);
	}




}
