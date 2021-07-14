package manager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.persistence.OptimisticLockException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import manager.entity.general.User;
import manager.entity.general.UserGroup;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.system.SMPerm;
import manager.util.DBUtil;
import manager.util.SecurityUtil;

public class UserDAOTest {
	
	@Before
	public void setUp() {
		DAOFactory.deleteAllTables();
	}
	
	@Test
	public void testBasic() throws Exception {
		UserDAO uDAO = DAOFactory.getUserDAO();
		User user = new User();
		user.setAccount("aa");
		user.setNickName("hh");
		user.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(user);
		int uId = uDAO.insertUser(user);
		assertTrue(user.getId() == uId);
		assertEquals(1, uId);
		uDAO.selectExistedUser(1);
		try {
			uDAO.selectUser(5);
			fail();
		}catch(NoSuchElement e) {}
		assertTrue(uDAO.includeUniqueUserByField(SMDB.F_NICK_NAME, "hh"));
		assertFalse(uDAO.includeUniqueUserByField(SMDB.F_EMAIL, "55"));
		user.setNickName("changed!");
		user = user.clone();
		uDAO.updateExistedUser(user);
		
		user.setNickName("realChanged");
		
		assertTrue(1==uDAO.selectUniqueExistedUserByField(SMDB.F_ACCOUNT, "aa").getId());;
	}
	
	@Test
	public void testUserGroupPerms() throws Exception {
		UserDAO uDAO = DAOFactory.getUserDAO();
		User u1 = new User();
		u1.setAccount("aa");
		u1.setNickName("hh");
		u1.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(u1);
		User u2 = new User();
		u2.setAccount("aa1");
		u2.setNickName("hh1");
		u2.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(u2);
		assertEquals(1, uDAO.insertUser(u1));
		assertEquals(2, uDAO.insertUser(u2));
		
		UserGroup group = new UserGroup();
		group.setName("UnBelieveable!");
		assertEquals(1, uDAO.insertUserGroup(group));
		
		uDAO.insertUsersToGroup(Arrays.asList(1,2),1);
		uDAO.deleteUsersFromGroup(Arrays.asList(1,2), 1);
	}
	
	
	@Test
	public void testConcurrent() throws Exception {
		UserDAO uDAO = DAOFactory.getUserDAO();
		User user = new User();
		user.setNickName("hh");
		user.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(user);
		int uId = uDAO.insertUser(user);
		assertTrue(user.getId() == uId);
		assertEquals(1, uId);
		
		
		SessionFactory sessionFactory = DBUtil.getHibernateSessionFactory();
		try(Session s1 = sessionFactory.openSession();
				Session s2 = sessionFactory.openSession()){
			s1.beginTransaction();
			s2.beginTransaction();
			User u1 = s1.get(User.class, 1);
			User u2 = s2.get(User.class, 1);
			u1.setNickName("changed!");
			s1.update(u1);
			s1.getTransaction().commit();
			u2.setAccount("changed seconde");
			s2.save(u2);
			s2.getTransaction().commit();
			fail();
		}catch(OptimisticLockException e) {
			//ok
		}
	}
	
	
	@Test
	public void batchInsert() throws Exception{
		UserDAO uDAO = DAOFactory.getUserDAO();
		UserGroup group = new UserGroup();
		group.setName("UnBelieveable!");
		assertEquals(1, uDAO.insertUserGroup(group));
		long t1 = System.currentTimeMillis() ;
		uDAO.insertPermsToGroup(Arrays.asList(SMPerm.values()), 1);
		long t2 = System.currentTimeMillis();
	}
	
	@Test
	public void testRelationsCRUD() throws Exception{
		UserDAO uDAO = DAOFactory.getUserDAO();
		User u1 = new User();
		u1.setAccount("aa");
		u1.setNickName("hh");
		u1.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(u1);
		User u2 = new User();
		u2.setAccount("aa1");
		u2.setNickName("hh1");
		u2.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(u2);
		assertEquals(1, uDAO.insertUser(u1));
		assertEquals(2, uDAO.insertUser(u2));
		
		UserGroup group = new UserGroup();
		group.setName("UnBelieveable!");
		UserGroup group2 = new UserGroup();
		group2.setName("UnBelieveable!");
		
		assertEquals(1, uDAO.insertUserGroup(group));
		uDAO.insertUsersToGroup(Arrays.asList(1,2),1);
		
		assertEquals(2, uDAO.insertUserGroup(group2));
		uDAO.insertUsersToGroup(Arrays.asList(1,2),1);
		
		assertEquals(2, uDAO.selectGroupsByUser(1).size());
		assertEquals(2, uDAO.selectGroupsByUser(2).size());
	}
	
}
