package manager.service.books;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.service.UserLogic;
import manager.system.SMError;
import manager.system.career.BookStyle;
import manager.util.CommonUtil;
import manager.util.TimeUtil;

public class NoteLogicTest {
	
	@Before
	public void setUp() throws Exception {
		TestUtil.initEnvironment();
		TestUtil.initData();
		TestUtil.addAdmin();
		TimeUtil.resetTravel();
	}
	
	@Test
	public void testBasicFlow() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		NoteLogic nL = NoteLogic.getInstance();
		
		/*表明admin已经存在 之后可以直接用userId==1了*/
		uL.getUser(1);
		
		String name="笔记本";
		String note="随便写点备注";
		BookStyle style = BookStyle.CYAN_BLUE;
		assert 1 == nL.createNoteBook(1, name, note,style);
		
		List<NoteBook> books = nL.loadBooks(1).stream().map(proxy->proxy.book).collect(toList());
		
		assert 1 == books.size();
		NoteBook book = books.get(0);
		
		assertEquals(name, book.getName());
		assertEquals(note, book.getNote());
		assertFalse(book.getClosed());
		
		name="笔记本2";
		note="";
		nL.saveNoteBook(1, 1, name, note,style,0);
		
		book = nL.loadBook(1, 1).book;
		
		assertEquals(name, book.getName());
		assertEquals(note, book.getNote());
		assertFalse(book.getClosed());
		
		try {
			nL.deleteNoteBook(1, 1);
			fail();
		}catch(LogicException e) {
			assertEquals(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, e.type);
		}

		nL.closeNoteBook(1, 1);
		
		books = nL.loadBooks(1).stream().map(proxy->proxy.book).collect(toList());
		assertEquals(0, books.size());
		
		assert 2 == nL.createNoteBook(1, name, note,style);
		
		String noteName = "2020年11月8日";
		
		assert 1 == nL.createNote(1, 2, noteName);
		
		try {
			nL.deleteNoteBook(1, 2);
			fail();
		}catch(LogicException e) {
			assertEquals(SMError.CANNOT_EDIT_NOTE_BOOK_OF_OTHERS, e.type);
		}
		
		Note noteEntity = nL.loadNote(1, 1).note;
		assertEquals(noteName, noteEntity.getName());
		
		assert 2 == nL.createNote(1, 2, noteName);
		noteEntity = nL.loadNote(1, 2).note;
		assertEquals(noteName, noteEntity.getName());
		
		noteEntity = nL.loadNote(1, 1).note;
		
		assert 3 == nL.createNote(1, 2, noteName);
		
		noteEntity = nL.loadNote(1, 1).note;
		noteEntity = nL.loadNote(1, 2).note;
		noteEntity = nL.loadNote(1, 3).note;
	}
	
	@Test
	public void testSyncError() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		NoteLogic nL = NoteLogic.getInstance();
		
		/*表明admin已经存在 之后可以直接用userId==1了*/
		uL.getUser(1);
		
		String name="笔记本";
		String note="随便写点备注";
		BookStyle style = BookStyle.CYAN_BLUE;
		assert 1 == nL.createNoteBook(1, name, note,style);
		
		for(int i=0;i<100;i++) {
			assert i+1 == nL.createNote(1, 1, "xx");
			new Thread(()->{
				try {
					nL.loadNote(1, 1);
				} catch (DBException | LogicException e) {
					assert false;
				}
			}).start();
			
			new Thread(()->{
				try {
					nL.saveNote(1, 1, name+CommonUtil.getByRandom(1, 10), "i", false);
				} catch (DBException | LogicException e) {
					assert false;
				}
			}).start();
			
			
		}
	}
	
	
	
	
	
	
	
}
