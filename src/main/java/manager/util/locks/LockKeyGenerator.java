package manager.util.locks;

public abstract class LockKeyGenerator {

    private static String USER_KEY_PREFIX = "user";

    public static String generateUserKey(long userId,String event){
        return USER_KEY_PREFIX+userId+event;
    }


}
