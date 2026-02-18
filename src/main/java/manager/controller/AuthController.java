package manager.controller;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
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
    public LoginInfo googleAuth(HttpServletResponse response, @RequestBody JSONObject param) {
        String token = param.getString(TOKEN);
        String tempUserId = param.getString(TEMP_USER_ID);
        Boolean rememberMe = param.getBoolean(REMEMBER_ME);
        return securityBooster.process(response,authService.googleAuth(token,tempUserId),rememberMe);
    }

    @PostMapping("/alipay")
    public LoginInfo alipayAuth(HttpServletResponse response,@RequestBody JSONObject param) {
        String token = param.getString(TOKEN);
        String tempUserId = param.getString(TEMP_USER_ID);
        Boolean rememberMe = param.getBoolean(REMEMBER_ME);
        return securityBooster.process(response,authService.alipayAuth(token,tempUserId),rememberMe);
    }

}
