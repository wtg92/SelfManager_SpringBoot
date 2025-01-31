package manager.entity.general.career;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import manager.annotation.SolrEntity;
import manager.annotation.SolrField;

import manager.entity.general.SMGeneralEntity;
import manager.system.DBConstants;

import java.util.Calendar;


@Entity
@Table(name = DBConstants.T_NOTE)
@SolrEntity(name = DBConstants.T_NOTE)
public class Note extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;

	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;

	@SolrField
	@Column
	private String content;

	@SolrField
	@Column
	private String name;
	
	@Column
	private Long noteBookId;
	
	@Column
	private Boolean withTodos;
	
	@Column
	private Boolean important;
	
	@Column
	private Boolean hidden;


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


	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Boolean getImportant() {
		return important;
	}

	public void setImportant(Boolean important) {
		this.important = important;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNoteBookId() {
		return noteBookId;
	}

	public void setNoteBookId(Long noteBookId) {
		this.noteBookId = noteBookId;
	}

	public Boolean getWithTodos() {
		return withTodos;
	}

	public void setWithTodos(Boolean withTodos) {
		this.withTodos = withTodos;
	}

}
