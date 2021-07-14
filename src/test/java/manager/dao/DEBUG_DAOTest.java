package manager.dao;

import java.util.Calendar;

import org.junit.Test;

import manager.dao.career.WorkDAO;
import manager.exception.DBException;
import manager.util.TimeUtil;

public class DEBUG_DAOTest {
	
	private WorkDAO wDAO = DAOFactory.getWorkDAO();
	
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
	
}
