package manager.dao.career;

import java.util.List;

import manager.entity.general.career.Memo;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMError;

public interface NoteDAO {
	
	int insertNoteBook(NoteBook book) throws DBException;
	int insertNote(Note note) throws DBException;
	int insertMemo(Memo memo) throws DBException;
	
	NoteBook selectNoteBook(int id) throws NoSuchElement, DBException;
	Note selectNote(int id) throws NoSuchElement, DBException;
	Memo selectMemoByOwner(int ownerId) throws NoSuchElement, DBException;
	
	void updateExistedNoteBook(NoteBook book) throws DBException;
	void updateExistedNote(Note note) throws DBException;
	void updateNoteNameAndContentAndWithTodos(Note note) throws DBException;
	
	void updateExistedMemo(Memo memo) throws DBException;
	
	List<NoteBook> selectBooksByOwner(int ownerId) throws DBException;
	
	void deleteExistedNoteBook(int bookId) throws DBException;
	void deleteExistedNote(int noteId) throws DBException;
	
	/*id name withTodos prevId important*/
	List<Note> selectNoteInfosByBook(int noteBookId) throws DBException;
	List<Note> selectNoteInfosByBookAndImportant(int noteBookId,boolean important) throws DBException;
	
	List<Note> selectNotesWithIdAndNameByIds(List<Integer> ids) throws DBException;
	List<NoteBook> selectBooksWithIdAndNameByIds(List<Integer> ids) throws DBException;
	
	Long countNotesByBook(int noteBookId) throws DBException;
	
	boolean includeNotesByBook(int noteBookId) throws DBException;
	
	void deleteNotesByBook(int bookId) throws DBException;
	
	/*====================== NOT ABSTRACT ==========================*/
	default Memo selectExistedMemoByOwner(int ownerId) throws DBException{
		try {
			return selectMemoByOwner(ownerId);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,ownerId);
		}
	}
	
	default NoteBook selectExistedNoteBook(int id) throws DBException{
		try {
			return selectNoteBook(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}
	
	default Note selectExistedNote(int id) throws DBException{
		try {
			return selectNote(id);
		}catch (NoSuchElement e) {
			throw new DBException(SMError.INCONSISTANT_DB_ERROR,id);
		}
	}

	
}
