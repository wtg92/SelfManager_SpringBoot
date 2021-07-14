package book;

import java.util.Calendar;

import manager.util.TimeUtil;


public class Test {
	
	
	
	public static String sayFromMyHeart(String yourWords) {
		
		if("I love u".equals(yourWords)){
			return "I love u too";
		}
		
		if(yourWords.contains("I") && yourWords.contains("love") && yourWords.contains("u")){
			return "I love u too";
		}
		
		if(yourWords != null){
			return "I love u too";
		}
		
		return "I love u,but u will never konw this";
		
	}
	
	
	public static class A implements Cloneable{
		int a;
		Calendar createTime;
		Calendar updateTime;
		
		@Override
		protected A clone() {
			try {
				return (A)super.clone();
			}catch(CloneNotSupportedException e) {
				throw new RuntimeException(e.getMessage());
			}
			
		}
	}
	
	public static interface DAO{
		Long insertA(A a);
	}
	
	private DAO dao;
	
	/*逻辑层 Service*/
	public void insertA(A a) {
		A src = a.clone();
		src.createTime = TimeUtil.getCurrentTime();
		src.updateTime = TimeUtil.getCurrentTime();
		dao.insertA(a);
	}
	
	
	public static class B{
		int v1;
		int v2;
	}
	
	@org.junit.Test
	public void loopExample() {
		
		int sumOfEven = 0;
		for(int i=0; i<10 ; i++) {
			if(i%2==0) {
				sumOfEven += i;
			}
		}
		
		int sumOfOdd = 0;
		for(int i=0; i<10 ; i++) {
			if(i%2==1) {
				sumOfOdd += i;
			}
		}
		
		System.out.println(sumOfOdd+":"+sumOfEven);
	}
	
	
	@org.junit.Test
	public void findMaxEvenIndex() {
		
		int maxEvenIndex = 0;

		for(int i=0;i<10;i++) {
			
			if(i%2 == 0) {
				maxEvenIndex = i;
			}
		}
		
		System.out.println(maxEvenIndex);
	}
	
	
	
	
}
