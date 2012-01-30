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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.selenium.D;
import com.pyx4j.tester.client.domain.test.DomainFactory;
import com.pyx4j.tester.client.domain.test.EntityI;

public class FormBasicTest extends TesterSeleniumTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Naviage to Test Form

        // TODO Use Debug Id
        //selenium.click("id=gwt-uid-2");
        // TODO Use Debug Id
        //selenium.click("gwt-debug-Navigation_Item-form_tester");
        // HAck to make it work for demo
        selenium.navigate().to(selenium.getCurrentUrl() + "#form_tester?formtype=FormBasic");
        selenium.waitWhileWorking();

    }

    public void testTextBox() {
        // Verify the form is Empty
        EntityI entityIempty = EntityFactory.create(EntityI.class);
        assertVisible(D.id(entityIempty.textBox()));
        assertValueOnForm(entityIempty.textBox());

        // TODO Use Debug Id 
        selenium.click("gwt-debug-buttonPanle-populateButton-label");

        EntityI entityIpopulated = DomainFactory.createEntityI();
        // Verify the form is populated with data from factory
        assertValueOnForm(entityIpopulated.textBox());
    }
}
