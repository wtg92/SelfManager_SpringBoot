package manager.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ReflectUtil {

    /**
     * A方法调用B
     * B中调用该方法 得到A的名字
     * @return
     */
    public static String getInvokerMethodName(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
       return stackTrace[3].getMethodName();
    }

    public static String getInvokerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].getClassName();
    }

    public static List<String> getFiledNamesByPrefix(Class<?> clazz, String prefix){
        List<String> fieldNames = new ArrayList<>();
        if (clazz == null || prefix == null) {
            throw new RuntimeException("Why Invoke This?");
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.startsWith(prefix)) {
                fieldNames.add(fieldName);
            }
        }
        return fieldNames;
    }

    public static <V,M> V setFiledValue(V obj,String fieldName,M value){
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Object or field name cannot be null/empty");
        }

        try {
            // 获取类对象
            Class<?> clazz = obj.getClass();

            // 获取字段对象，包括私有字段
            Field field = clazz.getDeclaredField(fieldName);

            // 设置字段可访问
            field.setAccessible(true);

            // 设置字段值
            field.set(obj, value);

            // 返回修改后的对象
            return obj;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Field '" + fieldName + "' not found in class " + obj.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field '" + fieldName + "'", e);
        }
    }
}
