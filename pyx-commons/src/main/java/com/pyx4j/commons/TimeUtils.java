/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jun 3, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

// GWT requires Date
@SuppressWarnings("deprecation")
public class TimeUtils {

    public static int[] MONTH_DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static int[] MONTH_DAYS_LEAP = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static boolean isOlderThan(final Date bithday, int years) {
        if (bithday == null) {
            return false;
        } else {
            Date now = new Date();
            Date then = TimeUtils.createDate(now.getYear() - years, now.getMonth(), now.getDate());
            // NOTE: Date.getDay() - return day of the WEEK!!?     
            return !bithday.after(then);
            // NOTE: we want today birthday user has been validated!  
        }
    }

    public static int maxMonthDays(Date date) {
        int year = date.getYear();
        if (isLeapYear(year)) {
            return MONTH_DAYS_LEAP[date.getMonth()];
        }
        return MONTH_DAYS[date.getMonth()];
    }

    public static boolean isLeapYear(int year) {
        if (year % 4 == 0) { // divisible by 4
            if (year % 100 == 0) { // divisible by 100 is not a leap year
                return year % 400 == 0; // unless that year is divisible by 400
            }
            return true;
        }
        return false;
    }

    public static String secSince(long start) {
        if (start == 0) {
            return "n/a";
        }
        return durationFormat(since(start));
    }

    public static String durationFormat(long msec) {
        long sec = msec / 1000;
        long min = sec / 60;
        sec -= min * 60;
        long h = min / 60;
        min -= h * 60;

        StringBuffer sb;
        sb = new StringBuffer();
        if (h != 0) {
            sb.append(CommonsStringUtils.d00((int) h)).append("h");
        }
        if ((h != 0) || (min != 0)) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(CommonsStringUtils.d00((int) min)).append("min");
        }

        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(CommonsStringUtils.d00((int) sec));
        sb.append("sec");

        if ((h == 0) && (min == 0) && (sec <= 7)) {
            msec -= 1000 * sec;
            sb.append(' ');
            sb.append(CommonsStringUtils.d000((int) msec));
            sb.append("msec");
        }
        return sb.toString();
    }

    public static String minutesSince(long start) {
        if (start == 0) {
            return "n/a";
        }
        long msec = since(start);
        long sec = msec / 1000;
        long min = sec / 60;
        long h = min / 60;
        min -= h * 60;

        StringBuffer sb;
        sb = new StringBuffer();
        if (h != 0) {
            sb.append(CommonsStringUtils.d00((int) h)).append(":");
            sb.append(CommonsStringUtils.d00((int) min));
        } else {
            sb.append(min);
        }
        if (h == 0) {
            sb.append(" minute");
            if (min != 1) {
                sb.append("s");
            }
        }
        return sb.toString();
    }

    public static int durationParseSeconds(String value) {
        int multiplier = 1;

        Map<String, Integer> timeUnits = new LinkedHashMap<String, Integer>();
        timeUnits.put("months:month", 30 * Consts.DAY2HOURS * Consts.HOURS2SEC);
        timeUnits.put("seconds:second:sec:s", 1);
        timeUnits.put("minutes:minute:min:m", Consts.MIN2SEC);
        timeUnits.put("hours:hour:h", Consts.HOURS2SEC);
        timeUnits.put("days:day:d", Consts.DAY2HOURS * Consts.HOURS2SEC);

        String text = value.trim();

        for (Map.Entry<String, Integer> me : timeUnits.entrySet()) {
            for (String unit : me.getKey().split(":")) {
                if (text.endsWith(unit)) {
                    text = text.substring(0, text.length() - unit.length()).trim();
                    multiplier = me.getValue();
                    break;
                }
            }
        }

        return Integer.valueOf(text.trim()).intValue() * multiplier;
    }

    public static String durationFormatSeconds(int seconds) {
        Map<String, Integer> timeUnits = new LinkedHashMap<String, Integer>();
        timeUnits.put("month:months", 30 * Consts.DAY2HOURS * Consts.HOURS2SEC);
        timeUnits.put("day:days", Consts.DAY2HOURS * Consts.HOURS2SEC);
        timeUnits.put("hour:hours", Consts.HOURS2SEC);
        timeUnits.put("minute:minutes", Consts.MIN2SEC);
        timeUnits.put("second:seconds", 1);

        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, Integer> me : timeUnits.entrySet()) {
            int value = seconds / me.getValue();
            if (value != 0) {
                seconds = seconds - (value * me.getValue());
                if (value == 1) {
                    b.append(" 1 ").append(me.getKey().split(":")[0]);
                } else {
                    b.append(" ").append(value).append(" ").append(me.getKey().split(":")[1]);
                }
            }
        }
        return b.toString();
    }

    public static long since(long start) {
        if (start == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - start);
    }

    public static Date createDate(int year, int month, int day) {
        return new Date(year, month, day);
    }

    public static Date dayStart(Date d) {
        return new Date(d.getYear(), d.getMonth(), d.getDate());
    }

    public static Date dayEnd(Date d) {
        return new Date(d.getYear(), d.getMonth(), d.getDate(), 23, 59, 59);
    }

    public static Date today() {
        return dayStart(new Date());
    }

    public static boolean isToday(Date d) {
        return isSameDay(d, new Date());
    }

    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        } else {
            return (d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate());
        }
    }

    public static String getTimeZoneInfo() {
        return "UTC" + String.valueOf(-(new Date()).getTimezoneOffset() / 60);
    }

    public static int getTimezoneOffset() {
        return (new Date()).getTimezoneOffset();
    }

    public static boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    public static void addDays(Date date, int days) {
        date.setDate(date.getDate() + days);
    }

    /**
     * Date formatter base on java.text.SimpleDateFormat for non time critical functions
     */
    public static String simpleFormat(Date date, String pattern) {
        return SimpleDateFormatImpl.format(pattern, date);
    }

    /**
     * Date parser base on java.text.SimpleDateFormat for non time critical functions
     */
    public static Date simpleParse(String text, String pattern) throws IllegalArgumentException {
        return SimpleDateFormatImpl.parse(text, pattern);
    }
}
