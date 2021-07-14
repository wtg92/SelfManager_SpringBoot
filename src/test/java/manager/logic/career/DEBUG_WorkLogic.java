package manager.logic.career;

import org.junit.Test;

import manager.exception.DBException;
import manager.exception.LogicException;

public class DEBUG_WorkLogic {
	
	
	@Test
	public void test() throws LogicException, DBException {
		WorkLogic wL = WorkLogic.getInstance();
		String note = "";
		for(int i=0;i<5000;i++) {
			note+="a";
		}
		wL.saveWorkSheet(1, 1, note);
	}
}
