package manager.logic.career;

import static manager.system.SM.logger;
import static java.util.stream.Collectors.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


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
import manager.exception.NoSuchElement;
import manager.logic.CacheScheduler;
import manager.system.CacheMode;
import manager.system.SM;
import manager.system.SMDB;
import manager.system.SMError;
import manager.system.SMPerm;
import manager.system.career.BookStyle;
import manager.system.career.CareerLogAction;
import manager.system.career.NoteLabel;
import manager.util.ThrowableSupplier;

/**
 * 这个类 暂且 只有涉及到seq变化的需要sync修饰
 */
public class NoteLogic_Real extends NoteLogic {

	private NoteDAO nDAO = DAOFactory.getNoteDAO();

	@Override
	public int createNoteBook(int creatorId, String name, String note, BookStyle style)
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

	/**
	 * 首先数据结构是没错的，根据最简原则，就应该这么弄。 其次 每次都需要全取出来 并且 应该提供 selectId And PrevId 和 UpdateId
	 * And PrevId 专门的两个函数 前台的事，等到前台再考虑
	 */
	@Override
	public int createNote(int creatorId, int noteBookId, String name) throws DBException, LogicException {
		uL.checkPerm(creatorId, SMPerm.CREATE_NOTE_BOOK_AND_NOTE);

		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (book.getOwnerId() != creatorId) {
			throw new LogicException(SMError.CREATE_NOTE_ERROR, "不能为别人的笔记本添加笔记");
		}

		Note note = new Note();
		note.setName(name);
		note.setContent("");
		note.setWithTodos(false);
		note.setImportant(false);
		note.setNoteBookId(noteBookId);
		note.setPrevNoteId(0);

		return opreateNoteAndAddToRoot(note.getImportant(), noteBookId, () -> nDAO.insertNote(note));
	}

	private synchronized int opreateNoteAndAddToRoot(boolean important, int bookId,
			ThrowableSupplier<Integer, DBException> dbOperatorAndPrevIdGetter) throws DBException {
		try {
			int originRootId = nDAO.selectNoteIdByBookAndPrevIdAndImportant(bookId, 0, important);
			int insertedId = dbOperatorAndPrevIdGetter.get();
			nDAO.updateNotePrevId(originRootId, insertedId);
			CacheScheduler.deleteEntityByIdOnlyForCache(originRootId, SMDB.T_NOTE);
			return insertedId;
		} catch (NoSuchElement e) {
			/* 本身就是Root 不牵扯其它 */
			return dbOperatorAndPrevIdGetter.get();
		}
	}

	@Override
	public void saveNoteBook(int saverId, int noteBookId, String name, String note, BookStyle style, int seqWeight)
			throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "无权修改别人的笔记本");
		}

		checkBookOpened(book);

		book.setNote(note);
		book.setName(name);
		book.setStyle(style);
		book.setSeqWeight(seqWeight);
		CacheScheduler.saveEntityAndUpdateCache(book, one -> nDAO.updateExistedNoteBook(one));
	}

	private void checkBookOpened(NoteBook book) throws LogicException {
		if (book.getClosed()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "笔记本已关闭");
		}
	}

	@Override
	public synchronized int saveNoteImportant(int saverId, int noteId, boolean important) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));

		int noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTE_ERROR, "无权修改别人的笔记");
		}

		checkBookOpened(book);

		if (important == note.getImportant()) {
			throw new LogicException(SMError.EDIT_NOTE_ERROR, "不能将重要的笔记标为重要或将普通笔记标为普通");
		}
		
		/* 修改笔记的important，此时对于!important列表来说，相当于删掉 对于important笔记来说 相当于增加 */
		
		refreshRelevantNotesSeqForDelete(note.getId(),note.getPrevNoteId(),noteBookId,!important);
		
		note.setImportant(important);
		note.setPrevNoteId(0);
		
		opreateNoteAndAddToRoot(important, noteBookId, () -> {
			try {
				CacheScheduler.saveEntityAndUpdateCache(note, one -> nDAO.updateExistedNote(one));
				return note.getId();
			} catch (LogicException e) {
				/* Java的这个异常处理接口 有点烦了嗷*/
				throw new DBException(e.type);
			}
		});

		return noteBookId;
	}
	
	/**
	 *  这里由于会出现并发问题 save的时候 不应该save SrcId!
	 */
	@Override
	public void saveNote(int saverId, int noteId, String name, String content, boolean withTodos)
			throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));

		int noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTE_ERROR, "无权修改别人的笔记");
		}

		checkBookOpened(book);

		note.setName(name);
		note.setContent(content);
		note.setWithTodos(withTodos);
		/*因为这个方法不想加sync修饰，因此采取另一种方式来保存。其实主要还是为了维护脆弱的prevId*/
		nDAO.updateNoteNameAndContentAndWithTodos(note);
		CacheScheduler.deleteEntityByIdOnlyForCache(note);
	}

	@Override
	public void saveMemo(int saverId, String note) throws DBException, LogicException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, saverId, Memo.class, ()->nDAO.selectExistedMemoByOwner(saverId));
		memo.setNote(note);
		CacheScheduler.saveEntityAndUpdateCache(memo,p->nDAO.updateExistedMemo(p));
	}
	
	@Override
	public synchronized void saveNotesSeq(int saverId, int targetNoteId, int prevNoteIdForTarget)
			throws DBException, LogicException {
		if(targetNoteId == prevNoteIdForTarget) {
			logger.errorLog("save notes seq with same ids, dont do anything");
			return;
		}
		Note note = CacheScheduler.getOne(CacheMode.E_ID, targetNoteId, Note.class,
				() -> nDAO.selectExistedNote(targetNoteId));
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, note.getNoteBookId(), NoteBook.class,
				() -> nDAO.selectExistedNoteBook(note.getNoteBookId()));
		if (saverId != book.getOwnerId()) {
			throw new LogicException(SMError.SAVE_NOTES_SEQ_ERROR, "不能调整非本人的笔记顺序");
		}

		checkBookOpened(book);

		List<Note> notesForThisBook = nDAO.selectNoteInfosByBookAndImportant(note.getNoteBookId(), note.getImportant());
		assert notesForThisBook.size() > 0;
		if (prevNoteIdForTarget != 0 && notesForThisBook.stream().allMatch(n -> n.getId() != prevNoteIdForTarget)) {
			throw new LogicException(SMError.SAVE_NOTES_SEQ_ERROR, "不能调整不同笔记本间或重要程度不同的笔记顺序");
		}

		int srcNotePrevId = note.getPrevNoteId();

		note.setPrevNoteId(prevNoteIdForTarget);
		CacheScheduler.saveEntityAndUpdateCache(note, one -> nDAO.updateExistedNote(one));

		Note prevSon = notesForThisBook.stream().filter(origin -> origin.getPrevNoteId() == prevNoteIdForTarget)
				.findAny().orElse(null);
		if (prevSon != null && prevSon.getId() != note.getId()) {
			nDAO.updateNotePrevId(prevSon.getId(), note.getId());
			CacheScheduler.deleteEntityByIdOnlyForCache(prevSon);
		}

		Note targetSon = notesForThisBook.stream().filter(origin -> origin.getPrevNoteId() == note.getId()).findAny()
				.orElse(null);
		if (targetSon != null && (prevSon == null || prevSon.getId() != note.getId())) {
			nDAO.updateNotePrevId(targetSon.getId(), srcNotePrevId);
			CacheScheduler.deleteEntityByIdOnlyForCache(targetSon);
		}
	}

	@Override
	public List<NoteBookProxy> loadBooks(int loginerId) throws DBException, LogicException {
		List<NoteBookProxy> rlt = nDAO.selectBooksByOwner(loginerId).stream().map(NoteBookProxy::new).collect(toList());
		return rlt;
	}

	@Override
	public NoteBookProxy loadBook(int loginerId, int bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOTE_SEE_OTHER_NOTE_BOOK);
		}
		return new NoteBookProxy(book);
	}

	@Override
	public BookContent loadBookContent(int loginerId, int noteBookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_SEE_OTHERS_NOTE);
		}
		BookContent content = new BookContent();
		content.notes = nDAO.selectNoteInfosByBook(noteBookId);
		content.book = book;
		return content;
	}

	@Override
	public NoteProxy loadNote(int loginerId, int noteId) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, noteId, Note.class, () -> nDAO.selectExistedNote(noteId));
		int noteBookId = note.getNoteBookId();
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, noteBookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(noteBookId));
		if (loginerId != book.getOwnerId()) {
			throw new LogicException(SMError.CANNOT_SEE_OTHERS_NOTE);
		}
		NoteProxy rlt = new NoteProxy(note);
		return rlt;
	}
	
	@Override
	public MemoProxy loadMemo(int loginerId) throws DBException, LogicException {
		Memo memo = CacheScheduler.getOneOrInitIfNotExists(CacheMode.E_UNIQUE_FIELD_ID, loginerId, Memo.class, 
				 ()->nDAO.selectMemoByOwner(loginerId), ()->initMemo(loginerId));
		
		MemoProxy proxy = new MemoProxy(memo);
		
		proxy.content = NoteContentConverter.convertMemo(memo);
		
		fill(proxy.content.items);
		
		return proxy;
	}
	
	private void fill(List<MemoItemProxy> items) throws DBException {
		List<Integer> booksId = items.stream().filter(p->p.item.getSrcNoteId()>0).map(memo->memo.item.getSrcBookId()).distinct().collect(toList());
		List<Integer> notesId  = items.stream().filter(p->p.item.getSrcNoteId()>0).map(memo->memo.item.getSrcNoteId()).distinct().collect(toList());
		Map<Integer,NoteBook> relevantBooks = nDAO.selectBooksWithIdAndNameByIds(booksId).stream().collect(toMap(NoteBook::getId,Function.identity()));
		Map<Integer,Note> relevantNotes = nDAO.selectNotesWithIdAndNameByIds(notesId).stream().collect(toMap(Note::getId,Function.identity()));;
		
		
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

	private synchronized int initMemo(int ownerId) throws DBException {
		Memo memo = new Memo();
		memo.setOwnerId(ownerId);
		memo.setContent("");
		memo.setNote("");
		return nDAO.insertMemo(memo);
	}
	
	
	@Override
	public synchronized void deleteNoteBook(int deletorId, int id) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, id, NoteBook.class, () -> nDAO.selectExistedNoteBook(id));
		if (deletorId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "不能删除非本人的笔记本");
		}

		if (!book.getClosed()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "不能删除已打开的笔记本");
		}

		/* 这里删除 就不管缓存了 等缓存自己消亡就行 */
		nDAO.deleteNotesByBook(book.getId());

		CacheScheduler.deleteEntityById(book, idForFunc -> nDAO.deleteExistedNoteBook(idForFunc));
	}

	@Override
	public synchronized void deleteNote(int deletorId, int id) throws DBException, LogicException {
		Note note = CacheScheduler.getOne(CacheMode.E_ID, id, Note.class, () -> nDAO.selectExistedNote(id));

		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, note.getNoteBookId(), NoteBook.class,
				() -> nDAO.selectExistedNoteBook(note.getNoteBookId()));
		if (deletorId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTE_ERROR, "不能删除非本人的笔记本");
		}

		checkBookOpened(book);

		refreshRelevantNotesSeqForDelete(note.getId(),note.getPrevNoteId(),book.getId(),note.getImportant());

		CacheScheduler.deleteEntityById(note, idForFunc -> nDAO.deleteExistedNote(idForFunc));
	}

	/* 链表来讲 假如删除一个节点 只需要维护它下一个节点 连到自己上一个节点即可 */
	private synchronized void refreshRelevantNotesSeqForDelete(int noteId,int prevId,int bookId,boolean important) throws DBException {
		assert noteId != 0;
		try {
			int noteSonId = nDAO.selectNoteIdByBookAndPrevIdAndImportant(bookId, noteId,important);
			nDAO.updateNotePrevId(noteSonId, prevId);
			CacheScheduler.deleteEntityByIdOnlyForCache(noteSonId, SMDB.T_NOTE);
		} catch (NoSuchElement e) {
			/* 无son 则不牵扯其它 */
		}
	}

	@Override
	public void closeNoteBook(int closerId, int bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (closerId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "不能关闭非本人的笔记本");
		}

		checkBookOpened(book);

		if (book.getNote().strip().length() == 0 && !nDAO.includeNotesByBook(bookId)) {
			CacheScheduler.deleteEntityById(book, idForFunc -> nDAO.deleteExistedNoteBook(idForFunc));
			return;
		}

		book.setClosed(true);
		CacheScheduler.saveEntityAndUpdateCache(book, one -> nDAO.updateExistedNoteBook(one));
	}

	@Override
	public void openNoteBook(int closerId, int bookId) throws DBException, LogicException {
		NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, bookId, NoteBook.class,
				() -> nDAO.selectExistedNoteBook(bookId));
		if (closerId != book.getOwnerId()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "不能重启非本人的笔记本");
		}

		if (!book.getClosed()) {
			throw new LogicException(SMError.EDIT_NOTEBOOK_ERROR, "笔记本已打开");
		}

		book.setClosed(false);
		CacheScheduler.saveEntityAndUpdateCache(book, one -> nDAO.updateExistedNoteBook(one));
	}

	@Override
	public void addItemToMemo(int adderId, String content, NoteLabel label, String note,int srcNoteId)
			throws LogicException, DBException {
		
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, adderId, Memo.class, ()->nDAO.selectExistedMemoByOwner(adderId));
		String srcNoteName = "";
		int srcBookId = 0;
		String srcBookName = "";
		if(srcNoteId != 0) {
			Note srcNote = CacheScheduler.getOne(CacheMode.E_ID, srcNoteId, Note.class, () -> nDAO.selectExistedNote(srcNoteId));
			srcNoteName = srcNote.getName();
			NoteBook book = CacheScheduler.getOne(CacheMode.E_ID, srcNote.getNoteBookId(), NoteBook.class,
					() -> nDAO.selectExistedNoteBook(srcNote.getNoteBookId()));
			
			if (adderId != book.getOwnerId()) {
				throw new LogicException(SMError.EDIT_MEMO_ERRO, "无权把别人笔记内容放入备忘录");
			}

			checkBookOpened(book);
			
			srcBookName = book.getName();
			srcBookId = book.getId();
			
		}
		
		NoteContentConverter.addItemToMemo(memo, content, label, note, srcNoteId, srcNoteName,srcBookId,srcBookName);
		CacheScheduler.saveEntityAndUpdateCache(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void removeItemFromMemo(int removerId, int itemId) throws LogicException, DBException {
		Memo one = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, removerId, Memo.class, ()->nDAO.selectExistedMemoByOwner(removerId));
		NoteContentConverter.removeItemFromMemo(one, itemId);
		CacheScheduler.saveEntityAndUpdateCache(one,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItem(int updaterId,int itemId,String content, NoteLabel label, String note)
			throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItem(memo, itemId, content, label, note);
		CacheScheduler.saveEntityAndUpdateCache(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItemsSeq(int updaterId, List<Integer> seqIds) throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItemsSeq(memo, seqIds);
		CacheScheduler.saveEntityAndUpdateCache(memo,p->nDAO.updateExistedMemo(p));
	}

	@Override
	public void saveMemoItemLabel(int updaterId, int itemId, NoteLabel label) throws LogicException, DBException {
		Memo memo = CacheScheduler.getOne(CacheMode.E_UNIQUE_FIELD_ID, updaterId, Memo.class, ()->nDAO.selectExistedMemoByOwner(updaterId));
		NoteContentConverter.updateMemoItemLabel(memo, itemId, label);
		CacheScheduler.saveEntityAndUpdateCache(memo,p->nDAO.updateExistedMemo(p));
	}

}
