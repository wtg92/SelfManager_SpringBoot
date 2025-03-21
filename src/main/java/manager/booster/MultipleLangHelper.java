package manager.booster;

import manager.util.ReflectUtil;

import java.util.stream.Collectors;

public abstract class MultipleLangHelper {

    private static final String JOINING_DELIMITER = "_";

    public static <V,M> V setFiledValue(V obj,String fieldName,String classifier, M value){
        return ReflectUtil.setFiledValue(obj,fieldName+JOINING_DELIMITER+classifier,value);
    }

}
