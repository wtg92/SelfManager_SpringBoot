package manager.data.proxy.career;


import manager.data.career.WorkSheetContent;
import manager.entity.general.career.WorkSheet;

public class WorkSheetProxy {
	
	public WorkSheet ws;
	public WorkSheetContent content;
	public String basePlanName;
	public String timezone;

	/*在不计算同步项的情况下，工作表是否完成了计划*/
	public boolean finishPlanWithoutDeptItems;
	public WorkSheetProxy(WorkSheet ws) {
		super();
		this.ws = ws;
	}
	
}
