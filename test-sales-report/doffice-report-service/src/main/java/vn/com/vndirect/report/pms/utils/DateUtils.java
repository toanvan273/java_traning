package vn.com.vndirect.report.pms.utils;

import org.apache.tomcat.jni.Local;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final ZoneId ZONE_ID_HCM = ZoneId.of("Asia/Ho_Chi_Minh");

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_ID_HCM);
    }

    public static Long localDateToEpochMilli(LocalDateTime date) {
        return date.atZone(ZONE_ID_HCM).toInstant().toEpochMilli();
    }

    public static Long localDateToEpochMilli(String date, String datePattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date result = simpleDateFormat.parse(date);
        return toLocalDateTime(result).atZone(ZONE_ID_HCM).toInstant().toEpochMilli();
    }

    public static LocalDateTime stringToLocalDateTime(String date, String datePattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date result = simpleDateFormat.parse(date);
        return toLocalDateTime(result).atZone(ZONE_ID_HCM).toLocalDateTime();
    }

    public static LocalDateTime atStartOfLocalDateTime(Long startDate) {
        return toLocalDateTime(startDate).with(LocalTime.MIN);
    }

    public static LocalDateTime atEndOfLocalDateTime(Long endDate) {
        return toLocalDateTime(endDate).with(LocalTime.MAX);
    }

    public static LocalDateTime toLocalDateTime(Long date) {
        return Instant.ofEpochMilli(date)
                .atZone(ZONE_ID_HCM)
                .toLocalDateTime();
    }

    public static Date atStartOfDay(Long date) {
        LocalDateTime start = atStartOfLocalDateTime(date);
        return localDateTimeToDate(start);
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime startOfDay = toLocalDateTime(date).with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static LocalDateTime localDateTimeAtStartOfDay(LocalDateTime date) {
        return date.atZone(ZONE_ID_HCM).toLocalDateTime().with(LocalTime.MIN);
    }

    public static LocalDateTime localDateTimeAtEndOfDay(LocalDateTime date) {
        return date.atZone(ZONE_ID_HCM).toLocalDateTime().with(LocalTime.MAX);
    }

    public static Date atEndOfDay(Long date) {
        LocalDateTime end = atEndOfLocalDateTime(date);
        return localDateTimeToDate(end);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime endOfDay = toLocalDateTime(date).with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID_HCM);
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID_HCM).toInstant());
    }

    public static LocalDate localDateFirstDayOfMonth() {
        return YearMonth.now(ZONE_ID_HCM).atDay(1);
    }

    public static LocalDate localDateLastDayOfMonth() {
        return YearMonth.now(ZONE_ID_HCM).atEndOfMonth();
    }

    public static LocalDateTime firstTimeOfMonth(LocalDateTime date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDateTime firstTimeOfMonth = yearMonth.atDay(1).atStartOfDay(ZONE_ID_HCM).toLocalDateTime();

        return firstTimeOfMonth;
    }

    public static LocalDateTime lastTimeOfMonth(LocalDateTime date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDateTime lastTimeOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZONE_ID_HCM).toLocalDateTime();

        return lastTimeOfMonth;
    }

    public static LocalDateTime getStartOfDay() {
        return LocalDateTime.now(ZONE_ID_HCM).toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getEndOfDay() {
        return LocalDateTime.now(ZONE_ID_HCM).toLocalDate().atTime(LocalTime.MAX);
    }

    public static Date getStartOfCurrentDay() {
        return getStartOfDay((Calendar) null);
    }

    public static Date getEndOfCurrentDay() {
        return getEndOfDay((Calendar) null);
    }

    public static Date getStartOfDay(Calendar cal) {
        if (cal == null) {
            cal = Calendar.getInstance();
        }

        setTime(cal, 0, 0, 0, 0);
        return cal.getTime();
    }

    public static Date getEndOfDay(Calendar cal) {
        if (cal == null) {
            cal = Calendar.getInstance();
        }

        setTime(cal, 23, 59, 59, 99);
        return cal.getTime();
    }

    public static void setTime(Calendar cal, int hourOfDay, int minute, int second, int milisecond) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, milisecond);
    }

    public static Date getStartOfDay(Long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        return getStartOfDay(cal);
    }

    public static Date getEndOfDay(Long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        return getEndOfDay(cal);
    }
}