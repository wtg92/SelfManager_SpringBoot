package manager.util;

import static manager.util.DBUtil.transFieldToAttr;
import static manager.util.DBUtil.transTableToEntity;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import manager.system.DBConstants;

public class DBUtilTest {
	
	@Test
	public void testTranslateName() {
		assertEquals("User", transTableToEntity(DBConstants.T_USER));
		assertEquals("UserGroup", transTableToEntity(DBConstants.T_USER_GROUP));
		assertEquals("weiXinOpenId", transFieldToAttr(DBConstants.F_WEI_XIN_OPEN_ID));
		assertEquals("nickName", transFieldToAttr(DBConstants.F_NICK_NAME));
	}
	
	
}
