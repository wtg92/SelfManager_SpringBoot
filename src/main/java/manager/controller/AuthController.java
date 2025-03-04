package manager.controller;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import manager.data.LoginInfo;
import manager.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import static manager.system.SMParams.*;
@RestController
@RequestMapping("/auth")
public class AuthController {



    @Resource
    private AuthService authService;

    @PostMapping("/google")
    public LoginInfo googleAuth(@RequestBody JSONObject param) {
        String token = param.getString(TOKEN);
        String tempUserId = param.getString(TEMP_USER_ID);
        return UserController.generateUnifiedLoginInfo(authService.googleAuth(token,tempUserId));
    }

    static class TokenRequest {
        private String token;
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
