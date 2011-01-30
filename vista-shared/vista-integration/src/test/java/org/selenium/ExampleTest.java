/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 29, 2011
 * @author vlads
 * @version $Id$
 */
package org.selenium;

import junit.framework.TestCase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

public class ExampleTest extends TestCase {

    public void testLogin() throws Exception {
        // The Firefox driver supports javascript
        WebDriver driver = new FirefoxDriver();

        // Emulating Selenium RC
        String baseUrl = "http://pyx4j-demo.appspot.com/";
        // Go to the home page
        Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
        try {
            selenium.open(baseUrl);

            SeleniumUtils.waitForLinkText(driver, "Sign In");

            selenium.click("link=Sign In");

            SeleniumUtils.waitForId(driver, "gwt-debug-AuthenticationRequest$email");

            selenium.type("gwt-debug-AuthenticationRequest$email", "emp001@pyx4j.com");
            selenium.type("gwt-debug-AuthenticationRequest$password", "emp001@pyx4j.com");
            selenium.click("gwt-debug-Dialog_Ok");

            SeleniumUtils.waitForId(driver, "gwt-debug-Crud_New");

        } finally {
            selenium.stop();
        }

    }
}
