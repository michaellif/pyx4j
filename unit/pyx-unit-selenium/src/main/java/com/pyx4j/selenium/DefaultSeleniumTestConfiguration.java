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

import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

public class DefaultSeleniumTestConfiguration implements ISeleniumTestConfiguration {

    protected PropertiesConfiguration propertiesConfiguration;

    public DefaultSeleniumTestConfiguration() {
        this(new PropertiesConfiguration(System.getProperties()));
    }

    public DefaultSeleniumTestConfiguration(PropertiesConfiguration propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    @Override
    public String getTestUrl() {
        return propertiesConfiguration.getValue("selenium.url", "http://localhost:8888/");
    }

    @Override
    public Driver getDriver() {
        return propertiesConfiguration.getEnumValue("selenium.driver", Driver.class, Driver.Firefox);
    }

    @Override
    public String getRemoteDriverHost() {
        return propertiesConfiguration.getValue("selenium.remoteDriverHost");
    }

    @Override
    public ProxyConfig getProxyConfig() {
        return null;
    }

    @Override
    public boolean reuseBrowser() {
        return propertiesConfiguration.getBooleanValue("selenium.reuseBrowser", false);
    }

    @Override
    public boolean keepBrowserOnError() {
        return propertiesConfiguration.getBooleanValue("selenium.keepBrowserOnError", ServerSideConfiguration.isStartedUnderEclipse());
    }

    @Override
    public boolean windowMaximize() {
        return propertiesConfiguration.getBooleanValue("selenium.windowMaximize", !ServerSideConfiguration.isStartedUnderEclipse());
    }

    @Override
    public int waitSeconds() {
        return propertiesConfiguration.getSecondsValue("selenium.waitSeconds", 60);
    }

    @Override
    public int implicitlyWaitSeconds() {
        return propertiesConfiguration.getSecondsValue("selenium.implicitlyWaitSeconds", 2);
    }

    @Override
    public String screenshotDir() {
        return System.getProperty("user.dir") + "/" + propertiesConfiguration.getValue("selenium.screenshots", "target/screenshots");
    }

    @Override
    public String toString() {
        return buildConfigurationText();
    }

    public String buildConfigurationText() {
        StringBuilder b = new StringBuilder();
        b.append("selenium.url                   : ").append(this.getTestUrl()).append("\n");
        b.append("selenium.driver                : ").append(this.getDriver()).append("\n");

        b.append(" -D webdriver.firefox.bin   =  : ").append(System.getProperty("webdriver.firefox.bin")).append("\n");
        b.append(" -D webdriver.chrome.driver =  : ").append(System.getProperty("webdriver.chrome.driver")).append("\n");
        b.append(" -D webdriver.ie.driver     =  : ").append(System.getProperty("webdriver.ie.driver")).append("\n");

        b.append("selenium.remoteDriverHost      : ").append(this.getRemoteDriverHost()).append("\n");
        b.append("selenium.reuseBrowser          : ").append(this.reuseBrowser()).append("\n");
        b.append("selenium.keepBrowserOnError    : ").append(this.keepBrowserOnError()).append("\n");
        b.append("selenium.windowMaximize        : ").append(this.windowMaximize()).append("\n");
        b.append("selenium.waitSeconds           : ").append(this.waitSeconds()).append("\n");
        b.append("selenium.implicitlyWaitSeconds : ").append(this.implicitlyWaitSeconds()).append("\n");
        return b.toString();
    }

}
