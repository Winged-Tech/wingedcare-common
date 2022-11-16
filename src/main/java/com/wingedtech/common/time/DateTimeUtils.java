package com.wingedtech.common.time;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DateTimeUtils provide public utility methods for the date-time library.
 *
 * @author taozhou
 */
public class DateTimeUtils {

    private static Clock debugClock;

    private static Duration debugTimeOffset;

    /**
     * 开启时间日期的debug模式，为全局时间增加指定的偏移量
     *
     * @param offset
     */
    public static void setDebugTimeOffset(@NotNull Duration offset) {
        setDebugClock(Clock.offset(Clock.systemUTC(), offset));
        debugTimeOffset = offset;
    }

    public static Duration getDebugTimeOffset() {
        return debugTimeOffset;
    }

    /**
     * 开启时间日期的debug模式，为全局时间设置指定的Clock
     */
    public static void setDebugClock(@NotNull Clock clock) {
        debugClock = clock;
    }

    /**
     * 删除时钟debug clock
     */
    public static void removeDebugClock() {
        debugClock = null;
        debugTimeOffset = null;
    }

    public static boolean isDebugClockOn() {
        return debugClock != null;
    }

    /**
     * 获取当前系统UTC时间，返回Date对象
     *
     * @return
     */
    public static Date nowDate() {
        return Date.from(nowInstant());
    }

    /**
     * 获取当前系统UTC时间，返回Instant对象
     *
     * @return
     */
    public static Instant nowInstant() {
        if (isDebugClockOn()) {
            return Instant.now(debugClock);
        }
        return Instant.now();
    }

    /**
     * 获取当前系统UTC时间，返回ZonedDateTime对象
     *
     * @return
     */
    public static ZonedDateTime nowZonedDateTime() {
        if (isDebugClockOn()) {
            return ZonedDateTime.now(debugClock);
        }
        return ZonedDateTime.now();
    }

    /**
     * 获取当前系统UTC时间，返回Calendar对象
     *
     * @return
     */
    public static Calendar nowCalendar() {
        return GregorianCalendar.from(nowZonedDateTime());
    }

    /**
     * 以指定的时区获取当前系统时间，返回Calendar对象
     *
     * @param timeZone
     * @return
     */
    public static Calendar nowCalendar(TimeZone timeZone) {
        final GregorianCalendar gregorianCalendar = GregorianCalendar.from(nowZonedDateTime());
        gregorianCalendar.setTimeZone(timeZone);
        return gregorianCalendar;
    }

    /**
     * 系统的时区ZoneId名称
     */
    public static final String SYSTEM_ZONE_ID_NAME = "Asia/Shanghai";

    /**
     * 系统的时区ZoneId
     */
    public static final ZoneId SYSTEM_ZONE_ID = ZoneId.of(SYSTEM_ZONE_ID_NAME);

    /**
     * 将指定的Instant转换成ZonedDateTime对象
     *
     * @param instant
     * @return
     */
    public static ZonedDateTime getZonedDateTime(Instant instant) {
        return instant.atZone(SYSTEM_ZONE_ID);
    }

    /**
     * 将指定的Instant时间转换成当日的起始时间点（00:00:00）
     *
     * @param instant Instant对象
     * @return
     */
    public static Instant getDayBeginning(Instant instant) {
        ZonedDateTime zonedDateTime = getDayBeginning(getZonedDateTime(instant));
        return Instant.from(zonedDateTime);
    }

    private static ZonedDateTime getDayBeginning(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 根据指定的时间段开始时间以及租期时长，计算租期结束时间
     *
     * @param beginningTime 租期开始时间
     * @param period        租期总时长
     * @return 租期结束时间点
     */
    public static Instant getPeriodEndTime(Instant beginningTime, Period period) {
        return Instant.from(getDayEnd(getDayBeginning(getZonedDateTime(beginningTime)).plus(period).minusDays(1)));
    }

    /**
     * 比较两个Instant对象，返回其中更早的时间对象
     *
     * @param instant1
     * @param instant2
     * @return
     */
    public static Instant getEarlierTime(Instant instant1, Instant instant2) {
        if (instant1.isBefore(instant2)) {
            return instant1;
        } else {
            return instant2;
        }
    }

    /**
     * 比较两个Instant对象，返回其中更晚的时间对象
     *
     * @param instant1
     * @param instant2
     * @return
     */
    public static Instant getLaterTime(Instant instant1, Instant instant2) {
        if (instant1.isAfter(instant2)) {
            return instant1;
        } else {
            return instant2;
        }
    }

    /**
     * 根据指定的Instant时间，获取其第二天的开始时间点
     *
     * @param instant
     * @return
     */
    public static Instant getNextDayBeginning(Instant instant) {
        return getDayBeginning(instant.plus(1, ChronoUnit.DAYS));
    }

    /**
     * 将制定的Instant时间转换成当日的结束时间点（23:59:59）
     *
     * @param instant Instant对象
     * @return
     */
    public static Instant getDayEnd(Instant instant) {
        ZonedDateTime zonedDateTime = getDayEnd(getZonedDateTime(instant));
        return Instant.from(zonedDateTime);
    }

    public static ZonedDateTime getDayEnd(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    /**
     * 获取上月的开始时间戳：1号0点0分0秒
     *
     * @param c
     * @return
     */
    public static Long getLastMonthStartTime(Calendar c) {
        c.add(Calendar.MONTH, -1);
        return getCurrentMonthStartTime(c);
    }

    /**
     * 获取当月的开始时间戳：1号0点0分0秒
     *
     * @param c
     * @return
     */
    public static Long getCurrentMonthStartTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取当月结束时间戳：当月的1号0点0分0秒
     *
     * @param c
     * @return
     */
    public static Long getCurrentMonthEndTime(Calendar c) {
        c.add(Calendar.MONTH, 1);
        return getCurrentMonthStartTime(c);
    }

    /**
     * 通过起止时间获取每个月的1号0点0分0秒的Calendar
     */
    public static List<Calendar> getMonthStartCalendarInPeriod(Long startTime, Long endTime) {
        List<Calendar> calendars = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        while (calendar.getTimeInMillis() <= endTime) {
            Calendar temp = Calendar.getInstance();
            temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            temp.set(Calendar.DAY_OF_MONTH, 1);
            temp.set(Calendar.HOUR_OF_DAY, 0);
            temp.set(Calendar.MINUTE, 0);
            temp.set(Calendar.SECOND, 0);
            calendars.add(temp);
            calendar.add(Calendar.MONTH, 1);
        }
        return calendars;
    }

    /**
     * 获取两个时间点相差的年月日
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Period getBetweenPeriod(Instant startTime, Instant endTime) {
        return Period.between(LocalDateTime.ofInstant(startTime, SYSTEM_ZONE_ID).toLocalDate(), LocalDateTime.ofInstant(endTime, SYSTEM_ZONE_ID).toLocalDate());
    }

    /**
     * 计算预交付时间
     */
    public static LocalDate calculateDays(DayType dayType, Integer days, LocalDate localDate) {
        if (DayType.NATURAL_DAY.equals(dayType)) {
            localDate = localDate.plusDays(days);
        } else if (DayType.WORK_DAY.equals(dayType)) {
            int week = days / 5;
            int day = days % 5;
            localDate = localDate.plusDays(7 * week);
            localDate = plusWorkDay(localDate, day);
        }
        return localDate;
    }


    public Cache<LocalDate, LocalDate> getLocalDateCache(List<LocalDate> localDates) {
        Cache<LocalDate, LocalDate> localDateCache = CacheBuilder.newBuilder().maximumSize(1000).build();
        localDates.forEach(localDate -> localDateCache.put(localDate, localDate));
        return localDateCache;
    }


    private static LocalDate plusWorkDay(LocalDate localDate, int day) {
        while (day > 0) {
            localDate = localDate.plusDays(1);
            if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                localDate = localDate.plusDays(2);
            } else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                localDate = localDate.plusDays(1);
            }
            day--;
        }
        return localDate;
    }

    public static Integer calculateRemainingDays(DayType dayType, LocalDate expectedDate, LocalDate localDate) {
        Integer _days = 0;
        if (DayType.NATURAL_DAY.equals(dayType)) {
            return between(expectedDate, localDate);
        } else if (DayType.WORK_DAY.equals(dayType)) {
            if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                localDate = localDate.minusDays(1);
            } else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                localDate = localDate.minusDays(2);
            }
            int days = between(expectedDate, localDate);
            if (days == 0) {
                return days;
            } else if (days < 0) {
                _days = -calculateRemainingDays(Math.abs(days), expectedDate);
            } else {
                _days = calculateRemainingDays(days, localDate);
            }
        }
        return _days;
    }

    /**
     * 计算剩余交付日(工作日)
     */
    private static Integer calculateRemainingDays(Integer days, LocalDate localDate) {
        int week = days / 7;
        int day = days % 7;
        int _days = 5 * week;
        localDate = localDate.plusDays(7 * week);
        while (day > 0) {
            localDate = localDate.plusDays(1);
            _days += 1;
            if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                localDate = localDate.plusDays(2);
                day -= 2;
            } else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                localDate = localDate.plusDays(1);
                day--;
            }
            day--;
        }
        return _days;
    }

    private static Integer between(LocalDate startDate, LocalDate endDate) {
        if (startDate.isEqual(endDate)) {
            return 0;
        }
        Long days = startDate.toEpochDay() - endDate.toEpochDay();
        return days.intValue();
    }

    public static Integer calculateAge(Instant birthday) {
        LocalDate start = instantToLocalDate(birthday);
        return calculateAge(start);
    }

    public static Integer calculateAge(LocalDate start) {
        LocalDate now = LocalDate.now();
        Long year = start.until(now, ChronoUnit.YEARS);
        return year.intValue();
    }

    public static LocalDate instantToLocalDate(Instant instant) {
        return instant.atZone(SYSTEM_ZONE_ID).toLocalDate();
    }

    public static Instant calculateInstantAfterDays(Integer days, DayType dayType) {
        LocalDate now = LocalDate.now();
        if (DayType.NATURAL_DAY.equals(dayType)) {
            now = now.plusDays(days);
        } else {
            now = plusWorkDay(now, days);
        }
        return now.atStartOfDay(SYSTEM_ZONE_ID).toInstant();
    }

    /**
     * 获取指定时间的上一个整点或半点
     * 分钟在0-29之间，则设置为0 （19：08 -> 19:00）
     * 分钟在30-59之间，则设置为30 (19:38 -> 19:30)
     *
     * @param instant 指定时间
     * @return
     */
    public static Instant getThePreviousWholeOrHalfHour(Instant instant) {

        if (null == instant) {
            return null;
        }

        ZonedDateTime zonedDateTime = getZonedDateTime(instant);
        int minute = zonedDateTime.getMinute();

        if (0 <= minute && minute <= 29) {
            zonedDateTime = zonedDateTime.withMinute(0).withSecond(0).withNano(0);
        } else {
            zonedDateTime = zonedDateTime.withMinute(30).withSecond(0).withNano(0);
        }
        return Instant.from(zonedDateTime);
    }


}
