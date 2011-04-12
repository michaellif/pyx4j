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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.impl.Log4jLoggerAdapter;
import org.slf4j.spi.LocationAwareLogger;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IObject;

/**
 * Compatibility layer with Selenium v1 i.e. with Selenium IDE generated scripts.
 */
public class SeleniumExtended extends WebDriverWrapper {

    private static final Logger log = LoggerFactory.getLogger(SeleniumExtended.class);

    final static String FQCN = SeleniumExtended.class.getName();

    public static String GWT_DEBUG_ID_PREFIX = "gwt-debug-";

    public static String GWT_LOG_PREFIX = "Selenium: ";

    private RenderedWebElement glassPanel;

    private boolean propagateLogToClient;

    public SeleniumExtended(ISeleniumTestConfiguration testConfig) {
        super(testConfig);
        driver.manage().timeouts().implicitlyWait(testConfig.implicitlyWaitSeconds(), TimeUnit.SECONDS);
    }

    public void setPropagateLogToClient(boolean enable) {
        propagateLogToClient = enable;
    }

    private void log(String format, Object... args) {
        if (propagateLogToClient) {
            clientLog(GWT_LOG_PREFIX + format, args);
        }
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        ((Log4jLoggerAdapter) log).log(null, FQCN, LocationAwareLogger.DEBUG_INT, ft.getMessage(), null, null);
    }

    private static String escapeJS(String message) {
        return message.replace("\"", "\\\"");
    }

    public void clientLog(String format, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        ((JavascriptExecutor) driver).executeScript("window.pyxClientLog(\"" + escapeJS(ft.getMessage()) + "\")");
    }

    public void clientLogFlush() {
        ((JavascriptExecutor) driver).executeScript("window.pyxClientLogFlush()");
    }

    public void clientLogRollOver(String message) {
        ((JavascriptExecutor) driver).executeScript("window.pyxClientLogRollOver(\"" + escapeJS(message) + "\")");
    }

    public void setGlassPanelAware() {
        glassPanel = (RenderedWebElement) driver.findElement(elementLocator("GlassPanel"));
    }

    public void waitWhileWorking() {
        waitWhileWorking(testConfig.waitSeconds());
    }

    public void waitWhileWorking(int waitSeconds) {
        if (glassPanel == null) {
            return;
        }
        long start = System.currentTimeMillis();
        for (int second = 0;; second++) {
            if ((System.currentTimeMillis() - start) >= waitSeconds * Consts.SEC2MILLISECONDS) {
                Assert.fail("Wait for GlassPanel; Timeout " + waitSeconds + " sec ...");
            }
            try {
                if (!glassPanel.isDisplayed()) {
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
        return By.id(gwtLocator(debugId.debugId()));
    }

    public By by(IDebugId parent, IDebugId child) {
        return By.id(gwtLocator(CompositeDebugId.debugId(parent, child)));
    }

    public By by(String parent, IDebugId child) {
        return By.id(gwtLocator(CompositeDebugId.debugId(parent, child)));
    }

    public By by(IDebugId parent, String child) {
        return By.id(gwtLocator(CompositeDebugId.debugId(parent, child)));
    }

    public By by(IObject<?> member) {
        return By.id(gwtLocator(member.getPath().debugId()));
    }

    public boolean isElementPresent(String paramString) {
        return isElementPresent(elementLocator(paramString));
    }

    public boolean isElementPresent(IDebugId debugId) {
        return isElementPresent(by(debugId));
    }

    public boolean isElementPresent(By paramBy) {
        try {
            driver.findElement(paramBy);
            return true;
        } catch (NoSuchElementException notFound) {
            return false;
        }
    }

    public boolean isVisible(String paramString) {
        return isVisible(elementLocator(paramString));
    }

    public boolean isVisible(IDebugId debugId) {
        return isVisible(by(debugId));
    }

    public boolean isVisible(By paramBy) {
        try {
            return ((RenderedWebElement) driver.findElement(paramBy)).isDisplayed();
        } catch (NoSuchElementException notFound) {
            return false;
        }
    }

    public void waitForLinkText(String text) {
        waitFor(By.linkText(text));
    }

    public WebElement waitFor(String paramString) {
        return waitFor(elementLocator(paramString));
    }

    public WebElement waitFor(IDebugId debugId) {
        return waitFor(by(debugId));
    }

    public WebElement waitFor(IDebugId debugId, int waitSeconds) {
        return waitFor(by(debugId), waitSeconds);
    }

    public WebElement waitFor(IObject<?> member) {
        return waitFor(by(member));
    }

    public WebElement waitFor(By paramBy) {
        return waitFor(paramBy, testConfig.waitSeconds());
    }

    /**
     * This method never returns null.
     */
    public WebElement waitFor(By paramBy, int waitSeconds) {
        long start = System.currentTimeMillis();
        boolean logOnce = true;
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
            if (logOnce) {
                log.debug("waitFor {} ", paramBy);
                logOnce = false;
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }

    public static boolean regExprEquals(String expected, String value) {
        if (CommonsStringUtils.equals(expected, value)) {
            return true;
        } else if (expected.startsWith("@") && value != null) {
            return value.matches(expected.substring(1));
        } else {
            return expected.equals(value);
        }
    }

    public void waitForText(String paramString, String expected, int waitSeconds) {
        waitForText(elementLocator(paramString), expected, waitSeconds);
    }

    public void waitForText(IDebugId debugId, String expected, int waitSeconds) {
        waitForText(by(debugId), expected, waitSeconds);
    }

    public void waitForText(By paramBy, String expected, int waitSeconds) {
        long start = System.currentTimeMillis();
        for (int second = 0;; second++) {
            if ((System.currentTimeMillis() - start) >= waitSeconds * Consts.SEC2MILLISECONDS) {
                Assert.fail("Wait  " + paramBy + "; Timeout " + waitSeconds + " sec ...");
            }
            try {
                WebElement el = driver.findElement(paramBy);
                if (el != null) {
                    if (regExprEquals(expected, el.getText())) {
                        return;
                    }
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

    public void click(By by) {
        WebElement element = driver.findElement(by);
        log("click on element <{}> id={} ", element.getTagName(), element.getAttribute("id"));
        element.click();
        this.waitWhileWorking();
    }

    public void click(String paramString) {
        click(elementLocator(paramString));
    }

    public void click(IDebugId debugId) {
        click(by(debugId));
    }

    public void click(IDebugId parent, IDebugId child) {
        click(by(parent, child));
    }

    public void click(IObject<?> member) {
        click(by(member));
    }

    public void type(By by, CharSequence... keysToSend) {
        WebElement element = driver.findElement(by);
        log("type in element <{}> id={} text={}", element.getTagName(), element.getAttribute("id"), keysToSend);
        element.clear();
        element.sendKeys(keysToSend);
    }

    public void type(String paramString, CharSequence... keysToSend) {
        type(elementLocator(paramString), keysToSend);
    }

    public void type(IDebugId debugId, CharSequence... keysToSend) {
        type(by(debugId), keysToSend);
    }

    public void type(IObject<?> member, CharSequence... keysToSend) {
        type(by(member), keysToSend);
    }

    public void setValue(WebElement element, String textValue) {
        log("setValue of element <{}> id={} text={}", element.getTagName(), element.getAttribute("id"), textValue);
        InputHelper.setValue(element, textValue);
    }

    public void setValue(IDebugId debugId, String textValue) {
        setValue(driver.findElement(by(debugId)), textValue);
    }

    public void setValue(String paramString, String textValue) {
        setValue(driver.findElement(elementLocator(paramString)), textValue);
    }

    public void setValue(IObject<?> member, String textValue) {
        setValue(driver.findElement(by(member)), textValue);
    }

    public String getText(String paramString) {
        return driver.findElement(elementLocator(paramString)).getText();
    }

    public String getText(IDebugId debugId) {
        return driver.findElement(by(debugId)).getText();
    }

    public String getText(IDebugId parent, IDebugId child) {
        return driver.findElement(by(parent, child)).getText();
    }

    public String getText(IObject<?> member) {
        return driver.findElement(by(member)).getText();
    }

    public String getValue(String paramString) {
        return driver.findElement(elementLocator(paramString)).getValue();
    }

    public void select(String id) {
        WebElement we = driver.findElement(By.id(id));
        if (!we.isSelected() && we.isEnabled()) {
            we.setSelected();
        }
    }

    public void fireEvent(String locator, String eventName) {
        fireEvent(driver.findElement(elementLocator(locator)), eventName);
    }

    public void fireEvent(WebElement we, String eventName) {
        new JavascriptLibrary().callEmbeddedSelenium(driver, "doFireEvent", we, eventName);
    }

    /**
     * @deprecated Use setValue(, boolean)
     */
    @Deprecated
    public void check(String locator, boolean check) {
        setValue(locator, check);
    }

    //CheckBox special case
    public void setValue(WebElement element, boolean selectionValue) {
        log("setValue of element <{}> id={} value={}", element.getTagName(), element.getAttribute("id"), selectionValue);
        InputHelper.setValue(driver, element, selectionValue);
    }

    //CheckBox special case
    public void setValue(IDebugId debugId, boolean selectionValue) {
        setValue(driver.findElement(by(debugId)), selectionValue);
    }

    //CheckBox special case
    public void setValue(String paramString, boolean selectionValue) {
        setValue(driver.findElement(elementLocator(paramString)), selectionValue);
    }

    //CheckBox special case
    public void setValue(IObject<?> member, boolean selectionValue) {
        setValue(driver.findElement(by(member)), selectionValue);
    }

    public boolean isEnabled(String locator) {
        return driver.findElement(elementLocator(locator)).isEnabled();
    }

    public boolean isEnabled(IDebugId debugId) {
        return driver.findElement(by(debugId)).isEnabled();
    }

    public static boolean isEditable(WebElement element) {
        return InputHelper.isEditable(element);
    }

    public boolean isEditable(String locator) {
        return InputHelper.isEditable(driver.findElement(elementLocator(locator)));
    }

    public boolean isEditable(IDebugId debugId) {
        return InputHelper.isEditable(driver.findElement(by(debugId)));
    }
}
