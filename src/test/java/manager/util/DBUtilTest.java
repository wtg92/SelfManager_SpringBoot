package manager.util;

import static manager.util.DBUtil.transFieldToAttr;
import static manager.util.DBUtil.transTableToEntity;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import manager.system.SMDB;

public class DBUtilTest {
	
	@Test
	public void testTranslateName() {
		assertEquals("User", transTableToEntity(SMDB.T_USER));
		assertEquals("UserGroup", transTableToEntity(SMDB.T_USER_GROUP));
		assertEquals("weiXinOpenId", transFieldToAttr(SMDB.F_WEI_XIN_OPEN_ID));
		assertEquals("nickName", transFieldToAttr(SMDB.F_NICK_NAME));
	}
	
	
}
