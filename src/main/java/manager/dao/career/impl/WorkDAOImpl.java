package manager.dao.career.impl;

import static manager.util.DBUtil.countEntitiesByBiFields;
import static manager.util.DBUtil.countEntitiesByRange;
import static manager.util.DBUtil.deleteEntity;
import static manager.util.DBUtil.getHibernateSessionFactory;
import static manager.util.DBUtil.includeEntitiesByField;
import static manager.util.DBUtil.includeUniqueEntityByBiFields;
import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.processDBExcpetion;
import static manager.util.DBUtil.selectEntitiesByDateScopeAndField;
import static manager.util.DBUtil.selectEntitiesByField;
import static manager.util.DBUtil.selectEntitiesByFieldAndManyField;
import static manager.util.DBUtil.selectEntity;
import static manager.util.DBUtil.selectUniqueEntityByField;
import static manager.util.DBUtil.updateExistedEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

public class WorkDAOImpl implements WorkDAO {
	private final SessionFactory hbFactory = getHibernateSessionFactory();
	@Override
	public int insertPlan(Plan plan) throws DBException {
		return insertEntity(plan, hbFactory);
	}

	@Override
	public int insertWorkSheet(WorkSheet sheet) throws DBException {
		return insertEntity(sheet, hbFactory);
	}
	
	@Override
	public int insertPlanDept(PlanDept dept) throws DBException {
		return insertEntity(dept, hbFactory);
	}

	@Override
	public Plan selectPlan(int id) throws NoSuchElement, DBException {
		return selectEntity(id,Plan.class, hbFactory);
	}

	@Override
	public WorkSheet selectWorkSheet(int id) throws NoSuchElement, DBException {
		return selectEntity(id, WorkSheet.class, hbFactory);
	}

	@Override
	public void updateExistedPlan(Plan existed) throws DBException {
		updateExistedEntity(existed, hbFactory);
	}

	@Override
	public List<Plan> selectPlansByOwnerAndStates(int ownerId,List<PlanState> states) throws DBException {
		return selectEntitiesByFieldAndManyField(Plan.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE, states,PlanState::getDbCode,hbFactory);
	}

	@Override
	public void deleteExistedPlan(int planId) throws DBException {
		deleteEntity(Plan.class, planId, hbFactory);
	}
	
	@Override
	public long countPlansByOwnerAndState(int ownerId, PlanState state) throws DBException {
		return countEntitiesByBiFields(Plan.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE,state.getDbCode(), hbFactory);
	}

	@Override
	public long countWorkSheetByOwnerAndState(int ownerId, WorkSheetState state) throws DBException {
		return countEntitiesByBiFields(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE,state.getDbCode(), hbFactory);
	}
	
	@Override
	public List<Plan> selectPlansByField(String field, Object val) throws DBException {
		return selectEntitiesByField(Plan.class, field, val, hbFactory);
	}

	@Override
	public boolean includeUniqueWorkSheetByOwnerAndDate(int ownerId, Calendar date) throws DBException {
		return includeUniqueEntityByBiFields(WorkSheet.class,SMDB.F_OWNER_ID,ownerId,SMDB.F_DATE,date,hbFactory);
	}

	@Override
	public void updateExistedWorkSheet(WorkSheet ws) throws DBException {
		updateExistedEntity(ws, hbFactory);
	}

	@Override
	public void deleteExistedWorkSheet(int wsId) throws DBException {
		deleteEntity(WorkSheet.class, wsId, hbFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(int ownerId, int page,int limit) throws DBException {
		List<WorkSheet> rlt = new ArrayList<WorkSheet>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s FROM %s WHERE %s=? ORDER BY %s DESC LIMIT %s,%s",
						SMDB.F_ID,SMDB.F_DATE,SMDB.F_STATE,
						SMDB.T_WORK_SHEET, SMDB.F_OWNER_ID,SMDB.F_DATE,(page*limit),limit);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, ownerId);
					
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						WorkSheet ws =new WorkSheet();
						ws.setId(rs.getInt(1));
						Calendar date = Calendar.getInstance();
						date.setTime(rs.getDate(2));
						ws.setDate(date);
						ws.setState(WorkSheetState.valueOfDBCode(rs.getInt(3)));
						rlt.add(ws);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}

	@Override
	public long countWorkSheetByDate(Calendar date) throws DBException {
		return countEntitiesByRange(WorkSheet.class, SMDB.F_DATE, TimeUtil.getMinTimeOfDay(date), TimeUtil.getMaxTimeOfDay(date), hbFactory);
	}

	@Override
	public boolean includeWorkSheetByPlanId(int planId) throws DBException {
		return includeEntitiesByField(WorkSheet.class, SMDB.F_PLAN_ID, planId, hbFactory);
	}

	@Override
	public PlanDept selectPlanDeptByOwner(int ownerId) throws NoSuchElement, DBException {
		return selectUniqueEntityByField(PlanDept.class, SMDB.F_OWNER_ID, ownerId, hbFactory);
	}

	@Override
	public void updateExistedPlanDept(PlanDept dept) throws DBException {
		updateExistedEntity(dept, hbFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByField(String field, Object val) throws DBException {
		return selectEntitiesByField(WorkSheet.class, field, val, hbFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetByOwnerAndStates(int ownerId, List<WorkSheetState> states)
			throws DBException {
		return selectEntitiesByFieldAndManyField(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_STATE, states,WorkSheetState::getDbCode,hbFactory);
	}
	
	@Override
	public List<String> selectNonNullTagsByUser(int loginerId) throws DBException {
		List<String> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("select %s From %s where %s=? and %s is not null and trim(%s) != ''",
						SMDB.F_TAGS,SMDB.T_PLAN,
						SMDB.F_OWNER_ID,SMDB.F_TAGS,SMDB.F_TAGS);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						rlt.add(rs.getString(1));
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}


	@Override
	public List<Plan> selectPlanInfosByIds(List<Integer> planIds) throws DBException {
		if(planIds.size() == 0) {
			return new ArrayList<Plan>();
		}
		
		List<Plan> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = planIds.stream().map(val -> val.toString()).collect(Collectors.joining(","));
				String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
						SMDB.F_ID,SMDB.F_NAME,
						SMDB.T_PLAN,SMDB.F_ID,theManySql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Plan one =new Plan();
						one.setId(rs.getInt(1));
						one.setName(rs.getString(2));
						rlt.add(one);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}

	@Override
	public long countWorkSheetByOwnerAndPlanId(int ownerId, int planId) throws DBException {
		return countEntitiesByBiFields(WorkSheet.class,SMDB.F_OWNER_ID,ownerId, SMDB.F_PLAN_ID,planId, hbFactory);
	}

	@Override
	public List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(int ownerId, Calendar startDate, Calendar endDate) throws DBException {
		return selectEntitiesByDateScopeAndField(WorkSheet.class, SMDB.F_DATE, startDate, endDate, SMDB.F_OWNER_ID, ownerId, hbFactory);
	}



	
}
