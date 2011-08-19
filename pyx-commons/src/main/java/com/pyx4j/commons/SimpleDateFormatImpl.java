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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class in not used in GWT see super-source path="emul" in resources
 */
class SimpleDateFormatImpl {

    public static String format(String pattern, Date date) {
        DateFormat fmt;
        if (pattern == null) {
            fmt = DateFormat.getDateInstance(DateFormat.DEFAULT);
        } else if (pattern.equals("short")) {
            fmt = DateFormat.getDateInstance(DateFormat.SHORT);
        } else if (pattern.equals("medium")) {
            fmt = DateFormat.getDateInstance(DateFormat.DEFAULT);
        } else if (pattern.equals("long")) {
            fmt = DateFormat.getDateInstance(DateFormat.LONG);
        } else if (pattern.equals("full")) {
            fmt = DateFormat.getDateInstance(DateFormat.FULL);
        } else {
            fmt = new SimpleDateFormat(pattern);
        }
        return fmt.format(date);
    }

    public static String formatTime(String pattern, Date date) {
        DateFormat fmt;
        if (pattern == null) {
            fmt = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        } else if (pattern.equals("short")) {
            fmt = DateFormat.getTimeInstance(DateFormat.SHORT);
        } else if (pattern.equals("medium")) {
            fmt = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        } else if (pattern.equals("long")) {
            fmt = DateFormat.getTimeInstance(DateFormat.LONG);
        } else if (pattern.equals("full")) {
            fmt = DateFormat.getTimeInstance(DateFormat.FULL);
        } else {
            fmt = new SimpleDateFormat(pattern);
        }
        return fmt.format(date);
    }

    public static Date parse(String text, String pattern) throws IllegalArgumentException {
        try {
            return new SimpleDateFormat(pattern).parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
