package manager;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import manager.dao.DAOFactory;
import manager.dao.UserDAO;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.logic.CacheScheduler;
import manager.system.Gender;
import manager.system.SM;
import manager.system.SMDB;
import manager.util.SecurityUtil;

public abstract class TestUtil {
	
	public static void initEnvironment() {
		DAOFactory.deleteAllTables();
		CacheScheduler.clearAllCache_ONLYFORTEST();
	}
	
	public static int addAdmin() throws LogicException, DBException {
		UserDAO uDAO = DAOFactory.getUserDAO();
		User user = new User();
		user.setAccount(SM.ADMIN_ACCOUNT);
		user.setNickName("admin");
		user.setPassword("123456789");
		user.setGender(Gender.OTHERS);
		SecurityUtil.encodeUserPwd(user);
		return uDAO.insertUser(user);
	}
	
	public static void initData() throws DBException {
		UserDAO uDAO = DAOFactory.getUserDAO();
		UserGroup group = new UserGroup();
		group.setName(SM.DEFAULT_BASIC_USER_GROUP);
		assert !uDAO.includeUniqueUserGroupByField(SMDB.F_NAME,group.getName());
		uDAO.insertUserGroup(group);
	}
	
	public static void checkEnumNoDup(Class<?> enumCla,String filedName) throws Exception {
		assert enumCla.getEnumConstants().length > 0 : "怎么能传进来没有实体的枚举";
		
		Field field =  enumCla.getEnumConstants()[0].getClass().getDeclaredField(filedName);
		field.setAccessible(true);
		Arrays.asList(enumCla.getEnumConstants()).stream().collect(Collectors.groupingBy(e->{
			try {
				return field.get(e);
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
				assert false;
				return null;
			}
		})).forEach((key,values)->{
			assertEquals("DUP "+key,1, values.size());
		});
	}
	
	
}
