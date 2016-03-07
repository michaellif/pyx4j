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
 * Created on Mar 4, 2016
 * @author vlads
 */
package com.pyx4j.test.commons;

import java.io.Serializable;
import java.text.ParseException;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.formatters.SimpleMessageFormatParser;

import junit.framework.TestCase;

public class SimpleMessageFormatParserTest extends TestCase {

    private void assertMessageFormat(String expected, String pattern, Object argument) throws ParseException {
        String result = SimpleMessageFormat.format(pattern, argument);
        assertEquals(pattern, expected, result);
        Serializable value = new SimpleMessageFormatParser<Serializable>(pattern).parse(result);
        assertEquals(pattern, argument, value);
    }

    public void testDurationParser() throws ParseException {
//        assertMessageFormat("02sec 000msec", "{0,duration}", 2000);
//        assertMessageFormat("02sec 000msec", "{0,duration,msec}", 2000);
        assertMessageFormat("2 seconds", "{0,duration,sec}", 2);
        assertMessageFormat("1 day", "{0,duration,sec}", 24 * Consts.HOURS2SEC);
    }

    public void TODO_testNumberFormat() throws ParseException {
        assertMessageFormat("2,000", "{0,number}", 2000);
        assertMessageFormat("2,000", "{0,number,integer}", 2000);
        assertMessageFormat("2000", "{0,number,#}", 2000);

        assertMessageFormat("12.3%", "{0,number,percent}", 0.123);
        assertMessageFormat("12%", "{0,number,percent}", 0.12);

        //Java default MessageFormat
        //assertMessageFormat("null", "{0,number,#}", (Object) null);
        assertMessageFormat("", "{0,number,#}", (Object) null);

        assertMessageFormat("2000.21", "{0,number,#.##}", 2000.21);
    }

}
