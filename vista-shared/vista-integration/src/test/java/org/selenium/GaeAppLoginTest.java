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

import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.selenium.BaseSeleniumTestCase;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;
import com.pyx4j.selenium.ISeleniumTestConfiguration;

public class GaeAppLoginTest extends BaseSeleniumTestCase {

    @Override
    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration() {
            @Override
            public String getTestUrl() {
                return "http://www22.birchwoodsoftwaregroup.com/tester/";
            }
        };
    }

    public void testLogin() throws Exception {
        Credentials credentials = J2SEServiceConnector.getCredentials("credentials.properties");

        // TODO Use this  credentials  to login to Google Apps

        selenium.waitFor("testMe");
    }

}
