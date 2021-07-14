package manager.util;

import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public abstract class TokenUtil {
	
	private final static Algorithm ALGORITHM = Algorithm.HMAC256("secret");
	private final static JWTVerifier VERIFIER = JWT.require(ALGORITHM)
		        .withIssuer("auth0")
		        .build();
	
	public static Map<String,Claim> getAllData(String token){
	    DecodedJWT jwt = VERIFIER.verify(token);
	    return jwt.getClaims();
	}
	
	public static Claim getData(String token,String key){
	    DecodedJWT jwt = VERIFIER.verify(token);
	    return jwt.getClaim(key);
	}
	
	public static String setData(Map<String,String> data) {
		Builder builder = JWT.create().withIssuer("auth0");
		data.forEach((key,val)->{
			builder.withClaim(key, val);
		});
		return builder.sign(ALGORITHM);
	}
	
	
}
