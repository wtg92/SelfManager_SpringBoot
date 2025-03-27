package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import manager.data.LoginInfo;
import manager.data.UserBasicInfo;
import manager.data.proxy.UserProxy;
import manager.service.UserService;
import manager.booster.SecurityBooster;
import manager.system.Gender;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.UIUtil;
import manager.util.YZMUtil;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Resource;

import static manager.system.SelfXParams.*;
import static manager.system.SelfXParams.TEMP_USER_ID;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private SecurityBooster securityBooster;

    @Resource
    private UserService userService;
    private static final String USER_PATH = "/user";
    @PatchMapping(USER_PATH)
    private void patchUser( @RequestHeader("Authorization") String authorizationHeader
            , @RequestBody JSONObject param ){
        long loginId = UIUtil.getLoginId(authorizationHeader);
        String nickName = param.getString(NICK_NAME);
        Gender gender = Gender.valueOfDBCode(param.getInteger(GENDER));
        String motto = param.getString(MOTTO);
        Long portraitId = securityBooster.getStableCommonId(param.getString(PORTRAIT_ID));
        userService.updateUser(loginId,nickName,gender,motto,portraitId);
    }


    @PostMapping("/signIn")
    public LoginInfo signIn(@RequestBody JSONObject param) {
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString(ACCOUNT);
        String accountPwd = param.getString(ACCOUNT_PWD);
        String email = param.getString(EMAIL);
        String emailVerifyCode = param.getString(EMAIL_VERIFY_CODE);
        String tel = param.getString(TEL);
        String telVerifyCode = param.getString(TEL_VERIFY_CODE);
        String tempUserId = param.getString(TEMP_USER_ID);
        return securityBooster.process(userService.signIn(tempUserId,method,account,accountPwd,email,emailVerifyCode,tel,telVerifyCode)) ;
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

    @PostMapping("/confirmUserToken")
    private LoginInfo confirmUserToken(@RequestBody JSONObject param){
        String token = param.getString(USER_TOKEN);
        long userId = SecurityBooster.getUserId(token);
        UserProxy user = userService.loadUser(userId, userId);
        return securityBooster.confirmUser(user,token);
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
    private UserBasicInfo getUserBasicInfo(@RequestParam(ID)String id,@RequestHeader(value = "Authorization",required = false)
        String authorizationHeader)  {
        long loginId = authorizationHeader == null ? 0 : UIUtil.getLoginId(authorizationHeader);
        Long decodedId = securityBooster.getStableCommonId(id);
        return userService.getUserBasicInfo(loginId,decodedId);
    }
}
