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
package com.propertvista.generator.util;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.propertvista.generator.PreloadData;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.person.Name;

public class RandomUtil extends DataGenerator {

    public static Date randomDate() {
        return randomDate(1930, 2020);
    }

    public static Date randomDate(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + ((yearTo - yearFrom) > 0 ? randomInt(yearTo - yearFrom) : 0));
        calendar.set(Calendar.DAY_OF_YEAR, randomInt(365));
        DateUtils.dayStart(calendar);
        return calendar.getTime();
    }

    public static LogicalDate randomLogicalDate() {
        return randomLogicalDate(1930, 2020);
    }

    public static LogicalDate randomLogicalDate(int yearFrom, int yearTo) {
        return new LogicalDate(randomDate(yearFrom, yearTo));
    }

    public static java.sql.Date randomSqlDate() {
        return randomSqlDate(1930, 2020);
    }

    public static java.sql.Date randomSqlDate(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + randomInt(yearTo - yearFrom));
        calendar.set(Calendar.DAY_OF_YEAR, randomInt(365));
        DateUtils.dayStart(calendar);
        return new java.sql.Date(calendar.getTime().getTime());
    }

    public static LogicalDate randomYear(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + randomInt(yearTo - yearFrom));
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        DateUtils.dayStart(calendar);
        return new LogicalDate(calendar.getTime().getTime());
    }

    public static String randomPersonEmail(Name person) {
        return person.firstName().getStringView().toLowerCase() + "." + person.lastName().getStringView().toLowerCase() + "@"
                + DataGenerator.random(PreloadData.EMAIL_DOMAINS);
    }

    public static Time randomTime() {
        return new Time(randomInt(86400 * 1000));
    }

    /**
     * Take random sample of elements from the list.
     * 
     * @param random
     *            random number generator
     * @param list
     *            list that contains the elements
     * @param sampleSize
     * @return new allocated list that contains the requested number of elements
     */
    public static <T> List<T> randomChoice(Random random, List<T> list, int sampleSize) {
        List<T> copy = new ArrayList<T>(list);

        if (sampleSize >= copy.size()) {
            return copy;
        } else {
            List<T> result = new ArrayList<T>(sampleSize);
            final int minIndex = list.size() - sampleSize;
            for (int lastIndex = list.size() - 1; lastIndex >= minIndex; --lastIndex) {
                int i = random.nextInt(lastIndex + 1);
                result.add(copy.get(i));
                copy.set(i, copy.get(lastIndex));
            }
            return result;
        }
    }

}
