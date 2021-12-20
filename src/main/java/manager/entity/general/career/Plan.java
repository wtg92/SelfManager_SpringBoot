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
import manager.system.career.PlanSetting;
import manager.system.career.PlanSettingConverter;
import manager.system.career.PlanState;
import manager.system.career.PlanStateConverter;
import manager.system.career.TagsConverter;

@Entity
@Table(name = SMDB.T_PLAN)
@DynamicInsert
@DynamicUpdate
public class Plan extends SMGeneralEntity {
	private static final long serialVersionUID = -2783464030882407249L;
	
	@Column
	private Calendar startDate;
	
	@Column
	private Calendar endDate;
	
	@Column
	private String note;
	
	@Column
	private String content;
	
	@Column
	private String name;
	
	@Column
	private Integer ownerId;
	
	@Column
	@Convert(converter = PlanStateConverter.class)
	private PlanState state;
	
	@Column
	@Convert(converter = PlanSettingConverter.class)
	private List<PlanSetting> setting;
	
	@Column
	@Convert(converter = TagsConverter.class)
	private List<String> tags;
	
	@Column
	private Integer seqWeight;
	
	public Plan() {}
	
	public boolean hasSetting(PlanSetting target) {
		if(setting == null)
			return false;
		
		return setting.contains(target);
	}
	
	
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Integer getSeqWeight() {
		return seqWeight;
	}

	public void setSeqWeight(Integer seqWeight) {
		this.seqWeight = seqWeight;
	}

	public List<PlanSetting> getSetting() {
		return setting;
	}


	public void setSetting(List<PlanSetting> setting) {
		this.setting = setting;
	}


	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public PlanState getState() {
		return state;
	}

	public void setState(PlanState state) {
		this.state = state;
	}

}
