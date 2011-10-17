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
 * Created on Oct 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.selenium;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.selenium.UnitTestExecutionTestBase;

public class ExecuteUnitTestsInBrowserTest extends UnitTestExecutionTestBase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteUnitTestsInBrowserTest.class);

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return "http://pyx4j-tester.appspot.com/";
            }

            @Override
            public boolean reuseBrowser() {
                return false;
            }

        };
    }

    public void testUnitTests() throws Exception {
        executeAllClientUnitTests(260);

        ExecutionStatus status = getExecutionStatus();

        log.info("Success Count: {}", status.success);
        log.info("Failed Count: {} ", status.failed);
        log.info("Duration: {} ", status.duration);

        if (status.success == 0) {
            Assert.fail("Success Count is 0");
        }
        Assert.assertEquals("Failed tests Count", 4, status.failed);
    }

}
