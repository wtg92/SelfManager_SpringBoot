package manager.data.proxy.career;

import manager.entity.virtual.worksheet.WorkItem;

public class WorkItemProxy {
	public WorkItem item;

	/*次数有可能出现小数 / 分钟不会
	 * */
	public double remainingValAtStart ;
	
	public WorkItemProxy(WorkItem workItem) {
		super();
		this.item = workItem;
	}
	
}
