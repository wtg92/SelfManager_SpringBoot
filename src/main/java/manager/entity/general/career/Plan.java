package manager.entity.general.career;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

import manager.util.TimeUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.data.EntityTag;
import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;
import manager.system.career.PlanSetting;
import manager.system.career.PlanState;
import manager.system.career.converter.PlanSettingConverter;
import manager.system.career.converter.PlanStateConverter;
import manager.system.converter.TagsConverter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = SMDB.T_PLAN)
@DynamicInsert
@DynamicUpdate
public class Plan extends SMGeneralEntity {

	@Column
	private String timezone;

	@Column
	private Long startUtc;

	@Column
	private Long endUtc;
	
	@Column
	private String note;
	
	@Column
	private String content;
	
	@Column
	private String name;
	
	@Column
	private Long ownerId;

	@Column
	@Convert(converter = PlanStateConverter.class)
	private PlanState state;
	
	@Column
	@Convert(converter = PlanSettingConverter.class)
	private List<PlanSetting> setting;
	
	@Column
	@Convert(converter = TagsConverter.class)
	private List<EntityTag> tags;
	
	@Column
	private Integer seqWeight;


	@Column
	@Deprecated
	private Calendar endDate = TimeUtil.getBlank();

	@Column
	@Deprecated
	private Calendar createTime = TimeUtil.getBlank();

	@Column
	@Deprecated
	private Calendar updateTime = TimeUtil.getBlank();

	@Column
	@Deprecated
	private Calendar startDate = TimeUtil.getBlank();

	@Override
	public Plan clone(){
		try {
			return (Plan) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Plan() {}
	
	public boolean hasSetting(PlanSetting target) {
		if(setting == null)
			return false;
		
		return setting.contains(target);
	}

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

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

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

	public List<EntityTag> getTags() {
		return tags;
	}

	public void setTags(List<EntityTag> tags) {
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

	@Deprecated
	public Calendar getStartDate() {
		return startDate;
	}

	@Deprecated
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Deprecated
	public Calendar getEndDate() {
		return endDate;
	}

	@Deprecated
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

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public PlanState getState() {
		return state;
	}

	public void setState(PlanState state) {
		this.state = state;
	}

}
