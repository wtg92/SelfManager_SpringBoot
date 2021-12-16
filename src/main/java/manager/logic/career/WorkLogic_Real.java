package manager.logic.career;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.dao.DAOFactory;
import manager.dao.career.WorkDAO;
import manager.data.career.PlanDeptContent;
import manager.data.career.WorkSheetContent;
import manager.data.career.WorkSheetContent.PlanItemNode;
import manager.data.proxy.career.PlanDeptProxy;
import manager.data.proxy.career.PlanItemProxy;
import manager.data.proxy.career.PlanProxy;
import manager.data.proxy.career.WorkItemProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanDept;
import manager.entity.general.career.WorkSheet;
import manager.entity.virtual.career.PlanDeptItem;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.CacheScheduler;
import manager.system.CacheMode;
import manager.system.SM;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.career.CareerLogAction;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.WorkItemType;
import manager.system.career.WorkSheetState;
import manager.util.TimeUtil;

public class WorkLogic_Real extends WorkLogic{
	
	final private static Logger logger = Logger.getLogger(WorkLogic_Real.class.getName());

	private WorkDAO wDAO = DAOFactory.getWorkDAO();
	
	@Override
	public int createPlan(int ownerId, String name, Calendar startDate, Calendar endDate, String note) throws LogicException, DBException {
		
		uL.checkPerm(ownerId, SMPerm.CREATE_WORKSHEET_PLAN);
		
		if(TimeUtil.isAfterByDate(startDate, endDate) && TimeUtil.isNotBlank(endDate)) {
			throw new LogicException(SMError.CREATE_PLAN_ERROR,"开始日期不能晚于结束日期");
		}
		
		Plan plan = new Plan();
		plan.setName(name);
		plan.setNote(note);
		plan.setOwnerId(ownerId);
		plan.setStartDate(startDate);
		plan.setEndDate(endDate);
		plan.setSeqWeight(0);
		plan.setState(calculateStateByNow(plan));
	
		WorkContentConverter.addLog(plan, CareerLogAction.CREATE_PLAN,
				ownerId,
				name,
				TimeUtil.parseDate(startDate),
				TimeUtil.parseDate(endDate),
				plan.getState().getDbCode());
		
		return wDAO.insertPlan(plan);
	}

	@Override
	public void addItemToPlan(int adderId, int planId, String categoryName, int value, String note,
			PlanItemType type, int fatherId, double mappingVal) throws LogicException, DBException {
		Plan existed = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(existed.getOwnerId() != adderId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.addItemToPlan(existed,adderId,categoryName,value,note,type,fatherId,mappingVal);
		CacheScheduler.saveEntityAndUpdateCache(existed,p->wDAO.updateExistedPlan(p));
	}

	@Override
	public void addItemToWSPlan(int adderId, int wsId, String categoryName, int value, String note, PlanItemType type,
			int fatherId, double mappingVal) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(adderId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,adderId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.addItemToWSPlan(ws, adderId, categoryName, value, note, type, fatherId, mappingVal);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	
	@Override
	public synchronized void addItemToWS(int adderId, int wsId, int planItemId, int value, String note, int mood,
			boolean forAdd,Calendar startTime, Calendar endTime) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(adderId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,adderId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.addItemToWorkSheet(ws, adderId, planItemId, value, note, mood, forAdd, startTime, endTime);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	@Override
	public void removeItemFromPlan(int removerId, int planId, int itemId) throws LogicException, DBException {
		Plan existed = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(existed.getOwnerId() != removerId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.removeItemFromPlan(existed,removerId,itemId);
		CacheScheduler.saveEntityAndUpdateCache(existed,p->wDAO.updateExistedPlan(p));
	}
	
	@Override
	public synchronized void removeItemFromWSPlan(int removerId, int wsId, int itemId) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(removerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,removerId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.removeItemFromWSPlan(ws, removerId, itemId);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	@Override
	public synchronized void removeItemFromWorkSheet(int removerId, int wsId, int itemId) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(removerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,removerId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.removeItemFromWorkSheet(ws, removerId, itemId);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	
	@Override
	public void savePlan(int saverId, int planId, String name, Calendar startDate, Calendar endDate, String note,
			boolean recalculateState,List<PlanSetting> settings,int seqWeight) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != saverId) {
			throw new LogicException(SMError.CANNOT_SAVE_PLAN);
		}
		
		if(!name.equals(plan.getName())
				|| TimeUtil.isNotSameByDate(plan.getStartDate(),startDate)
				|| TimeUtil.isNotSameByDate(plan.getEndDate(),endDate)) {
			WorkContentConverter.addLog(plan, CareerLogAction.SAVE_PLAN, saverId,
					plan.getName(),
					TimeUtil.parseDate(plan.getStartDate()),
					TimeUtil.parseDate(plan.getEndDate()),
					name,
					TimeUtil.parseDate(startDate),
					TimeUtil.parseDate(endDate));
		}
		
		plan.setName(name);
		plan.setStartDate(startDate);
		plan.setEndDate(endDate);
		plan.setSetting(settings);
		plan.setSeqWeight(seqWeight);
		plan.setNote(note);
		
		if(recalculateState) {
			PlanState stateByNow = calculateStateByNow(plan);
			if(stateByNow != plan.getState()) {
				WorkContentConverter.addLog(plan, CareerLogAction.STATE_CHENGED_DUE_TO_SAVING_PLAN, saverId,
						plan.getState().getDbCode(),
						stateByNow.getDbCode());
			}
			plan.setState(stateByNow);
		}
		
		CacheScheduler.saveEntityAndUpdateCache(plan,p->wDAO.updateExistedPlan(p));
	}

	@Override
	public void savePlanItem(int loginerId, int planId,int itemId , String catName, int value,String note, double mappingVal) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != loginerId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.updatePlanItem(plan, loginerId, itemId, catName, value, note, mappingVal);
		CacheScheduler.saveEntityAndUpdateCache(plan,p->wDAO.updateExistedPlan(p));
	}
	
	@Override
	public void savePlanItemFold(int loginerId, int planId, int itemId, boolean fold) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != loginerId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.updatePlanItemFold(plan, loginerId, itemId, fold);
		CacheScheduler.saveEntityAndUpdateCache(plan,p->wDAO.updateExistedPlan(p));
	}
	

	@Override
	public void saveWSPlanItem(int loginerId, int wsId, int itemId, String catName, int value, String note,
			double mappingVal) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(loginerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,loginerId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.updateWSPlanItem(ws, loginerId, itemId, catName, value, note, mappingVal);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}

	@Override
	public void saveWSPlanItemFold(int loginerId, int wsId, int itemId, boolean fold) throws DBException, LogicException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(loginerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,loginerId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.updateWSPlanItemFold(ws, loginerId, itemId, fold);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	@Override
	public void savePlanDeptItem(int updaterId, int itemId, String name, double val)
			throws LogicException, DBException {
		PlanDept dept = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, PlanDept.class, ()->wDAO.selectExistedPlanDeptByOwner(updaterId));
		WorkContentConverter.updatePlanDeptItem(dept, updaterId, itemId, name, val);
		CacheScheduler.saveEntityAndUpdateCache(dept,w->wDAO.updateExistedPlanDept(w));
	}
	
	
	@Override
	public void abandonPlan(int opreatorId, int planId) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != opreatorId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		assert plan.getState() != PlanState.ABANDONED;
		
		boolean existsWSBaseThisPlan = wDAO.includeWorkSheetByPlanId(plan.getId());
		if(!existsWSBaseThisPlan) {
			CacheScheduler.deleteEntityById(plan, (id)->wDAO.deleteExistedPlan(id));
			return;
		}
		
		PlanState after= PlanState.ABANDONED;
		assert plan.getState() != after;
		Calendar endDate = TimeUtil.getCurrentDate();
		
		WorkContentConverter.addLog(plan, CareerLogAction.ABANDON_PLAN, opreatorId,
				plan.getState().getDbCode(),
				after.getDbCode(),
				TimeUtil.parseDate(plan.getEndDate()),
				TimeUtil.parseDate(endDate));
		
		plan.setState(after);
		plan.setEndDate(endDate);
		CacheScheduler.saveEntityAndUpdateCache(plan,p->wDAO.updateExistedPlan(p));
	}

	@Override
	public void finishPlan(int opreatorId, int planId) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != opreatorId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		PlanState after= PlanState.FINISHED;
		assert plan.getState() != after;
		Calendar endDate = TimeUtil.getCurrentDate();
		
		WorkContentConverter.addLog(plan, CareerLogAction.FINISH_PLAN, opreatorId,
				plan.getState().getDbCode(),
				after.getDbCode(),
				TimeUtil.parseDate(plan.getEndDate()),
				TimeUtil.parseDate(endDate));
		
		plan.setState(after);
		plan.setEndDate(endDate);
		CacheScheduler.saveEntityAndUpdateCache(plan,p->wDAO.updateExistedPlan(p));
	}
	
	@Override
	public List<WorkSheet> loadWorkSheetInfosRecently(int opreatorId, int page) throws DBException, LogicException {
		List<WorkSheet> sheetInfos = wDAO.selectWorkSheetInfoRecentlyByOwner(opreatorId, page, DEFAULT_WS_LIMITE_OF_ONE_PAGE);
		
		List<WorkSheet> actives = sheetInfos.stream().filter(ws->ws.getState() == WorkSheetState.ACTIVE
				&& TimeUtil.isNotSameByDate(ws.getDate(), TimeUtil.getCurrentDate())).collect(toList());
		
		for(WorkSheet active:actives) {
			assert active.getState() == WorkSheetState.ACTIVE;
			WorkSheet fullWs = wDAO.selectExistedWorkSheet(active.getId());
			WorkSheetState stateByNow = calculateStateByNow(fullWs);
			if(stateByNow == fullWs.getState()) {
				continue;
			}
			
			WorkContentConverter.addLog(fullWs, CareerLogAction.WS_STATE_CHANGED_BY_DATE, SM.SYSTEM_ID,
					fullWs.getState().getDbCode(),
					stateByNow.getDbCode());
			
			fullWs.setState(stateByNow);
			CacheScheduler.saveEntityAndUpdateCacheOnlyIfExists(fullWs, one->wDAO.updateExistedWorkSheet(one));
			
			active.setState(stateByNow);
		}
		
		return sheetInfos;
	}
	
	
	/**
	 *  需要计划名 平均心情 假设不包括同步项  计划的完成情况
	 */
	@Override
	public List<WorkSheetProxy> loadWorkSheetsByDateScope(int loginerId, Calendar startDate, Calendar endDate)
			throws SMException {
		
		List<WorkSheet> wss = wDAO.selectWorkSheetsByOwnerAndDateScope(loginerId, startDate, endDate);
		
		List<WorkSheetProxy> rlt = fillPlanNames(wss);
		
		for(WorkSheetProxy ws: rlt) {
			WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws.ws);
			calculateWSContentDetail(content);
			ws.mood = calculateMoodByWokrItems(content.workItems);
			List<WorkItemProxy> itemsWithoutDeptItems = content.workItems.stream().filter(item->item.item.getType() != WorkItemType.DEPT).collect(toList());
			calculatePlanItemProxyDetail(content.planItems, itemsWithoutDeptItems);
			ws.finishPlanWithoutDeptItems = content.planItems.stream().allMatch(pItem->pItem.remainingValForCur <= 0.0);
			ws.content = content;
		}
		
		return clearUnnecessaryInfo(rlt);
	}

	@Override
	public WorkSheetProxy loadWorkSheet(int loginerId, int wsId) throws DBException, LogicException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(loginerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_SEE_OTHERS_WS,loginerId+" vs "+ws.getOwnerId());
		}
		WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
		calculateWSContentDetail(content);
		
		WorkSheetProxy rlt = new WorkSheetProxy(ws);
		
		rlt.mood = calculateMoodByWokrItems(content.workItems);
		rlt.basePlanName = CacheScheduler.getOne(CacheMode.E_ID, rlt.ws.getPlanId(), Plan.class, ()->wDAO.selectExistedPlan(rlt.ws.getPlanId())).getName();
		rlt.content = content;
		
		fill(rlt.content.logs); 
		
		return clearUnnecessaryInfo(rlt);
	}
	
	@Override
	public long loadWorkSheetCount(int loginerId,Calendar date) throws SMException {
		uL.checkPerm(loginerId, SMPerm.SEE_TODAY_WS_COUNT);
		long count = Long.parseLong(CacheScheduler.getTempValOrInit(CacheMode.T_WS_COUNT_FOR_DATE, date, ()->wDAO.countWorkSheetByDate(date)));
		return count;
	}

	
	@Override
	public List<Plan> loadActivePlans(int loginerId) throws LogicException, DBException {
		uL.checkPerm(loginerId, SMPerm.SEE_SELF_PLANS);
		
		List<Plan> target = wDAO.selectPlansByOwnerAndStates(loginerId,Arrays.asList(PlanState.ACTIVE,PlanState.PREPARED));
		
		List<Plan> prepared = target.stream().filter(one->
			one.getState()== PlanState.PREPARED
		).collect(toList());
		
		List<Plan> stateChangedForPrepared = prepared.stream().filter(one->{
			PlanState stateByNow =  calculateStateByNow(one);
			
			one.setState(stateByNow);
			
			return stateByNow != PlanState.PREPARED;
		}).collect(toList());
		
		for(Plan needToUpdate : stateChangedForPrepared) {
			WorkContentConverter.addLog(needToUpdate, CareerLogAction.PLAN_STATE_CHANGED_BY_DATE,
					SM.SYSTEM_ID,
					TimeUtil.parseDate(needToUpdate.getStartDate()),
					TimeUtil.parseDate(needToUpdate.getEndDate()),
					PlanState.PREPARED.getDbCode(),needToUpdate.getState().getDbCode());
		}
		CacheScheduler.saveEntitiesAndUpdateCacheOnlyIfExists(stateChangedForPrepared, (p)->wDAO.updateExistedPlan(p));
		prepared.removeAll(stateChangedForPrepared);
		target.removeAll(prepared);
		
		List<Plan> stateChangedForActive = target.stream().filter(one->{
			PlanState stateByNow =  calculateStateByNow(one);
			
			one.setState(stateByNow);
			
			return stateByNow != PlanState.ACTIVE;
		}).collect(toList());
		
		for(Plan needToUpdate : stateChangedForActive) {
			WorkContentConverter.addLog(needToUpdate, CareerLogAction.PLAN_STATE_CHANGED_BY_DATE,
					SM.SYSTEM_ID,
					TimeUtil.parseDate(needToUpdate.getStartDate()),
					TimeUtil.parseDate(needToUpdate.getEndDate()),
					PlanState.ACTIVE.getDbCode(),needToUpdate.getState().getDbCode());
		}
		CacheScheduler.saveEntitiesAndUpdateCacheOnlyIfExists(stateChangedForActive, (p)->wDAO.updateExistedPlan(p));
		
		target.removeAll(stateChangedForActive);
		CacheScheduler.putEntitiesToCacheById(target);
		return target;
	}
	
	@Override
	public PlanProxy loadPlan(int loginerId,int planId) throws LogicException, DBException {
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != loginerId) {
			throw new LogicException(SMError.CANNOT_SEE_PLAN);
		}
		
		PlanProxy proxy = new PlanProxy(plan);
	 	proxy.content = WorkContentConverter.convertPlanContent(plan);
	 	proxy.countWS = wDAO.countWorkSheetByOwnerAndPlanId(loginerId, planId);
	 	
	 	fill(proxy.content.logs);
	 	
		return proxy;
	}
	

	@Override
	public Map<String, Long> loadPlanStateStatistics(int ownerId) throws LogicException, DBException {
		Map<String,Long> rlt = new HashMap<>();
		
		for(PlanState state:PlanState.values()) {
			if(state == PlanState.UNDECIDED)
				continue;
			
			rlt.put(String.valueOf(state.getDbCode()), wDAO.countPlansByOwnerAndState(ownerId, state));
		}
		
		return rlt;
	}
	
	@Override
	public Map<String, Long> loadWSStateStatistics(int loginerId) throws LogicException, DBException {
		Map<String,Long> rlt = new HashMap<>();
		
		for(WorkSheetState state:WorkSheetState.values()) {
			if(state == WorkSheetState.UNDECIDED)
				continue;
			
			rlt.put(String.valueOf(state.getDbCode()), wDAO.countWorkSheetByOwnerAndState(loginerId, state));
		}
		
		return rlt;
	}
	
	@Override
	public List<Plan> loadPlansByState(int ownerId,PlanState stateZT) throws LogicException, DBException {
		if(stateZT == PlanState.UNDECIDED) {
			return wDAO.selectPlansByField(SMDB.F_OWNER_ID, ownerId);
		}
		return wDAO.selectPlansByOwnerAndStates(ownerId, Arrays.asList(stateZT));
	}
	
	@Override
	public PlanDeptProxy loadPlanDept(int loginerId) throws DBException, LogicException {
		PlanDept dept = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, PlanDept.class, 
				 ()->wDAO.selectPlanDeptByOwner(loginerId), ()->initPlanDept(loginerId));
		
		PlanDeptProxy proxy = new PlanDeptProxy(dept);
		
		proxy.content = WorkContentConverter.convertPlanDept(dept);
		
		fill(proxy.content.logs); 
		
		return proxy;
	}

	@Override
	public synchronized int openWorkSheetToday(int opreatorId, int planId) throws DBException, LogicException {
		Calendar today = TimeUtil.getCurrentDate();
		if(CacheScheduler.existsByBiFields(CacheMode.E_ID,
				WorkSheet::getOwnerId,opreatorId,
				WorkSheet::getDate,today,
				WorkSheet.class,()-> wDAO.includeUniqueWorkSheetByOwnerAndDate(opreatorId, today))) {
			throw new LogicException(SMError.OPEN_WORK_SHEET_SYNC_ERROR);
		}
		
		Plan plan = CacheScheduler.getOne(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		
		if(plan.getState() != PlanState.ACTIVE) {
			throw new LogicException(SMError.OPEN_WORK_BASE_WRONG_STATE_PLAN,plan.getState().getName());
		}
		
		if(plan.getOwnerId() != opreatorId) {
			throw new LogicException(SMError.CANNOTE_OPEN_OTHERS_PLAN,plan.getOwnerId()+":"+opreatorId);
		}
		
		WorkSheet ws = new WorkSheet();
		ws.setOwnerId(opreatorId);
		ws.setDate(today);
		ws.setPlanId(planId);
		
		WorkContentConverter.pushToWorkSheet(plan, ws);
		
		ws.setState(calculateStateByNow(ws));
		
		WorkContentConverter.addLog(ws, CareerLogAction.OPEN_WS_TODAY, opreatorId,
				plan.getName(),ws.getState().getDbCode());
		
		int id = wDAO.insertWorkSheet(ws);
		
		CacheScheduler.deleteTempKey(CacheMode.T_WS_COUNT_FOR_DATE, ws.getDate());
		
		return id;
	}

	@Override
	public synchronized void saveWorkSheet(int updaterId, int wsId, String note) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,updaterId+" vs "+ws.getOwnerId());
		}
		ws.setNote(note);
		CacheScheduler.saveEntityAndUpdateCache(ws, one->wDAO.updateExistedWorkSheet(one));
	}

	@Override
	public void deleteWorkSheet(int updaterId, int wsId) throws DBException, LogicException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,updaterId+" vs "+ws.getOwnerId());
		}
		
		CacheScheduler.deleteEntityById(ws, id->wDAO.deleteExistedWorkSheet(id));
		CacheScheduler.deleteTempKey(CacheMode.T_WS_COUNT_FOR_DATE, ws.getDate());
	}

	@Override
	public void assumeWorkSheetFinished(int opreatorId, int wsId) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(opreatorId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,opreatorId+" vs "+ws.getOwnerId());
		}
		
		ws.setState(WorkSheetState.NO_MONITOR);
		CacheScheduler.saveEntityAndUpdateCache(ws, one->wDAO.updateExistedWorkSheet(one));
	}

	@Override
	public void cancelAssumeWorkSheetFinished(int opreatorId, int wsId) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(opreatorId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,opreatorId+" vs "+ws.getOwnerId());
		}
		if(ws.getState() != WorkSheetState.NO_MONITOR) {
			throw new LogicException(SMError.CANNOT_CANCEL_WS_WHICH_NOT_ASSUMED,ws.getState().getName());
		}
		ws.setState(calculateStateByNow(ws));
		CacheScheduler.saveEntityAndUpdateCache(ws, one->wDAO.updateExistedWorkSheet(one));
	}

	
	
	@Override
	public synchronized void saveWorkItem(int updaterId,int wsId, int workItemId, int value, String note, int mood, boolean forAdd,
			Calendar startTime, Calendar endTime) throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,updaterId+" vs "+ws.getOwnerId());
		}
		WorkContentConverter.updateWorkItem(ws, updaterId, workItemId, value, note, mood, forAdd, startTime, endTime);
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	@Override
	public synchronized void saveWorkItemPlanItemId(int updaterId, int wsId, int workItemId, int planItemId)
			throws LogicException, DBException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,updaterId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.updateWorkItemPlanItemId(ws, updaterId, workItemId, planItemId);;
		
		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
	}
	
	private synchronized int initPlanDept(int ownerId) throws DBException {
		PlanDept dept = new PlanDept();
		dept.setOwnerId(ownerId);
		try {
			WorkContentConverter.addLog(dept, CareerLogAction.CREATE_PLAN_DEPT, SM.SYSTEM_ID);
		}catch (LogicException e) {
			/*这个函数不想让它抛逻辑异常 在这里处理*/
			throw new RuntimeException("initPlanDept error "+e.getMessage());
		}
		return wDAO.insertPlanDept(dept);
	}
	
	/**
	 * 要重新计算一下状态
	 */
	@Override
	public synchronized void syncToPlanDept(int loginerId, int wsId, int planItemId) throws DBException, LogicException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(loginerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,loginerId+" vs "+ws.getOwnerId());
		}

		WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
		calculateWSContentDetail(content);
		
		Map<Integer,PlanItemNode> planItemsById = parseTo(content.planItems);
		if(!planItemsById.containsKey(planItemId)) {
			throw new LogicException(SMError.INCONSISTANT_WS_DATA,"缺失的wsPlanId"+planItemId);
		}
		PlanItemProxy planItem = planItemsById.get(planItemId).item;
		
		if(planItem.remainingValForCur == 0) {
			throw new LogicException(SMError.NO_SYNC_ZERO_WS_PLAN_ITEM,planItem.item.getName());
		}
		
		PlanDept dept = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, PlanDept.class, 
				 ()->wDAO.selectPlanDeptByOwner(loginerId), ()->initPlanDept(loginerId));
		
		WorkContentConverter.syncToPlanDept(ws,dept,planItem,loginerId);

		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
		CacheScheduler.saveEntityAndUpdateCache(dept,d->wDAO.updateExistedPlanDept(d));
	}
	
	@Override
	public synchronized void syncAllToPlanDept(int loginerId, int wsId) throws DBException, LogicException {
		WorkSheet ws = CacheScheduler.getOne(CacheMode.E_ID,wsId, WorkSheet.class, ()->wDAO.selectExistedWorkSheet(wsId));
		if(loginerId != ws.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_OPREATE_OTHERS_WS,loginerId+" vs "+ws.getOwnerId());
		}

		WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
		calculateWSContentDetail(content);
		
		
		/*同步所有时，是以根节点为准 同步的*/
		List<PlanItemProxy> needingToSync = content.planItems.stream()
				.filter(item->item.remainingValForCur!=0).collect(toList());
		
		if(needingToSync.size() == 0) {
			logger.log(Level.WARNING,"同步所有却没有需要同步的，前台出现问题了？"+wsId);
		}
		
		PlanDept dept = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, PlanDept.class, 
				 ()->wDAO.selectPlanDeptByOwner(loginerId), ()->initPlanDept(loginerId));
		
		for(PlanItemProxy planItem :needingToSync) {
			WorkContentConverter.syncToPlanDept(ws,dept,planItem,loginerId);
		}

		refreshStateAfterItemModified(ws);
		
		CacheScheduler.saveEntityAndUpdateCache(ws,w->wDAO.updateExistedWorkSheet(w));
		CacheScheduler.saveEntityAndUpdateCache(dept,d->wDAO.updateExistedPlanDept(d));
	}
	
	
	@Override
	public void syncAllToPlanDeptBatch(int logienrId, List<Integer> wsIds) throws SMException {
		for(int wsId:wsIds) {
			syncAllToPlanDept(logienrId, wsId);
		}
	}
	
	@Override
	public List<WorkSheetProxy> loadWorkSheetByState(int loginerId, WorkSheetState stateZT)
			throws LogicException, DBException {
		if(stateZT == WorkSheetState.UNDECIDED) {
			return fillPlanNames(wDAO.selectWorkSheetByField(SMDB.F_OWNER_ID, loginerId));
		}
		return clearUnnecessaryInfo(fillPlanNames(wDAO.selectWorkSheetByOwnerAndStates(loginerId, Arrays.asList(stateZT))));
	}
	
	private List<WorkSheetProxy> fillPlanNames(List<WorkSheet> src) throws DBException{
		List<WorkSheetProxy> rlt = src.stream().map(WorkSheetProxy::new).collect(toList());
		List<Integer> planIds = src.stream().map(WorkSheet::getPlanId).distinct().collect(toList());
		Map<Integer,Plan> relevantPlans =  wDAO.selectPlanInfosByIds(planIds).stream().collect(toMap(Plan::getId, Function.identity()));
		
		for(WorkSheetProxy one : rlt) {
			try{
				one.basePlanName = relevantPlans.get(one.ws.getPlanId()).getName();
			}catch (Exception e) {
				e.printStackTrace();
				assert false ; 
				one.basePlanName = "出错，请点开查看";
			}
		}
		
		return rlt;
	}

	@Override
	public List<String> loadPlanDeptItemNames(int loginerId) throws DBException, LogicException {
		PlanDept dept = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, PlanDept.class, 
				 ()->wDAO.selectPlanDeptByOwner(loginerId), ()->initPlanDept(loginerId));
		PlanDeptContent content = WorkContentConverter.convertPlanDept(dept);
		return content.items.stream().map(PlanDeptItem::getName).collect(toList());
	}

	@Override
	public void copyPlanItemsFrom(int loginerId, int targetPlanId, int templetePlanId)
			throws DBException, LogicException {
		Plan target = CacheScheduler.getOne(CacheMode.E_ID, targetPlanId, Plan.class, ()->wDAO.selectExistedPlan(targetPlanId));
		if(target.getOwnerId() != loginerId) {
			throw new LogicException(SMError.CANNOT_EDIT_OTHERS_PLAN);
		}
		Plan templete = CacheScheduler.getOne(CacheMode.E_ID, templetePlanId, Plan.class, ()->wDAO.selectExistedPlan(templetePlanId));
		if(templete.getOwnerId() !=loginerId 
				&& !templete.hasSetting(PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS)) {
			throw new LogicException(SMError.CONNOT_COPY_OTHERS_PLANITEMS);
		}
		
		WorkContentConverter.copyPlanItemsFrom(target, templete, loginerId);
		
		CacheScheduler.saveEntityAndUpdateCache(target,p->wDAO.updateExistedPlan(p));
	}












}
