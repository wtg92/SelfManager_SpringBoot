package manager.logic.career.sub;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import manager.data.career.MemoContent;
import manager.data.proxy.career.MemoItemProxy;
import manager.entity.general.career.Memo;
import manager.entity.virtual.career.MemoItem;
import manager.exception.LogicException;
import manager.system.SMError;
import manager.system.career.NoteLabel;

/**
  *  处理Memo的解析及生成  
 * parse append 一一对应，当修改了某一属性 应当同时调整相关的两个函数 
 *  
 *  
 *  Memo:
 *  <mm>
 *  	<items p_key>
 *            同note 同labelName 不允许重复 只有一层
 *            <item>
 *           	<note>
 *            	</note>
 *            </item>
 *  	</items>
 *  </mm>
 *  
 */
public abstract class NoteContentConverter {
	/**
	 * 简写 T tag
	 *    A attribution
	 *    AE attribution for entity
	 *    AP attribution prefix
	 */
	private final static String T_MEMO = "mm";
	
	private final static String T_ITEMS = "its";
	private final static String T_ITEM = "it";
	private final static String T_NOTE = "nt";
	
	private final static String A_P_AUTO_INCREMENT_KEY = "p_key";
	private final static String A_ID = "id";
	private final static String A_SRC_NOTE_ID = "src_n_id";
	private final static String A_SRC_NOTE_NAME = "src_n_n";
	private final static String A_SRC_BOOK_ID = "src_b_id";
	private final static String A_SRC_BOOK_NAME = "src_b_n";
	private final static String A_LABEL = "lbl";
	
	private final static int PRIMARY_KEY_INITIAL_VAL = 1;
	
	
	private static Document initMemo() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(T_MEMO);
		
		Element item =  root.addElement(T_ITEMS);
		item.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(PRIMARY_KEY_INITIAL_VAL));
		
		return doc;
	}
	
	private static MemoItem parseMemoItem(Element element){
		MemoItem item = new MemoItem();
		item.setId(Integer.parseInt(element.attributeValue(A_ID)));
		item.setContent(element.getText());
		item.setSrcNoteId(Long.parseLong(element.attributeValue(A_SRC_NOTE_ID)));
		item.setSrcNoteName(element.attributeValue(A_SRC_NOTE_NAME));
		item.setSrcBookId(Long.parseLong(element.attributeValue(A_SRC_BOOK_ID)));
		item.setSrcBookName(element.attributeValue(A_SRC_BOOK_NAME));
		item.setLabel(NoteLabel.valueOfName(element.attributeValue(A_LABEL)));
		item.setNote(element.elementText(T_NOTE));
		return item;
	}
	
	
	private static int getPIdAndAutoIncrease(Element father) {
		int pId = Integer.parseInt(father.attributeValue(A_P_AUTO_INCREMENT_KEY));
		father.addAttribute(A_P_AUTO_INCREMENT_KEY, String.valueOf(pId+1));
		return pId;
	}
	
	private static List<MemoItem> getMemoItems(Document doc) {
		Element items= doc.getRootElement().element(T_ITEMS);
		List<MemoItem> rlt = new ArrayList<>();
		for(Element ele : items.elements(T_ITEM)) {
			MemoItem item = parseMemoItem(ele);
			rlt.add(item);
		}
		return rlt;
	}
	
	private static Element getMemoItemById(Document doc, int itemId) throws LogicException {
		Element itemsElement= doc.getRootElement().element(T_ITEMS);
		
		for(Element item:itemsElement.elements()) {
			if(Integer.parseInt(item.attributeValue(A_ID))!=itemId) {
				continue;
			}
			return item;
		}
		throw new LogicException(SMError.MEMO_DOC_ERROR,"memo 无法匹配id "+itemId+"\n"+doc.asXML());
	}
	
	private static Document getDocumentOrInitIfNotExists(Memo one) throws LogicException {
		if(one.getContent() == null || one.getContent().length() == 0) {
			return initMemo();
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.MEMO_DOC_ERROR,"解析xml 失败 "+one.getId());
		}
	}
	
	private static Document getDefinateDocument(Memo one) throws LogicException {
		if(one.getContent() == null) {
			throw new LogicException(SMError.MEMO_DOC_ERROR,"无content的memo"+one.getId());
		}
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SMError.MEMO_DOC_ERROR,"解析xml 失败 "+one.getId());
		}
	}
	
	
	private static void fillAttrsExceptId(MemoItem item,Element cur) {
		cur.setText(item.getContent());
		cur.addAttribute(A_SRC_NOTE_ID, item.getSrcNoteId().toString());
		cur.addAttribute(A_SRC_NOTE_NAME, item.getSrcNoteName());
		cur.addAttribute(A_SRC_BOOK_ID, item.getSrcBookId().toString());
		cur.addAttribute(A_SRC_BOOK_NAME, item.getSrcBookName());
		cur.addAttribute(A_LABEL, item.getLabel().getName());
		
		Element note = cur.element(T_NOTE);
		if(note == null) {
			note = cur.addElement(T_NOTE);
		}
		
		note.setText(item.getNote());
	}
	
	static MemoItem append(MemoItem item,Element itemsEle) {
		assert itemsEle.getName().equals(T_ITEMS);
		int pId = getPIdAndAutoIncrease(itemsEle);
		
		Element cur = itemsEle.addElement(T_ITEM);
		cur.addAttribute(A_ID, String.valueOf(pId));
		item.setId(pId);
		
		fillAttrsExceptId(item, cur);
		return item;
	}
	/* TODO和DONE视作同一标签  */
	private static void checkMemoItemsContentNoDup(Document doc,String content,NoteLabel label,Long srcNoteId) throws LogicException {
		List<MemoItem> items = getMemoItems(doc);
		if(items.stream().anyMatch(item->{
			boolean sameLabel = item.getLabel() == label;
			if(!sameLabel && (label == NoteLabel.DONE || label == NoteLabel.TODO)) {
				sameLabel = item.getLabel() == NoteLabel.DONE || item.getLabel() == NoteLabel.TODO;
			}
			return item.getContent().equals(content.strip()) && item.getSrcNoteId() == srcNoteId && sameLabel;
		})) {
			throw new LogicException(SMError.MEMO_ITEMS_DUP_ERROR,content);
		}
	}
	
	public static int addItemToMemo(Memo one,String content,NoteLabel label,String note,Long srcNoteId,String srcNoteName,Long srcBookId,String srcBookName) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(one);
		
		checkMemoItemsContentNoDup(doc, content,label, srcNoteId);
		
		Element itemsRoot= doc.getRootElement().element(T_ITEMS);
		
		MemoItem item = new MemoItem();
		
		item.setContent(content);
		item.setLabel(label);
		item.setNote(note);
		item.setSrcNoteId(srcNoteId);
		item.setSrcNoteName(srcNoteName);
		item.setSrcBookId(srcBookId);
		item.setSrcBookName(srcBookName);
		
		append(item,itemsRoot);
		
		one.setContent(doc.asXML());
		
		assert item.getId() !=0;
		return item.getId();
	}
	
	public static void updateMemoItem(Memo one,int itemId,String content,NoteLabel label,String note) throws LogicException{
		Document doc = getDefinateDocument(one);
		Element item = getMemoItemById(doc, itemId);
		MemoItem origin = parseMemoItem(item);
		
		if(!origin.getContent().equals(origin.getContent())) {
			checkMemoItemsContentNoDup(doc, content,label, origin.getSrcNoteId());
		}
		
		origin.setContent(content);
		origin.setLabel(label);
		origin.setNote(note);
		
		fillAttrsExceptId(origin, item);
		
		one.setContent(doc.asXML());
	}
	
	public static void updateMemoItemsSeq(Memo one,List<Integer> idsSeq) throws LogicException{
		Document doc = getDefinateDocument(one);

		Element itemsRoot= doc.getRootElement().element(T_ITEMS);
		
		List<Element> items = new LinkedList<>();
		
		for(int id:idsSeq) {
			Element removeAtFirst = getMemoItemById(doc, id);
			itemsRoot.remove(removeAtFirst);
			items.add(removeAtFirst);
		}
		
		for(Element addAtSecond:items) {
			itemsRoot.add((Element)addAtSecond);
		}
		
		one.setContent(doc.asXML());
	}
	
	public static void updateMemoItemLabel(Memo one,int itemId,NoteLabel label) throws LogicException{
		Document doc = getDefinateDocument(one);
		Element item = getMemoItemById(doc, itemId);
		MemoItem origin = parseMemoItem(item);
		origin.setLabel(label);
		fillAttrsExceptId(origin, item);
		one.setContent(doc.asXML());
	}
	
	public static void removeItemFromMemo(Memo one, int itemId) throws LogicException {
		Document doc = getDefinateDocument(one);
		Element itemsElement= doc.getRootElement().element(T_ITEMS);
		Element ele = getMemoItemById(doc, itemId);
		
		boolean success = itemsElement.remove(ele);
		if(!success)
			throw new LogicException(SMError.UNEXPCETED_OP_ERROR_FOR_MEMO,"删除失败");
		
		one.setContent(doc.asXML());
	}
	
	public static MemoContent convertMemo(Memo one) throws LogicException {
		Document doc = getDocumentOrInitIfNotExists(one);
		
		MemoContent rlt = new MemoContent();
		
		rlt.items = getMemoItems(doc).stream().map(MemoItemProxy::new).collect(toList());
		return rlt;
	}
}
