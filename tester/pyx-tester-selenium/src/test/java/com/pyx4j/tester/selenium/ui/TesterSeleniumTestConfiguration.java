/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.selenium.ui;

import com.pyx4j.essentials.j2se.HostConfig;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;

public class TesterSeleniumTestConfiguration extends DefaultSeleniumTestConfiguration {

    public static enum TesterDeploymentId {

        local,

        local_9000, // Deployed in Tomcat

        local_mac, // Igor, to run on Mac

        dev

    }

    protected final TesterDeploymentId deploymentId;

    protected final boolean gwtServerTrace;

    public TesterSeleniumTestConfiguration() {
        // Comment/uncomment lines here during development.  Never commit this file to SVN
        //this(TesterDeploymentId.dev);
        this(TesterDeploymentId.local);
    }

    private TesterSeleniumTestConfiguration(TesterDeploymentId deploymentId) {
        if (System.getProperty("bamboo.buildNumber") == null) {
            this.deploymentId = deploymentId;
        } else {
            this.deploymentId = TesterDeploymentId.dev;
        }
        gwtServerTrace = (deploymentId == TesterDeploymentId.local);
    }

    @Override
    public String getTestUrl() {
        StringBuilder url = new StringBuilder();
        url.append("http://");

        switch (deploymentId) {
        case local:
            if (getRemoteDriverHost() == null) {
                url.append("localhost");
            } else {
                url.append(HostConfig.getLocalHostIP());
            }
            url.append(":8881/tester/");
            break;
        case local_mac:
            if (getRemoteDriverHost() == null) {
                url.append("127.0.0.1");
            } else {
                url.append(HostConfig.getLocalHostIP());
            }
            url.append(":8881/tester/");
            break;
        case local_9000:
            if (getRemoteDriverHost() == null) {
                url.append("localhost");
            } else {
                url.append(HostConfig.getLocalHostIP());
            }
            url.append(":9000/tester/");
            break;
        case dev:
            url.append("tester-ui.pyx4j.com/tester");
            break;
        }

        if (gwtServerTrace) {
            url.append("?trace=true");
        }

        return url.toString();
    }

    @Override
    public boolean reuseBrowser() {
        return false;
    }

    @Override
    public Driver getDriver() {
        return super.getDriver();

        // Lazy man switch, See next version
        //return Driver.Chrome;
        //return Driver.IE;
    }

    @Override
    public String getRemoteDriverHost() {
        // Fail safe switch: if we committed private config
        if (System.getProperty("bamboo.buildNumber") != null) {
            return null;
        } else {
            return null;
            // e.g. Run Firefox Application in another VM
            // Just download http://code.google.com/p/selenium/downloads/detail?name=selenium-server-standalone-2.0.0.jar
            // And run java -jar selenium-server-standalone-2.0.0.jar

            // VladS Settings
            // XP-IE7, FF 3.6
            //return "10.1.1.155";

            // XP-IE8, FF 4
            //return "10.1.1.152";
        }
    }

}
