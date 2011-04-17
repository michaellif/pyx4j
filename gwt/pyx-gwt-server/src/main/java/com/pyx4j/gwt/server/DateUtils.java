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
 * Created on Jun 25, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.pyx4j.commons.TimeUtils;

public class DateUtils extends TimeUtils {

    /**
     * Quickly create a date object
     * 
     * @param year
     * @param month
     *            starts with 0, January == 0
     * @param day
     * @return new Date object
     */
    public static Date createDate(int year, int month, int day) {
        Calendar c = new GregorianCalendar();

        dayStart(c);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }

    public static void dayStart(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    public static void dayEnd(Calendar calendar) {
        dayStart(calendar);
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.SECOND, -1);
    }

    public static Date yearsAdd(Date date, int years) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.YEAR, years);

        return c.getTime();
    }

    public static boolean isSameDay(Calendar d1, Calendar d2) {
        if (d1 == null || d2 == null) {
            return false;
        } else {
            return (d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR) && d1.get(Calendar.DAY_OF_YEAR) == d2.get(Calendar.DAY_OF_YEAR));
        }
    }

    public static Date detectDateformat(String str) {
        final String[] formats = {

        "yyyy-MM-dd HH:mm:ss.SSS",

        "yyyy-MM-dd HH:mm:ss",

        "yyyy-MM-dd HH:mm",

                // We are North America after all.

                "MM-dd-yyyy HH:mm:ss.SSS",

                "MM-dd-yyyy HH:mm:ss",

                "MM-dd-yyyy HH:mm",

                "MM/dd/yyyy HH:mm:ss.SSS",

                "MM/dd/yyyy HH:mm:ss",

                "MM/dd/yyyy HH:mm",

                "MM-dd-yyyy",

                "yyyy-MM-dd",

                "MM/dd/yyyy", "M/dd/yyyy", "MM/d/yyyy", "M/d/yyyy",

                "MMM-dd-yyyy", "MMM/dd/yyyy", "dd-MMM-yyyy", };

        str = str.trim();

        if (str.equalsIgnoreCase("sysdate") || str.equalsIgnoreCase("now")) {
            return new Date();
        }

        boolean addOneSecond = false;
        if (str.endsWith("24:00:00")) {
            int idx = str.indexOf("24:00:00");
            str = str.substring(0, idx) + "23:59:59";
            addOneSecond = true;
        } else if (str.endsWith("24:00")) {
            int idx = str.indexOf("24:00");
            str = str.substring(0, idx) + "23:59:59";
            addOneSecond = true;
        }

        for (int i = 0; i < formats.length; i++) {
            try {
                SimpleDateFormat aFormat = new SimpleDateFormat(formats[i], Locale.ENGLISH);
                Date dateObj = aFormat.parse(str);
                if (!str.equals(aFormat.format(dateObj))) {
                    continue;
                }
                if (addOneSecond) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateObj);
                    calendar.add(Calendar.SECOND, 1);
                    return calendar.getTime();
                }
                return dateObj;
            } catch (ParseException ignore) {
                continue;
            }
        }
        throw new RuntimeException("undetected date format [" + str + "]");
    }
}
