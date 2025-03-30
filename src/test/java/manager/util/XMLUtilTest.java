package manager.util;
import static manager.util.XMLUtil.*;
import static org.junit.Assert.assertEquals;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;


public class XMLUtilTest {

	
	@Test
	public void testCommon() throws DocumentException {
		Document doc = DocumentHelper.createDocument();
		Element e1 = doc.addElement("root");
		Element e3 = e1.addElement("a");
		Element e2 = e1.addElement("a");

		e2.addElement("a");
		
		assertEquals(e1.elements().size(), e1.content().size());
		assertEquals(e3.elements().size(), e3.content().size());
		assertEquals(e2.elements().size(), e2.content().size());
		assertEquals(3, findAllByTagWithFather(e1,"a").size());
		assertEquals(doc.getRootElement(), findAllByTagWithFather(e1,"a").get(0).father);
		
//		String str = e1.asXML();
//		Document doc2 = DocumentHelper.parseText(str);
//		Element node = (Element)doc.selectSingleNode("/aa/nn");
//		System.out.println(node.asXML());
		
	}
	
	@Test
	public void testAPI() throws Exception{
		Document doc = DocumentHelper.createDocument();
		Element e1 = doc.addElement("root");
		Element e3 = e1.addElement("a");
		e3.setText("haha");
		System.out.println(e1.elementText("a"));
		System.out.println(e3.element("null"));
	}
	
	
	
}
