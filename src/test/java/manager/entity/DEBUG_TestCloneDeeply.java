package manager.entity;

import org.junit.Test;

import manager.entity.virtual.worksheet.PlanItem;

public class DEBUG_TestCloneDeeply {
	
	
	@Test
	public void testPlanItem() {
		PlanItem item =  new PlanItem();
		item.getDescendants().add(new PlanItem());
		item.getDescendants().add(new PlanItem());
		item.getDescendants().add(new PlanItem());
		
		item.getDescendants().get(0).getDescendants().add(new PlanItem());
		
		PlanItem cloned = item.clone();
		cloned.getDescendants().clear();
		assert cloned.getDescendants().size() == 0;
		assert item.getDescendants().size() == 3;
		
		cloned = item.clone();
		cloned.getDescendants().get(0).getDescendants().clear();;
		assert cloned.getDescendants().size() == 3;
		assert cloned.getDescendants().get(0).getDescendants().size() ==0;
		assert item.getDescendants().size() == 3;
		assert item.getDescendants().get(0).getDescendants().size() == 1;
	}
	
}
