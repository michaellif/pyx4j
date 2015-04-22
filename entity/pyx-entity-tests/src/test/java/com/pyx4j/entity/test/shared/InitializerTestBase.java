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
 * Created on Dec 23, 2009
 * @author vlads
 */
package com.pyx4j.entity.test.shared;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.pyx4j.entity.test.env.ConfigureTestsEnv;

public abstract class InitializerTestBase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ConfigureTestsEnv.configure();
    }

    public static boolean isJavaAssertEnabled() {
        try {
            int i = 2;
            assert i == 3;
            return false;
        } catch (Throwable e) {
            return true;
        }
    }

    static public void assertValueEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        if ((expected instanceof BigDecimal) && (((BigDecimal) expected).compareTo((BigDecimal) actual) == 0)) {
            return;
        }
        failNotEquals(message, expected, actual);
    }
}
