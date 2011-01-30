/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2011
 * @author vlads
 * @version $Id$
 */
package org.selenium;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.widgets.client.dialog.DialogDebugId;

public class ExampleV2Test extends BaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return "http://pyx4j-demo.appspot.com/";
            }
        };
    }

    public void testLogin() throws Exception {
        selenium.waitForLinkText("Sign In");

        selenium.click("link=Sign In");
        AuthenticationRequest meta = EntityFactory.getEntityTemplate(AuthenticationRequest.class);

        selenium.waitFor(meta.email());
        captureScreenshot("example");

        selenium.type(meta.email(), "emp001@pyx4j.com");
        selenium.type(meta.password(), "emp001@pyx4j.com");
        selenium.click(DialogDebugId.Dialog_Ok);

        selenium.waitFor(CrudDebugId.Crud_New);
    }
}
