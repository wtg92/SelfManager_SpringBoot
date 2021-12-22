package manager.entity.general.career;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;
import manager.system.career.TagsConverter;
import manager.system.career.WorkSheetState;
import manager.system.career.WorkSheetStateConverter;

@Entity
@Table(name = SMDB.T_WORK_SHEET)
@DynamicInsert
@DynamicUpdate
public class WorkSheet extends SMGeneralEntity {
	
	private static final long serialVersionUID = 7632220752878325383L;
	
	@Column
	private Calendar date;
	
	@Column
	private String content;
	
	@Column
	private String note;
	
	@Column
	private Integer planId;
	
	@Column
	private Integer ownerId;
	
	/*plan的一个快照  是基于该计划*/
	@Column
	private String plan;
	
	@Column
	@Convert(converter = WorkSheetStateConverter.class)
	private WorkSheetState state;
	
	@Column
	@Convert(converter = TagsConverter.class)
	private List<String> tags;
	
	public WorkSheet() {}

	public List<String> getTags() {
		return tags;
	}


	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Calendar getDate() {
		return date;
	}
	
	public void setDate(Calendar date) {
		this.date = date;
	}

	public Integer getPlanId() {
		return planId;
	}


	public void setPlanId(Integer planId) {
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


	public Integer getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(Integer ownerId) {
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
