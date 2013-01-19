/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on Jan 19, 2013
 * @author Mykola
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

public class DateAdapter {

    public static Date parseDate(String s) {
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static String printDate(Date dt) {
//        Calendar cal = new GregorianCalendar();
//        cal.setTime(dt);
        //return DatatypeConverter.printDate(cal);
        return new SimpleDateFormat("yyyy-MM-dd").format(dt);
    }

}
