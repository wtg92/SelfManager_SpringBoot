package manager.controller;
import com.alibaba.fastjson2.JSONObject;
import manager.booster.SecurityBooster;
import manager.data.LoginInfo;
import manager.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static manager.system.SelfXParams.*;
@RestController
@RequestMapping("/auth")
public class AuthController {


    @Resource
    private SecurityBooster securityBooster;

    @Resource
    private AuthService authService;

    @PostMapping("/google")
    public LoginInfo googleAuth(@RequestBody JSONObject param) {
        String token = param.getString(TOKEN);
        String tempUserId = param.getString(TEMP_USER_ID);
        return securityBooster.process(authService.googleAuth(token,tempUserId));
    }


    @PostMapping("/alipay")
    public LoginInfo alipayAuth(@RequestBody JSONObject param) {
        String token = param.getString(TOKEN);
        String tempUserId = param.getString(TEMP_USER_ID);
        return securityBooster.process(authService.alipayAuth(token,tempUserId));
    }

}
