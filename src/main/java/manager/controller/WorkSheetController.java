package manager.controller;

import com.alibaba.fastjson.JSONObject;
import manager.data.proxy.career.PlanProxy;
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
import java.util.List;

import static java.util.stream.Collectors.toList;
import static manager.system.SMParm.*;
import static manager.util.UIUtil.*;

@RestController
@RequestMapping("/ws")
public class WorkSheetController {

    private static final Logger logger = LoggerFactory.getLogger(WorkSheetController.class);

    @Resource
    private WorkLogic wL;

    @PostMapping("/loadWorkSheetInfosRecently")
    public List<WorkSheet> loadWorkSheetInfosRecently(
            @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int page = param.getInteger(PAGE);
        return wL.loadWorkSheetInfosRecently(loginId, page);

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

    @PostMapping(PLAN_PATH+"/copyPlanItemsById")
    private void copyPlanItemsById( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        int targetPlanId = param.getInteger(TARGET_PLAN_ID);
        String templePlanId = param.getString(TEMPLATE_ID);
        int templateId = ServletAdapter.getCommonId(templePlanId);
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

        wL.savePlan(loginId,planId,name,startDate,endDate,timezone,note,settings,seqWeight);
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

}