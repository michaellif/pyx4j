/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-02
 * @author vlads
 * @version $Id$
 */
package org.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

public class GaeAppLoginTest extends BaseSeleniumTestCase {

    final public String testsite = "http://www22.birchwoodsoftwaregroup.com/vista/tester";

    final public String mainsite = "http://www22.birchwoodsoftwaregroup.com/";

    final public String blankpage = "about:blank";

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return mainsite;
            }
        };
    }

    public void testLoginToMainSite() throws Exception {
        //Credentials credentials = J2SEServiceConnector.getCredentials(System.getProperty("user.dir", ".") + "/credentials.properties");
        Credentials credentials = J2SEServiceConnector.getCredentials(System.getProperty("user.dir", ".") + "/credentials.properties");

        /*** page 1 ***/
        selenium.click("id=googleSignIn");

        /*** page 2 ***/
        selenium.type("id=Email", credentials.email);
        selenium.type("id=Passwd", credentials.password);
        selenium.click("id=signIn");

        /*** page 3 ***/
        selenium.click("id=continue");

        /*** page 4 ***/
        selenium.waitFor(By.id("gwt-debug-Login"), 8);
        selenium.click("id=gwt-debug-Login");

        /*** page 5 ***/
        selenium.type("id=gwt-debug-AuthenticationRequest$email", "cust001@pyx4j.com");
        selenium.type("id=gwt-debug-AuthenticationRequest$password", "cust001@pyx4j.com");
        selenium.click("id=gwt-debug-Criteria_Submit");

    }

    public void testLogin() throws Exception {

        Credentials credentials = J2SEServiceConnector.getCredentials(System.getProperty("user.dir", ".") + "/credentials.properties");
        selenium.get(testsite);

        /*** page 1 ***/
        selenium.click("id=googleSignIn");

        /*** page 2 ***/
        selenium.type("id=Email", credentials.email);
        selenium.type("id=Passwd", credentials.password);
        selenium.click("id=signIn");

        /*** page 3 ***/
        selenium.click("id=continue");

        /*** page 4 ***/
        //it throw us on main site, but that's OK, we'll use get() :)
        selenium.waitFor(By.id("gwt-debug-Login"), 8);

        /*** page 5 ***/
        selenium.get(testsite);
        //unique ID does not exist for this one. 
        //This is just a test, I'll fix/remove it if it'll cause problems
        WebElement element1 = selenium.findElement(By.xpath("html/body/div[3]/div[3]/div/div[1]/table/tbody/tr/td/div/div/div[2]"));
        String atext = element1.getText();
        String strToFind = "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        if (atext.indexOf(strToFind) < 1) {
            System.out.println("Error: Cannot find text ");
            System.out.println("     : " + strToFind);
        }

    }

}
