package com.example.demodatn.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.example.demodatn.constant.Error;
import com.example.demodatn.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


/**
 * The type Date time utils.
 */
public class DateTimeUtils {

    public static String Z = "Z";

    public static String Z_OFFSET = "+00:00";
    public static final String TIME_MILLISECONDS_PATTERN = "^(0[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9]):([0-9]{1,3})$";

    /**
     * The constant YYYYMMDDhhmmss.
     */
    public static String YYYYMMDDhhmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static String YYYYMMDDhhmmssSSS1 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static String YYYYMMDDhhmmssSSS2 = "yyyy-MM-dd HH:mm:ss";
    public static String MMddyyy = "MM/dd/yyy";
    public static String YYYYMMDD = "yyyy-MM-dd";
    public static String hhmmssSSS = "HH:mm:ss:SSS";
    public static String MMDDYYYY = "MM-dd-yyyy";
    private final static Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

    /**
     * Convert String To Date Or Null
     *
     * @param date    the date
     * @param pattern the pattern
     * @return Date date
     */
    public static Date convertStringToDateOrNull(String date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            logger.error(StringUtils.buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * Convert Date to String
     *
     * @param date    the date
     * @param pattern the pattern
     * @return Date string
     */
    public static String convertDateToStringOrEmpty(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.format(date);
        } catch (Exception e) {
            logger.error(StringUtils.buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return "";
        }
    }

    /**
     * Get current date with timeZone UTC
     *
     * @return current date with utc
     * @throws ParseException the parse exception
     */
    public static Date getCurrentDateWithUTC() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDhhmmssSSS);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localDateFormat = new SimpleDateFormat(YYYYMMDDhhmmssSSS);
        return localDateFormat.parse(simpleDateFormat.format(new Date()));
    }

    /**
     * Format time to string string.
     *
     * @param date the date
     * @param time the time
     * @return the date
     */
    public static Date convertToDateByFormatDateTimeOrNull(Date date, String time) {
        try {
            String dateFm = convertDateToStringOrEmpty(date, YYYYMMDD);
            SimpleDateFormat sdf = new SimpleDateFormat(hhmmssSSS);
            sdf.parse(time);
            String resultFm = "%s %s";
            return convertStringToDateOrNull(String.format(resultFm, dateFm, time), YYYYMMDDhhmmssSSS);
        } catch (Exception e) {
            logger.error(StringUtils.buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }


    /**
     * Gets different time to time.
     *
     * @param timeTo   the time to
     * @param timeFrom the time from
     * @param unit     the unit
     * @return the different time to time
     */
    public static long getDifferentTimeToTime(Date timeTo, Date timeFrom, ChronoUnit unit) {
        long diff = timeFrom.getTime() - timeTo.getTime();

        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        switch (unit) {
            case SECONDS:
                return diffSeconds;
            case MINUTES:
                return diffMinutes;
            case HOURS:
                return diffHours;
            case DAYS:
                return diffDays;
        }
        return 0;
    }

    /**
     * Format time to string string.
     *
     * @param date    the date
     * @param time    the time
     * @return the string
     */
    public static String convertStringByFormatDateTimeOrNull(Date date, String time) {
        try {
            String dateFm = convertDateToStringOrEmpty(date, YYYYMMDD);
            SimpleDateFormat sdf = new SimpleDateFormat(hhmmssSSS);
            sdf.parse(time);
            String resultFm = "%s %s";
            Date result = convertStringToDateOrNull(String.format(resultFm, dateFm, time), YYYYMMDDhhmmssSSS);
            return convertDateToStringOrEmpty(result, YYYYMMDDhhmmssSSS1);

        } catch (Exception e) {
            logger.error(StringUtils.buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return null;
        }
    }

    /**
     * Convert time utc to time zone long.
     *
     * @param date     the date
     * @param timeZone the time zone
     * @return the long
     */
    public static long convertTimeUTCToTimeZoneToGetTimeDiffrence(Date date, String timeZone) {
        try {
            //Get time difference between 2 TZ contains time DST
            Instant dateUtc = date.toInstant();
            ZoneId zone = ZoneId.of(timeZone);
            // get offset
            ZonedDateTime time = ZonedDateTime.ofInstant(dateUtc, zone);
            ZoneOffset zoneOffset = time.getOffset();
            return zoneOffset.getTotalSeconds() * 1000;
        } catch (Exception e) {
            logger.error(StringUtils.buildLog(e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return 0L;
        }
    }

    /**
     * Add time to date date.
     *
     * @param date the date
     * @param time the time
     * @return the date
     */
    public static Date addTimeToDate(Date date, String time) {
        if ((!StringUtils.validateStringFormat(time, TIME_MILLISECONDS_PATTERN))) {
            logger.error(StringUtils.buildLog(Error.TIME_INVALID,
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            throw new CustomException(Error.TIME_INVALID.getMessage(), Error.TIME_INVALID.getCode(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String[] timeSplit = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, StringUtils.convertStringToIntegerOrNull(timeSplit[0]));
        calendar.add(Calendar.MINUTE, StringUtils.convertStringToIntegerOrNull(timeSplit[1]));
        calendar.add(Calendar.SECOND, StringUtils.convertStringToIntegerOrNull(timeSplit[2]));
        calendar.add(Calendar.MILLISECOND, StringUtils.convertStringToIntegerOrNull(timeSplit[3]));
        return new Date(calendar.getTime().getTime());
    }

//    public static Long convertHHMMSSSSSToMilliSecond(String duration) {
//        if (duration == null || (!StringUtils.validateStringFormat(duration, TIME_MILLISECONDS_PATTERN))) {
//            return null;
//        }
//        String hhmmss = duration.substring(0, duration.lastIndexOf(ConstantDefine.COLON_SYMBOL));
//        String sss = duration.substring(duration.lastIndexOf(ConstantDefine.COLON_SYMBOL) + 1);
//        LocalTime localTime = LocalTime.parse(hhmmss);
//        Long millis = localTime.toSecondOfDay() * 1000l + StringUtils.convertObjectToLongOrNull(sss);
//        return millis;
//    }

}
