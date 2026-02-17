package manager.booster;

import com.alibaba.fastjson2.JSON;
import com.google.api.client.util.Value;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.data.LoginInfo;
import manager.data.proxy.UserProxy;
import manager.data.proxy.career.PlanProxy;
import manager.entity.general.User;
import manager.exception.LogicException;
import manager.service.UserService;
import manager.service.books.SharingLinksAgent;
import manager.system.SelfXErrors;
import manager.util.SecurityBasis;
import manager.util.YZMUtil.YZMInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.logging.Logger;


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
 * 1. getLoginId -- Stable  -- 更改了鉴权机制后这个一直有就可以了
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

    @Value("${auth.cookie.alive-days}")
    private Integer COOKIE_ALIVE_DAYS ;

    @Value("${auth.cookie.secure}")
    private Boolean COOKIE_SECURE;

    @Autowired
    private UserService userService;



	final private static Logger logger = Logger.getLogger(SecurityBooster.class.getName());

    private static final String COOKIE_TOKEN_KEY = "access_token";

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

    private static class LoginPayload {
        public String userId;
        public Long sv;
        public long iat;
        public boolean rememberMe;
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

    public LoginRecord requireUser(HttpServletRequest request) throws LogicException {
        LoginRecord loginPayloadFromCookie = getLoginRecordFromCookie(request);
        if (loginPayloadFromCookie == null) {
            throw new LogicException(SelfXErrors.ILLEGAL_USER_TOKEN);
        }
        return loginPayloadFromCookie;
    }

    public long requireUserId(HttpServletRequest request) throws LogicException {
        return requireUser(request).userId;
	}

    public record LoginRecord(
            long userId,
            boolean rememberMe
    ) {}

    @Nullable
    public LoginRecord getLoginRecordFromCookie(HttpServletRequest request) {
        if (request == null) return null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (!COOKIE_TOKEN_KEY.equals(cookie.getName())) continue;
            String payloadJson;
            String token = cookie.getValue();
            try{
                payloadJson = basis.verifyAndGetPayload(token);
            }catch (LogicException e){
                return null;
            }
            LoginPayload payload = JSON.parseObject(payloadJson, LoginPayload.class);
            long userId = getStableCommonId(payload.userId);

            User userInternally = userService.getUserInternally(userId);

            if (!payload.sv.equals(userInternally.getSessionVersion())) {
                return null;
            }

            return new LoginRecord(userId, payload.rememberMe);
        }
        return null;
    }

    private void setCookieForLoginInfo(HttpServletResponse response,String token,boolean rememberMe){
        Cookie cookie = new Cookie(COOKIE_TOKEN_KEY, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(COOKIE_SECURE);
        cookie.setPath("/");
        if (rememberMe) {
            cookie.setMaxAge(60 * 60 * 24 * COOKIE_ALIVE_DAYS);
        } else {
            cookie.setMaxAge(-1);
        }

        response.addCookie(cookie);
    }

    public LoginInfo process(HttpServletResponse response, UserProxy proxy, boolean rememberMe) {
        assert proxy.user.getId() != 0;

        LoginInfo info = new LoginInfo(proxy);
        info.success = true;

        String encodedUserId = encodeStableCommonId(proxy.user.getId());
        info.userId = encodedUserId;
        info.portraitId = encodeStableCommonId(proxy.user.getPortraitId());

        LoginPayload payload = new LoginPayload();
        payload.userId = encodedUserId;
        payload.sv = proxy.user.getSessionVersion();
        payload.iat = System.currentTimeMillis();
        payload.rememberMe = rememberMe;

        String payloadJson = JSON.toJSONString(payload);
        String token = basis.signPayload(payloadJson);

        setCookieForLoginInfo(response,token,rememberMe);

        // 清理敏感信息
        proxy.user.setPassword("");
        proxy.user.setPwdSalt("");
        proxy.user.setId(0L);

        return info;
    }
	
	
	public PlanProxy process(PlanProxy plan) throws LogicException {
		plan.planId = encodeStableCommonId(plan.plan.getId());
		return plan;
	}

}
