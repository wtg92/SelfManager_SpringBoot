package manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.logic.career.WorkLogic;
import manager.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static manager.system.SMParm.*;
import static manager.util.UIUtil.getLoginId;

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
}
