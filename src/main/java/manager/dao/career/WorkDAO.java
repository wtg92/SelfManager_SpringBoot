package manager.dao.career;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanBalance;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SelfXErrors;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;

public interface WorkDAO {
	
	
	long insertPlan(Plan plan) throws DBException;
	long insertWorkSheet(WorkSheet sheet) throws DBException;
	long insertBalance(PlanBalance balance) throws DBException;
	
	Plan selectPlan(long id) throws NoSuchElement, DBException;
	List<Plan> selectPlansByOwnerAndStates(long ownerId,List<PlanState> states);

	List<WorkSheet> selectWorkSheetByOwnerAndStates(long ownerId,List<WorkSheetState> states);
	long countWorkSheetByOwnerAndPlanId(long ownerId,long planId) throws DBException;
	
	void deleteExistedPlan(long planId) throws DBException;
	void deleteExistedWorkSheet(long wsId);
	
	default Plan selectExistedPlan(long id) throws DBException{
		try {
			return selectPlan(id);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}
	
	WorkSheet selectWorkSheet(long id) throws NoSuchElement, DBException;
	PlanBalance selectBalanceByOwner(long ownerId) throws NoSuchElement, DBException;

	
	default WorkSheet selectExistedWorkSheet(long id) throws DBException{
		try {
			return selectWorkSheet(id);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}

	void updateExistedPlan(Plan existed) throws DBException;
	void updateExistedWorkSheet(WorkSheet ws) throws DBException;
	void updateExistedBalance(PlanBalance dept) throws DBException;
	
	List<Plan> selectPlansByField(String field, Object val) throws DBException;
	List<WorkSheet> selectWorkSheetByField(String field, Object val) throws DBException;
	

	boolean includeUniqueWorkSheetByOwnerAndDateAndTimezone(long ownerId,long date,String timezone);


	/*只查询date排序的最新几条 id state date三个字段*/
	/*page从0开始*/
	List<WorkSheet> selectWorkSheetInfoRecentlyByOwner(long ownerId,long page,long limit) throws DBException;
	

	/*只取名字 Id*/
	List<Plan> selectPlanInfosByIds(List<Long> planIds) throws DBException;

	long countWorkSheetByDateAndTimezone(Long date,String timezone);


	boolean includeWorkSheetByPlanId(long planId) throws DBException;
	List<String> selectNonNullPlanTagsByUser(long loginId) throws DBException;
	List<String> selectNonNullWorkSheetTagsByUser(long loginId) throws DBException;


    List<String> getDistinctWorksheetTimezones(long loginId);

	List<WorkSheet> selectWorkSheetsByOwnerAndDateScopeAndTimezone(long loginId, long startDate, long endDate, String timezone);

	List<WorkSheet> selectWorkSheetsByOwnerAndDateScope(long loginId, long startDate, long endDate);

	List<Plan> selectPlansByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan);

	long countPlansByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan);

	List<WorkSheet> selectWorksheetsByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan);
	long countWorksheetsByTerms(Map<String, Object> likes, Map<String, Object> equals, Map<String, Object> greaterThan, Map<String, Object> lessThan);


}
