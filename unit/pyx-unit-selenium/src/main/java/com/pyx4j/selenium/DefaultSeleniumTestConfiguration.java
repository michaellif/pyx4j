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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

public class DefaultSeleniumTestConfiguration implements ISeleniumTestConfiguration {

    public static String getProperty(String key, String notFoundValue) {
        String sysProperty = System.getProperty(key);
        if (CommonsStringUtils.isStringSet(sysProperty)) {
            return sysProperty;
        } else {
            return notFoundValue;
        }
    }

    public static boolean getProperty(String key, boolean notFoundValue) {
        String sysProperty = System.getProperty(key);
        if (CommonsStringUtils.isStringSet(sysProperty)) {
            return Boolean.valueOf(sysProperty);
        } else {
            return notFoundValue;
        }
    }

    public static int getProperty(String key, int notFoundValue) {
        String sysProperty = System.getProperty(key);
        if (CommonsStringUtils.isStringSet(sysProperty)) {
            return Integer.valueOf(sysProperty);
        } else {
            return notFoundValue;
        }
    }

    public DefaultSeleniumTestConfiguration() {

    }

    @Override
    public String getTestUrl() {
        return getProperty("selenium.url", "http://localhost:8888/");
    }

    @Override
    public Driver getDriver() {
        String sysProperty = System.getProperty("selenium.driver");
        if (CommonsStringUtils.isStringSet(sysProperty)) {
            return Driver.valueOf(sysProperty);
        } else {
            return Driver.Friefox;
        }
    }

    @Override
    public ProxyConfig getProxyConfig() {
        return null;
    }

    @Override
    public boolean reuseBrowser() {
        return getProperty("selenium.reuseBrowser", false);
    }

    @Override
    public boolean keepBrowserOnError() {
        return getProperty("selenium.keepBrowserOnError", false);
    }

    @Override
    public int waitSeconds() {
        return getProperty("selenium.waitSeconds", 60);
    }

    @Override
    public int implicitlyWaitSeconds() {
        return getProperty("selenium.implicitlyWaitSeconds", 2);
    }

    @Override
    public String screenshotDir() {
        return System.getProperty("user.dir") + "/" + getProperty("selenium.screenshots", "target/screenshots");
    }

}
