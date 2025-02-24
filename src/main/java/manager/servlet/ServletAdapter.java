package manager.servlet;

import static manager.util.TokenUtil.getData;
import static manager.util.TokenUtil.setData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.auth0.jwt.interfaces.Claim;

import manager.data.LoginInfo;
import manager.data.proxy.UserProxy;
import manager.data.proxy.career.PlanProxy;
import manager.exception.LogicException;
import manager.system.SMError;
import manager.util.SecurityUtil;
import manager.util.YZMUtil.YZMInfo;

/**
 * Servlet和前台通信的一个适配器
  * 职责： 管理token 
 *      id相关的加密解密 
  *            有一些logic返回的数据里带有敏感数据 需要Adapter来处理
 *           
 * TODO 有时间处理一下token的保存时间
 * 
 * @author 王天戈
 */
public class ServletAdapter {
	final private static Logger logger = Logger.getLogger(ServletAdapter.class.getName());

	private static final String USER_ID = "user_id";
	private static final String USER_PWD = "user_pwd";
	
	private static final Encoder BASE64_ENDOER = Base64.getEncoder();

	
	/**
	 * 现在确定userToken标准：读取出user_id 且能被解密
	 * @throws LogicException 
	 */
	public static long getUserId(String token) throws LogicException {
		Claim rlt = getData(token, USER_ID);
		if (rlt.asString() == null) {
			logger.log(Level.SEVERE,"token 无 user_id\n" + token);
			throw new LogicException(SMError.ILLEGAL_USER_TOKEN, "无user id");
		}
		try {
			return Long.parseLong(SecurityUtil.decodeInfo(rlt.asString()));
		} catch (NumberFormatException e) {
			throw new LogicException(SMError.ILLEGAL_USER_TOKEN,
					"format exception" + SecurityUtil.decodeInfo(rlt.asString()));
		}
	}
	
	public static long getCommonId(String code) throws LogicException {
		try {
			return Long.parseLong(SecurityUtil.decodeInfo(code));
		} catch (Exception e) {
			throw new LogicException(SMError.ILLEGAL_CODE);
		}
	}
	
	
	/**
	  *   需要验证密码，不对给提示信息 而不是抛异常
	 * @throws LogicException 
	 */
	public static LoginInfo confirmUser(UserProxy user,String token) throws LogicException {
		LoginInfo info = new LoginInfo(user);
		info.success = false;
		String pwd = getData(token, USER_PWD).asString();
		if (pwd == null) {
			throw new LogicException(SMError.ILLEGAL_USER_TOKEN, "无user pwd");
		}
		if(!user.user.getPassword().equals(pwd)) {
			info.errMsg = "登录密码失效，请重新登录";
			return info;
		}
		return process(user);
	}

	public static YZMInfo process(YZMInfo createEmailYZM) throws LogicException {
		if(createEmailYZM.checkSuccess) {
			return createEmailYZM;
		}
		createEmailYZM.xForCheck = 0;
		try(ByteArrayOutputStream out1 = new ByteArrayOutputStream() ;
				ByteArrayOutputStream out2 = new ByteArrayOutputStream();){
			ImageIO.write(createEmailYZM.backgroundImg, "jpg", out1);
			createEmailYZM.backgroundImgBase64 = BASE64_ENDOER.encodeToString(out1.toByteArray());
			
			ImageIO.write(createEmailYZM.cutImg, "jpg", out2);
			createEmailYZM.cutImgBase64 = BASE64_ENDOER.encodeToString(out2.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new LogicException(SMError.YZM_ENCODE_ERROR);
		}
		
		return createEmailYZM;
	}
	/**
	 * 生成前台需要的token信息
	 * 现在是UserId(加密) pwd
	 * @throws LogicException 
	 */
	public static LoginInfo process(UserProxy proxy) throws LogicException {
		assert proxy.user.getId() != 0;
		LoginInfo info = new LoginInfo(proxy);
		info.success = true;
		Map<String, String> data = new HashMap<String, String>();
		String encodedId = SecurityUtil.encodeInfo(proxy.user.getId());
		data.put(USER_ID, encodedId);
		data.put(USER_PWD, proxy.user.getPassword());
		info.token = setData(data);
		info.userId = encodedId;
		info.portraitId = SecurityUtil.encodeInfo(proxy.user.getPortraitId());
		proxy.user.setPassword("");
		proxy.user.setPwdSalt("");
		proxy.user.setId((long)0);

		return info;
	}
	
	
	public static PlanProxy process(PlanProxy plan) throws LogicException {
		plan.planId = SecurityUtil.encodeInfo(String.valueOf(plan.plan.getId()));
		return plan;
	}

}
