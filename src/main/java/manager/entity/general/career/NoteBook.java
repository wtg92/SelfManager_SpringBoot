package manager.entity.general.career;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;
import manager.system.career.BookStyle;
import manager.system.career.converter.BookStyleConverter;


@Entity
@Table(name = SMDB.T_NOTE_BOOK)
@DynamicInsert
@DynamicUpdate
public class NoteBook extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;
	
	/*备注*/
	@Column
	private String note;
	
	@Column
	private String name;
	
	@Column
	private Long ownerId;
	
	@Column
	private Boolean closed;

	@Column
	@Convert(converter = BookStyleConverter.class)
	private BookStyle style;
	
	@Column
	private Integer seqWeight;
	
	@Column
	private String notesSeq;
	
	
	
	
	public String getNotesSeq() {
		return notesSeq;
	}

	public void setNotesSeq(String notesSeq) {
		this.notesSeq = notesSeq;
	}

	public BookStyle getStyle() {
		return style;
	}

	public void setStyle(BookStyle style) {
		this.style = style;
	}

	public Integer getSeqWeight() {
		return seqWeight;
	}

	public void setSeqWeight(Integer seqWeight) {
		this.seqWeight = seqWeight;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}
	
}
