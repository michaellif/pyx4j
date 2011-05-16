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

import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.portal.domain.pt.IPerson;

public class RandomUtil extends DataGenerator {

    public static Date randomDate() {
        return randomDate(1930, 2020);
    }

    public static Date randomDate(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + randomInt(yearTo - yearFrom));
        calendar.set(Calendar.DAY_OF_YEAR, randomInt(365));
        DateUtils.dayStart(calendar);
        return calendar.getTime();
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

    public static java.sql.Date randomYear(int yearFrom, int yearTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom + randomInt(yearTo - yearFrom));
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        DateUtils.dayStart(calendar);
        return new java.sql.Date(calendar.getTime().getTime());
    }

    public static String randomPersonEmail(IPerson person) {
        return person.firstName().getStringView().toLowerCase() + "." + person.lastName().getStringView().toLowerCase() + "@"
                + RandomUtil.random(DemoData.EMAIL_DOMAINS);
    }
}
