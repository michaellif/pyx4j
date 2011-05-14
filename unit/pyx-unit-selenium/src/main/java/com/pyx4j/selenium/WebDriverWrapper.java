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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

public class WebDriverWrapper implements WebDriver {

    private static final Logger log = LoggerFactory.getLogger(WebDriverWrapper.class);

    protected ISeleniumTestConfiguration testConfig;

    protected WebDriver driver;

    public WebDriverWrapper(ISeleniumTestConfiguration testConfig) {
        this.testConfig = testConfig;
        if (this.testConfig.getRemoteDriverHost() == null) {
            switch (this.testConfig.getDriver()) {
            case Chrome:
                driver = new ChromeDriver();
                break;
            case Friefox:
                FirefoxProfile profile = null;
                ProxyConfig proxyConfig = testConfig.getProxyConfig();
                if (proxyConfig != null) {
                    profile = new FirefoxProfile();
                    Proxy proxy = new Proxy();
                    proxy.setProxyType(Proxy.ProxyType.MANUAL);
                    proxy.setHttpProxy(proxyConfig.getHost() + ":" + proxyConfig.getPort());
                    proxy.setNoProxy("localhost");
                    profile.setProxyPreferences(proxy);
                }
                driver = new FirefoxDriver(profile);
                break;
            case IE:
                driver = new InternetExplorerDriver();
                break;
            default:
                throw new IllegalArgumentException();
            }
        } else {
            DesiredCapabilities capabilities;
            switch (this.testConfig.getDriver()) {
            case Chrome:
                capabilities = DesiredCapabilities.chrome();
                break;
            case Friefox:
                capabilities = DesiredCapabilities.firefox();
                break;
            case IE:
                capabilities = DesiredCapabilities.internetExplorer();
                break;
            default:
                throw new IllegalArgumentException();
            }
            capabilities.setJavascriptEnabled(true);
            URL remoteAddress = null;
            try {
                remoteAddress = new URL("http://" + this.testConfig.getRemoteDriverHost() + ":4444/wd/hub");
            } catch (MalformedURLException ignore) {
            }
            driver = new RemoteWebDriver(remoteAddress, capabilities);

        }
        log.debug("WebDriver {} created", this.testConfig.getDriver());
        String testUrl = this.testConfig.getTestUrl();
        driver.get(testUrl);
        log.debug("load {} is complete", testUrl);
    }

    @Override
    public void get(String paramString) {
        driver.get(paramString);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By paramBy) {
        return driver.findElements(paramBy);
    }

    @Override
    public WebElement findElement(By paramBy) {
        return driver.findElement(paramBy);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }
}
