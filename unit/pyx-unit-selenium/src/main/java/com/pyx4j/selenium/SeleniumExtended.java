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
package com.pyx4j.selenium;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IObject;

/**
 * Compatibility layer with Selenium v1 i.e. with Selenium IDE generated scripts.
 */
public class SeleniumExtended extends WebDriverWrapper {

    public static String GWT_DEBUG_ID_PREFIX = "gwt-debug-";

    public SeleniumExtended(ISeleniumTestConfiguration testConfig) {
        super(testConfig);
        driver.manage().timeouts().implicitlyWait(testConfig.implicitlyWaitSeconds(), TimeUnit.SECONDS);
    }

    public static String gwtLocator(String baseID) {
        if (baseID.startsWith(GWT_DEBUG_ID_PREFIX)) {
            return baseID;
        } else {
            return GWT_DEBUG_ID_PREFIX + baseID;
        }
    }

    public static String gwtLocator(String baseID, String id) {
        if (baseID.startsWith(GWT_DEBUG_ID_PREFIX)) {
            return baseID;
        } else if (baseID.endsWith("-" + id)) {
            return GWT_DEBUG_ID_PREFIX + baseID;
        } else {
            return GWT_DEBUG_ID_PREFIX + baseID + "-" + id;
        }
    }

    public By elementLocator(String paramString) {
        String[] prams = paramString.split("=");
        if (prams.length > 1) {
            String text = paramString.substring(paramString.indexOf('=') + 1);
            if (prams[0].equals("link")) {
                return By.linkText(text);
            } else if (prams[0].equals("id")) {
                return By.id(text);
            } else if (prams[0].equals("name")) {
                return By.name(text);
            } else if (prams[0].equals("css")) {
                return By.cssSelector(text);
            } else if (prams[0].equals("xpath")) {
                return By.xpath(text);
            } else {
                throw new Error("Unsupported Element Locator '" + prams[0] + "'");
            }
        } else {
            return By.id(gwtLocator(paramString));
        }
    }

    public By by(IDebugId debugId) {
        return By.id(gwtLocator(debugId.getDebugIdString()));
    }

    public By by(IObject<?> member) {
        return By.id(gwtLocator(member.getPath().getDebugIdString()));
    }

    public void waitForLinkText(String text) {
        waitFor(By.linkText(text));
    }

    public void waitFor(String id) {
        waitFor(By.id(gwtLocator(id)));
    }

    public void waitFor(IDebugId debugId) {
        waitFor(by(debugId));
    }

    public void waitFor(IObject<?> member) {
        waitFor(by(member));
    }

    public void waitFor(By paramBy) {
        waitFor(paramBy, testConfig.waitSeconds());
    }

    public WebElement waitFor(By paramBy, int waitSeconds) {
        long start = System.currentTimeMillis();
        for (int second = 0;; second++) {
            if ((System.currentTimeMillis() - start) >= waitSeconds * Consts.SEC2MILLISECONDS) {
                Assert.fail("Wait  " + paramBy + "; Timeout " + waitSeconds + " sec ...");
            }
            try {
                WebElement el = driver.findElement(paramBy);
                if (el != null) {
                    return el;
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

    public void click(String paramString) {
        driver.findElement(elementLocator(paramString)).click();
    }

    public void click(IDebugId debugId) {
        driver.findElement(by(debugId)).click();
    }

    public void click(IObject<?> member) {
        driver.findElement(by(member)).click();
    }

    public void type(String paramString, CharSequence... keysToSend) {
        driver.findElement(elementLocator(paramString)).sendKeys(keysToSend);
    }

    public void type(IDebugId debugId, CharSequence... keysToSend) {
        driver.findElement(by(debugId)).sendKeys(keysToSend);
    }

    public void type(IObject<?> member, CharSequence... keysToSend) {
        driver.findElement(by(member)).sendKeys(keysToSend);
    }

    public String getText(String paramString) {
        return driver.findElement(elementLocator(paramString)).getText();
    }

    public String getText(IObject<?> member) {
        return driver.findElement(by(member)).getText();
    }

    public String getValue(String paramString) {
        return driver.findElement(elementLocator(paramString)).getValue();
    }

}
