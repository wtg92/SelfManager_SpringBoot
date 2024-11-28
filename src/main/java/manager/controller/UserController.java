package manager.controller;

import com.alibaba.fastjson2.JSONObject;
import manager.data.LoginInfo;
import manager.data.proxy.UserProxy;
import manager.exception.LogicException;
import manager.service.UserLogic;
import manager.servlet.ServletAdapter;
import manager.system.Gender;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.YZMUtil;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Resource;

import static manager.system.SMParm.*;
import static manager.system.SMParm.TEMP_USER_ID;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserLogic uL;

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
        UserProxy proxy = uL.signIn(tempUserId,method,account,accountPwd,email,emailVerifyCode,tel,telVerifyCode);
        return ServletAdapter.process(proxy);
    }

    @PostMapping("/sendEmailVerifyCodeForSignIn")
    private void sendEmailVerifyCodeForSignIn(@RequestBody JSONObject param){
        String email = param.getString(EMAIL);
        uL.sendEmailVerifyCodeForSignIn(email);
    }

    @PostMapping("/sendTelVerifyCodeForSignIn")
    private void sendTelVerifyCodeForSignIn(@RequestBody JSONObject param){
        String email = param.getString(TEL);
        uL.sendTelVerifyCodeForSignIn(email);
    }

    @PostMapping("/sendVerifyCodeForResetPWD")
    private void sendVerifyCodeForResetPWD(@RequestBody JSONObject param){
        String val = param.getString(VAL);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString(ACCOUNT);
        uL.sendVerifyCodeForResetPWD(account, val, method);
    }

    @PostMapping("/sendVerifyCode")
    private void sendVerifyCode(@RequestBody JSONObject param){
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        int x = param.getInteger(X);
        String val =  param.getString(VAL);
        if(forEmail) {
            uL.sendEmailVerifyCodeForSignUp(val, tempUserId, x);
        }else {
            uL.sendTelVerifyCodeForSignUp(val, tempUserId, x);
        }
    }

    @PostMapping("/retrieveAccount")
    private void retrieveAccount(@RequestBody JSONObject param){
        String val = param.getString(VAL);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        uL.retrieveAccount(method, val);
    }

    @PostMapping("/resetPWD")
    private void resetPWD(@RequestBody JSONObject param){
        String val = param.getString(VERIFY_SRC);
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString( ACCOUNT);
        String verifyCode = param.getString( VERIFY_CODE);
        String resetPWD = param.getString(RESET_PWD_VAL);
        uL.resetPWD(account,val, method,verifyCode,resetPWD);
    }

    @PostMapping("/existsUserByField")
    private boolean existsUserByField(@RequestBody JSONObject param){
        UserUniqueField field = UserUniqueField.valueOfDBCode(param.getInteger(FIELD));
        String val = param.getString(VAL);
        return uL.exists(field, val);
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
        uL.signUp(tempUserId, account, email, emailVerifyCode, tel, telVerifyCode, pwd, nickName, gender);
    }

    @PostMapping("/getYZM")
    private YZMUtil.YZMInfo getYZM(@RequestBody JSONObject param){
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        if(forEmail) {
            return ServletAdapter.process(uL.createEmailYZM(tempUserId, ""));
        }else {
            return ServletAdapter.process(uL.createTelYZM(tempUserId, ""));
        }
    }

    @PostMapping("/confirmUserToken")
    private LoginInfo confirmUserToken(@RequestBody JSONObject param){
        String token = param.getString(USER_TOKEN);
        long userId = ServletAdapter.getUserId(token);
        UserProxy user = uL.loadUser(userId, userId);
        return ServletAdapter.confirmUser(user,token);
    }

    @PostMapping("/confirmTempUser")
    private boolean confirmTempUser(@RequestBody JSONObject param) throws LogicException {
        String tempUserId = param.getString(TEMP_USER_ID);
        return uL.confirmTempUser(tempUserId);
    }

    @PostMapping("/getTempUser")
    private String getTempUser() throws LogicException {
        String tempUserId = uL.createTempUser();
        return tempUserId;
    }



    @PostMapping("/checkYZM")
    private YZMUtil.YZMInfo checkYZM(@RequestBody JSONObject param) throws LogicException {
        String tempUserId = param.getString(TEMP_USER_ID);
        boolean forEmail = param.getBooleanValue(FOR_EMAIL);
        int x = param.getInteger(X);
        String imgSrc = param.getString(IMG_SRC);
        if(forEmail) {
            return ServletAdapter.process(uL.checkEmailYZMAndRefreshIfFailed(tempUserId,x,imgSrc));
        }else {
            return ServletAdapter.process(uL.checkTelYZMAndRefreshIfFailed(tempUserId,x, imgSrc));
        }
    }
}
