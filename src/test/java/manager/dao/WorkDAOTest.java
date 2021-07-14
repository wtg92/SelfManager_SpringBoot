package manager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.dao.career.WorkDAO;
import manager.entity.general.User;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;
import manager.util.SecurityUtil;
import manager.util.TimeUtil;

public class WorkDAOTest {
	
	@Before
	public void setUp() {
		TestUtil.initEnvironment();
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
		assertEquals(1, uId);
		
		WorkDAO cDAO  = DAOFactory.getWorkDAO();
		Plan plan = new Plan();
		plan.setOwnerId(1);
		plan.setName("计划？");
		plan.setContent("xxx");
		plan.setStartDate(TimeUtil.getCurrentTime());
		plan.setEndDate(TimeUtil.getCurrentTime());
		plan.setState(PlanState.ACTIVE);
		
		assertEquals(1, cDAO.insertPlan(plan));
		
		Plan planFromDB =  cDAO.selectExistedPlan(1);
	
		assertEquals(1,(int) planFromDB.getOwnerId());
		
		WorkSheet sheet = new WorkSheet();
		sheet.setContent("xx");
		sheet.setOwnerId(1);
		sheet.setPlanId(1);
		sheet.setDate(TimeUtil.getCurrentDate());
		sheet.setPlan("ss");
		sheet.setState(WorkSheetState.ACTIVE);
		assertEquals(1, cDAO.insertWorkSheet(sheet));
		
		WorkSheet sheetFromDB =  cDAO.selectExistedWorkSheet(1);
	
		assertEquals(1,(int) sheetFromDB.getPlanId());
		assertEquals(1,(int) sheetFromDB.getOwnerId());
	}
	
	@Test
	public void testSetting() throws Exception{
		UserDAO uDAO = DAOFactory.getUserDAO();
		User user = new User();
		user.setAccount("aa");
		user.setNickName("hh");
		user.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(user);
		int uId = uDAO.insertUser(user);
		assertEquals(1, uId);
		
		WorkDAO cDAO  = DAOFactory.getWorkDAO();
		Plan plan = new Plan();
		plan.setOwnerId(1);
		plan.setName("计划？");
		plan.setContent("xxx");
		plan.setStartDate(TimeUtil.getCurrentTime());
		plan.setEndDate(TimeUtil.getCurrentTime());
		plan.setState(PlanState.ACTIVE);
		
		/*关键的*/
		plan.setSetting(Arrays.asList(PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS));
		
		assertEquals(1, cDAO.insertPlan(plan));

		plan = cDAO.selectExistedPlan(1);
		
		assertTrue(plan.getSetting().contains(PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS));
		
	}
	
	
	@Test
	public void testSelectWorkSheetInfoRecentlyByOwner() throws DBException, LogicException {
		
		UserDAO uDAO = DAOFactory.getUserDAO();
		User user = new User();
		user.setAccount("aa");
		user.setNickName("hh");
		user.setPassword("hhhhhhhhhhhhhhhhhhhhhhhhhh");
		SecurityUtil.encodeUserPwd(user);
		int uId = uDAO.insertUser(user);
		assertEquals(1, uId);
		
		
		WorkDAO dao = DAOFactory.getWorkDAO();
		Calendar today = TimeUtil.getCurrentDate();
		Plan plan = new Plan();
		plan.setOwnerId(1);
		plan.setName("计划？");
		plan.setContent("xxx");
		plan.setStartDate(TimeUtil.getCurrentTime());
		plan.setEndDate(TimeUtil.getCurrentTime());
		plan.setState(PlanState.ACTIVE);
		
		assertEquals(1, dao.insertPlan(plan));
		
		for(int i=0;i<10;i++) {
			WorkSheet ws = new WorkSheet();
			ws.setContent("xx");
			ws.setOwnerId(1);
			ws.setPlanId(1);
			ws.setDate(today);
			ws.setPlan("ss");
			ws.setState(WorkSheetState.ACTIVE);
			assertEquals(i+1,dao.insertWorkSheet(ws));
			today.add(Calendar.DATE, -1);
		}
		
		
		List<WorkSheet> rlt = dao.selectWorkSheetInfoRecentlyByOwner(1, 0, 10);
		assertEquals(10, rlt.size());
		for(int i=0;i<rlt.size();i++) {
			assertEquals(i+1, (int)rlt.get(i).getId());
		}
		
		assertEquals(5, dao.selectWorkSheetInfoRecentlyByOwner(1, 0, 5).size());;
		assertEquals(0, dao.selectWorkSheetInfoRecentlyByOwner(1, 1, 10).size());
		
		today = TimeUtil.getCurrentDate();
		for(int i=0;i<10;i++) {
			assertEquals(1,dao.countWorkSheetByDate(today));
			today.add(Calendar.DATE, -1);
		}
		
		
	}
	
}
