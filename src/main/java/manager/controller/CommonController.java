package manager.controller;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import manager.booster.longRunningTasks.LongRunningTasksMessage;
import manager.booster.longRunningTasks.LongRunningTasksScheduler;
import manager.service.work.WorkService;
import manager.booster.SecurityBooster;
import manager.solr.constants.SolrConfig;
import manager.system.Gender;
import manager.system.Language;
import manager.system.VerifyUserMethod;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.util.UIUtil;
import manager.util.ZonedTimeUtils;
import org.springframework.web.bind.annotation.*;

import manager.data.AjaxResult;
import manager.system.SelfX;

import javax.annotation.Resource;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    SecurityBooster securityBooster;

    @Resource
    LongRunningTasksScheduler longRunningTasksScheduler;

    @GetMapping("/getBasicInfo")
    public AjaxResult getBasicInfo() {
    	Map<String,Object> rlt = new HashMap<>();
    	rlt.put("version", SelfX.VERSION);
    	rlt.put("appStartTime", SelfX.APP_STARTING_TIME);
    	rlt.put("appName", SelfX.BRAND_NAME);
        rlt.put("searchPageSize", SolrConfig.SEARCH_PAGE_SIZE);
        rlt.put("searchHighlightingTag",SolrConfig.HIGHLIGHT_TAG);
        rlt.put("searchHighlightingFragSize",SolrConfig.HIGHLIGHT_FRAGMENT_SIZE);
        return AjaxResult.success(rlt);
    }

    @GetMapping("/getVerifyUserMethod")
    public AjaxResult getVerifyUserMethod() {
        return AjaxResult.success(Arrays
                .stream(VerifyUserMethod.values())
                .filter(one->one!=VerifyUserMethod.UNDECIDED)
                .collect(Collectors.toList()));
    }

    @GetMapping("/getGenders")
    public AjaxResult getGenders() {
        return AjaxResult.success(Gender.getGenders());
    }

    @GetMapping("/getPlanSettings")
    public List<PlanSetting> getPlanSettings() {
        return PlanSetting.getSettings();
    }

    @GetMapping("/getPlanStates")
    public List<PlanState> getPlanStates() {
        return PlanState.getStates();
    }

    @GetMapping("/getPlanItemTypes")
    public List<PlanItemType> getPlanItemTypes() {
        return PlanItemType.getTypes();
    }

    @GetMapping("/languages")
    public List<String> getLanguages() {
        return Arrays.stream(Language.values()).
                filter(one->one != Language.UNKNOWN).map(one->one.name).toList();
    }


    @GetMapping("/getWorksheetNumOfOnePage")
    public int getWorksheetNumOfOnePage() {
        return WorkService.DEFAULT_WS_LIMIT_OF_ONE_PAGE;
    }

    @GetMapping("/getStableLoginId")
    public String getLoginId(@RequestHeader("Authorization") String authorizationHeader){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return securityBooster.encodeStableCommonId(loginId);
    }

    @GetMapping("/getLongRunningTasksMessage")
    public LongRunningTasksMessage getLongRunningTasksMessage(@RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return longRunningTasksScheduler.getRunningMsg(loginId);
    }



}
