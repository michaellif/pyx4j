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
 * Created on 2011-02-27
 * @author vlads
 */
package com.pyx4j.test.commons;

import junit.framework.TestCase;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;

public class TimeUtilsTest extends TestCase {

    public void testRange() {
        assertTrue("startDate included",
                TimeUtils.isWithinRange(TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 4)));
        assertTrue("endDate included",
                TimeUtils.isWithinRange(TimeUtils.createDate(2011, 1, 4), TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 4)));

        assertTrue("middle", TimeUtils.isWithinRange(TimeUtils.createDate(2011, 1, 3), TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 4)));

        assertFalse("before", TimeUtils.isWithinRange(TimeUtils.createDate(2010, 12, 31), TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 4)));

        assertFalse("after", TimeUtils.isWithinRange(TimeUtils.createDate(2010, 1, 5), TimeUtils.createDate(2011, 1, 1), TimeUtils.createDate(2011, 1, 4)));
    }

    public void testLogicalDateOperations() {
        assertTrue("<", new LogicalDate(2000, 1, 3).lt(new LogicalDate(2000, 1, 4)));
        assertFalse("<", new LogicalDate(2000, 1, 3).lt(new LogicalDate(2000, 1, 3)));
        assertFalse("<", new LogicalDate(2000, 1, 3).lt(new LogicalDate(2000, 1, 2)));

        assertTrue("<=", new LogicalDate(2000, 1, 3).le(new LogicalDate(2000, 1, 4)));
        assertTrue("<=", new LogicalDate(2000, 1, 3).le(new LogicalDate(2000, 1, 3)));
        assertFalse("<=", new LogicalDate(2000, 1, 3).le(new LogicalDate(2000, 1, 2)));

        assertTrue(">", new LogicalDate(2000, 1, 3).gt(new LogicalDate(2000, 1, 2)));
        assertFalse(">", new LogicalDate(2000, 1, 3).gt(new LogicalDate(2000, 1, 3)));
        assertFalse(">", new LogicalDate(2000, 1, 3).gt(new LogicalDate(2000, 1, 4)));

        assertTrue(">=", new LogicalDate(2000, 1, 3).ge(new LogicalDate(2000, 1, 2)));
        assertTrue(">=", new LogicalDate(2000, 1, 3).ge(new LogicalDate(2000, 1, 3)));
        assertFalse(">=", new LogicalDate(2000, 1, 3).ge(new LogicalDate(2000, 1, 4)));
    }

    public void testDurationParse() {
        assertEquals(100, TimeUtils.durationParseSeconds("100"));
        assertEquals(10, TimeUtils.durationParseSeconds("10 sec"));
        assertEquals(10, TimeUtils.durationParseSeconds("10 seconds"));

        assertEquals(60, TimeUtils.durationParseSeconds("1 min"));
        assertEquals(70, TimeUtils.durationParseSeconds("1 min 10 sec"));
        assertEquals(2 * 60 * 60, TimeUtils.durationParseSeconds("2 hours"));
        assertEquals(2 * 60 * 60 + 1, TimeUtils.durationParseSeconds("2 hours 1 s"));
    }
}
