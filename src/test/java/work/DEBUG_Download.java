package work;

import org.junit.Test;

import java.time.ZoneId;

public class DEBUG_Download {
	
	
	@Test
	public void test1() {
		ZoneId.getAvailableZoneIds().stream().filter(one->one.contains("Asia")).forEach(one->{
			System.out.println(one);
		});
	}
	
}
