package manager.logic;

import org.junit.Test;

import manager.logic.sub.TagCalculator;

public class DEBUG_TagCalculator {
	
	@Test
	public void testTagParse() {
		System.out.println(TagCalculator.parseTo("^^^我靠"));
	}
	
}
