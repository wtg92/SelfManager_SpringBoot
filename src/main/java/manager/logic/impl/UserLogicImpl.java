package manager.logic.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import manager.dao.DAOFactory;
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
import manager.logic.sub.CacheScheduler;
import manager.system.CacheMode;
import manager.system.Gender;
import manager.system.NoSuchElementType;
import manager.system.SM;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.UserUniqueField;
import manager.system.VerifyUserMethod;
import manager.util.CacheUtil;
import manager.util.EmailUtil;
import manager.util.SMSUtil;
import manager.util.SecurityUtil;
import manager.util.ThrowableFunction;
import manager.util.ThrowableSupplier;
import manager.util.YZMUtil;
import manager.util.YZMUtil.YZMInfo;

public class UserLogicImpl extends UserLogic {
	final private static Logger logger = Logger.getLogger(UserLogicImpl.class.getName());

	private final UserDAO uDAO = DAOFactory.getUserDAO();
	
	@Override
	public boolean hasPerm(long userId, SMPerm perm) throws LogicException, DBException {
		/*admin owning all perms*/
		if(isAdmin(userId)) {
			return true;
		}
		ThrowableSupplier<List<Long>, DBException> userGroupGenerator = ()-> uDAO.selectGroupsByUser(userId);
		List<Long> groupsId = CacheScheduler.getRIds(CacheMode.R_ONE_TO_MANY_FORMER,SMDB.T_R_USER_GROUP,userId,userGroupGenerator);
		
		for(Long groupId : groupsId) {
			ThrowableSupplier<List<Integer>, DBException> groupPermGenerator = ()-> uDAO.selectPermsByGroup(groupId);
			List<Integer> permsId = CacheScheduler.getRIdsInInt(CacheMode.R_ONE_TO_MANY_FORMER, SMDB.T_R_GROUP_PERM, groupId, groupPermGenerator);
			if(permsId.contains(perm.getDbCode()))
				return true;
		}
		
		return false;
	}

	private Set<SMPerm> getUserAllPerms(long userId) throws DBException, LogicException{
		if(isAdmin(userId)) {
			return Arrays.stream(SMPerm.values()).filter(perm->perm != SMPerm.UNDECIDED).collect(toSet());
		}
		
		Set<SMPerm> perms = new HashSet<SMPerm>();
		
		ThrowableSupplier<List<Long>, DBException> userGroupGenerator = ()-> uDAO.selectGroupsByUser(userId);
		List<Long> groupsId = CacheScheduler.getRIds(CacheMode.R_ONE_TO_MANY_FORMER,SMDB.T_R_USER_GROUP,userId,userGroupGenerator);
		
		for(Long groupId : groupsId) {
			ThrowableSupplier<List<Integer>, DBException> groupPermGenerator = ()-> uDAO.selectPermsByGroup(groupId);
			List<Integer> permsId = CacheScheduler.getRIdsInInt(CacheMode.R_ONE_TO_MANY_FORMER, SMDB.T_R_GROUP_PERM, groupId, groupPermGenerator);
			perms.addAll(permsId.stream().map(SMPerm::valueOfDBCode).collect(toSet()));
		}
		
		return perms;
	}
	

	@Override
	public UserProxy signIn(String uuId,VerifyUserMethod method, String account,String accountPwd, String email, String emailVerifyCode, String tel, String telVerifyCode)
			throws LogicException, DBException {
		switch (method) {
		case ACCOUNT_PWD: {
			try {
				User user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);

				if (!SecurityUtil.verifyUserPwd(user, accountPwd)) {
					throw new LogicException(SMError.PWD_WRONG);
				}
				
				CacheScheduler.putEntityToCacheById(user);
				CacheScheduler.deleteTempKey(CacheMode.T_USER, uuId);
				
				return loadUser(user.getId(), user.getId());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.ACCOUNT_NULL, account);
			}
		}
		case EMAIL_VERIFY_CODE:{
			try {
				String ans = CacheScheduler.getTempVal(CacheMode.T_EMAIL_FOR_SIGN_IN, email);
				if(!ans.equals(emailVerifyCode)) {
					throw new LogicException(SMError.EMAIL_VERIFY_WRONG,emailVerifyCode);
				}
				
				User user = uDAO.selectUniqueUserByField(SMDB.F_EMAIL, email);
				CacheScheduler.putEntityToCacheById(user);
				CacheScheduler.deleteTempKey(CacheMode.T_USER, uuId);
				
				return loadUser(user.getId(), user.getId());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.EMAIL_VERIFY_TIMEOUT, email);
			}
		}

		case TEL_VERIFY_CODE:{
			try {
				String ans = CacheScheduler.getTempVal(CacheMode.T_TEL_FOR_SIGN_IN, tel);
				if(!ans.equals(telVerifyCode)) {
					throw new LogicException(SMError.TEL_VERIFY_WRONG,telVerifyCode);
				}
				
				User user = uDAO.selectUniqueUserByField(SMDB.F_TEL_NUM, tel);
				CacheScheduler.putEntityToCacheById(user);
				CacheScheduler.deleteTempKey(CacheMode.T_USER, uuId);
				
				return loadUser(user.getId(), user.getId());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.TEL_VERIFY_TIMEOUT, email);
			}
		}
		default:
			assert false : method;
			throw new RuntimeException("?????????????????????????????? " + method);
		}
	}

	@Override
	public User getUser(long userId) throws LogicException, DBException {
		ThrowableSupplier<User, DBException> generator = ()-> uDAO.selectExistedUser(userId);
		return CacheScheduler.getOne(CacheMode.E_ID,userId,User.class,generator);
	}

	@Override
	public UserProxy loadUser(long userId, long loginerId) throws LogicException, DBException {
		/*TODO loginerId??????????????????????????? ???????????????*/
		User user=  getUser(userId);
		UserProxy proxy = new UserProxy();
		proxy.user = user;
		proxy.perms = getUserAllPerms(userId);
		return proxy;
	}
	
	@Override
	public synchronized long signUp(String uuId,String account,String email,String emailVerifyCode,
			String tel,String telVerifyCode,String pwd,String nickName,Gender gender) throws LogicException, DBException {

		User user = new User();
		
		if(account.strip().length() == 0)
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"??????????????????");
		
		user.setAccount(account);
		
		if(nickName.strip().length() == 0)
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"??????????????????");
		
		user.setNickName(nickName);
		
		user.setPassword(pwd);
		SecurityUtil.encodeUserPwd(user);
		
		if(gender == Gender.UNDECIDED)
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"??????????????????");
		
		user.setGender(gender);
		
		if(email.strip().length() == 0 && tel.strip().length() == 0) {
			throw new LogicException(SMError.SIGN_UP_ILLGEAL,"??????????????????????????????????????????");
		};
		
		if(email.strip().length()>0) {
			try {
				String ans = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER, uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(emailVerifyCode)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"?????????????????????");
				}
				String matchEmail = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER, uuId, EMAIL_KEY_FOR_SIGN_UP);
				if(!matchEmail.equals(email)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"????????????????????????????????????");
				}
				
				user.setEmail(email);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"??????????????????????????????");
			}
			
		}
		
		if(tel.strip().length()>0) {
			try {
				String ans = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER, uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP);
				if(!ans.equals(telVerifyCode)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"?????????????????????");
				}
				String matchTel = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER, uuId, TEL_KEY_FOR_SIGN_UP);
				if(!matchTel.equals(tel)) {
					throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"????????????????????????????????????");
				}
				
				
				user.setTelNum(tel);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.CHECK_VERIFY_CODE_FAIL,"??????????????????????????????");
			}
		}
		long uId = uDAO.insertUser(user);
		UserGroup defaultGroup = uDAO.selectUniqueExistedUserGroupByField(SMDB.F_NAME, SM.DEFAULT_BASIC_USER_GROUP);
		/*?????????????????? ???????????????????????? ?????????????????????????????? ????????????????????? ????????????????????????????????? ??????????????? ???????????????????????????*/
		uDAO.insertUsersToGroup(Arrays.asList(uId), defaultGroup.getId());
		
		CacheScheduler.appendROnlyIfExists(CacheMode.R_ONE_TO_MANY_LATTER,SMDB.T_R_USER_GROUP,defaultGroup.getId(),uId);
		CacheScheduler.deleteTempKey(CacheMode.T_USER, uuId);
		
		return uId;
	}
	
	
	
	@Override
	public List<User> getUsers(List<Long> usersId) throws LogicException, DBException {
		ThrowableFunction<Long,User, DBException> generator = (userId)-> uDAO.selectExistedUser(userId);
		return CacheScheduler.getOnes(CacheMode.E_ID,usersId,User::getId,User.class,generator);
	}
	
	@Override
	public synchronized void addUsersToGroup(List<Long> usersId, long groupId,long loginerId) throws LogicException, DBException {
		
		checkPerm(loginerId, SMPerm.ADD_USERS_TO_PERM);
		
		List<User> users = getUsers(usersId);
		
		if(users.size() != usersId.size())
			throw new LogicException(SMError.INCONSTSTANT_ARGS_BETWEEN_DATA,"??????????????? "+usersId.size() + " vs " + users.size());
		
		ThrowableSupplier<Boolean, DBException> judger = ()->uDAO.includeUserGroup(groupId);
		if(!CacheScheduler.existsByIdentifier(CacheMode.E_ID,SMDB.T_USER_GROUP,groupId,judger)) {
			throw new LogicException(SMError.INCONSTSTANT_ARGS_BETWEEN_DATA,"?????????????????? "+groupId);
		}
		
		ThrowableSupplier<List<Long>, DBException> generator = ()-> uDAO.selectUsersIdByGroup(groupId);
		List<Long> usersForThisGroup = CacheScheduler.getRIds(CacheMode.R_ONE_TO_MANY_LATTER, SMDB.T_R_USER_GROUP, groupId, generator);
		List<Long> distinctUsers = usersId.stream().filter(uId->!usersForThisGroup.contains(uId)).collect(toList());
		
		if(distinctUsers.size() == 0) {
			logger.log(Level.WARNING,"?????????user??????????????????????????? "+groupId,"uL.addUsersToGroup");
			return;
		}
		
		uDAO.insertUsersToGroup(distinctUsers, groupId);
		CacheScheduler.deleteRCachesIfExist(CacheMode.R_ONE_TO_MANY_FORMER,SMDB.T_R_USER_GROUP,usersId);
		CacheScheduler.deleteRCacheIfExists(CacheMode.R_ONE_TO_MANY_LATTER, SMDB.T_R_USER_GROUP, groupId);
	}

	@Override
	public synchronized void overrideGroupPerms(List<SMPerm> permsForOverride, long groupId, long loginerId) throws LogicException, DBException {

		checkPerm(loginerId, SMPerm.EDIT_PERMS_TO_GROUP);
		
		ThrowableSupplier<Boolean, DBException> judger = ()->uDAO.includeUserGroup(groupId);
		if(!CacheScheduler.existsByIdentifier(CacheMode.E_ID,SMDB.T_USER_GROUP,groupId,judger)) {
			throw new LogicException(SMError.INCONSTSTANT_ARGS_BETWEEN_DATA,"?????????????????? "+groupId);
		}
		
		ThrowableSupplier<List<Integer>, DBException> generator = ()-> uDAO.selectPermsByGroup(groupId);
		List<SMPerm> permsForThisGroup = CacheScheduler.getRIdsInInt(CacheMode.R_ONE_TO_MANY_FORMER, SMDB.T_R_GROUP_PERM, groupId, generator)
				.stream().map(SMPerm::valueOfDBCode).collect(toList());
		List<SMPerm> permsForAdd = permsForOverride.stream().filter(perm->!permsForThisGroup.contains(perm)).collect(toList());
		List<SMPerm> permsForDelete = permsForThisGroup.stream().filter(perm->!permsForOverride.contains(perm)).collect(toList());
		if(permsForAdd.size()>0) {
			uDAO.insertPermsToGroup(permsForAdd, groupId);
		}
		if(permsForDelete.size()>0) {
			uDAO.deletePermsFromGroup(permsForDelete, groupId);	
		}

		CacheScheduler.deleteRCachesIfExistByInt(CacheMode.R_ONE_TO_MANY_LATTER,SMDB.T_R_GROUP_PERM,permsForAdd.stream().map(SMPerm::getDbCode).collect(toList()));
		CacheScheduler.deleteRCacheIfExists(CacheMode.R_ONE_TO_MANY_FORMER, SMDB.T_R_GROUP_PERM, groupId);
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
			if(CacheScheduler.setTempMapOnlyIfKeyNotExists(CacheMode.T_USER,uuId,"","")) {
				break;
			}else {
				logger.log(Level.WARNING,"?????????UUID???BUG???????????????????UUID???"+uuId);
			}
		}
		return uuId;
	}

	@Override
	public YZMInfo createTelYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = YZMUtil.createYZM(old);
		try {
			CacheScheduler.setTempMap(CacheMode.T_USER, uuId, TEL_YZM_KEY, String.valueOf(rlt.xForCheck));
		} catch (NoSuchElement e) {
			assert e.type ==  NoSuchElementType.REDIS_KEY_NOT_EXISTS;
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
		return rlt;
	}

	@Override
	public YZMInfo createEmailYZM(String uuId, String old) throws LogicException {
		YZMInfo rlt = YZMUtil.createYZM(old);
		try {
			CacheScheduler.setTempMap(CacheMode.T_USER, uuId, EMAIL_YZM_KEY, String.valueOf(rlt.xForCheck));
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
			rlt.checkSeccuss = true;
			return rlt;
		}
		return createTelYZM(uuId, imgSrc);
	}

	@Override
	public YZMInfo checkEmailYZMAndRefreshIfFailed(String uuId, int x, String imgSrc) throws LogicException {
		if(checkEmailYZM(uuId, x)) {
			YZMInfo rlt = new YZMInfo();
			rlt.checkSeccuss = true;
			return rlt;
		}
		return createEmailYZM(uuId, imgSrc);
	}
	
	private boolean checkTelYZM(String uuId,int x) throws LogicException {
		try {
			String answer = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER,uuId,TEL_YZM_KEY);
			int ans = Integer.parseInt(answer);
			return Math.abs(ans-x) <= RIGHT_RANGE_FOR_YZM;
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
	}
	
	private boolean checkEmailYZM(String uuId,int x) throws LogicException {
		try {
			String answer = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER,uuId,EMAIL_YZM_KEY);
			int ans = Integer.parseInt(answer);
			return Math.abs(ans-x) <= RIGHT_RANGE_FOR_YZM;
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.TEMP_USER_TIMEOUT);
		}
	}
	
	@Override
	public String sendTelVerifyCodeForSignUp(String tel, String uuId, int YZM) throws LogicException {
		if(!checkTelYZM(uuId, YZM)) {
			throw new LogicException(SMError.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM ?????????key??????????????????????????????????????????????????????????????????*/
		try {
			String verifyCode = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER,uuId,TEL_VERIFY_CODE_KEY_FOR_SIGN_UP);
			CacheScheduler.setTempMap(CacheMode.T_USER, uuId, TEL_KEY_FOR_SIGN_UP, tel);
			/*??????????????????*/
			/*TODO ??????????????????????????????????????????????????????*/
			SMSUtil.sendSMS(SMSUtil.SIGN_UP_TEMPLETE_ID, tel, verifyCode,CacheUtil.ALIVE_SECONDS/60);
			return verifyCode;
		} catch (NoSuchElement e) {
			try {
				String verifyCode = createVerifiCode();
				CacheScheduler.setTempMap(CacheMode.T_USER, uuId, TEL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
				CacheScheduler.setTempMap(CacheMode.T_USER, uuId, TEL_KEY_FOR_SIGN_UP, tel);
				SMSUtil.sendSMS(SMSUtil.SIGN_UP_TEMPLETE_ID, tel, verifyCode,CacheUtil.ALIVE_SECONDS/60);
				return verifyCode;
			} catch (NoSuchElement e1) {
				assert e.type ==  NoSuchElementType.REDIS_KEY_NOT_EXISTS;
				throw new LogicException(SMError.TEMP_USER_TIMEOUT);
			}

		}
	}

	@Override
	public String sendEmailVerifyCodeForSignUp(String email, String uuId, int YZM) throws LogicException {
		if(!checkEmailYZM(uuId, YZM)) {
			throw new LogicException(SMError.CHECK_YZM_ERROR);
		}
		/*checkEmailYZM ?????????key??????????????????????????????????????????????????????????????????*/
		try {
			String verifyCode = CacheScheduler.getTempMapValWihoutReset(CacheMode.T_USER,uuId,EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP);
			CacheScheduler.setTempMap(CacheMode.T_USER, uuId, EMAIL_KEY_FOR_SIGN_UP, email);
			/*??????????????????*/
			/*TODO ??????????????????????????????????????????????????????*/
			EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignUpEmailMes(verifyCode));
			return verifyCode;
		} catch (NoSuchElement e) {
			try {
				String verifyCode = createVerifiCode();
				CacheScheduler.setTempMap(CacheMode.T_USER, uuId, EMAIL_KEY_FOR_SIGN_UP, email);
				CacheScheduler.setTempMap(CacheMode.T_USER, uuId, EMAIL_VERIFY_CODE_KEY_FOR_SIGN_UP, verifyCode);
	
				EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignUpEmailMes(verifyCode));
				return verifyCode;
			} catch (NoSuchElement e1) {
				assert e.type ==  NoSuchElementType.REDIS_KEY_NOT_EXISTS;
				throw new LogicException(SMError.TEMP_USER_TIMEOUT);
			}

		}
	}
	
	@Override
	public void sendVerifyCodeForResetPWD(String account, String val, VerifyUserMethod method)
			throws LogicException, DBException {
		User user;
		try {
			user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.RESET_PWD_ERROR,"??????????????????"+account);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"????????????????????????"+val);
			}
			
			String verifyCode = createVerifiCode();
			CacheScheduler.setTempByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val, verifyCode);
			EmailUtil.sendSimpleEmail(val,VERIFY_CODE_EMAIL_SUBJECT , createResetPwdMes(verifyCode));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"???????????????????????????"+val);
			}
			
			String verifyCode = createVerifiCode();
			CacheScheduler.setTempByBiIdentifiers(CacheMode.T_TEL_FOR_RESET_PWD, account,val, verifyCode);
			SMSUtil.sendSMS(SMSUtil.RESET_PWD_TEMPLETE_ID, val, verifyCode);
			return;
		}
		default:
			assert false;
			throw new RuntimeException("????????????????????????"+method.getName());
		}
	}
	
	
	
	@Override
	public String sendTelVerifyCodeForSignIn(String tel) throws LogicException, DBException {
		if(!uDAO.includeUniqueUserByField(SMDB.F_TEL_NUM, tel)) {
			throw new LogicException(SMError.NON_EXISTED_TEL,tel);
		}
		String verifyCode = createVerifiCode();
		CacheScheduler.setTemp(CacheMode.T_TEL_FOR_SIGN_IN, tel, verifyCode);
		SMSUtil.sendSMS(SMSUtil.SIGN_IN_TEMPLETE_ID, tel, verifyCode,CacheUtil.ALIVE_SECONDS/60);
		return verifyCode;
	}

	@Override
	public String sendEmailVerifyCodeForSignIn(String email) throws LogicException, DBException {
		if(!uDAO.includeUniqueUserByField(SMDB.F_EMAIL, email)) {
			throw new LogicException(SMError.NON_EXISTED_EMAIL,email);
		}
		String verifyCode = createVerifiCode();
		CacheScheduler.setTemp(CacheMode.T_EMAIL_FOR_SIGN_IN, email, verifyCode);
		EmailUtil.sendSimpleEmail(email,VERIFY_CODE_EMAIL_SUBJECT , createSignInEmailMes(verifyCode));
		return verifyCode;
	}
	
	@Override
	public boolean exists(UserUniqueField field, String val) throws DBException, LogicException {
		switch (field) {
		case ACCOUNT:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getAccount, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_ACCOUNT, val));
		case EMAIL:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getEmail, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_EMAIL, val));
		case TEL_NUM:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getTelNum, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_TEL_NUM, val));
		case WEI_XIN_OPEN_ID:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getWeiXinOpenId, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_WEI_XIN_OPEN_ID, val));
		case ID_NUM:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getIdNum, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_ID_NUM, val));
		case NICK_NAME:
			return CacheScheduler.existsByField(CacheMode.E_ID, User::getNickName, val,  User.class,
					()->uDAO.includeUniqueUserByField(SMDB.F_NICK_NAME, val));
		default:
			throw new RuntimeException("????????????????????????"+field);
		}

	}

	@Override
	public boolean confirmTempUser(String uuId) {
		return CacheScheduler.existsForTemp(CacheMode.T_USER, uuId);
	}

	
	@Override
	public List<UserGroupProxy> loadAllUserGroups(long loginerId) throws DBException, LogicException {
		checkPerm(loginerId, SMPerm.SEE_USRS_AND_USER_GROUPS_DATA);
		
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
		checkPerm(loginerId, SMPerm.SEE_USRS_AND_USER_GROUPS_DATA);
		
		return uDAO.selectPermsByGroup(groupId).stream().map(SMPerm::valueOfDBCode).collect(toList());
	}

	@Override
	public UserSummary loadUserSummary(long loginerId) throws LogicException, DBException {
		checkPerm(loginerId, SMPerm.SEE_USRS_AND_USER_GROUPS_DATA);
		UserSummary summary = new UserSummary();
		summary.countUsers = uDAO.countAllUsers();
		summary.countActiveUsers = CacheScheduler.countAll(CacheMode.E_ID, User.class);
		return summary;
	}

	@Override
	public List<UserProxy> loadUsersOfGroup(long groupId, long loginerId) throws LogicException, DBException {
		checkPerm(loginerId, SMPerm.SEE_USRS_AND_USER_GROUPS_DATA);
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
				EmailUtil.sendSimpleEmail(val,SM.BRAND_NAME+"????????????", createRetrieveAccountMes(user.getAccount()));
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.RETRIEVE_USER_ERROR,"????????????email "+val);
			}
			break;
		case TEL_VERIFY_CODE:
			try {
				User user = uDAO.selectUniqueUserByField(SMDB.F_TEL_NUM, val);
				SMSUtil.sendSMS(SMSUtil.RETRIEVE_ACOUNT_TEMPLETE_ID, val, user.getAccount());
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.RETRIEVE_USER_ERROR,"????????????????????? "+val);
			}
			break;
		default:
			assert false;
			throw new RuntimeException("???????????????????????? "+method.getName());
		}
	}

	@Override
	public synchronized void resetPWD(String account, String val, VerifyUserMethod method, String verifyCode, String resetPWD) throws LogicException, DBException  {
		User user;
		try {
			user = uDAO.selectUniqueUserByField(SMDB.F_ACCOUNT, account);
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.RESET_PWD_ERROR,"?????????????????? "+account);
		}
		
		switch(method) {
		case EMAIL_VERIFY_CODE:{
			if(user.getEmail() == null || !user.getEmail().equals(val)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"???????????????????????? "+val);
			}
			
			String forCheck = null;
			try {
				forCheck = CacheScheduler.getTempValByBiIdentifiers(CacheMode.T_EMAIL_FOR_RESET_PWD,account,val);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"????????????????????????????????????");
			}
			if(!forCheck.equals(verifyCode)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"??????????????? "+verifyCode);
			}
			
			user.setPassword(resetPWD);
			SecurityUtil.encodeUserPwd(user);
			CacheScheduler.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		case TEL_VERIFY_CODE:{
			if(user.getTelNum() == null ||!user.getTelNum().equals(val)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"???????????????????????????"+val);
			}
			
			String forCheck = null;
			try {
				forCheck = CacheScheduler.getTempValByBiIdentifiers(CacheMode.T_TEL_FOR_RESET_PWD, account,val);
			} catch (NoSuchElement e) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"????????????????????????????????????");
			}
			if(!forCheck.equals(verifyCode)) {
				throw new LogicException(SMError.RESET_PWD_ERROR,"??????????????? "+verifyCode);
			}		
			user.setPassword(resetPWD);
			SecurityUtil.encodeUserPwd(user);
			CacheScheduler.saveEntity(user, one -> uDAO.updateExistedUser(user));
			return;
		}
		default:
			assert false;
			throw new RuntimeException("????????????????????????"+method.getName());
		}
	}

}
