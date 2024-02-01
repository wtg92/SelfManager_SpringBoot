package manager;

import manager.dao.UserDAO;
import manager.logic.UserLogic;
import manager.system.SMPerm;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import manager.SelfManagerSpringbootApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfManagerSpringbootApplication.class)
class ApplicationTests {

	@Autowired
	UserDAO userDAO;

	@Autowired
	UserLogic ul;

	@Test
	void contextLoads() {
		System.out.println("say Hello");
	}

	@Test
	public void testDB(){
		userDAO.selectUser(1);
	}

	@Test
	public  void testul(){
		ul.checkPerm(1, SMPerm.SEE_SELF_PLANS);
	}
}
