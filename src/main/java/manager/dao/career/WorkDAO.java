package manager.dao.career;

import java.util.Calendar;
import java.util.List;

import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanDept;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMError;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;

public interface WorkDAO {
	
	
	int insertPlan(Plan plan) throws DBException;
	int insertWorkSheet(WorkSheet sheet) throws DBException;
	int insertPlanDept(PlanDept dept) throws DBException;
	
	Plan selectPlan(int id) throws NoSuchElement, DBException;
	List<Plan> selectPlansByOwnerAndStates(int ownerId,List<PlanState> states) throws DBException;
	List<WorkSheet> selectWorkSheetByOwnerAndStates(int ownerId,List<WorkSheetState> states) throws DBException;
	long countPlansByOwnerAndState(int ownerId,PlanState state) throws DBException;
	long countWorkSheetByOwnerAndState(int ownerId,WorkSheetState state) throws DBException;
	long countWorkSheetByOwnerAndPlanId(int ownerId,int planId) throws DBException;
	
	void deleteExistedPlan(int planId) throws DBException;
	void deleteExistedWorkSheet(int wsId) throws DBException;
	
	default Plan selectExistedPlan(int id) throws DBException{
		try {
			return selectPlan(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}
	
	WorkSheet selectWorkSheet(int id) throws NoSuchElement, DBException;
	PlanDept selectPlanDeptByOwner(int ownerId) throws NoSuchElement, DBException;
	default PlanDept selectExistedPlanDeptByOwner(int ownerId) throws DBException{
		try {
			return selectPlanDeptByOwner(ownerId);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,ownerId);
		}
	}
	
	
	default WorkSheet selectExistedWorkSheet(int id) throws DBException{
		try {
			return selectWorkSheet(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}

	void updateExistedPlan(Plan existed) throws DBException;
	void updateExistedWorkSheet(WorkSheet ws) throws DBException;
	void updateExistedPlanDept(PlanDept dept) throws DBException;
	
	List<Plan> selectPlansByField(String field, Object val) throws DBException;
	List<WorkSheet> selectWorkSheetByField(String field, Object val) throws DBException;
	
	boolean includeUniqueWorkSheetByOwnerAndDate(int ownerId,Calendar date) throws DBException;
	
	/*只查询date排序的最新几条 id state date三个字段*/
	/*page从0开始*/
	List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(int ownerId,int page,int limit) throws DBException;
	
	/*闭区间*/
	List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(int ownerId,Calendar startDate,Calendar endDate) throws DBException;
	
	
	/*只取名字 Id*/
	List<Plan> selectPlanInfosByIds(List<Integer> planIds) throws DBException;
	long countWorkSheetByDate(Calendar date) throws DBException;
	
	boolean includeWorkSheetByPlanId(int planId) throws DBException;


	
}
