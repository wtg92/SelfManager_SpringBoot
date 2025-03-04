package manager.service;

import com.alibaba.fastjson2.JSON;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import manager.data.proxy.UserProxy;
import manager.exception.LogicException;
import manager.system.AuthUniqueFields;
import manager.system.DBConstants;
import manager.system.Gender;
import manager.system.SMError;
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

    public static String getUniqueDBField(AuthUniqueFields field){
        switch (field){
            case GMAIL -> {
                return DBConstants.F_EMAIL;
            }
            default ->
                throw new LogicException(SMError.UNEXPECTED_ERROR);
        }
    }

    public synchronized UserProxy googleAuth(String token,String uuId) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
        GoogleIdToken idToken;
        try{
            idToken = verifier.verify(token);
        }catch (Exception e){
            throw new LogicException(SMError.GMAIL_AUTH);
        }
        if( idToken == null){
            throw new LogicException(SMError.GMAIL_AUTH);
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
                ,""
                ,"");
        return userService.retrieveAuthUserByUniqueFiledForSignIn(
                uuId
                , fieldName
                ,email
        );
    }
}
