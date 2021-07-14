package manager.util;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public abstract class XMLUtil {
	
	/*找出本身+儿子+孙子 符合tagName的elements*/
	public static List<Element> findAllByTag(Element ele,String tagName){
		List<Element> rlt = new ArrayList<Element>();

		if(ele.getName().equals(tagName)) {
			rlt.add(ele);
		}
		
		for(Element node : ele.elements()) {
			rlt.addAll(findAllByTag(node, tagName));
		}
		return rlt;
	}
	
	/*为了remove简单*/
	public static class ElementWithFather{
		public Element father;
		public Element cur;
		
		public void removeCurFromFather() {
			if(father == null)
				throw new RuntimeException("无father的不该removeCur");
			
			boolean success = father.remove(cur);
			if(!success)
				throw new RuntimeException("remove Cur失败");
		}
		
		public ElementWithFather(Element father, Element cur) {
			super();
			this.father = father;
			this.cur = cur;
		}
		
	}
	
	private static List<ElementWithFather> findAllByTagWithFather(Element ele,String tagName,Element father){
		List<ElementWithFather> rlt = new ArrayList<>();

		if(ele.getName().equals(tagName)) {
			rlt.add(new ElementWithFather(father,ele));
		}
		
		for(Element node : ele.elements()) {
			rlt.addAll(findAllByTagWithFather(node, tagName,ele));
		}
		return rlt;
	}
	
	public static List<ElementWithFather> findAllByTagWithFather(Element ele,String tagName){
		return findAllByTagWithFather(ele, tagName, null);
	}
	
	
}
