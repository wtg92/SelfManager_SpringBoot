package book;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamTest {
	
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
				.filter(student->student.selectedMajorCategory() == MajorCategory.LIBERAL_ARTS)
				.collect(toList());
		
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
		List<Student> target = allGrades.stream().flatMap(grade->
			grade.students.stream()
			.sorted(Comparator.comparing(Student::getGPA).reversed())
			.limit(100)
		).collect(toList());
				
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
	public void testNull() {
		Stream<Integer>  stream = Stream.of(1,2,3,null);
		stream.collect(Collectors.groupingBy(Function.identity()));
	}
}
