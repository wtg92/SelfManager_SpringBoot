package book;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamTest {
	
	@Test
	public void testFairness() {
		
		/*越南有一位姓货名拉拉的女子发生了一件事*/
		Thing something = appear();
		
		/*事情冲突的双方一开始涉及的是一男一女*/
		assert something.conflictParties.size() == 2;
		assert something.conflictParties.get(0).getClass() == People.class; 
		assert something.conflictParties.get(1).getClass() == People.class;
		
		/*有很多人了解到了这件事*/
		List<People> peopleKnowingTheThing = learnAbout(something);
		
		/*这些人虽然都说越南语，但由于各种原因，对这件事大致产生了三种态度*/
		Map<Position,List<People>> peopleWithSelfPosition = peopleKnowingTheThing
				.stream().collect(Collectors.groupingBy(people->
			people.producePosition(something)
		));
		
		/*漠不关心、男方、女方*/
		assert peopleWithSelfPosition.size() == 3;
		assert peopleWithSelfPosition.containsKey(Position.DONT_CARE);
		assert peopleWithSelfPosition.containsKey(Position.THE_MAN);
		assert peopleWithSelfPosition.containsKey(Position.THE_WOMAN);
		
		/*无关乎持各自态度的人群多少，但事情由此而发酵，冲突程度在加深*/
		long before = something.conflictDegree;
		something.influencedBy(peopleWithSelfPosition);
		long after = something.conflictDegree;
		assert after > before;
		
		/*但任何事情，都存在一个程度阈值*/
		final long limitInFirstLevel = getFirstLevel(something);
		assert after > limitInFirstLevel;

		/*当冲突加深到一个阈值后，相关的一切都发生了变化，即量变引起质变*/
		assert something.conflictParties.size() == 2;
		
		/*不止是冲突双方发生了变化*/
		assert something.conflictParties.get(0).getClass() != People.class; 
		assert something.conflictParties.get(1).getClass() != People.class;
		assert something.conflictParties.get(0).getClass() == Gender.class; 
		assert something.conflictParties.get(1).getClass() == Gender.class;
		
		/*还有牵涉到的不同态度的人*/
		List<People> peopleKnowingTheThingAfterFirstLevel = learnAbout(something);
		Map<Position,List<People>> peopleWithSelfPositionAfterFirstLevel
		= peopleKnowingTheThingAfterFirstLevel
				.stream().collect(Collectors.groupingBy(people->
			people.producePosition(something)
		));
		assert peopleWithSelfPosition.size() == 3;
		assert peopleWithSelfPosition.containsKey(Position.DONT_CARE);
		assert peopleWithSelfPosition.containsKey(Position.MEN);
		assert peopleWithSelfPosition.containsKey(Position.WOMEN);
		
		
		assert peopleKnowingTheThingAfterFirstLevel.size() > peopleKnowingTheThing.size();
		assert peopleWithSelfPositionAfterFirstLevel.get(Position.DONT_CARE).size() >
			peopleWithSelfPosition.get(Position.DONT_CARE).size();
		
		/*如果没有达到公平，则事情冲突的加剧不会停止*/
		before = something.conflictDegree;	
		something.influencedBy(peopleWithSelfPositionAfterFirstLevel);
		after = something.conflictDegree;
		assert after > before;
		
		/*终于，冲突达到了第二个阈值*/
		final long limitInSecondLevel = getSecondLevel(something);
		assert after > limitInSecondLevel;
		
		/*同样，和冲突相关的一切事情又都发生了变化*/
		List<People> peopleKnowingTheThingAfterSecondLevel = learnAbout(something);
		Map<Position,List<People>> peopleWithSelfPositionAfterSecondLevel
		= peopleKnowingTheThingAfterSecondLevel
			.stream().collect(Collectors.groupingBy(people->
			people.producePosition(something)
		));
		
		assert something.conflictParties.get(0).getClass() != People.class; 
		assert something.conflictParties.get(0).getClass() != Gender.class; 
		assert something.conflictParties.get(1).getClass() != People.class; 
		assert something.conflictParties.get(1).getClass() != Gender.class; 
		assert peopleKnowingTheThingAfterSecondLevel.size() > 
			peopleKnowingTheThingAfterFirstLevel.size();
		assert peopleWithSelfPositionAfterSecondLevel.get(Position.DONT_CARE).size() >
			peopleWithSelfPositionAfterFirstLevel.get(Position.DONT_CARE).size();
		
		/*那么，现在是什么样子呢？*/
		/*我知道它还是个对象*/	
		assert something.conflictParties.get(0).getClass().getSuperclass() == Object.class; 
		assert something.conflictParties.get(1).getClass().getSuperclass() == Object.class; 
		
		/*我知道当冲突来到了第三个阈值，我已无法再置身事外*/
		assert !peopleWithSelfPositionAfterSecondLevel.get(Position.DONT_CARE).contains(me);
		
		/*我知道当事情如果不公平，冲突永远在加剧，或隐性或显性*/
		before = something.conflictDegree;	
		something.influencedBy(peopleWithSelfPositionAfterSecondLevel);
		after = something.conflictDegree;
		assert after > before;
		
		/*我知道永远有第三个阈值、第四个阈值.....*/
		assert getThirdLevel(something) != null;
		assert getFourthLevel(something) != null;
		
		/*至于其它、我不知道，不敢问，不敢想，我只能看*/
		me.observe();
	}
	
	
	
	private static boolean isEven(int num) {
		return num%2 == 0;
	}
	
	public static class Num{
		int value;

		public Num(int value) {
			super();
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
	
	private static boolean isEven(Num num) {
		return isEven(num.value);
	}
	
	@Test
	public void testLamada() {
		Stream.of(1,2,3,4).map(Num::new)
			.anyMatch(StreamTest::isEven);
	}
	
	private static int count = 0;
	
	public static class A {
		int id;
		A(){
			id = ++count;
		}
	}
	
	@Test
	public void testLazyEval() {
		
		List<A> demo = new ArrayList<>();
		demo.add(new A());
		demo.add(new A());
		demo.add(new A());
		
		A changedA = new A();
		assert changedA.id == 4;
		
		demo.stream().map(a->{
			System.out.println(a.id);
			changedA.id = 6;
			return a;
		}).distinct()
		.count();
		
		System.out.println(changedA.id);
	}
	
	@Test
	public void testStreamConsumed() {
		Stream<Integer> evenStreams = Stream.of(1,2,3,4).filter(num->num%2==0);
		List<Integer> evenNums = evenStreams.collect(Collectors.toList());
		long count = evenNums.stream().count();
		System.out.println("1,2,3,4中，偶数有"+count+"个");
		System.out.println("分别是");
		evenNums.stream().forEach(e->{
			System.out.println(e);
		});
	}
	
	@Test
	public void testDropWhile() {
		Stream.of(1,2,3,4,5,4,3,2,1)
		.dropWhile(num->num<4).forEach(num->{
			/*输出4、5、4、3、2、1*/
			System.out.println(num);
		});
	}
	
	@Test
	public void testTakeWhile() {
		Stream.of(1,2,3,4,5,4,3,2,1)
		.takeWhile(num->num<4).forEach(num->{
			/*输出1、2、3*/
			System.out.println(num);
		});
	}
	
	public<T> List<T> takeWhile(List<T> src,Predicate<T> tester) {
		
		List<T> rlt = new ArrayList<>();
		
		for(int i=0;i<src.size();i++) {
			T cur = src.get(i);
			if(tester.test(cur)) {
				rlt.add(cur);
			}else {
				break;
			}
		}
		
		return rlt;
	}
	

	@Test
	public void testMatch() {
		
		List<Object> s = new ArrayList<Object>();
		s.sort(null);
		
		/*1,2,3,4,5,6,7,8,9,10 是否 都是偶数*/
		Stream.of(1,2,3,4,5,6,7,8,9,10).allMatch(StreamTest::isEven);
		/*1,2,3,4,5,6,7,8,9,10 是否 存在一个偶数*/
		Stream.of(1,2,3,4,5,6,7,8,9,10).anyMatch(StreamTest::isEven);
		/*1,2,3,4,5,6,7,8,9,10 是否 都不是偶数*/
		Stream.of(1,2,3,4,5,6,7,8,9,10).noneMatch(StreamTest::isEven);
		
	}
	
	
	public static class Student{
		
		List<Double> historicalScore = new ArrayList<Double>();
		
		MajorCategory cat;
		
		MajorCategory selectedMajorCategory() {
			return cat;
		};
		
		public double getGPA() {
			return 0;
		}
		
	}
	
	
	static class Class{
		
		int num;
		List<Student> students;
		
	}
	
	
	@Test
	public void testLiberalArtsAndScienceDivisionStory() {
		
		/*1班和2班的同学要进行文理文科了*/
		List<Student> studentsOfClass1 = getAllStudentsInClass1();
		List<Student> studentsOfClass2 = getAllStudentsInClass2();
		
		/*第一步，把两个班的学生放在一块*/
		List<Student> studentsOfBothClass = Stream
				.concat(studentsOfClass1.stream(), studentsOfClass2.stream())
				.collect(toList());
		
		/* 
		 * 无论是1班还是2班的学生，在高二文理分科之前，都至少进行了四次阶段性考试
		 * 高一上、下学期的期中、期末考试。
		 * */
		studentsOfBothClass.stream().forEach(student->{
			for(int i=0 ;i < 4 ;i++) {
				double score = examine(student);
				student.historicalScore.add(score);
			}
		});
		
		/* 将要去文科班的，是选择了主修文科的同学 */
		List<Student> studentsForLiberalArtsClass = studentsOfBothClass.stream()
				.collect(Collectors.filtering(student->student.selectedMajorCategory() == MajorCategory.LIBERAL_ARTS
				, toList()));
		
		/* 将要去理科班的，是选择了主修理科的同学 */
		List<Student> studentsForScienceClass = studentsOfBothClass.stream()
				.filter(student->student.selectedMajorCategory() == MajorCategory.SCIENCE)
				.collect(toList());
		
		/* 谁能去理科实验班呢？ 理科班中，按历史成绩排名第一的同学 */
		Student studentForExperimentalClass = studentsForScienceClass.stream()
				.max(Comparator.comparing(student->{
			double averageScore = student.historicalScore.stream()
					.collect(Collectors.averagingDouble(i->i));
			return averageScore;
		})).get();
		
		/* 尽管被分到理科实验班的同学成绩很好，但他还是不会分身术 */
		studentsForScienceClass.remove(studentForExperimentalClass);
		
		/* 人员分配完毕，工作完成了*/
		assignToLiberalArtClass(studentsForLiberalArtsClass);
		assignToScienceClass(studentsForScienceClass);
		assignToExperimentalClass(studentForExperimentalClass);
	}
	
	static class Grade{
		/*本年级所有学生*/
		List<Student> students;
	}
	
	@Test
	public void testGrantScholarships() {
		
		List<Grade> allGrades = getAllGrades();
		
		/*
		 * 各年级的学生根据GPA逆序排序，前100名的学生，即为发放奖学金的名单。
		 * 即将“年级”映射为“该年级GAP前100名的学生”
		 */
		List<Student> target = allGrades.stream()
				.collect(Collectors.flatMapping(grade->
			grade.students.stream()
			.sorted(Comparator.comparing(Student::getGPA).reversed())
			.limit(100), toList()));
				
		target.forEach(this::grantScholarship);
	}
	
	
	@Test
	public void testComparator() {
		List<Num> nums =List.of(new Num(1)
				,new Num(2)
				,new Num(3));
		Comparator<Num> com = Comparator.comparing(a -> a.value);
		Num rlt = nums.stream()
				.max(com.reversed())
				.get();
		System.out.println(rlt.value);
	}
	
	
	
	
	private void grantScholarship(Student s) {
		
	}
	
	
	private List<Grade> getAllGrades() {
		// TODO Auto-generated method stub
		return null;
	}

	private void assignToExperimentalClass(Student studentForExperimentalClass) {
		// TODO Auto-generated method stub
		
	}

	private void assignToScienceClass(List<Student> studentsForScienceClass) {
		// TODO Auto-generated method stub
		
	}

	private void assignToLiberalArtClass(List<Student> studentsForLiberalArtClass) {
		// TODO Auto-generated method stub
		
	}

	private enum MajorCategory{
		
		LIBERAL_ARTS(),
		SCIENCE();
	}
	
	
	
	private static double examine(Student student) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Test
	public void testCommon() {
		int count = 1;
		for(int i=0;i<4;i++) {
			System.out.println(count++);
		}
	}
	

	private List<Student> getAllStudentsInClass2() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Student> getAllStudentsInClass1() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Test
	public void testReduce() {
		final Stream<Integer> intStream = Stream.of(1,2,3,4);
		final BinaryOperator<Integer> reducer = 
				(accumulator, currentValue) -> accumulator + currentValue;
		
		/*打印 10*/
		System.out.println(intStream.reduce(reducer).get());
	}
	
	@Test
	public void testReduce2() {
		final Stream<Integer> intStream = Stream.of(1,2,3,4);
		final BinaryOperator<Integer> reducer = 
				(accumulator, currentValue) -> accumulator + currentValue;
		
				
		/*打印 15*/
		System.out.println(intStream.reduce(5,reducer));
	}
	
	@Test
	public void testReduce3() {
		final Stream<String> stringStream = Stream
				.of("马尔科姆的一家","发展受阻","钢之炼金术师","傲骨贤妻","无问西东")
				.parallel();
		
		int sumOfChars = stringStream.reduce(0,
				(accumulator,currentValue)->accumulator+currentValue.length(),
				//返回值为Null，由于普通流无需“连接”
				(a,b)->a+b);
		
		//输出25
		System.out.println(sumOfChars);
	}
	
	@Test
	public void testArray() {
		final Stream<String> stringStream = Stream
				.of("马尔科姆的一家","发展受阻","钢之炼金术师","傲骨贤妻","无问西东");
		
		String[] rlt = (String[])stringStream.toArray();
		
		for(String s:rlt) {
			System.out.println(s);
		}
	}
	
	
	
	@Test
	public void testNull() {
		Stream<Integer>  stream = Stream.of(1,2,3,null);
		long s = stream.collect(Collectors.counting());
	}
	
	@Test
	public void testCollect() {
	}
	
	People me;
	
	
	
	
	
	
	private Object getFourthLevel(Thing something) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getThirdLevel(Thing something) {
		// TODO Auto-generated method stub
		return null;
	}

	private long getSecondLevel(Thing something) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static long getFirstLevel(Thing something) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static enum Position{
		DONT_CARE,
		THE_MAN,
		THE_WOMAN, MEN, WOMEN;
	}
	
	
	
	public static class Gender{
		
	}
	
	
	
	
	private static List<People> learnAbout(Thing huolala) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Thing appear() {

		
		return null;
	}

	public static class People{
		
		public Position producePosition(Thing thing) {
			return null;
		}

		public void observe() {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	
	public static class Thing{
		
		long conflictDegree;
		
		List<Object> conflictParties = null;

		public void influencedBy(Map<Position, List<People>> peopleWithSelfPosition) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
}
