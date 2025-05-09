package manager.cache;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import manager.system.SelfX;
import manager.util.CommonUtil;
import manager.util.ReflectUtil;

/**
 *    这个类专门处理解析相关
 *    理论上和CacheUtil配合，被CacheScheduler调度      但和CacheUtil彼此互不知晓
  *   关系表	
  *   key命名语法:库名:表名:第一个参数是否为key值:key值    
  *   	example: r_user_group     one:user    many:group         sm:r_user_group:1:userId
 *    val命名语法: 1:2:3:4:5
 *   		
  *   实体表     库名:表名:id        sm:user:userId           
 *   
 * @author 王天戈
 */
public abstract class CacheConverter {
	
	private static String SPLIT_CHAR = ":";
	private static String FORMER_ARG_FOR_KEY = "1";
	private static String LATTER_ARG_FOR_KEY = "0";
	private static String TEMP_PREFIX = "tp";

	public static String getPrefixEntity(String tableName){
		return  SelfX.DB_NAME + SPLIT_CHAR + tableName;
	}

	public static String createEntityKey(CacheMode mode, long identifier, String tableName) {
        return switch (mode) {
            case E_ID, E_UNIQUE_FIELD_ID -> getPrefixEntity(tableName) + SPLIT_CHAR + identifier;
			/**
			 * 不该再有关系的实体了
			 */
			case R_ONE_TO_MANY_FORMER ->
                    SelfX.DB_NAME + SPLIT_CHAR + tableName + SPLIT_CHAR + FORMER_ARG_FOR_KEY + SPLIT_CHAR + identifier;
            case R_ONE_TO_MANY_LATTER ->
                    SelfX.DB_NAME + SPLIT_CHAR + tableName + SPLIT_CHAR + LATTER_ARG_FOR_KEY + SPLIT_CHAR + identifier;
            default -> {
                assert false : mode;
                throw new RuntimeException("未配置的缓存模型 " + mode);
            }
        };
	}
	public static String createGeneralKey(CacheMode mode, Object ...identifier) {
		String identifierStr = Arrays.stream(identifier)
				.filter(one->{
					if(one  == null){
						System.out.println("createTempKey == null \t"+ ReflectUtil.getInvokerClassName()+":"+ReflectUtil.getInvokerMethodName());
					}
					return one != null;
				})
				.map(Object::toString)
				.collect(Collectors.joining(SPLIT_CHAR));
        return switch (mode) {
            case T_USER -> TEMP_PREFIX + SPLIT_CHAR + "user" + SPLIT_CHAR + identifierStr;
            case T_WS_COUNT_FOR_DATE -> TEMP_PREFIX + SPLIT_CHAR + "ws" + SPLIT_CHAR + identifierStr;
            case T_EMAIL_FOR_SIGN_IN -> TEMP_PREFIX + SPLIT_CHAR + "email" + SPLIT_CHAR + identifierStr;
            case T_TEL_FOR_SIGN_IN -> TEMP_PREFIX + SPLIT_CHAR + "tel" + SPLIT_CHAR + identifierStr;
            case T_UNIQUE_OBJ -> TEMP_PREFIX + SPLIT_CHAR + "uni" + SPLIT_CHAR + identifierStr;
			case PERM -> identifier[0]+SPLIT_CHAR+identifier[1];
            default -> {
                assert false : mode;
                throw new RuntimeException("未配置的缓存模型 " + mode);
            }
        };
	}
	
	

	
	public static String createTempKeyByBiIdentifiers(CacheMode mode,String identifier1,String identifier2) {
		final String RESET_MIDDLE_NAME = "resetpwd";
		switch(mode) {
		case T_EMAIL_FOR_RESET_PWD:
			return TEMP_PREFIX+SPLIT_CHAR+"email"+SPLIT_CHAR+RESET_MIDDLE_NAME+SPLIT_CHAR+identifier1+SPLIT_CHAR+identifier2;
		case T_TEL_FOR_RESET_PWD:
			return TEMP_PREFIX+SPLIT_CHAR+"tel"+SPLIT_CHAR+RESET_MIDDLE_NAME+SPLIT_CHAR+identifier1+SPLIT_CHAR+identifier2;
		default:
			assert false : mode;
		throw new RuntimeException("未配置的缓存模型 "+mode);
		}
	}
	
	
	public static String createPatternKey(CacheMode mode, String tableName) {
		switch(mode) {
			case E_UNIQUE_FIELD_ID:
			case E_ID:
				return SelfX.DB_NAME+SPLIT_CHAR+tableName+SPLIT_CHAR+"*";
			default:
				assert false : mode;
				throw new RuntimeException("未配置的缓存模型 "+mode);
		}
	}

	public static List<Long> parseRVal(String val) {
		if(val.strip().length() == 0)
			return new ArrayList<>();
				
		return CommonUtil.parseToLong(val.split(SPLIT_CHAR));
	}
	
	public static List<Integer> parseRValInInt(String val) {
		if(val.strip().length() == 0)
			return new ArrayList<>();
				
		return CommonUtil.parseToInt(val.split(SPLIT_CHAR));
	}
	
	
	public static<T> String createRsVal(List<T> val) {
		return val.stream().map(Object::toString).collect(joining(SPLIT_CHAR));
	}
	
	
	public static String createRsValForAppend(List<Long> theManyIds) {
		return SPLIT_CHAR+createRsVal(theManyIds);
	}
}
