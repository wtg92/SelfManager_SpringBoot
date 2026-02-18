package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.booster.CommonCipher;
import manager.booster.SecurityBooster;
import manager.data.LoginInfo;
import manager.data.UserBasicInfo;
import manager.data.proxy.UserProxy;
import manager.service.UserService;
import manager.system.Gender;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.YZMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static manager.system.SelfXParams.ACCOUNT;
import static manager.system.SelfXParams.ACCOUNT_PWD;
import static manager.system.SelfXParams.EMAIL;
import static manager.system.SelfXParams.EMAIL_VERIFY_CODE;
import static manager.system.SelfXParams.FIELD;
import static manager.system.SelfXParams.FOR_EMAIL;
import static manager.system.SelfXParams.GENDER;
import static manager.system.SelfXParams.ID;
import static manager.system.SelfXParams.IMG_SRC;
import static manager.system.SelfXParams.MOTTO;
import static manager.system.SelfXParams.NICK_NAME;
import static manager.system.SelfXParams.PORTRAIT_ID;
import static manager.system.SelfXParams.PWD;
import static manager.system.SelfXParams.REMEMBER_ME;
import static manager.system.SelfXParams.RESET_PWD_VAL;
import static manager.system.SelfXParams.SIGN_IN_METHOD;
import static manager.system.SelfXParams.TEL;
import static manager.system.SelfXParams.TEL_VERIFY_CODE;
import static manager.system.SelfXParams.TEMP_USER_ID;
import static manager.system.SelfXParams.VAL;
import static manager.system.SelfXParams.VERIFY_CODE;
import static manager.system.SelfXParams.VERIFY_SRC;
import static manager.system.SelfXParams.X;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SecurityBooster securityBooster;

    @Autowired
    private CommonCipher commonCipher;

    @Autowired
    private UserService userService;
    private static final String USER_PATH = "/user";
    @PatchMapping(USER_PATH)
    private void patchUser( HttpServletRequest request
            , @RequestBody JSONObject param ){
        long loginId = securityBooster.requireUserId(request);
        String nickName = param.getString(NICK_NAME);
        Gender gender = Gender.valueOfDBCode(param.getInteger(GENDER));
        String motto = param.getString(MOTTO);
        Long portraitId = commonCipher.getStableCommonId(param.getString(PORTRAIT_ID));
        userService.updateUser(loginId,nickName,gender,motto,portraitId);
    }

    @PostMapping("/signIn")
    public LoginInfo signIn(HttpServletResponse response,@RequestBody JSONObject param) {
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString(ACCOUNT);
        String accountPwd = param.getString(ACCOUNT_PWD);
        String email = param.getString(EMAIL);
        String emailVerifyCode = param.getString(EMAIL_VERIFY_CODE);
        String tel = param.getString(TEL);
        String telVerifyCode = param.getString(TEL_VERIFY_CODE);
        String tempUserId = param.getString(TEMP_USER_ID);
        Boolean rememberMe = param.getBoolean(REMEMBER_ME);
        return securityBooster.process(response,userService.signIn(tempUserId,method,account,accountPwd,email,emailVerifyCode,tel,telVerifyCode),rememberMe);
    }

    @PostMapping("/sendEmailVerifyCodeForSignIn")
    private void sendEmailVerifyCodeForSignIn(@RequestBody JSONObject param){
        String email = param.getString(EMAIL);
        userService.sendEmailVerifyCodeForSignIn(email);
    }

    @PostMapping("/sendTelVerifyCodeForSignIn")
    private void sendTelVerifyCodeForSignIn(@RequestBody JSONObject param){
        String email = param.getString(TEL);
        userService.sendTelVerifyCodeForSignIn(email);
    }

    @PostMapping("/sendVerifyCodeForResetPWD")
    private void sendVerifyCodeForResetPWD(@RequestBody JSONObject param){
        String val = param.getString(VAL);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString(ACCOUNT);
        userService.sendVerifyCodeForResetPWD(account, val, method);
    }

    @PostMapping("/sendVerifyCode")
    private void sendVerifyCode(@RequestBody JSONObject param){
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        int x = param.getInteger(X);
        String val =  param.getString(VAL);
        if(forEmail) {
            userService.sendEmailVerifyCodeForSignUp(val, tempUserId, x);
        }else {
            userService.sendTelVerifyCodeForSignUp(val, tempUserId, x);
        }
    }

    @PostMapping("/retrieveAccount")
    private void retrieveAccount(@RequestBody JSONObject param){
        String val = param.getString(VAL);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        userService.retrieveAccount(method, val);
    }

    @PostMapping("/resetPWD")
    private void resetPWD(@RequestBody JSONObject param){
        String val = param.getString(VERIFY_SRC);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString( ACCOUNT);
        String verifyCode = param.getString( VERIFY_CODE);
        String resetPWD = param.getString(RESET_PWD_VAL);
        userService.resetPWD(account,val, method,verifyCode,resetPWD);
    }

    @PostMapping("/existsUserByField")
    private boolean existsUserByField(@RequestBody JSONObject param){
        UserUniqueField field = UserUniqueField.valueOfDBCode(param.getInteger(FIELD));
        String val = param.getString(VAL);
        return userService.exists(field, val);
    }


    @PostMapping("/signUp")
    private void signUp(@RequestBody JSONObject param){
        String account = param.getString(ACCOUNT);
        String pwd = param.getString(PWD);
        String nickName = param.getString(NICK_NAME);
        Gender gender = Gender.valueOfDBCode(param.getInteger(GENDER));
        String email = param.getString(EMAIL);
        String emailVerifyCode = param.getString(EMAIL_VERIFY_CODE);
        String tel = param.getString(TEL);
        String telVerifyCode = param.getString(TEL_VERIFY_CODE);
        String tempUserId = param.getString(TEMP_USER_ID);
        userService.signUp(tempUserId, account, email, emailVerifyCode, tel, telVerifyCode, pwd, nickName, gender);
    }

    @PostMapping("/getYZM")
    private YZMUtil.YZMInfo getYZM(@RequestBody JSONObject param){
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        if(forEmail) {
            return SecurityBooster.process(userService.createEmailYZM(tempUserId, ""));
        }else {
            return SecurityBooster.process(userService.createTelYZM(tempUserId, ""));
        }
    }
//
//    @PostMapping("/confirmUserToken")
//    @Deprecated
//    private LoginInfo confirmUserToken(@RequestBody JSONObject param){
//        String token = param.getString(USER_TOKEN);
//        Long userId = null;
//        try{
////            userId = SecurityBooster.getUserId(token);
//        }catch (Exception e){
//            LoginInfo info = new LoginInfo();
//            info.success = false;
//            return info;
//        }
//
//        //验证账号
//        UserProxy user = userService.loadUser(userId, userId);
//        //验证密码
//        return securityBooster.confirmUser(user,token);
//    }

        @GetMapping("/me")
        public LoginInfo me(HttpServletRequest request, HttpServletResponse response) {
            SecurityBooster.LoginRecord loginRecord = securityBooster.requireUser(request);
            long userId = loginRecord.userId();
            UserProxy user = userService.loadUser(userId, userId);
            return securityBooster.process(response,user,loginRecord.rememberMe());
        }



    @PostMapping("/confirmTempUser")
    private boolean confirmTempUser(@RequestBody JSONObject param)  {
        String tempUserId = param.getString(TEMP_USER_ID);
        return userService.confirmTempUser(tempUserId);
    }

    @PostMapping("/getTempUser")
    private String getTempUser()  {
        return userService.createTempUser();
    }



    @PostMapping("/checkYZM")
    private YZMUtil.YZMInfo checkYZM(@RequestBody JSONObject param)  {
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        int x = param.getInteger(X);
        String imgSrc = param.getString(IMG_SRC);
        if(forEmail) {
            return SecurityBooster.process(userService.checkEmailYZMAndRefreshIfFailed(tempUserId,x,imgSrc));
        }else {
            return SecurityBooster.process(userService.checkTelYZMAndRefreshIfFailed(tempUserId,x, imgSrc));
        }
    }


    @GetMapping("/getUserBasicInfo")
    private UserBasicInfo getUserBasicInfo(@RequestParam(ID)String id,HttpServletRequest request)  {
        Long loginId = securityBooster.requireOptionalUserId(request);
        Long decodedId = commonCipher.getStableCommonId(id);
        return userService.getUserBasicInfo(loginId,decodedId);
    }
}
