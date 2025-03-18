package manager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestUtil {
    public static void checkEnumNoDup(Class<?> enumCla,String filedName) throws Exception {
        assert enumCla.getEnumConstants().length > 0 : "怎么能传进来没有实体的枚举";

        Field field =  enumCla.getEnumConstants()[0].getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        Arrays.stream(enumCla.getEnumConstants()).collect(Collectors.groupingBy(e->{
            try {
                return field.get(e);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
                assert false;
                return null;
            }
        })).forEach((key,values)->{
            assertEquals("DUP "+key,1, values.size());
        });
    }

}
