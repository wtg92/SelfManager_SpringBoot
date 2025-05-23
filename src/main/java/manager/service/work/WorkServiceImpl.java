package manager.service.work;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import manager.cache.CacheOperator;
import manager.dao.career.WorkDAO;
import manager.data.EntityTag;
import manager.data.worksheet.BalanceContent;
import manager.data.MultipleItemsResult;
import manager.data.worksheet.WorkSheetContent;
import manager.data.worksheet.WorkSheetContent.PlanItemNode;
import manager.data.proxy.career.PlanBalanceProxy;
import manager.data.proxy.career.PlanItemProxy;
import manager.data.proxy.career.PlanProxy;
import manager.data.proxy.career.WorkItemProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanBalance;
import manager.entity.general.career.WorkSheet;
import manager.entity.virtual.worksheet.BalanceItem;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.booster.TagCalculator;
import manager.cache.CacheMode;
import manager.system.SelfX;
import manager.system.DBConstants;
import manager.system.SelfXErrors;
import manager.system.SelfXPerms;
import manager.system.career.CareerLogAction;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.WorkItemType;
import manager.system.career.WorkSheetState;
import manager.util.CommonUtil;

import manager.util.ZonedTimeUtils;
import manager.util.locks.LockHandler;
import manager.util.locks.UserLockManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 关于缓存的clone getEntity 其实不必clone
 */
@Service
public class WorkServiceImpl extends WorkService {
	
	final private static Logger logger = Logger.getLogger(WorkServiceImpl.class.getName());

	@Resource
	private WorkDAO wDAO;

	@Resource
	private UserLockManager locker;

	@Resource
	CacheOperator cache;

	private Plan getPlan(long planId){
		return cache.getEntity(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
	}

	/**
	 * 内存里的缓存 有一个危险 是可能被修改
	 * 因此所有的get 都需要clone一下
	 */
	private WorkSheet getWorksheet(long wsId){
		return cache.getWorksheet(wsId, ()->wDAO.selectExistedWorkSheet(wsId));
	}

	private PlanBalance getPlanBalance(long loginId){
		return cache.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginId, PlanBalance.class,
				()->wDAO.selectBalanceByOwner(loginId), ()->initPlanDept(loginId));
	}

	private void updateWorksheetSynchronously(WorkSheet ws, long loginId){
		locker.lockByUserAndClass(loginId,()->{
			cache.saveWorksheet(ws, one->wDAO.updateExistedWorkSheet(one));
		});
	}

	private void updatePlanDeptSynchronously(PlanBalance dept, long loginId){
		locker.lockByUserAndClass(loginId,()->{
			cache.saveEntity(dept, d->wDAO.updateExistedBalance(d));
		});
	}

	private void updatePlanSynchronously(Plan plan, long loginId){
		locker.lockByUserAndClass(loginId,()->{
			cache.saveEntity(plan, p->wDAO.updateExistedPlan(p));
		});
	}

	private long addPlanSynchronously(Plan plan, long loginId){
		LockHandler<Long> handler = new LockHandler<>();
		locker.lockByUserAndClass(loginId,()->{
			handler.val = wDAO.insertPlan(plan);
		});
		return handler.val;
	}

	private void deleteCountRecord(Long dateUtc, String timezone) {
		cache.deleteGeneralKey(CacheMode.T_WS_COUNT_FOR_DATE, dateUtc,timezone);
	}

	@Override
	public long createPlan(long loginId, String name, Long startDate, Long endDate,String timezone, String note) throws LogicException, DBException {
		uL.checkPerm(loginId, SelfXPerms.CREATE_WORKSHEET_PLAN);

		Plan plan = new Plan();
		plan.setName(name);
		plan.setNote(note);
		plan.setOwnerId(loginId);
		plan.setStartUtc(startDate);
		plan.setEndUtc(endDate);
		plan.setSeqWeight(0);
		/**
		 * 下面两行代码 顺序不能变：
		 * calculateStateByNow 依赖于 timezone
		 */
		plan.setTimezone(timezone);
		plan.setState(calculateStateByNow(plan));

		WorkContentConverter.addLog(plan, CareerLogAction.CREATE_PLAN,
				loginId,
				name,
				startDate,
				endDate,
				timezone,
				plan.getState().getDbCode());

		return addPlanSynchronously(plan,loginId);
	}

	@Override
	public void addItemToPlan(long adderId, long planId, String categoryName, int value, String note,
			PlanItemType type, int fatherId, double mappingVal) throws LogicException, DBException {
		Plan existed = getPlan(planId);
		if(existed.getOwnerId() != adderId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		WorkContentConverter.addItemToPlan(existed,adderId,categoryName,value,note,type,fatherId,mappingVal);
		updatePlanSynchronously(existed,adderId);
	}
	
	@Override
	public void addItemToWSPlan(long loginId, long wsId, String categoryName, int value, String note, PlanItemType type,
			int fatherId, double mappingVal) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		
		WorkContentConverter.addItemToWSPlan(ws, loginId, categoryName, value, note, type, fatherId, mappingVal);
		refreshStateAfterItemModified(ws);
		updateWorksheetSynchronously(ws,loginId);
	}

	@Override
	public void addItemToWS(long loginId, long wsId, int planItemId, double value, String note, int mood, boolean forAdd, Long startUtc, Long endUtc) {
		locker.lockByUserAndClass(loginId,()->{
			WorkSheet ws = getWorksheet(wsId);
			if(loginId != ws.getOwnerId()) {
				throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
			}

			WorkContentConverter.addItemToWorkSheet(ws, loginId, planItemId, value, note, mood, forAdd, startUtc, endUtc);
			refreshStateAfterItemModified(ws);
			updateWorksheetSynchronously(ws,loginId);
		});
	}

	@Override
	public void removeItemFromPlan(long loginId, long planId, int itemId) throws LogicException, DBException {
		Plan existed = getPlan(planId);
		if(existed.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.removeItemFromPlan(existed,loginId,itemId);
		updatePlanSynchronously(existed,loginId);
	}
	
	@Override
	public void removeItemFromWSPlan(long removerId, long wsId, int itemId) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(removerId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,removerId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.removeItemFromWSPlan(ws, removerId, itemId);
		
		refreshStateAfterItemModified(ws);
		updateWorksheetSynchronously(ws,removerId);
	}
	
	@Override
	public void removeItemFromWorkSheet(long loginId, long wsId, int itemId) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		
		WorkContentConverter.removeItemFromWorkSheet(ws, loginId, itemId);
		
		refreshStateAfterItemModified(ws);
		
		updateWorksheetSynchronously(ws,loginId);
	}

	@Override
	public void resetPlanTags(long loginId, long planId, List<String> tags) throws SMException {
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		List<EntityTag> entityTags = tags.stream().map(tag->new EntityTag(tag, false)).collect(toList());
		TagCalculator.checkTagsForReset(entityTags);
		plan.setTags(entityTags);
		updatePlanSynchronously(plan,loginId);
	}
	
	/**
	 * 当Reset时，已经确定这一个WorkSheet的所有标签，
	 * 现在的目的，仅仅是确定其中哪些是CreatedBySystem，哪些是CreatedByUser
	 * 则判断标准是 已有的CreatedBySystem 依旧，其它全是CreatedByUser
	 */
	@Override
	public void resetWorkSheetTags(long loginId,long wsId,List<String> tags) throws SMException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		
		List<EntityTag> old = ws.getTags();
		
		List<EntityTag> entityTags = tags.stream().map(tagName->{
			EntityTag one = new EntityTag();
			one.name = tagName;
			one.createdBySystem = old.stream().filter(t->t.createdBySystem).anyMatch(t->t.name.equals(tagName));
			return one;
		}).collect(toList());
		
		TagCalculator.checkTagsForReset(entityTags);
		
		ws.setTags(entityTags);

		updateWorksheetSynchronously(ws,loginId);
	}

	@Override
	public void savePlan(long loginId, long planId, String name, Long startDate
			, Long endDate,String timezone, String note, List<PlanSetting> settings, int seqWeight
			, boolean recalculateState
	) {
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}

		plan.setName(name);
		plan.setStartUtc(startDate);
		plan.setEndUtc(endDate);
		plan.setSetting(settings);
		plan.setSeqWeight(seqWeight);
		plan.setNote(note);
		plan.setTimezone(timezone);

		updatePlanSynchronously(plan,loginId);
		if(recalculateState){
			recalculatePlanState(loginId,planId);
		}
	}

	@Override
	public void recalculatePlanState(long loginId, long planId) {
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		PlanState stateByNow = calculateStateByNow(plan);
		if(stateByNow != plan.getState()) {
			WorkContentConverter.addLog(plan, CareerLogAction.STATE_CHENGED_DUE_TO_SAVING_PLAN, loginId,
					plan.getState().getDbCode(),
					stateByNow.getDbCode());
		}
		plan.setState(stateByNow);
		updatePlanSynchronously(plan,loginId);
	}

	@Override
	public void saveWorkSheetPlanId(long updaterId, long wsId, long planId) throws SMException {
		WorkSheet ws = getWorksheet(wsId);
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != updaterId) {
			throw new LogicException(SelfXErrors.CAREER_ACTION_ERROR,"不能把工作表的基准计划修改为他人的计划");
		}
		
		ws.setPlanId(planId);
		updateWorksheetSynchronously(ws,updaterId);
	}
	
	@Override
	public void savePlanItem(long loginId, long planId,int itemId , String catName, int value,String note, double mappingVal) throws LogicException, DBException {
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.updatePlanItem(plan, loginId, itemId, catName, value, note, mappingVal);

		updatePlanSynchronously(plan,loginId);
	}
	
	@Override
	public void savePlanItemFold(long loginId, long planId, int itemId, boolean fold) throws LogicException, DBException {
		Plan plan = cache.getEntity(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		
		WorkContentConverter.updatePlanItemFold(plan, loginId, itemId, fold);

		updatePlanSynchronously(plan,loginId);
	}

	@Override
	public void saveWSPlanItem(long loginId, long wsId, int itemId, String catName, int value, String note,
			double mappingVal) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		
		WorkContentConverter.updateWSPlanItem(ws, loginId, itemId, catName, value, note, mappingVal);
		refreshStateAfterItemModified(ws);
		updateWorksheetSynchronously(ws,loginId);
	}

	@Override
	public void saveWSPlanItemFold(long loginId, long wsId, int itemId, boolean fold) throws DBException, LogicException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,loginId+" vs "+ws.getOwnerId());
		}
		
		WorkContentConverter.updateWSPlanItemFold(ws, loginId, itemId, fold);
		updateWorksheetSynchronously(ws,loginId);
	}

	@Override
	public void patchBalanceItem(long loginId, int itemId, String name, double val){
		PlanBalance dept = getPlanBalance(loginId);
		WorkContentConverter.updatePlanDeptItem(dept, loginId, itemId, name, val);
		updatePlanDeptSynchronously(dept,loginId);
	}

	@Override
	public void abandonPlan(long loginId, long planId){
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}

		assert plan.getState() != PlanState.ABANDONED;
		boolean existsWSBaseThisPlan = wDAO.includeWorkSheetByPlanId(plan.getId());
		if(!existsWSBaseThisPlan) {
			cache.deleteEntityById(plan, (id)->wDAO.deleteExistedPlan(id));
			return;
		}

		PlanState after= PlanState.ABANDONED;
		assert plan.getState() != after;
		long endTime = System.currentTimeMillis();


		WorkContentConverter.addLog(plan, CareerLogAction.ABANDON_PLAN, loginId,
				plan.getState().getDbCode(),
				after.getDbCode(),
				plan.getEndUtc(),
				endTime);

		plan.setState(after);
		plan.setEndUtc(endTime);
		updatePlanSynchronously(plan,loginId);
	}

	@Override
	public void finishPlan(long loginId, long planId) throws LogicException, DBException {
		Plan plan = getPlan(planId);
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		PlanState after= PlanState.ABANDONED;
		assert plan.getState() != after;
		long endTime = System.currentTimeMillis();
		WorkContentConverter.addLog(plan, CareerLogAction.FINISH_PLAN, loginId,
				plan.getState().getDbCode(),
				after.getDbCode(),
				plan.getEndUtc(),
				endTime);

		plan.setState(after);
		plan.setEndUtc(endTime);
		updatePlanSynchronously(plan,loginId);
	}

	@Override
	public void calculateWorksheetStatesRoutinely(long loginId) {

		locker.lockByUserAndClass(loginId,()->{
			List<WorkSheet> toUpdate = wDAO.selectWorkSheetByOwnerAndStates(loginId
							,List.of(WorkSheetState.ACTIVE)).stream()
					.filter(one->calculateStateByNow(one) != one.getState())
					.collect(Collectors.toList());

			toUpdate.forEach(one->{
				WorkSheetState stateByNow =  calculateStateByNow(one);
				WorkContentConverter.addLog(one, CareerLogAction.WS_STATE_CHANGED_BY_DATE, SelfX.SYSTEM_ID,
						one.getState().getDbCode(),
						stateByNow.getDbCode());
				one.setState(stateByNow);
			});

			cache.saveEntities(toUpdate, (p)->wDAO.updateExistedWorkSheet(p));
		});
	}

	@Override
	public List<WorkSheet> loadWorkSheetInfosRecently(long operateId, int page) throws DBException, LogicException {
		return wDAO.selectWorkSheetInfoRecentlyByOwner(operateId, page, DEFAULT_WS_LIMIT_OF_ONE_PAGE);
	}

	@Override
	public List<WorkSheetProxy> loadWorkSheetsByDateScopeAndTimezone(long loginId, long startDate, long endDate, String timezone, Boolean regardingTimezone) {
		List<WorkSheet> wss =
				regardingTimezone ?
						wDAO.selectWorkSheetsByOwnerAndDateScopeAndTimezone(loginId, startDate, endDate,timezone)
						:
						wDAO.selectWorkSheetsByOwnerAndDateScope(loginId, startDate, endDate);

		List<WorkSheetProxy> rlt = fillPlanInfos(wss);

		for(WorkSheetProxy ws: rlt) {
			WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws.ws);
			calculateWSContentDetail(content);
			List<WorkItemProxy> itemsWithoutDeptItems = content.workItems.stream().filter(item->item.item.getType() != WorkItemType.DEBT).collect(toList());
			calculatePlanItemProxyDetail(content.planItems, itemsWithoutDeptItems);
			ws.finishPlanWithoutDeptItems = content.planItems.stream().allMatch(pItem->pItem.remainingValForCur <= 0.0);
			ws.content = content;
		}

		return clearUnnecessaryInfo(rlt);
	}
	@Override
	public WorkSheetProxy loadWorkSheet(long loginId, long wsId) throws DBException, LogicException {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_SEE_OTHERS_WS);
		}
		WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
		calculateWSContentDetail(content);
		
		WorkSheetProxy rlt = new WorkSheetProxy(ws);
		Plan plan = getPlan(rlt.ws.getPlanId());
		rlt.basePlanName = plan.getName();
		rlt.content = content;
		
		fill(rlt.content.logs); 
		
		return clearUnnecessaryInfo(rlt);
	}

	@Override
	public WorkSheet getWorksheet(long loginId, long wsId) {
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_SEE_OTHERS_WS);
		}
		return ws;
	}


	@Override
	public long getWorkSheetCount(long loginId, Long date, String timezone) {
		uL.checkPerm(loginId, SelfXPerms.SEE_TODAY_WS_COUNT);
		return Long.parseLong(cache.getGeneralValOrInit(CacheMode.T_WS_COUNT_FOR_DATE
				,()->String.valueOf(wDAO.countWorkSheetByDateAndTimezone(date,timezone))
				,date,timezone));
	}

	/**
	 * 只为预备和进行中状态的计划 重新计算状态
	 * @param loginId
	 */
	@Override
	public void calculatePlanStatesRoutinely(long loginId) {
		uL.checkPerm(loginId, SelfXPerms.SEE_SELF_PLANS);
		locker.lockByUserAndClass(loginId,()->{
			List<Plan> toUpdate = wDAO.selectPlansByOwnerAndStates(loginId,Arrays.asList(PlanState.ACTIVE,PlanState.PREPARED)).stream()
					.filter(one->calculateStateByNow(one) != one.getState())
					.collect(Collectors.toList());

			toUpdate.forEach(one->{
				PlanState stateByNow =  calculateStateByNow(one);
				WorkContentConverter.addLog(one, CareerLogAction.PLAN_STATE_CHANGED_BY_DATE,
						SelfX.SYSTEM_ID,
						one.getStartUtc(),
						one.getEndUtc(),
						one.getState().getDbCode(),
						stateByNow.getDbCode());
				one.setState(stateByNow);
			});

			cache.saveEntities(toUpdate, (p)->wDAO.updateExistedPlan(p));
		});
	}



	@Override
	public PlanProxy loadPlan(long loginId,long planId) {

		Plan plan = getPlan(planId);

		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_SEE_PLAN_OF_OTHERS);
		}

		PlanProxy proxy = new PlanProxy(plan);
	 	proxy.content = WorkContentConverter.convertPlanContent(plan);
	 	
	 	fill(proxy.content.logs);
	 	
		return proxy;
	}

	@Override
	public Map<String, Long> 	loadPlanStateStatistics(long ownerId) throws LogicException, DBException {
		Map<String,Long> rlt = new HashMap<>();
		
		for(PlanState state:PlanState.values()) {
			if(state == PlanState.UNDECIDED)
				continue;

			rlt.put(String.valueOf(state.getDbCode()), countPlansByOwnerAndState(ownerId, state));
		}
		
		return rlt;
	}

	private long countPlansByOwnerAndState(long ownerId,PlanState state){
		Map<String,Object> equals = new HashMap<>();
		equals.put(DBConstants.F_STATE,state);
		equals.put(DBConstants.F_OWNER_ID,ownerId);
		return wDAO.countPlansByTerms(null,equals,null,null);
	}
	
	@Override
	public Map<String, Long> loadWSStateStatistics(long loginId) throws LogicException, DBException {
		Map<String,Long> rlt = new HashMap<>();
		
		for(WorkSheetState state:WorkSheetState.values()) {
			if(state == WorkSheetState.UNDECIDED)
				continue;
			
			rlt.put(String.valueOf(state.getDbCode()), countWorkSheetByOwnerAndState(loginId, state));
		}
		
		return rlt;
	}

	private long countWorkSheetByOwnerAndState(long loginId,WorkSheetState state){
		Map<String,Object> equals = new HashMap<>();
		equals.put(DBConstants.F_STATE,state);
		equals.put(DBConstants.F_OWNER_ID,loginId);
		return wDAO.countWorksheetsByTerms(null,equals,null,null);
	}

	@Override
	public MultipleItemsResult<Plan> loadPlansByTerms(long loginId, Integer state, String name, Long startUtcForCreate, Long endUtcForCreate, Long startUtcForUpdate, Long endUtcForUpdate, String timezone) {
		Map<String,Object> likes = new HashMap<>();

		if(!CommonUtil.emptyJudge(name,String::isEmpty)){
			likes.put(DBConstants.F_NAME,name);
		}

		Map<String,Object> equals = new HashMap<>();
		equals.put(DBConstants.F_OWNER_ID,loginId);
		if(!CommonUtil.emptyJudge(state,s->s == 0)){
			equals.put(DBConstants.F_STATE,PlanState.valueOfDBCode(state));
		}

		if(!CommonUtil.emptyJudge(timezone,String::isEmpty)){
			equals.put(DBConstants.F_TIMEZONE,timezone);
		}

		Map<String,Object> greaterThan = new HashMap<>();

		if(!CommonUtil.emptyJudge(startUtcForCreate,s->s == 0)){
			greaterThan.put(DBConstants.F_CREATE_UTC,startUtcForCreate);
		}
		if(!CommonUtil.emptyJudge(startUtcForUpdate,s->s == 0)){
			greaterThan.put(DBConstants.F_UPDATE_UTC,startUtcForUpdate);
		}

		Map<String,Object> lessThan = new HashMap<>();
		if(!CommonUtil.emptyJudge(endUtcForCreate,s->s == 0)){
			lessThan.put(DBConstants.F_CREATE_UTC,endUtcForCreate);
		}
		if(!CommonUtil.emptyJudge(endUtcForUpdate,s->s == 0)){
			lessThan.put(DBConstants.F_UPDATE_UTC,endUtcForUpdate);
		}

		List<Plan> items = wDAO.selectPlansByTerms(likes,equals,greaterThan,lessThan);
		long count = wDAO.countPlansByTerms(likes,equals,greaterThan,lessThan);
		MultipleItemsResult<Plan> rlt = new MultipleItemsResult<>();
		rlt.items = items;
		rlt.count = count;
		return rlt;
	}

	@Override
	public MultipleItemsResult<WorkSheetProxy> loadWorksheetsByTerms(long loginId, Integer state, Long startUtcForDate, Long endUtcForDate, Long startUtcForUpdate, Long endUtcForUpdate, String timezone, long planId) {
		Map<String,Object> likes = new HashMap<>();

		Map<String,Object> equals = new HashMap<>();
		equals.put(DBConstants.F_OWNER_ID,loginId);
		if(!CommonUtil.emptyJudge(state,s->s == 0)){
			equals.put(DBConstants.F_STATE,WorkSheetState.valueOfDBCode(state));
		}

		if(!CommonUtil.emptyJudge(planId,s->s == 0)){
			equals.put(DBConstants.F_PLAN_ID,planId);
		}

		if(!CommonUtil.emptyJudge(timezone,String::isEmpty)){
			equals.put(DBConstants.F_TIMEZONE,timezone);
		}

		Map<String,Object> greaterThan = new HashMap<>();

		if(!CommonUtil.emptyJudge(startUtcForDate,s->s == 0)){
			greaterThan.put(DBConstants.F_DATE_UTC,startUtcForDate);
		}
		if(!CommonUtil.emptyJudge(startUtcForUpdate,s->s == 0)){
			greaterThan.put(DBConstants.F_UPDATE_UTC,startUtcForUpdate);
		}

		Map<String,Object> lessThan = new HashMap<>();
		if(!CommonUtil.emptyJudge(endUtcForDate,s->s == 0)){
			lessThan.put(DBConstants.F_DATE_UTC,endUtcForDate);
		}
		if(!CommonUtil.emptyJudge(endUtcForUpdate,s->s == 0)){
			lessThan.put(DBConstants.F_UPDATE_UTC,endUtcForUpdate);
		}

		List<WorkSheetProxy> items = clearUnnecessaryInfo(fillPlanInfos(wDAO.selectWorksheetsByTerms(likes,equals,greaterThan,lessThan)));
		long count = wDAO.countWorksheetsByTerms(likes,equals,greaterThan,lessThan);
		MultipleItemsResult<WorkSheetProxy> rlt = new MultipleItemsResult<>();
		rlt.items = items;
		rlt.count = count;
		return rlt;
	}

	@Override
	public PlanBalanceProxy getBalance(long loginId){
		PlanBalance balance = getPlanBalance(loginId);
		
		PlanBalanceProxy proxy = new PlanBalanceProxy(balance);
		
		proxy.content = WorkContentConverter.convertPlanDept(balance);
		
		fill(proxy.content.logs); 
		
		return proxy;
	}

	@Override
	public List<WorkSheetProxy> loadWorkSheetByState(long loginId, WorkSheetState stateZT){
		if(stateZT == WorkSheetState.UNDECIDED) {
			return fillPlanInfos(wDAO.selectWorkSheetByField(DBConstants.F_OWNER_ID, loginId));
		}
		return clearUnnecessaryInfo(fillPlanInfos(wDAO.selectWorkSheetByOwnerAndStates(loginId, Arrays.asList(stateZT))));
	}
	
	@Override
	public List<String> getPlanBalanceItemNames(long loginId){
		PlanBalance dept = cache.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginId, PlanBalance.class,
				 ()->wDAO.selectBalanceByOwner(loginId), ()->initPlanDept(loginId));
		BalanceContent content = WorkContentConverter.convertPlanDept(dept);
		return content.items.stream().map(BalanceItem::getName).collect(toList());
	}
	
	@Override
	public List<String> loadAllPlanTagsByUser(long loginId){
		
		List<String> tagStrs = wDAO.selectNonNullPlanTagsByUser(loginId);
		
		return tagStrs.stream().flatMap(tagStr->TagCalculator.parseToTags(tagStr).stream().map(tag->tag.name))
				.distinct().collect(toList());
	}
	
	@Override
	public List<String> loadAllWorkSheetTagsByUser(long loginId) throws SMException {
		
		List<String> tagStrs = wDAO.selectNonNullWorkSheetTagsByUser(loginId);
		
		return tagStrs.stream().flatMap(tagStr->TagCalculator.parseToTags(tagStr).stream().map(tag->tag.name))
				.distinct().collect(toList());
	}
	
	@Override
	public long openWorkSheetToday(long loginId, long planId){

		Plan plan = getPlan(planId);
		if(plan.getState() != PlanState.ACTIVE) {
			throw new LogicException(SelfXErrors.OPEN_WORK_BASE_WRONG_STATE_PLAN,plan.getState().getName());
		}
		if(plan.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_OPEN_OTHERS_PLAN,plan.getOwnerId()+":"+loginId);
		}

		LockHandler<Long> handler = new LockHandler<>();

		locker.lockByUserAndClass(loginId,()->{
			final String timezone = plan.getTimezone();
			long today = ZonedTimeUtils.getCurrentDateUtc(timezone);
			if(wDAO.includeUniqueWorkSheetByOwnerAndDateAndTimezone(loginId, today,timezone)) {
				throw new LogicException(SelfXErrors.OPEN_WORK_SHEET_SYNC_ERROR);
			}
			WorkSheet ws = new WorkSheet();
			ws.setOwnerId(loginId);
			ws.setDateUtc(today);
			ws.setTimezone(timezone);
			ws.setPlanId(planId);
			ws.setNote("");
			/*新创建的工作表的Tag和计划保持一致*/
			ws.setTags(plan.getTags());
			WorkContentConverter.pushToWorkSheet(plan, ws);
			ws.setState(calculateStateByNow(ws));
			WorkContentConverter.addLog(ws, CareerLogAction.OPEN_WS_TODAY, loginId,
					plan.getName(),ws.getState().getDbCode());
			handler.val = wDAO.insertWorkSheet(ws);
			deleteCountRecord(ws.getDateUtc(),ws.getTimezone());

		});
		return handler.val;
	}



	@Override
	public void saveWorkSheet(long updaterId, long wsId, String note) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(updaterId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,updaterId+" vs "+ws.getOwnerId());
		}
		ws.setNote(note);
		updateWorksheetSynchronously(ws,updaterId);
	}


	@Override
	public void deleteWorkSheet(long loginId, long wsId){
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,loginId+" vs "+ws.getOwnerId());
		}
		locker.lockByUserAndClass(loginId,()->{
			cache.deleteWorksheet(wsId,()->wDAO.deleteExistedWorkSheet(wsId));
			deleteCountRecord(ws.getDateUtc(),ws.getTimezone());
		});
	}

	@Override
	public void assumeWorkSheetFinished(long opreatorId, long wsId) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(opreatorId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,opreatorId+" vs "+ws.getOwnerId());
		}
		ws.setState(WorkSheetState.NO_MONITOR);
		updateWorksheetSynchronously(ws,opreatorId);
	}

	@Override
	public void cancelAssumeWorkSheetFinished(long opreatorId, long wsId) throws LogicException, DBException {
		WorkSheet ws = getWorksheet(wsId);
		if(opreatorId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,opreatorId+" vs "+ws.getOwnerId());
		}
		if(ws.getState() != WorkSheetState.NO_MONITOR) {
			throw new LogicException(SelfXErrors.CANNOT_CANCEL_WS_WHICH_NOT_ASSUMED,ws.getState().getName());
		}
		ws.setState(calculateStateByNow(ws));
		updateWorksheetSynchronously(ws,opreatorId);
	}
	

	/**
	 * 和前台配合 为了并发的安全 选择 从取的一刻就锁住
	 * 大概的背景是这样的：
	 * 1.前台的每一个item的更新会锁 但是不会回显
	 * 2.这样保证了item单个的更新不会出现并发问题，但是多个item之间会出现并发问题。
	 * 3.后台把取这一步也给加锁 相当于多个Item之间即便前台出现了并发问题 后台也不会出现并发问题
	 */
	@Override
	public void saveWorkItem(long loginId, int wsId,int itemId, double val, String note, int mood, boolean forAdd, Long startUtc, Long endUtc) {
		locker.lockByUserAndClass(loginId,()->{
			WorkSheet ws = getWorksheet(wsId);
			if(loginId != ws.getOwnerId()) {
				throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
			}
			WorkContentConverter.updateWorkItem(ws, loginId, itemId, val, note, mood, forAdd, startUtc, endUtc);
			refreshStateAfterItemModified(ws);
			updateWorksheetSynchronously(ws,loginId);
		});
	}



	@Override
	public void saveWorkItemPlanItemId(long loginId, long wsId, int workItemId, int planItemId){
		WorkSheet ws = getWorksheet(wsId);
		if(loginId != ws.getOwnerId()) {
			throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
		}
		WorkContentConverter.updateWorkItemPlanItemId(ws, loginId, workItemId, planItemId);;
		refreshStateAfterItemModified(ws);
		updateWorksheetSynchronously(ws,loginId);
	}
	
	private long initPlanDept(long ownerId) throws DBException {
		PlanBalance dept = new PlanBalance();
		dept.setOwnerId(ownerId);
		try {
			WorkContentConverter.addLog(dept, CareerLogAction.CREATE_PLAN_DEPT, SelfX.SYSTEM_ID);
		}catch (LogicException e) {
			/*这个函数不想让它抛逻辑异常 在这里处理*/
			throw new RuntimeException("initPlanDept error "+e.getMessage());
		}
		return wDAO.insertBalance(dept);
	}
	
	/**
	 * 要重新计算一下状态
	 */
	@Override
	public void syncToBalance(long loginId, long wsId, int planItemId) throws DBException, LogicException {
		locker.lockByUserAndClass(loginId,()->{
			WorkSheet ws = getWorksheet(wsId);
			if(loginId != ws.getOwnerId()) {
				throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS);
			}

			WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
			calculateWSContentDetail(content);

			Map<Integer,PlanItemNode> planItemsById = parseTo(content.planItems);
			if(!planItemsById.containsKey(planItemId)) {
				throw new LogicException(SelfXErrors.INCONSISTENT_WS_DATA,"缺失的wsPlanId"+planItemId);
			}
			PlanItemProxy planItem = planItemsById.get(planItemId).item;

			if(planItem.remainingValForCur == 0) {
				throw new LogicException(SelfXErrors.NO_SYNC_ZERO_WS_PLAN_ITEM,planItem.item.getName());
			}

			PlanBalance dept = getPlanBalance(loginId);
			WorkContentConverter.syncToPlanDept(ws,dept,planItem,loginId);
			refreshStateAfterItemModified(ws);

			updateWorksheetSynchronously(ws,loginId);
			updatePlanDeptSynchronously(dept,loginId);
		});
	}
	
	@Override
	public void syncAllToBalance(long loginId, long wsId) throws DBException, LogicException {
		locker.lockByUserAndClass(loginId,()->{
			WorkSheet ws = getWorksheet(wsId);
			if(loginId != ws.getOwnerId()) {
				throw new LogicException(SelfXErrors.CANNOT_MODIFY_OTHERS_WS,loginId+" vs "+ws.getOwnerId());
			}

			WorkSheetContent content = WorkContentConverter.convertWorkSheet(ws);
			calculateWSContentDetail(content);


			/*同步所有时，是以根节点为准 同步的*/
			List<PlanItemProxy> needingToSync = content.planItems.stream()
					.filter(item->item.remainingValForCur!=0).toList();

			if(needingToSync.isEmpty()) {
				logger.log(Level.WARNING,"同步所有却没有需要同步的，前台出现问题了？"+wsId);
			}

			PlanBalance dept = getPlanBalance(loginId);

			for(PlanItemProxy planItem :needingToSync) {
				WorkContentConverter.syncToPlanDept(ws,dept,planItem,loginId);
			}

			refreshStateAfterItemModified(ws);

			updateWorksheetSynchronously(ws,loginId);
			updatePlanDeptSynchronously(dept,loginId);
		});
	}
	
	@Override
	public void syncAllToBalanceInBatch(long loginId, List<Integer> wsIds) throws SMException {
		for(long wsId:wsIds) {
			syncAllToBalance(loginId, wsId);
		}
	}
	
	/**
	 * 为了尽量保留用户手动修改的工作表痕迹，计划标签的同步逻辑为：
	 * 只删除工作表中,由系统生成的Tag，替换成计划的标签
	 * 如果手动修改的标签 在计划的标签里，将标签由手动改为系统创建
	 */
	@Override
	public void syncPlanTagsToWorkSheet(long loginId, long planId) throws SMException {
		Plan target = cache.getEntity(CacheMode.E_ID, planId, Plan.class, ()->wDAO.selectExistedPlan(planId));
		if(target.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_SYNC_OTHERS_PLAN_TAGS);
		}
		locker.lockByUserAndClass(loginId,()->{
			List<WorkSheet> toSave = wDAO.selectWorkSheetByField(DBConstants.F_PLAN_ID, planId);

			for(WorkSheet workSheet : toSave) {
				List<EntityTag> tagsByPlan =  CommonUtil.cloneList(target.getTags(),tag->{
                    tag.createdBySystem = true;
					return tag;
				});
				/*万一有重复 认为该标签该是tagsFromPlan，即当同步之后，认为该标签是由系统创立的*/
				List<EntityTag> tagsByUser = workSheet.getTags().stream()
						.filter(tag->!tag.createdBySystem)
						.filter(tag->tagsByPlan.stream().noneMatch(tagByPlan->tag.name.equals(tagByPlan.name)))
						.toList();

				List<EntityTag> tagsForNew = new ArrayList<EntityTag>();
				tagsForNew.addAll(tagsByUser);
				tagsForNew.addAll(tagsByPlan);

				TagCalculator.checkTagsForReset(tagsForNew);
				workSheet.setTags(tagsForNew);
			}

			cache.saveEntities(toSave, p->wDAO.updateExistedWorkSheet(p));
		});
	}
	
	private List<WorkSheetProxy> fillPlanInfos(List<WorkSheet> src) throws DBException{
		List<WorkSheetProxy> rlt = src.stream().map(WorkSheetProxy::new).collect(toList());
		List<Long> planIds = src.stream().map(WorkSheet::getPlanId).distinct().collect(toList());
		Map<Long,Plan> relevantPlans =  wDAO.selectPlanInfosByIds(planIds).stream().collect(toMap(Plan::getId, Function.identity()));
		
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
	public void copyPlanItemsFrom(long loginId, long targetPlanId, long templatePlanId)
			throws DBException, LogicException {
		Plan target = getPlan(targetPlanId);
		if(target.getOwnerId() != loginId) {
			throw new LogicException(SelfXErrors.CANNOT_EDIT_OTHERS_PLAN);
		}
		Plan template = getPlan(templatePlanId);
		if(template.getOwnerId() !=loginId
				&& !template.hasSetting(PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS)) {
			throw new LogicException(SelfXErrors.CANNOT_COPY_OTHERS_PLAN_ITEMS);
		}
		
		WorkContentConverter.copyPlanItemsFrom(target, template, loginId);
		updatePlanSynchronously(target,loginId);
	}

	@Override
	public long getCountWSBasedOfPlan(Integer planId, long loginId) {
		return wDAO.countWorkSheetByOwnerAndPlanId(loginId, planId);
	}

	@Override
	public List<String> loadAllWorkSheetTimezones(long loginId) {
		return wDAO.getDistinctWorksheetTimezones(loginId);
	}


}
