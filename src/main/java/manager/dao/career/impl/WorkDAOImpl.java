package manager.dao.career.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;

import manager.dao.career.WorkDAO;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanBalance;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import static manager.util.DBUtil.*;

@Repository
public class WorkDAOImpl implements WorkDAO {
	@Resource
	private SessionFactory sessionFactory;
	@Override
	public long insertPlan(Plan plan) throws DBException {
		return insertEntity(plan, sessionFactory);
	}

	@Override
	public long insertWorkSheet(WorkSheet sheet) throws DBException {
		return insertEntity(sheet, sessionFactory);
	}
	
	@Override
	public long insertBalance(PlanBalance balance) throws DBException {
		return insertEntity(balance, sessionFactory);
	}

	@Override
	public Plan selectPlan(long id) throws NoSuchElement, DBException {
		return selectEntity(id,Plan.class, sessionFactory);
	}

	@Override
	public WorkSheet selectWorkSheet(long id) throws NoSuchElement, DBException {
		return selectEntity(id, WorkSheet.class, sessionFactory);
	}

	@Override
	public void updateExistedPlan(Plan existed) throws DBException {
		updateExistedEntity(existed, sessionFactory);
	}

	@Override
	public List<Plan> selectPlansByOwnerAndStates(long ownerId,List<PlanState> states) throws DBException {
		return selectEntitiesByFieldAndManyField(Plan.class, DBConstants.F_OWNER_ID,ownerId, DBConstants.F_STATE, states,PlanState::getDbCode, sessionFactory);
	}

	@Override
	public void deleteExistedPlan(long planId) throws DBException {
		deleteEntity(Plan.class, planId, sessionFactory);
	}

	@Override
	public List<Plan> selectPlansByField(String field, Object val) throws DBException {
		return selectEntitiesByField(Plan.class, field, val, sessionFactory);
	}

	@Override
	public boolean includeUniqueWorkSheetByOwnerAndDateAndTimezone(long ownerId,long date,String timezone) throws DBException {
		return includeUniqueEntityByTriFields(WorkSheet.class
				, DBConstants.F_OWNER_ID,ownerId
				, DBConstants.F_DATE_UTC,date
				, DBConstants.F_TIMEZONE,timezone
				, sessionFactory);
	}

	@Override
	public void updateExistedWorkSheet(WorkSheet ws) throws DBException {
		updateExistedEntity(ws, sessionFactory);
	}

	@Override
	public void deleteExistedWorkSheet(long wsId){
		deleteEntity(WorkSheet.class, wsId, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(long ownerId, long page,long limit) throws DBException {
		List<WorkSheet> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s=? ORDER BY %s DESC LIMIT %s,%s",
						DBConstants.F_ID, DBConstants.F_DATE_UTC, DBConstants.F_STATE, DBConstants.F_TIMEZONE,
						DBConstants.T_WORK_SHEET, DBConstants.F_OWNER_ID, DBConstants.F_DATE_UTC,(page*limit),limit);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setLong(1, ownerId);

					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						WorkSheet ws =new WorkSheet();
						ws.setId(rs.getLong(1));
						ws.setDateUtc(rs.getLong(2));
						ws.setState(WorkSheetState.valueOfDBCode(rs.getInt(3)));
						ws.setTimezone(rs.getString(4));
						rlt.add(ws);
					}
				}
			});
		});
		return rlt;
	}


	@Override
	public long countWorkSheetByDateAndTimezone(Long date, String timezone) {
		return countEntitiesByBiFields(WorkSheet.class, DBConstants.F_DATE_UTC,date
				, DBConstants.F_TIMEZONE,timezone,sessionFactory);
	}

	@Override
	public boolean includeWorkSheetByPlanId(long planId) throws DBException {
		return includeEntitiesByField(WorkSheet.class, DBConstants.F_PLAN_ID, planId, sessionFactory);
	}

	@Override
	public PlanBalance selectBalanceByOwner(long ownerId) throws NoSuchElement, DBException {
		return selectUniqueEntityByField(PlanBalance.class, DBConstants.F_OWNER_ID, ownerId, sessionFactory);
	}

	@Override
	public void updateExistedBalance(PlanBalance balance) throws DBException {
		updateExistedEntity(balance, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByField(String field, Object val) throws DBException {
		return selectEntitiesByField(WorkSheet.class, field, val, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByOwnerAndStates(long ownerId, List<WorkSheetState> states)
			throws DBException {
		return selectEntitiesByFieldAndManyField(WorkSheet.class, DBConstants.F_OWNER_ID,ownerId, DBConstants.F_STATE, states,WorkSheetState::getDbCode, sessionFactory);
	}
	
	@Override
	public List<String> selectNonNullPlanTagsByUser(long loginerId) throws DBException {
		List<String> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			try {
				session.doWork(conn -> {
					String sql = String.format("select %s From %s where %s=? and %s is not null and trim(%s) != ''",
							DBConstants.F_TAGS, DBConstants.T_PLAN,
							DBConstants.F_OWNER_ID, DBConstants.F_TAGS, DBConstants.F_TAGS);
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						ps.setLong(1, loginerId);
						ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							rlt.add(rs.getString(1));
						}
					}
				});
			} catch (Exception e) {
				throw processDBException(e);
			}
		});
		return rlt;
	}

	@Override
	public List<String> selectNonNullWorkSheetTagsByUser(long loginerId) throws DBException {
		List<String> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			session.doWork(conn -> {
				try {
					String sql = String.format("select %s From %s where %s=? and %s is not null and trim(%s) != ''",
							DBConstants.F_TAGS, DBConstants.T_WORK_SHEET,
							DBConstants.F_OWNER_ID, DBConstants.F_TAGS, DBConstants.F_TAGS);
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						ps.setLong(1, loginerId);
						ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							rlt.add(rs.getString(1));
						}
					}
				} catch (Exception e) {
					throw processDBException(e);
				}
			});
		});
		return rlt;
	}

	@Override
	public List<String> getDistinctWorksheetTimezones(long loginId) {
		List<String> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			session.doWork(conn -> {
				try {
					String sql = String.format("select distinct %s From %s where %s=?",
							DBConstants.F_TIMEZONE, DBConstants.T_WORK_SHEET,
							DBConstants.F_OWNER_ID);
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						ps.setLong(1, loginId);
						ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							rlt.add(rs.getString(1));
						}
					}
				} catch (Exception e) {
					throw processDBException(e);
				}
			});
		});
		return rlt;
	}




	@Override
	public List<Plan> selectPlanInfosByIds(List<Long> planIds) throws DBException {
		if(planIds.isEmpty()) {
			return new ArrayList<Plan>();
		}
		List<Plan> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			session.doWork(conn -> {
				try {
					String theManySql = planIds.stream().map(val -> val.toString()).collect(Collectors.joining(","));
					String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
							DBConstants.F_ID, DBConstants.F_NAME,
							DBConstants.T_PLAN, DBConstants.F_ID,theManySql);
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							Plan one =new Plan();
							one.setId(rs.getLong(1));
							one.setName(rs.getString(2));
							rlt.add(one);
						}
					}
				} catch (Exception e) {
					throw processDBException(e);
				}
			});
		});
		return rlt;
	}

	@Override
	public long countWorkSheetByOwnerAndPlanId(long ownerId, long planId) throws DBException {
		return countEntitiesByBiFields(WorkSheet.class, DBConstants.F_OWNER_ID,ownerId, DBConstants.F_PLAN_ID,planId, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScopeAndTimezone(long loginId, long startDate, long endDate, String timezone) {
		Map<String,Object> params = new HashMap<>();
		params.put(DBConstants.F_OWNER_ID,loginId);
		params.put(DBConstants.F_TIMEZONE,timezone);
		return selectEntitiesByRange(WorkSheet.class, DBConstants.F_DATE_UTC,startDate,endDate,params,sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(long loginId, long startDate, long endDate) {
		Map<String,Object> params = new HashMap<>();
		params.put(DBConstants.F_OWNER_ID,loginId);
		return selectEntitiesByRange(WorkSheet.class, DBConstants.F_DATE_UTC,startDate,endDate,params,sessionFactory);
	}

	@Override
	public List<Plan> selectPlansByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan) {
		return selectEntitiesByTerms(Plan.class,likes,equals,greaterThan,lessThan,sessionFactory);
	}

	@Override
	public long countPlansByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan) {
		return countEntitiesByTerms(Plan.class,likes,equals,greaterThan,lessThan,sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorksheetsByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan) {
		return selectEntitiesByTerms(WorkSheet.class,likes,equals,greaterThan,lessThan,sessionFactory);
	}

	@Override
	public long countWorksheetsByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan) {
		return countEntitiesByTerms(WorkSheet.class,likes,equals,greaterThan,lessThan,sessionFactory);
	}


}
