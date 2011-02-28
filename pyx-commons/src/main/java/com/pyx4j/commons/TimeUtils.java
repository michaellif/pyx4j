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

public class TimeUtils {

    public static int[] MONTH_DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static int[] MONTH_DAYS_LEAP = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static String[] MONTH_NAMES_SHORT = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    public static int maxMonthDays(Date date) {
        int year = date.getYear();
        if (isLeapYear(year)) {
            return MONTH_DAYS_LEAP[date.getMonth()];
        }
        return MONTH_DAYS[date.getMonth()];
    }

    public static boolean isLeapYear(int year) {
        if (year % 4 == 0) // divisible by 4
        {
            if (year % 100 == 0) { // divible by 100 is not a leap year
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
        long msec = since(start);
        long sec = msec / 1000;
        long min = sec / 60;
        sec -= min * 60;
        long h = min / 60;
        min -= h * 60;

        StringBuffer sb;
        sb = new StringBuffer();
        if (h != 0) {
            sb.append(CommonsStringUtils.d00((int) h)).append(":");
        }
        if ((h != 0) || (min != 0)) {
            sb.append(CommonsStringUtils.d00((int) min)).append(".");
        }
        sb.append(CommonsStringUtils.d00((int) sec));
        if ((h == 0) && (min == 0)) {
            sb.append(" sec");
        }
        if ((h == 0) && (min == 0) && (sec <= 1)) {
            msec -= 1000 * sec;
            sb.append(" ");
            sb.append(CommonsStringUtils.d000((int) msec));
            sb.append(" msec");
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

    public static long since(long start) {
        if (start == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - start);
    }

    @SuppressWarnings("deprecation")
    public static Date createDate(int year, int month, int day) {
        return new Date(year, month, day);
    }

    @SuppressWarnings("deprecation")
    public static Date dayStart(Date d) {
        return new Date(d.getYear(), d.getMonth(), d.getDate());
    }

    @SuppressWarnings("deprecation")
    public static Date dayEnd(Date d) {
        return new Date(d.getYear(), d.getMonth(), d.getDate(), 23, 59, 59);
    }

    public static boolean isToday(Date d) {
        return isSameDay(d, new Date());
    }

    @SuppressWarnings("deprecation")
    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        } else {
            return (d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate());
        }
    }

    @SuppressWarnings("deprecation")
    public static String getTimeZoneInfo() {
        return "UTC" + String.valueOf(-(new Date()).getTimezoneOffset() / 60);
    }

    @SuppressWarnings("deprecation")
    public static int getTimezoneOffset() {
        return (new Date()).getTimezoneOffset();
    }

    public static boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

}
