package manager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.entity.general.User;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.system.Gender;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.SecurityUtil;
import manager.util.YZMUtil.YZMInfo;
/**
 *  TODO Junit 的多线程八成会导致 缓存在test的过程中出现问题
 * @author 王天戈
 *
 */
public class UserLogicTest {
	
	
	@Before
	public void setUp() throws Exception {
		TestUtil.initEnvironment();
		TestUtil.initData();
	}
	
	@Test
	public void testBasicFlow() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		/*登录网站 未登录时会分配uuId*/
		String uuId = uL.createTempUser();
		
		String account = "admin";
		String email = "wtg92@126.com";
		String pwd= "12345678";
		String nickName="lkkk";
		Gender gender = Gender.MALE;
		
		assertFalse(uL.exists(UserUniqueField.ACCOUNT, account));
		assertFalse(uL.exists(UserUniqueField.EMAIL, email));
		assertFalse(uL.exists(UserUniqueField.NICK_NAME, nickName));
		
		YZMInfo rlt = uL.createEmailYZM(uuId, "");
		String verifyCode = uL.sendEmailVerifyCodeForSignUp(email, uuId, rlt.xForCheck);
		
		long id = uL.signUp(uuId, account, email, verifyCode, "", "", pwd, nickName, gender);
		User user = uL.getUser(id);
		assertEquals(account, user.getAccount());
		assertEquals(email, user.getEmail());
		assertTrue(SecurityUtil.verifyUserPwd(user, pwd));
		assertEquals(nickName, user.getNickName());
		assertEquals(gender, user.getGender());
		try {
			uL.getUser(5);
			fail();
		}catch(DBException e) {}
		
		assertTrue(uL.exists(UserUniqueField.ACCOUNT, account));
		try {
			uL.signIn(uuId,VerifyUserMethod.ACCOUNT_PWD, "xxxxxx","xxx","","","","");
			fail();
		}catch (LogicException e) {
			assertEquals(SMError.ACCOUNT_NULL, e.type);
		}
		
		try {
			uL.signIn(uuId,VerifyUserMethod.ACCOUNT_PWD, account, "xxxxxx","","","","");
			fail();
		}catch (LogicException e) {
			assertEquals(SMError.PWD_WRONG, e.type);
		}
		
		try {
			uL.signIn(uuId,VerifyUserMethod.EMAIL_VERIFY_CODE, "", "","xxx","xx","","");
			fail();
		}catch (LogicException e) {
			assertEquals(SMError.EMAIL_VERIFY_TIMEOUT, e.type);
		}
		
		uL.signIn(uuId,VerifyUserMethod.ACCOUNT_PWD, account, pwd,"","","","");
		
	}
	
	
	@Test
	public void testPermBasic() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		/*登录网站 未登录时会分配给未*/
		String uuId = uL.createTempUser();
		
		String account = "wwwwww";
		String email = "wtg92@126.com";
		String pwd= "complacte1!!!!!!!!!";
		String nickName="lkkk";
		Gender gender = Gender.MALE;
		
		assertFalse(uL.exists(UserUniqueField.ACCOUNT, account));
		assertFalse(uL.exists(UserUniqueField.EMAIL, email));
		assertFalse(uL.exists(UserUniqueField.NICK_NAME, nickName));
		
		YZMInfo rlt = uL.createEmailYZM(uuId, "");
		String verifyCode = uL.sendEmailVerifyCodeForSignUp(email, uuId, rlt.xForCheck);
		
		long id = uL.signUp(uuId, account, email, verifyCode, "", "", pwd, nickName, gender);
		
		long admin = TestUtil.addAdmin();
		long groupId = uL.createUserGroup("用户组1", admin);
		uL.addUsersToGroup(Arrays.asList(id), groupId, admin);
		SMPerm targetPerm = SMPerm.EDIT_PERMS_TO_GROUP;
		uL.overrideGroupPerms(Arrays.asList(targetPerm), groupId, admin);

		assertTrue(uL.hasPerm(id, targetPerm));
		
		try{
			uL.createUserGroup("secon perm", id);
			fail();
		}catch(LogicException e) {
			assertEquals(SMError.LACK_PERM, e.type);
		}
		
		
	}
	
}
