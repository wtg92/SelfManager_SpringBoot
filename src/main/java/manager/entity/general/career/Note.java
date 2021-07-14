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
	private Integer noteBookId;
	
	@Column
	private Boolean withTodos;
	
	/**
	  * 用来调整顺序
	 */
	@Column
	private Integer prevNoteId;
	
	
	@Column
	private Boolean important;

	@Override
	public String toString() {
		return String.format(" {'id':%s,'prevNoteId':%s,'import':%s}",
				getId(),prevNoteId,important);
	}


	public boolean isRoot() {
		return prevNoteId == 0 ;
	}
	
	public Boolean getImportant() {
		return important;
	}


	public void setImportant(Boolean important) {
		this.important = important;
	}


	public Integer getPrevNoteId() {
		return prevNoteId;
	}

	public void setPrevNoteId(Integer prevNoteId) {
		this.prevNoteId = prevNoteId;
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

	public Integer getNoteBookId() {
		return noteBookId;
	}

	public void setNoteBookId(Integer noteBookId) {
		this.noteBookId = noteBookId;
	}

	public Boolean getWithTodos() {
		return withTodos;
	}

	public void setWithTodos(Boolean withTodos) {
		this.withTodos = withTodos;
	}

}
