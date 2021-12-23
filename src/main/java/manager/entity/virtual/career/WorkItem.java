package manager.entity.virtual.career;

import java.util.Calendar;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.WorkItemType;
import manager.util.TimeUtil;

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
	
	private Calendar startTime;
	private Calendar endTime;
	
	private WorkItemType type;
	
	
	
	@Override
	public String toString() {
		return String.format(
				" {id:%d,planItemId:'%s', value:'%s', note:'%s', mood:'%s', forAdd:'%s', startTime:'%s', endTime:'%s', type:'%s'}",
				getId(),planItemId, value, note, mood, forAdd, TimeUtil.parseTime(startTime), TimeUtil.parseTime(endTime), type);
	}
	public WorkItemType getType() {
		return type;
	}
	public void setType(WorkItemType type) {
		this.type = type;
	}
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	public Calendar getEndTime() {
		return endTime;
	}
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
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
