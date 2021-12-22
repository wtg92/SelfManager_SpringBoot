package manager.data.proxy.career;


import java.util.List;

import manager.data.career.WorkSheetContent;
import manager.entity.general.career.WorkSheet;

public class WorkSheetProxy {
	
	public WorkSheet ws;
	public WorkSheetContent content;
	public String basePlanName;
	public List<String> planTags;
	public double mood;
	/*在不计算同步项的情况下，工作表是否完成了计划*/
	public boolean finishPlanWithoutDeptItems;
	public WorkSheetProxy(WorkSheet ws) {
		super();
		this.ws = ws;
	}
	
}
