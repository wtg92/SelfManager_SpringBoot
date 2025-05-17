package manager.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    public static <T> String getStringFieldValue(T base, String fieldName) {
        if (base == null || fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Object or field name cannot be null/empty");
        }

        Class<?> clazz = base.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true); // 允许访问 private 字段
                Object value = field.get(base);
                return value != null ? value.toString() : null;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("无法访问字段: " + fieldName, e);
            }
        }

        return null; // 如果整个类层次结构中都找不到字段
    }

    public static <T> T filterFields(T original, List<String> fieldNames) {
        if (original == null || fieldNames == null) {
            throw new IllegalArgumentException("Original object and field names cannot be null");
        }

        try {
            Class<?> clazz = original.getClass();
            // 检查是否可实例化
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                throw new IllegalArgumentException("Target class must be concrete: " + clazz.getName());
            }

            T newInstance = (T) clazz.getDeclaredConstructor().newInstance();

            // 递归处理类及其父类的字段
            Class<?> current = clazz;
            while (current != null) {
                for (Field field : current.getDeclaredFields()) {
                    // 跳过静态字段
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    field.setAccessible(true);
                    if (fieldNames.contains(field.getName())) {
                        field.set(newInstance, field.get(original));
                    } else if (!field.getType().isPrimitive()) {
                        // 非原始类型才设为 null
                        field.set(newInstance, null);
                    }
                }
                current = current.getSuperclass();
            }
            return newInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to filter fields via reflection", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Security manager blocked reflection", e);
        }
    }
}
