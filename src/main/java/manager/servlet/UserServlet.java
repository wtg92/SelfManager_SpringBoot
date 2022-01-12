package manager.servlet;

import static java.util.stream.Collectors.toList;
import static manager.system.SMParm.ACCOUNT;
import static manager.system.SMParm.ACCOUNT_PWD;
import static manager.system.SMParm.EMAIL;
import static manager.system.SMParm.EMAIL_VERIFY_CODE;
import static manager.system.SMParm.FIELD;
import static manager.system.SMParm.FOR_EMAIL;
import static manager.system.SMParm.GENDER;
import static manager.system.SMParm.GROUP_ID;
import static manager.system.SMParm.IMG_SRC;
import static manager.system.SMParm.METHOD;
import static manager.system.SMParm.NICK_NAME;
import static manager.system.SMParm.OP;
import static manager.system.SMParm.PERMS;
import static manager.system.SMParm.PWD;
import static manager.system.SMParm.RESET_PWD_VAL;
import static manager.system.SMParm.SIGN_IN_METHOD;
import static manager.system.SMParm.TEL;
import static manager.system.SMParm.TEL_VERIFY_CODE;
import static manager.system.SMParm.TEMP_USER_ID;
import static manager.system.SMParm.USER_TOKEN;
import static manager.system.SMParm.VAL;
import static manager.system.SMParm.VERIFY_CODE;
import static manager.system.SMParm.VERIFY_SRC;
import static manager.system.SMParm.X;
import static manager.util.UIUtil.getLoginerId;
import static manager.util.UIUtil.getNonNullParam;
import static manager.util.UIUtil.getNonNullParamInBool;
import static manager.util.UIUtil.getNonNullParamInInt;
import static manager.util.UIUtil.getNonNullParamsInInt;
import static manager.util.UIUtil.getNullObjJSON;
import static manager.util.UIUtil.getParamJSON;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

import manager.data.proxy.UserProxy;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.logic.UserLogic;
import manager.system.Gender;
import manager.system.SMError;
import manager.system.SMOP;
import manager.system.SMPerm;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;

@WebServlet(name="UserServlet",urlPatterns = "/UserServlet")
public class UserServlet extends SMServlet{
	
	private UserLogic uL = UserLogic.getInstance();
	
	private static final long serialVersionUID = 2441620312018018123L;

	@Override
	public String process(HttpServletRequest request) throws LogicException, ServletException, IOException, DBException {
		SMOP op = SMOP.valueOfName(getNonNullParam(request,OP));
		switch (op) {
		case U_SIGN_IN:
			return signIn(request);
		case U_CONFIRM_USER_TOKEN:
			return confirmUserToken(request);
		case U_CONFIRM_TEMP_USER:
			return confirmTempUser(request);
		case U_GET_TEMP_USER:
			return getTempUser(request);
		case U_GET_YAM:
			return getYZM(request);
		case U_CHECK_YZM:
			return checkYZM(request);
		case U_SEND_VERIFY_CODE:
			return sendVerifyCode(request);
		case U_EXISTS_USER_WITH_FIELD:
			return existsUserByField(request);
		case U_SIGN_UP:
			return signUp(request);
		case U_LOAD_USER_SUMMARY:
			return loadUserSummary(request);
		case U_LOAD_ALL_USER_GROUPS:
			return loadAllUserGroups(request);
		case U_LOAD_USERS_OF_GROUP:
			return loadUsersOfGroup(request);
		case U_LOAD_PERMS_OF_GROUP:
			return loadPermsOfGroup(request);
		case U_OVERRIDE_GROUP_PERMS:
			return overrideGroupPerms(request);
		case U_SEND_EMAIL_VERIFY_CODE_FOR_SIGN_IN:
			return sendEmailVerifyCodeForSignIn(request);
		case U_SEND_TEL_VERIFY_CODE_FOR_SIGN_IN:
			return sendTelVerifyCodeForSignIn(request);
		case U_RETRIEVE_ACCOUNT:
			return retrieveAccount(request);
		case U_SEND_VERIFY_CODE_FOR_RESET_PWD:
			return sendVerifyCodeForResetPWD(request);
		case U_RESET_PWD:
			return resetPWD(request);
		default:
			assert false : op.getName();
			throw new LogicException(SMError.UNKOWN_OP,getNonNullParam(request,OP));
		}
	}
	
	private String resetPWD(HttpServletRequest request) throws LogicException, DBException {
		String val = getNonNullParam(request, VERIFY_SRC);
		VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(getNonNullParamInInt(request, METHOD)); 
		String account = getNonNullParam(request, ACCOUNT);
		String verifyCode = getNonNullParam(request, VERIFY_CODE);
		String resetPWD = getNonNullParam(request,RESET_PWD_VAL);
		uL.resetPWD(account,val, method,verifyCode,resetPWD);
		return getNullObjJSON();
	}

	private String sendVerifyCodeForResetPWD(HttpServletRequest request) throws LogicException, DBException {
		String val = getNonNullParam(request, VAL);
		VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(getNonNullParamInInt(request, METHOD)); 
		String account = getNonNullParam(request, ACCOUNT);
		uL.sendVerifyCodeForResetPWD(account, val, method);
		return getNullObjJSON();
	}

	private String retrieveAccount(HttpServletRequest request) throws LogicException, DBException {
		String val = getNonNullParam(request, VAL);
		VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(getNonNullParamInInt(request, METHOD)); 
		uL.retrieveAccount(method, val);
		return getNullObjJSON();
	}

	private String sendTelVerifyCodeForSignIn(HttpServletRequest request) throws LogicException, DBException {
		String tel = getNonNullParam(request, TEL);
		uL.sendTelVerifyCodeForSignIn(tel);
		return getNullObjJSON();
	}

	private String sendEmailVerifyCodeForSignIn(HttpServletRequest request) throws LogicException, DBException {
		String email = getNonNullParam(request, EMAIL);
		uL.sendEmailVerifyCodeForSignIn(email);
		return getNullObjJSON();
	}

	private String overrideGroupPerms(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long groupId = getNonNullParamInInt(request,GROUP_ID);
		List<SMPerm> perms = getNonNullParamsInInt(request,PERMS).stream().map(SMPerm::valueOfDBCode).collect(toList());
		uL.overrideGroupPerms(perms, groupId, loginerId);
		return getNullObjJSON();
	}

	@SuppressWarnings("unchecked")
	private String loadPermsOfGroup(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long groupId = getNonNullParamInInt(request,GROUP_ID);
		SerializeConfig conf = new SerializeConfig();
		conf.configEnumAsJavaBean(SMPerm.class);
		return JSON.toJSONString(uL.loadPermsOfGroup(groupId, loginerId),conf);
	}

	private String loadUsersOfGroup(HttpServletRequest request) throws LogicException, DBException {
		long loginerId = getLoginerId(request);
		long groupId = getNonNullParamInInt(request,GROUP_ID);
		return JSON.toJSONString(uL.loadUsersOfGroup(groupId, loginerId));
	}

	private String loadAllUserGroups(HttpServletRequest request) throws LogicException, DBException {
		long userId = getLoginerId(request);
		return JSON.toJSONString(uL.loadAllUserGroups(userId));
	}

	private String loadUserSummary(HttpServletRequest request) throws LogicException, DBException {
		long userId = getLoginerId(request);
		return JSON.toJSONString(uL.loadUserSummary(userId));
	}

	private String signUp(HttpServletRequest request) throws LogicException, DBException {
		String account = getNonNullParam(request, ACCOUNT);
		String pwd = getNonNullParam(request, PWD);
		String nickName = getNonNullParam(request, NICK_NAME);
		Gender gender = Gender.valueOfDBCode(getNonNullParamInInt(request, GENDER));
		String email = getNonNullParam(request, EMAIL);
		String emailVerifyCode = getNonNullParam(request, EMAIL_VERIFY_CODE);
		String tel = getNonNullParam(request, TEL);
		String telVerifyCode = getNonNullParam(request, TEL_VERIFY_CODE);
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		uL.signUp(tempUserId, account, email, emailVerifyCode, tel, telVerifyCode, pwd, nickName, gender);
		return getNullObjJSON();
	}

	private String existsUserByField(HttpServletRequest request) throws LogicException, DBException {
		UserUniqueField field = UserUniqueField.valueOfDBCode(getNonNullParamInInt(request, FIELD));
		String val = getNonNullParam(request, VAL);
		return getParamJSON(uL.exists(field, val));
	}

	private String sendVerifyCode(HttpServletRequest request) throws LogicException {
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		boolean forEmail = getNonNullParamInBool(request, FOR_EMAIL);
		int x = getNonNullParamInInt(request, X);
		String val = getNonNullParam(request, VAL);
		if(forEmail) {
			uL.sendEmailVerifyCodeForSignUp(val, tempUserId, x);
		}else {
			uL.sendTelVerifyCodeForSignUp(val, tempUserId, x);
		}
		return getNullObjJSON();
	}

	private String checkYZM(HttpServletRequest request) throws LogicException {
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		boolean forEmail = getNonNullParamInBool(request, FOR_EMAIL);
		int x = getNonNullParamInInt(request, X);
		String imgSrc = getNonNullParam(request, IMG_SRC);
		if(forEmail) {
			return JSON.toJSONString(ServletAdapter.process(uL.checkEmailYZMAndRefreshIfFailed(tempUserId,x,imgSrc)));
		}else {
			return JSON.toJSONString(ServletAdapter.process(uL.checkTelYZMAndRefreshIfFailed(tempUserId,x, imgSrc)));
		}

	}

	private String getYZM(HttpServletRequest request) throws LogicException {
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		boolean forEmail = getNonNullParamInBool(request, FOR_EMAIL);
		if(forEmail) {
			return JSON.toJSONString(ServletAdapter.process(uL.createEmailYZM(tempUserId, "")));
		}else {
			return JSON.toJSONString(ServletAdapter.process(uL.createTelYZM(tempUserId, "")));
		}
	}

	private String getTempUser(HttpServletRequest request) throws LogicException {
		String tempUserId = uL.createTempUser();
		return getParamJSON(tempUserId);
	}

	private String confirmTempUser(HttpServletRequest request) throws LogicException {
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		return getParamJSON(uL.confirmTempUser(tempUserId));
	}

	private String confirmUserToken(HttpServletRequest request) throws LogicException, DBException {
		String token = getNonNullParam(request,USER_TOKEN); 
		long userId = ServletAdapter.getUserId(token);
		UserProxy user = uL.loadUser(userId, userId);
		return JSON.toJSONString(ServletAdapter.confirmUser(user,token));
	}
	
	private String signIn(HttpServletRequest request) throws LogicException, DBException {
		VerifyUserMethod method = VerifyUserMethod.valueOfDBCode(getNonNullParamInInt(request, SIGN_IN_METHOD)); 
		String account = getNonNullParam(request, ACCOUNT);
		String accountPwd = getNonNullParam(request, ACCOUNT_PWD);
		String email = getNonNullParam(request, EMAIL);
		String emailVerifyCode = getNonNullParam(request, EMAIL_VERIFY_CODE);
		String tel = getNonNullParam(request, TEL);
		String telVerifyCode = getNonNullParam(request, TEL_VERIFY_CODE);
		String tempUserId = getNonNullParam(request, TEMP_USER_ID);
		UserProxy proxy = uL.signIn(tempUserId,method,account,accountPwd,email,emailVerifyCode,tel,telVerifyCode);
		return JSON.toJSONString(ServletAdapter.process(proxy));
	}
	
	
	
}
