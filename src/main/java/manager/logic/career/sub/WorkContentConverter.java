package manager.logic.career.sub;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static manager.util.XMLUtil.findAllByTagWithFather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.util.ZonedTimeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import manager.data.career.PlanContent;
import manager.data.career.PlanDeptContent;
import manager.data.career.WorkSheetContent;
import manager.data.proxy.career.CareerLogProxy;
import manager.data.proxy.career.PlanItemProxy;
import manager.data.proxy.career.WorkItemProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.PlanDept;
import manager.entity.general.career.WorkSheet;
import manager.entity.virtual.career.CareerLog;
import manager.entity.virtual.career.PlanDeptItem;
import manager.entity.virtual.career.PlanItem;
import manager.entity.virtual.career.WorkItem;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.system.SM;
import manager.system.SMError;
import manager.system.career.CareerLogAction;
import manager.system.career.PlanItemType;
import manager.system.career.WorkItemType;
import manager.util.CommonUtil;
import manager.util.TimeUtil;
import manager.util.XMLUtil.ElementWithFather;

/**
  *  处理WorkSheet Plan content的解析及生成  
  *  理论上讲WorkSheet有关于XML的一切事情 都应交由该逻辑去做，上层不该管XML的问题
  * 有一个不小心有些混乱的地方:catId==planItmeId 两者说的是同一个东西
 * parse append 一一对应，当修改了某一属性 应当同时调整相关的两个函数 
 *  
 * WorkSheet:
 *  <ws>
 *   	<items p_key>
 *   		
 *   	</items>
 *      <logs p_key>
 *      </logs>
 *   </ws>
 *  Plan:
 *  <pl>
 *   	<items p_key>
 *   		<item>
 *              <item>
 *              </item>       
 *   		</item>
 *   		<item></item>
 *   	</items>
 *      <logs p_key>
 *      </logs>
 *  </pl>
 *  
 *  PlanDept:
 *  <pdp>
 *      <items p_key>
 *             同类型 不允许重名 只有一层
 *      </items>
 *      <logs />
 *  </pdp>
 *  
 */
public abstract class WorkContentConverter {
	
	final private static Logger logger = Logger.getLogger(WorkContentConverter.class.getName());
	
	/**
	 * 简写 T tag
	 *    A attribution
	 *    AE attribution for entity
	 *    AP attribution prefix
	 */
	private final static String T_PLAN = "pl";
	private final static String T_WORKSHEET = "ws";
	private final static String T_PLAN_DEPT = "pdp";
	
	private final static String T_ITEMS = "items";
	private final static String T_ITEM = "item";
	
	private final static String T_LOGS = "logs";
	private final static String T_LOG = "log";
	
	private final static String A_P_AUTO_INCREMENT_KEY = "p_key";
	private final static String A_ID = "id";
	private final static String A_NAME = "name";
	private final static String A_MAPPING_VAL= "mapping_val";
	private final static String A_FOLD = "fold";
	private final static String A_TYPE = "type";
	private final static String A_VALUE = "val";
	private final static String A_FOR_ADD = "f_add";
	private final static String A_MODE = "mode";

	private final static String A_CREATE_TIME= "c_time";
	private final static String A_CREATE_TIME_UTC= "c_time_utc";

	private final static String A_PARAMS = "params";
	private final static String A_ACTION = "ac";
	private final static String A_CREATOR_ID = "c_id";
	private final static String A_PLAN_ITEM_ID ="pl_item_id";

	@Deprecated
	private final static String A_START_TIME ="s_time";
	private final static String A_START_TIME_UTC ="s_time_utc";

	@Deprecated
	private final static String A_END_TIME ="e_time";

	private final static String A_END_TIME_UTC ="e_time_utc";
	
	private final static String PARAM_SPLITOR = "_";
	
	private final static int PRIMARY_KEY_INITIAL_VAL = 1;
	
	
	private final static int MAX_NUM_OF_PLAN_DEPT_LOG = 50;
	private final static int NUM_OF_CLEAR_PLAN_DEPT_LOG = 30;
	
	
	
	private static Document initPlan() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(T_PLAN);
		
		Element item =  root.addElement(T_ITEMS);
		item.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		
		Element log = root.addElement(T_LOGS);
		log.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		
		return doc;
	}
	
	private static Document initWorkSheet() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(T_WORKSHEET);
		
		Element items =  root.addElement(T_ITEMS);
		items.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		
		Element logs = root.addElement(T_LOGS);
		logs.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		return doc;
	}
	
	private static Document initPlanDept() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(T_PLAN_DEPT);
		Element items =  root.addElement(T_ITEMS);
		items.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		
		Element logs = root.addElement(T_LOGS);
		logs.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		return doc;
	}
	
	
	private static CareerLog parseLog(Element element) {
		assert element.getName().equals(T_LOG);
		CareerLogAction action = CareerLogAction.valueOfDBCode(Integer.parseInt(element.attributeValue(A_ACTION)));
		
		CareerLog log = new CareerLog(action,Integer.parseInt(element.attributeValue(A_CREATOR_ID)));
		log.setId(Integer.parseInt(element.attributeValue(A_ID)));
		log.setCreateTime(TimeUtil.parseTime(element.attributeValue(A_CREATE_TIME)));
		String[] params = element.attributeValue(A_PARAMS).split(PARAM_SPLITOR);
		log.setParams(new LinkedList<String>(Arrays.asList(params)));
		return log;	
	}
	
	private static PlanItem parsePlanItem(Element element) {
		PlanItem item = new PlanItem();
		item.setId(Integer.parseInt(element.attributeValue(A_ID)));
		item.setValue(Integer.parseInt(element.attributeValue(A_VALUE)));
		item.setNote(element.getText());
		item.setName(element.attributeValue(A_NAME));
		item.setMappingValue(Double.parseDouble(element.attributeValue(A_MAPPING_VAL)));
		item.setType(PlanItemType.valueOfDBCode(Integer.parseInt(element.attributeValue(A_TYPE))));
		
		String foldAttr = element.attributeValue(A_FOLD);
		item.setFold(foldAttr == null ? false : Boolean.parseBoolean(foldAttr));
		
		return item;
	}
	
	private static WorkItem parseWorkItem(Element element) {
		WorkItem item = new WorkItem();
		item.setId(Integer.parseInt(element.attributeValue(A_ID)));
		item.setNote(element.getText());
		item.setMood(Integer.parseInt(element.attributeValue(A_MODE)));
		item.setForAdd(Boolean.parseBoolean(element.attributeValue(A_FOR_ADD)));

		item.setStartTime(TimeUtil.parseTime(element.attributeValue(A_START_TIME)));
		item.setStartUtc(Long.parseLong(element.attributeValue(A_START_TIME_UTC)));

		item.setEndTime(TimeUtil.parseTime(element.attributeValue(A_END_TIME)));
		item.setEndUtc(Long.parseLong(element.attributeValue(A_END_TIME_UTC)));

		item.setPlanItemId(Integer.parseInt(element.attributeValue(A_PLAN_ITEM_ID)));
		item.setType(WorkItemType.valueOfDBCode(element.attributeValue(A_TYPE)));
		item.setValue(Double.parseDouble(element.attributeValue(A_VALUE)));
		return item;
	}
	
	private static PlanDeptItem parsePlanDeptItem(Element element){
		PlanDeptItem dept = new PlanDeptItem();
		dept.setId(Integer.parseInt(element.attributeValue(A_ID)));
		dept.setName(element.attributeValue(A_NAME));
		dept.setValue(Double.parseDouble(element.attributeValue(A_VALUE)));
		dept.setType(PlanItemType.valueOfDBCode(element.attributeValue(A_TYPE)));
		return dept;
	}
	
	private static int getPIdAndAutoIncrease(Element father) {
		int pId = Integer.parseInt(father.attributeValue(A_P_AUTO_INCREMENT_KEY));
		father.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(pId+1));
		return pId;
	}
	private static List<PlanDeptItem> getPlanDeptItems(Document deptDoc){
		Element items= deptDoc.getRootElement().element(T_ITEMS);
		List<PlanDeptItem> rlt = new ArrayList<>();
		for(Element ele : items.elements()) {
			if(!ele.getName().equals(T_ITEM)) {
				logger.log(Level.SEVERE,"PlanDeptItem 出现了非 Item的tag"+ele.getName());
				continue;
			}
			PlanDeptItem item = parsePlanDeptItem(ele);
			rlt.add(item);
		}
		return rlt;
	}
	
	private static List<WorkItem> getWorkItems(Document wsDoc) {
		Element items= wsDoc.getRootElement().element(T_ITEMS);
		List<WorkItem> rlt = new ArrayList<>();
		for(Element ele : items.elements()) {
			if(!ele.getName().equals(T_ITEM)) {
				continue;
			}
			WorkItem item = parseWorkItem(ele);
			rlt.add(item);
		}
		return rlt;
	}
	
	
	private static List<PlanItem> getPlanItems(Document doc){
		Element items= doc.getRootElement().element(T_ITEMS);
		return getPlanItems(items);
	}
	
	private static List<PlanItem> getPlanItems(Element root) {
		List<PlanItem> rlt = new ArrayList<PlanItem>();
		for(Element ele : root.elements()) {
			if(!ele.getName().equals(T_ITEM)) {
				continue;
			}
			PlanItem item = parsePlanItem(ele);
			item.setDescendants(getPlanItems(ele));
			rlt.add(item);
		}
		return rlt;
	}
	
	private static ElementWithFather getPlanItemById(Document planDoc, int itemId) throws LogicException {
		Element itemsElement= planDoc.getRootElement().element(T_ITEMS);
		return getPlanItemById(itemsElement, itemId);
	}
	
	/**
	 * itemId == 0  返回根节点 无father
	 */
	private static ElementWithFather getPlanItemById(Element root, int itemId) throws LogicException {
		if(itemId == 0) {
			return new ElementWithFather(null, root);
		}
		
		for(ElementWithFather item:findAllByTagWithFather(root, T_ITEM)) {
			if(Integer.parseInt(item.cur.attributeValue(A_ID))!=itemId) {
				continue;
			}
			return item;
		}
		throw new LogicException(SMError.CAREER_ACTION_ERROR,"planItem无法匹配id "+itemId+"\n"+root.asXML());
	}
	
	/**
	 * itemId == 0  返回根节点 无father
	 */
	private static Element getWorkItemById(Document wsContentDoc, int workItemId) throws LogicException {
		Element itemsElement= wsContentDoc.getRootElement().element(T_ITEMS);
		
		for(Element item:itemsElement.elements()) {
			if(Integer.parseInt(item.attributeValue(A_ID))!=workItemId) {
				continue;
			}
			return item;
		}
		throw new LogicException(SMError.CAREER_ACTION_ERROR,"wsItem无法匹配id "+workItemId+"\n"+wsContentDoc.asXML());
	}
	
	private static Element getPlanDeptItemById(Document deptDoc, int deptItemId) throws LogicException {
		Element itemsElement= deptDoc.getRootElement().element(T_ITEMS);
		
		for(Element item:itemsElement.elements()) {
			if(Integer.parseInt(item.attributeValue(A_ID))!=deptItemId) {
				continue;
			}
			return item;
		}
		throw new LogicException(SMError.CAREER_ACTION_ERROR,"planDept无法匹配id "+deptItemId+"\n"+deptDoc.asXML());
	}
	
	private static Element getPlanDeptItemByNameAndType(Element itemsElement,String name,PlanItemType type) throws NoSuchElement {
		for(Element item:itemsElement.elements()) {
			PlanDeptItem deptItem = parsePlanDeptItem(item);
			if(deptItem.getName().equals(name)
					&& deptItem.getType() == type) {
				return item;
			}
		}
		
		throw new NoSuchElement();
	}
	
	private static List<CareerLog> getLogs(Document doc){
		Element logsElement= doc.getRootElement().element(T_LOGS);
		return findAllByTag(logsElement, T_LOG).stream().map(WorkContentConverter::parseLog).collect(toList());
	}
	
	private static Document getDocumentOrInitIfNotExists(PlanDept one) throws LogicException {
		if(one.getContent() == null || one.getContent().length() == 0) {
			return initPlanDept();
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT,"WorkSheet 解析xml 失败 "+one.getId());
		}
	}
	
	
	private static Document getDocumentOrInitIfNotExists(WorkSheet one) throws LogicException {
		if(one.getContent() == null || one.getContent().length() == 0) {
			return initWorkSheet();
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT,"WorkSheet 解析xml 失败 "+one.getId());
		}
	}
	
	private static Document getDocumentOrInitIfNotExists(Plan one) throws LogicException {
		if(one.getContent() == null) {
			return initPlan();
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WL_ENTITY_CONTENT,"Plan 解析xml 失败 "+one.getId());
		}
	}
	
	private static Document getDefinatePlanDocument(WorkSheet one) throws LogicException {
		Document content = null;
		if (one.getPlan() == null) {
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT, "无plan的ws"+one.getId());
		}
		try {
			content = DocumentHelper.parseText(one.getPlan());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT, "WS 解析 plan 失败 " + one.getId());
		}
		return content;
	}
	
	private static Document getDefinateDocument(WorkSheet one) throws LogicException {
		if (one.getContent() == null) {
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT, "无content的ws"+one.getId());
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT, "WS 解析content 失败 " + one.getId());
		}
	}
	
	
	private static Document getDefinateDocument(Plan one) throws LogicException {
		if (one.getContent() == null) {
			throw new LogicException(SMError.ILLEGAL_WL_ENTITY_CONTENT, "使用没有init过的plan Item");
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WL_ENTITY_CONTENT, "Plan 解析xml 失败 " + one.getId());
		}
	}
	
	private static Document getDefinateDocument(PlanDept one) throws LogicException {
		if (one.getContent() == null) {
			throw new LogicException(SMError.ILLEGAL_WL_ENTITY_CONTENT, "使用没有init过的plan dept");
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.ILLEGAL_WL_ENTITY_CONTENT, "PlanDept 解析xml 失败 " + one.getId());
		}
	}
	
	private static void fillAttrsExceptId(PlanDeptItem item,Element cur) {
		cur.addAttribute(A_VALUE, item.getValue().toString());
		cur.addAttribute(A_NAME, item.getName());
		cur.addAttribute(A_TYPE, String.valueOf(item.getType().getDbCode()));
	}
	
	private static void fillAttrsExceptId(PlanItem item,Element cur) {
		cur.addAttribute(A_VALUE, item.getValue().toString());
		cur.setText(item.getNote());
		cur.addAttribute(A_NAME, item.getName());
		cur.addAttribute(A_MAPPING_VAL,item.getMappingValue().toString());
		cur.addAttribute(A_TYPE, String.valueOf(item.getType().getDbCode()));
		cur.addAttribute(A_FOLD, String.valueOf(item.isFold()));
	}
	
	private static void fillAttrsExceptId(WorkItem item,Element cur) {
		cur.addAttribute(A_PLAN_ITEM_ID, item.getPlanItemId().toString());
		cur.addAttribute(A_MODE, item.getMood().toString());
		cur.addAttribute(A_FOR_ADD, item.isForAdd().toString());

		cur.addAttribute(A_START_TIME, TimeUtil.parseTime(item.getStartTime()));
		cur.addAttribute(A_START_TIME_UTC, item.getStartUtc().toString());

		cur.addAttribute(A_END_TIME, TimeUtil.parseTime(item.getEndTime()));
		cur.addAttribute(A_END_TIME_UTC, item.getEndUtc().toString());

		cur.addAttribute(A_TYPE, String.valueOf(item.getType().getDbCode()));
		cur.addAttribute(A_VALUE, item.getValue().toString());
		/*note 有换行符  只能用text存储*/
		cur.setText(item.getNote());
	}
	
	static CareerLog append(CareerLog log,Element father) {
		assert father.getName().equals(T_LOGS);
		assert log.getCreatorId() != null;
		int pId = getPIdAndAutoIncrease(father);
		Element cur = father.addElement(T_LOG);
		cur.addAttribute(A_ID, String.valueOf(pId));
		cur.addAttribute(A_CREATE_TIME, TimeUtil.parseTime(log.getCreateTime()));
		cur.addAttribute(A_ACTION, String.valueOf(log.getAction().getDbCode()));
		cur.addAttribute(A_CREATOR_ID, log.getCreatorId().toString());
		assert log.getParams().stream().allMatch(param->!param.contains(PARAM_SPLITOR)) : "Log参数不允许包含param splitor 否则会引发错误";
		cur.addAttribute(A_PARAMS, log.getParams().stream().collect(joining(PARAM_SPLITOR)));
		log.setId(pId);
		return log;	
	}

	/*plan的item是有嵌套的 因此必须传入idLoc 该element上记录id的自增信息 */
	static PlanItem append(PlanItem item,Element father,Element idLoc) {
		assert father.getName().equals(T_ITEMS) || father.getName().equals(T_ITEM);
		int pId = getPIdAndAutoIncrease(idLoc);
		Element cur = father.addElement(T_ITEM);
		cur.addAttribute(A_ID, String.valueOf(pId));
		item.setId(pId);
		fillAttrsExceptId(item,cur);
		return item;
	}
	
	static WorkItem append(WorkItem item,Element itemsDoc) {
		assert itemsDoc.getName().equals(T_ITEMS);
		int pId = getPIdAndAutoIncrease(itemsDoc);
		
		Element cur = itemsDoc.addElement(T_ITEM);
		cur.addAttribute(A_ID, String.valueOf(pId));
		item.setId(pId);
		
		fillAttrsExceptId(item, cur);
		return item;
	}
	
	static PlanDeptItem append(PlanDeptItem item,Element itemsDoc) {
		assert itemsDoc.getName().equals(T_ITEMS);
		int pId = getPIdAndAutoIncrease(itemsDoc);
		
		Element cur = itemsDoc.addElement(T_ITEM);
		cur.addAttribute(A_ID, String.valueOf(pId));
		item.setId(pId);
		
		fillAttrsExceptId(item, cur);
		return item;
	}
	
	
	private static void checkPlanItemNameNoDup(Element itemsElement,String itemName) throws LogicException {
		List<PlanItem> items = findAllByTag(itemsElement, T_ITEM).stream().map(WorkContentConverter::parsePlanItem).collect(toList());
		
		if(items.stream().anyMatch(item->item.getName().equals(itemName.strip()))) {
			throw new LogicException(SMError.FORBID_DUP_CAT_IN_PLAN,itemName);
		}
	}
	
	private static void checkPlanItemIdExisted(Document planDoc, int planItemIdForCat) throws LogicException {
		List<PlanItem> itmes = findAllByTag(planDoc.getRootElement().element(T_ITEMS), T_ITEM).stream().map(WorkContentConverter::parsePlanItem).collect(toList());
		if(itmes.stream().allMatch(item->item.getId() != planItemIdForCat)) {
			throw new LogicException(SMError.INCONSISTANT_WS_DATA,"无法找到对应 planItem 的id "+planItemIdForCat);
		}
	}
	
	/**
	 * TODO adderId 没有用到 先不改API 未来或许能用到
	 * 
	 * @param planItemId itemId 
	 * @param value 可以为0 进行中
	 * @param startTime 不可以为0 前台再添加的时候 就赋值
	 * @param endTime 可以为空 进行中
	 * @throws LogicException
	 */
	@Deprecated
	public static int addItemToWorkSheet(WorkSheet one,long adderId, int planItemId, int value, String note, int mood,boolean forAdd, Calendar startTime,Calendar endTime) throws LogicException {
		Document wsDoc = getDocumentOrInitIfNotExists(one);
		
		Document planDoc = getDefinatePlanDocument(one);
		checkPlanItemIdExisted(planDoc,planItemId);
		
		Element itemsRoot= wsDoc.getRootElement().element(T_ITEMS);
		
		WorkItem item = new WorkItem();
		
		item.setValue((double)value);
		item.setForAdd(forAdd);
		item.setMood(mood);
		item.setNote(note);
		item.setStartTime(startTime);
		item.setEndTime(endTime);
		item.setPlanItemId(planItemId);
		
		item.setType(WorkItemType.GENERAL);
		
		append(item,itemsRoot);
		
		one.setContent(wsDoc.asXML());
		
		assert item.getId() !=0;
		return item.getId();
	}

	public static int addItemToWorkSheet(WorkSheet one,long adderId, int planItemId, double value, String note, int mood,boolean forAdd, Long startUtc, Long endUtc) throws LogicException {
		Document wsDoc = getDocumentOrInitIfNotExists(one);

		Document planDoc = getDefinatePlanDocument(one);
		checkPlanItemIdExisted(planDoc,planItemId);

		Element itemsRoot= wsDoc.getRootElement().element(T_ITEMS);

		WorkItem item = new WorkItem();

		item.setValue(value);
		item.setForAdd(forAdd);
		item.setMood(mood);
		item.setNote(note);
		item.setStartUtc(startUtc);
		item.setEndUtc(endUtc);
		item.setPlanItemId(planItemId);

		item.setType(WorkItemType.GENERAL);

		checkStartUtcAndEndUtcLegal(item,one);

		append(item,itemsRoot);

		one.setContent(wsDoc.asXML());

		assert item.getId() !=0;
		return item.getId();
	}



	/**
	  *  先检查Category 内 有没有 categoryName 有的话 抛异常（不允许重复，要不然会导致混乱） 没有的话 添加
	 *   添加Log 
	 */
	public static int addItemToPlan(Plan one,long adderId, String categoryName, int value, String note, PlanItemType type,int fatherId, double mappingVal) throws LogicException {
		Document content = getDocumentOrInitIfNotExists(one);
		int itemId = addItemToPlanDoc(content,content,adderId, categoryName, value, note, type, fatherId, mappingVal);
		one.setContent(content.asXML());
		return itemId;
	}
	
	public static int addItemToWSPlan(WorkSheet one,long adderId, String categoryName, int value, String note, PlanItemType type,int fatherId, double mappingVal) throws LogicException {
		Document plan = getDefinatePlanDocument(one);
		Document ws = getDocumentOrInitIfNotExists(one);
		int itemId = addItemToPlanDoc(plan,ws,adderId, categoryName, value, note, type, fatherId, mappingVal);
		one.setPlan(plan.asXML());
		one.setContent(ws.asXML());
		return itemId;
	}
	
	private static int addItemToPlanDoc(Document content,Document logsDoc,long adderId , String categoryName, int value, String note, PlanItemType type,int fatherId, double mappingVal) throws LogicException {
		Element itemsRoot= content.getRootElement().element(T_ITEMS);
		checkPlanItemNameNoDup(itemsRoot, categoryName);
		
		Element planItem = getPlanItemById(itemsRoot,fatherId).cur;
		
		PlanItem item = new PlanItem();
		item.setValue(value);
		item.setNote(note);
		item.setName(categoryName);
		item.setMappingValue(mappingVal);
		item.setType(type);
		
		append(item, planItem,itemsRoot);
		
		/*由于item这里本身的复杂度 log需要找到父元素才行*/
		if(fatherId == 0) {
			addLog(logsDoc, CareerLogAction.ADD_ROOT_ITEM_TO_PLAN,adderId,categoryName,value,type.getDbCode());
		}else {
			assert planItem != itemsRoot;
			PlanItem father =  parsePlanItem(planItem);
			addLog(logsDoc, CareerLogAction.ADD_SON_ITEM_TO_PLAN,adderId,categoryName,mappingVal,type.getDbCode(),father.getName()
						,father.getType().getDbCode());
		}
		
		assert item.getId() !=0;
		return item.getId();
	}
	
	
	public static void addLog(Plan root,CareerLogAction action,long creatorId,Object ...parms) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(root);
		addLog(doc, action, creatorId, parms);
		root.setContent(doc.asXML());
	}
	
	public static void addLog(WorkSheet root,CareerLogAction action,long creatorId,Object ...parms) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(root);
		addLog(doc, action, creatorId, parms);
		root.setContent(doc.asXML());
	}
	
	public static void addLog(PlanDept root,CareerLogAction action,long creatorId,Object ...parms) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(root);
		addPlanDpetLog(doc, action, creatorId, parms);
		root.setContent(doc.asXML());
	}
	
	/**
	 * planDept这里有点特殊 假如Log到了上限 需要先清空较早的一定数量Log 再添加Log
	 */
	private static void addPlanDpetLog(Document deptDoc,CareerLogAction action,long creatorId,Object ...parms) {
		Element logsElement= deptDoc.getRootElement().element(T_LOGS);
		if(logsElement.elements().size()<= MAX_NUM_OF_PLAN_DEPT_LOG) {
			addLog(deptDoc,action,creatorId,parms);
			return;
		}

		List<Element> allLogs = logsElement.elements();
		assert NUM_OF_CLEAR_PLAN_DEPT_LOG < allLogs.size();
		for(int i=0;i<NUM_OF_CLEAR_PLAN_DEPT_LOG;i++) {
			logsElement.remove(allLogs.get(i));
		}
		
		List<PlanDeptItem> items = getPlanDeptItems(deptDoc);
		String snapShot = items.stream().map(item->LogParser.getSnapshot(item)).collect(joining(","));
		addLog(deptDoc, CareerLogAction.CLEAR_DEPT_LOGS_WHEN_TOO_MUCH, SM.SYSTEM_ID,
				MAX_NUM_OF_PLAN_DEPT_LOG,
				NUM_OF_CLEAR_PLAN_DEPT_LOG,
				snapShot);
		
		addLog(deptDoc,action,creatorId,parms);
	}
	
	
	private static void addLog(Document root,CareerLogAction action,long creatorId,Object ...parms) {
		Element logsElement= root.getRootElement().element(T_LOGS);
		CareerLog log = new CareerLog(action,creatorId);
		log.addParams(parms);
		log.setCreateTime(TimeUtil.getCurrentTime());
		append(log, logsElement);
	}
	
	/**
	 * 当修改 categoryName value mappingVal两个值才会产生Log 否则只是更新
	 * 其它的值不允许修改
	 */
	private static void updatePlanItem(Document content,Document logsDoc,long updaterId,int itemId, String categoryName, int value, String note, double mappingVal) throws LogicException {
		Element itemsElement= content.getRootElement().element(T_ITEMS);
		ElementWithFather planItem = getPlanItemById(itemsElement, itemId);
		
		assert planItem.father != null;
		
		PlanItem origin = parsePlanItem(planItem.cur);
		
		if(!origin.getName().equals(categoryName)) {
			/*说明改类别名了*/
			checkPlanItemNameNoDup(itemsElement, categoryName);
		}
		
		/*先处理Log 因为需要用origin*/
		if(!origin.getName().equals(categoryName)
				|| origin.getValue() != value
				|| origin.getMappingValue() != mappingVal
				) {
			
			if(planItem.father == itemsElement) {
				/*root Item*/
				addLog(logsDoc, CareerLogAction.UPDATE_ROOT_PLAN_ITEM,
						updaterId,
						origin.getType().getDbCode(),
						origin.getName(),
						origin.getValue(),
						categoryName,
						value);				
				
			}else {
				/*son item*/
				assert planItem.father != null;
				PlanItem fatherItem = parsePlanItem(planItem.father);
				addLog(logsDoc, CareerLogAction.UPDATE_SON_PLAN_ITEM,
						updaterId,
						fatherItem.getName(),
						fatherItem.getType().getDbCode(),
						origin.getType().getDbCode(),
						origin.getName(),
						origin.getMappingValue(),
						categoryName,
						mappingVal);			
			}

		}
		
		origin.setName(categoryName);
		origin.setValue(value);
		origin.setNote(note);
		origin.setMappingValue(mappingVal);
		
		fillAttrsExceptId(origin, planItem.cur);
		
	}
	
	public static void updatePlanItem(Plan one,long updaterId,int itemId, String categoryName, int value, String note, double mappingVal) throws LogicException{
		Document content = getDefinateDocument(one);
		updatePlanItem(content, content, updaterId, itemId, categoryName, value, note, mappingVal);
		one.setContent(content.asXML());
	}
	public static void updatePlanItemFold(Plan one, long loginerId, int itemId, boolean fold) throws LogicException {
		Document content = getDefinateDocument(one);
		updatePlanItemFold(content,itemId,fold);
		one.setContent(content.asXML());
	}
	
	private static void updatePlanItemFold(Document content, int itemId, boolean fold) throws LogicException {
		Element itemsElement= content.getRootElement().element(T_ITEMS);
		ElementWithFather planItem = getPlanItemById(itemsElement, itemId);
		
		assert planItem.father != null;
		
		PlanItem origin = parsePlanItem(planItem.cur);
		origin.setFold(fold);
		fillAttrsExceptId(origin, planItem.cur);
	}

	public static void updateWSPlanItem(WorkSheet one,long updaterId,int itemId, String categoryName, int value, String note, double mappingVal) throws LogicException{
		Document ws = getDocumentOrInitIfNotExists(one);
		Document plan = getDefinatePlanDocument(one);
		updatePlanItem(plan, ws, updaterId, itemId, categoryName, value, note, mappingVal);
		one.setContent(ws.asXML());
		one.setPlan(plan.asXML());
	}
	
	public static void updateWSPlanItemFold(WorkSheet one, long loginerId, int itemId, boolean fold) throws LogicException {
		Document plan = getDefinatePlanDocument(one);
		updatePlanItemFold(plan, itemId, fold);
		one.setPlan(plan.asXML());
	}
	
	/**
	 * 切换计划项与已有计划项计数类型不同时，计数值会置为0
	 */
	public static void updateWorkItemPlanItemId(WorkSheet one,long updaterId,int workItemId, int planItemId) throws LogicException{
		Document ws = getDefinateDocument(one);
		Element targetWorkItem = getWorkItemById(ws, workItemId);
		WorkItem origin = parseWorkItem(targetWorkItem);
		
		Document plan = getDefinatePlanDocument(one);
		PlanItem old = parsePlanItem(getPlanItemById(plan, origin.getPlanItemId()).cur);
		PlanItem toUpdateOne =  parsePlanItem(getPlanItemById(plan, planItemId).cur);
		
		if(old.getType() != toUpdateOne.getType()) {
			origin.setValue(0.0);
		}
		
		origin.setPlanItemId(planItemId);
		
		fillAttrsExceptId(origin, targetWorkItem);
		one.setContent(ws.asXML());
	}
	
	
	public static void updateWorkItem(WorkSheet one,long updaterId,int workItemId, double value, String note, int mood,boolean forAdd,Long startTime,Long endTime) throws LogicException{
		Document ws = getDefinateDocument(one);
		Element targetWorkItem = getWorkItemById(ws, workItemId);
		WorkItem origin = parseWorkItem(targetWorkItem);
		origin.setValue(value);
		origin.setNote(note);
		origin.setMood(mood);
		origin.setForAdd(forAdd);
		origin.setStartUtc(startTime);
		origin.setEndUtc(endTime);
		
		checkStartUtcAndEndUtcLegal(origin,one);
		
		fillAttrsExceptId(origin, targetWorkItem);
		one.setContent(ws.asXML());
	}

	/**
	 *  workItems 年月日是由对应的ws决定的
	 *  所以item的startUtc和endUtc 只体现 时分
	 *  但其中又包含了日期的信息
	 *  因此我需要校验 他们必须在同一天
	 *  这是不必要的检验 但是前端太容易出错了
	 */
	private static void checkStartUtcAndEndUtcLegal(WorkItem origin, WorkSheet one) {
		if(origin.getStartUtc() != 0
			&& ZonedTimeUtils.isNotSameByDate(one.getTimezone(),one.getDateUtc(),origin.getStartUtc())){
			throw new LogicException(SMError.UNEXPECTED_ERROR);
		}
		if(origin.getEndUtc() != 0
				&& ZonedTimeUtils.isNotSameByDate(one.getTimezone(),one.getDateUtc(),origin.getEndUtc())
		){
			throw new LogicException(SMError.UNEXPECTED_ERROR);
		}
	}


	/**
	 * 	假如存在同名项 则合并 假如最后为0 则删除。
	 * 
	 */
	public static void updatePlanDeptItem(PlanDept one,long updaterId,int itemId, String name,double val) throws LogicException{
		Document deptDoc = getDefinateDocument(one);
		Element deptItemEle = getPlanDeptItemById(deptDoc, itemId);
		PlanDeptItem item = parsePlanDeptItem(deptItemEle);
		if(item.getName().equals(name)) {
			/*说明没改名 那么不存在合并的问题 只用考虑是否为0既可*/
			addPlanDpetLog(deptDoc, CareerLogAction.MODIFY_DEPT_ITEM_VAL, updaterId,
					item.getType().getDbCode(),
					item.getName(),
					CommonUtil.fixDouble(item.getValue()),
					val);
			
			item.setValue(val);
				
			overridePlanDeptItemOrDeleteWhenValZero(deptDoc, item, deptItemEle);
			
			one.setContent(deptDoc.asXML());
			return;
		}
		
		Element itemsElement= deptDoc.getRootElement().element(T_ITEMS);
		
		try {
			Element theEleToMerge = getPlanDeptItemByNameAndType(itemsElement, name, item.getType());

			boolean deleteSuccess = itemsElement.remove(deptItemEle);
			if(!deleteSuccess) {
				throw new LogicException(SMError.SYNC_ITEM_WITH_DEPT_ERROR,"删除欠账项失败 "+item.getName());
			}

			PlanDeptItem theItemToMerge = parsePlanDeptItem(theEleToMerge);
			
			double after = CommonUtil.fixDouble(theItemToMerge.getValue()+val);
			
			addPlanDpetLog(deptDoc, CareerLogAction.MODIFY_DEPT_ITEM_NAME_CAUSE_MERGE, updaterId,
					item.getType().getDbCode(),
					item.getName(),
					CommonUtil.fixDouble(item.getValue()),
					name,
					val,
					theItemToMerge.getName(),
					CommonUtil.fixDouble(theItemToMerge.getValue()),
					after);
			
			theItemToMerge.setValue(after);
			overridePlanDeptItemOrDeleteWhenValZero(deptDoc, theItemToMerge, theEleToMerge);
			
			one.setContent(deptDoc.asXML());
			
		} catch (NoSuchElement e) {
			
			addPlanDpetLog(deptDoc, CareerLogAction.MODIFY_DEPT_ITEM_VAL_AND_NAME, updaterId,
					item.getType().getDbCode(),
					item.getName(),
					CommonUtil.fixDouble(item.getValue()),
					name,
					val);
			
			item.setName(name);
			item.setValue(val);
				
			overridePlanDeptItemOrDeleteWhenValZero(deptDoc, item, deptItemEle);
			
			one.setContent(deptDoc.asXML());
		}
	}
	
	
	/**
	 * itemId找不到 抛异常（虽然可能有并发问题，但是它为什么要并发呢？） CAREER_ACTION_ERROR
	 * 
	  *  删掉item时，会同时remove掉子Item 
	 * @throws LogicException 
	 * 
	 * 
	 */
	private static void removeItemFromPlanDoc(Document plan,Document logsDoc,long removerId, int itemId) throws LogicException {
		ElementWithFather planItem = getPlanItemById(plan, itemId);
		planItem.removeCurFromFather();
		
		PlanItem theRemove = parsePlanItem(planItem.cur) ;
		addLog(logsDoc, CareerLogAction.REMOVE_ITEM_FROM_PLAN_AS_FATHER,
				removerId,
				theRemove.getName());
		
		for(Element sub : findAllByTag(planItem.cur, T_ITEM)) {
			if(sub == planItem.cur) {
				continue;
			}
			
			PlanItem subItem = parsePlanItem(sub);
			addLog(logsDoc, CareerLogAction.REMOVE_ITEM_FROM_PLAN_DUE_TO_FATHER_REMOVED,
					removerId,
					theRemove.getName(),subItem.getName());
		}
	}
	
	
	public static void removeItemFromPlan(Plan one,long removerId, int itemId) throws LogicException {
		Document content = getDefinateDocument(one);
		removeItemFromPlanDoc(content, content, removerId, itemId);
		one.setContent(content.asXML());
	}
	
	public static void removeItemFromWSPlan(WorkSheet one,long removerId, int itemId) throws LogicException {
		Document ws = getDocumentOrInitIfNotExists(one);
		Document plan = getDefinatePlanDocument(one);
		WorkItem relevant =  getWorkItems(ws).stream().filter(wI->wI.getPlanItemId() == itemId).findAny().orElse(null);
		if(relevant != null) {
			throw new LogicException(SMError.CANNOT_DELETE_WS_PLAN_ITEM_WITH_WORK_ITEM,"关联的工作项 "+ TimeUtil.parseTime(relevant.getStartTime()));
		}
		
		removeItemFromPlanDoc(plan, ws, removerId, itemId);
		one.setContent(ws.asXML());
		one.setPlan(plan.asXML());
	}
	
	
	public static void removeItemFromWorkSheet(WorkSheet one,long removerId, int itemId) throws LogicException {
		Document content = getDefinateDocument(one);
		Element itemsElement= content.getRootElement().element(T_ITEMS);
		Element workElement = getWorkItemById(content, itemId);
		
		boolean success = itemsElement.remove(workElement);
		if(!success)
			throw new LogicException(SMError.UNEXPCETED_OP_ERROR_FOR_WS,"删除失败");
		
		one.setContent(content.asXML());
	}
	
	
	/**
	 * 起名convert 是为了与parse区分开
	 * 由于功能考虑 所以参数和addItem 并不一致 
	 */
	
	public static PlanContent convertPlanContent(Plan one) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(one);
		PlanContent rlt = new PlanContent();
		rlt.items = getPlanItems(doc);
		rlt.logs = getLogs(doc).stream().map(CareerLogProxy::new).collect(toList());
		return rlt;
	}
	
	public static PlanDeptContent convertPlanDept(PlanDept dept) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(dept);
		PlanDeptContent rlt = new PlanDeptContent();
		rlt.items = getPlanDeptItems(doc);
		rlt.logs = getLogs(doc).stream().map(CareerLogProxy::new).collect(toList());
		return rlt;
	}
	
	public static WorkSheetContent convertWorkSheet(WorkSheet ws) throws LogicException {
		WorkSheetContent rlt = new WorkSheetContent();
		Document planDoc = getDefinatePlanDocument(ws);
		rlt.planItems = getPlanItems(planDoc).stream().map(PlanItemProxy::new).collect(toList());
		Document wsDoc = getDocumentOrInitIfNotExists(ws);
		rlt.workItems = getWorkItems(wsDoc).stream().map(WorkItemProxy::new).collect(toList());
		rlt.logs = getLogs(wsDoc).stream().map(CareerLogProxy::new).collect(toList());
		return rlt;
	}
	
	private static List<Element> findAllByTag(Element ele,String tagName){
		List<Element> rlt = new ArrayList<Element>();

		if(ele.getName().equals(tagName)) {
			rlt.add(ele);
		}
		
		for(Element node : ele.elements()) {
			rlt.addAll(findAllByTag(node, tagName));
		}
		return rlt;
	}
	
	public static void pushToWorkSheet(Plan plan,WorkSheet ws) throws LogicException {
		
		if(ws.getContent()!=null && ws.getContent().length() > 0) {
			throw new LogicException(SMError.ILLEGAL_WORK_SHEET_CONTENT,"push 时 WorkSheet不该有值 ");
		}
		Document planDoc = getDocumentOrInitIfNotExists(plan);
		ws.setPlan(adpatPlanDocToWS(planDoc).asXML());
	}
	
	/*ws的plan 不要有log相关的信息*/
	private static Document adpatPlanDocToWS(Document planDoc) {
		Element root = planDoc.getRootElement();
		Element logs = root.element(T_LOGS);
		assert logs != null;
		root.remove(logs);
		return planDoc;
	}
	
	public static void copyPlanItemsFrom(Plan target,Plan templete,long opreatorId) throws LogicException {
		Document targetDoc = getDocumentOrInitIfNotExists(target);
		Document templeteDoc = getDocumentOrInitIfNotExists(templete);
		
		Element rootForTemplete = templeteDoc.getRootElement().element(T_ITEMS);
		Element rootForTarget= targetDoc.getRootElement().element(T_ITEMS);
		
		for(Element toRemvoe : rootForTarget.elements()) {
			rootForTarget.remove(toRemvoe);
		}
		for(Element toCopy : rootForTemplete.elements()) {
			rootForTarget.add((Element)toCopy.clone());
		}
		
		/*复制主键id自增值*/
		rootForTarget.addAttribute(A_P_AUTO_INCREMENT_KEY, rootForTemplete.attributeValue(A_P_AUTO_INCREMENT_KEY));
		
		addLog(targetDoc, CareerLogAction.COPY_PLAN_ITEMS, opreatorId, templete.getName());
		
		target.setContent(targetDoc.asXML());
	}
	
	/**
	 *  通过planItem 拿到 planItemId Name remaingValForCur
	 *  通过name 与 dept对应。  strip()以防万一
	 * sync后 对ws 要加type为 dept的workItem 加Log
	 *  对dept 加Log 来自某一天的工作表的某一项
	 *  假设sync为0，则销毁掉这条DeptItem 同时加Log
	*/
	public static void syncToPlanDept(WorkSheet ws, PlanDept dept, PlanItemProxy planItem,long opreatorId) throws LogicException {
		assert planItem.remainingValForCur != 0;

		Document deptDoc = getDefinateDocument(dept);
		
		String deptItemName = planItem.item.getName().strip();
		PlanItemType type = planItem.item.getType();
		Element itemsElement= deptDoc.getRootElement().element(T_ITEMS);
		try {
			Element targetDeptItemElement =  getPlanDeptItemByNameAndType(itemsElement, deptItemName, type);
			PlanDeptItem deptItem = parsePlanDeptItem(targetDeptItemElement);
			assert deptItem.getName().equals(deptItemName);
			
			double after = CommonUtil.fixDouble(planItem.remainingValForCur+deptItem.getValue());
			
			addPlanDpetLog(deptDoc, CareerLogAction.SYNC_ITEM_FOR_DEPT, opreatorId,
					ws.getDateUtc(),
					deptItemName,
					deptItem.getValue(),
					after,
					type.getDbCode());
			
			deptItem.setValue(after);

			overridePlanDeptItemOrDeleteWhenValZero(deptDoc, deptItem, targetDeptItemElement);
		} catch (NoSuchElement e) {
			PlanDeptItem deptItem = new PlanDeptItem();
			deptItem.setType(type);
			deptItem.setValue(CommonUtil.fixDouble(planItem.remainingValForCur));
			deptItem.setName(deptItemName);
			append(deptItem, itemsElement);
			
			addPlanDpetLog(deptDoc, CareerLogAction.ADD_DEPT_ITEM, opreatorId,
					ws.getDateUtc(),
					deptItemName,
					deptItem.getValue(),
					type.getDbCode());
		}
		
		Document wsDoc = getDefinateDocument(ws);
		
		WorkItem item = new WorkItem();
		long syncTime = System.currentTimeMillis();
		item.setType(WorkItemType.DEBT);
		item.setValue(planItem.remainingValForCur);
		item.setPlanItemId(planItem.item.getId());
		item.setNote("");
		item.setMood(0);
		item.setStartUtc(syncTime);
		item.setEndUtc(syncTime);
		
		refineForAddAndVal(item);
		
		append(item, wsDoc.getRootElement().element(T_ITEMS));
		
		dept.setContent(deptDoc.asXML());
		ws.setContent(wsDoc.asXML());
	}
	
	private static void overridePlanDeptItemOrDeleteWhenValZero(Document deptDoc,PlanDeptItem deptItemForOverride,Element targetDeptItemElement) throws LogicException {
		if(deptItemForOverride.getValue() != 0) {
			fillAttrsExceptId(deptItemForOverride, targetDeptItemElement);
		}else {
			Element itemsElement= deptDoc.getRootElement().element(T_ITEMS);
			boolean deleteSuccess = itemsElement.remove(targetDeptItemElement);
			if(!deleteSuccess) {
				throw new LogicException(SMError.SYNC_ITEM_WITH_DEPT_ERROR,"删除欠账项失败 "+deptItemForOverride.getName());
			}
			addPlanDpetLog(deptDoc, CareerLogAction.REMOVE_DEPT_ITEM_DUE_TO_ZERO_VAL, SM.SYSTEM_ID,
					deptItemForOverride.getName());
		}
	}
	
	private static void refineForAddAndVal(WorkItem item) {
		if(item.getValue() > 0) {
			item.setForAdd(false);
			return ;
		}
		
		item.setForAdd(true);
		item.setValue(item.getValue() * -1);
		assert item.getValue() > 0;
	}

	


}
