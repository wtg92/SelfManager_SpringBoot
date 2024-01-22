package manager.entity.general;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.system.SMDB;

import java.util.Calendar;

@Entity
@Table(name = SMDB.T_USER_GROUP)
@DynamicInsert
@DynamicUpdate
public class UserGroup extends SMGeneralEntity {
	private static final long serialVersionUID = 1964950012742005158L;
	
	@Column
	private String name;

	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;


	/*======================== Auto-Genrated Code==================================*/
	
	public UserGroup() {}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
