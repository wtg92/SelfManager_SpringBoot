package manager.service;

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
import manager.data.UserBasicInfo;
import manager.data.UserSummary;
import manager.data.proxy.UserGroupProxy;
import manager.data.proxy.UserProxy;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.booster.SecurityBooster;
import manager.system.Gender;
import manager.system.NoSuchElementType;
import manager.system.SelfX;
import manager.system.DBConstants;
import manager.system.SelfXErrors;
import manager.system.SelfXPerms;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.EmailUtil;
import manager.util.SMSUtil;
import manager.util.ThrowableSupplier;
import manager.util.YZMUtil;
import manager.util.YZMUtil.YZMInfo;
import manager.util.locks.UserLockManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * 2023.9.19 Review了一次 主要修改的是 在验证验证码时 如果验证失效 应当删除验证码缓存 使其验证码失效
 */
@Service
public class UserLogicImpl extends UserService {
	final private static Logger logger = Logger.getLogger(UserLogicImpl.class.getName());

	@Resource
	private UserDAO uDAO;
	@Resource
	private YZMUtil yzmUtil;

	@Resource
	private UserLockManager locker;

	@Resource
	CacheOperator cache;

	@Resource
	FilesService filesService;

	@Resource
	SecurityBooster securityBooster;

	public User getUser(long userId){
		ThrowableSupplier<User, DBException> generator = ()-> uDAO.selectExistedUser(userId);
		return cache.getEntity(CacheMode.E_ID,userId,User.class,generator);
	}


	private boolean isAdmin(long userId) throws LogicException, DBException {
		return getUser(userId).getAccount().equals(SelfX.ADMIN_ACCOUNT);
	}
	@Override
	public boolean hasPerm(long userId, SelfXPerms perm) {
		/*admin owning all perms*/
		if(isAdmin(userId)) {
			return true;
		}

		Set<Integer> perms = cache.getPermsByUser(userId,()->new HashSet<>(uDAO.selectPermsByUser(userId)));
		return perms.contains(perm.getDbCode());
	}

	public Set<SelfXPerms> getUserAllPerms(long userId) throws DBException, LogicException{
		if(isAdmin(userId)) {
			return Arrays.stream(SelfXPerms.values()).filter(perm->perm != SelfXPerms.UNDECIDED).collect(toSet());
		}
		return new HashSet<>(cache.getPermsByUser(userId,()->new HashSet<>(uDAO.selectPermsByUser(userId)))
				.stream().map(SelfXPerms::valueOfDBCode).toList());
	}
	

	@Override
	public UserProxy signIn(String uuId,VerifyUserMethod method, String account,String accountPwd, String email, String emailVerifyCode, String tel, String telVerifyCode){
		switch (method) {
		case ACCOUNT_PWD: {
			try {
				User user = uDAO.selectUniqueUserByField(DBConstants.F_ACCOUNT, account);

				if (!SecurityBooster.verifyUserPwd(user, accountPwd)) {
					//TODO 未来做点处理  防止连续登录
					throw new LogicException(SelfXErrors.PWD_WRONG);
				}

				cache.removeTempUser(uuId);
				return loadUser(user.getId(), user.getId());
			} catch (NoSuchElement e) {
				throw new LogicException(SelfXErrors.ACCOUNT_NULL, account);
			}
		}
		case EMAIL_VERIFY_CODE:{
			CacheMode mode = CacheMode.T_EMAIL_FOR_SIGN_IN;
			String key = createGeneralKey(mode, email);
			String ans = cache.get(key);
			if(ans == null)
				throw new LogicException(SelfXErrors.EMAIL_VERIFY_TIMEOUT, email);

			if(!ans.equals(emailVerifyCode)) {
				cache.remove(key);
				throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL,emailVerifyCode);
			}

			User user = uDAO.selectUniqueUserByField(DBConstants.F_EMAIL, email);
			cache.removeTempUser(uuId);
			return loadUser(user.getId(), user.getId());
		}

		case TEL_VERIFY_CODE:{
			CacheMode mode = CacheMode.T_TEL_FOR_SIGN_IN;
			String key = createGeneralKey(mode, email);
			String ans = cache.get(key);
			if(ans == null)
				throw new LogicException(SelfXErrors.TEL_VERIFY_TIMEOUT, tel);

			if(!ans.equals(telVerifyCode)) {
				cache.remove(key);
				throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL,telVerifyCode);
			}

			User user = uDAO.selectUniqueUserByField(DBConstants.F_TEL_NUM, tel);
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
			throw new LogicException(SelfXErrors.COMMON,"Unexpected");
		}
		User user=  getUser(userId);
		UserProxy proxy = new UserProxy();
		proxy.user = user;
		proxy.perms = getUserAllPerms(userId);
		return proxy;
	}
	
	@Override
	@Transactional
	public void signUp(String uuId,String account,String email,String emailVerifyCode,
			String tel,String telVerifyCode,String pwd,String nickName,Gender gender) throws LogicException, DBException {

		if(account.isBlank())
			throw new LogicException(SelfXErrors.SIGN_UP_ILLEGAL,"账号不能为空");
		
		if(nickName.isBlank())
			throw new LogicException(SelfXErrors.SIGN_UP_ILLEGAL,"昵称不能为空");
		
		if(gender == Gender.UNDECIDED)
			throw new LogicException(SelfXErrors.SIGN_UP_ILLEGAL,"性别不能为空");
		
		if(email.isBlank() && tel.isBlank()) {
			throw new LogicException(SelfXErrors.SIGN_UP_ILLEGAL,"邮箱和手机号必须至少填入一个");
		};
		
		if(!email.isBlank()) {
			try {
				String ans = cache.getTempUserMapVal( uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(emailVerifyCode)) {
					throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL);
				}
				String matchEmail = cache.getTempUserMapVal( uuId, EMAIL_KEY_FOR_SIGN_UP);
				if(!matchEmail.equals(email)) {
					throw new LogicException(SelfXErrors.INCONSISTENT_AUTHENTICATION_MAIL);
				}
			} catch (NoSuchElement e) {
				throw new LogicException(SelfXErrors.EMAIL_VERIFY_TIMEOUT);
			}
			
		}
		
		if(!tel.isBlank()) {
			try {
				String ans = cache.getTempUserMapVal( uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(telVerifyCode)) {
					throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL);
				}
				String matchTel = cache.getTempUserMapVal( uuId, TEL_KEY_FOR_SIGN_UP);
				if(!matchTel.equals(tel)) {
					throw new LogicException(SelfXErrors.INCONSISTENT_AUTHENTICATION_TEL);
				}
			} catch (NoSuchElement e) {
				throw new LogicException(SelfXErrors.TEL_VERIFY_TIMEOUT);
			}
		}
		signUpDirectly(uuId,account,pwd,nickName,gender,email,tel);
	}

	@Override
	public synchronized void signUpDirectly(String uuId,String account,String pwd,String nickName,Gender gender
			,String email,String tel) {
		User user = new User();
		user.setAccount(account);
		user.setNickName(nickName);
		user.setPassword(pwd);
		SecurityBooster.encodeUserPwd(user);
		user.setGender(gender);
		user.setEmail(email);
		user.setTelNum(tel);
		long uId = uDAO.insertUser(user);
		UserGroup defaultGroup = uDAO.selectUniqueExistedUserGroupByField(DBConstants.F_NAME, SelfX.DEFAULT_BASIC_USER_GROUP);
		/*这里比较特殊 是系统自动添加的 并且相对来说太过频繁 不应该重置缓存 这里选择直接添加进缓存 影响到的是 一个组里有多少用户*/
		uDAO.insertUsersToGroup(List.of(uId), defaultGroup.getId());
		cache.deleteGeneralKey(CacheMode.T_USER, uuId);
	}


	/**
	 * 似乎不用缓存比较好 但底层如果是用缓存取每一个的话 如果底层置信 则本函数置信
	 */
	@Override
	public List<User> getUsers(List<Long> usersId) throws LogicException, DBException {
		return uDAO.selectUsersByIds(usersId);
	}
	
	@Override
	public synchronized void addUsersToGroup(List<Long> usersId, long groupId,long loginId) throws LogicException, DBException {
		
		checkPerm(loginId, SelfXPerms.ADD_USERS_TO_PERM);
		
		List<User> users = getUsers(usersId);
		
		if(users.size() != usersId.size())
			throw new LogicException(SelfXErrors.INCONSISTENT_ARGS_BETWEEN_DATA,"数量不一致 "+usersId.size() + " vs " + users.size());
		
		if(!uDAO.includeUserGroup(groupId))
			throw new LogicException(SelfXErrors.INCONSISTENT_ARGS_BETWEEN_DATA,"用户组不存在 "+groupId);

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
	public synchronized void overrideGroupPerms(List<SelfXPerms> permsForOverride, long groupId, long loginId) throws LogicException, DBException {

		checkPerm(loginId, SelfXPerms.EDIT_PERMS_TO_GROUP);
		
		if(!uDAO.includeUserGroup(groupId)) {
			throw new LogicException(SelfXErrors.INCONSISTENT_ARGS_BETWEEN_DATA,"用户组不存在 "+groupId);
		}
		
		List<SelfXPerms> permsForThisGroup =uDAO.selectPermsByGroup(groupId)
				.stream().map(SelfXPerms::valueOfDBCode).toList();
		List<SelfXPerms> permsForAdd = permsForOverride.stream().filter(perm->!permsForThisGroup.contains(perm)).collect(toList());
		List<SelfXPerms> permsForDelete = permsForThisGroup.stream().filter(perm->!permsForOverride.contains(perm)).collect(toList());
		if(!permsForAdd.isEmpty()) {
			uDAO.insertPermsToGroup(permsForAdd, groupId);
		}
		if(!permsForDelete.isEmpty()) {
			uDAO.deletePermsFromGroup(permsForDelete, groupId);	
		}

		cache.clearPerms();
	}

	@Override
	public synchronized long createUserGroup(String name, long loginId) throws LogicException, DBException {
		
		checkPerm(loginId, SelfXPerms.CREATE_USER_GROUP);
		
		if(uDAO.includeUniqueUserGroupByField(DBConstants.F_NAME, name)) {
			throw new LogicException(SelfXErrors.DUP_USER_GROUP_NAME);
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

	@Nullable
	@Override
	public UserProxy retrieveAuthUserByUniqueFiledForSignIn(String uuId, String fieldName, String fieldVal) {
		try {
			User user = uDAO.selectUniqueUserByField(fieldName, fieldVal);
			cache.removeTempUser(uuId);
			return loadUser(user.getId(), user.getId());
		} catch (NoSuchElement e) {
			return null;
		}
	}

	@Override
	public YZMInfo createTelYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = yzmUtil.createYZM(old);
		try {
			cache.setTempUserMap( uuId, TEL_YZM_KEY, String.valueOf(rlt.xForCheck));
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);
		}
		return rlt;
	}

	@Override
	public YZMInfo createEmailYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = yzmUtil.createYZM(old);
		try {
			cache.setTempUserMap( uuId, EMAIL_YZM_KEY, String.valueOf(rlt.xForCheck));
		} catch (NoSuchElement e) {
			assert e.type ==  NoSuchElementType.REDIS_KEY_NOT_EXISTS;
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);
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
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);
		}
	}
	
	private boolean checkEmailYZM(String uuId,int x) throws LogicException {
		try {
			String answer = cache.getTempUserMapVal(uuId,EMAIL_YZM_KEY);
			int ans = Integer.parseInt(answer);
			return Math.abs(ans-x) <= RIGHT_RANGE_FOR_YZM;
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);
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
			throw new LogicException(SelfXErrors.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM 保证了key一定存在（极小的可能性不存在，但那不考虑了）*/
		try {
			String verifyCode = createVerifyCode();
			cache.setTempUserMap( uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
			cache.setTempUserMap( uuId, TEL_KEY_FOR_SIGN_UP, tel);
			SMSUtil.sendSMS(SMSUtil.SIGN_UP_TEMPLATE_ID, tel, verifyCode, cache.getExpirationSeconds());
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);

		}
	}

	@Override
	public void sendEmailVerifyCodeForSignUp(String email, String uuId, int YZM) throws LogicException {
		if(!checkEmailYZM(uuId, YZM)) {
			throw new LogicException(SelfXErrors.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM 保证了key一定存在（极小的可能性不存在，但那不考虑了）*/
		try {
			String verifyCode = createVerifyCode();
			cache.setTempUserMap(uuId, EMAIL_KEY_FOR_SIGN_UP, email);
			cache.setTempUserMap(uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
			EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignUpEmailMes(verifyCode));
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.TEMP_USER_TIMEOUT);
		}
	}
	
	@Override
	public void sendVerifyCodeForResetPWD(String account, String val, VerifyUserMethod method)
			throws LogicException, DBException {
		User user;
		try {
			user = uDAO.selectUniqueUserByField(DBConstants.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.NON_EXISTED_ACCOUNT,"不存在的账号"+account);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SelfXErrors.NON_EXISTED_EMAIL);
			}
			
			String verifyCode = createVerifyCode();
			String key = createTempKeyByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val);
			cache.set(key,verifyCode);
			EmailUtil.sendSimpleEmail(val,VERIFY_CODE_EMAIL_SUBJECT , createResetPwdMes(verifyCode));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SelfXErrors.NON_EXISTED_TEL);
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
		if(!uDAO.includeUniqueUserByField(DBConstants.F_TEL_NUM, tel)) {
			throw new LogicException(SelfXErrors.NON_EXISTED_TEL,tel);
		}
		String verifyCode = createVerifyCode();
		String key = createGeneralKey(CacheMode.T_TEL_FOR_SIGN_IN, tel);
		cache.set(key, verifyCode);
		SMSUtil.sendSMS(SMSUtil.SIGN_IN_TEMPLATE_ID, tel, verifyCode,  cache.getExpirationSeconds());
	}

	@Override
	public void sendEmailVerifyCodeForSignIn(String email) throws LogicException, DBException {
		if(!uDAO.includeUniqueUserByField(DBConstants.F_EMAIL, email)) {
			throw new LogicException(SelfXErrors.NON_EXISTED_EMAIL,email);
		}
		String verifyCode = createVerifyCode();
		String key = createGeneralKey(CacheMode.T_EMAIL_FOR_SIGN_IN, email);
		cache.set(key, verifyCode);
		EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignInEmailMes(verifyCode));
	}
	
	@Override
	public boolean exists(UserUniqueField field, String val) throws DBException, LogicException {
        return switch (field) {
            case ACCOUNT -> uDAO.includeUniqueUserByField(DBConstants.F_ACCOUNT, val);
            case EMAIL -> uDAO.includeUniqueUserByField(DBConstants.F_EMAIL, val);
            case TEL_NUM -> uDAO.includeUniqueUserByField(DBConstants.F_TEL_NUM, val);
            case WEI_XIN_OPEN_ID ->uDAO.includeUniqueUserByField(DBConstants.F_WEI_XIN_OPEN_ID, val);
            case ID_NUM -> uDAO.includeUniqueUserByField(DBConstants.F_ID_NUM, val);
            case NICK_NAME -> uDAO.includeUniqueUserByField(DBConstants.F_NICK_NAME, val);
            default -> throw new RuntimeException("未配置的检查类型" + field);
        };
	}

	@Override
	public boolean confirmTempUser(String uuId) {
		return cache.existsForTempUser(uuId);
	}

	
	@Override
	public List<UserGroupProxy> loadAllUserGroups(long loginId) throws DBException, LogicException {
		checkPerm(loginId, SelfXPerms.SEE_USERS_AND_USER_GROUPS_DATA);
		
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
	public List<SelfXPerms> loadPermsOfGroup(long groupId, long loginId) throws DBException, LogicException {
		checkPerm(loginId, SelfXPerms.SEE_USERS_AND_USER_GROUPS_DATA);
		
		return uDAO.selectPermsByGroup(groupId).stream().map(SelfXPerms::valueOfDBCode).collect(toList());
	}

	@Override
	public UserSummary loadUserSummary(long loginId) throws LogicException, DBException {
		checkPerm(loginId, SelfXPerms.SEE_USERS_AND_USER_GROUPS_DATA);
		UserSummary summary = new UserSummary();
		summary.countUsers = uDAO.countAllUsers();
		/**
		 * 所谓的活跃用户 指的就是缓存里的用户
		 */
		summary.countActiveUsers = cache.countAllEntities(User.class);
		return summary;
	}

	@Override
	public List<UserProxy> loadUsersOfGroup(long groupId, long loginId) throws LogicException, DBException {
		checkPerm(loginId, SelfXPerms.SEE_USERS_AND_USER_GROUPS_DATA);
		return uDAO.selectUsersByGroup(groupId, SelfX.MAX_DB_LINES_IN_ONE_SELECTS).stream().map(user->{
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
				User user = uDAO.selectUniqueUserByField(DBConstants.F_EMAIL, val);
				EmailUtil.sendSimpleEmail(val, SelfX.BRAND_NAME+"找回账号", createRetrieveAccountMes(user.getAccount()));
			} catch (NoSuchElement e) {
				throw new LogicException(SelfXErrors.NON_EXISTED_EMAIL);
			}
			break;
		case TEL_VERIFY_CODE:
			try {
				User user = uDAO.selectUniqueUserByField(DBConstants.F_TEL_NUM, val);
				SMSUtil.sendSMS(SMSUtil.RETRIEVE_ACCOUNT_TEMPLATE_ID, val, user.getAccount());
			} catch (NoSuchElement e) {
				throw new LogicException(SelfXErrors.NON_EXISTED_TEL);
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
			user = uDAO.selectUniqueUserByField(DBConstants.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SelfXErrors.NON_EXISTED_ACCOUNT);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SelfXErrors.NON_EXISTED_EMAIL);
			}
			
			String key = createTempKeyByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val);
			String forCheck = cache.get(key);
			if(forCheck == null)
				throw new LogicException(SelfXErrors.EMAIL_VERIFY_TIMEOUT);
			if(!forCheck.equals(verifyCode)) {
				cache.remove(key);
				throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL);
			}
			
			user.setPassword(resetPWD);
			SecurityBooster.encodeUserPwd(user);
			cache.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SelfXErrors.NON_EXISTED_TEL,"账号和手机号不匹配"+val);
			}

			String key = createTempKeyByBiIdentifiers(CacheMode.T_TEL_FOR_RESET_PWD,account,val);
			String forCheck = cache.get(key);
			if(forCheck == null)
				throw new LogicException(SelfXErrors.TEL_VERIFY_TIMEOUT);

			if(!forCheck.equals(verifyCode)) {
				cache.remove(key);
				throw new LogicException(SelfXErrors.CHECK_VERIFY_CODE_FAIL);
			}		
			
			user.setPassword(resetPWD);
			SecurityBooster.encodeUserPwd(user);
			cache.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		default:
			assert false;
			throw new RuntimeException("未配置的找回类型"+method.getName());
		}
	}

	@Override
	public UserBasicInfo getUserBasicInfo(Long loginId,Long targetId) {
		boolean isSameUser = loginId.equals(targetId);
		UserBasicInfo info = new UserBasicInfo();
		User user = getUser(targetId);
		info.nickName = user.getNickName();
		info.motto = user.getMotto();
		info.gender = user.getGender().getDbCode();
		info.isSelf = isSameUser;
		info.portraitId = securityBooster.encodeStableCommonId(user.getPortraitId());
		return info;
	}

	@Override
	public void updateUser(long loginId, String nickName, Gender gender, String motto, Long portraitId) {
		locker.lockByUserAndClass(loginId,()->{
			User user = uDAO.selectUser(loginId);
			user.setNickName(nickName);
			user.setGender(gender);
			user.setMotto(motto);
			Long originalPortraitId = user.getPortraitId();
			if(originalPortraitId != null && !originalPortraitId.equals(portraitId)){
				filesService.deleteFileRecord(loginId,originalPortraitId);
			}
			user.setPortraitId(portraitId);
			cache.saveEntity(user, one -> uDAO.updateExistedUser(user));
		});
	}

}
