package manager.logic;

import java.util.List;

import manager.data.LoginInfo;
import manager.data.UserSummary;
import manager.data.proxy.UserGroupProxy;
import manager.data.proxy.UserProxy;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.system.Gender;
import manager.system.SM;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.VerifyUserMethod;
import manager.system.UserUniqueField;
import manager.util.CommonUtil;
import manager.util.YZMUtil.YZMInfo;

/**
 * temp相关 性能考虑 就不sync了 原因是 sync是为了addPermsToGroup 和 addUsersToGroup 这两个都和temp无关
 * @author 王天戈
 *
 */
public abstract class UserLogic {
	
	private static UserLogic instance = null;
	
	final protected static String EMAIL_YZM_KEY= "email_yzm";
	final protected static String TEL_YZM_KEY= "tel_yzm";
	
	/*验证码正确的阈值 5代表 跟正确值相差在5之内*/
	final protected static int RIGHT_RANGE_FOR_YZM = 5;
	
	final protected static String EMAIL_KEY_FOR_SIGN_UP = "email_fsu";
	final protected static String TEL_KEY_FOR_SIGN_UP = "tel_fsu";
	
	final protected static String EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP = "email_verify_code_fsu";
	final protected static String TEL_VERIFY_CODE_KEY_FOR_SIGN_UP = "tel_verify_code_fsu";

	final protected static String VERIFY_CODE_EMAIL_SUBJECT = SM.BRAND_NAME+"验证码";
	
	protected static String createSignUpEmailMes(String verifyCode) {
		return String.format("您好，您正在注册 %s 账号，您的验证码为 %s，"
				+ "请尽快完成注册，防止验证码过期失效。如果非本人操作，抱歉打扰，请忽略本邮件。",SM.BRAND_NAME,verifyCode); 
	}
	
	protected static String createSignInEmailMes(String verifyCode) {
		return String.format("您好，您正在登录 %s ，您的验证码为 %s，"
				+ "请尽快完成登录，防止验证码过期失效。如果非本人操作，抱歉打扰，请忽略本邮件。",SM.BRAND_NAME,verifyCode); 
	}
	
	protected static String createRetrieveAccountMes(String account) {
		return String.format("您好，您正在找回%s的账号 ，您的账号为 %s，如果非本人操作，抱歉打扰，请忽略本邮件。",SM.BRAND_NAME,account); 
	}
	
	protected static String createResetPwdMes(String verifyCode) {
		return String.format("您好，您正在重置账号密码，您的验证码为 %s，"
				+ "请尽快完成重置，防止验证码过期失效。如果非本人操作，抱歉打扰，请忽略本邮件。",verifyCode); 
	}
	
	public static synchronized UserLogic getInstance() {
		if(instance == null) {
			instance = new UserLogic_Real();
		}
		return instance;
	}
	
	protected boolean isAdmin(int userId) throws LogicException, DBException {
		return getUser(userId).getAccount().equals(SM.ADMIN_ACCOUNT);
	}
	
	protected abstract boolean hasPerm(int userId,SMPerm perm) throws LogicException, DBException;
	
	public void checkPerm(int userId,SMPerm perm) throws LogicException, DBException {
		if(!hasPerm(userId, perm)) 
			throw new LogicException(SMError.LACK_PERM,perm.getName());
	}
	
	public abstract User getUser(int userId) throws LogicException, DBException;
	public abstract List<User> getUsers(List<Integer> usersId) throws LogicException, DBException;
	public abstract UserProxy loadUser(int userId,int loginerId) throws LogicException, DBException;
	/**
	 * @return 临时用户的唯一标识 UUID
	 */
	public abstract String createTempUser() throws LogicException;
	
	protected static String createVerifiCode(){
		StringBuffer str = new StringBuffer();
		for(int i=0;i<6;i++) {
			str.append(CommonUtil.getByRandom(0, 10));
		}
		return str.toString();
	}
	
	/**
	 * @return YZMInfo :前台base64编码图片，xForCheck仅仅是给测试用例用的，前台不应该用
	 * @throws LogicException 
	 */
	public abstract YZMInfo createTelYZM(String uuId,String old) throws LogicException;
	public abstract YZMInfo createEmailYZM(String uuId,String old) throws LogicException;
	/**
	 * 检查 如果成功 YZMInfo.checkSuccess 为true  如果失败 重新生成一张YZM给前台 
	 */
	public abstract YZMInfo checkTelYZMAndRefreshIfFailed(String uuId,int x, String imgSrc) throws LogicException;
	public abstract YZMInfo checkEmailYZMAndRefreshIfFailed(String uuId,int x, String imgSrc) throws LogicException;
	
	/**
	 * @return verfiyCode 仅仅是给test使用 UI不能用
	 */
	public abstract String sendTelVerifyCodeForSignUp(String tel,String uuId,int YZM) throws LogicException;
	public abstract String sendEmailVerifyCodeForSignUp(String email,String uuId,int YZM) throws LogicException;
	
	public abstract String sendTelVerifyCodeForSignIn(String tel) throws LogicException, DBException;
	public abstract String sendEmailVerifyCodeForSignIn(String email) throws LogicException, DBException;
	
	public abstract void sendVerifyCodeForResetPWD(String account,String val,VerifyUserMethod method) throws LogicException, DBException;
	
	
	public abstract void retrieveAccount(VerifyUserMethod method,String val) throws LogicException, DBException;
	
	/**
	  *  正常情况下，根据不同场景，有可能会抛如下异常：ACCOUNT_NULL EMAIL_NULL PWD_WRONG
	 *  TODO 手机还没处理
	 *   既然是登录，就默认缓存里不存在，不用从缓存里取了，没必要。同时，直接加入缓存。
	 *  登录成功 清理temp缓存
	 */
	public abstract UserProxy signIn(String uuId,VerifyUserMethod method, String account,String accountPwd, String email, String emailPwd, String tel, String telVerifyCode) throws LogicException, DBException;
	
	public abstract boolean exists(UserUniqueField field,String val) throws DBException, LogicException;
	
	/**
	  *   唯一性交给前台及DB维护，这样只可能是爬虫会提交重复数据，就不必管它的提示新消息了。
	 *   
	   *  前台注册的功能是这样的：允许用户注册时同时填写邮箱和手机号，但二者必须至少选择一个填写，否则抛SIGN_UP_ILLGEAL
	   *  如果输入邮箱/手机号字符串为空，则判定该属性不被验证。
	   *  如果验证邮箱/手机号不通过，抛CHECK_VERIFY_CODE_FAIL 
	  * pwd逻辑层校验至少需要8位  否则抛 ILLEGAL_PWD
	  * account nickName gender不为空 否则抛SIGN_UP_ILLGEAL  
	  * 
	   *   初始用户会被放到普通用户组
	   * 
	   *清理UUID 
	   *   
	 */
	public abstract int signUp(String uuId,String account,String email,String emailVerifyCode,String tel,
			String telVerifyCode,String pwd,String nickName,Gender gender) throws LogicException, DBException;
	
	/**
	 *   要求权限 ADD_USERS_TO_PERM ，没有，抛LACK_PERM
	 * usersId,groupId必须存在，不存在，抛INCONSTSTANT_ARGS_BETWEEN_DATA 
	  *  对于已经在group里的user，重复添加不会报错，可以兼容这种情况。
	 * 
	 * @author 王天戈
	 */
	public abstract void addUsersToGroup(List<Integer> usersId,int groupId,int loginerId) throws LogicException,DBException;
	/**
	 *   要求权限 EDIT_PERMS_TO_GROUP ，没有，抛LACK_PERM
	 * groupId必须存在，不存在，抛INCONSTSTANT_ARGS_BETWEEN_DATA 
	 * 增添已有的 删除没有的
	 * @author 王天戈
	 */
	public abstract void overrideGroupPerms(List<SMPerm> perms,int groupId,int loginerId) throws LogicException,DBException;
	/**
	  *  要求权限CREATE_USER_GROUP
	 *   用户组不能重名，重名，抛DUP_USER_GROUP_NAME
	 *  这反正不是用户用的功能 不用缓存了 麻烦
	 */
	public abstract int createUserGroup(String name,int loginerId) throws LogicException,DBException;
	
	/**
	 * 检验UUID是否还存在在缓存中，若存在，则重置一下存活时间
	 */
	public abstract boolean confirmTempUser(String uuId);
	
	
	/**
	 * 用户管理相关都是管理人员做的，不用缓存
	 * @throws LogicException 
	 */
	public abstract List<UserGroupProxy> loadAllUserGroups(int loginerId) throws DBException, LogicException;
	public abstract List<SMPerm> loadPermsOfGroup(int groupId,int loginerId) throws DBException, LogicException;
	public abstract UserSummary loadUserSummary(int loginerId) throws LogicException, DBException;
	/*最多显示500条*/
	public abstract List<UserProxy> loadUsersOfGroup(int groupId, int loginerId) throws LogicException, DBException;

	public abstract void resetPWD(String account, String val, VerifyUserMethod method, String verifyCode,
			String resetPWD) throws LogicException, DBException;
	
}
