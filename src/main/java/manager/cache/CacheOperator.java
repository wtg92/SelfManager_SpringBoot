package manager.cache;

import com.alibaba.fastjson2.JSON;
import manager.entity.SMEntity;
import manager.entity.general.User;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.exception.SMException;
import manager.system.SMError;
import manager.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static manager.cache.CacheConverter.*;
import static manager.util.CommonUtil.*;


/**
 * 有一点诡异
 * 不过把分逻辑的各个缓存都放在这里处理吧
 */
@Component
public class CacheOperator {
	
	
	@Resource CaffeineCollection caches;
	

	private static final Logger logger = LoggerFactory.getLogger(CacheOperator.class);




	public Integer getExpirationSeconds(){
		return caches.COMMON_EXPIRATION_OF_MIN;
	}

	public void set(String key, String value) {
		caches.Common_Cache.put(key,value);
	}

	public String get(String key)  {
		return caches.Common_Cache.getIfPresent(key);
	}

	public void remove(String key){
		caches.Common_Cache.invalidate(key);
	}

	public  <T extends SMEntity> T getOne(CacheMode mode, long identifier, Class<T> cla,
			ThrowableSupplier<T, DBException> generator) throws LogicException, DBException {
		String key = createEntityKey(mode,identifier,getEntityTableName(cla));
		String val = caches.Common_Cache.get(key,(k)->JSON.toJSONString(generator.get()));
		return JSON.parseObject(val, cla);
	}

	public Set<Integer> getPermsByUser(Long userId, Supplier<Set<Integer>> generator) {
		return caches.Perms_Cache.get(userId,(e)->generator.get());
	}

	public void clearPerms(Long userId){
		caches.Perms_Cache.invalidate(userId);
	}
	public void clearPerms(){
		caches.Perms_Cache.invalidateAll();
	}


	/*initor 使用ThrowableSupplier 的原因是 由于该函数 通常应当是dao.insert 要返回一个id  */
	public <T extends SMEntity> T getOneOrInitIfNotExists(CacheMode mode, long identifier, Class<T> cla,
                                                                 BiThrowableSupplier<T, DBException,NoSuchElement> generator, ThrowableSupplier<Long,DBException> initor) throws LogicException, DBException {
		
		String key = createEntityKey(mode,identifier,getEntityTableName(cla));

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

	private void deleteFromKeys(List<String> keys){
		keys.forEach(this::deleteFromKey);
	}

	private void deleteFromKey(String key){
		caches.Common_Cache.invalidate(key);
	}

	private void putKeyVal(String key,String val){
		caches.Common_Cache.put(key,val);
	}


	public String getGeneralValOrInit(CacheMode mode, ThrowableSupplier<String, SMException> generator, Object ...identifiers) throws DBException {
		String key = createGeneralKey(mode, identifiers);
		String val = caches.Common_Cache.getIfPresent(key);
		if(val != null){
			return val;
		}
		return caches.Common_Cache.get(key,(k)->JSON.toJSONString(generator.get()));
	}


	/**
	 * 先使用这个吧 至少会减轻服务器压力 不是吗
	 */
	public <T extends SMEntity> void saveEntity(T one,ThrowableConsumer<T, DBException> updator) throws DBException, LogicException {
		String key = createEntityKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()));
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

	public<T extends SMEntity> long countAllEntities(Class<T> cla) {
		String tableName = getEntityTableName(cla);
		String prefix = getPrefixEntity(tableName);
		Set<String> keys = caches.Common_Cache.asMap().keySet();
		return keys.stream().filter(one->one.startsWith(prefix)).count();
	}

	/**
	 * 这个需要全删除掉Cache
	 */
	public <T extends SMEntity> void saveEntities(List<T> ones, ThrowableConsumer<T, DBException> updater) throws DBException, LogicException {
		try {
			for(T one: ones) {
				updater.accept(one);
			}
		}finally {
			if(!ones.isEmpty()) {
				deleteFromKeys(ones.stream().map(one-> createEntityKey(CacheMode.E_ID,one.getId(),getEntityTableName(one.getClass()))).collect(toList()));
			}
		}
	}

	public <T extends SMEntity> void deleteEntityByIdOnlyForCache(T entity) throws DBException {
		deleteEntityByIdOnlyForCache(entity.getId(), getEntityTableName(entity.getClass()));
	}

	public void deleteEntityByIdOnlyForCache(long id,String tableName) throws DBException {
		String key = CacheConverter.createEntityKey(CacheMode.E_ID, id, tableName);
		deleteFromKey(key);
	}

	public <T extends SMEntity> void deleteEntityById(T entity,ThrowableConsumer<Long, DBException> deleter) throws DBException {
		assert entity.getId() != 0;
		deleter.accept(entity.getId());
		deleteEntityByIdOnlyForCache(entity);
	}


	public void deleteGeneralKey(CacheMode mode, Object ...identifier) {
		String key = createGeneralKey(mode, identifier);
		deleteFromKey(key);
	}


	public boolean setTempUser(String uuId) {
		if(caches.Temp_Users_Cache.getIfPresent(uuId) != null){
			/**
			 * 几乎不可能的情况 此刻不能覆盖已有用户
			 */
			return false;
		}

		caches.Temp_Users_Cache.put(uuId,new HashMap<>());
		return true;
	}

	public String getTempUserMapVal(String uuId, String key) {

		Map<String, String> tempUser = caches.Temp_Users_Cache.getIfPresent(uuId);

		if(tempUser == null)
			throw new NoSuchElement();

		/**
		 * 由于此时的缓存失效 只可能是整个失效 因此在此时 不可能为null
		 */
		return tempUser.get(key);
	}

	public void setTempMap(String uuId, String key, String val) {
		Map<String, String> tempUser = caches.Temp_Users_Cache.getIfPresent(uuId);

		if(tempUser == null)
			throw new NoSuchElement();

		tempUser.put(key,val);
	}

	public boolean existsForTemp(String uuId) {
		return caches.Temp_Users_Cache.getIfPresent(uuId) != null;
	}

	public void removeTempUser(String uuId) {
		caches.Temp_Users_Cache.invalidate(uuId);
	}


}
