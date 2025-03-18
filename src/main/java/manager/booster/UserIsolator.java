package manager.booster;

public abstract class UserIsolator {

    private static final String DELIMITER = "_";
    public static String calculateCoreNamByUser(String baseName,Long userId){
        return baseName + DELIMITER + userId;
    }

}
