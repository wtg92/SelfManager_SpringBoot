package manager.data.proxy.career;


import manager.data.worksheet.WorkSheetContent;
import manager.entity.general.career.WorkSheet;

public class WorkSheetProxy {
	
	public WorkSheet ws;
	public WorkSheetContent content;
	public String basePlanName;
	/*在不计算同步项的情况下，工作表是否完成了计划*/
	public boolean finishPlanWithoutDeptItems;
	public WorkSheetProxy(WorkSheet ws) {
		super();
		this.ws =  ws;
	}
	
}
