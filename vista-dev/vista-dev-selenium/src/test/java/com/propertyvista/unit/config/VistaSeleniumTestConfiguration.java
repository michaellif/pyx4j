/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.unit.config;

import com.pyx4j.essentials.j2se.HostConfig;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;

import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaDeploymentId;
import com.propertyvista.domain.security.common.VistaApplication;

/**
 * This is local Global configurator for all tests.
 *
 * Do not commit the modifications to this to SVN.
 */
public class VistaSeleniumTestConfiguration extends DefaultSeleniumTestConfiguration {

    protected final ApplicationId appID;

    protected final VistaDeploymentId deploymentId;

    protected final boolean gwtServerTrace;

    public VistaSeleniumTestConfiguration() {
        this(ApplicationId.crm);
    }

    public VistaSeleniumTestConfiguration(ApplicationId appID) {
        // Comment/uncomment lines here during development.  Never commit this file to SVN
        //this(appID, VistaDeploymentId.www22);
        //this(appID, VistaDeploymentId.www33);
        this(appID, VistaDeploymentId.local);
    }

    private VistaSeleniumTestConfiguration(ApplicationId appID, VistaDeploymentId deploymentId) {
        this.appID = appID;
        if (System.getProperty("bamboo.buildNumber") == null) {
            this.deploymentId = deploymentId;
        } else {
            this.deploymentId = VistaDeploymentId.env11;
        }
        gwtServerTrace = (deploymentId == VistaDeploymentId.local);
    }

    @Override
    public String getTestUrl() {
        StringBuilder url = new StringBuilder();

        String protocol;
        String pmcDnsName = "vista";

        switch (deploymentId) {
        case local:
        case local_9000:
            protocol = "http://";
            break;
        default:
            protocol = "https://";
        }

        url.append(protocol);
        url.append(pmcDnsName);
        url.append("-");

        switch (appID) {
        case site:
            url.append(VistaApplication.site.name());
            break;
        case prospect:
        case resident:
            url.append("portal");
            break;
        case crm:
            url.append(VistaApplication.crm.name());
            break;
        }

        switch (deploymentId) {
        case local:
            if (getRemoteDriverHost() == null) {
                url.append(".local.devpv.com");
            } else {
                url.append(HostConfig.getLocalHostIP());
            }
            url.append(":8888");
            break;
        case local_9000:
            if (getRemoteDriverHost() == null) {
                url.append("localhost");
            } else {
                url.append(HostConfig.getLocalHostIP());
            }
            url.append(":9000");
            break;
        case env11:
            url.append("-11.devpv.com");
            break;
        case env22:
            url.append("-22.devpv.com");
            break;
        case env66:
            url.append("-66.devpv.com");
            break;
        case env88:
            url.append("-88.devpv.com");
            break;
        }

        url.append("/");

        if (appID == ApplicationId.prospect) {
            url.append(ApplicationId.prospect.name());
        }

        if (gwtServerTrace) {
            url.append("?trace=true");
        }

        return url.toString();
    }

    @Override
    public boolean reuseBrowser() {
        return true;
    }

    @Override
    public ProxyConfig getProxyConfig() {
        return SystemConfig.instance().getProxyConfig();
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
