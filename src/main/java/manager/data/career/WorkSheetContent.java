package manager.data.career;

import java.util.ArrayList;
import java.util.List;

import manager.data.proxy.career.CareerLogProxy;
import manager.data.proxy.career.PlanItemProxy;
import manager.data.proxy.career.WorkItemProxy;
import manager.entity.virtual.career.PlanItem;

public class WorkSheetContent {
	
	public List<PlanItemProxy> planItems = new ArrayList<>();
	
	public List<WorkItemProxy> workItems = new ArrayList<>();
	
	public List<CareerLogProxy> logs = new ArrayList<>();
	
	
	public static class PlanItemNode{
		
		public PlanItemProxy item;
		public PlanItemNode prev;
		/*映射关系*/
		
		public boolean isRoot() {
			return prev == null;
		}

		public PlanItemNode(PlanItemProxy item) {
			super();
			this.item = item;
		}

	}
	
}
