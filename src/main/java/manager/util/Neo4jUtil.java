package manager.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import manager.entity.general.SMGeneralEntity;

public abstract class Neo4jUtil {
	
	private static final Driver Driver = init();
	
	private static Driver init() {
		String url = CommonUtil.getValFromPropertiesFileInResource("neo4j_url");
		String pwd = CommonUtil.getValFromPropertiesFileInResource("neo4j_pwd");
		return GraphDatabase.driver("bolt://"+url,AuthTokens.basic("neo4j",pwd));
	}
	
	public static Driver getDriver() {
		return Driver;
	}
	
	
	public static long createOne(SMGeneralEntity one) {
		
		return 1;
	}
	
	
	public static List<String> getLabels(Class<? extends SMGeneralEntity> cla){
		List<String> rlt = new ArrayList<String>();
		/**
		 * 到未来用到时，再引入注解
		 */
		rlt.add(cla.getSimpleName());
		return rlt;
	}
	
	public static String transferToNeo4jObj(SMGeneralEntity one,String notation) {
		
		Class<? extends SMGeneralEntity> cla = one.getClass();
		List<String> labels = getLabels(cla);
		Map<String,Object> fieldsWithVal = new HashMap<String, Object>();
		 
		Class<?> cur = cla;
		while(cur != Object.class) {
			Map<String,Object> fieldsWithValForSpecCla = getNeo4jFieldsWithVal(cur,one);
			CommonUtil.mergeMap(fieldsWithVal,fieldsWithValForSpecCla);
			cur = cur.getSuperclass();
		}
		
		
		String rlt = String.format("("+notation+":%s {%s})", labels.stream().collect(Collectors.joining(":")),calculateObjStr(fieldsWithVal));
		return rlt;
	}

	private static String calculateObjStr(Map<String, Object> fieldsWithVal) {
		
		List<String> props = new ArrayList<String>();
		
		fieldsWithVal.forEach((key,val)->{
			if(val == null) {
				return;
			}
			props.add(key+":"+getNeo4jValStr(val));
		});
		
		return props.stream().collect(Collectors.joining(","));
	}

	private static String getNeo4jValStr(Object val) {
		
		if(val instanceof String) {
			return "'"+val+"'";
		}
		if(val instanceof Calendar) {
			Calendar v =  (Calendar)val;
			return  "datetime('"+TimeUtil.getNeo4jDateTime(v)+"')";
		}
		
		return String.valueOf(val);
	}

	private static Map<String, Object> getNeo4jFieldsWithVal(Class<?> cla,SMGeneralEntity one) {
		
		Map<String, Object> rlt = new HashMap<String, Object>();
		
		List<String> filteredField = Arrays.asList("serialVersionUID");
		for(Field field: cla.getDeclaredFields()) {
			if(filteredField.contains(field.getName())) {
				continue;
			}
			try {
				field.setAccessible(true);
				rlt.put(field.getName(), field.get(one));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		return rlt;
	}
	
	
	
}
