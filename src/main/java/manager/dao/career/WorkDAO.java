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
	
	
	long insertPlan(Plan plan) throws DBException;
	long insertWorkSheet(WorkSheet sheet) throws DBException;
	long insertPlanDept(PlanDept dept) throws DBException;
	
	Plan selectPlan(long id) throws NoSuchElement, DBException;
	List<Plan> selectPlansByOwnerAndStates(long ownerId,List<PlanState> states) throws DBException;
	List<WorkSheet> selectWorkSheetByOwnerAndStates(long ownerId,List<WorkSheetState> states) throws DBException;
	long countPlansByOwnerAndState(long ownerId,PlanState state) throws DBException;
	long countWorkSheetByOwnerAndState(long ownerId,WorkSheetState state) throws DBException;
	long countWorkSheetByOwnerAndPlanId(long ownerId,long planId) throws DBException;
	
	void deleteExistedPlan(long planId) throws DBException;
	void deleteExistedWorkSheet(long wsId) throws DBException;
	
	default Plan selectExistedPlan(long id) throws DBException{
		try {
			return selectPlan(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}
	
	WorkSheet selectWorkSheet(long id) throws NoSuchElement, DBException;
	PlanDept selectPlanDeptByOwner(long ownerId) throws NoSuchElement, DBException;
	default PlanDept selectExistedPlanDeptByOwner(long ownerId) throws DBException{
		try {
			return selectPlanDeptByOwner(ownerId);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,ownerId);
		}
	}
	
	
	default WorkSheet selectExistedWorkSheet(long id) throws DBException{
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
	
	boolean includeUniqueWorkSheetByOwnerAndDate(long ownerId,Calendar date) throws DBException;
	
	/*?????????date????????????????????? id state date????????????*/
	/*page???0??????*/
	List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(long ownerId,long page,long limit) throws DBException;
	
	/*?????????*/
	List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(long ownerId,Calendar startDate,Calendar endDate) throws DBException;
	
	
	/*???????????? Id*/
	List<Plan> selectPlanInfosByIds(List<Long> planIds) throws DBException;
	long countWorkSheetByDate(Calendar date) throws DBException;
	
	boolean includeWorkSheetByPlanId(long planId) throws DBException;
	List<String> selectNonNullPlanTagsByUser(long loginerId) throws DBException;
	List<String> selectNonNullWorkSheetTagsByUser(long loginerId) throws DBException;

	
}
