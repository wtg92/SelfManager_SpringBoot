package manager.logic.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static manager.cache.CacheConverter.createGeneralKey;
import static manager.cache.CacheConverter.createTempKeyByBiIdentifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.transaction.Transactional;
import manager.cache.*;
import manager.dao.UserDAO;
import manager.data.UserSummary;
import manager.data.proxy.UserGroupProxy;
import manager.data.proxy.UserProxy;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.logic.UserLogic;
import manager.system.Gender;
import manager.system.NoSuchElementType;
import manager.system.SM;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.EmailUtil;
import manager.util.SMSUtil;
import manager.util.SecurityUtil;
import manager.util.ThrowableSupplier;
import manager.util.YZMUtil;
import manager.util.YZMUtil.YZMInfo;
import manager.util.locks.UserLockManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 2023.9.19 Review了一次 主要修改的是 在验证验证码时 如果验证失效 应当删除验证码缓存 使其验证码失效
 */
@Service
public class UserLogicImpl extends UserLogic {
	final private static Logger logger = Logger.getLogger(UserLogicImpl.class.getName());

	@Resource
	private UserDAO uDAO;
	@Resource
	private YZMUtil yzmUtil;

	@Resource
	private UserLockManager locker;

	@Resource
	CacheOperator cache;


	@Override
	public User getUser(long userId){
		ThrowableSupplier<User, DBException> generator = ()-> uDAO.selectExistedUser(userId);
		return cache.getOne(CacheMode.E_ID,userId,User.class,generator);
	}



	@Override
	public boolean hasPerm(long userId, SMPerm perm) {
		/*admin owning all perms*/
		if(isAdmin(userId)) {
			return true;
		}

		Set<Integer> perms = cache.getPermsByUser(userId,()->new HashSet<>(uDAO.selectPermsByUser(userId)));
		return perms.contains(perm.getDbCode());
	}

	private Set<SMPerm> getUserAllPerms(long userId) throws DBException, LogicException{
		if(isAdmin(userId)) {
			return Arrays.stream(SMPerm.values()).filter(perm->perm != SMPerm.UNDECIDED).collect(toSet());
		}
		return new HashSet<>(cache.getPermsByUser(userId,()->new HashSet<>(uDAO.selectPermsByUser(userId)))
				.stream().map(SMPerm::valueOfDBCode).toList());
	}
	

	@Override
	public UserProxy signIn(String uuId,VerifyUserMethod method, String account,String accountPwd, String email, String emailVerifyCode, String tel, String telVerifyCode){
		switch (method) {
		case ACCOUNT_PWD: {
			try {
				User user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);

				if (!SecurityUtil.verifyUserPwd(user, accountPwd)) {
					//TODO 未来做点处理  防止连续登录
					throw new LogicException(SMError.PWD_WRONG);
				}

				cache.removeTempUser(uuId);
				return loadUser(user.getId(), user.getId());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.ACCOUNT_NULL, account);
			}
		}
		case EMAIL_VERIFY_CODE:{
			CacheMode mode = CacheMode.T_EMAIL_FOR_SIGN_IN;
			String key = createGeneralKey(mode, email);
			String ans = cache.get(key);
			if(ans == null)
				throw new LogicException(SMError.EMAIL_VERIFY_TIMEOUT, email);

			if(!ans.equals(emailVerifyCode)) {
				cache.remove(key);
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,emailVerifyCode);
			}

			User user = uDAO.selectUniqueUserByField(SMDB.F_EMAIL, email);
			cache.removeTempUser(uuId);
			return loadUser(user.getId(), user.getId());
		}

		case TEL_VERIFY_CODE:{
			CacheMode mode = CacheMode.T_TEL_FOR_SIGN_IN;
			String key = createGeneralKey(mode, email);
			String ans = cache.get(key);
			if(ans == null)
				throw new LogicException(SMError.TEL_VERIFY_TIMEOUT, tel);

			if(!ans.equals(telVerifyCode)) {
				cache.remove(key);
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,telVerifyCode);
			}

			User user = uDAO.selectUniqueUserByField(SMDB.F_TEL_NUM, tel);
			cache.removeTempUser(uuId);
			return loadUser(user.getId(), user.getId());
		}
		default:
			assert false : method;
			throw new RuntimeException("未配置的登录方式验证 " + method);
		}
	}



	@Override
	public UserProxy loadUser(long userId, long loginId) throws LogicException, DBException {
		if(!isAdmin(loginId) && userId != loginId){
			throw new LogicException(SMError.COMMON,"Unexpected");
		}
		User user=  getUser(userId);
		UserProxy proxy = new UserProxy();
		proxy.user = user;
		proxy.perms = getUserAllPerms(userId);
		return proxy;
	}
	
	@Override
	@Transactional
	public synchronized long signUp(String uuId,String account,String email,String emailVerifyCode,
			String tel,String telVerifyCode,String pwd,String nickName,Gender gender) throws LogicException, DBException {

		User user = new User();
		
		if(account.isBlank())
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"账号不能为空");
		
		user.setAccount(account);
		
		if(nickName.isBlank())
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"昵称不能为空");
		
		user.setNickName(nickName);
		
		user.setPassword(pwd);
		SecurityUtil.encodeUserPwd(user);
		
		if(gender == Gender.UNDECIDED)
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"性别不能为空");
		
		user.setGender(gender);
		
		if(email.isBlank() && tel.isBlank()) {
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"邮箱和手机号必须至少填入一个");
		};
		
		if(!email.isBlank()) {
			try {
				String ans = cache.getTempUserMapVal( uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(emailVerifyCode)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"邮箱验证码错误");
				}
				String matchEmail = cache.getTempUserMapVal( uuId, EMAIL_KEY_FOR_SIGN_UP);
				if(!matchEmail.equals(email)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"注册邮箱与验证邮箱不匹配");
				}
				
				user.setEmail(email);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"邮箱验证码已过期失效");
			}
			
		}
		
		if(!tel.isBlank()) {
			try {
				String ans = cache.getTempUserMapVal( uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(telVerifyCode)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"手机验证码错误");
				}
				String matchTel = cache.getTempUserMapVal( uuId, TEL_KEY_FOR_SIGN_UP);
				if(!matchTel.equals(tel)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"注册手机与验证手机不匹配");
				}
				
				
				user.setTelNum(tel);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"手机验证码已过期失效");
			}
		}
		long uId = uDAO.insertUser(user);
		UserGroup defaultGroup = uDAO.selectUniqueExistedUserGroupByField(SMDB.F_NAME, SM.DEFAULT_BASIC_USER_GROUP);
		/*这里比较特殊 是系统自动添加的 并且相对来说太过频繁 不应该重置缓存 这里选择直接添加进缓存 影响到的是 一个组里有多少用户*/
		uDAO.insertUsersToGroup(List.of(uId), defaultGroup.getId());
		
		cache.deleteGeneralKey(CacheMode.T_USER, uuId);
		
		return uId;
	}


	/**
	 * 似乎不用缓存比较好 但底层如果是用缓存取每一个的话 如果底层置信 则本函数置信
	 */
	@Override
	public List<User> getUsers(List<Long> usersId) throws LogicException, DBException {
		return uDAO.selectUsersByIds(usersId);
	}
	
	@Override
	public synchronized void addUsersToGroup(List<Long> usersId, long groupId,long loginerId) throws LogicException, DBException {
		
		checkPerm(loginerId, SMPerm.ADD_USERS_TO_PERM);
		
		List<User> users = getUsers(usersId);
		
		if(users.size() != usersId.size())
			throw new LogicException(SMError.INCONSISTENT_ARGS_BETWEEN_DATA,"数量不一致 "+usersId.size() + " vs " + users.size());
		
		if(!uDAO.includeUserGroup(groupId))
			throw new LogicException(SMError.INCONSISTENT_ARGS_BETWEEN_DATA,"用户组不存在 "+groupId);

		List<Long> usersForThisGroup = uDAO.selectUsersIdByGroup(groupId);
		List<Long> distinctUsers = usersId.stream().filter(uId->!usersForThisGroup.contains(uId)).collect(toList());
		
		if(distinctUsers.isEmpty()) {
			logger.log(Level.WARNING,"添加的user全是已存在在组里的 "+groupId,"uL.addUsersToGroup");
			return;
		}
		
		uDAO.insertUsersToGroup(distinctUsers, groupId);

		cache.clearPerms();
	}

	@Override
	public synchronized void overrideGroupPerms(List<SMPerm> permsForOverride, long groupId, long loginId) throws LogicException, DBException {

		checkPerm(loginId, SMPerm.EDIT_PERMS_TO_GROUP);
		
		if(!uDAO.includeUserGroup(groupId)) {
			throw new LogicException(SMError.INCONSISTENT_ARGS_BETWEEN_DATA,"用户组不存在 "+groupId);
		}
		
		List<SMPerm> permsForThisGroup =uDAO.selectPermsByGroup(groupId)
				.stream().map(SMPerm::valueOfDBCode).toList();
		List<SMPerm> permsForAdd = permsForOverride.stream().filter(perm->!permsForThisGroup.contains(perm)).collect(toList());
		List<SMPerm> permsForDelete = permsForThisGroup.stream().filter(perm->!permsForOverride.contains(perm)).collect(toList());
		if(!permsForAdd.isEmpty()) {
			uDAO.insertPermsToGroup(permsForAdd, groupId);
		}
		if(!permsForDelete.isEmpty()) {
			uDAO.deletePermsFromGroup(permsForDelete, groupId);	
		}

		cache.clearPerms();
	}

	@Override
	public synchronized long createUserGroup(String name, long loginerId) throws LogicException, DBException {
		
		checkPerm(loginerId, SMPerm.CREATE_USER_GROUP);
		
		if(uDAO.includeUniqueUserGroupByField(SMDB.F_NAME, name)) {
			throw new LogicException(SMError.DUP_USER_GROUP_NAME);
		}
		
		UserGroup group = new UserGroup();
		group.setName(name);
		
		return uDAO.insertUserGroup(group);
	}

	@Override
	public String createTempUser() throws LogicException {
		String uuId = "";
		while(true) {
			uuId = UUID.randomUUID().toString();
			/**
			 * 这里其实分了两步：
			 * 临时用户的数据结构是  用户:::uuId {} 这里只是为了初始化一个临时用户
			 */
			if(cache.setTempUser(uuId)) {
				break;
			}else {
				logger.log(Level.WARNING,"遇到了UUID的BUG?出现了重复的UUID？"+uuId);
			}
		}
		return uuId;
	}

	@Override
	public YZMInfo createTelYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = yzmUtil.createYZM(old);
		try {
			cache.setTempMap( uuId, TEL_YZM_KEY, String.valueOf(rlt.xForCheck));
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
		return rlt;
	}

	@Override
	public YZMInfo createEmailYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = yzmUtil.createYZM(old);
		try {
			cache.setTempMap( uuId, EMAIL_YZM_KEY, String.valueOf(rlt.xForCheck));
		} catch (NoSuchElement e) {
			assert e.type ==  NoSuchElementType.REDIS_KEY_NOT_EXISTS;
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
		return rlt;
	}

	@Override
	public YZMInfo checkTelYZMAndRefreshIfFailed(String uuId, int x, String imgSrc) throws LogicException {
		if(checkTelYZM(uuId, x)) {
			YZMInfo rlt = new YZMInfo();
			rlt.checkSuccess = true;
			return rlt;
		}
		return createTelYZM(uuId, imgSrc);
	}

	@Override
	public YZMInfo checkEmailYZMAndRefreshIfFailed(String uuId, int x, String imgSrc) throws LogicException {
		if(checkEmailYZM(uuId, x)) {
			YZMInfo rlt = new YZMInfo();
			rlt.checkSuccess = true;
			return rlt;
		}
		return createEmailYZM(uuId, imgSrc);
	}
	
	private boolean checkTelYZM(String uuId,int x) throws LogicException {
		try {
			String answer = cache.getTempUserMapVal(uuId,TEL_YZM_KEY);
			int ans = Integer.parseInt(answer);
			return Math.abs(ans-x) <= RIGHT_RANGE_FOR_YZM;
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
	}
	
	private boolean checkEmailYZM(String uuId,int x) throws LogicException {
		try {
			String answer = cache.getTempUserMapVal(uuId,EMAIL_YZM_KEY);
			int ans = Integer.parseInt(answer);
			return Math.abs(ans-x) <= RIGHT_RANGE_FOR_YZM;
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
	}

	@Override
	public void sendTelVerifyCodeForSignUp(String tel, String uuId, int YZM) throws LogicException {
		/**
		 * 将来会引入Google的吗？
		 * 如果引入的话 是否就不再需要这个了
		 * 本身这里可能有点诡异 为何在生成验证码的时候 生成了verifyCode?
		 */
		if(!checkTelYZM(uuId, YZM)) {
			throw new LogicException(SMError.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM 保证了key一定存在（极小的可能性不存在，但那不考虑了）*/
		try {
			String verifyCode = createVerifyCode();
			cache.setTempMap( uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
			cache.setTempMap( uuId, TEL_KEY_FOR_SIGN_UP, tel);
			SMSUtil.sendSMS(SMSUtil.SIGN_UP_TEMPLATE_ID, tel, verifyCode, cache.getExpirationSeconds());
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);

		}
	}

	@Override
	public void sendEmailVerifyCodeForSignUp(String email, String uuId, int YZM) throws LogicException {
		if(!checkEmailYZM(uuId, YZM)) {
			throw new LogicException(SMError.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM 保证了key一定存在（极小的可能性不存在，但那不考虑了）*/
		try {
			String verifyCode = createVerifyCode();
			cache.setTempMap(uuId, EMAIL_KEY_FOR_SIGN_UP, email);
			cache.setTempMap(uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
			EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignUpEmailMes(verifyCode));
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
	}
	
	@Override
	public void sendVerifyCodeForResetPWD(String account, String val, VerifyUserMethod method)
			throws LogicException, DBException {
		User user;
		try {
			user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.NON_EXISTED_ACCOUNT,"不存在的账号"+account);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SMError.NON_EXISTED_EMAIL);
			}
			
			String verifyCode = createVerifyCode();
			String key = createTempKeyByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val);
			cache.set(key,verifyCode);
			EmailUtil.sendSimpleEmail(val,VERIFY_CODE_EMAIL_SUBJECT , createResetPwdMes(verifyCode));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SMError.NON_EXISTED_TEL);
			}
			
			String verifyCode = createVerifyCode();
			String key = createTempKeyByBiIdentifiers(CacheMode.T_TEL_FOR_RESET_PWD,account,val);
			cache.set(key,verifyCode);
			SMSUtil.sendSMS(SMSUtil.RESET_PWD_TEMPLATE_ID, val, verifyCode);
			return;
		}
		default:
			assert false;
			throw new RuntimeException("未配置的找回类型"+method.getName());
		}
	}
	
	
	
	@Override
	public void sendTelVerifyCodeForSignIn(String tel) throws LogicException, DBException {
		if(!uDAO.includeUniqueUserByField(SMDB.F_TEL_NUM, tel)) {
			throw new LogicException(SMError.NON_EXISTED_TEL,tel);
		}
		String verifyCode = createVerifyCode();
		String key = createGeneralKey(CacheMode.T_TEL_FOR_SIGN_IN, tel);
		cache.set(key, verifyCode);
		SMSUtil.sendSMS(SMSUtil.SIGN_IN_TEMPLATE_ID, tel, verifyCode,  cache.getExpirationSeconds());
	}

	@Override
	public void sendEmailVerifyCodeForSignIn(String email) throws LogicException, DBException {
		if(!uDAO.includeUniqueUserByField(SMDB.F_EMAIL, email)) {
			throw new LogicException(SMError.NON_EXISTED_EMAIL,email);
		}
		String verifyCode = createVerifyCode();
		String key = createGeneralKey(CacheMode.T_EMAIL_FOR_SIGN_IN, email);
		cache.set(key, verifyCode);
		EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignInEmailMes(verifyCode));
	}
	
	@Override
	public boolean exists(UserUniqueField field, String val) throws DBException, LogicException {
        return switch (field) {
            case ACCOUNT -> uDAO.includeUniqueUserByField(SMDB.F_ACCOUNT, val);
            case EMAIL -> uDAO.includeUniqueUserByField(SMDB.F_EMAIL, val);
            case TEL_NUM -> uDAO.includeUniqueUserByField(SMDB.F_TEL_NUM, val);
            case WEI_XIN_OPEN_ID ->uDAO.includeUniqueUserByField(SMDB.F_WEI_XIN_OPEN_ID, val);
            case ID_NUM -> uDAO.includeUniqueUserByField(SMDB.F_ID_NUM, val);
            case NICK_NAME -> uDAO.includeUniqueUserByField(SMDB.F_NICK_NAME, val);
            default -> throw new RuntimeException("未配置的检查类型" + field);
        };
	}

	@Override
	public boolean confirmTempUser(String uuId) {
		return cache.existsForTemp(uuId);
	}

	
	@Override
	public List<UserGroupProxy> loadAllUserGroups(long loginerId) throws DBException, LogicException {
		checkPerm(loginerId, SMPerm.SEE_USERS_AND_USER_GROUPS_DATA);
		
		List<UserGroupProxy> rlt = new ArrayList<>(); 
		for(UserGroup group : uDAO.selectAllUserGroup()) {
			UserGroupProxy one = new UserGroupProxy();
			one.group = group;
			one.countUsers = uDAO.countUsersOfGroup(group.getId());
			rlt.add(one);
		}
		return rlt;
	}

	@Override
	public List<SMPerm> loadPermsOfGroup(long groupId, long loginerId) throws DBException, LogicException {
		checkPerm(loginerId, SMPerm.SEE_USERS_AND_USER_GROUPS_DATA);
		
		return uDAO.selectPermsByGroup(groupId).stream().map(SMPerm::valueOfDBCode).collect(toList());
	}

	@Override
	public UserSummary loadUserSummary(long loginerId) throws LogicException, DBException {
		checkPerm(loginerId, SMPerm.SEE_USERS_AND_USER_GROUPS_DATA);
		UserSummary summary = new UserSummary();
		summary.countUsers = uDAO.countAllUsers();
		/**
		 * 所谓的活跃用户 指的就是缓存里的用户
		 */
		summary.countActiveUsers = cache.countAllEntities(User.class);
		return summary;
	}

	@Override
	public List<UserProxy> loadUsersOfGroup(long groupId, long loginerId) throws LogicException, DBException {
		checkPerm(loginerId, SMPerm.SEE_USERS_AND_USER_GROUPS_DATA);
		return uDAO.selectUsersByGroup(groupId,SMDB.MAX_NUM_IN_ONE_SELECT).stream().map(user->{
			UserProxy proxy = new UserProxy();
			proxy.user=user;
			return proxy;
		}).collect(toList());
	}

	@Override
	public void retrieveAccount(VerifyUserMethod method, String val) throws LogicException, DBException {
		switch(method) {
		case EMAIL_VERIFY_CODE:
			try {
				User user = uDAO.selectUniqueUserByField(SMDB.F_EMAIL, val);
				EmailUtil.sendSimpleEmail(val,SM.BRAND_NAME+"找回账号", createRetrieveAccountMes(user.getAccount()));
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.NON_EXISTED_EMAIL);
			}
			break;
		case TEL_VERIFY_CODE:
			try {
				User user = uDAO.selectUniqueUserByField(SMDB.F_TEL_NUM, val);
				SMSUtil.sendSMS(SMSUtil.RETRIEVE_ACCOUNT_TEMPLATE_ID, val, user.getAccount());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.NON_EXISTED_TEL);
			}
			break;
		default:
			assert false;
			throw new RuntimeException("未配置的找回类型 "+method.getName());
		}
	}

	@Override
	public synchronized void resetPWD(String account, String val, VerifyUserMethod method, String verifyCode, String resetPWD) throws LogicException, DBException  {
		User user;
		try {
			user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.NON_EXISTED_ACCOUNT);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SMError.NON_EXISTED_EMAIL);
			}
			
			String key = createTempKeyByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val);
			String forCheck = cache.get(key);
			if(forCheck == null)
				throw new LogicException(SMError.TEL_VERIFY_TIMEOUT,"验证码已失效，请重新获取");
			if(!forCheck.equals(verifyCode)) {
				cache.remove(key);
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL);
			}
			
			user.setPassword(resetPWD);
			SecurityUtil.encodeUserPwd(user);
			cache.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SMError.NON_EXISTED_TEL,"账号和手机号不匹配"+val);
			}

			String key = createTempKeyByBiIdentifiers(CacheMode.T_TEL_FOR_RESET_PWD,account,val);
			String forCheck = cache.get(key);
			if(forCheck == null)
				throw new LogicException(SMError.TEL_VERIFY_TIMEOUT,"验证码已失效，请重新获取");

			if(!forCheck.equals(verifyCode)) {
				cache.remove(key);
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL);
			}		
			
			user.setPassword(resetPWD);
			SecurityUtil.encodeUserPwd(user);
			cache.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		default:
			assert false;
			throw new RuntimeException("未配置的找回类型"+method.getName());
		}
	}

}
