package manager.util;

import manager.exception.LogicException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;

public class ZonedTimeUtils {

    public static ZonedDateTime copyDateOnly(ZonedDateTime date) {
        ZonedDateTime copy = ZonedDateTime
                .of(date.getYear(),date.getMonthValue(),date.getDayOfMonth()
                ,0,0,0,0,date.getZone());
        return copy;
    }

    private static void assertSameZoneForComparing(ZonedDateTime active,ZonedDateTime passive){
        if(!active.getZone().equals(passive.getZone())){
            throw new LogicException("shouldn't compare time with different timezones "+active.getZone()+" vs "+passive.getZone());
        }
    }

    public static boolean isAfterByDate(ZonedDateTime active,ZonedDateTime passive){
        assertSameZoneForComparing(active,passive);
        return active.toLocalDate().isAfter(passive.toLocalDate());
    }

    public static boolean isAfterByTime(ZonedDateTime active,ZonedDateTime passive){
        assertSameZoneForComparing(active,passive);
        return active.toLocalDateTime().isAfter(passive.toLocalDateTime());
    }


    public static boolean isNotSameByDate(String timezone, Long activeUtc, Long passiveUtc) {
        return !isSameByDate(timezone,activeUtc,passiveUtc);
    }

    public static ZonedDateTime get(String timezone,Long timestamp){
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.of(timezone));
    }

    public static boolean isSameByDate(String timezone, Long activeUtc, Long passiveUtc) {
        return isSameByDate(get(timezone,activeUtc),get(timezone,passiveUtc));
    }

    public static boolean isSameByDate(ZonedDateTime t1,ZonedDateTime t2) {
        assertSameZoneForComparing(t1,t2);
        return t1.toLocalDate().isEqual(t2.toLocalDate());
    }

}
