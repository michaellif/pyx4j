/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.portal;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;
import com.propertyvista.unit.VistaBaseSeleniumTestCase;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

abstract class WizardBaseSeleniumTestCase extends VistaBaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.portal);
    }

    protected void saveAndContinue() {
        assertNoMessages();
        String url = selenium.getCurrentUrl();
        selenium.click(CrudDebugId.Crud_Save);
        assertNoMessages();

        assertNotEquals("URL did not changed", url, selenium.getCurrentUrl());
    }

    public void assertNoMessages() {
        for (UserMessageType type : UserMessageType.values()) {
            assertNotVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, type));
        }
    }

}
