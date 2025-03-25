package manager.data.proxy.career;

import manager.data.worksheet.PlanContent;
import manager.entity.general.career.Plan;
import manager.system.career.PlanSetting;

public class PlanProxy {
	
	public Plan plan;
	public String planId;
	public PlanContent content;
	
	public boolean getAllowOthersCopy() {
		return plan.hasSetting(PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS);
	}
	
	public PlanProxy(Plan plan) {
		super();
		this.plan = plan;
	}
	
	
	
}	
