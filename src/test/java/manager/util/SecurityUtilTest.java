package manager.util;

import static manager.util.SecurityBasis.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import org.junit.Test;

import manager.entity.general.User;

public class SecurityUtilTest {

    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey key = keyGen.generateKey();
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(secret);
    }


	@Test
	public void testEncodeAndDecodePwd() throws Exception {
		String salt = PBKDF2.getSalt();
		String pwd = "110";
		String rlt = PBKDF2.encodePassword(pwd, salt);
		assertTrue(PBKDF2.verifyPassword("110", salt, rlt));
		assertFalse(PBKDF2.verifyPassword("1101", salt, rlt));

		User user = new User();
		String pwd2 = "XXXXXXX222222222";
		user.setPassword(pwd2);

		encodeUserPwd(user);
		assertTrue(verifyUserPwd(user, pwd2));
	}

	@Test
	public void testEncodeAndDecodeInfo() throws Exception {
		String s1 = "1";
		String s2 = "lk xjy when it comes?";
		
		String keyForS1 = SecurityBasis.encodeUnstableInfo(s1);
		String keyForS2 = SecurityBasis.encodeUnstableInfo(s2);
		assertNotEquals(s1, keyForS1);
		assertNotEquals(s2, keyForS2);
		
		assertEquals(s1, SecurityBasis.decodeUnstableInfo(keyForS1));
		assertEquals(s2, SecurityBasis.decodeUnstableInfo(keyForS2));
	}





}
