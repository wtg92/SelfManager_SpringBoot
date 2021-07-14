package manager.util;


import static manager.system.SM.logger;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import manager.entity.SMEntity;
import manager.entity.general.SMGeneralEntity;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.system.SMError;

/**
 * 数据库默认时间 : "1970-01-01 08:00:00"（和Java保持一致）
 * 
 * TODO 待设计灵活的事务支持
 * 
 * @author 王天戈
 *
 */
public abstract class DBUtil {
	private static final SessionFactory hibernateSessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {

		try {
			final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure("hb.cfg.xml");
			builder.applySetting("password",
					CommonUtil.getValFromPropertiesFileInResource("mysql_pwd"));
			
			
			final StandardServiceRegistry registry = builder.build();

			return new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
			throw new RuntimeException("There was an error building the factory...!");
		}

	}

	public static SessionFactory getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	/*这个 val 竟然可以直接用枚举 我也不知道咋了。。*/
	public static <T> T selectUniqueExistedEntityByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws DBException {
		try {
			return selectUniqueEntityByField(cla, fieldName, val, hbFactory);
		} catch (NoSuchElement e) {
			e.printStackTrace();
			assert false;
			throw new DBException(SMError.INCONSISTANT_DB_ERROR, fieldName + ":" + val);
		}
	}
	
	/*TODO 或许可以使用limit 1 优化？*/
	public static <T> T selectUniqueEntityByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws NoSuchElement, DBException {
		List<T> rlt = selectEntitiesByField(cla, fieldName, val, hbFactory);

		if (rlt.size() == 0)
			throw new NoSuchElement();

		if (rlt.size() == 1)
			return rlt.get(0);

		throw new DBException(SMError.INCONSISTANT_DB_ERROR, fieldName + ":" + val + ":" + rlt.size());
	}
	
	public static <T> T selectUniqueEntityByBiFields(Class<T> cla, String field1Name, Object val1,String field2Name,Object val2,
			SessionFactory hbFactory) throws NoSuchElement, DBException {
		List<T> rlt = selectEntitiesByBiFields(cla, field1Name, val1, field2Name, val2, hbFactory);

		if (rlt.size() == 0)
			throw new NoSuchElement();

		if (rlt.size() == 1)
			return rlt.get(0);

		throw new DBException(SMError.INCONSISTANT_DB_ERROR, field1Name + ":" + val1 + ":" + rlt.size());
	}
	
	
	public static<T> List<T> selectEntitiesByField(Class<T> cla, String fieldName, Object val,
			SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			String hql = String.format("FROM %s WHERE %s=:val", transTableToEntity(tableName), transFieldToAttr(fieldName));
			TypedQuery<T> query = session.createQuery(hql, cla);
			query.setParameter("val", CommonUtil.pretreatForString(val));
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	/**
	 *  ！！！theManyVals 只适合Int类型的，假如非int类型，SQL应该就错了
	 */
	public static<T,E> List<T> selectEntitiesByFieldAndManyField(Class<T> cla, String theOneField, Object theOneVal,
			String theManyField, List<E> theManyVals,Function<E,Object> theManyValsTranslator,
			SessionFactory hbFactory) throws DBException {
		if(theManyVals.size() == 0) {
			return new ArrayList<>();
		}
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			
			String theManySql = theManyVals.stream().map(val -> theManyValsTranslator.apply(val).toString()).collect(Collectors.joining(","));
			String hql = String.format("FROM %s WHERE %s=:val and (%s in (%s))", transTableToEntity(tableName), transFieldToAttr(theOneField),transFieldToAttr(theManyField),theManySql);
			TypedQuery<T> query = session.createQuery(hql, cla);
			query.setParameter("val", CommonUtil.pretreatForString(theOneVal));
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	/**
	 *  ！！！theManyVals 只适合Int类型的，假如非int类型，SQL应该就错了
	 */
	public static<T,E> List<T> selectEntitiesByManyField(Class<T> cla,String theManyField, List<E> theManyVals,Function<E,Object> theManyValsTranslator,
			SessionFactory hbFactory) throws DBException {
		if(theManyVals.size() == 0) {
			return new ArrayList<>();
		}
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			
			String theManySql = theManyVals.stream().map(val -> theManyValsTranslator.apply(val).toString()).collect(Collectors.joining(","));
			String hql = String.format("FROM %s WHERE (%s in (%s))", transTableToEntity(tableName),transFieldToAttr(theManyField),theManySql);
			TypedQuery<T> query = session.createQuery(hql, cla);
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	public static<T> List<T> selectEntitiesByDateScopeAndField(Class<T> cla, String dateFieldName,Calendar startDate,Calendar endDate,
			String field2Name, Object field2val,
			SessionFactory hbFactory) throws DBException {
		Calendar minTimeOfstartDate = TimeUtil.getMinTimeOfDay(startDate);
	    Calendar maxTimeOfendDate = TimeUtil.getMaxTimeOfDay(endDate);
		return selectEntitiesByTimeScopeAndField(cla, dateFieldName, minTimeOfstartDate, maxTimeOfendDate, field2Name, field2val, hbFactory);
	}
	
	/*闭区间*/
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
			query.setParameter("val3", CommonUtil.pretreatForString(field2val));
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	
	
	public static<T> List<T> selectEntitiesByBiFields(Class<T> cla, String field1Name, Object field1val,
			String field2Name, Object field2val,
			SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			String hql = String.format("FROM %s WHERE %s=:val1 and %s=:val2", transTableToEntity(tableName), transFieldToAttr(field1Name),transFieldToAttr(field2Name));
			TypedQuery<T> query = session.createQuery(hql, cla);
			query.setParameter("val1", CommonUtil.pretreatForString(field1val) );
			query.setParameter("val2", CommonUtil.pretreatForString(field2val));
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	
	public static <T> List<T> selectAllEntities(Class<T> cla,SessionFactory hbFactory) throws DBException{
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			String hql = String.format("FROM %s ", transTableToEntity(tableName));
			TypedQuery<T> query = session.createQuery(hql, cla);
			List<T> entities = query.getResultList();
			trans.commit();
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	public static<T> long countEntitiesByBiFields(Class<T> cla, String field1, Object val1,
			String field2, Object val2,
			SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		return countByBiFields(tableName, field1, val1, field2, val2, hbFactory);
	}
	
	/*闭区间*/
	public static<T> long countEntitiesByRange(Class<T> cla, String field, Object min,Object max,
			SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		try {
			Session session = hbFactory.getCurrentSession();
			Transaction trans = session.beginTransaction();
			String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s>=? and %s<=?", tableName, field,field);
			@SuppressWarnings("rawtypes")
			List rlt = session.createSQLQuery(sql).setParameter(1, CommonUtil.pretreatForString(min)).setParameter(2, CommonUtil.pretreatForString(max)).getResultList();
			trans.commit();
			assert rlt.size() == 1;
			BigInteger r =  (BigInteger)rlt.get(0);
			return r.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
		
	}
	
	
	public static<T> long countEntitiesByField(Class<T> cla, String field1, Object val1,SessionFactory hbFactory) throws DBException {
		String tableName = CommonUtil.getEntityTableName(cla);
		return countByField(tableName, field1, val1, hbFactory);
	}
	
	
	public static long countAllEntities(Class<? extends SMEntity> cla,SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			String tableName = CommonUtil.getEntityTableName(cla);
			Transaction trans = session.beginTransaction();
			String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
			@SuppressWarnings("rawtypes")
			List rlt = session.createSQLQuery(sql).getResultList();
			trans.commit();
			assert rlt.size() == 1;
			BigInteger r =  (BigInteger)rlt.get(0);
			return r.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	

	
	public static long countByBiFields(String tableName, String field1, Object val1,String field2,Object val2,SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			Transaction trans = session.beginTransaction();
			String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s=? and %s=?", tableName, field1,field2);
			@SuppressWarnings("rawtypes")
			List rlt = session.createSQLQuery(sql).setParameter(1, CommonUtil.pretreatForString(val1)).setParameter(2, CommonUtil.pretreatForString(val2)).getResultList();
			trans.commit();
			assert rlt.size() == 1;
			BigInteger r =  (BigInteger)rlt.get(0);
			return r.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}
	}
	
	public static long countByField(String tableName, String fieldName, Object val,SessionFactory hbFactory) throws DBException {
		try {
			Session session = hbFactory.getCurrentSession();
			Transaction trans = session.beginTransaction();
			String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s=?", tableName, fieldName);
			@SuppressWarnings("rawtypes")
			List rlt = session.createSQLQuery(sql).setParameter(1, CommonUtil.pretreatForString(val)).getResultList();
			trans.commit();
			assert rlt.size() == 1;
			BigInteger r =  (BigInteger)rlt.get(0);
			return r.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
		}

	}
	public static<T extends SMGeneralEntity> int insertEntity(T one,SessionFactory hbFactory) throws DBException {
		one.setCreateTime(TimeUtil.getCurrentTime());
		one.setUpdateTime(TimeUtil.getCurrentTime());

		Transaction trans = null;
		Session session = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			Integer id = (Integer) session.save(one);
			trans.commit();
			return id;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	public static<T extends SMGeneralEntity> T selectEntity(int id,Class<T> cla,SessionFactory hbFactory) throws DBException, NoSuchElement {
		Transaction trans = null;
		Session session = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			T rlt = session.get(cla, id);
			trans.commit();
			if (rlt == null)
				throw new NoSuchElement();

			return rlt;
		} catch (NoSuchElement e) {
			throw e;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	
	public static<T extends SMGeneralEntity> void updateExistedEntity(T one,SessionFactory hbFactory) throws DBException {
		one.setUpdateTime(TimeUtil.getCurrentTime());
		assert one.getId() != 0;
		Transaction trans = null;
		Session session = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.update(one);
			trans.commit();
		} catch (OptimisticLockException e) {
			e.printStackTrace();
			if (trans != null) {
				trans.rollback();
			}
			throw new DBException(SMError.DB_SYNC_ERROR);
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	public static DBException processDBExcpetion(Transaction trans,Session session,Exception e){
		e.printStackTrace();
		/*回滚一下*/
		if (trans != null)
			trans.rollback();
		try {
			detectDataTooLongExceptionAndThrowIfAny(e);
		}catch(DBException dataTooLong) {
			assert dataTooLong.type == SMError.DATA_TOO_LONG : dataTooLong.type;
			return dataTooLong;
		}

		/*假如是由于DataTooLong导致的 就不必关闭session了 反正也没什么影响*/
		if (session != null)
			session.close();
		
		return new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
	}
	
	
	private static void detectDataTooLongExceptionAndThrowIfAny(Exception e) throws DBException{
		Throwable cur = e.getCause();
		while(cur != null) {
			if(cur.getMessage().contains("Data too long for column")) {
				throw new DBException(SMError.DATA_TOO_LONG);
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
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
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
			throw new DBException(SMError.INCONSISTANT_DB_ERROR, count);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
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
			throw new DBException(SMError.INCONSISTANT_DB_ERROR, count);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(SMError.UNKNOWN_DB_ERROR, e.getMessage());
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
		if (str.length() == 0) {
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
	public static void insertGeneralRTableData(String tableName, List<Integer> argIds1, int argId2, SessionFactory hbFactory) throws DBException {
		if (argIds1.size() == 0)
			return;
		
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String rSql = argIds1.stream().map(argId1 -> {
					String rlt = "(null," + argId1 + "," + argId2 + ")";
					return rlt;
				}).collect(Collectors.joining(","));
				String sql = String.format("INSERT INTO %s values %s", tableName, rSql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.executeUpdate();
				}
			});
			trans.commit();
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	private static void setPSParams(PreparedStatement ps,Object ...params) throws SQLException {
		for(int i=0;i<params.length;i++) {
			Object param = params[i];
			int indexForPS = i+1;
			if(param.getClass().equals(int.class) || param instanceof Integer) {
				ps.setInt(indexForPS, (int)param);
			}else if(param.getClass().equals(float.class) || param instanceof Float) {
				assert false : "shouldn't use float";
				ps.setFloat(indexForPS, (float)param);
			}else if(param.getClass().equals(double.class) || param instanceof Double) {
				ps.setDouble(indexForPS, (double)param);
			}else if(param.getClass().equals(boolean.class) || param instanceof Boolean) {
				ps.setBoolean(indexForPS, (boolean)param);
			}else {
				ps.setString(indexForPS, CommonUtil.pretreatForString(param).toString());
			}
		}
	}
	
	public static<T extends SMEntity> int deleteEntitiesByField(Class<T> cla, String field,Object val,SessionFactory hbFactory) throws DBException {
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			List<Integer> rlt = new ArrayList<Integer>();
			session.doWork(conn -> {
				String sql = String.format("DELETE FROM %s WHERE %s=?  ", CommonUtil.getEntityTableName(cla), field);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					setPSParams(ps, val);
					int deleteRows = ps.executeUpdate();
					rlt.add(deleteRows);
				}
			});
			trans.commit();
			assert rlt.size() == 1;
			return rlt.get(0);
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
		
		
	}
	
	public static<T extends SMEntity> void deleteEntity(Class<T> cla, int id,SessionFactory hbFactory) throws DBException {
		int deleteRows = deleteEntitiesByField(cla, SMDB.F_ID, id, hbFactory);
		if(deleteRows == 0) {
			logger.errorLog("delete by id zero"+ CommonUtil.getEntityTableName(cla)+":"+id);
			assert false;
		}
	}
	
	
	public static void deleteGeneralRTableData(String tableName, String fieldForTheMany, String fieldForTheOne,
			List<Integer> theManyIds, int theOneId, SessionFactory hbFactory) throws DBException {
		if(theManyIds.size() == 0) {
			return ;
		}
		
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = theManyIds.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(","));
				String sql = String.format("DELETE FROM %s WHERE (%s in (%s)) and %s=%s  ", tableName, fieldForTheMany,
						theManySql, fieldForTheOne, theOneId);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.executeUpdate();
				}
			});
			trans.commit();
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}

	}

	public static List<Integer> selectGeneralRTableData(String tableName, String fieldForTheMany, String fieldForTheOne,
			int theOneId, SessionFactory hbFactory) throws DBException {
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			List<Integer> rlt = new ArrayList<Integer>();
			session.doWork(conn->{
				String sql = String.format("SELECT %s FROM %s WHERE %s = ?  ",fieldForTheMany
						,tableName,fieldForTheOne);
				try(PreparedStatement ps = conn.prepareStatement(sql)){
					ps.setInt(1, theOneId);
					try(ResultSet rs = ps.executeQuery();){
						while(rs.next()) {
							rlt.add(rs.getInt(fieldForTheMany));
						}
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
		

	}
	

}
