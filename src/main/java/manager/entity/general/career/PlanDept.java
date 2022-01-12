package manager.entity.general.career;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;

@Entity
@Table(name = SMDB.T_PLAN_DEPT)
@DynamicInsert
@DynamicUpdate
public class PlanDept extends SMGeneralEntity {
	
	private static final long serialVersionUID = -3632012117826858197L;

	@Column
	private String content;
	
	@Column
	private Long ownerId;
	
	public PlanDept() {}
	
	public String getContent() {
		return this.content;
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

}
