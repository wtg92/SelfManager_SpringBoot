package manager.util;

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

}
