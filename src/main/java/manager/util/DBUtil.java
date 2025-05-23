package manager.util;



import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TypedQuery;
import manager.data.general.FinalHandler;
import manager.data.general.FinalIntegerTempStorageCalculator;
import manager.system.SelfX;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.entity.SMEntity;
import manager.entity.general.SMGeneralEntity;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.system.SelfXErrors;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

/**
 * 数据库默认时间 : "1970-01-01 08:00:00"（和Java保持一致）
 * 
 * TODO 待设计灵活的事务支持
 * 
 * @author 王天戈
 *
 */
public abstract class DBUtil {

	/*这个 val 竟然可以直接用枚举 我也不知道咋了。。*/
	public static <T> T selectUniqueExistedEntityByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws DBException {
		try {
			return selectUniqueEntityByField(cla, fieldName, val, hbFactory);
		} catch (NoSuchElement e) {
			e.printStackTrace();
			assert false;
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, fieldName + ":" + val);
		}
	}
	
	/*TODO 或许可以使用limit 1 优化？*/
	public static <T> T selectUniqueEntityByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws NoSuchElement, DBException {
		List<T> rlt = selectEntitiesByField(cla, fieldName, val, hbFactory);

		if (rlt.isEmpty())
			throw new NoSuchElement();

		if (rlt.size() == 1)
			return rlt.get(0);

		throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, fieldName + ":" + val + ":" + rlt.size());
	}
	
	public static <T> T selectUniqueEntityByBiFields(Class<T> cla, String field1Name, Object val1,String field2Name,Object val2,
			SessionFactory hbFactory) throws NoSuchElement, DBException {
		List<T> rlt = selectEntitiesByBiFields(cla, field1Name, val1, field2Name, val2, hbFactory);

		if (rlt.isEmpty())
			throw new NoSuchElement();

		if (rlt.size() == 1)
			return rlt.get(0);

		throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, field1Name + ":" + val1 + ":" + rlt.size());
	}
	
	
	public static<T> List<T> selectEntitiesByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromStatelessSession((session)->{

				String tableName = CommonUtil.getEntityTableName(cla);
				String hql = String.format("FROM %s WHERE %s=:val", transTableToEntity(tableName), transFieldToAttr(fieldName));
				TypedQuery<T> query = session.createQuery(hql, cla);
				query.setParameter("val", val);
				return query.getResultList();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	/**
	 *
	 * @param theManyValsTranslator 假设是long类型 直接转化成字符串  字符串类型转化自己加双引号
	 */
	public static<T,E> List<T> selectEntitiesByManyField(Class<T> cla,
													 String theManyField, List<E> theManyVals,Function<E,Object> theManyValsTranslator,
													 SessionFactory hbFactory) throws DBException {
		if(theManyVals.isEmpty()) {
			return new ArrayList<>();
		}

		return hbFactory.fromSession(session -> {
			try{
				String tableName = CommonUtil.getEntityTableName(cla);
				String theManySql = theManyVals.stream().map(val -> theManyValsTranslator.apply(val).toString()).collect(Collectors.joining(","));
				String hql = String.format("FROM %s WHERE (%s in (%s))", transTableToEntity(tableName), transFieldToAttr(theManyField),theManySql);
				TypedQuery<T> query =
						session.createQuery(hql, cla);
				return query.getResultList();
			}catch (Exception e){
				throw processDBException(e);
			}
		});
	}

	public static<T,E> List<T> selectEntitiesByFieldAndManyField(Class<T> cla, String theOneField, Object theOneVal,
			String theManyField, List<E> theManyVals,Function<E,Object> theManyValsTranslator,
			SessionFactory hbFactory) throws DBException {
		if(theManyVals.isEmpty()) {
			return new ArrayList<>();
		}

		return hbFactory.fromSession(session -> {
			try{
				String tableName = CommonUtil.getEntityTableName(cla);
				String theManySql = theManyVals.stream().map(val -> theManyValsTranslator.apply(val).toString()).collect(Collectors.joining(","));
				String hql = String.format("FROM %s WHERE %s=:val and (%s in (%s))", transTableToEntity(tableName), transFieldToAttr(theOneField),transFieldToAttr(theManyField),theManySql);
				TypedQuery<T> query =
						session.createQuery(hql, cla).setParameter("val", theOneVal);
				return query.getResultList();
			}catch (Exception e){
				throw processDBException(e);
			}
		});
	}

	private static void fillTermsSQL(StringBuffer additionalParams , Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan,FinalIntegerTempStorageCalculator storageCalculator){
		if(equals != null){
			equals.forEach((key,val)->{
				if(val == null){
					additionalParams.append(String.format(" AND %s is NULL",transFieldToAttr(key)));
				}else{
					additionalParams.append(String.format(" AND %s=:%s",transFieldToAttr(key),storageCalculator.getAndIncrementHandlerAndStorage(val)));
				}
			});
		}

		if(likes != null){
			likes.forEach((key,val)->{
				additionalParams.append(String.format(" AND %s LIKE :%s",transFieldToAttr(key),storageCalculator.getAndIncrementHandlerAndStorage("%"+val+"%")));
			});
		}

		if(greaterThan != null){
			greaterThan.forEach((key,val)->{
				additionalParams.append(String.format(" AND %s >= :%s",transFieldToAttr(key),storageCalculator.getAndIncrementHandlerAndStorage(val)));
			});
		}

		if(lessThan != null){
			lessThan.forEach((key,val)->{
				additionalParams.append(String.format(" AND %s <= :%s",transFieldToAttr(key),storageCalculator.getAndIncrementHandlerAndStorage(val)));
			});
		}
	}
	public static<T> List<T> selectEntitiesByTerms(Class<T> cla, Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan,
												   SessionFactory hbFactory){
		String tableName = CommonUtil.getEntityTableName(cla);

		StringBuffer additionalParams = new StringBuffer();

		FinalIntegerTempStorageCalculator storageCalculator = new FinalIntegerTempStorageCalculator(1,"val");

		fillTermsSQL(additionalParams,likes,equals,greaterThan,lessThan,storageCalculator);

		String hql = String.format("FROM %s WHERE 1=1 "+additionalParams.toString(), transTableToEntity(tableName));

		return hbFactory.fromStatelessSession(session -> {
			try {
				FinalHandler<Query<T>> handler = new FinalHandler<>();
				handler.val = session.createQuery(hql, cla);

				storageCalculator.consumerValues((key,val)->{
					handler.val =handler.val.setParameter(key,val);
				});

				return handler.val.setMaxResults(SelfX.MAX_DB_LINES_IN_ONE_SELECTS).getResultList();
			}catch (Exception e) {
				throw processDBException(e);
			}
		});
	}


	public static<T> long countEntitiesByTerms(Class<T> cla, Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan,
												   SessionFactory hbFactory){
		String tableName = CommonUtil.getEntityTableName(cla);

		StringBuffer additionalParams = new StringBuffer();

		FinalIntegerTempStorageCalculator storageCalculator = new FinalIntegerTempStorageCalculator(1,"val");

		fillTermsSQL(additionalParams,likes,equals,greaterThan,lessThan,storageCalculator);

		String hql = String.format("SELECT COUNT(*) FROM %s WHERE 1=1 "+additionalParams.toString(), transTableToEntity(tableName));
		try {
			return hbFactory.fromStatelessSession(session -> {

					FinalHandler<Query<Long>> handler = new FinalHandler<>();
					handler.val = session.createQuery(hql, Long.class);

					storageCalculator.consumerValues((key,val)->{
						handler.val =handler.val.setParameter(key,val);
					});

					return handler.val.uniqueResult();

			});
		}catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static<T> List<T> selectEntitiesByRange(Class<T> cla, String rangeFieldName,Long minBorder,Long maxBorder,
															   Map<String,Object> additionalFilter,
															   SessionFactory hbFactory){
		String tableName = CommonUtil.getEntityTableName(cla);

		StringBuffer additionalParams = new StringBuffer();

		FinalIntegerTempStorageCalculator storageCalculator = new FinalIntegerTempStorageCalculator(3,"val");
		if(additionalFilter != null){
			additionalFilter.forEach((key,val)->{
				additionalParams.append(String.format(" AND %s=:%s",transFieldToAttr(key),storageCalculator.getAndIncrementHandlerAndStorage(val)));
			});
		}

		String hql = String.format("FROM %s WHERE %s>=:val1 and %s<=:val2 "+additionalParams.toString(), transTableToEntity(tableName)
				,transFieldToAttr(rangeFieldName)
				,transFieldToAttr(rangeFieldName));
		try {
			return hbFactory.fromStatelessSession(session -> {

					FinalHandler<Query<T>> handler = new FinalHandler<>();
					handler.val = session.createQuery(hql, cla)
							.setParameter("val1", minBorder)
							.setParameter("val2", maxBorder);

					storageCalculator.consumerValues((key,val)->{
						handler.val =handler.val.setParameter(key,val);
					});
					return handler.val.getResultList();
			});
		}catch (Exception e) {
			throw processDBException(e);
		}
	}

	/*闭区间*/
	@Deprecated
	public static<T> List<T> selectEntitiesByTimeScopeAndField(Class<T> cla, String timeFieldName,Calendar startTime,Calendar endTime,
			String field2Name, Object field2val,
			SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			String hql = String.format("FROM %s WHERE %s>=:val1 and %s<=:val2 and %s=:val3", transTableToEntity(tableName), transFieldToAttr(timeFieldName),transFieldToAttr(timeFieldName),transFieldToAttr(field2Name));
			TypedQuery<T> query = session.createQuery(hql, cla);
			query.setParameter("val1", startTime);
			query.setParameter("val2", endTime);
			query.setParameter("val3", field2val);
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}



	public static<T> List<T> selectEntitiesByBiFields(Class<T> cla, String field1Name, Object field1val,
			String field2Name, Object field2val,
			SessionFactory hbFactory) throws DBException {

		String tableName = CommonUtil.getEntityTableName(cla);
		try {
			return hbFactory.fromStatelessSession(session -> {
				String hql = String.format("FROM %s WHERE %s=:val1 and %s=:val2", transTableToEntity(tableName), transFieldToAttr(field1Name),transFieldToAttr(field2Name));
				return session.createQuery(hql, cla)
						.setParameter("val1", field1val )
						.setParameter("val2", field2val)
						.getResultList();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}

	}


	public static <T> List<T> selectAllEntities(Class<T> cla,SessionFactory hbFactory) throws DBException{
		String tableName = CommonUtil.getEntityTableName(cla);

		return hbFactory.fromStatelessSession(session -> {
			try{
				String hql = String.format("FROM %s ", transTableToEntity(tableName));
				TypedQuery<T> query = session.createQuery(hql, cla);
				return query.getResultList();
			}catch (Exception e){
				throw processDBException(e);
			}
		});
	}

	public static<T> long countEntitiesByBiFields(Class<T> cla, String field1, Object val1,
			String field2, Object val2,
			SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		return countByBiFields(tableName, field1, val1, field2, val2, hbFactory);
	}


	public static<T> long countEntitiesByTriFields(Class<T> cla,
												   String field1, Object val1,
												   String field2, Object val2,
												   String field3, Object val3,
												  SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		return countByTriFields(tableName, field1, val1, field2, val2,field3,val3,hbFactory);
	}


	/*闭区间*/
	public static<T> long countEntitiesByRange(Class<T> cla, String field, Object min,Object max,
			SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		try {
			return hbFactory.fromStatelessSession(session->{

					String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s>=? and %s<=?", tableName, field,field);
					return session.createQuery(sql, Long.class)
						.setParameter(1, min)
						.setParameter(2, max)
						.getSingleResult();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}


	public static<T> long countEntitiesByField(Class<T> cla, String field1, Object val1,SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		return countByField(tableName, field1, val1, hbFactory);
	}

	public static long countAllEntities(Class<? extends SMEntity> cla,SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		try {
			return hbFactory.fromStatelessSession((session)->{

					String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
					return session.createQuery(sql, Long.class)
						.getSingleResult();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static long  countByTriFields(String tableName
			, String field1, Object val1
			,String field2,Object val2
			,String field3,Object val3
			,SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromStatelessSession(session->{
					String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s=? and %s=? and %s=?"
							, tableName, field1,field2,field3);
					return session.createNativeQuery(sql, Long.class)
							.setParameter(1, val1)
							.setParameter(2, val2)
							.setParameter(3, val3)
							.uniqueResult();
			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}


	public static long  countByBiFields(String tableName, String field1, Object val1,String field2,Object val2,SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromStatelessSession(session->{

					String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s=? and %s=?"
							, tableName, field1,field2);
					return session.createNativeQuery(sql, Long.class)
							.setParameter(1, val1)
							.setParameter(2, val2)
							.uniqueResult();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static long countByField(String tableName, String fieldName, Object val,SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromStatelessSession(session->{

					String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s=?", tableName, fieldName);
					return session.createNativeQuery(sql, Long.class)
							.setParameter(1, val)
							.getSingleResult();
			});
		} catch (Exception e) {
			throw  processDBException(e);
		}
	}

	public static<T extends SMGeneralEntity> long insertEntity(T one,SessionFactory hbFactory) throws DBException {
		one.setCreateUtc(System.currentTimeMillis());
		one.setUpdateUtc(System.currentTimeMillis());
		try{
			hbFactory.inTransaction(session ->
				session.persist(one)
			);
		}catch (Exception e){
			throw processDBException(e);
		}
		return one.getId();
	}
	public static<T extends SMGeneralEntity> T selectExistedEntity(long id,Class<T> cla,SessionFactory hbFactory) throws DBException {
		try {
			return selectEntity(id,cla,hbFactory);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}
	public static<T extends SMGeneralEntity> T selectEntity(long id,Class<T> cla,SessionFactory hbFactory) throws NoSuchElement {
		try {
			T rlt = hbFactory.fromStatelessSession((session)->
					 session.get(cla, id)
			);
			if (rlt == null)
				throw new NoSuchElement();
			return rlt;
		} catch (Exception e) {
			throw processDBException(e);
		}

	}

	public static<T> T executeSqlAndGetUniqueResult(SessionFactory sessionFactory, String sql,Class<T> cla) {
		T result = null;
		try (Session session = sessionFactory.openSession()) {
			// 创建原生SQL查询
			NativeQuery<T> query = session.createNativeQuery(sql, cla);
			// 执行查询并获取唯一结果
			return query.uniqueResult();
		} catch (Exception e) {
			throw processDBException(e);
		}
	}
	public static<T extends SMGeneralEntity> void updateExistedEntity(T one,SessionFactory hbFactory) throws DBException {
		one.setUpdateUtc(System.currentTimeMillis());
		assert one.getId() != 0;
		try {
			hbFactory.inTransaction(session -> {
					session.merge(one);
			});
		} catch (OptimisticLockException e) {
			throw new DBException(SelfXErrors.DB_SYNC_ERROR);
		} catch (Exception e) {
			throw processDBException(e);
		}

	}
	public static DBException processDBException(Exception e){
		e.printStackTrace();
		try {
			detectDataTooLongExceptionAndThrowIfAny(e);
		}catch(DBException dataTooLong) {
			assert dataTooLong.type == SelfXErrors.DATA_TOO_LONG : dataTooLong.type;
			return dataTooLong;
		}
		return new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
	}
	public static DBException processDBException(Transaction trans,Session session,Exception e){
		try {
			e.printStackTrace();
			/*回滚一下*/
			if (trans != null)
				trans.rollback();
			try {
				detectDataTooLongExceptionAndThrowIfAny(e);
			}catch(DBException dataTooLong) {
				assert dataTooLong.type == SelfXErrors.DATA_TOO_LONG : dataTooLong.type;
				return dataTooLong;
			}

			return new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}finally {
			/*trans.rollback() 可能出现异常*/
			if (session != null) {
				session.close();
			}
		}

	}


	private static void detectDataTooLongExceptionAndThrowIfAny(Exception e) throws DBException{
		Throwable cur = e.getCause();
		while(cur != null) {
			if(cur.getMessage().contains("Data too long for column")) {
				throw new DBException(SelfXErrors.DATA_TOO_LONG);
			}
			cur = cur.getCause();
		}
	}




	public static<T> boolean includeEntitiesByField(Class<T> entityCla, String fieldName, Object val, SessionFactory hbFactory)
			throws DBException {
		try {
			long count = countEntitiesByField(entityCla, fieldName, val, hbFactory);
			if (count == 0)
				return false;

			return true;
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}

	public static<T> boolean includeUniqueEntityByField(Class<T> entityCla, String fieldName, Object val, SessionFactory hbFactory)
			throws DBException {
		try {
			long count = countEntitiesByField(entityCla, fieldName, val, hbFactory);
			if (count == 0)
				return false;
			if (count == 1)
				return true;
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, count);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}

	public static<T> boolean includeUniqueEntityByBiFields(Class<T> entityCla, String field1, Object val1,String field2,Object val2,SessionFactory hbFactory)
			throws DBException {
		try {
			long count = countEntitiesByBiFields(entityCla, field1, val1, field2, val2, hbFactory);
			if (count == 0)
				return false;
			if (count == 1)
				return true;
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, count);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			throw new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}

	public static<T> boolean includeUniqueEntityByTriFields(Class<T> entityCla
			, String field1, Object val1
			,String field2,Object val2
			,String field3,Object val3
			,SessionFactory hbFactory){
		try {
			long count = countEntitiesByTriFields(entityCla
					, field1, val1
					, field2, val2
					, field3, val3
					, hbFactory);
			if (count == 0)
				return false;
			if (count == 1)
				return true;
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR, count);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			throw new DBException(SelfXErrors.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}

	public static String transTableToEntity(String tableName) {
		return lineToHump(tableName, true);
	}

	public static String transFieldToAttr(String filed) {
		return lineToHump(filed, false);
	}

	/* 命名方式 下划线 转换为 驼峰 用来只需要配置表命 就可以 跟HQL适应 */
	public static String lineToHump(String str, boolean withFirstCharUpperCase) {
		if (str.isEmpty()) {
			assert false;
			return str;
		}
		Pattern linePattern = Pattern.compile("_(\\w)");
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);

		if (withFirstCharUpperCase) {
			assert sb.length() > 0;
			char c = sb.charAt(0);
			if (c >= 'a' && c <= 'z') {
				c -= 32;
				sb.setCharAt(0, c);
			}
		}

		return sb.toString();
	}

	/**
	 * GeneralRelationTable:应当是三列 id,xxId,yyId
	 *
	 * @param argIds1 对应xxId
	 * @param argId2  对应yyId
	 * @author 王天戈
	 * @throws DBException
	 */
	public static void insertGeneralRTableData(String tableName, List<Long> argIds1, long argId2, SessionFactory hbFactory) throws DBException {
		if (argIds1.isEmpty())
			return;
		try {
			hbFactory.inTransaction((one)->{
				String rSql = argIds1.stream().map(argId1 ->
					"(null," + argId1 + "," + argId2 + ")"
				).collect(Collectors.joining(","));
				one.createNativeQuery("INSERT INTO "+tableName+" values "+rSql,Object.class)
						.executeUpdate();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static<T extends SMEntity> int deleteEntitiesByField(Class<T> cla, String field,Object val,SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromTransaction(session ->{

					return session.createNativeQuery("DELETE FROM "+CommonUtil.getEntityTableName(cla)+" WHERE "+field+"=?  ", Long.class)
									.setParameter(1, val)
									.executeUpdate();

			});
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static<T extends SMEntity> void deleteEntity(Class<T> cla, long id,SessionFactory hbFactory) throws DBException {
		long deleteRows = deleteEntitiesByField(cla, DBConstants.F_ID, id, hbFactory);
		if(deleteRows == 0) {
			assert false;
		}
	}

	/**
	 *  tableName, fieldForTheMany,
	 * 						theManySql, fieldForTheOne, theOneId);
	 */
	public static void deleteGeneralRTableData(String tableName, String fieldForTheMany, String fieldForTheOne,
			List<Long> theManyIds, long theOneId, SessionFactory hbFactory) throws DBException {
		if(theManyIds.isEmpty()) {
			return ;
		}

		String theManySql = theManyIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		try {
			hbFactory.fromTransaction(session -> session.createNativeQuery("DELETE FROM "+tableName+" WHERE (? in (?)) and ?=?  ", Long.class)
				.setParameter(1, fieldForTheMany)
				.setParameter(2, theManySql)
				.setParameter(3, fieldForTheOne)
				.setParameter(4, theOneId)
				.executeUpdate());
		} catch (Exception e) {
			throw processDBException(e);
		}
	}

	public static List<Long> selectGeneralRTableData(String tableName, String fieldForTheMany, String fieldForTheOne,
			long theOneId, SessionFactory hbFactory) throws DBException {
		try {
			return hbFactory.fromStatelessSession(session->{
					return session.createNativeQuery("SELECT "+fieldForTheMany+" FROM "+tableName+" WHERE "+fieldForTheOne+" = ?  ", Long.class)
							.setParameter(1,theOneId)
							.getResultList();
				}
			);
		} catch (Exception e) {
			throw processDBException(e);
		}
	}
	
	public static List<Integer> selectGeneralRTableDataInInt(String tableName, String fieldForTheMany, String fieldForTheOne,
			long theOneId, SessionFactory hbFactory) throws DBException {
		return selectGeneralRTableData(tableName, fieldForTheMany, fieldForTheOne, theOneId, hbFactory).stream().map(l->(int)l.longValue()).collect(Collectors.toList());
	}
}
