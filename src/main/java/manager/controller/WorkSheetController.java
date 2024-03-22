package manager.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import manager.data.proxy.career.PlanProxy;
import manager.data.proxy.career.WorkSheetProxy;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.logic.career.WorkLogic;
import manager.servlet.ServletAdapter;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static manager.system.SMParm.*;
import static manager.util.UIUtil.*;

@RestController
@RequestMapping("/ws")
public class WorkSheetController {

    @Resource
    private WorkLogic wL;

    @GetMapping("/worksheetRecently")
    public List<WorkSheet> loadWorkSheetInfosRecently(
            @RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(PAGE)Integer page ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadWorkSheetInfosRecently(loginId, page);
    }

    @GetMapping("/worksheetsCount")
    public long getWorksheetsCount(
            @RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(DATE)Long date
            ,@RequestParam(TIMEZONE)String timezone) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.getWorkSheetCount(loginId,date,timezone);
    }

    @GetMapping(WORK_SHEET_PATH)
    public WorkSheetProxy getWorksheet(
            @RequestHeader("Authorization") String authorizationHeader
            ,@RequestParam(ID)Long wsId) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadWorkSheet(loginId, wsId);
    }

    @PatchMapping(WORK_SHEET_PATH)
    public void patchWorksheet(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        String note = param.getString(NOTE);
        wL.saveWorkSheet(loginId, wsId, note);
    }
    @PatchMapping(WORK_SHEET_PATH+"/planId")
    public void patchWorksheetPlanId(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        long wsId = param.getLong(WS_ID);
        long planId = ServletAdapter.getCommonId(param.getString(PLAN_ID));
        wL.saveWorkSheetPlanId(loginId, wsId, planId);
    }


    @PatchMapping(WORK_SHEET_PATH+"/assumeFinished")
    public void assumeWorkSheetFinished(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        wL.assumeWorkSheetFinished(loginId, wsId);
    }

    @PatchMapping(WORK_SHEET_PATH+"/cancelAssumeFinished")
    public void cancelAssumeWorkSheetFinished(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        wL.cancelAssumeWorkSheetFinished(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/planItem")
    private void postWorksheetPlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
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
    private void postWorkItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
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
    private void deleteWorkItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(WS_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromWorkSheet(loginId, wsId, itemId);
    }

    @PostMapping(WORK_SHEET_PATH+"/workItem/syncAllToDept")
    private void syncAllToDept( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(WS_ID);
        wL.syncAllToPlanDept(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/workItem/syncToDept")
    private void syncToPlanDept( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(WS_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.syncToPlanDept(loginId, wsId, itemId);
    }

    @PatchMapping(WORK_SHEET_PATH+"/workItem")
    private WorkSheetProxy patchWorkItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
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
    private void patchWorkItemPlanItemId( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(WS_ID);
        int workItemId = param.getInteger(WORK_ITEM_ID);
        int planItemId = param.getInteger(PLAN_ITEM_ID);
        wL.saveWorkItemPlanItemId(loginId, wsId, workItemId, planItemId);
    }



    @PatchMapping(WORK_SHEET_PATH+"/planItem")
    private void patchWorksheetPlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        String catName = param.getString(CAT_NAME);
        int val = getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);
        int itemId = param.getInteger( ITEM_ID);
        wL.saveWSPlanItem(loginId, wsId, itemId, catName, val, note, mappingVal);
    }

    @PatchMapping(WORK_SHEET_PATH+"/planItem/fold")
    private void patchWorksheetPlanItemFold( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        int itemId = param.getInteger( ITEM_ID);
        boolean fold = param.getBoolean(FOLD);
        wL.saveWSPlanItemFold(loginId, wsId, itemId, fold);
    }


    @DeleteMapping(WORK_SHEET_PATH+"/planItem")
    private void deleteWorksheetPlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromWSPlan(loginId, wsId, itemId);
    }

    @DeleteMapping(WORK_SHEET_PATH)
    public void deleteWorksheet(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int wsId = param.getInteger(ID);
        wL.deleteWorkSheet(loginId, wsId);
    }

    @PostMapping(WORK_SHEET_PATH+"/calculateStatesRoutinely")
    private void calculateWorksheetStatesRoutinely( @RequestHeader("Authorization") String authorizationHeader){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        wL.calculateWorksheetStatesRoutinely(loginId);
    }


    @PostMapping("/loadActivePlans")
    public List<Plan> loadActivePlans(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadActivePlans(loginId);
    }

    @PostMapping("/abandonPlan")
    public void abandonPlan(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(PLAN_ID);
        wL.abandonPlan(loginId, planId);
    }

    @PostMapping("/finishPlan")
    public void finishPlan(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(PLAN_ID);
        wL.finishPlan(loginId, planId);
    }

    private static final String PLAN_PATH = "/plan";

    private static final String WORK_SHEET_PATH = "/ws";


    @GetMapping(PLAN_PATH)
    public PlanProxy getPlan(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(PLAN_ID)Integer planId ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return ServletAdapter.process(wL.loadPlan(loginId, planId));
    }

    @GetMapping(PLAN_PATH+"/countWSBased")
    public long getCountWSBasedOfPlan(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestParam(PLAN_ID)Integer planId ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.getCountWSBasedOfPlan(planId,loginId);
    }



    @GetMapping(PLAN_PATH+"/deptItemNames")
    public List<String> getPlanDeptItemNames(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadPlanDeptItemNames(loginId);
    }

    @GetMapping(PLAN_PATH+"/tags")
    public List<String> loadAllPlanTagsByUser(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadAllPlanTagsByUser(loginId);
    }

    @GetMapping(WORK_SHEET_PATH+"/timezones")
    public List<String> loadAllWorkSheetTimezones(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadAllWorkSheetTimezones(loginId);
    }

    @GetMapping(WORK_SHEET_PATH+"/byDateScopeAndTimezone")
    public List<WorkSheetProxy> loadWorkSheetsByDateScopeAndTimezone(
            @RequestParam(START_DATE)Long startDateUtc
            ,@RequestParam(END_DATE)Long endDateUtc
            ,@RequestParam(TIMEZONE)String timezone
            ,@RequestParam("regarding_timezone")Boolean regarding
            ,@RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        long startDate = getInDate(startDateUtc,timezone);
        long endDate = getInDate(endDateUtc,timezone);
        return wL.loadWorkSheetsByDateScopeAndTimezone(loginId, startDate, endDate,timezone,regarding);
    }



    @GetMapping(WORK_SHEET_PATH+"/tags")
    public List<String> loadAllWorkSheetTags(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return wL.loadAllWorkSheetTagsByUser(loginId);
    }


    @PostMapping(PLAN_PATH+"/copyPlanItemsById")
    private void copyPlanItemsById( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        long targetPlanId = param.getLong(TARGET_PLAN_ID);
        String templePlanId = param.getString(TEMPLATE_ID);
        long templateId = ServletAdapter.getCommonId(templePlanId);
        wL.copyPlanItemsFrom(loginId, targetPlanId, templateId);
    }

    @PostMapping(PLAN_PATH+"/calculatePlanStatesRoutinely")
    private void calculatePlanStatesRoutinely( @RequestHeader("Authorization") String authorizationHeader){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        wL.calculatePlanStatesRoutinely(loginId);
    }

    @PatchMapping(PLAN_PATH+"/recalculatePlanState")
    private void recalculatePlanState( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(ID);
        wL.recalculatePlanState(loginId, planId);
    }

    @PostMapping(PLAN_PATH+"/syncPlanTagsToWorkSheet")
    private void syncPlanTagsToWorkSheet( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(ID);
        wL.syncPlanTagsToWorkSheet(loginId, planId);
    }

    @PostMapping(PLAN_PATH+"/tags/reset")
    private void resetPlanTags( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(ID);
        List<String> tags = param.getList(TAGS,String.class);
        wL.resetPlanTags(loginId, planId,tags);
    }

    @PostMapping(WORK_SHEET_PATH+"/tags/reset")
    private void resetWorkSheetTags( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        long wsId = param.getInteger(ID);
        List<String> tags = param.getList(TAGS,String.class);
        wL.resetWorkSheetTags(loginId, wsId,tags);
    }

    @PostMapping(PLAN_PATH+"/item")
    private void postPlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){

        long loginId = UIUtil.getLoginId(authorizationHeader);
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
    private void patchPlanItemFold( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(PLAN_ID);
        int itemId = param.getInteger( ITEM_ID);
        boolean fold = param.getBoolean(FOLD);
        wL.savePlanItemFold(loginId, planId, itemId, fold);
    }

    @PatchMapping(PLAN_PATH+"/item")
    private void patchPlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(PLAN_ID);
        String catName = param.getString(CAT_NAME);
        int val = getParamIntegerOrZeroDefault(param,VAL);
        String note = param.getString(NOTE);
        double mappingVal = getParamDoubleOrZeroDefault(param,MAPPING_VAL);
        int itemId = param.getInteger( ITEM_ID);
        wL.savePlanItem(loginId, planId, itemId, catName, val, note, mappingVal);
    }

    @DeleteMapping(PLAN_PATH+"/item")
    private void deletePlanItem( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int planId = param.getInteger(PLAN_ID);
        int itemId = param.getInteger(ITEM_ID);
        wL.removeItemFromPlan(loginId, planId, itemId);
    }


    @PutMapping(PLAN_PATH)
    public void putPlan(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
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
    private long postPlan( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String name = param.getString(NAME);
        Long startDate = param.getLong(START_DATE);
        Long endDate = param.getLong(END_DATE);
        String note = param.getString(NOTE);
        String timezone = param.getString(TIMEZONE);
        return wL.createPlan(loginId, name, startDate, endDate,timezone, note);
    }



    @PostMapping(WORK_SHEET_PATH)
    private void openWorkSheetToday( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int targetPlanId = param.getInteger(TARGET_PLAN_ID);
        wL.openWorkSheetToday(loginId, targetPlanId);
    }

}
