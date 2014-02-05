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
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.selenium.ui;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.selenium.DebugIdBuilder;
import com.pyx4j.site.client.NavigationIDs;
import com.pyx4j.tester.client.TesterSiteMap;
import com.pyx4j.tester.client.domain.CComponentProperties;
import com.pyx4j.tester.client.domain.test.DomainFactory;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.view.form.FormTesterViewImpl;

public class FormBasicTest extends TesterSeleniumTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Navigate to Test Form
        selenium.click(DebugIdBuilder.id(NavigationIDs.Navigation_Item, TesterSiteMap.FormTester.class));
    }

    public void testTextBox() {
        // Verify the form is Empty
        EntityI entityIempty = EntityFactory.create(EntityI.class);
        assertVisible(DebugIdBuilder.id(entityIempty.textBox()));
        assertValueOnForm(entityIempty.textBox());

        // How to click on a Label for a component
        // It will populate component properties form
        selenium.click(DebugIdBuilder.id(DebugIdBuilder.id(proto(EntityI.class).textBox()), WidgetDecorator.DebugIds.Label.debugId()));

        // Set mandatory flag on component properties panel
        selenium.click(DebugIdBuilder.id(proto(CComponentProperties.class).mandatory()));

        // Check mandatory asterisk" visible
        assertVisible(DebugIdBuilder.id(DebugIdBuilder.id(proto(EntityI.class).textBox()), WidgetDecorator.DebugIds.MandatoryImage.debugId()));

        // Click again
        selenium.click(DebugIdBuilder.id(proto(CComponentProperties.class).mandatory()));

        // Check mandatory "asterisk" disappeared
        assertNotVisible(DebugIdBuilder.id(DebugIdBuilder.id(proto(EntityI.class).textBox()), WidgetDecorator.DebugIds.MandatoryImage.debugId()));

        // Click populate button
        selenium.click(DebugIdBuilder.id(FormTesterViewImpl.DebugIds.ButtonPanel, FormTesterViewImpl.DebugIds.PopulateButton));

        EntityI entityIpopulated = DomainFactory.createEntityI();
        // Verify the form is populated with data from factory
        assertValueOnForm(entityIpopulated.textBox());

        // Visible is checked by default. Click to un-check. 
        selenium.click(DebugIdBuilder.id(proto(CComponentProperties.class).visible()));

        // Check that component invisible
        assertNotVisible(DebugIdBuilder.id(proto(EntityI.class).textBox()));

        // Set visible
        selenium.click(DebugIdBuilder.id(proto(CComponentProperties.class).visible()));

        // Check that component visible
        assertVisible(DebugIdBuilder.id(proto(EntityI.class).textBox()));
    }
}
