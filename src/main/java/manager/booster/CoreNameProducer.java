package manager.booster;

public abstract class CoreNameProducer {

    private static final String DELIMITER = "_";
    public static String calculateCoreNameByUser(String baseName, Long userId){
        return baseName + DELIMITER + userId;
    }

}
