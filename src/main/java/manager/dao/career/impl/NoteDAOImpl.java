package manager.dao.career.impl;

import static manager.util.DBUtil.countEntitiesByField;
import static manager.util.DBUtil.deleteEntitiesByField;
import static manager.util.DBUtil.deleteEntity;
import static manager.util.DBUtil.includeEntitiesByField;
import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.selectEntitiesByField;
import static manager.util.DBUtil.selectEntity;
import static manager.util.DBUtil.selectUniqueEntityByField;
import static manager.util.DBUtil.updateExistedEntity;
import static manager.util.DBUtil.processDBException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.dao.career.NoteDAO;
import manager.entity.general.career.Memo;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.util.TimeUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class NoteDAOImpl implements NoteDAO {
	
	@Resource
	private SessionFactory sessionFactory;
	
	@Override
	public long insertNoteBook(NoteBook book) throws DBException {
		return insertEntity(book, sessionFactory);
	}

	@Override
	public long insertNote(Note note) throws DBException {
		return insertEntity(note, sessionFactory);
	}

	@Override
	public long insertMemo(Memo memo) throws DBException {
		return insertEntity(memo, sessionFactory);
	}

	@Override
	public NoteBook selectNoteBook(long id) throws NoSuchElement, DBException {
		return selectEntity(id,NoteBook.class, sessionFactory);
	}

	@Override
	public Note selectNote(long id) throws NoSuchElement, DBException {
		return selectEntity(id,Note.class, sessionFactory);
	}

	@Override
	public Memo selectMemoByOwner(long ownerId) throws NoSuchElement, DBException {
		return selectUniqueEntityByField(Memo.class, DBConstants.F_OWNER_ID, ownerId, sessionFactory);
	}
	
	@Override
	public void updateExistedNoteBook(NoteBook book) throws DBException {
		updateExistedEntity(book, sessionFactory);
		
	}

	@Override
	public void updateExistedNote(Note note) throws DBException {
		updateExistedEntity(note, sessionFactory);
	}
	
	@Override
	public void updateExistedMemo(Memo memo) throws DBException {
		updateExistedEntity(memo, sessionFactory);
	}
	
	@Override
	public void updateNoteNameAndContentAndWithTodos(Note note) throws DBException {
		Session session = null;
		Transaction trans = null;
		note.setUpdateTime(TimeUtil.getCurrentTime());
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("UPDATE %s SET %s=? ,%s=?, %s=? , %s=? WHERE %s=?",
						DBConstants.T_NOTE, DBConstants.F_NAME, DBConstants.F_CONTENT, DBConstants.F_WITH_TODOS, DBConstants.F_UPDATE_TIME, DBConstants.F_ID);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setString(1, note.getName());
					ps.setString(2, note.getContent());
					ps.setBoolean(3, note.getWithTodos());
					ps.setTimestamp(4, new Timestamp(note.getUpdateTime().getTimeInMillis()));
					ps.setLong(5, note.getId());
					long modifedNum = ps.executeUpdate();
					assert modifedNum == 1;
				}
			});
			trans.commit();
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}
	
	
	@Override
	public List<NoteBook> selectBooksByOwner(long ownerId) throws DBException {
		return selectEntitiesByField(NoteBook.class, DBConstants.F_OWNER_ID, ownerId, sessionFactory);
	}

	@Override
	public void deleteExistedNoteBook(long bookId) throws DBException {
		deleteEntity(NoteBook.class, bookId, sessionFactory);
	}

	@Override
	public void deleteExistedNote(long noteId) throws DBException {
		deleteEntity(Note.class, noteId, sessionFactory);
	}
	
	
	@Override
	public List<Note> selectNoteInfosByBookAndImportant(long noteBookId, boolean important) throws DBException {
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s FROM %s WHERE %s=? and %s=?",
						DBConstants.F_ID, DBConstants.F_NAME, DBConstants.F_WITH_TODOS, DBConstants.T_NOTE, DBConstants.F_NOTE_BOOK_ID, DBConstants.F_IMPORTANT);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setLong(1, noteBookId);
					ps.setBoolean(2, important);
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note note = new Note();
						note.setId(rs.getLong(1));
						note.setName(rs.getString(3));
						note.setWithTodos(rs.getBoolean(4));
						/*为了保持一致 set上 虽然理论上讲 上层不该用到*/
						note.setImportant(important);
						note.setNoteBookId(noteBookId);
						rlt.add(note);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}
	
	@Override
	public List<Note> selectNoteInfosByBook(long noteBookId) throws DBException {
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s,%s,%s,%s FROM %s WHERE %s=?",
						DBConstants.F_ID, DBConstants.F_NAME, DBConstants.F_WITH_TODOS, DBConstants.F_IMPORTANT, DBConstants.F_HIDDEN , DBConstants.F_UPDATE_UTC, DBConstants.T_NOTE, DBConstants.F_NOTE_BOOK_ID);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setLong(1, noteBookId);
					
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note note = new Note();
						note.setId(rs.getLong(1));
						note.setName(rs.getString(2));
						note.setWithTodos(rs.getBoolean(3));
						note.setImportant(rs.getBoolean(4));
						note.setHidden(rs.getBoolean(5));
						note.setUpdateUtc(rs.getLong(DBConstants.F_UPDATE_UTC));
						note.setNoteBookId(noteBookId);
						rlt.add(note);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}

	@Override
	public Long countNotesByBook(long noteBookId) throws DBException {
		return countEntitiesByField(Note.class, DBConstants.F_NOTE_BOOK_ID, noteBookId, sessionFactory);
	}

	@Override
	public boolean includeNotesByBook(long noteBookId) throws DBException {
		return includeEntitiesByField(Note.class, DBConstants.F_NOTE_BOOK_ID, noteBookId, sessionFactory);
	}

	@Override
	public void deleteNotesByBook(long bookId) throws DBException {
		deleteEntitiesByField(Note.class, DBConstants.F_NOTE_BOOK_ID, bookId, sessionFactory);
	}

	@Override
	public List<Note> selectNotesWithIdAndNameByIds(List<Long> ids) throws DBException {
		if(ids.size() == 0) {
			return new ArrayList<>();
		}
		
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = ids.stream().map(val -> val.toString()).collect(Collectors.joining(","));
				String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
						DBConstants.F_ID, DBConstants.F_NAME,
						DBConstants.T_NOTE, DBConstants.F_ID,theManySql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note one =new Note();
						one.setId(rs.getLong(1));
						one.setName(rs.getString(2));
						rlt.add(one);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}

	@Override
	public List<NoteBook> selectBooksWithIdAndNameByIds(List<Long> ids) throws DBException {
		if(ids.size() == 0) {
			return new ArrayList<>();
		}
		
		List<NoteBook> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = sessionFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = ids.stream().map(val -> val.toString()).collect(Collectors.joining(","));
				String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
						DBConstants.F_ID, DBConstants.F_NAME,
						DBConstants.T_NOTE_BOOK, DBConstants.F_ID,theManySql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						NoteBook one =new NoteBook();
						one.setId(rs.getLong(1));
						one.setName(rs.getString(2));
						rlt.add(one);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBException(trans, session, e);
		}
	}




}
