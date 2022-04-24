package manager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
	  public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
	  
	  public static final String STANDARD_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	  
	  
	  private static int traveledHours = 0;
	  
	  public static void travelHours(int paramInt) {
	    assert paramInt != 0;
	    traveledHours += paramInt;
	  }
	  
	  private static SimpleDateFormat getDateFormatInChina() {
		return new SimpleDateFormat(STANDARD_DATE_PATTERN, Locale.CHINA);
	  }

	  private static SimpleDateFormat getTimeFormatInChina() {
		return new SimpleDateFormat(STANDARD_TIME_PATTERN, Locale.CHINA);
	  }
	  
	public static void travelDays(int paramInt) {
	    assert paramInt > 0;
	    traveledHours += paramInt * 24;
	  }
	  
	  public static void travelTo(String paramString) {
	    int i = countHoursDiff(getCurrentTime(), parseTime(paramString));
	    assert i != 0 : i;
	    travelHours(i);
	  }
	  
	  public static void resetTravel() { traveledHours = 0; }
	  
	  public static boolean isTraveling() { return (traveledHours != 0); }
	  
	  public static Calendar getCurrentDate() { return copyDateOnly(getCurrentTime()); }
	  
	  public static Calendar getCurrentTime() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(10, traveledHours);
	    return copyTimeOnly(calendar);
	  }
	  
	  public static Calendar getBlank() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(0L);
	    return calendar;
	  }
	  
	  public static boolean isBeforeByDate(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyDateOnly(paramCalendar1);
	    Calendar calendar2 = copyDateOnly(paramCalendar2);
	    return calendar1.before(calendar2);
	  }
	  
	  public static boolean isNotBeforeByDate(Calendar paramCalendar1, Calendar paramCalendar2) { return !isBeforeByDate(paramCalendar1, paramCalendar2); }
	  
	  public static boolean isSameByDate(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyDateOnly(paramCalendar1);
	    Calendar calendar2 = copyDateOnly(paramCalendar2);
	    return calendar1.equals(calendar2);
	  }
	  
	  public static boolean isNotSameByDate(Calendar paramCalendar1, Calendar paramCalendar2) { return !isSameByDate(paramCalendar1, paramCalendar2); }
	  
	  public static boolean isAfterByDate(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyDateOnly(paramCalendar1);
	    Calendar calendar2 = copyDateOnly(paramCalendar2);
	    return calendar1.after(calendar2);
	  }
	  
	  public static boolean isNotAfterByDate(Calendar paramCalendar1, Calendar paramCalendar2) { 
		  return !isAfterByDate(paramCalendar1, paramCalendar2); 
	  }
	  
	  public static boolean isSameByTime(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyTimeOnly(paramCalendar1);
	    Calendar calendar2 = copyTimeOnly(paramCalendar2);
	    return calendar1.equals(calendar2);
	  }
	  
	  public static boolean isSameWithinOneSecond(Calendar paramCalendar1, Calendar paramCalendar2) {
	    long l1 = paramCalendar1.getTimeInMillis();
	    long l2 = paramCalendar2.getTimeInMillis();
	    long l3 = (l1 > l2) ? (l1 - l2) : (l2 - l1);
	    return (l3 <= 1000L);
	  }
	  
	  public static boolean isBeforeByTime(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyTimeOnly(paramCalendar1);
	    Calendar calendar2 = copyTimeOnly(paramCalendar2);
	    return calendar1.before(calendar2);
	  }
	  
	  public static boolean isAfterByTime(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyTimeOnly(paramCalendar1);
	    Calendar calendar2 = copyTimeOnly(paramCalendar2);
	    return calendar1.after(calendar2);
	  }
	  
	  public static boolean isBlank(Calendar paramCalendar) { 
		  return (paramCalendar.getTimeInMillis() == 0L); 
	  }
	  
	  public static boolean isNotBlank(Calendar paramCalendar) {
		  return !isBlank(paramCalendar); 
	}
	  
	  public static String parseTime(Calendar paramCalendar, String paramString) {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString, Locale.CHINA);
	    return simpleDateFormat.format(paramCalendar.getTime());
	  }
	  
	  public static String parseTime(Calendar paramCalendar) { 
		  return getTimeFormatInChina().format(paramCalendar.getTime()); 
	  }
	  
	  public static String parseDate(Calendar paramCalendar) {
		  return getDateFormatInChina().format(paramCalendar.getTime()); 
	  }
	  
	  public static Calendar parseTimeString(String paramString1, String paramString2) {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString2, Locale.CHINA);
	    Calendar calendar = Calendar.getInstance();
	    try {
	      calendar.setTime(simpleDateFormat.parse(paramString1));
	    } catch (ParseException parseException) {
	      throw new RuntimeException("parseTimeString fail. target=" + paramString1 + ", error=" + parseException.getMessage());
	    } catch (RuntimeException runtimeException) {
	      throw new RuntimeException("parseTimeString fail. target=" + paramString1 + ", error=" + runtimeException.getMessage());
	    } 
	    return calendar;
	  }
	  
	  public static Calendar parseDate(String paramString) {
	    try {
	      Date date = getDateFormatInChina().parse(paramString);
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTime(date);
	      return copyDateOnly(calendar);
	    } catch (ParseException parseException) {
	      throw new RuntimeException("parseDateString fail. target=" + paramString + ", error=" + parseException.getMessage());
	    } catch (RuntimeException runtimeException) {
	      throw new RuntimeException("parseDateString fail. target=" + paramString + ", error=" + runtimeException.getMessage());
	    } 
	  }
	  
	  public static Calendar parseTime(String paramString) {
		  return parseTimeString(paramString, "yyyy-MM-dd HH:mm:ss"); 
	  }
	  
	  public static Calendar copy(Calendar paramCalendar) { return (Calendar)paramCalendar.clone(); }
	  
	  public static Calendar copyDateOnly(Calendar paramCalendar) {
	    Calendar calendar = getBlank();
	    calendar.set(1, paramCalendar.get(1));
	    calendar.set(2, paramCalendar.get(2));
	    calendar.set(5, paramCalendar.get(5));
	    return calendar;
	  }
	  
	  public static Calendar copyTimeOnly(Calendar paramCalendar) {
	    Calendar calendar = getBlank();
	    calendar.set(1, paramCalendar.get(1));
	    calendar.set(2, paramCalendar.get(2));
	    calendar.set(5, paramCalendar.get(5));
	    calendar.set(11, paramCalendar.get(11));
	    calendar.set(12, paramCalendar.get(12));
	    calendar.set(13, paramCalendar.get(13));
	    return calendar;
	  }
	  
	  public static boolean isDateEquals(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyDateOnly(paramCalendar1);
	    Calendar calendar2 = copyDateOnly(paramCalendar2);
	    return (calendar1.getTimeInMillis() == calendar2.getTimeInMillis());
	  }
	  
	  public static boolean isTimeEquals(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyTimeOnly(paramCalendar1);
	    Calendar calendar2 = copyTimeOnly(paramCalendar2);
	    return (calendar1.getTimeInMillis() == calendar2.getTimeInMillis());
	  }
	  
	  public static int countDaysDiff(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = copyDateOnly(paramCalendar1);
	    Calendar calendar2 = copyDateOnly(paramCalendar2);
	    return (int)((calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 24L / 60L / 60L / 1000L);
	  }
	  
	  public static Calendar createByMinute(int paramInt1, int paramInt2) {
	    assert paramInt1 >= 0 && paramInt1 <= 23;
	    assert paramInt2 >= 0 && paramInt2 <= 59;
	    Calendar calendar = getBlank();
	    calendar.set(11, paramInt1);
	    calendar.set(12, paramInt2);
	    return calendar;
	  }
	  
	  public static Calendar copyDate(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar = (Calendar)paramCalendar1.clone();
	    calendar.set(1, paramCalendar2.get(1));
	    calendar.set(2, paramCalendar2.get(2));
	    calendar.set(5, paramCalendar2.get(5));
	    return calendar;
	  }
	  
	  public static Calendar copyMinuteOnly(Calendar paramCalendar) {
		  return createByMinute(paramCalendar.get(11), paramCalendar.get(12)); 
		 }
	  
	  /**
	   * 会丢失掉秒数的精度
	   */
	  public static int countMinutesDiff(Calendar paramCalendar1, Calendar paramCalendar2) {
		    return (int)((paramCalendar1.getTimeInMillis() - paramCalendar2.getTimeInMillis()) / 60L / 1000L);
	  }
	  
	  public static int countHoursDiff(Calendar paramCalendar1, Calendar paramCalendar2) {
	    Calendar calendar1 = parseToHour(paramCalendar1);
	    Calendar calendar2 = parseToHour(paramCalendar2);
	    return (int)((calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 60L / 60L / 1000L);
	  }
	  
	  static Calendar parseToHour(Calendar paramCalendar) {
	    String str = parseTime(paramCalendar);
	    str = str.substring(0, str.length() - 5) + "00:00";
	    return parseTime(str);
	  }
	  
	  public static Calendar getMinTimeOfDay(Calendar day) {
		  Calendar min = TimeUtil.copy(day);
		  min.set(Calendar.HOUR, 0);
		  min.set(Calendar.MINUTE,0);
		  min.set(Calendar.SECOND,0);
		  min.set(Calendar.MILLISECOND,0);
		  return min;
	  }
	  
	  public static Calendar getMaxTimeOfDay(Calendar day) {
		  Calendar max = TimeUtil.copy(day);
		  max.set(Calendar.HOUR, 23);
		  max.set(Calendar.MINUTE,59);
		  max.set(Calendar.SECOND,59);
		  max.set(Calendar.MILLISECOND,999);
		  return max;
	  }

	public static String getNeo4jDateTime(Calendar val) {
		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(val.getTime());
		String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA).format(val.getTime());
		return date+"T"+time;
	}
	  
}
