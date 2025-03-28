package manager.dao;

import java.util.List;

import manager.entity.general.SystemMapping;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SelfXErrors;
import manager.system.SelfXPerms;

public interface  UserDAO {
	
	long insertUser(User user) throws DBException;
	void updateExistedUser(User user) throws DBException;
	User selectUser(long id) throws NoSuchElement, DBException;
	User selectUniqueUserByField(String field,Object val) throws DBException,NoSuchElement;
	boolean includeUniqueUserByField(String fieldName,String fieldVal) throws DBException;
	
	boolean includeUserGroup(long groupId) throws DBException;
	boolean includeUniqueUserGroupByField(String field,String val) throws DBException;
	List<Long> selectGroupsByUser(long userId) throws DBException;;
	List<Integer> selectPermsByGroup(long groupId) throws DBException;;
	List<Long> selectUsersIdByGroup(long groupId) throws DBException;;
	List<User> selectUsersByGroup(long groupId,long limit) throws DBException;
	
	List<UserGroup> selectAllUserGroup() throws DBException;
	
	long countUsersOfGroup(long groupId) throws DBException;
	long countAllUsers() throws DBException;
	
	long insertUserGroup(UserGroup group) throws DBException;
	UserGroup selectUniqueExistedUserGroupByField(String field,String val)throws DBException;
	void updateExistedUserGroup(UserGroup group) throws DBException;
	
	void insertUsersToGroup(List<Long> usersId, long groupId) throws DBException;
	void deleteUsersFromGroup(List<Long> usersId, long groupId) throws DBException;
	
	void insertPermsToGroup(List<SelfXPerms> perms, long groupId) throws DBException;
	
	void deletePermsFromGroup(List<SelfXPerms> perms, long groupId) throws DBException;

	List<User> selectUsersByIds(List<Long> userIds);

	default User selectExistedUser(long id) throws DBException {
		try {
			return selectUser(id);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}
	
	default User selectUniqueExistedUserByField(String field,String val) throws DBException{
		try {
			return selectUniqueUserByField(field, val);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,field+" "+val);
		}
	}

    boolean hasPerm(long userId, SelfXPerms perm);

	List<Integer> selectPermsByUser(long userId);

	long insertSystemMapping(SystemMapping mapping);
}
