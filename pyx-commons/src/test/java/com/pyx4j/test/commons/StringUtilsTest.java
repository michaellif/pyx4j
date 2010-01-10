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
 * Created on Sep 28, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.test.commons;

import com.pyx4j.commons.CommonsStringUtils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void testd00() {
        assertEquals("00", CommonsStringUtils.d00(0));
        assertEquals("09", CommonsStringUtils.d00(9));
        assertEquals("10", CommonsStringUtils.d00(10));

        assertEquals("000", CommonsStringUtils.formatLong(0));
        assertEquals("009", CommonsStringUtils.formatLong(9));
        assertEquals("010", CommonsStringUtils.formatLong(10));
        assertEquals("099", CommonsStringUtils.formatLong(99));
        assertEquals("100", CommonsStringUtils.formatLong(100));
        assertEquals("1,000", CommonsStringUtils.formatLong(1000));
        assertEquals("1,001", CommonsStringUtils.formatLong(1001));
    }

}
