/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Nov 26, 2015
 * @author vlads
 */
package com.pyx4j.commons;

import java.util.Date;

/**
 * Collection of Format function available in GWT and on server.
 */
public class SimpleFormat {

    public static String numberFormat(Number value, String pattern) {
        return SimpleNumberFormatImpl.format(pattern, value);
    }

    public static Number numberParse(String text, String pattern) throws NumberFormatException {
        return SimpleNumberFormatImpl.parse(text, pattern);
    }

    /**
     * Date formatter base on java.text.SimpleDateFormat for non time critical functions
     */
    public static String dateFormat(Date date, String pattern) {
        return SimpleDateFormatImpl.format(pattern, date);
    }

    /**
     * Date parser base on java.text.SimpleDateFormat for non time critical functions
     */
    public static Date dateParse(String text, String pattern) throws IllegalArgumentException {
        return SimpleDateFormatImpl.parse(text, pattern);
    }

}
