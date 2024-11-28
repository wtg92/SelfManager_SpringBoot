package manager.controller;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import manager.service.work.WorkLogic;
import manager.system.Gender;
import manager.system.Language;
import manager.system.VerifyUserMethod;
import manager.system.career.PlanItemType;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import manager.data.AjaxResult;
import manager.system.SM;

@RestController
@RequestMapping("/common")
public class CommonController {
	
    @GetMapping("/getBasicInfo")
    public AjaxResult getBasicInfo() {
    	Map<String,Object> rlt = new HashMap<>();
    	rlt.put("version", SM.VERSION);
    	rlt.put("appStartTime", SM.APP_STARTING_TIME);
    	rlt.put("appName",SM.BRAND_NAME);
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

    @GetMapping("/getTimezones")
    public Set<String> getTimezones() {
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        /**
         * 有些时区废弃
         */
        availableZoneIds.remove("America/Shiprock");
        return availableZoneIds;
    }

    @GetMapping("/languages")
    public List<String> getLanguages() {
        return Arrays.stream(Language.values()).map(one->one.name).toList();
    }


    @GetMapping("/getWorksheetNumOfOnePage")
    public int getWorksheetNumOfOnePage() {
        return WorkLogic.DEFAULT_WS_LIMIT_OF_ONE_PAGE;
    }
}
