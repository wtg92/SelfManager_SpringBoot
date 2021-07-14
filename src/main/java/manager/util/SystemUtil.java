package manager.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import manager.exception.NoSuchElement;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;

public abstract class SystemUtil {
	
	/*这种机制mysql中是使用的Int 最大是2的32次方 这里取一个稍小的值 30 更安全些*/
	final private static int MAX_ENUM_DB_CODE = 30;
	
	public static <T extends Enum<?>> T valueOfDBCode(int dbCode,Function<T,Integer> dbCodeGetter,Class<T> enumCla) throws NoSuchElement {
		List<T> rlt = Arrays.stream(enumCla.getEnumConstants())
			.filter(e->dbCodeGetter.apply(e) == dbCode)
			.collect(toList());
		
		if(rlt.size() > 1) 
			throw new RuntimeException("dup dbCode"+dbCode);
		
		if(rlt.size() == 0)
			throw new NoSuchElement();
		
		return rlt.get(0);
	}
	
	public static <T extends Enum<?>> T valueOfField(String field,Function<T,String> fieldGetter,Class<T> enumCla) throws NoSuchElement {
		List<T> rlt = Arrays.stream(enumCla.getEnumConstants())
			.filter(e->fieldGetter.apply(e).equals(field))
			.collect(toList());
		
		if(rlt.size() > 1) 
			throw new RuntimeException("dup field"+field);
		
		if(rlt.size() == 0)
			throw new NoSuchElement();
		
		return rlt.get(0);
	}
	
	/*使用这种机制时，对应枚举必须有Undecided!*/
	public static<T> List<T> parseToList(int enumsCode,Function<Integer, T> valueOfDBCoder){
		boolean[] enumsBoolArray = parseCodeToEnumsBoolArray(enumsCode);
		List<T> rlt = new ArrayList<T>();
		for(int i=0;i<enumsBoolArray.length;i++) {
			if(enumsBoolArray[i]) {
				/*+1是去掉Undecided的影响，数据库显然没必要存储Undecided的信息*/
				rlt.add(valueOfDBCoder.apply(i+1));
			}
		}
		return rlt;
	}
	
	public static<T> int parseToEnumsCode(List<T> enums,Function<T, Integer> getDBCoder) {
		boolean[] enumsArray = new boolean[MAX_ENUM_DB_CODE];
		for(T e:enums) {
			int dbCode = getDBCoder.apply(e);
			assert dbCode <= MAX_ENUM_DB_CODE;
			enumsArray[dbCode-1] = true;
		}
		
		return parseEnumsCode(enumsArray);
	}
	
	
	private static boolean[] parseCodeToEnumsBoolArray(int code) {
		char[] chars = Long.toBinaryString(code).toCharArray();
		boolean[] rlt = new boolean[MAX_ENUM_DB_CODE];
		for(int i = 0;i < chars.length;i++) {
			if(chars[chars.length-i-1] == '1') {
				rlt[i] = true;
			}else {
				rlt[i] = false;
			}
		}
		return rlt;
	}
	
	private static int parseEnumsCode(boolean[] matchArray)  {
		assert matchArray.length == MAX_ENUM_DB_CODE;
		String rlt = "";
		for(int i=0; i <matchArray.length;i++) {
			if(matchArray[i]) {
				rlt = "1" + rlt;
			}else {
				rlt = "0" + rlt;
			}
		}
		return Integer.parseUnsignedInt(rlt, 2);
	}
	
}
