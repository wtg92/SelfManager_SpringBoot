package manager.logic.career.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import manager.dao.DAOFactory;
import manager.dao.career.NoteDAO;
import manager.data.career.BookContent;
import manager.data.proxy.career.MemoItemProxy;
import manager.data.proxy.career.MemoProxy;
import manager.data.proxy.career.NoteBookProxy;
import manager.data.proxy.career.NoteProxy;
import manager.entity.general.career.Memo;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.career.NoteLogic;
import manager.logic.career.sub.NoteContentConverter;
import manager.logic.sub.CacheScheduler;
import manager.system.CacheMode;
import manager.system.SM;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.career.BookStyle;
import manager.system.career.NoteLabel;

/**
 * 这个类 暂且 只有涉及到seq变化的需要sync修饰
 */
public class NoteLogicImpl extends NoteLogic {

	private NoteDAO nDAO = DAOFactory.getNoteDAO();

	@Override
	public long createNoteBook(long creatorId, String name, String note, BookStyle style)
			throws DBException, LogicException {

		uL.checkPerm(creatorId, SMPerm.CREATE_NOTE_BOOK_AND_NOTE);

		NoteBook book = new NoteBook();
		book.setName(name);
		book.setNote(note);
		book.setClosed(false);
		book.setOwnerId(creatorId);
		book.setSeqWeight(0);
		book.setStyle(style);

		return nDAO.insertNoteBook(book);
	}

	@Override
	public long createNote(long creatorId, long noteBookId, String name) throws DBException, LogicException {
		uL.checkPerm(creatorId, SMPerm.CREATE_NOTE_BOOK_AND_NOTE);

		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (book.getOwnerId() != creatorId) {
			throw new LogicException(SMError.CANNOT_ADD_NOTE_IN_THE_NOTEBOOK_OF_OTHERS, "不能为别人的笔记本添加笔记");
		}

		Note note = new Note();
		note.setName(name);
		note.setContent("");
		note.setWithTodos(false);
		note.setImportant(false);
		note.setNoteBookId(noteBookId);

		return nDAO.insertNote(note);
	}
	
	@Override
	public void saveNoteBook(long saverId, long noteBookId, String name, String note, BookStyle style, int seqWeight)
			throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "无权修改别人的笔记本");
		}

		checkBookOpened(book);

		book.setNote(note);
		book.setName(name);
		book.setStyle(style);
		book.setSeqWeight(seqWeight);
		CacheScheduler.saveEntity(book, one -> nDAO.updateExistedNoteBook(one));
	}

	private void checkBookOpened(NoteBook book) throws LogicException {
		if (book.getClosed()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "笔记本已关闭");
		}
	}

	@Override
	public long saveNoteImportant(long saverId, long noteId, boolean important) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));

		long noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_OF_OTHERS, "无权修改别人的笔记");
		}

		checkBookOpened(book);

		note.setImportant(important);
		
		CacheScheduler.saveEntity(note, one -> nDAO.updateExistedNote(one));

		return noteBookId;
	}
	
	
	@Override
	public long saveNoteHidden(long saverId, long noteId, boolean hidden) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));

		long noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_OF_OTHERS, "无权修改别人的笔记");
		}

		checkBookOpened(book);

		note.setHidden(hidden);
		
		CacheScheduler.saveEntity(note, one -> nDAO.updateExistedNote(one));

		return noteBookId;
	}
	
	
	@Override
	public void saveNote(long saverId, long noteId, String name, String content, boolean withTodos)
			throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));

		long noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_OF_OTHERS, "无权修改别人的笔记");
		}

		checkBookOpened(book);

		note.setName(name);
		note.setContent(content);
		note.setWithTodos(withTodos);
		nDAO.updateNoteNameAndContentAndWithTodos(note);
		CacheScheduler.deleteEntityByIdOnlyForCache(note);
	}

	@Override
	public void saveMemo(long saverId, String note) throws DBException, LogicException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, saverId, Memo.class, ()->nDAO.selectExistedMemoByOwner(saverId));
		memo.setNote(note);
		CacheScheduler.saveEntity(memo,p->nDAO.updateExistedMemo(p));
	}
	
	@Override
	public void saveNotesSeq (long saverId,long bookId,List<Integer> notesSeq) throws SMException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "不能调整非本人的笔记顺序");
		}

		checkBookOpened(book);
		book.setNotesSeq(notesSeq.stream().map(String::valueOf).collect(Collectors.joining(SM.ARRAY_SPLIT_MARK)));
		CacheScheduler.saveEntity(book, one -> nDAO.updateExistedNoteBook(one));
	}

	@Override
	public List<NoteBookProxy> loadBooks(long loginerId) throws DBException, LogicException {
		List<NoteBookProxy> rlt = nDAO.selectBooksByOwner(loginerId).stream().map(NoteBookProxy::new).collect(toList());
		return rlt;
	}

	@Override
	public NoteBookProxy loadBook(long loginerId, long bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_SEE_OTHER_NOTE_BOOK);
		}
		return new NoteBookProxy(book);
	}

	@Override
	public BookContent loadBookContent(long loginerId, long noteBookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_SEE_OTHERS_NOTE);
		}
		BookContent content = new BookContent();
		
		Map<Boolean,List<Note>> notesByImportant = nDAO.selectNoteInfosByBook(noteBookId).stream().collect(Collectors.partitioningBy(Note::getImportant));
		
		content.importantNotes =  sortBy(notesByImportant.get(true),book.getNotesSeq());
		content.generalNotes =  sortBy(notesByImportant.get(false),book.getNotesSeq());
		content.book = book;
		return content;
	}

	@Override
	public NoteProxy loadNote(long loginerId, long noteId) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));
		long noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_SEE_OTHERS_NOTE);
		}
		NoteProxy rlt = new NoteProxy(note);
		return rlt;
	}
	
	@Override
	public MemoProxy loadMemo(long loginerId) throws DBException, LogicException {
		Memo memo = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, Memo.class, 
				 ()->nDAO.selectMemoByOwner(loginerId), ()->initMemo(loginerId));
		
		MemoProxy proxy = new MemoProxy(memo);
		
		proxy.content = NoteContentConverter.convertMemo(memo);
		
		fill(proxy.content.items);
		
		return proxy;
	}
	
	private void fill(List<MemoItemProxy> items) throws DBException {
		List<Long> booksId = items.stream().filter(p->p.item.getSrcNoteId()>0).map(memo->memo.item.getSrcBookId()).distinct().collect(toList());
		List<Long> notesId  = items.stream().filter(p->p.item.getSrcNoteId()>0).map(memo->memo.item.getSrcNoteId()).distinct().collect(toList());
		Map<Long,NoteBook> relevantBooks = nDAO.selectBooksWithIdAndNameByIds(booksId).stream().collect(toMap(NoteBook::getId,Function.identity()));
		Map<Long,Note> relevantNotes = nDAO.selectNotesWithIdAndNameByIds(notesId).stream().collect(toMap(Note::getId,Function.identity()));;
		
		
		for(MemoItemProxy proxy : items) {
			if(proxy.item.getSrcNoteId() == 0) {
				continue;
			}
			
			NoteBook book = relevantBooks.get(proxy.item.getSrcBookId());
			if(book == null) {
				proxy.srcBookName = proxy.item.getSrcBookName()+"（已删除）";
			}else {
				proxy.srcBookName = book.getName();
			}
			
			Note note = relevantNotes.get(proxy.item.getSrcNoteId());
			if(note == null) {
				proxy.srcNoteName = proxy.item.getSrcNoteName()+"（已删除）";
			}else {
				proxy.srcNoteName = note.getName();
			}
		}
	}

	private synchronized long initMemo(long ownerId) throws DBException {
		Memo memo = new Memo();
		memo.setOwnerId(ownerId);
		memo.setContent("");
		memo.setNote("");
		return nDAO.insertMemo(memo);
	}
	
	
	@Override
	public void deleteNoteBook(long deletorId, long id) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, id, NoteBook.class, () -> nDAO.selectExistedNoteBook(id));
		if (deletorId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "不能删除非本人的笔记本");
		}

		if (!book.getClosed()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "不能删除已打开的笔记本");
		}

		/* 这里删除 就不管缓存了 等缓存自己消亡就行 */
		nDAO.deleteNotesByBook(book.getId());

		CacheScheduler.deleteEntityById(book, idForFunc -> nDAO.deleteExistedNoteBook(idForFunc));
	}

	@Override
	public void deleteNote(long deletorId, long id) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, id, Note.class, () -> nDAO.selectExistedNote(id));

		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, note.getNoteBookId(), NoteBook.class,
				() -> nDAO.selectExistedNoteBook(note.getNoteBookId()));
		
		if (deletorId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_OF_OTHERS, "不能删除非本人的笔记本");
		}

		checkBookOpened(book);

		CacheScheduler.deleteEntityById(note, idForFunc -> nDAO.deleteExistedNote(idForFunc));
	}

	@Override
	public void closeNoteBook(long closerId, long bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (closerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "不能关闭非本人的笔记本");
		}

		checkBookOpened(book);

		if (book.getNote().strip().length() == 0 && !nDAO.includeNotesByBook(bookId)) {
			CacheScheduler.deleteEntityById(book, idForFunc -> nDAO.deleteExistedNoteBook(idForFunc));
			return;
		}

		book.setClosed(true);
		CacheScheduler.saveEntity(book, one -> nDAO.updateExistedNoteBook(one));
	}

	@Override
	public void openNoteBook(long closerId, long bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (closerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "不能重启非本人的笔记本");
		}

		if (!book.getClosed()) {
			throw new LogicException(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, "笔记本已打开");
		}

		book.setClosed(false);
		CacheScheduler.saveEntity(book, one -> nDAO.updateExistedNoteBook(one));
	}

	@Override
	public void addItemToMemo(long adderId, String content, NoteLabel label, String note,long srcNoteId)
			throws LogicException, DBException {
		
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, adderId, Memo.class, ()->nDAO.selectExistedMemoByOwner(adderId));
		String srcNoteName = "";
		long srcBookId = 0;
		String srcBookName = "";
		if(srcNoteId != 0) {
			Note srcNote = CacheScheduler.getOne(CacheMode.E_ID, srcNoteId, Note.class, () -> nDAO.selectExistedNote(srcNoteId));
			srcNoteName = srcNote.getName();
			NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, srcNote.getNoteBookId(), NoteBook.class,
					() -> nDAO.selectExistedNoteBook(srcNote.getNoteBookId()));
			
			if (adderId != book.getOwnerId()) {
				throw new LogicException(SMError.EDIT_MEMO_OF_OTHERS_ERROR, "无权把别人笔记内容放入备忘录");
			}

			checkBookOpened(book);
			
			srcBookName = book.getName();
			srcBookId = book.getId();
			
		}
		
		NoteContentConverter.addItemToMemo(memo, content, label, note, srcNoteId, srcNoteName,srcBookId,srcBookName);
		CacheScheduler.saveEntity(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void removeItemFromMemo(long removerId, int itemId) throws LogicException, DBException {
		Memo one = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, removerId, Memo.class, ()->nDAO.selectExistedMemoByOwner(removerId));
		NoteContentConverter.removeItemFromMemo(one, itemId);
		CacheScheduler.saveEntity(one,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItem(long updaterId,int itemId,String content, NoteLabel label, String note)
			throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItem(memo, itemId, content, label, note);
		CacheScheduler.saveEntity(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItemsSeq(long updaterId, List<Integer> seqIds) throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItemsSeq(memo, seqIds);
		CacheScheduler.saveEntity(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItemLabel(long updaterId, int itemId, NoteLabel label) throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItemLabel(memo, itemId, label);
		CacheScheduler.saveEntity(memo,p->nDAO.updateExistedMemo(p));
	}

}
