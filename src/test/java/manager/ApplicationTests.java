package manager;

import manager.dao.UserDAO;
import manager.dao.career.WorkDAO;
import manager.logic.UserLogic;
import manager.logic.career.WorkLogic;
import manager.system.SMPerm;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import manager.SelfManagerSpringbootApplication;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SelfManagerSpringbootApplication.class)
class ApplicationTests {

	@Autowired
	UserDAO userDAO;

	@Autowired
	UserLogic ul;

	@Autowired
	WorkDAO workDAO;

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

	@Resource
	WorkLogic wl;

	@Test
	public void test2(){
		workDAO.includeWorkSheetByPlanId(1);
	}
}
