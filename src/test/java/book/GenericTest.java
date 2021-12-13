package book;

import java.util.function.Function;

public class GenericTest {
	
	
	public void testVar() {
		var varDemo = new GenericTest();
	}
	
	
	public<T> T selectBestOne(T t1,T t2,T t3,Function<T,Integer> judger) {
		int score1 = judger.apply(t1);
		int score2 = judger.apply(t2);
		int score3 = judger.apply(t3);
		if(score1 >= score2 && score1 >= score3) {
			return t1;
		}
		if(score2 >= score1 && score2 >= score3) {
			return t2;
		}
		return t3;
	}
	
	public static class Apple{
		int smellGoodLevel;
	}
	
	public static class People{
		int comfortableLevel;
	}
	
	public void selectBestAppleAndSelectBestCompanion(){
		
		/*我的面前摆着三个苹果*/
		Apple a1 = new Apple();
		Apple a2 = new Apple();
		Apple a3 = new Apple();
		
		Apple theSweetest = selectBestOne(a1, a2, a3,
				/*闻起来香的苹果，我就认为更甜*/
				(apple)->apple.smellGoodLevel);
		
		/*我吃到了最甜的苹果，我很开心*/
		eatAndHappyForMe(theSweetest);
		
		/*有三个好看的人对我有好感*/
		People p1 = new People();
		People p2 = new People();
		People p3 = new People();
		
		People theMostSuitable = selectBestOne(p1, p2, p3,
				/*相处更舒服的人，我认为是更适合的*/
				(people)->people.comfortableLevel);
		
		/*我和最合适的人在一起了，我很开心*/
		accompanyAndHappyForMe(theMostSuitable);
		
	}


	
	
	
	
	private void accompanyAndHappyForMe(People theMostSuitable) {
		// TODO Auto-generated method stub
		
	}


	private void eatAndHappyForMe(Apple theSweetest) {
		// TODO Auto-generated method stub
		
	}


	private Apple getApple() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
