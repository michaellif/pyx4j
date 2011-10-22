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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.selenium.SeleniumTestBase;

import com.propertyvista.portal.tester.TestComponentDebugId;
import com.propertyvista.portal.tester.TesterDebugId;
import com.propertyvista.unit.VistaDevLogin;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

public class ButtonTest extends SeleniumTestBase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.tester);
    }

    public void testButtonClickTwice() throws Exception {
        VistaDevLogin.login(selenium);
        selenium.waitFor(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text0"));
        selenium.click(new CompositeDebugId(TesterDebugId.TesterMainMenu, "text0"));

        selenium.click(TestComponentDebugId.CButton, TesterDebugId.StartTestSufix);

        selenium.click(TesterDebugId.ComponentUnderTest);

        assertEquals("Button event Result", "CButton clicked", selenium.getText(TesterDebugId.TestMessage));
        selenium.click(TesterDebugId.TestMessageClear);

        assertEquals("Clear message", "", selenium.getText(TesterDebugId.TestMessage));

        // Test if we can press second time
        selenium.click(TesterDebugId.ComponentUnderTest);
        assertEquals("Button press again event Result", "CButton clicked", selenium.getText(TesterDebugId.TestMessage));

        selenium.click(TesterDebugId.TestMessageClear);
        assertEquals("Clear message", "", selenium.getText(TesterDebugId.TestMessage));

        // Test if we can press third time
        selenium.click(TesterDebugId.ComponentUnderTest);
        assertEquals("Button press again event Result", "CButton clicked", selenium.getText(TesterDebugId.TestMessage));

        selenium.click(TesterDebugId.TestMessageClear);
        assertEquals("Clear message", "", selenium.getText(TesterDebugId.TestMessage));
    }

}
