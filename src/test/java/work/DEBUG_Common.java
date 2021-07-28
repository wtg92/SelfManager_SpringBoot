package work;

import java.util.stream.Stream;

import org.junit.Test;

public class DEBUG_Common {
	
	@Test
	public void testSorted() {
		Stream.of(1,3,2,12,321,2).sorted().forEach(i->{
			System.out.println(i);
		});
	}
	
}
