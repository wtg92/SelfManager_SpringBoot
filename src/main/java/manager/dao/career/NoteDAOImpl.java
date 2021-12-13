package manager.dao.career;

import static manager.util.DBUtil.countEntitiesByField;
import static manager.util.DBUtil.deleteEntitiesByField;
import static manager.util.DBUtil.deleteEntity;
import static manager.util.DBUtil.getHibernateSessionFactory;
import static manager.util.DBUtil.includeEntitiesByField;
import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.processDBExcpetion;
import static manager.util.DBUtil.selectEntitiesByField;
import static manager.util.DBUtil.selectEntity;
import static manager.util.DBUtil.selectUniqueEntityByField;
import static manager.util.DBUtil.updateExistedEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import manager.entity.general.career.Memo;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.SMDB;
import manager.util.TimeUtil;

public class NoteDAOImpl implements NoteDAO {
	
	
	private final SessionFactory hbFactory = getHibernateSessionFactory();
	
	@Override
	public int insertNoteBook(NoteBook book) throws DBException {
		return insertEntity(book, hbFactory);
	}

	@Override
	public int insertNote(Note note) throws DBException {
		return insertEntity(note, hbFactory);
	}

	@Override
	public int insertMemo(Memo memo) throws DBException {
		return insertEntity(memo, hbFactory);
	}

	@Override
	public NoteBook selectNoteBook(int id) throws NoSuchElement, DBException {
		return selectEntity(id,NoteBook.class, hbFactory);
	}

	@Override
	public Note selectNote(int id) throws NoSuchElement, DBException {
		return selectEntity(id,Note.class, hbFactory);
	}

	@Override
	public Memo selectMemoByOwner(int ownerId) throws NoSuchElement, DBException {
		return selectUniqueEntityByField(Memo.class, SMDB.F_OWNER_ID, ownerId, hbFactory);
	}
	
	@Override
	public void updateExistedNoteBook(NoteBook book) throws DBException {
		updateExistedEntity(book, hbFactory);
		
	}

	@Override
	public void updateExistedNote(Note note) throws DBException {
		updateExistedEntity(note, hbFactory);
	}
	
	@Override
	public void updateExistedMemo(Memo memo) throws DBException {
		updateExistedEntity(memo, hbFactory);
	}
	
	@Override
	public void updateNoteNameAndContentAndWithTodos(Note note) throws DBException {
		Session session = null;
		Transaction trans = null;
		note.setUpdateTime(TimeUtil.getCurrentTime());
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("UPDATE %s SET %s=? ,%s=?, %s=? , %s=? WHERE %s=?",
						SMDB.T_NOTE,SMDB.F_NAME,SMDB.F_CONTENT,SMDB.F_WITH_TODOS,SMDB.F_UPDATE_TIME,SMDB.F_ID);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setString(1, note.getName());
					ps.setString(2, note.getContent());
					ps.setBoolean(3, note.getWithTodos());
					ps.setTimestamp(4, new Timestamp(note.getUpdateTime().getTimeInMillis()));
					ps.setInt(5, note.getId());
					int modifedNum = ps.executeUpdate();
					assert modifedNum == 1;
				}
			});
			trans.commit();
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	
	@Override
	public List<NoteBook> selectBooksByOwner(int ownerId) throws DBException {
		return selectEntitiesByField(NoteBook.class, SMDB.F_OWNER_ID, ownerId, hbFactory);
	}

	@Override
	public void deleteExistedNoteBook(int bookId) throws DBException {
		deleteEntity(NoteBook.class, bookId, hbFactory);
	}

	@Override
	public void deleteExistedNote(int noteId) throws DBException {
		deleteEntity(Note.class, noteId, hbFactory);
	}
	
	
	@Override
	public List<Note> selectNoteInfosByBookAndImportant(int noteBookId, boolean important) throws DBException {
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s FROM %s WHERE %s=? and %s=?",
						SMDB.F_ID,SMDB.F_NAME,SMDB.F_WITH_TODOS,SMDB.T_NOTE,SMDB.F_NOTE_BOOK_ID,SMDB.F_IMPORTANT);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, noteBookId);
					ps.setBoolean(2, important);
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note note = new Note();
						note.setId(rs.getInt(1));
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
			throw processDBExcpetion(trans, session, e);
		}
	}
	
	@Override
	public List<Note> selectNoteInfosByBook(int noteBookId) throws DBException {
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String sql = String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s=?",
						SMDB.F_ID,SMDB.F_NAME,SMDB.F_WITH_TODOS,SMDB.F_IMPORTANT,SMDB.T_NOTE,SMDB.F_NOTE_BOOK_ID);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, noteBookId);
					
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note note = new Note();
						note.setId(rs.getInt(1));
						note.setName(rs.getString(2));
						note.setWithTodos(rs.getBoolean(3));
						note.setImportant(rs.getBoolean(4));
						note.setNoteBookId(noteBookId);
						rlt.add(note);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}

	@Override
	public Long countNotesByBook(int noteBookId) throws DBException {
		return countEntitiesByField(Note.class, SMDB.F_NOTE_BOOK_ID, noteBookId, hbFactory);
	}

	@Override
	public boolean includeNotesByBook(int noteBookId) throws DBException {
		return includeEntitiesByField(Note.class, SMDB.F_NOTE_BOOK_ID, noteBookId, hbFactory);
	}

	@Override
	public void deleteNotesByBook(int bookId) throws DBException {
		deleteEntitiesByField(Note.class, SMDB.F_NOTE_BOOK_ID, bookId, hbFactory);
	}

	@Override
	public List<Note> selectNotesWithIdAndNameByIds(List<Integer> ids) throws DBException {
		if(ids.size() == 0) {
			return new ArrayList<>();
		}
		
		List<Note> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = ids.stream().map(val -> val.toString()).collect(Collectors.joining(","));
				String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
						SMDB.F_ID,SMDB.F_NAME,
						SMDB.T_NOTE,SMDB.F_ID,theManySql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Note one =new Note();
						one.setId(rs.getInt(1));
						one.setName(rs.getString(2));
						rlt.add(one);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}

	@Override
	public List<NoteBook> selectBooksWithIdAndNameByIds(List<Integer> ids) throws DBException {
		if(ids.size() == 0) {
			return new ArrayList<>();
		}
		
		List<NoteBook> rlt = new ArrayList<>();
		Session session = null;
		Transaction trans = null;
		try {
			session = hbFactory.getCurrentSession();
			trans = session.beginTransaction();
			session.doWork(conn -> {
				String theManySql = ids.stream().map(val -> val.toString()).collect(Collectors.joining(","));
				String sql = String.format("SELECT %s,%s FROM %s WHERE (%s in (%s))",
						SMDB.F_ID,SMDB.F_NAME,
						SMDB.T_NOTE_BOOK,SMDB.F_ID,theManySql);
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						NoteBook one =new NoteBook();
						one.setId(rs.getInt(1));
						one.setName(rs.getString(2));
						rlt.add(one);
					}
				}
			});
			trans.commit();
			return rlt;
		} catch (Exception e) {
			throw processDBExcpetion(trans, session, e);
		}
	}




}
