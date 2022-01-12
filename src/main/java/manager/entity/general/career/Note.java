package manager.entity.general.career;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;


@Entity
@Table(name = SMDB.T_NOTE)
@DynamicInsert
@DynamicUpdate
public class Note extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;
	
	@Column
	private String content;
	
	@Column
	private String name;
	
	@Column
	private Long noteBookId;
	
	@Column
	private Boolean withTodos;
	
	@Column
	private Boolean important;

	
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
