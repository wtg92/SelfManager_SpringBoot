package manager.booster;

import manager.util.SecurityUtil;

public abstract class UserIsolator {

    private static final String DELIMITER = "_v1_";
    public static String calculateCoreNamByUser(String baseName,Long userId){
        return baseName + DELIMITER + userId;
    }

}
