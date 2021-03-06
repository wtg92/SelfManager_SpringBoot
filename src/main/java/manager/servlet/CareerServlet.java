package manager.servlet;

import static java.util.stream.Collectors.toList;
import static manager.system.SMParm.BOOK_ID;
import static manager.system.SMParm.CAT_NAME;
import static manager.system.SMParm.CAT_TYPE;
import static manager.system.SMParm.CONTENT;
import static manager.system.SMParm.DATA;
import static manager.system.SMParm.DATE;
import static manager.system.SMParm.END_DATE;
import static manager.system.SMParm.END_TIME;
import static manager.system.SMParm.FATHER_ID;
import static manager.system.SMParm.FOLD;
import static manager.system.SMParm.FOR_ADD;
import static manager.system.SMParm.IDS;
import static manager.system.SMParm.IMPORTANT;
import static manager.system.SMParm.ITEM_ID;
import static manager.system.SMParm.LABEL;
import static manager.system.SMParm.MAPPING_VAL;
import static manager.system.SMParm.MOOD;
import static manager.system.SMParm.NAME;
import static manager.system.SMParm.NOTE;
import static manager.system.SMParm.NOTES_SEQ;
import static manager.system.SMParm.NOTE_ID;
import static manager.system.SMParm.OP;
import static manager.system.SMParm.PAGE;
import static manager.system.SMParm.PLAN_ID;
import static manager.system.SMParm.PLAN_ITEM_ID;
import static manager.system.SMParm.PLAN_SETTING;
import static manager.system.SMParm.RECALCULATE_STATE;
import static manager.system.SMParm.SEQ_WEIGHT;
import static manager.system.SMParm.SRC_NOTE_ID;
import static manager.system.SMParm.START_DATE;
import static manager.system.SMParm.START_TIME;
import static manager.system.SMParm.STATE;
import static manager.system.SMParm.STYLE;
import static manager.system.SMParm.TAGS;
import static manager.system.SMParm.TARGET_ID;
import static manager.system.SMParm.TARGET_PLAN_ID;
import static manager.system.SMParm.TEMPLE_PLAN_ID;
import static manager.system.SMParm.VAL;
import static manager.system.SMParm.WITH_TODOS;
import static manager.system.SMParm.WORK_ITEM_ID;
import static manager.system.SMParm.WS_ID;
import static manager.system.SMParm.WS_IDS;
import static manager.util.UIUtil.getBiParamsJSON;
import static manager.util.UIUtil.getJSONArrayParam;
import static manager.util.UIUtil.getLoginerId;
import static manager.util.UIUtil.getNonNullParam;
import static manager.util.UIUtil.getNonNullParamInBool;
import static manager.util.UIUtil.getNonNullParamInDate;
import static manager.util.UIUtil.getNonNullParamInDouble;
import static manager.util.UIUtil.getNonNullParamInInt;
import static manager.util.UIUtil.getNonNullParamInTime;
import static manager.util.UIUtil.getNonNullParamsInInt;
import static manager.util.UIUtil.getNullObjJSON;
import static manager.util.UIUtil.getParamJSON;
import static manager.util.UIUtil.getParamOrBlankDefault;
import static manager.util.UIUtil.getParamOrBlankDefaultInDate;
import static manager.util.UIUtil.getParamOrBlankDefaultInTime;
import static manager.util.UIUtil.getParamOrFalseDefault;
import static manager.util.UIUtil.getParamOrZeroDefault;
import static manager.util.UIUtil.getParamOrZeroDefaultInDouble;
import static manager.util.UIUtil.getParamOrZeroDefaultInInt;
import static manager.util.UIUtil.getParamsInIntOrZeroDefault;
import static manager.util.UIUtil.getParamsOrEmptyList;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

import manager.entity.virtual.career.WorkItem;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.career.NoteLogic;
import manager.logic.career.WorkLogic;
import manager.system.SMError;
import manager.system.SMOP;
import manager.system.career.BookStyle;
import manager.system.career.NoteLabel;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.WorkItemType;
import manager.system.career.WorkSheetState;

@WebServlet(name="CareerServlet",urlPatterns = "/CareerServlet")
public class CareerServlet extends SMServlet{
	private static final long serialVersionUID = -566721941701587967L;
	
	private WorkLogic wL = WorkLogic.getInstance();
	private NoteLogic nL = NoteLogic.getInstance();
	
	private SerializeConfig workConf = createWorkEnumConfig();
	private SerializeConfig noteConf = createNoteEnumConfig();
	
	@SuppressWarnings("unchecked")
	private SerializeConfig createWorkEnumConfig() {
		SerializeConfig conf = new SerializeConfig();
		conf.configEnumAsJavaBean(PlanState.class);
		conf.configEnumAsJavaBean(PlanItemType.class);
		conf.configEnumAsJavaBean(WorkSheetState.class);
		conf.configEnumAsJavaBean(WorkItemType.class);
		conf.configEnumAsJavaBean(PlanSetting.class);
		return conf;
	}
	
	@SuppressWarnings("unchecked")
	private SerializeConfig createNoteEnumConfig() {
		SerializeConfig conf = new SerializeConfig();
		conf.configEnumAsJavaBean(NoteLabel.class);
		conf.configEnumAsJavaBean(BookStyle.class);
		return conf;
	}
	
	@Override
	public String process(HttpServletRequest request) throws SMException, ServletException, IOException {
		SMOP op = SMOP.valueOfName(getNonNullParam(request,OP));
		switch (op) {
		case C_LOAD_ACTIVE_PLANS:
			return loadActivePlans(request);
		case C_CREATE_PLAN:
			return createPlan(request);
		case C_ADD_ITEM_TO_PLAN:
			return addItemToPlan(request);
		case C_REMOVE_ITEM_FROM_PLAN:
			return removeItemFromPlan(request);
		case C_LOAD_PLAN:
			return loadPlan(request);
		case C_LOAD_ALL_PLAN_TAGS:
			return loadAllPlanTags(request);
		case C_LOAD_ALL_WORKSHEET_TAGS:
			return loadAllWorkSheetTags(request);
		case C_SAVE_PLAN:
			return savePlan(request);
		case C_SAVE_PLAN_ITEM:
			return savePlanItem(request);
		case C_SAVE_PLAN_ITEM_FOLD:
			return savePlanItemFold(request);
		case C_RESET_PLAN_TAGS:
			return resetPlanTags(request);
		case C_RESET_WORK_SHEET_TAGS:
			return resetWorkSheetTags(request);
		case C_ABANDON_PLAN:
			return abandonPlan(request);
		case C_FINISH_PLAN:
			return finishPlan(request);
		case C_LOAD_PLAN_STATE_STATISTICS:
			return loadPlanStatesStatistics(request);
		case C_LOAD_WS_STATE_STATISITCS:
			return loadWSStateStatistics(request);
		case C_LOAD_PLANS_BY_STATE:
			return loadPlansByState(request);
		case C_LOAD_WS_BY_STATE:
			return loadWSByState(request);
		case C_OPEN_WORK_SHEET_TODAY:
			return openWorkSheetToday(request);
		case C_SAVE_WORK_SHEET:
			return saveWorkSheet(request);
		case C_DELETE_WORK_SHEET:
			return deleteWorkSheet(request);
		case C_ASSUME_WORK_SHEET_FINISHED:
			return assumeWorkSheetFinished(request);
		case C_CANCEL_ASSUME_WORK_SHEET_FINISHED:
			return cacelAssumeWorkSheetFinished(request);
		case C_LOAD_WORK_SHEET_INFOS_RECENTLY:
			return loadWorkSheetInfosRecently(request);
		case C_LOAD_WORK_SHEET:
			return loadWorkSheet(request);
		case C_LOAD_WORK_SHEET_COUNT:
			return loadTodaySheetCount(request);
		case C_ADD_ITEM_TO_WS_PLAN:
			return addItemToWSPlan(request);
		case C_SAVE_WS_PLAN_ITEM:
			return saveWSPlanItem(request);
		case C_SAVE_WS_PLAN_ITEM_FOLD:
			return saveWSPlanItemFold(request);
		case C_REMOVE_ITEM_FROM_WS_PLAN:
			return removeItemFromWSPlan(request);
		case C_REMOVE_ITEM_FROM_WORK_SHEET:
			return removeItemFromWorkSheet(request);
		case C_ADD_ITEM_TO_WS:
			return addItemToWS(request);
		case C_SAVE_WORK_ITEMS:
			return saveWorkItems(request);
		case C_SAVE_WORK_ITEM_PLAN_ITEM_ID:
			return saveWorkItemPlanItemId(request);
		case C_SYNC_PLAN_TAGS_TO_WORKSHEET:
			return syncPlanTagsToWorkSheet(request);
		case C_SYNC_TO_PLAN_DEPT:
			return syncToPlanDept(request);
		case C_SYNC_ALL_TO_PLAN_DEPT:
			return syncAllToPlanDept(request);
		case C_SYNC_ALL_TO_PLAN_DEPT_BATCH:
			return syncAllToPlanDeptBatch(request);
		case C_LOAD_PLAN_DEPT:
			return loadPlanDept(request);
		case C_LOAD_PLAN_DEPT_ITEM_NAMES:
			return loadPlanDeptItemNames(request);
		case C_SAVE_DEPT_ITEM:
			return saveDeptItem(request);
		case C_COPY_PLAN_ITEMS_BY_ID:
			return copyPlanItemsById(request);
		case C_CREATE_NOTE_BOOK:
			return createNoteBook(request);
		case C_LOAD_BOOKS:
			return loadBooks(request);
		case C_LOAD_BOOK:
			return loadBook(request);
		case C_CLOSE_NOTE_BOOK:
			return closeBook(request);
		case C_LOAD_BOOK_CONTENT:
			return loadBookContent(request);
		case C_SAVE_NOTE_BOOK:
			return saveNoteBook(request);
		case C_CREATE_NOTE:
			return createNote(request);
		case C_OPEN_NOTE_BOOK:
			return openBook(request);
		case C_DELETE_NOTE_BOOK:
			return deleteBook(request);
		case C_DELETE_NOTE:
			return deleteNote(request);
		case C_SAVE_NOTES_SEQ:
			return saveNotesSeq(request);
		case C_LOAD_NOTE:
			return loadNote(request);
		case C_SAVE_NOTE:
			return saveNote(request);
		case C_LOAD_NOTE_LABELS:
			return loadNoteLabels(request);
		case C_SAVE_NOTE_IMPORTANT:
			return saveNoteImportant(request);
		case C_ADD_ITEM_TO_MEMO:
			return addItemToMemo(request);
		case C_LOAD_MEMO:
			return loadMemo(request);
		case C_SAVE_MEMO:
			return saveMemo(request);
		case C_SAVE_MEMO_ITEM:
			return saveMemoItem(request);
		case C_REMOVE_ITEM_FROM_MEMO:
			return removeItemFromMemo(request);
		case C_SAVE_MEMO_ITEMS_SEQ:
			return saveMemoItemsSeq(request);
		case C_SAVE_MEMO_ITEM_LABEL:
			return saveMemoItemLabel(request);
		case C_LOAD_WORK_SHEETS_BY_DATE_SCOPE:
			return loadWorkSheetsByDateScope(request);
		case C_SAVE_WORK_SHEET_PLAN_ID:
			return saveWorkSheetPlanId(request);
		default:
			assert false : op.getName();
			throw new LogicException(SMError.UNKOWN_OP,getNonNullParam(request,OP));
		}
	}

	private String syncPlanTagsToWorkSheet(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		wL.syncPlanTagsToWorkSheet(loginerId, planId);
		return getNullObjJSON();
	}

	private String loadAllPlanTags(HttpServletRequest request) throws SMException{
		long loginerId = getLoginerId(request);
		List<String> tags = wL.loadAllPlanTagsByUser(loginerId);
		return JSON.toJSONString(tags);
	}

	private String loadAllWorkSheetTags(HttpServletRequest request) throws SMException{
		long loginerId = getLoginerId(request);
		List<String> tags = wL.loadAllWorkSheetTagsByUser(loginerId);
		return JSON.toJSONString(tags);
	}
	
	
	private String loadWorkSheetsByDateScope(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		Calendar startDate = getNonNullParamInDate(request, START_DATE);
		Calendar endDate = getNonNullParamInDate(request, END_DATE);
		return JSON.toJSONString(wL.loadWorkSheetsByDateScope(loginerId, startDate, endDate),workConf);
	}

	private String saveMemoItemLabel(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		int itemId  = getNonNullParamInInt(request, ITEM_ID);
		NoteLabel label = NoteLabel.valueOfName(getNonNullParam(request, LABEL));
		nL.saveMemoItemLabel(loginerId, itemId, label);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String saveMemoItemsSeq(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		List<Integer> ids  = getNonNullParamsInInt(request, IDS);
		nL.saveMemoItemsSeq(loginerId, ids);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String removeItemFromMemo(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		int itemId  = getNonNullParamInInt(request, ITEM_ID);
		nL.removeItemFromMemo(loginerId, itemId);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String saveMemoItem(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int itemId  = getNonNullParamInInt(request, ITEM_ID);
		String content = getNonNullParam(request, CONTENT);
		String note = getParamOrBlankDefault(request, NOTE);
		NoteLabel label = NoteLabel.valueOfName(getNonNullParam(request, LABEL));
		nL.saveMemoItem(loginerId, itemId, content, label, note);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String saveMemo(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String note = getParamOrBlankDefault(request, NOTE);
		nL.saveMemo(loginerId, note);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String loadMemo(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String addItemToMemo(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String content = getNonNullParam(request, CONTENT);
		String note = getParamOrBlankDefault(request, NOTE);
		NoteLabel label = NoteLabel.valueOfName(getNonNullParam(request, LABEL));
		long srcNoteId = getNonNullParamInInt(request, SRC_NOTE_ID);
		nL.addItemToMemo(loginerId, content, label, note,srcNoteId);
		return JSON.toJSONString(nL.loadMemo(loginerId), noteConf); 
	}

	private String saveNoteImportant(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int noteId = getNonNullParamInInt(request, NOTE_ID);
		boolean important = getNonNullParamInBool(request,IMPORTANT);
		long bookId = nL.saveNoteImportant(loginerId, noteId, important);
		
		return getBiParamsJSON(nL.loadNote(loginerId, noteId),nL.loadBookContent(loginerId, bookId),noteConf);
	}

	private String loadNoteLabels(HttpServletRequest request) throws LogicException {
		getLoginerId(request);
		return JSON.toJSONString(NoteLabel.loadCommonLabels(),noteConf);
	}

	private String saveNote(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int noteId = getNonNullParamInInt(request, NOTE_ID);
		String content = getParamOrBlankDefault(request, CONTENT);
		String name = getParamOrBlankDefault(request, NAME);
		boolean withTodos = getNonNullParamInBool(request,WITH_TODOS);
		nL.saveNote(loginerId, noteId, name, content,withTodos);
		/*withTodo????????????*/
		return JSON.toJSONString(nL.loadNote(loginerId, noteId),noteConf);
	}

	private String loadNote(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int noteId = getNonNullParamInInt(request, NOTE_ID);
		return JSON.toJSONString(nL.loadNote(loginerId, noteId),noteConf);
	}

	private String saveNotesSeq(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, TARGET_ID);
		List<Integer> notesSeq = getNonNullParamsInInt(request, NOTES_SEQ);
		nL.saveNotesSeq(loginerId, bookId, notesSeq);
		return getNullObjJSON();
	}

	private String deleteNote(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int noteId = getNonNullParamInInt(request, NOTE_ID);
		nL.deleteNote(loginerId, noteId);
		return getNullObjJSON();
	}

	private String deleteBook(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		nL.deleteNoteBook(loginerId, bookId);
		return getNullObjJSON();
	}

	private String openBook(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		nL.openNoteBook(loginerId, bookId);
		return getNullObjJSON();
	}

	private String createNote(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		String name = getNonNullParam(request, NAME);
		long noteId = nL.createNote(loginerId, bookId, name);
		return getBiParamsJSON(nL.loadBookContent(loginerId, bookId),noteId,noteConf); 
	}

	private String saveNoteBook(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String name = getNonNullParam(request, NAME);
		String note = getParamOrBlankDefault(request, NOTE);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		BookStyle style = BookStyle.valueOfDBCode(getNonNullParamInInt(request, STYLE));
		int seqWeight = getParamOrZeroDefaultInInt(request, SEQ_WEIGHT);
		nL.saveNoteBook(loginerId, bookId, name, note,style,seqWeight);
		return JSON.toJSONString(nL.loadBook(loginerId, bookId),noteConf);
	}

	private String loadBookContent(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		return JSON.toJSONString(nL.loadBookContent(loginerId, bookId),noteConf);
	}

	private String closeBook(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		nL.closeNoteBook(loginerId, bookId);
		return getNullObjJSON();
	}

	private String loadBook(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long bookId = getNonNullParamInInt(request, BOOK_ID);
		return JSON.toJSONString(nL.loadBook(loginerId, bookId),noteConf);
	}

	private String loadBooks(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(nL.loadBooks(loginerId),noteConf);
	}

	private String createNoteBook(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String name = getNonNullParam(request, NAME);
		String note = getParamOrBlankDefault(request, NOTE);
		BookStyle style = BookStyle.valueOfDBCode(getNonNullParamInInt(request, STYLE));
		long id = nL.createNoteBook(loginerId, name, note,style);
		return JSON.toJSONString(nL.loadBook(loginerId, id),noteConf);
	}

	private String copyPlanItemsById(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int targetPlanId = getNonNullParamInInt(request, TARGET_PLAN_ID);
		String templePlanId = getNonNullParam(request, TEMPLE_PLAN_ID);
		int templetePlanId = ServletAdapter.getCommonId(templePlanId);
		wL.copyPlanItemsFrom(loginerId, targetPlanId, templetePlanId);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, targetPlanId)),workConf);
	}

	private String saveDeptItem(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		String name = getNonNullParam(request, NAME);
		double val = getNonNullParamInDouble(request, VAL);
		wL.savePlanDeptItem(loginerId, itemId, name, val);
		return JSON.toJSONString(wL.loadPlanDept(loginerId),workConf);
	}

	private String syncAllToPlanDept(HttpServletRequest request) throws DBException, LogicException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		wL.syncAllToPlanDept(loginerId, wsId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	private String syncAllToPlanDeptBatch(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		List<Integer> wsIds = getNonNullParamsInInt(request, WS_IDS);
		int wsIdForRefresh = getNonNullParamInInt(request, WS_ID);
		wL.syncAllToPlanDeptBatch(loginerId, wsIds);
		/*0 means no refresh*/
		return wsIdForRefresh == 0 ? getNullObjJSON() : JSON.toJSONString(wL.loadWorkSheet(loginerId, wsIdForRefresh),workConf);
	}
	
	private String loadPlanDeptItemNames(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(wL.loadPlanDeptItemNames(loginerId),workConf);
	}

	private String loadWSByState(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		WorkSheetState stateZT = WorkSheetState.valueOfDBCode(getParamOrZeroDefault(request, STATE));
		return JSON.toJSONString(wL.loadWorkSheetByState(loginerId,stateZT),workConf);
	}

	private String loadWSStateStatistics(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(wL.loadWSStateStatistics(loginerId));
	}

	private String loadPlanDept(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(wL.loadPlanDept(loginerId),workConf);
	}

	private String syncToPlanDept(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int planItemId = getNonNullParamInInt(request, PLAN_ITEM_ID);
		wL.syncToPlanDept(loginerId, wsId, planItemId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String removeItemFromWorkSheet(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		wL.removeItemFromWorkSheet(loginerId, wsId, itemId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String saveWorkItems(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		List<WorkItem> workItems = getJSONArrayParam(request, DATA,WorkItem.class);
		
		wL.saveWorkItems(loginerId, wsId,workItems);
		
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	private String saveWorkSheetPlanId(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int planId = ServletAdapter.getCommonId(getNonNullParam(request, PLAN_ID));
		int wsId = getNonNullParamInInt(request, WS_ID);
		wL.saveWorkSheetPlanId(loginerId, wsId, planId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	private String saveWorkItemPlanItemId(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int workItemId = getNonNullParamInInt(request, WORK_ITEM_ID);
		int planItimId = getNonNullParamInInt(request, PLAN_ITEM_ID);
		wL.saveWorkItemPlanItemId(loginerId, wsId, workItemId, planItimId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String addItemToWS(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int planItemId = getNonNullParamInInt(request, PLAN_ITEM_ID);
		int val = getNonNullParamInInt(request, VAL);
		String note = getParamOrBlankDefault(request, NOTE);
		int mood = getNonNullParamInInt(request, MOOD);
		boolean forAdd = getNonNullParamInBool(request, FOR_ADD);
		Calendar startTime = getNonNullParamInTime(request, START_TIME);
		Calendar endTime = getParamOrBlankDefaultInTime(request, END_TIME);
		wL.addItemToWS(loginerId, wsId, planItemId, val, note, mood, forAdd, startTime, endTime);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String removeItemFromWSPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		wL.removeItemFromWSPlan(loginerId, wsId, itemId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String saveWSPlanItem(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		String catName = getNonNullParam(request, CAT_NAME);
		int val = getParamOrZeroDefault(request, VAL);
		String note = getParamOrBlankDefault(request, NOTE);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		double mappingVal = getParamOrZeroDefaultInDouble(request, MAPPING_VAL);
		wL.saveWSPlanItem(loginerId, wsId, itemId, catName, val, note, mappingVal);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	
	private String saveWSPlanItemFold(HttpServletRequest request) throws SMException{
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		boolean fold = getNonNullParamInBool(request, FOLD);
		wL.saveWSPlanItemFold(loginerId, wsId, itemId, fold);
		return getNullObjJSON();
	}

	private String addItemToWSPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		String catName = getNonNullParam(request, CAT_NAME);
		int val = getParamOrZeroDefault(request, VAL);
		String note = getParamOrBlankDefault(request, NOTE);
		PlanItemType type = PlanItemType.valueOfDBCode(getNonNullParamInInt(request, CAT_TYPE));
		int fatherId = getNonNullParamInInt(request, FATHER_ID);
		double mappingVal = getParamOrZeroDefaultInDouble(request, MAPPING_VAL);
		wL.addItemToWSPlan(loginerId, wsId, catName, val, note, type, fatherId, mappingVal);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String loadTodaySheetCount(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		Calendar date = getNonNullParamInDate(request, DATE);
		return getParamJSON(wL.loadWorkSheetCount(loginerId,date));
	}
	
	private String loadWorkSheet(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	private String loadWorkSheetInfosRecently(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int page = getNonNullParamInInt(request, PAGE);
		return JSON.toJSONString(wL.loadWorkSheetInfosRecently(loginerId, page),workConf);
	}

	private String cacelAssumeWorkSheetFinished(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		wL.cancelAssumeWorkSheetFinished(loginerId, wsId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String assumeWorkSheetFinished(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		wL.assumeWorkSheetFinished(loginerId, wsId);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}

	private String deleteWorkSheet(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		wL.deleteWorkSheet(loginerId, wsId);
		return getNullObjJSON();
	}

	private String saveWorkSheet(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		String note = getNonNullParam(request, NOTE);
		wL.saveWorkSheet(loginerId, wsId, note);
		return getNullObjJSON();
	}

	private String openWorkSheetToday(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		wL.openWorkSheetToday(loginerId, planId);
		return JSON.toJSONString(wL.loadWorkSheetInfosRecently(loginerId, 0),workConf) ;
	}

	private String loadPlansByState(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		PlanState stateZT = PlanState.valueOfDBCode(getParamOrZeroDefault(request, STATE));
		return JSON.toJSONString(wL.loadPlansByState(loginerId,stateZT),workConf);
	}

	private String loadPlanStatesStatistics(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(wL.loadPlanStateStatistics(loginerId));
	}

	private String finishPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		wL.finishPlan(loginerId, planId);
		return getNullObjJSON();
	}

	private String abandonPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		wL.abandonPlan(loginerId, planId);
		return getNullObjJSON();
	}
	
	
	private String resetWorkSheetTags(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int wsId = getNonNullParamInInt(request, WS_ID);
		List<String> tags = getParamsOrEmptyList(request, TAGS);
		wL.resetWorkSheetTags(loginerId, wsId, tags);
		return JSON.toJSONString(wL.loadWorkSheet(loginerId, wsId),workConf);
	}
	
	
	private String resetPlanTags(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		List<String> tags = getParamsOrEmptyList(request, TAGS);
		wL.resetPlanTags(loginerId, planId,tags);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}
	
	
	private String savePlanItemFold(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		boolean fold = getNonNullParamInBool(request, FOLD);
		wL.savePlanItemFold(loginerId, planId, itemId, fold);
		return getNullObjJSON();
	}
	

	private String savePlanItem(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		String catName = getNonNullParam(request, CAT_NAME);
		int val = getParamOrZeroDefault(request, VAL);
		String note = getParamOrBlankDefault(request, NOTE);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		double mappingVal = getParamOrZeroDefaultInDouble(request, MAPPING_VAL);
		wL.savePlanItem(loginerId, planId, itemId, catName, val, note, mappingVal);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}


	
	private String savePlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String name = getNonNullParam(request, NAME);
		Calendar startDate = getNonNullParamInDate(request, START_DATE);
		Calendar endDate = getParamOrBlankDefaultInDate(request, END_DATE);
		String note = getParamOrBlankDefault(request, NOTE);
		boolean recalculateState = getParamOrFalseDefault(request, RECALCULATE_STATE);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		int seqWeight = getParamOrZeroDefaultInInt(request, SEQ_WEIGHT);
		List<PlanSetting> settings = getParamsInIntOrZeroDefault(request, PLAN_SETTING).stream().map(PlanSetting::valueOfDBCode).collect(toList());
		
		wL.savePlan(loginerId,planId,name,startDate,endDate,note,recalculateState,settings,seqWeight);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}

	private String loadPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}

	private String removeItemFromPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		int itemId = getNonNullParamInInt(request, ITEM_ID);
		wL.removeItemFromPlan(loginerId, planId, itemId);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}

	/**
	 * @return plan ???UI ???????????????????????????
	 */
	private String createPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		String name = getNonNullParam(request, NAME);
		Calendar startDate = getNonNullParamInDate(request, START_DATE);
		Calendar endDate = getParamOrBlankDefaultInDate(request, END_DATE);
		String note = getParamOrBlankDefault(request, NOTE);
		long id = wL.createPlan(loginerId, name, startDate, endDate, note);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, id)),workConf);
	}

	private String addItemToPlan(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		int planId = getNonNullParamInInt(request, PLAN_ID);
		String catName = getNonNullParam(request, CAT_NAME);
		int val = getParamOrZeroDefault(request, VAL);
		String note = getParamOrBlankDefault(request, NOTE);
		PlanItemType type = PlanItemType.valueOfDBCode(getNonNullParamInInt(request, CAT_TYPE));
		int fatherId = getNonNullParamInInt(request, FATHER_ID);
		double mappingVal = getParamOrZeroDefaultInDouble(request, MAPPING_VAL);
		wL.addItemToPlan(loginerId, planId, catName, val, note, type, fatherId, mappingVal);
		return JSON.toJSONString(ServletAdapter.process(wL.loadPlan(loginerId, planId)),workConf);
	}

	private String loadActivePlans(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(wL.loadActivePlans(loginerId),workConf);
	}
	
}
