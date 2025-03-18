package manager.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import manager.entity.general.User;
import manager.exception.LogicException;
import manager.system.SelfXErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
  *  敏感的数据id再加密，要不然太麻烦了
 *  只允许给Booster使用 不允许被其它引用
 */
@Component
public class SecurityBasis {

	@Value("${security.stable-common-id-key}")
	private String STABLE_KEY;

	final private static int MIN_LENGTH_FOR_PWD = 8;

	public static void encodeUserPwd(User user) throws LogicException {
		if(user.getPassword() == null || user.getPassword().length() < MIN_LENGTH_FOR_PWD) {
			throw new LogicException(SelfXErrors.ILLEGAL_PWD,user.getPassword());
		}
		try {
			user.setPwdSalt(PBKDF2.getSalt());
		} catch (NoSuchAlgorithmException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR,e.getMessage());
		}

		try {
			user.setPassword(PBKDF2.encodePassword(user.getPassword(), user.getPwdSalt()));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR,e.getMessage());
		}
	}


	public static boolean verifyUserPwd(User user,String pwd) throws LogicException{
		assert !user.getPwdSalt().isEmpty();
		try {
			return PBKDF2.verifyPassword(pwd, user.getPwdSalt(),user.getPassword() );
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR,e.getMessage());
		}
	}

	public String encodeStableInfo(Object info) throws LogicException {
		if(info == null || info.toString().isEmpty()){
			return "";
		}
		try {
			return Base64.getEncoder().encodeToString(AES.encrypt(info.toString().getBytes(), STABLE_KEY));
		}catch (Exception e) {
			/*
			 * 加密不存在场景差异 因此在这里直接异常处理
			 */
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR,e.getMessage());
		}
	}

	public String decodeStableInfo(String info) throws Exception{
		return new String(AES.decrypt(Base64.getDecoder().decode(info),STABLE_KEY));
	}


	public static String encodeUnstableInfo(Object info) throws LogicException {
		if(info == null || info.toString().isEmpty()){
			return "";
		}
		try {
			return Base64.getEncoder().encodeToString(AES.encrypt(info.toString().getBytes()));
		}catch (Exception e) {
			/*
			 * 加密不存在场景差异 因此在这里直接异常处理
			 */
			throw new LogicException(SelfXErrors.UNEXPECTED_ERROR,e.getMessage());
		}
	}
	
	public static String decodeUnstableInfo(String info) throws Exception{
		 return new String(AES.decrypt(Base64.getDecoder().decode(info)));
	}

	
	public static abstract class AES{

		private static final String ALGORITHM = "AES";

		private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
		private static SecretKey key;
		private static final byte[] iv = {0x01, 0x23, 0x45, 0x67, 0x89 - 0xFF, 0xAB - 0xFF, 0xCD - 0xFF, 0xEF - 0xFF,
				0x01, 0x23, 0x45, 0x67, 0x89 - 0xFF, 0xAB - 0xFF, 0xCD - 0xFF, 0xEF - 0xFF};
		private static final AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);

		static {
			try {
				key = generateKey();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new RuntimeException("CANNOT LET THIS HAPPEN");
			}
		}

		private static SecretKey generateKey() throws NoSuchAlgorithmException {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			keyGenerator.init(256); // You can adjust the key size as needed
			return keyGenerator.generateKey();
		}

		public static String generateKeyAsBase64() throws NoSuchAlgorithmException {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			keyGenerator.init(256);
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] keyBytes = secretKey.getEncoded();
			return Base64.getEncoder().encodeToString(keyBytes);
		}

		public static SecretKey getSecretKeyFromBase64(String base64Key) {
			byte[] keyBytes = Base64.getDecoder().decode(base64Key);
			return new SecretKeySpec(keyBytes, ALGORITHM);
		}

		private static byte[] encrypt(byte[] data,String specificKey) throws Exception {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKeyFromBase64(specificKey) , paramSpec);
			return cipher.doFinal(data);
		}

		private static byte[] encrypt(byte[] data) throws Exception {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			return cipher.doFinal(data);
		}

		private static byte[] decrypt(byte[] bytes,String specificKey) throws Exception {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKeyFromBase64(specificKey), paramSpec);
			return cipher.doFinal(bytes);
		}

		private static byte[] decrypt(byte[] bytes) throws Exception {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			return cipher.doFinal(bytes);
		}
	}
	
	
	
	static abstract class PBKDF2{
		   static boolean verifyPassword(String password, String salt, String key)  
		            throws NoSuchAlgorithmException, InvalidKeySpecException {  
		        String result = encodePassword(password, salt);  
		        return result.equals(key);  
		    } 
			
			
			private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";  
			  
		    private static final int SALT_SIZE = 16;  
		  
		    private static final int HASH_SIZE = 16;  
		  
		    private static final int PBKDF2_ITERATIONS = 1000;  
		  
		 
		  
		    static String encodePassword(String password, String salt) throws NoSuchAlgorithmException,
		            InvalidKeySpecException {
		    	//将16进制字符串形式的salt转换成byte数组
		    	byte[] bytes = DatatypeConverter.parseHexBinary(salt);
		        KeySpec spec = new PBEKeySpec(password.toCharArray(), bytes, PBKDF2_ITERATIONS, HASH_SIZE * 4);
		        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		        byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
				//将byte数组转换为16进制的字符串
		        return DatatypeConverter.printHexBinary(hash);
		    }
		    
		  
		    /** 
		     * 生成随机盐值
		     *  
		     */  
		    static String getSalt() throws NoSuchAlgorithmException {  
		        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");  
		        byte[] bytes = new byte[SALT_SIZE / 2];  
		        random.nextBytes(bytes);  
		        //将byte数组转换为16进制的字符串
		        String salt = DatatypeConverter.printHexBinary(bytes);
		        return salt;  
		    }	
	}
	
 
	
}
