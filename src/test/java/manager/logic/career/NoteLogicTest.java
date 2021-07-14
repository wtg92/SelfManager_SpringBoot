package manager.logic.career;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import manager.TestUtil;
import manager.entity.general.career.Note;
import manager.entity.general.career.NoteBook;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.logic.UserLogic;
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
			assertEquals(SMError.EDIT_NOTEBOOK_ERROR, e.type);
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
			assertEquals(SMError.EDIT_NOTEBOOK_ERROR, e.type);
		}
		
		Note noteEntity = nL.loadNote(1, 1).note;
		assertEquals(noteName, noteEntity.getName());
		assertTrue(noteEntity.isRoot());
		
		assert 2 == nL.createNote(1, 2, noteName);
		noteEntity = nL.loadNote(1, 2).note;
		assertEquals(noteName, noteEntity.getName());
		assertTrue(noteEntity.isRoot());
		
		noteEntity = nL.loadNote(1, 1).note;
		assertFalse(noteEntity.isRoot());
		assertTrue(2 == noteEntity.getPrevNoteId());
		
		assert 3 == nL.createNote(1, 2, noteName);
		
		/*最后一个放到了第一个*/
		nL.saveNotesSeq(1, 1, 0);
		
		noteEntity = nL.loadNote(1, 1).note;
		assertTrue(noteEntity.isRoot());
		noteEntity = nL.loadNote(1, 2).note;
		assertTrue(3 == noteEntity.getPrevNoteId());
		noteEntity = nL.loadNote(1, 3).note;
		assertTrue(1 == noteEntity.getPrevNoteId());
		
	}
	
	
	@Test
	public void testNotesSeq() throws Exception{
		UserLogic uL = UserLogic.getInstance();
		NoteLogic nL = NoteLogic.getInstance();
		
		/*表明admin已经存在 之后可以直接用userId==1了*/
		uL.getUser(1);
		
		String name="笔记本";
		String note="随便写点备注";
		BookStyle style = BookStyle.CYAN_BLUE;
		assert 1 == nL.createNoteBook(1, name, note,style);
		
		assert 1 == nL.createNote(1, 1, "xx");
		assert 2 == nL.createNote(1, 1, "xx");
		assert 3 == nL.createNote(1, 1, "xx");
		assert 4 == nL.createNote(1, 1, "xx");
		assert 5 == nL.createNote(1, 1, "xx");
		
		/*1->2->3->4->5*/
		List<Note> notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		/*1->3->4->2->5*/
		nL.saveNotesSeq(1, 2, 5);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 5, 1);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 1, 5);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 5, 1);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 3, 2);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 2, 3);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		assert 6 == nL.createNote(1, 1, "www");
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.deleteNote(1, 1);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.deleteNote(1, 6);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		/*现在只剩下 2345*/
		nL.saveNotesSeq(1, 2, 3);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 4, 5);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		
		nL.saveNotesSeq(1, 5, 4);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 3, 5);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNotesSeq(1, 2, 3);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNoteImportant(1, 2, true);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNoteImportant(1, 2, false);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNoteImportant(1, 3, true);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNoteImportant(1, 4, true);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.saveNoteImportant(1, 2, true);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		assert 7 == nL.createNote(1, 1, "www");
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
		
		nL.deleteNote(1, 3);
		notes = nL.loadBookContent(1, 1).notes;
		testSeqLegal(notes);
	}
	
	private static void testSeqLegal(List<Note> notesWithImportantAndNotImportant) {
		notesWithImportantAndNotImportant.stream().collect(partitioningBy(Note::getImportant)).values().forEach(notes->{
		    List<Note> afterCalculatedSeq = new ArrayList<>();
		    Map<String,Integer> curId = new HashMap<String, Integer>();
		    curId.put("id", 0);
		    Note nextNode;
		    while((nextNode=notes.stream().filter(n->n.getPrevNoteId()== curId.get("id")).findAny().orElse(null))!=null){
		    	afterCalculatedSeq.add(nextNode);
		        curId.put("id", nextNode.getId());
		    }
		    try {
		    	 assertEquals(notes.size(), afterCalculatedSeq.size());
		    }catch (AssertionError e) {
		    	CommonUtil.printList(notes);
		    	CommonUtil.printList(afterCalculatedSeq);
		    	throw e;
		    }
		   
		});
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
