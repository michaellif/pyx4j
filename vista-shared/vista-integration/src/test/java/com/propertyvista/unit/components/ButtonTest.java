/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.components;

import com.propertyvista.portal.tester.TesterDebugId;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

public class ButtonTest extends BaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.tester);
    }

    public void testButtonClickTwice() throws Exception {
        VistaDevLogin.login(selenium);
        selenium.waitFor(TesterDebugId.TesterMainMenu.name() + "-text0").click();

        selenium.click("pyx-CButton-href");

        selenium.click("pyx-testedcomponent");

        assertEquals("Button event Result", "CButton clicked", selenium.getText(TesterDebugId.TestMessage));
        selenium.click(TesterDebugId.TestMessageClear);

        assertEquals("Clear message", "", selenium.getText(TesterDebugId.TestMessage));

        // Test if we can press second time
        selenium.click("pyx-testedcomponent");
        assertEquals("Button press again event Result", "CButton clicked", selenium.getText(TesterDebugId.TestMessage));

    }

}
