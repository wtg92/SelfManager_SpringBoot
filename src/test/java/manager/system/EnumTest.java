package manager.system;

import org.junit.Test;

import manager.system.career.BookStyle;
import manager.system.career.CareerLogAction;
import manager.system.career.NoteLabel;
import manager.system.career.PlanItemType;
import manager.system.career.PlanState;
import manager.system.career.WorkItemType;
import manager.system.career.WorkSheetState;
import manager.system.tool.Tool;

public class EnumTest {
	
	
	@Test
	public void noDupTest() throws Exception {
		TestUtil.checkEnumNoDup(SelfXPerms.class,"dbCode");
		TestUtil.checkEnumNoDup(SMOP.class,"name");
		
		TestUtil.checkEnumNoDup(CareerLogAction.class,"dbCode");
		TestUtil.checkEnumNoDup(PlanItemType.class,"dbCode");
		TestUtil.checkEnumNoDup(PlanState.class,"dbCode");
		TestUtil.checkEnumNoDup(WorkItemType.class,"dbCode");
		TestUtil.checkEnumNoDup(WorkSheetState.class,"dbCode");
		
		TestUtil.checkEnumNoDup(Gender.class,"dbCode");
		TestUtil.checkEnumNoDup(VerifyUserMethod.class,"dbCode");
		TestUtil.checkEnumNoDup(UserUniqueField.class,"dbCode");
		
		TestUtil.checkEnumNoDup(BookStyle.class,"dbCode");
		TestUtil.checkEnumNoDup(NoteLabel.class,"name");
		
		TestUtil.checkEnumNoDup(Tool.class,"dbCode");
		
	}
	
	
	@Test
	public void testPermSetting() {
		SelfXPerms.getPermsByGroup();
	}
	
}
