package manager.util;
import static manager.util.EmailUtil.*;

import org.junit.Test;

public class EmailUtilTest {

	@Test
	public void testBasic() throws Exception {
		sendSimpleEmail("wtg92@126.com", "验证码信息", "您好，您的验证码是 5566");
	}
	
}
