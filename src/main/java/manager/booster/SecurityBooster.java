package manager.booster;

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

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.interfaces.Claim;

import manager.data.LoginInfo;
import manager.data.proxy.UserProxy;
import manager.data.proxy.career.PlanProxy;
import manager.entity.general.User;
import manager.exception.LogicException;
import manager.service.books.SharingLinksAgent;
import manager.system.SelfXErrors;
import manager.util.SecurityBasis;
import manager.util.YZMUtil.YZMInfo;
import org.springframework.stereotype.Component;


/**
 * Unstable means different at every time server reboots
 * 关于CommonId:
 * 1。由于我只是想掩盖数字的信息 所以它一定是long
 * 2. 所谓CommonId unstable的话 处理的是 不会被实际使用的 只是想加密一下请求间的数据  stable 是要被存起来的
 *
 *  能unstable的尽量unstable
 *  stable相对unstable 开发上更不容易出错
 *
 * 这里列举所有CommonId stable/unstable 理由
 * 1. getLoginId -- Unstable  -- 由于我希望每次重启都使得用户重新登录
 * 2. FileRecord -- Stable -- 1.用户头像 直接存id 因此两者都行  2.共享页 需要直接存在state里 因此必须stable
 * 3. UserBasicInfo.ID -- Stable -- 1.由于用户看见，变化不好 2.共享Link 存储的是加密后的userId 因此必须stable
 * 4. UserBasicInfo.头像ID -- Stable -- 1.本质是fileId file是Stable 因此这里是stable
 * 5. SharingBook最后更新人 -- Unstable -- TODO 这个还未使用 但将来提供一个接口 暂定unstable
 * 6. PlanId -- Stable -- 1.用户直接使用，变化不好
 *
 */
@Component
public class SecurityBooster {

	@Resource
	SecurityBasis basis;


	final private static Logger logger = Logger.getLogger(SecurityBooster.class.getName());

	private static final String USER_ID = "user_id";
	private static final String USER_PWD = "user_pwd";
	
	private static final Encoder BASE64_ENCODER = Base64.getEncoder();

	public String encodeSharingLinkURLParams(SharingLinksAgent.EncryptionParams params){
		String jsonString = JSON.toJSONString(params);
		return basis.encodeStableInfo(jsonString);
	}

	public SharingLinksAgent.EncryptionParams decodeSharingLinkURLParams(String params){
		try{
			String s = basis.decodeStableInfo(params);
			return JSON.parseObject(s,SharingLinksAgent.EncryptionParams.class);
		}catch (Exception e){
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
		}
	}



	public static boolean verifyUserPwd(User user, String pwd){
		return SecurityBasis.verifyUserPwd(user,pwd);
	}

	public static void encodeUserPwd(User user) throws LogicException {
		SecurityBasis.encodeUserPwd(user);
	}

	public static long getUserId(String token) throws LogicException {
		Claim rlt = getData(token, USER_ID);
		if (rlt.asString() == null) {
			logger.log(Level.SEVERE,"token 无 user_id\n" + token);
			throw new LogicException(SelfXErrors.ILLEGAL_USER_TOKEN, "无user id");
		}
		try {
			return getUnstableCommonId(rlt.asString());
		} catch (NumberFormatException e) {
			throw new LogicException(SelfXErrors.ILLEGAL_USER_TOKEN);
		} catch (Exception e){
			throw new LogicException(SelfXErrors.LOGIN_FAILED,e.getMessage());
		}
	}

	public static long getUnstableCommonId(String code) throws LogicException {
		try {
			return Long.parseLong(SecurityBasis.decodeUnstableInfo(code));
		} catch (Exception e) {
			throw new LogicException(SelfXErrors.ILLEGAL_CODE);
		}
	}

	public static String encodeUnstableCommonId(Object code) throws LogicException {
		return SecurityBasis.encodeUnstableInfo(code);
	}

	public long getStableCommonId(String code) throws LogicException {
		try {
			return Long.parseLong(basis.decodeStableInfo(code));
		} catch (Exception e) {
			e.printStackTrace();
			throw new LogicException(SelfXErrors.ILLEGAL_CODE);
		}
	}

	public String encodeStableCommonId(Object code) throws LogicException {
		return basis.encodeStableInfo(code);
	}

	/**
	  *   需要验证密码，不对给提示信息 而不是抛异常
	 * @throws LogicException 
	 */
	public LoginInfo confirmUser(UserProxy user,String token) throws LogicException {
		LoginInfo info = new LoginInfo(user);
		info.success = false;
		String pwd = getData(token, USER_PWD).asString();
		if (pwd == null) {
			throw new LogicException(SelfXErrors.ILLEGAL_USER_TOKEN, "无user pwd");
		}
		if(!user.user.getPassword().equals(pwd)) {
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
			createEmailYZM.backgroundImgBase64 = BASE64_ENCODER.encodeToString(out1.toByteArray());
			
			ImageIO.write(createEmailYZM.cutImg, "jpg", out2);
			createEmailYZM.cutImgBase64 = BASE64_ENCODER.encodeToString(out2.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new LogicException(SelfXErrors.YZM_ENCODE_ERROR);
		}
		
		return createEmailYZM;
	}
	/**
	 * 生成前台需要的token信息
	 * 现在是UserId(加密) pwd
	 * @throws LogicException 
	 */
	public LoginInfo process(UserProxy proxy) throws LogicException {
		assert proxy.user.getId() != 0;
		LoginInfo info = new LoginInfo(proxy);
		info.success = true;
		Map<String, String> data = new HashMap<String, String>();
		//这个是前台的登录token 因此unstable
		String encodedId = encodeUnstableCommonId(proxy.user.getId());
		data.put(USER_ID, encodedId);
		data.put(USER_PWD, proxy.user.getPassword());
		info.token = setData(data);
		info.userId = encodedId;
		//其实是所谓的fileId fileId是stable 因此这里是stable
		info.portraitId = encodeStableCommonId(proxy.user.getPortraitId());
		proxy.user.setPassword("");
		proxy.user.setPwdSalt("");
		proxy.user.setId((long)0);

		return info;
	}
	
	
	public PlanProxy process(PlanProxy plan) throws LogicException {
		plan.planId = encodeStableCommonId(plan.plan.getId());
		return plan;
	}

}
