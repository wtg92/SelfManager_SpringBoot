package manager.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import manager.entity.general.User;
import manager.exception.LogicException;
import manager.system.SMError;

/**
  *  敏感的数据id再加密，要不然太麻烦了
 */
public abstract class SecurityUtil {
	
	final private static int MIN_LENGTH_FOR_PWD = 8;
	
	public static void encodeUserPwd(User user) throws LogicException {
		if(user.getPassword() == null || user.getPassword().length() < MIN_LENGTH_FOR_PWD) {
			throw new LogicException(SMError.ILLEGAL_PWD,user.getPassword());
		}
		try {
			user.setPwdSalt(PBKDF2.getSalt());
		} catch (NoSuchAlgorithmException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SMError.UNEXPCTED_ERROR,e.getMessage());
		}
		
		try {
			user.setPassword(PBKDF2.encodePassword(user.getPassword(), user.getPwdSalt()));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SMError.UNEXPCTED_ERROR,e.getMessage());
		}
	}
	
	
	public static boolean verifyUserPwd(User user,String pwd) throws LogicException{
		assert user.getPwdSalt().length()>0;
		try {
			return PBKDF2.verifyPassword(pwd, user.getPwdSalt(),user.getPassword() );
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			assert false;
			e.printStackTrace();
			throw new LogicException(SMError.UNEXPCTED_ERROR,e.getMessage());
		}
	}
	

	
	public static String encodeInfo(String info) throws LogicException {
		try {
			return Base64.getEncoder().encodeToString(AES.encrypt(info.getBytes()));
		}catch (Exception e) {
			throw new LogicException(SMError.UNEXPCTED_ERROR,e.getMessage());
		}
	}
	
	public static String decodeInfo(String info) throws LogicException{
		try {
			 return new String(AES.decrypt(Base64.getDecoder().decode(info)));
		}catch (Exception e) {
			e.printStackTrace();
			throw new LogicException(SMError.UNEXPCTED_ERROR,e.getMessage());
		}
	}

	
	static abstract class AES{
		private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
		/*TOOD 有时间不把这个写死 让它每次重启动实例都重新生成一遍 然后放到缓存里*/
		private static final String PRIMARY_KEY_FOR_ENCODE_INFO = "d3645889311a0569a53ffc6a5504d686";
		private static final Key key = new SecretKeySpec(CommonUtil.toByteArray(PRIMARY_KEY_FOR_ENCODE_INFO), "AES");
        private static final byte[] iv = {0x01, 0x23, 0x45, 0x67, 0x89 - 0xFF, 0xAB - 0xFF, 0xCD - 0xFF, 0xEF - 0xFF,
                0x01, 0x23, 0x45, 0x67, 0x89 - 0xFF, 0xAB - 0xFF, 0xCD - 0xFF, 0xEF - 0xFF};
        private static final AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
	    private static byte[] encrypt(byte[] data) throws Exception {
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
	        cipher.init(Cipher.ENCRYPT_MODE, key,paramSpec);
	        return cipher.doFinal(data);
	    }
	    private static byte[] decrypt(byte[] bytes) throws Exception {
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
	        cipher.init(Cipher.DECRYPT_MODE, key,paramSpec);
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
