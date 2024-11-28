package manager.service.tool;

import org.junit.Test;

public class ToolLogicTest {
	
	@Test
	public void testBasic() throws Exception {
		ToolLogic tL = ToolLogic.getInstance();
		tL.loadToolRecordSummary(1);
	}
	
	
}
