import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import manager.dao.UserDAOTest;
import manager.dao.WorkDAOTest;
import manager.service.UserLogicTest;
import manager.service.books.NoteLogicStaticTest;
import manager.service.books.NoteLogicTest;
import manager.service.books.WorkContentConverterTest;
import manager.service.books.WorkLogicTest;
import manager.service.tool.ToolLogicTest;
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
	
	@Test
	public void test1() {
		System.out.println("allTests");
	}
	
	
}


