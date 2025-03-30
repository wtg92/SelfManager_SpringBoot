package manager.data.proxy.career;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import manager.entity.virtual.worksheet.PlanItem;

/*！当改数据结构时，别忘了检查clone也要深克隆*/
public class PlanItemProxy implements Cloneable{
	public PlanItem item;
	
	/*对于该节点，所有这个PlanItem的workItem累积起来的值（不包括孩子）*/
	public int sumValForWorkItems = 0;
	
	/*对于该节点来说，计划剩余的所有时间如果都交由该节点做，还差多少*/
	public double remainingValForCur = 0.0;
	
	public List<PlanItemProxy> descendants = new ArrayList<PlanItemProxy>();
	
	public PlanItemProxy(PlanItem item) {
		super();
		this.item = item.clone();
		this.descendants = item.getDescendants().stream().map(PlanItemProxy::new).collect(Collectors.toList());
		this.item.getDescendants().clear();
	}
	
	@Override
	public PlanItemProxy clone(){
		try {
			PlanItemProxy rlt = (PlanItemProxy) super.clone();
			rlt.item = this.item.clone();
			rlt.descendants = this.descendants.stream().map(PlanItemProxy::clone).collect(Collectors.toList());
			return rlt;
		} catch (CloneNotSupportedException e) {
			assert false ;
			return null;
		}
	}
	
}
