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
package com.pyx4j.test.commons;

import java.util.Date;

import junit.framework.TestCase;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.IStringView;
import com.pyx4j.commons.SimpleMessageFormat;

public class SimpleMessageFormatTest extends TestCase {

    private enum FormatTestEnum {

        mouse, rat, rabbit, chimp;

        @Override
        public String toString() {
            // this is to ensure the implementation doesn't compare enums the wrong way 
            return "The " + this.name().toLowerCase();
        };

    }

    private class FormatedObject implements IStringView {

        private final String value;

        FormatedObject(String value) {
            this.value = value;
        }

        @Override
        public String getStringView() {
            return "The " + value;
        }

        @Override
        public String toString() {
            return "@X " + value;
        }

    }

    private void assertMessageFormat(String expected, String pattern, Object... arguments) {
        String result = SimpleMessageFormat.format(pattern, arguments);
        assertEquals(pattern, expected, result);
    }

    public void testReplacements() {
        assertMessageFormat("A, B, C", "{0}, {1}, {2}", "A", "B", "C");
        assertMessageFormat("A, B, A", "{0}, {1}, {0}", "A", "B", "C");

        //Java default MessageFormat
        //assertMessageFormat("A, null, C", "{0}, {1}, {2}", "A", null, "C");
        assertMessageFormat("A, , C", "{0}, {1}, {2}", "A", null, "C");

        assertMessageFormat("{0}", "'{0}'", "A");
        assertMessageFormat("\"A\"", "\"{0}\"", "A");
        assertMessageFormat("'A'", "''{0}''", "A");
        assertMessageFormat("[A]", "[{0}]", "A");

        assertMessageFormat("As", "A's");
        assertMessageFormat("A's", "A''s");
        assertMessageFormat("A's B'z X", "A''s B''z {0}", "X");
    }

    public void testQuotedStrings() {
        assertMessageFormat("As", "A's");
        assertMessageFormat("A{}e", "A'{}'e");
    }

    public void testErrorParents() {
        try {
            assertMessageFormat("n/a", "A{}B");
            fail("Error expected");
        } catch (Throwable ok) {
        }
        assertMessageFormat("As Be", "A's' {0}e", "B");
        assertMessageFormat("As {0}e", "A's {0}e", "B");
        assertMessageFormat("As '{0}'e", "A's ''{0}''e", "B");
    }

    public void testNumberFormat() {
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

    public void testFormatObjects() {
        assertMessageFormat("The mouse", "{0}", new FormatedObject("mouse"));
    }

    public void testChoiceFormat() {
        assertMessageFormat("One", "{0,choice,0#Zero|1#One}", 1);
        assertMessageFormat("1st", "{0,choice,1#{0}st|2#{0}nd}", 1);
        assertMessageFormat("2st", "{0,choice,1#{1}st|2#{1}nd}", 1, 2);
        assertMessageFormat("2nd", "{0,choice,1#{1}st|2#{1}nd}", 2, 2);

        String pattern10x = "{0,choice,9#{0}|9<10 or more}";
        assertMessageFormat("7", pattern10x, 7);
        assertMessageFormat("9", pattern10x, 9);
        assertMessageFormat("10 or more", pattern10x, 10);

        // Boolean
        assertMessageFormat("Yes", "{0,choice,0#No|1#Yes}", Boolean.TRUE);
        assertMessageFormat("No", "{0,choice,null#May|0#No|1#Yes}", Boolean.FALSE);
        assertMessageFormat("May", "{0,choice,null#May|0#No|1#Yes}", (Boolean) null);

        // Strings
        assertMessageFormat("A", "{0}{1,choice,null#|!null#, {1}}", "A", null);
        assertMessageFormat("A", "{0}{1,choice,null#|!null#, {1}}", "A", "");
        assertMessageFormat("A, B", "{0}{1,choice,null#|!null#, {1}}", "A", "B");

        String pattern = "{0,choice,-1#is negative|0#is zero or fraction|1#is one|1.0<is 1+|2#is two|3<is more than 3}";
        assertMessageFormat("is negative", pattern, -2);
        assertMessageFormat("is negative", pattern, -1);
        assertMessageFormat("is negative", pattern, -0.1);
        assertMessageFormat("is zero or fraction", pattern, 0);
        assertMessageFormat("is zero or fraction", pattern, 0.1);
        assertMessageFormat("is one", pattern, 1);
        assertMessageFormat("is 1+", pattern, 1.1);
        assertMessageFormat("is two", pattern, 2);
        assertMessageFormat("is two", pattern, 2.1);
        assertMessageFormat("is two", pattern, 3);
        assertMessageFormat("is more than 3", pattern, 3.1);
    }

    public void testFormatEnums() {
        assertMessageFormat("The mouse", "{0}", FormatTestEnum.mouse);
    }

    public void testChoiceFormatEnums() {
        // Enums
        assertMessageFormat("Mickey", "{0,choice,mouse#Mickey|chimp#Caesar|rabbit#Wabbit}", FormatTestEnum.mouse);
        assertMessageFormat("Caesar", "{0,choice,mouse#Mickey|chimp#Caesar|rabbit#Wabbit}", FormatTestEnum.chimp);
        assertMessageFormat("Wabbit", "{0,choice,mouse#Mickey|chimp#Caesar|rabbit#Wabbit}", FormatTestEnum.rabbit);
        assertMessageFormat("hole", "{0,choice,mouse#Mickey|chimp#Caesar|rabbit#{1}}", FormatTestEnum.rabbit, "hole");
    }

    public void testChoiceFormatNulls() {
        assertMessageFormat("Nil", "{0,choice,null#Nil|0#Zero|1#One}", (Object) null);

        @SuppressWarnings("deprecation")
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));

        assertMessageFormat("txt (date: 70-01-03)", "txt {0,choice,null#|!null#(date: {0,date,yy-MM-dd})}", date);

        assertMessageFormat("txt ", "txt {0,choice,null#|!null#(date: {0,date,yy-MM-dd})}", (Date) null);
    }

    public void testChoiceFormatNested() {
        String nestedPattern = "{0,choice,0#{1,choice,0#Any|0<Less than {1}}|0<{1,choice,0#{0} or more|0<From {0} to {1}}}";
        assertMessageFormat("Any", nestedPattern, 0, 0);
        assertMessageFormat("Less than 1", nestedPattern, 0, 1);
        assertMessageFormat("1 or more", nestedPattern, 1, 0);
        assertMessageFormat("From 1 to 2", nestedPattern, 1, 2);
    }

    public void testDateFormat() {
        @SuppressWarnings("deprecation")
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("January 1970", "{0,date,MMMM yyyy}", date);
        assertMessageFormat("Saturday, January 3, 1970", "{0,date,EEEE, MMMM d, yyyy}", date);

        assertMessageFormat("", "{0,date,short}", (Date) null);
    }

    /**
     * This can't be tested in generic way. Since we don't want to define java.util.Locale
     * in this implementation.
     */
    public void Off_testDateFormatLocale() {
        @SuppressWarnings("deprecation")
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("3-Jan-1970", "{0,date}", date);
        assertMessageFormat("03/01/70", "{0,date,short}", date);
        assertMessageFormat("3-Jan-1970", "{0,date,medium}", date);
        assertMessageFormat("January 3, 1970", "{0,date,long}", date);
    }

    public void testTimeFormat() {
        @SuppressWarnings("deprecation")
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("4:10:00", "{0,time,H:mm:ss}", date);
    }

    public void Off_testTimeFormatLocale() {
        @SuppressWarnings("deprecation")
        int offset = (new Date(0)).getTimezoneOffset();
        Date date = new Date(Consts.MIN2MSEC * (offset + (((3 - 1) * Consts.DAY2HOURS + 4) * Consts.HOURS2MIN) + 10));
        assertMessageFormat("4:10:00 AM", "{0,time}", date);
        assertMessageFormat("4:10 AM", "{0,time,short}", date);
        assertMessageFormat("4:10:00 AM", "{0,time,medium}", date);
        //assertMessageFormat("4:10:00 EST AM", "{0,time,long}", date);
        //assertMessageFormat("4:10:00 o'clock AM EST", "{0,time,full}", date);
    }
}
