package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import manager.booster.SecurityBooster;
import manager.data.MultipleItemsResult;
import manager.data.proxy.career.PlanBalanceProxy;
import manager.data.proxy.career.PlanProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.service.work.WorkService;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static manager.system.SelfXParams.CAT_NAME;
import static manager.system.SelfXParams.CAT_TYPE;
import static manager.system.SelfXParams.DATE;
import static manager.system.SelfXParams.END_DATE;
import static manager.system.SelfXParams.END_TIME;
import static manager.system.SelfXParams.END_UTC_FOR_CREATE;
import static manager.system.SelfXParams.END_UTC_FOR_DATE;
import static manager.system.SelfXParams.END_UTC_FOR_UPDATE;
import static manager.system.SelfXParams.FATHER_ID;
import static manager.system.SelfXParams.FOLD;
import static manager.system.SelfXParams.FOR_ADD;
import static manager.system.SelfXParams.ID;
import static manager.system.SelfXParams.ITEM_ID;
import static manager.system.SelfXParams.MAPPING_VAL;
import static manager.system.SelfXParams.MOOD;
import static manager.system.SelfXParams.NAME;
import static manager.system.SelfXParams.NOTE;
import static manager.system.SelfXParams.PAGE;
import static manager.system.SelfXParams.PLAN_ID;
import static manager.system.SelfXParams.PLAN_ITEM_ID;
import static manager.system.SelfXParams.PLAN_SETTING;
import static manager.system.SelfXParams.RECALCULATE_STATE;
import static manager.system.SelfXParams.SEQ_WEIGHT;
import static manager.system.SelfXParams.START_DATE;
import static manager.system.SelfXParams.START_TIME;
import static manager.system.SelfXParams.START_UTC_FOR_CREATE;
import static manager.system.SelfXParams.START_UTC_FOR_DATE;
import static manager.system.SelfXParams.START_UTC_FOR_UPDATE;
import static manager.system.SelfXParams.STATE;
import static manager.system.SelfXParams.TAGS;
import static manager.system.SelfXParams.TARGET_PLAN_ID;
import static manager.system.SelfXParams.TEMPLATE_ID;
import static manager.system.SelfXParams.TIMEZONE;
import static manager.system.SelfXParams.VAL;
import static manager.system.SelfXParams.WORK_ITEM_ID;
import static manager.system.SelfXParams.WS_ID;
import static manager.system.SelfXParams.WS_IDS;
import static manager.util.UIUtil.getInDate;
import static manager.util.UIUtil.getParamDoubleOrZeroDefault;
import static manager.util.UIUtil.getParamIntegerOrZeroDefault;
import static manager.util.UIUtil.transferToIntList;

@RestController
@RequestMapping("/ws")
public class WorkSheetController {

    @Resource
    private SecurityBooster securityBooster;

    @Resource
    private WorkService wL;

    private static final String PLAN_PATH = "/plan";

    private static final String WORK_SHEET_PATH = "/ws";

    private static final String BALANCE_PATH = "/balance";

    @GetMapping("/worksheetRecently")
    public List<WorkSheet> loadWorkSheetInfosRecently(
            HttpServletRequest request
            ,@RequestParam(PAGE)Integer page ) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadWorkSheetInfosRecently(loginId, page);
    }

    @GetMapping("/worksheetsCount")
    public long getWorksheetsCount(
            HttpServletRequest request
            ,@RequestParam(DATE)Long date
            ,@RequestParam(TIMEZONE)String timezone) {
        long loginId = securityBooster.requireUserId(request);
        return wL.getWorkSheetCount(loginId,date,timezone);
    }

    @GetMapping(WORK_SHEET_PATH)
    public WorkSheetProxy getWorksheet(
            HttpServletRequest request
            ,@RequestParam(ID)Long wsId) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadWorkSheet(loginId, wsId);
    }

    @PatchMapping(WORK_SHEET_PATH)
    public void patchWorksheet(HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        String note = param.getString(NOTE);
        wL.saveWorkSheet(loginId, wsId, note);
    }
    @PatchMapping(WORK_SHEET_PATH+"/planId")
    public void patchWorksheetPlanId(HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        long wsId = param.getLong(WS_ID);
        long planId = securityBooster.getStableCommonId(param.getString(PLAN_ID));
        wL.saveWorkSheetPlanId(loginId, wsId, planId);
    }


    @PatchMapping(WORK_SHEET_PATH+"/assumeFinished")
    public void assumeWorkSheetFinished(HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        wL.assumeWorkSheetFinished(loginId, wsId);
    }

    @PatchMapping(WORK_SHEET_PATH+"/cancelAssumeFinished")
    public void cancelAssumeWorkSheetFinished(HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        wL.cancelAssumeWorkSheetFinished(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/planItem")
    private void postWorksheetPlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        String catName = param.getString(CAT_NAME);
        int val =  getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        PlanItemType type = PlanItemType.valueOfDBCode(param.getInteger(CAT_TYPE));
        int fatherId = param.getInteger(FATHER_ID);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);
        wL.addItemToWSPlan(loginId, wsId, catName, val, note, type, fatherId, mappingVal);
    }
    @PostMapping(WORK_SHEET_PATH+"/workItem")
    private void postWorkItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        int planItemId = param.getInteger(PLAN_ITEM_ID);
        double val =  getParamDoubleOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        int mood =  getParamIntegerOrZeroDefault(param,MOOD);
        boolean forAdd = param.getBoolean(FOR_ADD);

        Long startUtc = param.getLong(START_TIME);
        Long endUtc = param.getLong(END_TIME);

        wL.addItemToWS(loginId, wsId, planItemId, val, note, mood, forAdd, startUtc, endUtc);
    }

    @DeleteMapping(WORK_SHEET_PATH+"/workItem")
    private void deleteWorkItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromWorkSheet(loginId, wsId, itemId);
    }

    @PostMapping(WORK_SHEET_PATH+"/syncAllToBalanceInBatch")
    private void syncAllToBalanceInBatch(HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        List<Integer> wsIds =  param.getList(WS_IDS,Integer.class);
        wL.syncAllToBalanceInBatch(loginId, wsIds);
    }

    @PostMapping(WORK_SHEET_PATH+"/workItem/syncAllToBalance")
    private void syncAllToBalance( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        wL.syncAllToBalance(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/workItem/syncToBalance")
    private void syncToBalance( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.syncToBalance(loginId, wsId, itemId);
    }

    @PatchMapping(WORK_SHEET_PATH+"/workItem")
    private WorkSheetProxy patchWorkItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        int itemId = param.getInteger(ITEM_ID);
        double val =  getParamDoubleOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        int mood =  getParamIntegerOrZeroDefault(param,MOOD);
        boolean forAdd = param.getBoolean(FOR_ADD);

        Long startUtc = param.getLong(START_TIME);
        Long endUtc = param.getLong(END_TIME);

        wL.saveWorkItem(loginId,wsId,itemId,val, note, mood, forAdd, startUtc, endUtc);        /**
         * 前台更新对应的更新时间 平均心情 左侧列名的更新
         */
        return wL.loadWorkSheet(loginId,wsId);
    }


    @PatchMapping(WORK_SHEET_PATH+"/workItem/planItemId")
    private void patchWorkItemPlanItemId( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(WS_ID);
        int workItemId = param.getInteger(WORK_ITEM_ID);
        int planItemId = param.getInteger(PLAN_ITEM_ID);
        wL.saveWorkItemPlanItemId(loginId, wsId, workItemId, planItemId);
    }



    @PatchMapping(WORK_SHEET_PATH+"/planItem")
    private void patchWorksheetPlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        String catName = param.getString(CAT_NAME);
        int val = getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);
        int itemId = param.getInteger( ITEM_ID);
        wL.saveWSPlanItem(loginId, wsId, itemId, catName, val, note, mappingVal);
    }

    @PatchMapping(WORK_SHEET_PATH+"/planItem/fold")
    private void patchWorksheetPlanItemFold( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        int itemId = param.getInteger( ITEM_ID);
        boolean fold = param.getBoolean(FOLD);
        wL.saveWSPlanItemFold(loginId, wsId, itemId, fold);
    }


    @DeleteMapping(WORK_SHEET_PATH+"/planItem")
    private void deleteWorksheetPlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromWSPlan(loginId, wsId, itemId);
    }

    @DeleteMapping(WORK_SHEET_PATH)
    public void deleteWorksheet(HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int wsId = param.getInteger(ID);
        wL.deleteWorkSheet(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/calculateStatesRoutinely")
    private void calculateWorksheetStatesRoutinely( HttpServletRequest request){
        long loginId = securityBooster.requireUserId(request);
        wL.calculateWorksheetStatesRoutinely(loginId);
    }

    @PostMapping("/abandonPlan")
    public void abandonPlan(
            HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        wL.abandonPlan(loginId, planId);
    }

    @PostMapping("/finishPlan")
    public void finishPlan(
            HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        wL.finishPlan(loginId, planId);
    }



    @GetMapping(PLAN_PATH)
    public PlanProxy getPlan(
            HttpServletRequest request
            , @RequestParam(PLAN_ID)Integer planId ) {
        long loginId = securityBooster.requireUserId(request);
        return securityBooster.process(wL.loadPlan(loginId, planId));
    }

    @GetMapping(BALANCE_PATH)
    public PlanBalanceProxy getBalance(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.getBalance(loginId);
    }

    @PatchMapping(BALANCE_PATH)
    public void patchBalanceItem(
            HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int itemId = param.getInteger(ITEM_ID);
        String name = param.getString(NAME);
        double val = param.getDouble(VAL);
        wL.patchBalanceItem(loginId, itemId, name, val);
    }

    @GetMapping(PLAN_PATH+"/statistics/states")
    public Map<String,Long> getPlanStateStatistic(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadPlanStateStatistics(loginId);
    }

    @GetMapping(WORK_SHEET_PATH+"/statistics/states")
    public Map<String,Long> getWorksheetStateStatistic(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadWSStateStatistics(loginId);
    }

    @GetMapping(PLAN_PATH+"/statistics")
    public MultipleItemsResult<Plan> loadPlansByTerms(
            HttpServletRequest request,
            @RequestParam(STATE)Integer state,
            @RequestParam(value = NAME,required = false)String name,
            @RequestParam(value = START_UTC_FOR_CREATE,required = false)Long startUtcForCreate,
            @RequestParam(value = END_UTC_FOR_CREATE,required = false)Long endUtcForCreate,
            @RequestParam(value = START_UTC_FOR_UPDATE,required = false)Long startUtcForUpdate,
            @RequestParam(value = END_UTC_FOR_UPDATE,required = false)Long endUtcForUpdate,
            @RequestParam(value = TIMEZONE,required = false)String timezone
            ) {

        long loginId = securityBooster.requireUserId(request);

        return wL.loadPlansByTerms(loginId,state,name,startUtcForCreate,endUtcForCreate,startUtcForUpdate,endUtcForUpdate,timezone);
    }

    @GetMapping(WORK_SHEET_PATH+"/statistics")
    public MultipleItemsResult<WorkSheetProxy> loadWorksheetsByTerms(
            HttpServletRequest request,
            @RequestParam(STATE)Integer state,
            @RequestParam(START_UTC_FOR_DATE)Long startUtcForDate,
            @RequestParam(END_UTC_FOR_DATE)Long endUtcForDate,
            @RequestParam(START_UTC_FOR_UPDATE)Long startUtcForUpdate,
            @RequestParam(END_UTC_FOR_UPDATE)Long endUtcForUpdate,
            @RequestParam(PLAN_ID)String planDecodedId,
            @RequestParam(TIMEZONE)String timezone
    ) {
        long loginId = securityBooster.requireUserId(request);
        long planId = planDecodedId.trim().isEmpty() ? 0 : securityBooster.getStableCommonId(planDecodedId);
            return wL.loadWorksheetsByTerms(loginId,state,startUtcForDate,endUtcForDate
                ,startUtcForUpdate,endUtcForUpdate,timezone,planId);
    }


    @GetMapping(PLAN_PATH+"/countWSBased")
    public long getCountWSBasedOfPlan(
            HttpServletRequest request
            , @RequestParam(PLAN_ID)Integer planId ) {
        long loginId = securityBooster.requireUserId(request);
        return wL.getCountWSBasedOfPlan(planId,loginId);
    }

    @GetMapping(BALANCE_PATH +"/itemNames")
    public List<String> getPlanBalanceItemNames(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.getPlanBalanceItemNames(loginId);
    }

    @GetMapping(PLAN_PATH+"/tags")
    public List<String> loadAllPlanTagsByUser(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadAllPlanTagsByUser(loginId);
    }

    @GetMapping(WORK_SHEET_PATH+"/timezones")
    public List<String> loadAllWorkSheetTimezones(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadAllWorkSheetTimezones(loginId);
    }

    @GetMapping(WORK_SHEET_PATH+"/byDateScopeAndTimezone")
    public List<WorkSheetProxy> loadWorkSheetsByDateScopeAndTimezone(
            @RequestParam(START_DATE)Long startDateUtc
            ,@RequestParam(END_DATE)Long endDateUtc
            ,@RequestParam(TIMEZONE)String timezone
            ,@RequestParam("regarding_timezone")Boolean regarding
            ,HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        long startDate = getInDate(startDateUtc,timezone);
        long endDate = getInDate(endDateUtc,timezone);
        return wL.loadWorkSheetsByDateScopeAndTimezone(loginId, startDate, endDate,timezone,regarding);
    }



    @GetMapping(WORK_SHEET_PATH+"/tags")
    public List<String> loadAllWorkSheetTags(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return wL.loadAllWorkSheetTagsByUser(loginId);
    }


    @PostMapping(PLAN_PATH+"/copyPlanItemsById")
    private void copyPlanItemsById( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        long targetPlanId = param.getLong(TARGET_PLAN_ID);
        String templePlanId = param.getString(TEMPLATE_ID);
        long templateId = securityBooster.getStableCommonId(templePlanId);
        wL.copyPlanItemsFrom(loginId, targetPlanId, templateId);
    }

    @PostMapping(PLAN_PATH+"/calculatePlanStatesRoutinely")
    private void calculatePlanStatesRoutinely( HttpServletRequest request){
        long loginId = securityBooster.requireUserId(request);
        wL.calculatePlanStatesRoutinely(loginId);
    }

    @PatchMapping(PLAN_PATH+"/recalculatePlanState")
    private void recalculatePlanState( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(ID);
        wL.recalculatePlanState(loginId, planId);
    }

    @PostMapping(PLAN_PATH+"/syncPlanTagsToWorkSheet")
    private void syncPlanTagsToWorkSheet( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(ID);
        wL.syncPlanTagsToWorkSheet(loginId, planId);
    }

    @PostMapping(PLAN_PATH+"/tags/reset")
    private void resetPlanTags( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(ID);
        List<String> tags = param.getList(TAGS,String.class);
        wL.resetPlanTags(loginId, planId,tags);
    }

    @PostMapping(WORK_SHEET_PATH+"/tags/reset")
    private void resetWorkSheetTags( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        long wsId = param.getInteger(ID);
        List<String> tags = param.getList(TAGS,String.class);
        wL.resetWorkSheetTags(loginId, wsId,tags);
    }

    @PostMapping(PLAN_PATH+"/item")
    private void postPlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){

        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        String catName = param.getString(CAT_NAME);
        int val =  getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        PlanItemType type = PlanItemType.valueOfDBCode(param.getInteger(CAT_TYPE));
        int fatherId = param.getInteger(FATHER_ID);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);

        wL.addItemToPlan(loginId, planId, catName, val, note, type, fatherId, mappingVal);
    }

    @PatchMapping(PLAN_PATH+"/item/fold")
    private void patchPlanItemFold( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        int itemId = param.getInteger( ITEM_ID);
        boolean fold = param.getBoolean(FOLD);
        wL.savePlanItemFold(loginId, planId, itemId, fold);
    }

    @PatchMapping(PLAN_PATH+"/item")
    private void patchPlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        String catName = param.getString(CAT_NAME);
        int val = getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);
        int itemId = param.getInteger( ITEM_ID);
        wL.savePlanItem(loginId, planId, itemId, catName, val, note, mappingVal);
    }

    @DeleteMapping(PLAN_PATH+"/item")
    private void deletePlanItem( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromPlan(loginId, planId, itemId);
    }


    @PutMapping(PLAN_PATH)
    public void putPlan(
            HttpServletRequest request
            , @RequestBody JSONObject param ) {
        long loginId = securityBooster.requireUserId(request);
        int planId = param.getInteger(PLAN_ID);
        String name = param.getString(NAME);
        Long startDate = param.getLong(START_DATE);
        Long endDate = param.getLong(END_DATE);
        String note = param.getString(NOTE);
        int seqWeight = param.getInteger(SEQ_WEIGHT);
        String timezone = param.getString(TIMEZONE);
        List<PlanSetting> settings = transferToIntList(param.getJSONArray(PLAN_SETTING)).stream().map(PlanSetting::valueOfDBCode).collect(toList());
        Boolean recalculateState = param.getBoolean(RECALCULATE_STATE);
        wL.savePlan(loginId,planId,name,startDate,endDate,timezone,note,settings,seqWeight,recalculateState);
    }

    @PostMapping(PLAN_PATH)
    private long postPlan( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String name = param.getString(NAME);
        Long startDate = param.getLong(START_DATE);
        Long endDate = param.getLong(END_DATE);
        String note = param.getString(NOTE);
        String timezone = param.getString(TIMEZONE);
        return wL.createPlan(loginId, name, startDate, endDate,timezone, note);
    }



    @PostMapping(WORK_SHEET_PATH)
    private void openWorkSheetToday( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        int targetPlanId = param.getInteger(TARGET_PLAN_ID);
        wL.openWorkSheetToday(loginId, targetPlanId);
    }

}
