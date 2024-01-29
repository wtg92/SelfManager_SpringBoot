package manager.entity.general.career;

import java.util.Calendar;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.data.EntityTag;
import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;
import manager.system.career.WorkSheetState;
import manager.system.career.converter.WorkSheetStateConverter;
import manager.system.converter.TagsConverter;

@Entity
@Table(name = SMDB.T_WORK_SHEET)
@DynamicInsert
@DynamicUpdate
public class WorkSheet extends SMGeneralEntity {
	
	private static final long serialVersionUID = 7632220752878325383L;
	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;

	@Column
	private Calendar date;
	
	@Column
	private String content;
	
	@Column
	private String note;
	
	@Column
	private Long planId;
	
	@Column
	private Long ownerId;
	
	/*plan的一个快照  是基于该计划*/
	@Column
	private String plan;
	
	@Column
	@Convert(converter = WorkSheetStateConverter.class)
	private WorkSheetState state;
	
	@Column
	@Convert(converter = TagsConverter.class)
	private List<EntityTag> tags;
	
	public WorkSheet() {}

	public Calendar getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}
	public Calendar getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Calendar updateTime) {
		this.updateTime = updateTime;
	}


	public List<EntityTag> getTags() {
		return tags;
	}

	public void setTags(List<EntityTag> tags) {
		this.tags = tags;
	}

	public Calendar getDate() {
		return date;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}

	public Long getPlanId() {
		return planId;
	}


	public void setPlanId(Long planId) {
		this.planId = planId;
	}


	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public Long getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}


	public String getPlan() {
		return plan;
	}


	public void setPlan(String plan) {
		this.plan = plan;
	}


	public WorkSheetState getState() {
		return state;
	}


	public void setState(WorkSheetState state) {
		this.state = state;
	}

	

	
}
