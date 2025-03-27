package manager.booster;

import manager.util.ReflectUtil;

import java.util.stream.Collectors;

public abstract class MultipleLangHelper {

    private static final String JOINING_DELIMITER = "_";

    public static <V,M> V setFiledValue(V obj,String fieldName,String langVersion, M value){
        return ReflectUtil.setFiledValue(obj,
                getFiledName(fieldName,langVersion),value);
    }

    public static String getFiledName(String fieldName,String langVersion){
        return getFiledPrefix(fieldName)+langVersion;
    }

    public static String getFiledPrefix(String fieldName){
        return fieldName+JOINING_DELIMITER;
    }

}
