package manager.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import manager.system.VerifyUserMethod;
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

}