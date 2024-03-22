package manager.dao.career.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.dao.career.WorkDAO;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanDept;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;
import manager.util.TimeUtil;
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
	public long insertPlanDept(PlanDept dept) throws DBException {
		return insertEntity(dept, sessionFactory);
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
		return selectEntitiesByFieldAndManyField(Plan.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE, states,PlanState::getDbCode, sessionFactory);
	}

	@Override
	public void deleteExistedPlan(long planId) throws DBException {
		deleteEntity(Plan.class, planId, sessionFactory);
	}
	
	@Override
	public long countPlansByOwnerAndState(long ownerId, PlanState state) throws DBException {
		return countEntitiesByBiFields(Plan.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE,state.getDbCode(), sessionFactory);
	}

	@Override
	public long countWorkSheetByOwnerAndState(long ownerId, WorkSheetState state) throws DBException {
		return countEntitiesByBiFields(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE,state.getDbCode(), sessionFactory);
	}
	
	@Override
	public List<Plan> selectPlansByField(String field, Object val) throws DBException {
		return selectEntitiesByField(Plan.class, field, val, sessionFactory);
	}

	@Override
	public boolean includeUniqueWorkSheetByOwnerAndDateAndTimezone(long ownerId,long date,String timezone) throws DBException {
		return includeUniqueEntityByTriFields(WorkSheet.class
				,SMDB.F_OWNER_ID,ownerId
				,SMDB.F_DATE_UTC,date
				,SMDB.F_TIMEZONE,timezone
				, sessionFactory);
	}

	@Override
	public void updateExistedWorkSheet(WorkSheet ws) throws DBException {
		updateExistedEntity(ws, sessionFactory);
	}

	@Override
	public void deleteExistedWorkSheet(long wsId) throws DBException {
		deleteEntity(WorkSheet.class, wsId, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(long ownerId, long page,long limit) throws DBException {
		List<WorkSheet> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s=? ORDER BY %s DESC LIMIT %s,%s",
						SMDB.F_ID,SMDB.F_DATE_UTC,SMDB.F_STATE,SMDB.F_TIMEZONE,
						SMDB.T_WORK_SHEET, SMDB.F_OWNER_ID,SMDB.F_DATE_UTC,(page*limit),limit);
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
	public long countWorkSheetByDate(Calendar date) throws DBException {
		return countEntitiesByRange(WorkSheet.class, SMDB.F_DATE, TimeUtil.getMinTimeOfDay(date), TimeUtil.getMaxTimeOfDay(date), sessionFactory);
	}

	@Override
	public long countWorkSheetByDateAndTimezone(Long date, String timezone) {
		return countEntitiesByBiFields(WorkSheet.class,SMDB.F_DATE_UTC,date
				,SMDB.F_TIMEZONE,timezone,sessionFactory);
	}

	@Override
	public boolean includeWorkSheetByPlanId(long planId) throws DBException {
		return includeEntitiesByField(WorkSheet.class, SMDB.F_PLAN_ID, planId, sessionFactory);
	}

	@Override
	public PlanDept selectPlanDeptByOwner(long ownerId) throws NoSuchElement, DBException {
		return selectUniqueEntityByField(PlanDept.class, SMDB.F_OWNER_ID, ownerId, sessionFactory);
	}

	@Override
	public void updateExistedPlanDept(PlanDept dept) throws DBException {
		updateExistedEntity(dept, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByField(String field, Object val) throws DBException {
		return selectEntitiesByField(WorkSheet.class, field, val, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByOwnerAndStates(long ownerId, List<WorkSheetState> states)
			throws DBException {
		return selectEntitiesByFieldAndManyField(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE, states,WorkSheetState::getDbCode, sessionFactory);
	}
	
	@Override
	public List<String> selectNonNullPlanTagsByUser(long loginerId) throws DBException {
		List<String> rlt = new ArrayList<>();
		sessionFactory.inStatelessSession(session->{
			try {
				session.doWork(conn -> {
					String sql = String.format("select %s From %s where %s=? and %s is not null and trim(%s) != ''",
							SMDB.F_TAGS,SMDB.T_PLAN,
							SMDB.F_OWNER_ID,SMDB.F_TAGS,SMDB.F_TAGS);
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
							SMDB.F_TAGS,SMDB.T_WORK_SHEET,
							SMDB.F_OWNER_ID,SMDB.F_TAGS,SMDB.F_TAGS);
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
							SMDB.F_TIMEZONE,SMDB.T_WORK_SHEET,
							SMDB.F_OWNER_ID);
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
							SMDB.F_ID,SMDB.F_NAME,
							SMDB.T_PLAN,SMDB.F_ID,theManySql);
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
		return countEntitiesByBiFields(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_PLAN_ID,planId, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(long ownerId, Calendar startDate, Calendar endDate) throws DBException {
		return selectEntitiesByDateScopeAndField(WorkSheet.class, SMDB.F_DATE, startDate, endDate, SMDB.F_OWNER_ID, ownerId, sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScopeAndTimezone(long loginId, long startDate, long endDate, String timezone) {
		Map<String,Object> params = new HashMap<>();
		params.put(SMDB.F_OWNER_ID,loginId);
		params.put(SMDB.F_TIMEZONE,timezone);
		return selectEntitiesByRange(WorkSheet.class,SMDB.F_DATE_UTC,startDate,endDate,params,sessionFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(long loginId, long startDate, long endDate) {
		Map<String,Object> params = new HashMap<>();
		params.put(SMDB.F_OWNER_ID,loginId);
		return selectEntitiesByRange(WorkSheet.class,SMDB.F_DATE_UTC,startDate,endDate,params,sessionFactory);
	}

	
}
