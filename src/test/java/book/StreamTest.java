package book;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
		
		Stream.of("1","11","111","1111","123","12","1").dropWhile(a->a.length()<3).forEach(z->{
			System.out.println(z);
		});
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
		
		/* 谁能去理科实验班呢？ 理科班中，按历史成绩逆序排序，排名第一的同学 */
		Student studentForExperimentalClass = studentsForScienceClass.stream()
				.sorted(Comparator.comparing(student->{
			double averageScore = student.historicalScore.stream()
					.collect(Collectors.averagingDouble(i->i));
			return -averageScore;
		})).collect(toList()).get(0);
		
		/* 尽管被分到理科实验班的同学成绩很好，但他还是不会分身术 */
		studentsForScienceClass.remove(studentForExperimentalClass);
		
		/* 人员分配完毕，工作完成了*/
		assignToLiberalArtClass(studentsForLiberalArtsClass);
		assignToScienceClass(studentsForScienceClass);
		assignToExperimentalClass(studentForExperimentalClass);
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
	
	
	
	
	
	
	
	
	
}
