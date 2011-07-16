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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.selenium.D;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.financial.Money;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.unit.VistaSeleniumTestBase;
import com.propertyvista.unit.config.ApplicationId;
import com.propertyvista.unit.config.VistaSeleniumTestConfiguration;

abstract class WizardSeleniumTestBase extends VistaSeleniumTestBase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new VistaSeleniumTestConfiguration(ApplicationId.ptapp);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assertNoMessages();
    }

    protected void saveAndContinue() {
        saveAndContinue(true);
    }

    protected void saveAndContinue(boolean checkMessage) {
        if (checkMessage) {
            assertNoMessages();
        }
        String url = selenium.getCurrentUrl();
        selenium.click(CrudDebugId.Crud_Save);
        if (checkMessage) {
            assertNoMessages();
            assertNotEquals("URL did not changed", url, selenium.getCurrentUrl());
        }
    }

    public void assertNoMessages() {
        for (UserMessageType type : UserMessageType.values()) {
            assertNotVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, type));
        }

    }

    public void assertMessages(UserMessageType type) {
        assertVisible(new CompositeDebugId(VistaFormsDebugId.UserMessage_Prefix, type));
    }

    protected User createTestUser() {
        String strNow = new SimpleDateFormat("MMdd-hhmmss").format(Calendar.getInstance().getTime());
        String email = "tst" + strNow + DemoData.USERS_DOMAIN;
        User user = EntityFactory.create(User.class);
        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);
        return user;
    }

    public void assertValueOnForm(IDebugId formDebugId, Money member) {
        assertEquals(member.getMeta().getCaption(), member.amount().getStringView(), selenium.getValue(D.id(formDebugId, member)));
    }

}
