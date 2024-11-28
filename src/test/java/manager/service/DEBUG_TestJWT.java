package manager.service;

import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class DEBUG_TestJWT {

	@Test
	public void test3(){
		long t1 = Long.parseLong("1699346580000");
		Date d = new Date();
		d.setTime(t1);
		System.out.println(d);
		Predicate<String> s = null;

	}


	@Test
	public void test() {
		Algorithm algorithm = Algorithm.HMAC256("secret");
		String token = JWT.create().withIssuer("auth0")
				.withClaim("a", "b").withClaim("b", "ss").sign(algorithm);
		System.out.println(token);
		
		try {
		    JWTVerifier verifier = JWT.require(algorithm)
			        .withIssuer("auth0")
			        .build(); //Reusable verifier instance
			    DecodedJWT jwt = verifier.verify(token);
			    System.out.println(jwt.getClaim("ddd").asString() == null);
		}catch (JWTVerificationException e) {
			
		}

	}
	
	@Test
	public void testUUID() {
		System.out.println(UUID.randomUUID().toString());;
	}
	
	  public static void main(String[] args) {
	        String[] labels = {
	                "TODO","OUBE","NFDUQ"
	        };

	        String tmp = ".*?(";
	        for(String i:labels)
	            tmp = tmp + i + "|";
	        tmp = tmp.substring(0,tmp.length() - 1);
	        tmp += ") (.*?)\\)";
	        System.out.println(tmp);

	        final String regex = tmp;
	        final String string = "我今天要 TODO 李梦洋)" +
	                "### OUBE asdfasdf)";

	        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
	        final Matcher matcher = pattern.matcher(string);

	        while (matcher.find()) {
	            System.out.println("Full match: " + matcher.group(0));
	            for (int i = 1; i <= matcher.groupCount(); i++) {
	                System.out.println("Group " + i + ": " + matcher.group(i));
	            }
	        }
	    }
}
