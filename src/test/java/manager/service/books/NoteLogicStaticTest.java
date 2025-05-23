package manager.service.books;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import manager.data.worksheet.MemoContent;
import manager.entity.general.career.Memo;
import manager.entity.virtual.worksheet.MemoItem;
import manager.system.career.NoteLabel;

public class NoteLogicStaticTest {
	
	
	@Test
	public void testContentCoverter() throws Exception{
		Memo one = new Memo();
		String content = "xx";
		NoteLabel label = NoteLabel.SPEC_NULL;
		String note = "note..";
		Long srcNoteId = (long)0;
		String srcNoteName = "";
		Long srcBookId =(long)0;
		String srcBookName = "";
		
		NoteContentConverter.addItemToMemo(one, content, label, note, srcNoteId, srcNoteName,srcBookId,srcBookName);
		
		MemoContent rlt = NoteContentConverter.convertMemo(one);
		assertEquals(1, rlt.items.size());
		MemoItem target  = rlt.items.get(0).item;
		
		assertTrue(1 == target.getId());
		
		assertEquals(content,target.getContent());
		assertEquals(label,target.getLabel());
		assertEquals(note,target.getNote());
		assertEquals(srcNoteId,target.getSrcNoteId());
		assertEquals(srcNoteName,target.getSrcNoteName());
		
		content = "changed";
		note = "lk";
		
		NoteContentConverter.updateMemoItem(one, 1, content, label, note);
		rlt = NoteContentConverter.convertMemo(one);
		assertEquals(1, rlt.items.size());
		target  = rlt.items.get(0).item;
		
		assertEquals(content,target.getContent());
		assertEquals(label,target.getLabel());
		assertEquals(note,target.getNote());
		assertEquals(srcNoteId,target.getSrcNoteId());
		assertEquals(srcNoteName,target.getSrcNoteName());
		
		NoteContentConverter.removeItemFromMemo(one, 1);
		rlt = NoteContentConverter.convertMemo(one);
		assertEquals(0, rlt.items.size());
	}
}
