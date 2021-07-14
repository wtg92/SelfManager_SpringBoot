package manager.util;

import static manager.util.CacheConverter.createRsVal;
import static manager.util.CacheConverter.parseRVal;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CacheConverterTest {
	
	@Test
	public void testRVal() {
		List<Integer> src1 = Arrays.asList(1,2,3,4,5);
		List<Integer> src2 = Collections.emptyList();
		
		List<Integer> src1AfterConvert = parseRVal(createRsVal(src1));
		List<Integer> src2AfterConvert = parseRVal(createRsVal(src2));
		
		assertTrue(CommonUtil.equalsOfElements(src1, src1AfterConvert));
		assertTrue(CommonUtil.equalsOfElements(src2, src2AfterConvert));
	}
	
	
}
