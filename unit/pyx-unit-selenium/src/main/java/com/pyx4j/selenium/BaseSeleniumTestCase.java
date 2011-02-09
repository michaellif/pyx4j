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

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

public class BaseSeleniumTestCase extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(BaseSeleniumTestCase.class);

    protected ISeleniumTestConfiguration testConfig;

    protected SeleniumExtended selenium;

    private static SeleniumExtended seleniumReuse;

    private boolean errorHappened;

    public BaseSeleniumTestCase() {
        super();
    }

    public BaseSeleniumTestCase(String name) {
        super(name);
    }

    protected ISeleniumTestConfiguration getSeleniumTestConfiguration() {
        return new DefaultSeleniumTestConfiguration();
    }

    public <T extends IEntity> T meta(Class<T> clazz) {
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
}
