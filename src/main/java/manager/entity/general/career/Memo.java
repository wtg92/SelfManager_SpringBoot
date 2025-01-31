package manager.entity.general.career;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.DBConstants;

import java.util.Calendar;


@Entity
@Table(name = DBConstants.T_MEMO)
@DynamicInsert
@DynamicUpdate
public class Memo extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;

	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;

	@Column
	private String content;
	
	@Column
	private Long ownerId;

	@Column
	private String note;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

}
