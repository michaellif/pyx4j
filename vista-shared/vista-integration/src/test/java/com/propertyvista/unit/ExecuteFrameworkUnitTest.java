/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.selenium.UnitTestExecutionTestBase;

public class ExecuteFrameworkUnitTest extends UnitTestExecutionTestBase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteFrameworkUnitTest.class);

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
