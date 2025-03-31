package manager.util;

import manager.exception.LogicException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ZonedTimeUtils {

    public static ZonedDateTime copyDateOnly(ZonedDateTime date) {
        return ZonedDateTime
                .of(date.getYear(),date.getMonthValue(),date.getDayOfMonth()
                ,0,0,0,0,date.getZone());
    }

    private static void assertSameZoneForComparing(ZonedDateTime active,ZonedDateTime passive){
        if(!active.getZone().equals(passive.getZone())){
            throw new LogicException("shouldn't compare time with different timezones "+active.getZone()+" vs "+passive.getZone());
        }
    }

    public static boolean isAfterByDateUtc(long activeUtc,long passiveUtc,ZoneId zone){
        ZonedDateTime active = Instant.ofEpochMilli(activeUtc).atZone(zone);
        ZonedDateTime passive = Instant.ofEpochMilli(passiveUtc).atZone(zone);
        return isAfterByDate(active,passive);
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

    public static long getCurrentDateUtc(String timezone) {
        return getCurrentDateUtc(ZoneId.of(timezone));
    }

    public static long getCurrentDateUtc(ZoneId timezone) {
        return getCurrentDate(timezone).toInstant().toEpochMilli();
    }

    public static ZonedDateTime getCurrentDate(ZoneId timezone) {
        ZonedDateTime now = ZonedDateTime.now(timezone);
        return copyDateOnly(now);
    }


    public static boolean isBeforeByDate(ZonedDateTime active, ZonedDateTime passive) {
        assertSameZoneForComparing(active,passive);
        return active.toLocalDate().isBefore(passive.toLocalDate());
    }

    public static String parseDate(ZonedDateTime today) {
        return String.format("%s : [%s-%s-%s]"
                ,today.getZone().getId()
                ,today.toLocalDate().getYear()
                ,today.toLocalDate().getMonth()
                ,today.toLocalDate().getDayOfMonth()
                );
    }

    public static Long copyDateOnly(Long utc, String timezone) {
        return copyDateOnly(get(timezone,utc)).toInstant().toEpochMilli();
    }

    public static List<String> mapTimezoneTo24Categories(Set<String> availableZones) {
        Map<Integer, String> offsetMap = new TreeMap<>();

        for (String zoneId : availableZones) {
            if (!zoneId.startsWith("Etc/GMT") && !zoneId.equals("UTC")) {
                continue; // 只保留 "Etc/GMT±X" 和 "UTC"
            }

            ZoneId zone = ZoneId.of(zoneId);
            int offset = ZonedDateTime.now(zone).getOffset().getTotalSeconds() / 3600;

            // 只保留一个最通用的时区名称
            offsetMap.putIfAbsent(offset, zoneId);
        }


        return new ArrayList<>(offsetMap.values());
    }
}
