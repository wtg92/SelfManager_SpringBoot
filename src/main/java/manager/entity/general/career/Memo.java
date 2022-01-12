package manager.entity.general.career;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;


@Entity
@Table(name = SMDB.T_MEMO)
@DynamicInsert
@DynamicUpdate
public class Memo extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;
	
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
	
}
