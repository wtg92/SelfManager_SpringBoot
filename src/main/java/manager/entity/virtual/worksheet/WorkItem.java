package manager.entity.virtual.worksheet;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.WorkItemType;

public class WorkItem extends SMVirtualEntity{
	
	private Integer planItemId;
	/**
	 * 代表分钟/次数
	 */
	private Double value;
	
	private String note;
	/*0-5  1-5:level 0 means no comment*/
	private Integer mood;
	
	/*前台到底是选择加号还是减号*/
	private Boolean forAdd; 

	/**
	 * 来个默认值 让toString 不至于空指针
	 */
	private Long startUtc = (long)0;
	private Long endUtc = (long)0;

	private WorkItemType type;


	public Long getStartUtc() {
		return startUtc;
	}

	public void setStartUtc(Long startUtc) {
		this.startUtc = startUtc;
	}

	public Long getEndUtc() {
		return endUtc;
	}

	public void setEndUtc(Long endUtc) {
		this.endUtc = endUtc;
	}

	public WorkItemType getType() {
		return type;
	}
	public void setType(WorkItemType type) {
		this.type = type;
	}

	public Integer getPlanItemId() {
		return planItemId;
	}
	public void setPlanItemId(Integer planItemId) {
		this.planItemId = planItemId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Integer getMood() {
		return mood;
	}
	public void setMood(Integer mood) {
		this.mood = mood;
	}
	public Boolean isForAdd() {
		return forAdd;
	}
	public void setForAdd(Boolean forAdd) {
		this.forAdd = forAdd;
	}
	
}
