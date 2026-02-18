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
import manager.system.SelfXErrors;
import manager.util.SecurityBasis;
import manager.util.YZMUtil.YZMInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.logging.Logger;



@Component
public class SecurityBooster {

	@Autowired
	SecurityBasis basis;

    @Value("${auth.cookie.alive-days}")
    private Integer COOKIE_ALIVE_DAYS ;

    @Value("${auth.cookie.secure}")
    private Boolean COOKIE_SECURE;

    @Autowired
    private UserService userService;


    @Autowired private CommonCipher commonCipher;


	final private static Logger logger = Logger.getLogger(SecurityBooster.class.getName());

    private static final String COOKIE_TOKEN_KEY = "access_token";

	private static final Encoder BASE64_ENCODER = Base64.getEncoder();

	public static boolean verifyUserPwd(User user, String pwd){
		return SecurityBasis.verifyUserPwd(user,pwd);
	}

	public static void encodeUserPwd(User user) throws LogicException {
		SecurityBasis.encodeUserPwd(user);
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
        return requireUser(request).userId();
	}

    public Long requireOptionalUserId(HttpServletRequest request) throws LogicException {
        LoginRecord loginRecordFromCookie = getLoginRecordFromCookie(request);
        return loginRecordFromCookie == null ? null : loginRecordFromCookie.userId();
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
            long userId = commonCipher.getStableCommonId(payload.userId);

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

        String encodedUserId = commonCipher.encodeStableCommonId(proxy.user.getId());
        info.userId = encodedUserId;
        info.portraitId = commonCipher.encodeStableCommonId(proxy.user.getPortraitId());

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
		plan.planId = commonCipher.encodeStableCommonId(plan.plan.getId());
		return plan;
	}

}
