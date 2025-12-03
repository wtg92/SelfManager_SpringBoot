package manager.cache;

import com.alibaba.fastjson2.JSON;
import manager.booster.longRunningTasks.LongRunningTasksMessage;
import manager.entity.SMEntity;
import manager.entity.general.FileRecord;
import manager.entity.general.User;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanBalance;
import manager.solr.books.PageNode;
import manager.solr.books.SharingBook;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.exception.SMException;
import manager.solr.books.SharingLink;
import manager.system.SelfXErrors;
import manager.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static manager.cache.CacheConverter.*;
import static manager.util.CommonUtil.*;


/**
 * 有一点诡异
 * 不过把分逻辑的各个缓存都放在这里处理吧
 *
 * !!! 所有的特定类型的缓存都需要clone 不允许被上层改动
 */
@Component
public class CacheOperator {
	
	
	@Resource CaffeineCollection caches;
	

	private static final Logger logger = LoggerFactory.getLogger(CacheOperator.class);



	private <T> void saveWithCacheEvict(long key, T obj,
										ThrowableConsumer<T, DBException> updater,
										Consumer<Long> evictor) {
		try {
			updater.accept(obj);
		} finally {
			evictor.accept(key);
		}
	}


	// 验证码过期时间 check ok
	public Integer getExpirationSeconds(){
		return caches.COMMON_EXPIRATION_OF_MIN;
	}
	// 验证码信息记录 check ok
	public void set(String key, String value) {
		caches.Common_Cache.put(key,value);
	}
	// 验证码信息记录 check ok
	public String get(String key)  {
		return caches.Common_Cache.getIfPresent(key);
	}
	// 验证码信息记录 check ok
	public void remove(String key){
		caches.Common_Cache.invalidate(key);
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
	public PlanBalance getOneOrInitBalanceIfNotExistsByUserId(long userId,
											   BiThrowableSupplier<PlanBalance, DBException,NoSuchElement> generator, ThrowableSupplier<Long,DBException> initor){
		
		PlanBalance val = caches.PlanBalance_Cache.getIfPresent(userId);
		if(val != null){
			return val;
		}

		try {
			val = generator.get();
			caches.PlanBalance_Cache.put(userId, val);
			return val;
		} catch (NoSuchElement e1) {
			initor.get();
			try {
				val = generator.get();
				caches.PlanBalance_Cache.put(userId,val);
				return val;
			} catch (NoSuchElement e2) {
				e2.printStackTrace();
				assert false;
				throw new LogicException(SelfXErrors.WRONG_INITIATOR,userId);
			}
		}
	}

	public void saveBalanceByOwnerId(PlanBalance one,ThrowableConsumer<PlanBalance, DBException> updater){
		long key = one.getOwnerId();
		try {
			updater.accept(one);
		}catch(DBException e) {
			if(e.type == SelfXErrors.DB_SYNC_ERROR) {
				removeBalance(key);
			}
			throw e;
		}
		removeBalance(key);
	}
	private void removeBalance(long id){
		caches.PlanBalance_Cache.invalidate(id);
	}

	public void savePlans(List<Plan> ones, ThrowableConsumer<Plan, DBException> updater){
		try {
			for(Plan one: ones) {
				updater.accept(one);
			}
		}finally {
			if(!ones.isEmpty()) {
				ones.forEach(plan -> removePlan(plan.getId()));
			}
		}
	}

	public void saveWorksheets(List<WorkSheet> ones, ThrowableConsumer<WorkSheet, DBException> updater){
		try {
			for(WorkSheet one: ones) {
				updater.accept(one);
			}
		}finally {
			if(!ones.isEmpty()) {
				ones.forEach(workSheet -> removeWorksheet(workSheet.getId()));
			}
		}
	}

	// worksheet count   --- check ok
	public String getGeneralValOrInit(CacheMode mode, ThrowableSupplier<String, SMException> generator, Object ...identifiers) {
		String key = createGeneralKey(mode, identifiers);
		String val = caches.Common_Cache.getIfPresent(key);
		if(val != null){
			return val;
		}
		return caches.Common_Cache.get(key,(k)->generator.get());
	}


	public void saveUser(User one,ThrowableConsumer<User, DBException> updater){
		saveWithCacheEvict(one.getId(), one, updater, this::removeUser);
	}
	private void removeUser(long id){
		caches.Users_Cache.invalidate(id);
	}
	public User getUser(Long id, ThrowableSupplier<User, DBException> generator){
		return caches.Users_Cache.get(id,(k)->generator.get()).clone();
	}

	public Plan getPlan(Long id, ThrowableSupplier<Plan, DBException> generator){
		return caches.Plans_Cache.get(id,(k)->generator.get()).clone();
	}

	public void savePlan(Plan one,ThrowableConsumer<Plan, DBException> updater){
		saveWithCacheEvict(one.getId(), one, updater, this::removePlan);
	}
	public void removePlan(long id){
		caches.Plans_Cache.invalidate(id);
	}
	/*- Worksheet----start- */
	public WorkSheet getWorksheet(Long wsId,ThrowableSupplier<WorkSheet, DBException> generator){
		return caches.Worksheet_Cache.get(wsId,(k)->generator.get()).clone();
	}
	public void removeWorksheet(long id){
		caches.Worksheet_Cache.invalidate(id);
	}

	public void deleteEntity(long id,Runnable deleting,Consumer<Long> remover){
		try{
			deleting.run();
		} finally {
			remover.accept(id);
		}
	}

	public void saveWorksheet(WorkSheet one,ThrowableConsumer<WorkSheet, DBException> updater){
		saveWithCacheEvict(one.getId(), one, updater, this::removeWorksheet);
	}

	/*- Worksheet----end- */

	/*- FileRecord----start- */

	public FileRecord getFileRecord(Long id,ThrowableSupplier<FileRecord, DBException> generator){
		return caches.File_Records_Cache.get(id,(k)->generator.get()).clone();
	}
	public void saveFileRecord(FileRecord one, ThrowableConsumer<FileRecord, DBException> updater) {
		long key = one.getId();
		try {
			updater.accept(one);
		}catch(DBException e) {
			if(e.type == SelfXErrors.DB_SYNC_ERROR) {
				removeFileRecord(key);
			}
			throw e;
		}
		removeFileRecord(key);
	}
	public void removeFileRecord(long id){
		caches.File_Records_Cache.invalidate(id);
	}

	/*- FileRecord----end- */

	/*- Links----start- */

	public void saveLink(long loginId,Boolean isCommunityLink, String linkId, Runnable saving ) {
		try{
			saving.run();
		}finally {
			removeLinkFromCache(loginId,isCommunityLink,linkId);
		}
	}

	private static String generateLinkCacheId(long loginId,Boolean isCommunityLink,String linkId){
		return
				isCommunityLink ? linkId :
						generateCacheIdInUserIsolation(loginId,linkId);
	}

	public SharingLink getLink(long loginId,Boolean isCommunityLink,String linkId, Supplier<SharingLink> generator) {
		String cacheId =generateLinkCacheId(loginId,isCommunityLink,linkId);
		SharingLink link = caches.Links_Cache.get(cacheId,(k)->generator.get());
		if(link == null){
			return null;
		}
		return link.clone();
	}

	public void deleteLink(long loginId,Boolean isCommunityLink,String linkId,Runnable deleting){
		try{
			deleting.run();
		}finally {
			removeLinkFromCache(loginId,isCommunityLink,linkId);
		}
	}

	public void removeLinkFromCache(long loginId,Boolean isCommunityLink,String linkId){
		String cacheId =generateLinkCacheId(loginId,isCommunityLink,linkId);
		caches.Links_Cache.invalidate(cacheId);
	}

	/*- Links----end- */

	/*- BOOK----start- */

	public SharingBook getBook(long loginId, String bookId, Supplier<SharingBook> generator) {
		String cacheId = generateCacheIdInUserIsolation(loginId,bookId);
		return caches.Books_Cache.get(cacheId,(k)->generator.get()).clone();
	}

	public List<String> getClosedBookIds(long loginId, Supplier<List<String>> generator) {
		List<String> list = caches.Closed_Book_Ids_Cache.get(loginId, (k) -> generator.get());
		return list == null ? null : new ArrayList<>(list);
	}

	public void removeClosedBookIdsFromCache(long loginId) {
		caches.Closed_Book_Ids_Cache.invalidate(loginId);
	}

	private void removeBookFromCache(long loginId, String id){
		String cacheId = generateCacheIdInUserIsolation(loginId,id);
		caches.Books_Cache.invalidate(cacheId);
	}

	public void deleteBook(long loginId,String id,Runnable deleting){
		try{
			deleting.run();
		}finally {
			removeBookFromCache(loginId,id);
			removeClosedBookIdsFromCache(loginId);
		}
	}

	public void saveBook(long loginId, String bookId, Runnable saving ) {
		try{
			saving.run();
		}finally {
			removeBookFromCache(loginId,bookId);
		}
	}

	/*- BOOK----end- */

	/*- PageNode----start- */
	public PageNode getPageNode(long loginId, String pageId, Supplier<PageNode> generator) {
		String cacheId = generateCacheIdInUserIsolation(loginId,pageId);
		PageNode pageNode = caches.Page_Nodes_Cache.get(cacheId, (k) -> generator.get());
		if(pageNode == null){
			return null;
		}
		return pageNode.clone();
	}

	private void removePageNode(long loginId,String id){
		String cacheId = generateCacheIdInUserIsolation(loginId,id);
		caches.Page_Nodes_Cache.invalidate(cacheId);
	}

	public void deletePageNode(long loginId,String id,Runnable deleting){
		try {
			deleting.run();
		}finally {
			removePageNode(loginId,id);
		}
	}
	public void savePageNode(long loginId, String nodeId, Runnable saving ) {
		try{
			saving.run();
		}finally {
			removePageNode(loginId,nodeId);
		}
	}

	/*- PageNode----end- */

	public void pushLongRunningMessage(long userId, LongRunningTasksMessage msg){
		caches.Long_Running_Tasks_Cache.put(userId,msg);
	}

	public LongRunningTasksMessage getLongRunningMessage(long userId){
		return caches.Long_Running_Tasks_Cache.getIfPresent(userId);
	}


	public<T extends SMEntity> long countAllEntities(Class<T> cla) {
		String tableName = getEntityTableName(cla);
		String prefix = getPrefixEntity(tableName);
		Set<String> keys = caches.Common_Cache.asMap().keySet();
		return keys.stream().filter(one->one.startsWith(prefix)).count();
	}


	public void deleteGeneralKey(CacheMode mode, Object ...identifier) {
		String key = createGeneralKey(mode, identifier);
		caches.Common_Cache.invalidate(key);
	}

	public boolean setTempUser(String uuId) {
		if(caches.Temp_Users_Cache.getIfPresent(uuId) != null){
			/**
			 * 几乎不可能的情况 此刻不能覆盖已有用户
			 */
			return false;
		}

		caches.Temp_Users_Cache.put(uuId,new ConcurrentHashMap<>());
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

	public void setTempUserMap(String uuId, String key, String val) {
		Map<String, String> tempUser = caches.Temp_Users_Cache.getIfPresent(uuId);

		if(tempUser == null)
			throw new NoSuchElement();

		tempUser.put(key,val);
	}

	public boolean existsForTempUser(String uuId) {
		return caches.Temp_Users_Cache.getIfPresent(uuId) != null;
	}

	public void removeTempUser(String uuId) {
		if(uuId == null) {
			/*
			* 在无痕模式登录的时候 UUID 因为某种原因为空 此时也相当于没有
			* 啥也不做 无伤大雅
			* */
			return;
		}
		caches.Temp_Users_Cache.invalidate(uuId);
	}

	private static String generateCacheIdInUserIsolation(long loginId, String id){
		return loginId+"#"+id;
	}


}
