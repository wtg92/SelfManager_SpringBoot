package manager.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import manager.entity.general.SystemMapping;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.dao.UserDAO;
import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.system.SelfXPerms;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import static manager.util.DBUtil.*;


@Repository
public class UserDAOImpl implements UserDAO {

	@Resource
	private SessionFactory sessionFactory;

	@Override
	public long insertUser(User user) throws DBException {
		return insertEntity(user, sessionFactory);
	}

	@Override
	public void updateExistedUser(User user) throws DBException {
		updateExistedEntity(user, sessionFactory);
	}

	@Override
	public User selectUser(long id) throws NoSuchElement, DBException {
		return selectEntity(id,User.class , sessionFactory);
	}

	@Override
	public boolean includeUniqueUserByField(String attrName, String attrVal) throws DBException {
		return includeUniqueEntityByField(User.class, attrName, attrVal, sessionFactory);
	}


	@Override
	public long insertUserGroup(UserGroup group) throws DBException {
		return insertEntity(group, sessionFactory);
	}

	@Override
	public void updateExistedUserGroup(UserGroup group) throws DBException {
		updateExistedEntity(group, sessionFactory);
	}

	@Override
	public void insertUsersToGroup(List<Long> usersId, long groupId) throws DBException {
		insertGeneralRTableData(DBConstants.T_R_USER_GROUP, usersId, groupId, sessionFactory);
	}

	@Override
	public void insertPermsToGroup(List<SelfXPerms> perms, long groupId) throws DBException {
		insertGeneralRTableData(DBConstants.T_R_GROUP_PERM,perms.stream().map(perm->(long)perm.getDbCode()).collect(Collectors.toList()), groupId, sessionFactory);
	}

	@Override
	public void deleteUsersFromGroup(List<Long> usersId, long groupId) throws DBException {
		deleteGeneralRTableData(DBConstants.T_R_USER_GROUP, DBConstants.F_USER_ID, DBConstants.F_USER_GROUP_ID, usersId,groupId, sessionFactory);
	}

	@Override
	public void deletePermsFromGroup(List<SelfXPerms> perms, long groupId) throws DBException {
		deleteGeneralRTableData(DBConstants.T_R_GROUP_PERM, DBConstants.F_PERM_ID, DBConstants.F_USER_GROUP_ID,
				perms.stream().map(perm->(long)perm.getDbCode()).collect(Collectors.toList()), groupId, sessionFactory);
	}

	@Override
	public List<User> selectUsersByIds(List<Long> userIds) {
		return selectEntitiesByManyField(User.class, DBConstants.F_ID,userIds, String::valueOf, sessionFactory);
	}

	@Override
	public boolean hasPerm(long userId, SelfXPerms perm) {
		return sessionFactory.fromStatelessSession((session)->
			 session.createNativeQuery("""
					select count(*)>0 from scientific_manager.user u1
					left join
					r_user_group r1 on u1.id = r1.user_id
					left join
					r_group_perm r2 on r1.user_group_id = r2.user_group_id
					where u1.id = ? and r2.perm_id = ?
				""",Boolean.class)
					.setParameter(1,userId)
					.setParameter(2,perm.getDbCode())
					.uniqueResult()
		);
	}

	@Override
	public List<Integer> selectPermsByUser(long userId) {
		return sessionFactory.fromStatelessSession((session)->
				session.createNativeQuery("""
					select perm_id from scientific_manager.user u1
					left join
					r_user_group r1 on u1.id = r1.user_id
					left join
					r_group_perm r2 on r1.user_group_id = r2.user_group_id
					where u1.id = ?
				""",Integer.class)
						.setParameter(1,userId)
						.list()
		);
	}

	@Override
	public long insertSystemMapping(SystemMapping mapping) {
		return insertEntity(mapping, sessionFactory);
	}

	@Override
	public List<Long> selectGroupsByUser(long userId) throws DBException {
		return selectGeneralRTableData(DBConstants.T_R_USER_GROUP, DBConstants.F_USER_GROUP_ID, DBConstants.F_USER_ID, userId, sessionFactory);
	}

	@Override
	public List<Integer> selectPermsByGroup(long groupId) throws DBException {
		return selectGeneralRTableDataInInt(DBConstants.T_R_GROUP_PERM, DBConstants.F_PERM_ID, DBConstants.F_USER_GROUP_ID, groupId, sessionFactory);
	}

	@Override
	public List<Long> selectUsersIdByGroup(long groupId) throws DBException {
		return selectGeneralRTableData(DBConstants.T_R_USER_GROUP, DBConstants.F_USER_ID, DBConstants.F_USER_GROUP_ID, groupId, sessionFactory);
	}

	@Override
	public boolean includeUserGroup(long groupId) throws DBException {
		return includeUniqueEntityByField(UserGroup.class, DBConstants.F_ID, groupId, sessionFactory);
	}

	@Override
	public boolean includeUniqueUserGroupByField(String attrName, String attrVal) throws DBException {
		return includeUniqueEntityByField(UserGroup.class, attrName, attrVal, sessionFactory);
	}

	@Override
	public UserGroup selectUniqueExistedUserGroupByField(String field, String val) throws DBException {
		return selectUniqueExistedEntityByField(UserGroup.class, field, val, sessionFactory);
	}
	

	@Override
	public User selectUniqueUserByField(String field, Object val) throws DBException, NoSuchElement {
		return selectUniqueEntityByField(User.class, field, val, sessionFactory);
	}

	@Override
	public List<UserGroup> selectAllUserGroup() throws DBException {
		return selectAllEntities(UserGroup.class, sessionFactory);
	}

	@Override
	public long countUsersOfGroup(long groupId) throws DBException {
		return countByField(DBConstants.T_R_USER_GROUP, DBConstants.F_USER_GROUP_ID, groupId, sessionFactory);
	}

	@Override
	public long countAllUsers() throws DBException {
		return countAllEntities(User.class, sessionFactory);
	}

	@Override
	public List<User> selectUsersByGroup(long groupId,long limit) throws DBException {
		Session session = null;
		Transaction trans = null;
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			String sql = String.format("SELECT * FROM %s WHERE %s in (select %s FROM %s WHERE %s=?) limit %s", DBConstants.T_USER, DBConstants.F_ID, DBConstants.F_USER_ID, DBConstants.T_R_USER_GROUP, DBConstants.F_USER_GROUP_ID,limit);
			@SuppressWarnings("unchecked")
			List<User> entities = session.createQuery(sql,User.class)
					.setParameter(1, groupId).list();
			trans.commit();
			return entities;
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}
}
