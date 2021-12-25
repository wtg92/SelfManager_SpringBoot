package manager.dao.impl;

import static manager.util.DBUtil.*;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.dao.UserDAO;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;

public class UserDAOImpl implements UserDAO {

	private final SessionFactory hbFactory = getHibernateSessionFactory();

	@Override
	public int insertUser(User user) throws DBException {
		return insertEntity(user, hbFactory);
	}

	@Override
	public void updateExistedUser(User user) throws DBException {
		updateExistedEntity(user, hbFactory);
	}

	@Override
	public User selectUser(int id) throws NoSuchElement, DBException {
		return selectEntity(id,User.class ,hbFactory);
	}

	@Override
	public boolean includeUniqueUserByField(String attrName, String attrVal) throws DBException {
		return includeUniqueEntityByField(User.class, attrName, attrVal, hbFactory);
	}


	@Override
	public int insertUserGroup(UserGroup group) throws DBException {
		return insertEntity(group, hbFactory);
	}

	@Override
	public void updateExistedUserGroup(UserGroup group) throws DBException {
		updateExistedEntity(group, hbFactory);
	}

	@Override
	public void insertUsersToGroup(List<Integer> usersId, int groupId) throws DBException {
		insertGeneralRTableData(SMDB.T_R_USER_GROUP, usersId, groupId, hbFactory);
	}

	@Override
	public void insertPermsToGroup(List<SMPerm> perms, int groupId) throws DBException {
		insertGeneralRTableData(SMDB.T_R_GROUP_PERM,perms.stream().map(SMPerm::getDbCode).collect(Collectors.toList()), groupId, hbFactory);
	}

	@Override
	public void deleteUsersFromGroup(List<Integer> usersId, int groupId) throws DBException {
		deleteGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_ID, SMDB.F_USER_GROUP_ID, usersId,groupId,hbFactory);
	}

	@Override
	public void deletePermsFromGroup(List<SMPerm> perms, int groupId) throws DBException {
		deleteGeneralRTableData(SMDB.T_R_GROUP_PERM, SMDB.F_PERM_ID, SMDB.F_USER_GROUP_ID,
				perms.stream().map(SMPerm::getDbCode).collect(Collectors.toList()), groupId, hbFactory);
	}

	@Override
	public List<Integer> selectGroupsByUser(int userId) throws DBException {
		return selectGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_GROUP_ID, SMDB.F_USER_ID, userId,hbFactory);
	}

	@Override
	public List<Integer> selectPermsByGroup(int groupId) throws DBException {
		return selectGeneralRTableData(SMDB.T_R_GROUP_PERM,SMDB.F_PERM_ID, SMDB.F_USER_GROUP_ID, groupId,hbFactory);
	}

	@Override
	public List<Integer> selectUsersIdByGroup(int groupId) throws DBException {
		return selectGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_ID,SMDB.F_USER_GROUP_ID, groupId,hbFactory);
	}

	@Override
	public boolean includeUserGroup(int groupId) throws DBException {
		return includeUniqueEntityByField(UserGroup.class, SMDB.F_ID, groupId, hbFactory);
	}

	@Override
	public boolean includeUniqueUserGroupByField(String attrName, String attrVal) throws DBException {
		return includeUniqueEntityByField(UserGroup.class, attrName, attrVal, hbFactory);
	}

	@Override
	public UserGroup selectUniqueExistedUserGroupByField(String field, String val) throws DBException {
		return selectUniqueExistedEntityByField(UserGroup.class, field, val, hbFactory);
	}
	

	@Override
	public User selectUniqueUserByField(String field, Object val) throws DBException, NoSuchElement {
		return selectUniqueEntityByField(User.class, field, val, hbFactory);
	}

	@Override
	public List<UserGroup> selectAllUserGroup() throws DBException {
		return selectAllEntities(UserGroup.class, hbFactory);
	}

	@Override
	public long countUsersOfGroup(int groupId) throws DBException {
		return countByField(SMDB.T_R_USER_GROUP, SMDB.F_USER_GROUP_ID, groupId, hbFactory);
	}

	@Override
	public long countAllUsers() throws DBException {
		return countAllEntities(User.class, hbFactory);
	}

	@Override
	public List<User> selectUsersByGroup(int groupId,int limit) throws DBException {
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			String sql = String.format("SELECT * FROM %s WHERE %s in (select %s FROM %s WHERE %s=?) limit %s", SMDB.T_USER,SMDB.F_ID,SMDB.F_USER_ID,SMDB.T_R_USER_GROUP,SMDB.F_USER_GROUP_ID,limit);
			@SuppressWarnings("unchecked")
			List<User> entities = session.createSQLQuery(sql).setParameter(1, groupId).addEntity(User.class).list();
			trans.commit();
			return entities;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
}
