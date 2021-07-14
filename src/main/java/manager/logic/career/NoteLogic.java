package manager.logic.career;


import java.util.List;

import manager.data.career.BookContent;
import manager.data.proxy.career.MemoProxy;
import manager.data.proxy.career.NoteBookProxy;
import manager.data.proxy.career.NoteProxy;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.logic.UserLogic;
import manager.system.career.BookStyle;
import manager.system.career.NoteLabel;


public abstract class NoteLogic{
	
	
	private static NoteLogic instance = null;
	
	UserLogic uL = UserLogic.getInstance();
	
	public abstract int createNoteBook(int creatorId,String name,String note,BookStyle style) throws DBException,LogicException;
	public abstract int createNote(int creatorId,int noteBookId,String name) throws DBException,LogicException;
	
	public abstract void saveNoteBook(int saverId,int noteBookId,String name,String note, BookStyle style, int seqWeight) throws DBException,LogicException;
	/**
	 * 由于标签的计算是前台的，因此 withTodos 也应该由前台告诉我，同样的正则 不应该写在两处地方
	 */
	public abstract void saveNote(int saverId,int noteId,String name,String content,boolean withTodos) throws DBException,LogicException;
	public abstract void saveMemo(int saverId,String note)throws DBException,LogicException;
	/**
	 * @return bookId 方便Servlet
	 */
	public abstract int saveNoteImportant(int saverId,int noteId,boolean important) throws DBException,LogicException;
	public abstract void saveNotesSeq (int saverId,int targetNoteId,int prevNoteIdForTarget) throws DBException,LogicException;
	
	
	public abstract List<NoteBookProxy> loadBooks(int loginerId) throws DBException,LogicException;
	public abstract NoteBookProxy loadBook(int loginerId,int bookId) throws DBException,LogicException;
	/**
	 * Note: id name withTodos prevId important
	 */
	public abstract BookContent loadBookContent(int loginerId,int noteBookId) throws DBException, LogicException;
	public abstract NoteProxy loadNote(int loginerId,int noteId) throws DBException, LogicException;
	public abstract MemoProxy loadMemo(int loginerId) throws DBException, LogicException;
	
	
	/**
	  * 只允许noteBook Closed时，才允许删除，删除时，会将Note一起删掉
	 */
	public abstract void deleteNoteBook(int deletorId,int id) throws DBException, LogicException;
	
	/**
	 * 这个方法还要维护prevNoteId的连贯性
	 */
	public abstract void deleteNote(int deletorId,int id) throws DBException, LogicException;
	
	public abstract void addItemToMemo(int adderId,String content,NoteLabel label,String note,int srcNoteId) throws LogicException, DBException;
	
	public abstract void removeItemFromMemo(int removerId,int itemId)  throws LogicException, DBException;
	
	public abstract void saveMemoItem(int updaterId,int itemId,String content,NoteLabel label,String note) throws LogicException, DBException;
	
	public abstract void saveMemoItemsSeq(int updaterId,List<Integer> seqIds)  throws LogicException, DBException;
	
	public abstract void saveMemoItemLabel(int updaterId,int itemId,NoteLabel label) throws LogicException, DBException;
	
	/**
	  *  假如NoteBook里 无Note且无备注 则直接删除
	 * 假如有Note 放到closed里 
	 */
	public abstract void closeNoteBook(int closerId,int bookId) throws DBException, LogicException;
	public abstract void openNoteBook(int closerId,int bookId) throws DBException, LogicException;
	
/*=================================================NOT ABSTRACT ==============================================================*/	
	
	
	public static synchronized NoteLogic getInstance() {
		if(instance == null) {
			instance = new NoteLogic_Real();
		}
		return instance;
	}
	

}
