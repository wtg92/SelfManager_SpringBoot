package manager.util;

import java.time.ZoneId;
import java.util.Calendar;

public class RefiningUtil {

    /**
     * utc 是基于date生成的 但当utc有值之后 date要抛弃
     * @param utc
     * @param date 基于
     * @return
     */
    public static boolean shouldFixUtcBasedOnDate(Long utc, Calendar date){
        if(utc == null){
            return true;
        }
        if(utc > 0){
            return false;
        }
        assert utc == 0;
        return TimeUtil.isNotBlank(date);
    }

    public static String getDefaultTimeZone() {
        return "Asia/Shanghai";
    }
}
