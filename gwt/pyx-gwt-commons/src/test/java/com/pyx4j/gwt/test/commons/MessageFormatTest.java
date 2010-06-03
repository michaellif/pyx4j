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
 * Created on 2010-05-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.test.commons;

import java.text.MessageFormat;
import java.util.Date;

import junit.framework.TestCase;

import com.pyx4j.commons.Consts;

//import com.pyx4j.gwt.emul.java.text.MessageFormat;

public class MessageFormatTest extends TestCase {

    private void assertMessageFormat(String expected, String pattern, Object... arguments) {
        String result = MessageFormat.format(pattern, arguments);
        assertEquals(pattern, expected, result);
    }

    public void testReplacements() {
        assertMessageFormat("A, B, C", "{0}, {1}, {2}", "A", "B", "C");
        assertMessageFormat("A, B, A", "{0}, {1}, {0}", "A", "B", "C");
        assertMessageFormat("A, null, C", "{0}, {1}, {2}", "A", null, "C");

        assertMessageFormat("{0}", "'{0}'", "A");
    }

    public void testNumberFormat() {
        assertMessageFormat("2,000", "{0,number}", 2000);
        assertMessageFormat("2,000", "{0,number,integer}", 2000);
        assertMessageFormat("2000", "{0,number,#}", 2000);
        assertMessageFormat("null", "{0,number,#}", (Object) null);
        assertMessageFormat("2000.21", "{0,number,#.##}", 2000.21);
    }

    public void TODO_testChoiceFormat() {
        assertMessageFormat("One", "{0,choice,0#Zero|1#One}", 1);
    }

    /**
     * This can't be tested in generic way. Since we don't want to define java.util.Locale
     * in this implementation.
     */
    public void Off_testDateFormat() {
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("3-Jan-1970", "{0,date}", date);
        assertMessageFormat("03/01/70", "{0,date,short}", date);
        assertMessageFormat("3-Jan-1970", "{0,date,medium}", date);
        assertMessageFormat("January 3, 1970", "{0,date,long}", date);
        assertMessageFormat("January 1970", "{0,date,MMMM yyyy}", date);
        assertMessageFormat("Saturday, January 3, 1970", "{0,date,full}", date);
    }

    public void Off_testTimeFormat() {
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("4:10:00 AM", "{0,time}", date);
        assertMessageFormat("4:10 AM", "{0,time,short}", date);
        assertMessageFormat("4:10:00 AM", "{0,time,medium}", date);
        //assertMessageFormat("4:10:00 EST AM", "{0,time,long}", date);
        //assertMessageFormat("4:10:00 o'clock AM EST", "{0,time,full}", date);
    }
}
