/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
