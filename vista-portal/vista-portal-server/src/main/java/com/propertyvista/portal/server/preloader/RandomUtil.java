/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pyx4j.essentials.server.preloader.DataGenerator;

public class RandomUtil extends DataGenerator {

    public static Date randomDate() {
        return randomDate(1930, 2020);
    }

    public static Date randomDate(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + randomInt(yearTo - yearFrom));
        calendar.set(Calendar.DAY_OF_YEAR, randomInt(365));

        // TODO have not done minutes/hours for now

        // Note:  Calendar.getTime() return FULL date (with hours/minutes/seconds!) despite it set just year and day...
        // our DatePicker control manipulates just rounded to days dates, so after just moving focus in/out in DatePicker 
        // it seems that it was date change - and some of our checks mis-behave... So I've rounded this calendar date:  
        Date d = calendar.getTime();
        return new Date(d.getYear(), d.getMonth(), d.getDate());
    }

    public static java.sql.Date randomSqlDate(int yearFrom, int yearTo) {
        return new java.sql.Date(randomDate(yearFrom, yearTo).getTime());
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static int randomInt(int max) {
        return random.nextInt(max);
    }

    public static <T> T random(T[] array) {
        if (array.length == 0) {
            return null;
        }
        int index = random.nextInt(array.length);
        return array[index];
    }

    public static <T> T random(List<T> list) {
        if (list.size() == 0) {
            return null;
        }
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    @SuppressWarnings("unchecked")
    public static <T> T random(Class<?> en) {
        return (T) random(en.getEnumConstants());
    }

    //    public static String randomPhone() {
    //        StringBuilder sb = new StringBuilder();
    //
    //        sb.append("(416) ");
    //        sb.append(randomInt(10) * 100 + randomInt(10) * 10 + randomInt(10));
    //        sb.append("-");
    //        sb.append(randomInt(10) * 1000 + randomInt(10) * 100 + randomInt(10) * 10 + randomInt(10));
    //
    //        return sb.toString();
    //    }
}
