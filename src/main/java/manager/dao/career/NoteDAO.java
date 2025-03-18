package manager.dao.career;

import java.util.List;

import manager.entity.general.career.Memo;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SelfXErrors;

public interface NoteDAO {
	
	long insertNoteBook(NoteBook book) throws DBException;
	long insertNote(Note note) throws DBException;
	long insertMemo(Memo memo) throws DBException;
	
	NoteBook selectNoteBook(long id) throws NoSuchElement, DBException;
	Note selectNote(long id) throws NoSuchElement, DBException;
	Memo selectMemoByOwner(long ownerId) throws NoSuchElement, DBException;
	
	void updateExistedNoteBook(NoteBook book) throws DBException;
	void updateExistedNote(Note note) throws DBException;
	void updateNoteNameAndContentAndWithTodos(Note note) throws DBException;
	
	void updateExistedMemo(Memo memo) throws DBException;
	
	List<NoteBook> selectBooksByOwner(long ownerId) throws DBException;
	
	void deleteExistedNoteBook(long bookId) throws DBException;
	void deleteExistedNote(long noteId) throws DBException;
	
	/*id name withTodos prevId important*/
	List<Note> selectNoteInfosByBook(long noteBookId) throws DBException;
	List<Note> selectNoteInfosByBookAndImportant(long noteBookId,boolean important) throws DBException;
	
	List<Note> selectNotesWithIdAndNameByIds(List<Long> ids) throws DBException;
	List<NoteBook> selectBooksWithIdAndNameByIds(List<Long> ids) throws DBException;
	
	Long countNotesByBook(long noteBookId) throws DBException;
	
	boolean includeNotesByBook(long noteBookId) throws DBException;
	
	void deleteNotesByBook(long bookId) throws DBException;
	
	/*====================== NOT ABSTRACT ==========================*/
	default Memo selectExistedMemoByOwner(long ownerId) throws DBException{
		try {
			return selectMemoByOwner(ownerId);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,ownerId);
		}
	}
	
	default NoteBook selectExistedNoteBook(long id) throws DBException{
		try {
			return selectNoteBook(id);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}
	
	default Note selectExistedNote(long id) throws DBException{
		try {
			return selectNote(id);
		}catch (NoSuchElement e) {
			throw new DBException(SelfXErrors.INCONSISTENT_DB_ERROR,id);
		}
	}

	
}
