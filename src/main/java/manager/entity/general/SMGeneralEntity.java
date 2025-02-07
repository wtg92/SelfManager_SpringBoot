package manager.entity.general;



import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import manager.annotation.SolrField;
import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import manager.entity.SMEntity;


@MappedSuperclass
@OptimisticLocking(type=OptimisticLockType.VERSION)
public abstract class SMGeneralEntity extends SMEntity implements Cloneable{
	
	private static final long serialVersionUID = 6178314886295205584L;

	@Field
	@Column
	private Long createUtc;

	@Column
	private Long updateUtc;


	@Column(name="hb_version")
	@Version
	/**
	 * 并发问题是任何数据库都会遇到的
	 * 该字段注解
	 */
	private Integer version;


	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getCreateUtc() {
		return createUtc;
	}

	public void setCreateUtc(Long createUtc) {
		this.createUtc = createUtc;
	}

	public Long getUpdateUtc() {
		return updateUtc;
	}

	public void setUpdateUtc(Long updateUtc) {
		this.updateUtc = updateUtc;
	}
}
