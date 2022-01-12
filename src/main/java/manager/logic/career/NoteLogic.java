package manager.logic.career;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import manager.data.career.BookContent;
import manager.data.proxy.career.MemoProxy;
import manager.data.proxy.career.NoteBookProxy;
import manager.data.proxy.career.NoteProxy;
import manager.entity.general.career.Note;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.UserLogic;
import manager.logic.career.impl.NoteLogicImpl;
import manager.system.SM;
import manager.system.career.BookStyle;
import manager.system.career.NoteLabel;


public abstract class NoteLogic{
	
	
	private static NoteLogic instance = null;
	
	protected UserLogic uL = UserLogic.getInstance();
	
	public abstract long createNoteBook(long creatorId,String name,String note,BookStyle style) throws DBException,LogicException;
	public abstract long createNote(long creatorId,long noteBookId,String name) throws DBException,LogicException;
	
	public abstract void saveNoteBook(long saverId,long noteBookId,String name,String note, BookStyle style, int seqWeight) throws DBException,LogicException;
	/**
	 * 由于标签的计算是前台的，因此 withTodos 也应该由前台告诉我，同样的正则 不应该写在两处地方
	 */
	public abstract void saveNote(long saverId,long noteId,String name,String content,boolean withTodos) throws DBException,LogicException;
	public abstract void saveMemo(long saverId,String note)throws DBException,LogicException;
	/**
	 * @return bookId 方便Servlet
	 */
	public abstract long saveNoteImportant(long saverId,long noteId,boolean important) throws DBException,LogicException;
	
	public abstract void saveNotesSeq (long saverId,long bookId,List<Integer> notesSeq) throws SMException;
	
	
	public abstract List<NoteBookProxy> loadBooks(long loginerId) throws DBException,LogicException;
	public abstract NoteBookProxy loadBook(long loginerId,long bookId) throws DBException,LogicException;
	/**
	 * Note: id name withTodos prevId important
	 */
	public abstract BookContent loadBookContent(long loginerId,long noteBookId) throws DBException, LogicException;
	public abstract NoteProxy loadNote(long loginerId,long noteId) throws DBException, LogicException;
	public abstract MemoProxy loadMemo(long loginerId) throws DBException, LogicException;
	
	
	/**
	  * 只允许noteBook Closed时，才允许删除，删除时，会将Note一起删掉
	 */
	public abstract void deleteNoteBook(long deletorId,long id) throws DBException, LogicException;
	
	public abstract void deleteNote(long deletorId,long id) throws DBException, LogicException;
	
	public abstract void addItemToMemo(long adderId,String content,NoteLabel label,String note,long srcNoteId) throws LogicException, DBException;
	
	public abstract void removeItemFromMemo(long removerId,int itemId)  throws LogicException, DBException;
	
	public abstract void saveMemoItem(long updaterId,int itemId,String content,NoteLabel label,String note) throws LogicException, DBException;
	
	public abstract void saveMemoItemsSeq(long updaterId,List<Integer> seqIds)  throws LogicException, DBException;
	
	public abstract void saveMemoItemLabel(long updaterId,int itemId,NoteLabel label) throws LogicException, DBException;
	
	/**
	  *  假如NoteBook里 无Note且无备注 则直接删除
	 * 假如有Note 放到closed里 
	 */
	public abstract void closeNoteBook(long closerId,long bookId) throws DBException, LogicException;
	public abstract void openNoteBook(long closerId,long bookId) throws DBException, LogicException;
	
/*=================================================NOT ABSTRACT ==============================================================*/

	/**
	 * 处理顺序，有一定容错性：
	 * a.notesSeq可能为null 旧数据，此时顺序按输入
	 * b.可能存在ID 不在notesSeq中的情况 此刻是由于保存时意外发生得错误导致的,也有可能是新增的，把这部分摘出来，放到最前面，根据ID倒序
	 * c.可能存在NotesSeq有，但ID没的情况，此时是由于删除或异常情况 过掉。
	 * d.由于笔记页根据是否重要分组，因此，NotesSeq有，但ID没的情况还可能是由于数据不在所在分组里
	 */
	protected static List<Note> sortBy(List<Note> target, String notesSeq) {
		
		Comparator<Note> comp = Comparator.comparing(Note::getUpdateTime).reversed();
		
		if(notesSeq == null) {
			return target.stream().sorted(comp).collect(Collectors.toList());
		}
		
		List<Integer> seq = Arrays.stream(notesSeq.split(SM.ARRAY_SPLIT_MARK)).map(Integer::parseInt).collect(Collectors.toList());
		
		List<Note> rlt = new ArrayList<Note>();
		
		List<Note> notIncluded = target.stream().filter(t->!seq.contains(t.getId())).sorted(comp).collect(Collectors.toList());
		
		rlt.addAll(notIncluded);
		
		Map<Long,Note> notesById =  target.stream().collect(Collectors.toMap(Note::getId, Function.identity()));
		
		seq.forEach(id->{
			
			if(!notesById.containsKey(id)) {
				return;
			}
			
			rlt.add(notesById.get(id));
		});
		
		return rlt;
	}
	
	public static synchronized NoteLogic getInstance() {
		if(instance == null) {
			instance = new NoteLogicImpl();
		}
		return instance;
	}
	

}
