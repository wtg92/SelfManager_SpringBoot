package manager.controller;

import java.util.HashMap;
import java.util.Map;

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
        return AjaxResult.success(rlt);
    }

}
