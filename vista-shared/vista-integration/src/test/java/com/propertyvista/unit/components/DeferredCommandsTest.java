/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.components;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

import com.propertyvista.portal.tester.TesterDebugId;
import com.propertyvista.unit.TestUtils;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

public class DeferredCommandsTest extends BaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.tester);
    }

    public void testScheduler() throws Exception {
        VistaDevLogin.login(selenium);
        selenium.waitFor(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text3"));
        selenium.click(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text3"));

        selenium.click(TesterDebugId.DeferredStartProcess);

        selenium.click(TesterDebugId.DeferredCheckStatus);

        TestUtils.sleep(3000);
        assertEquals("executionSeq", "started,ended,scheduleFinally,scheduleEntry,scheduleDeferred,checked,scheduleChecked,",
                selenium.getText(TesterDebugId.DeferredMessageCheck));

    }

}
