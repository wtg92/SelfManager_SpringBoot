package manager.dao;

import java.util.List;

import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;

public interface UserDAO {
	
	int insertUser(User user) throws DBException;
	void updateExistedUser(User user) throws DBException;
	User selectUser(int id) throws NoSuchElement, DBException;
	User selectUniqueUserByField(String field,Object val) throws DBException,NoSuchElement;
	boolean includeUniqueUserByField(String fieldName,String fieldVal) throws DBException;
	
	boolean includeUserGroup(int groupId) throws DBException;
	boolean includeUniqueUserGroupByField(String field,String val) throws DBException;
	List<Integer> selectGroupsByUser(int userId) throws DBException;;
	List<Integer> selectPermsByGroup(int groupId) throws DBException;;
	List<Integer> selectUsersIdByGroup(int groupId) throws DBException;;
	List<User> selectUsersByGroup(int groupId,int limit) throws DBException;
	
	List<UserGroup> selectAllUserGroup() throws DBException;
	
	long countUsersOfGroup(int groupId) throws DBException;
	long countAllUsers() throws DBException;
	
	int insertUserGroup(UserGroup group) throws DBException;
	UserGroup selectUniqueExistedUserGroupByField(String field,String val)throws DBException;
	void updateExistedUserGroup(UserGroup group) throws DBException;
	
	void insertUsersToGroup(List<Integer> usersId, int groupId) throws DBException;
	void deleteUsersFromGroup(List<Integer> usersId, int groupId) throws DBException;
	
	void insertPermsToGroup(List<SMPerm> perms,int groupId) throws DBException;
	
	void deletePermsFromGroup(List<SMPerm> perms,int groupId) throws DBException;
	
	
	default User selectExistedUser(int id) throws DBException {
		try {
			return selectUser(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}
	
	default User selectUniqueExistedUserByField(String field,String val) throws DBException{
		try {
			return selectUniqueUserByField(field, val);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,field+" "+val);
		}
	}
	
}
