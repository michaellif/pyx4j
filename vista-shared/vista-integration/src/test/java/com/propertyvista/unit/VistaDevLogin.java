/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.selenium.SeleniumExtended;

public class VistaDevLogin {

    private static final Logger log = LoggerFactory.getLogger(VistaDevLogin.class);

    public static void login(SeleniumExtended selenium) {
        if (!selenium.isElementPresent("id=googleSignIn")) {
            log.debug("login not required");
            return;
        }

        Credentials credentials = J2SEServiceConnector.getCredentials(System.getProperty("user.dir", ".") + "/credentials.properties");

        selenium.click("id=googleSignIn");

        selenium.type("id=Email", credentials.email);
        selenium.type("id=Passwd", credentials.password);
        selenium.click("id=signIn");

        selenium.click("id=continue");

    }
}
