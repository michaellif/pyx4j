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

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.pyx4j.commons.Consts;

public class SeleniumUtils {

    static int waitSeconds = 40;

    static void waitForLinkText(WebDriver driver, String text) {
        waitFor(driver, By.linkText(text));
    }

    static void waitForId(WebDriver driver, String id) {
        waitFor(driver, By.id(id));
    }

    static void waitFor(WebDriver driver, By paramBy) {
        long start = System.currentTimeMillis();
        for (int second = 0;; second++) {
            if ((System.currentTimeMillis() - start) >= waitSeconds * Consts.SEC2MILLISECONDS) {
                Assert.fail("Wait  " + paramBy + " Timeout ...");
            }
            try {
                WebElement el = driver.findElement(paramBy);
                if (el != null) {
                    return;
                }
            } catch (Throwable ok) {
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }
}
