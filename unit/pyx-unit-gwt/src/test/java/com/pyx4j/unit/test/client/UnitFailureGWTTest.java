/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.test.client;

import junit.framework.TestCase;

import com.google.gwt.user.client.Timer;
import com.pyx4j.unit.client.GUnitTester;

/**
 * We only test Failure scenarios here, We have tones of other working cases to test
 * successful results.
 * 
 */
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

    public void testDelayedFailure() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        new Timer() {
            @Override
            public void run() {
                throw new IllegalArgumentException("Expected, Delayed Failed");
            }
        }.schedule(50);
    }

    public void testExpectedException() {
        throw new IllegalArgumentException("Expected, Failed");
    }

}
