package manager.controller;

import com.alibaba.fastjson.JSONObject;
import manager.data.AjaxResult;
import manager.data.proxy.UserProxy;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.logic.UserLogic;
import manager.servlet.ServletAdapter;
import manager.system.VerifyUserMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static manager.system.SMParm.*;
import static manager.system.SMParm.TEMP_USER_ID;
import static manager.util.UIUtil.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserLogic uL = UserLogic.getInstance();

    @PostMapping("/signIn")
    public AjaxResult signIn(@RequestBody JSONObject param) {
        VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(
                param.getInteger(SIGN_IN_METHOD));
        String account = param.getString(ACCOUNT);
        String accountPwd = param.getString(ACCOUNT_PWD);
        String email = param.getString(EMAIL);
        String emailVerifyCode = param.getString(EMAIL_VERIFY_CODE);
        String tel = param.getString(TEL);
        String telVerifyCode = param.getString(TEL_VERIFY_CODE);
        String tempUserId = param.getString(TEMP_USER_ID);
        logger.debug("signIn:" + account+"::"+email+"::"+tel);
        UserProxy proxy = uL.signIn(tempUserId,method,account,accountPwd,email,emailVerifyCode,tel,telVerifyCode);
        return AjaxResult.success(ServletAdapter.process(proxy));
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



}
