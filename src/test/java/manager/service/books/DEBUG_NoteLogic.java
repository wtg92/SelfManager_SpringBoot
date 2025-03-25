package manager.service.books;

import org.junit.Test;

import manager.data.worksheet.BookContent;
import manager.exception.DBException;
import manager.exception.LogicException;

public class DEBUG_NoteLogic {
	
	@Test
	public void detectDeleteNote() throws Exception {
		NoteLogic nL = NoteLogic.getInstance();
		int userId = 1;
		int bookId = 18;
		BookContent content =  nL.loadBookContent(userId, bookId);
		content.importantNotes.forEach(note->{
			new Thread(()->{
				try {
					nL.deleteNote(userId, note.getId());
				} catch (DBException | LogicException e) {
					e.printStackTrace();
					assert false;
				}
			}).start();			
		});
		
		
		for(int i=0;i<10;i++) {
			nL.createNote(userId, bookId, "测试"+i);
		}
		
		content =  nL.loadBookContent(userId, bookId);
		
		final BookContent finalContent = content;
		
		content.generalNotes.forEach(note->{
			new Thread(()->{
				try {
					nL.deleteNote(userId, note.getId());
				} catch (DBException | LogicException e) {
					e.printStackTrace();
					assert false;
				}
			}).start();			
		});
		
		
	}
}
