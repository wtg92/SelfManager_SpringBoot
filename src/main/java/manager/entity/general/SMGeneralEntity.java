package manager.entity.general;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import manager.entity.SMEntity;


@MappedSuperclass
@OptimisticLocking(type=OptimisticLockType.VERSION)
public abstract class SMGeneralEntity extends SMEntity implements Cloneable{
	
	private static final long serialVersionUID = 6178314886295205584L;
	
	@Column
	private Calendar createTime;
	@Column
	private Calendar updateTime;
	
	@Column
	@Version
	private Integer hbVersion;


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


	public Integer getHbVersion() {
		return hbVersion;
	}


	public void setHbVersion(Integer hbVersion) {
		this.hbVersion = hbVersion;
	}
}
