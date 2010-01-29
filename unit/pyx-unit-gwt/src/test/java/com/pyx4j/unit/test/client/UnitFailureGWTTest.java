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

    public void testAsyncSuccess() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        new Timer() {
            @Override
            public void run() {
                GUnitTester.finishTest(UnitFailureGWTTest.this);
            }
        }.schedule(150);
    }

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
        }.schedule(150);
    }

    public void testDelayedFailure() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        new Timer() {
            @Override
            public void run() {
                throw new IllegalArgumentException("Expected, Delayed Failed");
            }
        }.schedule(150);
    }

    public void testExpectedException() {
        throw new IllegalArgumentException("Expected, Failed");
    }

}
