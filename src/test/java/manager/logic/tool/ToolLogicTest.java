package manager.logic.tool;

import org.junit.Test;

import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;

public class ToolLogicTest {
	
	@Test
	public void testBasic() throws Exception {
		ToolLogic tL = ToolLogic.getInstance();
		tL.loadToolRecordSummary(1);
	}
	
	
}
