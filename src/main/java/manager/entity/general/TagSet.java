package manager.entity.general;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.system.SMDB;
import manager.system.TagType;
import manager.system.converter.TagTypeConverter;

@Entity
@Table(name = SMDB.T_TAG_SET)
@DynamicInsert
@DynamicUpdate
public class TagSet extends SMGeneralEntity {

	private static final long serialVersionUID = 5913737394951577315L;

	@Column
	private Integer userId;
	
	@Column
	private String tags;
	
	@Column
	@Convert(converter = TagTypeConverter.class)
	private TagType type;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}
	
}
