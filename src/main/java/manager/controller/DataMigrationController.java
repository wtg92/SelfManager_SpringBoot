package manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import manager.booster.SecurityBooster;
import manager.service.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/migration")
public class DataMigrationController {

    @Autowired
    private DataMigrationService migrationService;

    @Autowired
    private SecurityBooster securityBooster;

    @PostMapping("/doFullMigrateOfV1")
    public void doFullMigrateOfV1(
            HttpServletRequest request) {
        long loginId = securityBooster.requireUserId(request);
        migrationService.doFullMigrateOfV1(loginId);
    }

    @PostMapping("/checkLatestMigration")
    public Map<String,Object> checkLatestMigration(HttpServletRequest request){
        long loginId = securityBooster.requireUserId(request);
        return migrationService.checkLatestMigration(loginId);
    }
}
