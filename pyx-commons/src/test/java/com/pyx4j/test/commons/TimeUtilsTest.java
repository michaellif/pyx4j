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
 * @version $Id$
 */
package com.pyx4j.test.commons;

import junit.framework.TestCase;

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
}
