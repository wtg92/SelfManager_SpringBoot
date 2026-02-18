package manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import manager.booster.CommonCipher;
import manager.booster.SecurityBooster;
import manager.booster.longRunningTasks.LongRunningTasksMessage;
import manager.booster.longRunningTasks.LongRunningTasksScheduler;
import manager.data.AjaxResult;
import manager.service.work.WorkService;
import manager.solr.constants.SolrConfig;
import manager.system.Gender;
import manager.system.Language;
import manager.system.SelfX;
import manager.system.VerifyUserMethod;
import manager.system.books.BooksConstants;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    SecurityBooster securityBooster;

    @Autowired
    CommonCipher commonCipher;

    @Autowired
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
        rlt.put("maxSizeOfTagsForLinks", BooksConstants.MAX_SIZE_OF_TAGS_FOR_LINKS);
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
    public String getLoginId(HttpServletRequest request){
        long loginId = securityBooster.requireUserId(request);
        return commonCipher.encodeStableCommonId(loginId);
    }

    @GetMapping("/getLongRunningTasksMessage")
    public LongRunningTasksMessage getLongRunningTasksMessage(HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        return longRunningTasksScheduler.getRunningMsg(loginId);
    }



}
