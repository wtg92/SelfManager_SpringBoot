package manager.logic.career;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.data.career.StatisticsList;
import manager.data.career.WorkSheetContent;
import manager.data.career.WorkSheetContent.PlanItemNode;
import manager.data.proxy.career.CareerLogProxy;
import manager.data.proxy.career.PlanBalanceProxy;
import manager.data.proxy.career.PlanItemProxy;
import manager.data.proxy.career.PlanProxy;
import manager.data.proxy.career.WorkItemProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.User;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.entity.virtual.career.PlanItem;
import manager.entity.virtual.career.WorkItem;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.UserLogic;
import manager.logic.career.impl.WorkLogicImpl_Legacy;
import manager.logic.career.sub.LogParser;
import manager.logic.career.sub.WorkContentConverter;
import manager.system.SM;
import manager.system.career.CareerLogAction;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.WorkItemType;
import manager.system.career.WorkSheetState;
import manager.util.ZonedTimeUtils;

import javax.annotation.Resource;

public abstract class WorkLogic{

	@Resource
	protected UserLogic uL;
	final private static Logger logger = Logger.getLogger(WorkLogic.class.getName());

	private static WorkLogic instance = null;

	public static int DEFAULT_WS_LIMIT_OF_ONE_PAGE = 20;

	/**
	 * 检验权限
	 * 需要有Log
	 * endDate isNull means continue to today
	 * @throws DBException 
	 * @throws LogicException 
	 */
	@Deprecated
	public abstract long createPlan(long ownerId,String name,Calendar startDate,Calendar endDate,String note) throws LogicException, DBException;

	public abstract long createPlan(long ownerId,String name,Long startDate,Long endDate,String timezone,String note) throws LogicException, DBException;


	/**
	 * 可以暂且无权限，但是只能修改自己的
	 * 需要有Log
	 * @throws DBException 
	 * @throws LogicException 
	*/
	public abstract void addItemToPlan(long adderId,long planId,String categoryName,int value,String note,PlanItemType type,int fatherId,double mappingVal) throws LogicException, DBException;
	public abstract void addItemToWSPlan(long adderId,long wsId,String categoryName,int value,String note,PlanItemType type,int fatherId,double mappingVal) throws LogicException, DBException;

	public abstract void addItemToWS(long loginId, long wsId, int planItemId, double value, String note, int mood,boolean forAdd,Long startUtc, Long endUtc);


	/**
	  * 当remove时，会一起连子的Item一起remove掉
	 */
	public abstract void removeItemFromPlan(long removerId,long planId,int itemId) throws LogicException, DBException;
	public abstract void removeItemFromWSPlan(long removerId,long wsId,int itemId) throws LogicException, DBException;
	public abstract void removeItemFromWorkSheet(long removerId, long wsId, int itemId) throws LogicException, DBException;
	/**
	 *  加载出state 为 active和prepared 的plan  
	 *   从数据库取出后，会进行refreshStateByTime 这时如果更新为Finished 则会更新且不会返回 更新为Active会返回
	 * 取出后 如果不在缓存里，就放到缓存里（认为用户很快会使用） 
	 *  
	 */
	public abstract List<Plan> loadActivePlans(long loginId) throws LogicException, DBException;
	public abstract void calculatePlanStatesRoutinely(long loginId);

	public abstract void calculateWorksheetStatesRoutinely(long loginId);


	/**
	 * @return key state.dbCode.toString value count
	 */
	public abstract Map<String,Long> loadPlanStateStatistics(long ownerId) throws LogicException, DBException;
	public abstract Map<String,Long> loadWSStateStatistics(long loginId) throws LogicException, DBException;
	/*ZT means ZoerTerm 当是0时，代表不设限*/
	@Deprecated
	public abstract List<Plan> loadPlansByState(long ownerId,PlanState stateZT,int pageNum,int pageSize);

	public abstract StatisticsList<Plan> loadPlansByTerms(long loginId, Integer state, String name, Long startUtcForCreate, Long endUtcForCreate, Long startUtcForUpdate, Long endUtcForUpdate, String timezone);
	public abstract StatisticsList<WorkSheetProxy> loadWorksheetsByTerms(long loginId, Integer state, Long startUtcForDate, Long endUtcForDate, Long startUtcForUpdate, Long endUtcForUpdate, String timezone, long planId);


	public abstract List<WorkSheetProxy> loadWorkSheetByState(long loginId, WorkSheetState stateZT)  throws LogicException, DBException;
	/**
	 * 暂且只让人看到自己的
	 */
	public abstract PlanProxy loadPlan(long loginId,long planId) throws LogicException, DBException;
	
	/**
	 * dao 取四个字段 id dateUtc state timezone
	 *  对于取出来Active的WS(不包括今天) 进行状态计算
	 *  								2024-02-07
	 * @param page 从0开始
	 */
	public abstract List<WorkSheet> loadWorkSheetInfosRecently(long opreatorId,int page) throws DBException, LogicException;
	
	public abstract WorkSheetProxy loadWorkSheet(long loginId,long wsId) throws DBException, LogicException;
	public abstract WorkSheet getWorksheet(long loginId,long wsId);
	
	/**
	 *   闭区间
	 *
	 */
	@Deprecated
	public abstract List<WorkSheetProxy> loadWorkSheetsByDateScope(long loginId,Calendar startDate,Calendar endDate) throws SMException;
	
	@Deprecated
	public abstract long loadWorkSheetCount(long loginId,Calendar date) throws SMException;

	public abstract long getWorkSheetCount(long loginId,Long date,String timezone);


	public abstract List<String> loadAllPlanTagsByUser(long loginId) throws SMException;
	public abstract List<String> loadAllWorkSheetTagsByUser(long loginId) throws SMException;
	public abstract PlanBalanceProxy getBalance(long loginId) throws DBException, LogicException;
	public abstract List<String> getPlanBalanceItemNames(long loginId);
	/**
	  * 假如存在一个WorkItem 依据该Plan 建立(No Cache)，那么会将改plan的EndDate设为今天，并且将状态设置为abandon.
	  * 否则，直接删除
	 */
	public abstract void abandonPlan(long opreatorId,long planId) throws LogicException, DBException;
	/* 状态设置为Finished 改plan的EndDate设为今天*/
	public abstract void finishPlan(long opreatorId,long planId) throws LogicException, DBException;
	
	public abstract void resetPlanTags(long opreatorId,long planId,List<String> tags) throws SMException;
	public abstract void resetWorkSheetTags(long opreatorId,long wsId,List<String> tags) throws SMException;

	
	public abstract void saveWorkItemPlanItemId(long updaterId,long wsId,int workItemId, int planItemId) throws LogicException, DBException;

	@Deprecated
	public abstract void saveWorkItems(long loginId, long wsId, List<WorkItem> workItems) throws SMException;

	public abstract void saveWorkItem(long loginId, int wsId,int itemId, double val, String note, int mood, boolean forAdd, Long startUtc, Long endUtc);

	/**
	 * 之所以不允许修改类型及父ID 是因为 mappingval 是基于这两个值而设置的
	 * 如果修改了类型及父ID 则全部都没有了意义
	 */
	public abstract void savePlanItem(long loginId, long planId,int itemId,String catName,int value,String note,double mappingVal) throws LogicException, DBException;
	public abstract void savePlanItemFold(long loginId, long planId, int itemId, boolean fold) throws LogicException, DBException;
	public abstract void saveWSPlanItem(long loginId, long wsId,int itemId,String catName,int value,String note,double mappingVal) throws LogicException, DBException;
	public abstract void saveWSPlanItemFold(long loginId, long wsId, int itemId, boolean fold) throws DBException, LogicException;
	public abstract void savePlan(long loginId, long planId, String name, Long startDate, Long endDate,String timezone,
								  String note,List<PlanSetting> settings,int seqWeight, boolean recalculateState);
	public abstract void recalculatePlanState(long loginId, long planId);

	@Deprecated
	public abstract void savePlan(long loginId, long planId, String name, Calendar startDate, Calendar endDate,
			String note, boolean recalculateState, List<PlanSetting> settings,int seqWeight) throws LogicException, DBException;
	public abstract void saveWorkSheet(long updaterId,long wsId,String note) throws LogicException, DBException;	
	public abstract void patchBalanceItem(long updaterId, int itemId, String name, double val) throws LogicException, DBException;
	public abstract void saveWorkSheetPlanId(long updaterId,long wsId,long planId) throws SMException;	

	/**
	 * sync修饰
	  *  先检查今天没有工作表 否则抛并发异常
	  *  查找计划，假如计划非Active 抛异常 
	 * 
	 * @return workSheetId
	 */
	public abstract long openWorkSheetToday(long opreatorId,long planId) throws DBException, LogicException;
	
	public abstract void deleteWorkSheet(long deletorId,long wsId) throws DBException, LogicException;
	public abstract void assumeWorkSheetFinished(long opreatorId,long wsId) throws LogicException, DBException;
	public abstract void cancelAssumeWorkSheetFinished(long opreatorId,long wsId) throws LogicException, DBException;

	public abstract void syncToBalance(long loginId, long wsId, int planItemId) throws DBException, LogicException;
	public abstract void syncAllToBalance(long loginId, long wsId) throws DBException, LogicException;
	public abstract void syncAllToBalanceInBatch(long loginId, List<Integer> wsIds) throws SMException;
	public abstract void syncPlanTagsToWorkSheet(long loginId,long planId) throws SMException;
	public abstract void copyPlanItemsFrom(long loginId,long targetPlanId,long templatePlanId) throws DBException, LogicException;

	public abstract long getCountWSBasedOfPlan(Integer planId, long loginId);

	public abstract List<String> loadAllWorkSheetTimezones(long loginId);

	/**
	 *   闭区间
	 */
	public abstract List<WorkSheetProxy> loadWorkSheetsByDateScopeAndTimezone(long loginId, long startDate, long endDate, String timezone, Boolean regardingTimezone);


	/*=================================================NOT ABSTRACT ==============================================================*/
	


	public static synchronized WorkLogic getInstance() {
		if(instance == null) {
			instance = new WorkLogicImpl_Legacy();
		}
		return instance;
	}

	
	protected static List<WorkSheetProxy> clearUnnecessaryInfo(List<WorkSheetProxy> base){
		base.forEach(WorkLogic::clearUnnecessaryInfo);
		return base;
	}
	
	protected static WorkSheetProxy clearUnnecessaryInfo(WorkSheetProxy base) {
		base.ws.setContent(null);
		base.ws.setPlan(null);
		return base;
	}
	
	
	/**
	 * state 即便为ABANDON（用户手动废除）/Finished(时间到了或手动完成) 也会根据日期重新计算 假如不想令其其计算，应当上层处理
	 * plan的endDate is null 意味着永远实行
	 *  应当是只有在savePlan 和 loadActivePlans时触发状态的重新计算，如果Abandon ， 前者不会触发计算，后者通过用户选择来决定是否重新计算
	 * 
	 */
	public static PlanState calculateStateByNow(Plan plan) {

		ZoneId zone = ZoneId.of(plan.getTimezone());
		ZonedDateTime startDateZonedDateTime = Instant.ofEpochMilli(plan.getStartUtc()).atZone(zone);
		ZonedDateTime endDateZonedDateTime = Instant.ofEpochMilli(plan.getEndUtc()).atZone(zone);
		ZonedDateTime now = Instant.now().atZone(zone);

		if(ZonedTimeUtils.isAfterByDate(startDateZonedDateTime,now)){
			return PlanState.PREPARED;
		}

		if(plan.getEndUtc() == 0) {
			return PlanState.ACTIVE;
		}
		
		if(ZonedTimeUtils.isAfterByDate(now, endDateZonedDateTime)) {
			return PlanState.FINISHED;
		}
		
		return PlanState.ACTIVE;
	}
	

	protected static WorkSheetState calculateStateByNow(WorkSheet workSheet) throws LogicException {
		WorkSheetContent wsContent =  WorkContentConverter.convertWorkSheet(workSheet);
		calculateWSContentDetail(wsContent);
		return calculateStateByNow(workSheet, wsContent);
	}
	
	/**
	 * 当WS的PlanItem/WorkItem变化时调用，要当心应在调用setContent之后调用，因为这个需要加Log
	 * 假如是假定完成的 就不再重新计算了
	 * @return 返回是否修改了状态并添加了Log
	 */
	protected static boolean refreshStateAfterItemModified(WorkSheet workSheet) throws LogicException {
		if(workSheet.getState() == WorkSheetState.NO_MONITOR) {
			return false;
		}
		
		WorkSheetState origin = workSheet.getState();
		WorkSheetState stateByNow =  calculateStateByNow(workSheet);
		if(origin == stateByNow) {
			return false;
		}
		
		WorkContentConverter.addLog(workSheet, CareerLogAction.WS_STATE_CHENGED_DUE_TO_ITEM_MODIFIED, SM.SYSTEM_ID,
				workSheet.getState().getDbCode(),
				stateByNow.getDbCode());
		
		workSheet.setState(stateByNow);
		return true;
	}
	
	/**
	 *  假如今天比date还早 报一条errorLog（古怪数据）
	 *  假如这一天所有的planItem的remaingVal等于0 认为完成
	 *  >0  今天超过 则超期 <0 则进行中
	 *
	 *  ABCD
	 *   
	 * @throws LogicException 
	 */
	protected static WorkSheetState calculateStateByNow(WorkSheet workSheet,WorkSheetContent contentWithDetail){

		ZoneId zone = ZoneId.of(workSheet.getTimezone());

		if(contentWithDetail.planItems.stream().allMatch(planItem->planItem.remainingValForCur == 0)) {
			return WorkSheetState.FINISHED;
		}
		if(contentWithDetail.planItems.stream().anyMatch(planItem->planItem.remainingValForCur > 0)) {
			ZonedDateTime today = ZonedTimeUtils.getCurrentDate(zone);
			ZonedDateTime date = Instant.ofEpochMilli(workSheet.getDateUtc()).atZone(zone);
			if(ZonedTimeUtils.isAfterByDate(today,date)) {
				return WorkSheetState.OVERDUE;
			}
			if(ZonedTimeUtils.isBeforeByDate(today, date)) {
				logger.log(Level.WARNING,"诡异的数据，今天在workSheet的date之前"
						,ZonedTimeUtils.parseDate(today)+" vs "+ZonedTimeUtils.parseDate(date));
			}
			return WorkSheetState.ACTIVE;
		}
		
		return WorkSheetState.OVER_FINISHED;
	}
	
	/**
	 *    要看懂这个函数，先要理解WorkSheetContent需要求什么
	 *  主要是求三个值 
	 *  PlanItemProxy.sumValForWorkItems  : 对于某一个planItem节点，合并基于该item的workItems 统计出时间
	 *  PlanItemProxy.remainingValForCur :  对于root节点 设置的投入值，算差值，差值如果全部交由该节点消除，将会是多少
	 *  WorkItemProxy.remainingValAtStart ： 这一条workItem是基于多大的值开始进行计算的？
	 */
	protected static void calculateWSContentDetail(WorkSheetContent ws) {
		List<WorkItemProxy> accumaltorForWorkItem = new ArrayList<WorkItemProxy>();
		/* workItem按startTime排序 所以每一个workItem的remainingValForCur
		 *  就相当于把已遍历的workItem当做所有workItem求出的对应planItem的remainningVal值
		 * 但这里当心的是需要planItems克隆 不能影响到已经计算完毕的planItems
		 * 
		 * 这里的计算肯定存在多余（我只要某一root的相关结果，而这里求了所有），但由于相关计算过于复杂 就这么弄了。性能也影响不了多少
		 * */
		ws.workItems.sort(Comparator.comparing(item->item.item.getStartUtc()));
		ws.workItems.forEach(workItem->{
			List<PlanItemProxy> planItemsForTemp = ws.planItems.stream().map(PlanItemProxy::clone)
					.collect(toList());
			
			calculatePlanItemProxyDetail(planItemsForTemp, accumaltorForWorkItem);
			
			workItem.remainingValAtStart = parseTo(planItemsForTemp).values().stream()
					.map(node->node.item)
					.filter(planItem->planItem.item.getId().equals(workItem.item.getPlanItemId())).findAny().get().remainingValForCur;
			
			accumaltorForWorkItem.add(workItem);
		});
		calculatePlanItemProxyDetail(ws.planItems, ws.workItems);
	}
	
	
	protected static void calculatePlanItemProxyDetail(List<PlanItemProxy> planItems,List<WorkItemProxy> workItems) {
		/*planItem 知道所有root的 也就是所谓planItems*/
		Map<Integer,PlanItemNode> planItemsById = parseTo(planItems);
		/*对于root 减去所有workItem后剩余的时间 可以负数 代表超额了*/
		Map<Integer,Double> remainingValForRoot = new HashMap<>();
		planItems.forEach(item->remainingValForRoot.put(item.item.getId(),(double)item.item.getValue()));

		for(WorkItemProxy wItem:workItems) {
			int planItemId = wItem.item.getPlanItemId();
			assert planItemsById.containsKey(planItemId);
			PlanItemNode planForThisWork = planItemsById.get(planItemId);
			/*一直映射到root*/
			PlanItemNode cur = planForThisWork;
			double valueSum = (wItem.item.isForAdd() ? -1 : 1)*wItem.item.getValue();
			while(!cur.isRoot()) {
				/*这都是向前的映射*/
				assert cur.prev != null;
				valueSum = mapToFather(valueSum,cur.item.item,cur.prev.item.item.getType());
				cur = cur.prev;
			}
			int planItemIdForCur = cur.item.item.getId();
			remainingValForRoot.put(planItemIdForCur, remainingValForRoot.get(planItemIdForCur)-valueSum);
		}
		
		for(WorkItemProxy wItem:workItems) {
			if(wItem.item.getType() != WorkItemType.GENERAL)
				continue;
			
			int planItemId = wItem.item.getPlanItemId();
			assert planItemsById.containsKey(planItemId);
			PlanItemNode planForThisWork = planItemsById.get(planItemId);
			planForThisWork.item.sumValForWorkItems += wItem.item.getValue()*(wItem.item.isForAdd() ? -1 : 1);
		}
		
		/*所有的planItem 要找到root 看剩余多少 再映射回来*/
		for(PlanItemNode pNode: planItemsById.values()) {
			
			/*root的逆向链表，这里的prev其实应该改为next 但只是命名 就先忍受了*/
			PlanItemNode rootReverseNode = new PlanItemNode(pNode.item);
			
			/*找Root*/
			PlanItemNode root = pNode;
			while(!root.isRoot()) {
				root = root.prev;
				
				PlanItemNode tempReverseNode = rootReverseNode;
				rootReverseNode = new PlanItemNode(root.item);
				rootReverseNode.prev = tempReverseNode;
			}
			
			int rootId = root.item.item.getId();
			assert remainingValForRoot.containsKey(rootId);
			double remaingVal = remainingValForRoot.get(rootId);
			
			PlanItemNode curForReverseNode = rootReverseNode;
			/*一直到映射到当前节点 向后映射 这里不需要加本身 因为在计算root时已经计算过了*/
			while(curForReverseNode.item.item.getId() != pNode.item.item.getId()) {
				remaingVal = mapToSon(remaingVal,curForReverseNode.prev.item.item,curForReverseNode.item.item.getType());
				curForReverseNode = curForReverseNode.prev;
			}
			pNode.item.remainingValForCur = remaingVal;
		}
	}
	
	
	
	private static double mapToSon(double fatherVal, PlanItem sonItem, PlanItemType fatherType) {
		/*子 0.5的映射比 那现在给父亲 10分钟 相当于子的 20分钟*/
		if(sonItem.getType() == fatherType) {
			return sonItem.getMappingValue() == 0 ? 0 : fatherVal / sonItem.getMappingValue();
		}
		if(fatherType == PlanItemType.MINUTES) {
			assert sonItem.getType() == PlanItemType.TIMES;
			/*确定当下的fatherValue是分钟 现在给父亲 10分钟 假设映射为一次等于10分钟 那么返回值应当是1次*/
			return sonItem.getMappingValue() == 0? 0 :  fatherVal / sonItem.getMappingValue();
		}
		assert sonItem.getType() == PlanItemType.MINUTES;
		/*父亲是次数 当下的son是分钟 假如一次为10分钟 那么现在给父亲1次 子类应该得到10分钟*/
		return fatherVal  * sonItem.getMappingValue();
	}

	private static double mapToFather(double accumlateSonVal, PlanItem sonItem, PlanItemType fatherType) {
		if(sonItem.getType() == fatherType) {
			return accumlateSonVal * sonItem.getMappingValue();
		}
		if(fatherType == PlanItemType.MINUTES) {
			assert sonItem.getType() == PlanItemType.TIMES;
			/*确定当下的sonValue是次数*/
			return accumlateSonVal  * sonItem.getMappingValue();
		}
		assert sonItem.getType() == PlanItemType.MINUTES;
		/*当下的son是分钟 那需要除了*/
		return sonItem.getMappingValue() == 0 ? 0 : accumlateSonVal/sonItem.getMappingValue();
	}

	protected static Map<Integer, PlanItemNode> parseTo(List<PlanItemProxy> planItems) {
		Map<Integer,PlanItemNode> rlt = new HashMap<>();
		PlanItemNode father = null;
		for(PlanItemProxy item :planItems) {
			collectItem(item, father, rlt);
		}
		return rlt;
	}
	
	private static void collectItem(PlanItemProxy item,PlanItemNode father,Map<Integer,PlanItemNode> collection) {
		PlanItemNode now =  new PlanItemNode(item);
		now.prev = father;
		item.descendants.forEach(descItem->collectItem(descItem, now,collection));
		assert !collection.containsKey(item.item.getId());
		collection.put(item.item.getId(), now);
	}
	
	protected void fill(List<CareerLogProxy> logs) throws LogicException, DBException {
		List<Long> relevantUsers = logs.stream().map(proxy->proxy.log.getCreatorId()).distinct().filter(id->id!=SM.SYSTEM_ID).collect(toList());
		Map<Long,User> users = uL.getUsers(relevantUsers).stream().collect(toMap(User::getId, Function.identity()));
		assert users.size() == relevantUsers.size();
		
		for(CareerLogProxy log:logs) {
			try {
				log.creatorName = log.log.getCreatorId() == SM.SYSTEM_ID ? SM.SYSTEM_NAME : users.get(log.log.getCreatorId()).getNickName();
				log.info = LogParser.parse(log.log.getAction(), log.log.getParams());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
				log.info = "由于未知BUG，我们遗失了这条日志";
			}
		}
	}


}
