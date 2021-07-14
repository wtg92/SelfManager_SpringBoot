package manager.util;

import java.util.Calendar;

import org.junit.Test;

public class TimeUtilTest {
	
	@Test
	public void testGeneral() {
		
		Calendar now = TimeUtil.getCurrentTime();
		Calendar copy = TimeUtil.copy(now);
		
		assert TimeUtil.isSameByTime(now, copy);
		assert TimeUtil.isSameByDate(now, copy);
		
		copy.add(Calendar.MINUTE, 1);
		
		assert !TimeUtil.isSameByTime(now, copy);
		assert TimeUtil.isSameByDate(now, copy);
		assert TimeUtil.isBeforeByTime(now, copy);
		
		now = TimeUtil.parseDate(TimeUtil.parseDate(now));
		copy = TimeUtil.parseDate(TimeUtil.parseDate(copy));
		
		assert TimeUtil.isSameByTime(now, copy);
		assert TimeUtil.isSameByDate(now, copy);
	}
	
}
