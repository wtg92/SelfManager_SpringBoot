import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import manager.dao.UserDAOTest;
import manager.dao.WorkDAOTest;
import manager.logic.UserLogicTest;
import manager.logic.career.NoteLogicStaticTest;
import manager.logic.career.NoteLogicTest;
import manager.logic.career.WorkContentConverterTest;
import manager.logic.career.WorkLogicTest;
import manager.logic.tool.ToolLogicTest;
import manager.system.EnumTest;
import manager.util.TimeUtilTest;

@RunWith(Suite.class)
@SuiteClasses({
	UserLogicTest.class,
	UserDAOTest.class,
	WorkDAOTest.class,
	WorkLogicTest.class,
	WorkContentConverterTest.class,
	EnumTest.class,
	NoteLogicTest.class,
	NoteLogicStaticTest.class,
	ToolLogicTest.class,
	TimeUtilTest.class
})
public class AllTests {
	
	
}


