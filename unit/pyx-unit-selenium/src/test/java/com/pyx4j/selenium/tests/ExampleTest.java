/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 29, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.selenium.tests;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;
import com.pyx4j.selenium.SeleniumTestBase;
import com.pyx4j.widgets.client.dialog.DialogDebugId;

public class ExampleTest extends SeleniumTestBase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            //            @Override
            //            public String getTestUrl() {
            //                return "http://pyx4j-demo.appspot.com/";
            //            }
        };
    }

    /**
     * This is what Selenium IDE generates
     */
    public void testLoginGenerated() throws Exception {
        selenium.waitForLinkText("Sign In");

        selenium.click("link=Sign In");

        selenium.waitFor("gwt-debug-AuthenticationRequest$email");

        selenium.type("gwt-debug-AuthenticationRequest$email", "emp001@pyx4j.com");
        selenium.type("gwt-debug-AuthenticationRequest$password", "emp001@pyx4j.com");
        selenium.click("gwt-debug-Dialog_Ok");

        selenium.waitFor("gwt-debug-Crud_New");
    }

    /**
     * This is slightly optimized to avoid GWT prefix
     */
    public void testLogin4GWT() throws Exception {
        selenium.waitForLinkText("Sign In");

        selenium.click("link=Sign In");

        selenium.waitFor("AuthenticationRequest$email");

        selenium.type("AuthenticationRequest$email", "emp001@pyx4j.com");
        selenium.type("AuthenticationRequest$password", "emp001@pyx4j.com");
        selenium.click("Dialog_Ok");

        selenium.waitFor("Crud_New");
    }

    /**
     * version for pyx
     */
    public void testLogin4Pyx() throws Exception {
        selenium.waitForLinkText("Sign In");

        selenium.click("link=Sign In");
        AuthenticationRequest meta = EntityFactory.getEntityPrototype(AuthenticationRequest.class);

        selenium.waitFor(meta.email());
        captureScreenshot("example");

        selenium.type(meta.email(), "emp001@pyx4j.com");
        selenium.type(meta.password(), "emp001@pyx4j.com");
        selenium.click(DialogDebugId.Dialog_Ok);

        selenium.waitFor(CrudDebugId.Crud_New);
    }

    public void Alt_TestLogin4Pyx() throws Exception {
        selenium.waitForLinkText("Sign In");

        selenium.click("link=Sign In");

        selenium.waitFor(proto(AuthenticationRequest.class).email());

        selenium.type(proto(AuthenticationRequest.class).email(), "emp001@pyx4j.com");
        selenium.type(proto(AuthenticationRequest.class).password(), "emp001@pyx4j.com");
        selenium.click(DialogDebugId.Dialog_Ok);

        selenium.waitFor(CrudDebugId.Crud_New);
    }
}
