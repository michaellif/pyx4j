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
 */
package com.pyx4j.selenium;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.meta.MemberMeta;

public class SeleniumTestBase extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(SeleniumTestBase.class);

    protected ISeleniumTestConfiguration testConfig;

    protected SeleniumExtended selenium;

    private static SeleniumExtended seleniumReuse;

    private boolean errorHappened;

    public SeleniumTestBase() {
        super();
    }

    public SeleniumTestBase(String name) {
        super(name);
    }

    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration();
    }

    public <T extends IEntity> T proto(Class<T> clazz) {
        return EntityFactory.getEntityPrototype(clazz);
    }

    protected void captureScreenshot(String sufix) {
        if (selenium == null) {
            return;
        }
        if (!(selenium.driver instanceof TakesScreenshot)) {
            return;
        }
        String filename = this.getClass().getName() + "." + getName() + sufix + ".png";
        try {
            File scrFile = ((TakesScreenshot) selenium.driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(testConfig.screenshotDir(), filename));
            log.info("Saved screenshot " + filename);
        } catch (Throwable e) {
            log.error("Couldn't save screenshot " + filename, e);
            System.err.println("Couldn't save screenshot " + filename + ": " + e.getMessage());
        }
    }

    protected void windowMaximize() {
        new JavascriptLibrary().executeScript(selenium.driver,
                "if (window.screen) { window.moveTo(0, 0); window.resizeTo(window.screen.availWidth, window.screen.availHeight);};");
    }

    @Override
    protected void setUp() throws Exception {
        log.info("====== Test " + this.getClass().getName() + "." + getName() + " ======");
        setUp(getSeleniumTestConfiguration());
    }

    @Override
    protected void tearDown() throws Exception {
        log.debug("test ends {} ", getName());
    }

    private void setUp(ISeleniumTestConfiguration testConfig) throws Exception {
        this.testConfig = testConfig;
        if (testConfig.reuseBrowser() && (seleniumReuse != null)) {
            selenium = seleniumReuse;
            log.info("Reuse selenium");
        } else {
            selenium = new SeleniumExtended(testConfig);
        }
        if (this.testConfig.windowMaximize()) {
            windowMaximize();
        }
        log.debug("start test execution {}", getName());
    }

    public void setErrorHappened(boolean errorHappened) {
        this.errorHappened = errorHappened;
    }

    public boolean isErrorHappened() {
        return errorHappened;
    }

    @Override
    public void runBare() throws Throwable {
        setErrorHappened(false);
        Throwable exception = null;
        boolean setUpOk = false;
        try {
            setUp();
            setUpOk = true;
        } finally {
            if (!setUpOk) {
                captureScreenshot("-setUp");
            }
        }
        try {
            runTest();
        } catch (Throwable running) {
            log.error("test " + getName() + " error", running);
            exception = running;
            setErrorHappened(true);
            captureScreenshot("");
        } finally {
            if (!(testConfig.keepBrowserOnError() && isErrorHappened())) {
                try {
                    tearDown();
                } catch (Throwable tearingDown) {
                    setErrorHappened(true);
                    if (exception == null) {
                        //capture once
                        captureScreenshot("-tearDown");
                        exception = tearingDown;
                    }
                }
            }
        }

        boolean keepBrowser = false;
        if ((testConfig.keepBrowserOnError() && isErrorHappened())) {
            keepBrowser = true;
        } else if (testConfig.reuseBrowser()) {
            keepBrowser = true;
        }

        if (selenium != null) {
            selenium.clientLogRollOver(SeleniumExtended.GWT_LOG_PREFIX + "test-end " + this.getClass().getName() + "." + getName());
        }

        if ((selenium != null) && (!keepBrowser)) {
            selenium.driver.quit();
        } else {
            log.warn("We are abandon selenium session. It should remain open");
        }

        if (testConfig.reuseBrowser() && (!isErrorHappened())) {
            log.warn("Kepp selenium instance for next test");
            seleniumReuse = selenium;
        } else {
            seleniumReuse = null;
        }
        selenium = null;

        if (exception != null) {
            throw exception;
        }
    }

    // ====================== asserts  ===========================

    static public void assertNotEquals(String message, String expected, String actual) {
        if (CommonsStringUtils.equals(expected, actual)) {
            fail(message + " has equal values [" + actual + "]");
        }
    }

    public void assertEditable(String locator) {
        assertTrue(locator + " should be editable", selenium.isEditable(locator));
    }

    public void assertNotEditable(String locator) {
        assertFalse(locator + " should not be editable", selenium.isEditable(locator));
    }

    public void assertEditable(IDebugId debugId) {
        assertTrue(debugId.debugId() + " should be editable", selenium.isEditable(debugId));
    }

    public void assertNotEditable(IDebugId debugId) {
        assertFalse(debugId.debugId() + " should not be editable", selenium.isEditable(debugId));
    }

    public void assertEnabled(String locator) {
        assertTrue(locator + " should be enabled", selenium.isEnabled(locator));
    }

    public void assertNotEnabled(String locator) {
        assertFalse(locator + " should not be enabled", selenium.isEnabled(locator));
    }

    public void assertEnabled(IDebugId debugId) {
        assertTrue(debugId.debugId() + " should be enabled", selenium.isEnabled(debugId));
    }

    public void assertNotEnabled(IDebugId debugId) {
        assertFalse(debugId.debugId() + " should not be enabled", selenium.isEnabled(debugId));
    }

    public void assertPresent(String locator) {
        assertTrue(locator + " should exists", selenium.isElementPresent(locator));
    }

    public void assertNotPresent(String locator) {
        assertFalse(locator + " should not be present", selenium.isElementPresent(locator));
    }

    public void assertPresent(IDebugId debugId) {
        assertTrue(debugId.debugId() + " should exists", selenium.isElementPresent(debugId));
    }

    public void assertNotPresent(IDebugId debugId) {
        assertFalse(debugId.debugId() + " should not be present", selenium.isElementPresent(debugId));
    }

    public void assertVisible(String locator) {
        assertTrue(locator + " should be visible", selenium.isVisible(locator));
    }

    public void assertNotVisible(String locator) {
        assertFalse(locator + " should not be visible", selenium.isVisible(locator));
    }

    public void assertVisible(IDebugId debugId) {
        assertTrue(debugId.debugId() + " should be visible", selenium.isVisible(debugId));
    }

    public void assertNotVisible(IDebugId debugId) {
        assertFalse(debugId.debugId() + " should not be visible", selenium.isVisible(debugId));
    }

    /**
     * Helper function to avoid casts
     */
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T detach(T entity) {
        return (T) entity.detach();
    }

    public void assertValueOnForm(IEntity member) {
        assertValueOnForm(null, member);
    }

    public void assertValueOnForm(IDebugId formDebugId, IEntity member) {
        assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(DebugIdBuilder.id(formDebugId, member)));
    }

    public void assertValueOnForm(IPrimitive<?> member) {
        assertValueOnForm(null, member);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void assertValueOnForm(IDebugId formDebugId, IPrimitive<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().isEnum()) {
            assertEquals(member.getMeta().getCaption(), member.getValue(), selenium.getEnumValue(formDebugId, (IPrimitive<Enum>) member));
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))
                || (mm.getValueClass().equals(LogicalDate.class))) {
            // CDatePicker, CMonthYearPicker
            assertEquals(member.getMeta().getCaption(), member.getValue(), selenium.getDateValue(formDebugId, (IPrimitive<Date>) member));
        } else if (mm.getValueClass().equals(Boolean.class)) {
            assertEquals(member.getMeta().getCaption(), member.getValue(), selenium.getBooleanValue(formDebugId, (IPrimitive<Boolean>) member));
        } else if (mm.getValueClass().equals(Integer.class)) {
            // CIntegerField();
            assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(formDebugId, member));
        } else if (mm.getValueClass().equals(Long.class)) {
            // CLongField();
            assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(formDebugId, member));
        } else if (mm.getValueClass().equals(Double.class)) {
            // CDoubleField();
            assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(formDebugId, member));
        } else if (mm.getValueClass().equals(String.class)) {
            // CTextField();
            assertEquals(member.getMeta().getCaption(), member.getStringView(), selenium.getValue(formDebugId, member));
        } else {
            throw new Error("No comparison defined for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
        }
    }

    public void setValueOnForm(IEntity member) {
        setValueOnForm(null, member);
    }

    public void setValueOnForm(IDebugId formDebugId, IEntity member) {
        selenium.setValue(DebugIdBuilder.id(formDebugId, member), member.getStringView());
    }

    public void setValueOnForm(IPrimitive<?> member) {
        setValueOnForm(null, member);
    }

    public void setValueOnForm(IDebugId formDebugId, IPrimitive<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().isEnum()) {
            selenium.setEnumValue(DebugIdBuilder.id(formDebugId, member), (Enum<?>) member.getValue());
        } else if (mm.getValueClass().equals(Boolean.class)) {
            selenium.setValue(DebugIdBuilder.id(formDebugId, member), (Boolean) member.getValue());
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))
                || (mm.getValueClass().equals(LogicalDate.class))) {
            selenium.setDateValue(DebugIdBuilder.id(formDebugId, member), (Date) member.getValue(), mm.getFormat());
        } else {
            selenium.setValue(DebugIdBuilder.id(formDebugId, member), member.getStringView());
        }
    }
}
