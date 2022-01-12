package manager.dao.impl;

import static manager.util.DBUtil.countAllEntities;
import static manager.util.DBUtil.countByField;
import static manager.util.DBUtil.deleteGeneralRTableData;
import static manager.util.DBUtil.getHibernateSessionFactory;
import static manager.util.DBUtil.includeUniqueEntityByField;
import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.insertGeneralRTableData;
import static manager.util.DBUtil.processDBExcpetion;
import static manager.util.DBUtil.selectAllEntities;
import static manager.util.DBUtil.selectEntity;
import static manager.util.DBUtil.selectGeneralRTableData;
import static manager.util.DBUtil.selectGeneralRTableDataInInt;
import static manager.util.DBUtil.selectUniqueEntityByField;
import static manager.util.DBUtil.selectUniqueExistedEntityByField;
import static manager.util.DBUtil.updateExistedEntity;

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
import manager.system.SMPerm;

public class UserDAOImpl implements UserDAO {

	private final SessionFactory hbFactory = getHibernateSessionFactory();

	@Override
	public long insertUser(User user) throws DBException {
		return insertEntity(user, hbFactory);
	}

	@Override
	public void updateExistedUser(User user) throws DBException {
		updateExistedEntity(user, hbFactory);
	}

	@Override
	public User selectUser(long id) throws NoSuchElement, DBException {
		return selectEntity(id,User.class ,hbFactory);
	}

	@Override
	public boolean includeUniqueUserByField(String attrName, String attrVal) throws DBException {
		return includeUniqueEntityByField(User.class, attrName, attrVal, hbFactory);
	}


	@Override
	public long insertUserGroup(UserGroup group) throws DBException {
		return insertEntity(group, hbFactory);
	}

	@Override
	public void updateExistedUserGroup(UserGroup group) throws DBException {
		updateExistedEntity(group, hbFactory);
	}

	@Override
	public void insertUsersToGroup(List<Long> usersId, long groupId) throws DBException {
		insertGeneralRTableData(SMDB.T_R_USER_GROUP, usersId, groupId, hbFactory);
	}

	@Override
	public void insertPermsToGroup(List<SMPerm> perms, long groupId) throws DBException {
		insertGeneralRTableData(SMDB.T_R_GROUP_PERM,perms.stream().map(perm->(long)perm.getDbCode()).collect(Collectors.toList()), groupId, hbFactory);
	}

	@Override
	public void deleteUsersFromGroup(List<Long> usersId, long groupId) throws DBException {
		deleteGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_ID, SMDB.F_USER_GROUP_ID, usersId,groupId,hbFactory);
	}

	@Override
	public void deletePermsFromGroup(List<SMPerm> perms, long groupId) throws DBException {
		deleteGeneralRTableData(SMDB.T_R_GROUP_PERM, SMDB.F_PERM_ID, SMDB.F_USER_GROUP_ID,
				perms.stream().map(perm->(long)perm.getDbCode()).collect(Collectors.toList()), groupId, hbFactory);
	}

	@Override
	public List<Long> selectGroupsByUser(long userId) throws DBException {
		return selectGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_GROUP_ID, SMDB.F_USER_ID, userId,hbFactory);
	}

	@Override
	public List<Integer> selectPermsByGroup(long groupId) throws DBException {
		return selectGeneralRTableDataInInt(SMDB.T_R_GROUP_PERM,SMDB.F_PERM_ID, SMDB.F_USER_GROUP_ID, groupId,hbFactory);
	}

	@Override
	public List<Long> selectUsersIdByGroup(long groupId) throws DBException {
		return selectGeneralRTableData(SMDB.T_R_USER_GROUP, SMDB.F_USER_ID,SMDB.F_USER_GROUP_ID, groupId,hbFactory);
	}

	@Override
	public boolean includeUserGroup(long groupId) throws DBException {
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
	public long countUsersOfGroup(long groupId) throws DBException {
		return countByField(SMDB.T_R_USER_GROUP, SMDB.F_USER_GROUP_ID, groupId, hbFactory);
	}

	@Override
	public long countAllUsers() throws DBException {
		return countAllEntities(User.class, hbFactory);
	}

	@Override
	public List<User> selectUsersByGroup(long groupId,long limit) throws DBException {
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
