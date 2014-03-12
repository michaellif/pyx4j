/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 12, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.poc.dls;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DaylightSavingFindProblemDays {

    public static void main(String[] args) throws Exception {

        Date start = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2009-03-08");

        for (int days = 0; days < 2000; days++) {

            Calendar c = Calendar.getInstance();
            c.setTime(start);
            c.add(Calendar.DAY_OF_YEAR, days);

            for (int h = 0; h <= 23; h++) {
                DaylightSavingJDBC.testHour = h;
                String hh = String.valueOf(h);
                if (hh.length() < 2) {
                    hh = "0" + hh;
                }
                DaylightSavingJDBC.dateTimeInSaylightSavingDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(c.getTime()) + " " + hh + ":21:28";

                try {
                    DaylightSavingJDBC.main(args);
                } catch (Throwable e) {
                    System.err.println(e.getMessage());
                }
            }

        }

    }
}
