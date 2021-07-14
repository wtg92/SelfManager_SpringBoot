package manager.entity.virtual.career;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.NoteLabel;

public class MemoItem extends SMVirtualEntity{
	
	private String content;
	private String note;
	private NoteLabel label;
	
	private Integer srcNoteId;
	/*当Note被删除时使用*/
	private String srcNoteName;
	
	private Integer srcBookId;
	private String srcBookName;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public NoteLabel getLabel() {
		return label;
	}
	public void setLabel(NoteLabel label) {
		this.label = label;
	}
	public Integer getSrcNoteId() {
		return srcNoteId;
	}
	public void setSrcNoteId(Integer srcNoteId) {
		this.srcNoteId = srcNoteId;
	}
	public String getSrcNoteName() {
		return srcNoteName;
	}
	public void setSrcNoteName(String srcNoteName) {
		this.srcNoteName = srcNoteName;
	}

	public Integer getSrcBookId() {
		return srcBookId;
	}

	public void setSrcBookId(Integer srcBookId) {
		this.srcBookId = srcBookId;
	}

	public String getSrcBookName() {
		return srcBookName;
	}

	public void setSrcBookName(String srcBookName) {
		this.srcBookName = srcBookName;
	}
	
}
