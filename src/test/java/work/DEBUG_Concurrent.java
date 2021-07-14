package work;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import manager.util.CommonUtil;

public class DEBUG_Concurrent {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	
	@Test
	public void test() {
		
		List<Integer> ints = IntStream.range(0, 10000).mapToObj(i->i).collect(Collectors.toList());
		
		scheduler.scheduleAtFixedRate(()->{
			ints.remove(CommonUtil.getByRandom(0, 100));
		}, 0,500,TimeUnit.MICROSECONDS);
		
		ints.forEach(i->{
			
			try {
				System.out.println(i);
			}catch(Exception e) {
				System.out.println("error");
			}

		});
		
		System.out.println("done "+ints.size());
		
		while(true) {}
	}
	
}
