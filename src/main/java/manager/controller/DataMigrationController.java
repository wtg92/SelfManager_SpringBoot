package manager.controller;

import manager.entity.general.career.WorkSheet;
import manager.logic.career.DataMigrationService;
import manager.util.UIUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static manager.system.SMParm.PAGE;

@RestController
@RequestMapping("/migration")
public class DataMigrationController {

    @Resource
    private DataMigrationService migrationService;


    @PostMapping("/doFullMigrateOfV1")
    public void doFullMigrateOfV1(
            @RequestHeader("Authorization") String authorizationHeader) {
        long loginId = UIUtil.getLoginId(authorizationHeader);
        migrationService.doFullMigrateOfV1(loginId);
    }

    @PostMapping("/checkLatestMigration")
    public Map<String,Object> checkLatestMigration(@RequestHeader("Authorization") String authorizationHeader){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        return migrationService.checkLatestMigration(loginId);
    }
}
