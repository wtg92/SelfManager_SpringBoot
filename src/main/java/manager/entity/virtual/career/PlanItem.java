package manager.entity.virtual.career;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.PlanItemType;

public class PlanItem extends SMVirtualEntity{
	
	/*次数或分钟*/
	private Integer value;
	private String note;

	private List<PlanItem> descendants = new ArrayList<PlanItem>();
	
	private String name;
	
	/*都是以次数来转化分钟的 所以该值在时间-次数的范畴内只有两种可能:
	 * 小数    表名同类别 的映射关系             0.5 表明 当下做10min的事情 相当于父Category做 5min  即  父/子=0.5
	 *
	 * 在最初设计时 次数-->分钟的转化 不可以有小数
	 * 但为什么不可以？
	 *
	 * 我检验了所有逻辑 认为可以小数
	 *  */
	private Double mappingValue;
	
	/*表明分钟/次数*/
	private PlanItemType type;
	
	private boolean fold;
	
	@Override
	public PlanItem clone(){
		try {
			PlanItem item =  (PlanItem) super.clone();
			item.descendants = item.descendants.stream().map(PlanItem::clone).collect(Collectors.toList());
			return item;
		} catch (CloneNotSupportedException e) {
			assert false : e.getMessage();
			return null;
		}
	}
	
	
	public boolean isFold() {
		return fold;
	}

	public void setFold(boolean fold) {
		this.fold = fold;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getMappingValue() {
		return mappingValue;
	}
	public void setMappingValue(Double mappingValue) {
		this.mappingValue = mappingValue;
	}
	public PlanItemType getType() {
		return type;
	}
	public void setType(PlanItemType type) {
		this.type = type;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<PlanItem> getDescendants() {
		return descendants;
	}
	public void setDescendants(List<PlanItem> descendants) {
		this.descendants = descendants;
	}
	
	
	
}
