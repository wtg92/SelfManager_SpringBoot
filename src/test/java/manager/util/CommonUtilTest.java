package manager.util;
import static manager.util.CommonUtil.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Properties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class CommonUtilTest {

	
	@Test
	public void testReadProperties() throws IOException {
		Properties properties = new Properties();
		try(InputStream in  = this.getClass().getClassLoader().getResourceAsStream("sm.properties")){
			assert in !=null;
			properties.load(in);
			String rlt = (String) properties.get("redis_ip");
			System.out.println(rlt);
		}
		
		System.out.println(getValFromPropertiesFileInResource("redis_ip", "sm.properties"));
	}
	
	@Test
	public void testRandom() {
		for(int i=0;i<100;i++) {
			int v = getByRandom(2, 10);
			assertTrue(v>=2 && v<10);
		}
	}
	

}
