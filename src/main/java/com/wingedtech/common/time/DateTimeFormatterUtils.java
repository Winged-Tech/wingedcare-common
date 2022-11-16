package com.wingedtech.common.time;

import com.google.common.base.Strings;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtils {

    private static final String FORMAT_YMD = "yyyy-MM-dd";
    private static final String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    private static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_YMDHMSS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String FORMAT_YMDHMS_TRIME = "yyyyMMddHHmmss";

    /**
     * 系统的时区ZoneId名称
     */
    private static final String SYSTEM_ZONE_ID_NAME = "Asia/Shanghai";

    /**
     * 系统的时区ZoneId
     */
    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.of(SYSTEM_ZONE_ID_NAME);

    public static String format(Instant instant, String format) {
        if (instant == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, SYSTEM_ZONE_ID);
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime parse(String time, String format) {
        if (Strings.isNullOrEmpty(time)) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * 将 instant 格式化为 2018-11-11 字符串
     */
    public static String formatYMD(Instant instant) {
        return format(instant, FORMAT_YMD);
    }

    /**
     * 将 instant 格式化为 2018-11-11 22:22 字符串
     */
    public static String formatYMDHM(Instant instant) {
        return format(instant, FORMAT_YMDHM);
    }

    /**
     * 将 instant 格式化为 2018-11-11 22:22:22 字符串
     */
    public static String formatYMDHMS(Instant instant) {
        return format(instant, FORMAT_YMDHMS);
    }

    /**
     * 将 instant 格式化为 20181111222222 字符串
     */
    public static String formatYMDHMSTrim(Instant instant) {
        return format(instant, FORMAT_YMDHMS_TRIME);
    }

    /**
     * 将 2018-11-11 转换为 instant
     */
    public static Instant toInstantYMD(String time) {
        return toInstant(time, FORMAT_YMD);
    }

    /**
     * 将 2018-11-11 22:22 转换为 instant
     */
    public static Instant toInstantYMDHM(String time) {
        return toInstant(time, FORMAT_YMDHM);
    }

    /**
     * 将 2018-11-11 22:22:22 转换为 instant
     */
    public static Instant toInstantYMDHMS(String time) {
        return toInstant(time, FORMAT_YMDHMS);
    }

    /**
     * 将 2018-11-11 22:22:22.320 转换为 instant
     */
    public static Instant toInstantYMDHMSS(String time) {
        return toInstant(time, FORMAT_YMDHMSS);
    }

    /**
     * 将时间字符串 转换为Instant
     *
     * @param format 时间
     * @return Instant
     */
    public static Instant toInstant(String time, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime parse = LocalDateTime.parse(time, dateTimeFormatter);
        return parse.toInstant(ZoneOffset.UTC);
    }
}
