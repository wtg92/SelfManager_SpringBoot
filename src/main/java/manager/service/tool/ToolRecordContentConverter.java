package manager.service.tool;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import manager.data.tool.ToolRecordContent;
import manager.entity.general.tool.ToolRecord;
import manager.exception.LogicException;
import manager.system.SelfXErrors;

/**
 *  <rec>
 *  	<history></history>
 *  </rec>
 *  
 */
public abstract class ToolRecordContentConverter {
	/**
	 * 简写 T tag
	 *    A attribution
	 *    AE attribution for entity
	 *    AP attribution prefix
	 */
	private final static String T_RECORD = "rec";
	
	private final static String T_HISTORY = "his";
	
	private final static String A_VERSION = "version";
	private final static String A_SUC_COUNT = "suc_count";
	private final static String A_FAIL_COUNT = "fail_count";
	private final static String A_TOOL="tool";
	
	private static Document initToolRecord() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(T_RECORD);
		root.addElement(T_HISTORY);
		return doc;
	}
	
	public static void initContent(ToolRecord record) throws LogicException {
		assert record.getContent() == null;
		Document doc = initToolRecord();
		Element ele = doc.getRootElement();
		ele.addAttribute(A_VERSION, "0.1");
		ele.addAttribute(A_SUC_COUNT, "0");
		ele.addAttribute(A_FAIL_COUNT, "0");
		/*For check*/
		ele.addAttribute(A_TOOL, String.valueOf(record.getTool().getDbCode()));
		record.setContent(doc.asXML());
	}
	
	public static void addRecordSucOnce(ToolRecord record) throws LogicException {
		Document doc = getDefinateDocument(record);
		Element root =  doc.getRootElement();
		int src = Integer.parseInt(root.attributeValue(A_SUC_COUNT));
		root.addAttribute(A_SUC_COUNT, String.valueOf(src+1));
		record.setContent(doc.asXML());
	}
	
	public static void addRecordFailOnce(ToolRecord record) throws LogicException {
		Document doc = getDefinateDocument(record);
		Element root =  doc.getRootElement();
		int src = Integer.parseInt(root.attributeValue(A_FAIL_COUNT));
		root.addAttribute(A_FAIL_COUNT, String.valueOf(src+1));
		record.setContent(doc.asXML());
	}
	
	
	private static Document getDefinateDocument(ToolRecord one) throws LogicException {
		try {
			return DocumentHelper.parseText(one.getContent());
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new LogicException(SelfXErrors.TOOL_RECORD_DOC_ERROR,"解析xml 失败 "+one.getId());
		}
		
	}
	public static ToolRecordContent convertToolRecord(ToolRecord one) throws LogicException {
		Document doc = getDefinateDocument(one);
		Element root =  doc.getRootElement();
		ToolRecordContent content = new ToolRecordContent();
		content.sucCount = Integer.parseInt(root.attributeValue(A_SUC_COUNT));
		content.failCount = Integer.parseInt(root.attributeValue(A_FAIL_COUNT));
		content.version = root.attributeValue(A_VERSION);
		
		return content;
	}
}
