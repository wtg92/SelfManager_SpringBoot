package manager.service;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import manager.data.proxy.UserProxy;
import manager.exception.LogicException;
import manager.system.auth.AlipayConfig;
import manager.system.auth.AuthUniqueFields;
import manager.system.DBConstants;
import manager.system.Gender;
import manager.system.SelfXErrors;
import manager.system.auth.ThirdPartConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {

    @Resource
    UserService userService;

    @Value("${google.clientId}")
    private String GOOGLE_CLIENT_ID;

    @Resource
    AlipayConfig alipayConfig;

    public static String getUniqueDBField(AuthUniqueFields field){
        switch (field){
            case GMAIL -> {
                return DBConstants.F_EMAIL;
            }
            case ALIPAY -> {
                return DBConstants.F_ALIPAY_OPEN_ID;
            }
            default ->
                throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
        }
    }



    public UserProxy googleAuth(String token,String uuId) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
        GoogleIdToken idToken;
        try{
            idToken = verifier.verify(token);
        }catch (Exception e){
            throw new LogicException(SelfXErrors.THIRTY_PARTY_AUTH, ThirdPartConstants.GOOGLE);
        }
        if( idToken == null){
            throw new LogicException(SelfXErrors.THIRTY_PARTY_AUTH, ThirdPartConstants.GOOGLE);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String fieldName = getUniqueDBField(AuthUniqueFields.GMAIL);
        UserProxy userProxy = userService.retrieveAuthUserByUniqueFiledForSignIn(
                uuId
                , fieldName
                ,email
                );
        if(userProxy != null){
            return userProxy;
        }
        String nickName = (String) payload.get("name");
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        userService.signUpDirectly(uuId,username
                ,password
                ,nickName
                , Gender.UNKNOWN
                ,email
                ,null
                ,null);
        return userService.retrieveAuthUserByUniqueFiledForSignIn(
                uuId
                , fieldName
                ,email
        );
    }

    public UserProxy alipayAuth(String code, String uuId) {
        String openId = null;
        String nickName = null;
        try {
            // 使用授权码换取访问令牌
            AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
            oauthTokenRequest.setCode(code);
            oauthTokenRequest.setGrantType("authorization_code");
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayConfig.getAlipayClient().execute(oauthTokenRequest);
            String accessToken = oauthTokenResponse.getAccessToken();

            // 使用访问令牌获取用户信息
            AlipayUserInfoShareRequest userInfoRequest = new AlipayUserInfoShareRequest();
            AlipayUserInfoShareResponse userInfoResponse = alipayConfig.getAlipayClient().execute(userInfoRequest, accessToken);
            if (userInfoResponse.isSuccess()) {
                // 处理用户信息
                openId = userInfoResponse.getOpenId();
                nickName = userInfoResponse.getNickName();
                // 可以将用户信息存储到数据库或进行其他操作
            } else {
                throw new LogicException(SelfXErrors.THIRTY_PARTY_AUTH, ThirdPartConstants.ALIPAY);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new LogicException(SelfXErrors.THIRTY_PARTY_AUTH, ThirdPartConstants.ALIPAY);
        }

        String fieldName = getUniqueDBField(AuthUniqueFields.ALIPAY);
        UserProxy userProxy = userService.retrieveAuthUserByUniqueFiledForSignIn(
                uuId
                , fieldName
                ,openId
        );
        if(userProxy != null){
            return userProxy;
        }
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        userService.signUpDirectly(uuId,username
                ,password
                ,nickName
                , Gender.UNKNOWN
                , null
                ,null
                ,openId);
        return userService.retrieveAuthUserByUniqueFiledForSignIn(
                uuId
                , fieldName
                ,openId
        );
    }
}
