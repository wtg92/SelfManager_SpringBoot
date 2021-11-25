package manager.logic.career;

import org.junit.Test;

import manager.data.career.BookContent;
import manager.exception.DBException;
import manager.exception.LogicException;

public class DEBUG_NoteLogic {
	
	@Test
	public void detectDeleteNote() throws Exception {
		NoteLogic nL = NoteLogic.getInstance();
		int userId = 1;
		int bookId = 18;
		BookContent content =  nL.loadBookContent(userId, bookId);
		content.notes.forEach(note->{
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
		new Thread(()->{
			try {
				nL.saveNotesSeq(userId, finalContent.notes.get(5).getNoteBookId(), finalContent.notes.get(3).getNoteBookId());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
			}
		}).start();
		new Thread(()->{
			try {
				nL.saveNotesSeq(userId, finalContent.notes.get(2).getNoteBookId(), finalContent.notes.get(1).getNoteBookId());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
			}
		}).start();
		
		new Thread(()->{
			try {
				nL.saveNotesSeq(userId, finalContent.notes.get(0).getNoteBookId(), finalContent.notes.get(3).getNoteBookId());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
			}
		}).start();
		
		new Thread(()->{
			try {
				nL.saveNotesSeq(userId, finalContent.notes.get(5).getNoteBookId(), finalContent.notes.get(8).getNoteBookId());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
			}
		}).start();
		
		new Thread(()->{
			try {
				nL.saveNotesSeq(userId, finalContent.notes.get(1).getNoteBookId(), finalContent.notes.get(7).getNoteBookId());
			}catch(Exception e) {
				e.printStackTrace();
				assert false;
			}
		}).start();
		
		content.notes.forEach(note->{
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
