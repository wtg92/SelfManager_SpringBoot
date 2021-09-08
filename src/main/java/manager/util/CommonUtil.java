package manager.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static manager.system.SM.logger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.persistence.Table;

import manager.system.SM;

public abstract class CommonUtil {
	private static final Random random = new SecureRandom();
	
	public static InputStream getFileInResourcesDirectoryBufferedly(String fileName) {
		try {
			return new BufferedInputStream(SM.class.getResourceAsStream("/"+fileName),5000) ;
		}catch (Exception e) {
			e.printStackTrace();
			assert false;
			throw new RuntimeException("read file error "+e.getMessage());
		}

	}
	public static boolean getBoolValFromPropertiesFileInResource(String key) {
		return Boolean.parseBoolean(getValFromPropertiesFileInResource(key, SM.PROPERTIES_FILE_NAME));
	}
	
	public static int getIntValFromPropertiesFileInResource(String key) {
		return Integer.parseInt(getValFromPropertiesFileInResource(key, SM.PROPERTIES_FILE_NAME));
	}
	
	public static String getValFromPropertiesFileInResource(String key) {
		return getValFromPropertiesFileInResource(key, SM.PROPERTIES_FILE_NAME);
	}
	
	public static String getValFromPropertiesFileInResource(String key,String fileName) {
		Properties proerties = new Properties();
		try(InputStream in = getFileInResourcesDirectoryBufferedly(fileName)){
			proerties.load(in);
			String rlt = proerties.getProperty(key);
			if(rlt == null)
				throw new RuntimeException("缺乏配置 "+key);
			return rlt.strip();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("config file error "+e.getMessage());
		}
	}
	
	public static String[] splitByUpperCase(String src){
		return src.split("(?<!^)(?=[A-Z])");
	}
	
	public static byte[] toByteArray(String hexString) {
		hexString = hexString.replaceAll(" ", "");
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String strHex = Integer.toHexString(bytes[i]);
			if (strHex.length() > 3) {
				sb.append(strHex.substring(6));
			} else {
				if (strHex.length() < 2) {
					sb.append("0" + strHex);
				} else {
					sb.append(strHex);
				}
			}
		}
		return sb.toString();
	}
	
	public static String getEntityTableName(Class<?> cla){
		Table anno = cla.getAnnotation(Table.class);
		if(anno == null)
			throw new RuntimeException(cla.getName()+"无Table注解");
		return anno.name();
	}
	
	/*获得在[min,max)之间的值 */
	public static int getByRandom(int min,int max) {

		return min + random.nextInt(max-min);
	}
	
	
	public static List<Integer> parseTo(String[] args){
		return Arrays.stream(args).map(Integer::parseInt).collect(toList());
	}
	
	public static void printList(List<?> rlt) {
		logger.debugLog(createPrintStr(rlt, Object::toString, ","));
	}
	public static void printList(List<?> rlt,String loggerKey) {
		logger.debugLog(createPrintStr(rlt, Object::toString, ","),loggerKey);
	}
	
	public static String createPrintStr(List<?> rlt,Function<Object, String> converter,String split) {
		if(rlt.size() == 0)
			return "NULL";
		
		return "Size:"+rlt.size()+"\n"+rlt.stream().map(e->converter.apply(e)).collect(joining(split));
	}
	
	public static<T> boolean equalsOfElements(List<T> src1 , List<T> src2,BiFunction<T, T, Boolean> equalsComparator) {
		if(src1.size() != src2.size())
			return false;
		
		for(int i=0;i<src1.size();i++) {
			if(!equalsComparator.apply(src1.get(i), src2.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static double fixDouble(Double src) {
	   return fixDouble(src, 2);
	}
	
	public static double fixDouble(Double src,int decimal) {
	    String srcText = String.format("%."+decimal+"f",src) ;
        if(srcText.indexOf(".") > 0){  
        	srcText = srcText.replaceAll("0+?$", "");//去掉多余的0  
        	srcText = srcText.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
	    return Double.parseDouble(srcText);
	}
	
	public static <T extends Object> boolean equalsOfElements(List<T> src1 , List<T> src2) {
		return equalsOfElements(src1, src2,(t1,t2)->t1.equals(t2));
	}
	
	/**
	 * 用在缓存/DB的时候
	 * 希望一些通用函数可以使用Object
	 * int String 可以直接.toString()转 但是Calendar不行
	 *  这个函数 现在遇到的情况是 只处理Calendar 把它转换成对应字符串
	 *  
	 * 假如传入字节码对象 则返回对象名称 
	 *  
	 */
	public static Object pretreatForString(Object val) {
		if(val instanceof Calendar) {
			return TimeUtil.parseTime((Calendar)val);
		}
		
		if(val instanceof Class<?>) {
			return ((Class<?>)val).getName();
		}
		
		return val;
	}
	
}
