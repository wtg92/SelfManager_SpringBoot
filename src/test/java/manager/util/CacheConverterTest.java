package manager.util;

import static manager.cache.CacheConverter.createRsVal;
import static manager.cache.CacheConverter.parseRVal;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CacheConverterTest {
	
	@Test
	public void testRVal() {
		List<Long> src1 = Arrays.asList((long)1,(long)2,(long)3,(long)4,(long)5);
		List<Long> src2 = Collections.emptyList();
		
		List<Long> src1AfterConvert = parseRVal(createRsVal(src1));
		List<Long> src2AfterConvert = parseRVal(createRsVal(src2));
		
		assertTrue(CommonUtil.equalsOfElements(src1, src1AfterConvert));
		assertTrue(CommonUtil.equalsOfElements(src2, src2AfterConvert));
	}
	
	
}
