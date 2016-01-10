/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-04
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

//TODO AsyncEntry
public class SimpleDateFormatImpl {

    public static String format(String pattern, Date date) {
        DateTimeFormat fmt;
        if (pattern == null) {
            fmt = DateTimeFormat.getMediumDateFormat();
            //fmt = DateTimeFormat.getFormat("d-MMM-yyyy");
        } else if (pattern.equals("short")) {
            fmt = DateTimeFormat.getShortDateFormat();
            //03/01/70
            //fmt = DateTimeFormat.getFormat("dd/MM/yy");
        } else if (pattern.equals("medium")) {
            fmt = DateTimeFormat.getMediumDateFormat();
            //3-Jan-1970
            //fmt = DateTimeFormat.getFormat("d-MMM-yyyy");
        } else if (pattern.equals("long")) {
            fmt = DateTimeFormat.getLongDateFormat();
        } else if (pattern.equals("full")) {
            fmt = DateTimeFormat.getFullDateFormat();
        } else if (pattern.equals("epoch")) {
            return String.valueOf(date.getTime() / 1000);
        } else {
            fmt = DateTimeFormat.getFormat(pattern);
        }
        return fmt.format(date);
    }
    
    public static String formatTime(String pattern, Date date) {
        DateTimeFormat fmt;
        if (pattern == null) {
            fmt = DateTimeFormat.getMediumTimeFormat();
        } else if (pattern.equals("short")) {
            fmt = DateTimeFormat.getShortTimeFormat();
        } else if (pattern.equals("medium")) {
            fmt = DateTimeFormat.getMediumTimeFormat();
        } else if (pattern.equals("long")) {
            fmt = DateTimeFormat.getLongTimeFormat();
        } else if (pattern.equals("full")) {
            fmt = DateTimeFormat.getFullTimeFormat();
        } else {
            fmt = DateTimeFormat.getFormat(pattern);
        }
        return fmt.format(date);
    }

    public static Date parse(String text, String pattern) throws IllegalArgumentException {
        return DateTimeFormat.getFormat(pattern).parse(text);
    }
    
    public static Date parseStrict(String text, String pattern) throws IllegalArgumentException {
        return DateTimeFormat.getFormat(pattern).parseStrict(text);
    }

}
