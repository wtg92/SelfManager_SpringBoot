package book;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectTest {
	
	public void reflectFirstStep(){
		/*第一种方式*/
		Class<ReflectTest> claFromStatic = ReflectTest.class;
		/*第二种方式*/
		ReflectTest base = new ReflectTest();
		Class<? extends ReflectTest> claFromEntity = base.getClass();
 	}
	
	public static class Father{
		public int fatherInt;
		private int privateFatherInt;
		
		public void publicFatherMethod() {}
		void defaultFatherMethod() {}
		private void privateFatherMethod() {}
	}
	
	public static class Son extends Father{
		public int sonInt;
		static int staticSonInt;
		
		public int getSonInt() {
			return sonInt;
		}
		
		static void defaultStaticSonMethod() {
			System.out.println("Call defaultStaticSonMethod..");
		}
	}
	
	
	
	
	@Retention(RetentionPolicy.SOURCE)
	public @interface A1{
		public int wow() default 1;
	}
	
	@Retention(RetentionPolicy.CLASS)
	public @interface A2{
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface A3{
		
	}
	
	@A1(wow = 5)
	public class BasicAnnotation{
		
		@A1
		public int a;
		
		@A1(wow = 3)
		public int b;
		
	}
	
	@org.junit.Test
	public void calculateImportant() {
		
		HugeObject obj = new HugeObject();
		Class<HugeObject> cla = HugeObject.class;
		
		/*由于对象属性不为public，因此使用Declared，得到所有字段*/
		Field[] allFields = cla.getDeclaredFields();
		
		/*找出重要且为int类型的字段*/
		List<Field> fields = Arrays.stream(allFields)
		/*过滤出被重要注解标记过的字段*/
		.filter(f->f.isAnnotationPresent(Important.class))
		/*过滤出其中为int类型*/
		.filter(f->f.getType() == int.class)
		.collect(Collectors.toList());
		
		/*再找出其中重要类型为1的字段*/
		List<Field> fieldsOfType1 = fields.stream()
				/*过滤出注解类型为1的字段*/
				.filter(f->f.getAnnotation(Important.class).type() == 1)
				.collect(Collectors.toList());
		
		/*需求的字段都找到了，现在只需将实体对象传入，计算即可*/
		
		/*所有重要的int类型的字段求和*/
		int sumForAllImportantFields = fields.stream()
				.mapToInt(f->{
					try {
						return f.getInt(obj);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e.getMessage());
					}
				})
				.sum();
		
		/*所有重要的int类型的且注解类型为1的字段求和*/
		int sumForImportantFieldsOfType1 = fieldsOfType1.stream()
				.mapToInt(f->{
					try {
						return f.getInt(obj);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e.getMessage());
					}
				})
				.sum();
		
		/*输出为313*/
		System.out.println(sumForAllImportantFields);
		/*输出为3*/
		System.out.println(sumForImportantFieldsOfType1);
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Important{
		public int type() default 0;
	}
	
	public class HugeObject{
		
		@Important
		int i1 = 10;
		
		int i2 = 5;
		
		@Important(type=1)
		int i3 = 3;
		
		int i4 = 50;
		
		@Important
		int i5 = 300;
		
		@Important(type=1)
		String s1 = "test";
		
		char c1 = 'a';
		
		@Important
		char c2 = 'b';
		
		char c3 = 'c';
	}
	
	
	
	
	
	@org.junit.Test
	public void testBasicAnnotation() {
		
		Father son = new Son();
		/*false*/
		System.out.println(son.getClass() == Father.class);
		/*true*/
		System.out.println(son.getClass() == Son.class);
		
	}
	
	
	public void testAnnotation() {
		Class<Son> sonClass = Son.class;
//		sonClass.annotation
		
	}
	
	@org.junit.Test
	public void testUsageCase() throws Exception {
		Son son = new Son();
		/*一行代码得到值*/
		System.out.println(son.sonInt);
		/*三行代码得到值*/
		Class<? extends Father> sonClass = son.getClass();
		Field f = sonClass.getDeclaredField("sonInt");
		System.out.println(f.getInt(son));
	}
	
	
	@org.junit.Test
	public void testGetFields() throws Exception {
		Son son = new Son();
		Father father = new Father();
		Class<? extends Father> sonClass = son.getClass();
		Field f = sonClass.getDeclaredField("sonInt");
		/*输出5*/
		System.out.println(f.getInt(son));
		
		
	}
	
	@org.junit.Test
	public void testCallMethod() throws Exception {
		Son son = new Son();
		Class<? extends Father> sonClass = son.getClass();
		Method entityMethod = sonClass.getMethod("getSonInt");
		Method staticMethod = sonClass.getDeclaredMethod("defaultStaticSonMethod");
		
		son.sonInt = 5;
		/*输出5*/
		System.out.println(entityMethod.invoke(son));
		/*打印 Call defaultStaticSonMethod..*/
		staticMethod.invoke(null);
	}
	
	
	@org.junit.Test
	public void testReflectEntity() {
		Father father = new Father();
		Father son = new Son();

		Class<? extends Father> fatherClass = father.getClass();
		Class<? extends Father> sonClass = son.getClass();
		
		/*输出1 为fatherInt*/
		System.out.println(fatherClass.getFields().length);
		/*输出2 为fatherInt和privateFatherInt*/
		System.out.println(fatherClass.getDeclaredFields().length);
		/*输出2 为fatherInt和sonInt*/
		System.out.println(sonClass.getFields().length);
		/*输出3 为sonInt和privateSonInt和defaultSonInt*/
		System.out.println(sonClass.getDeclaredFields().length);
		/*输出10 为publicMethodFather以及继承自Object的9个方法*/
		System.out.println(fatherClass.getMethods().length);
		/*输出3 为publicFatherMethod defaultFatherMethod privateFatherMethod*/
		System.out.println(fatherClass.getDeclaredMethods().length);
		/*输出11 为publicSonMethod publicFatherMethod 以及继承自Object的9个方法*/
		System.out.println(sonClass.getMethods().length);
		/*输出1 为publicSonMethod*/
		System.out.println(sonClass.getDeclaredMethods().length);
	}
	
	@org.junit.Test
	public void testGetFieldAndMethod() {
		Father father = new Father();
		Father son = new Son();

		Class<? extends Father> fatherClass = father.getClass();
		Class<? extends Father> sonClass = son.getClass();
		
		/*输出1 为fatherInt*/
		System.out.println(fatherClass.getFields().length);
		/*输出2 为fatherInt*/
		System.out.println(fatherClass.getDeclaredFields().length);
		/*输出2 为fatherInt和sonInt*/
		System.out.println(sonClass.getFields().length);
		System.out.println(sonClass.getDeclaredFields().length);
		/*输出*/
		System.out.println(fatherClass.getMethods().length);
		System.out.println(fatherClass.getDeclaredMethods().length);
		System.out.println(sonClass.getMethods().length);
		System.out.println(sonClass.getDeclaredMethods().length);
	}
	
}
