package manager.data.proxy.career;

import manager.entity.virtual.worksheet.MemoItem;

public class MemoItemProxy {
	public MemoItem item;
	
	public String srcNoteName;
	public String srcBookName;
	
	
	public boolean isCreatedByUser() {
		return item.getSrcNoteId() == 0;
	}
	
	public MemoItemProxy(MemoItem item) {
		super();
		this.item = item;
	}
	
}
