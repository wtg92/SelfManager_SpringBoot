package manager.dao;

import java.util.Calendar;

import org.junit.Test;

import manager.dao.career.WorkDAO;
import manager.entity.general.User;
import manager.exception.DBException;
import manager.system.Gender;
import manager.util.SecurityUtil;
import manager.util.TimeUtil;

public class DEBUG_DAOTest {
	
	private WorkDAO wDAO = DAOFactory.getWorkDAO();
	private UserDAO uDAO = DAOFactory.getUserDAO();
	@Test
	public void testSelectWorkSheetsByOwnerAndDateScope() throws DBException {
		
		Calendar startDate = TimeUtil.getCurrentDate();
		startDate.set(Calendar.YEAR, 2020);
		startDate.set(Calendar.MONTH, 10);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = TimeUtil.getCurrentDate();
		endDate.set(Calendar.YEAR,2020);
		endDate.set(Calendar.MONTH, 10);
		endDate.set(Calendar.DAY_OF_MONTH,30);
		
		System.out.println(wDAO.selectWorkSheetsByOwnerAndDateScope(1, startDate, endDate).size());
	}
	
	
	@Test
	public void addUser() throws Exception{
		User user = new User();
		user.setAccount("sample");
		user.setNickName("sample");
		user.setPassword("123456789");
		SecurityUtil.encodeUserPwd(user);
		user.setGender(Gender.OTHERS);
		uDAO.insertUser(user);
	}


	@Test
	public void getPWD() throws Exception{
		System.out.println(SecurityUtil.decodeInfo("5EE3E9B630BD3222"));
	}
	
}
