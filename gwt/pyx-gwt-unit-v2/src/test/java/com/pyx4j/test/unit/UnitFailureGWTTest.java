/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id: UnitFailureGWTTest.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.test.unit;

import junit.framework.TestCase;

import com.google.gwt.user.client.Timer;
import com.pyx4j.unit.GUnitTester;

public class UnitFailureGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    public void testCompareFailure() {
        assertEquals("Expected, Failed", "Bob", "John");
    }

    public void testAsyncFailure() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        new Timer() {
            @Override
            public void run() {
                fail("Expected, Failed");
            }
        }.schedule(50);
    }

    public void testExpectedException() {
        throw new IllegalArgumentException("Expected, Failed");
    }

}
