package manager.entity.general.career;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.DBConstants;
import manager.system.books.BookStyle;
import manager.system.career.converter.BookStyleConverter;

import java.util.Calendar;


@Entity
@Table(name = DBConstants.T_NOTE_BOOK)
@DynamicInsert
@DynamicUpdate
public class NoteBook extends SMGeneralEntity {

	private static final long serialVersionUID = 6913789086585365898L;

	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;

	/*备注*/
	@Column
	private String note;

	@Field
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
