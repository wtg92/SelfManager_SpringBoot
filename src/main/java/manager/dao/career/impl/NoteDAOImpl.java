package manager.dao.career.impl;

import manager.dao.career.NoteDAO;
import manager.dao.career.WorkDAO;
import manager.entity.general.career.*;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static manager.util.DBUtil.*;

@Repository
public class NoteDAOImpl implements NoteDAO {
	@Resource
	private SessionFactory sessionFactory;


	@Override
	public long insertNoteBook(NoteBook book) throws DBException {
		return 0;
	}

	@Override
	public long insertNote(Note note) throws DBException {
		return 0;
	}

	@Override
	public long insertMemo(Memo memo) throws DBException {
		return 0;
	}

	@Override
	public NoteBook selectNoteBook(long id) throws NoSuchElement, DBException {
		return null;
	}

	@Override
	public Note selectNote(long id) throws NoSuchElement, DBException {
		return null;
	}

	@Override
	public Memo selectMemoByOwner(long ownerId) throws NoSuchElement, DBException {
		return null;
	}

	@Override
	public void updateExistedNoteBook(NoteBook book) throws DBException {

	}

	@Override
	public void updateExistedNote(Note note) throws DBException {

	}

	@Override
	public void updateNoteNameAndContentAndWithTodos(Note note) throws DBException {

	}

	@Override
	public void updateExistedMemo(Memo memo) throws DBException {

	}

	@Override
	public List<NoteBook> selectBooksByOwner(long ownerId) throws DBException {
		return selectEntitiesByField(NoteBook.class, DBConstants.F_OWNER_ID,ownerId, sessionFactory);

	}

	@Override
	public void deleteExistedNoteBook(long bookId) throws DBException {

	}

	@Override
	public void deleteExistedNote(long noteId) throws DBException {

	}

	@Override
	public List<Note> selectNoteInfosByBook(long noteBookId) throws DBException {
		return selectEntitiesByField(Note.class, DBConstants.F_NOTE_BOOK_ID,noteBookId, sessionFactory);
	}

	@Override
	public List<Note> selectNoteInfosByBookAndImportant(long noteBookId, boolean important) throws DBException {
		return null;
	}

	@Override
	public List<Note> selectNotesWithIdAndNameByIds(List<Long> ids) throws DBException {
		return null;
	}

	@Override
	public List<NoteBook> selectBooksWithIdAndNameByIds(List<Long> ids) throws DBException {
		return null;
	}

	@Override
	public Long countNotesByBook(long noteBookId) throws DBException {
		return null;
	}

	@Override
	public boolean includeNotesByBook(long noteBookId) throws DBException {
		return false;
	}

	@Override
	public void deleteNotesByBook(long bookId) throws DBException {

	}
}
