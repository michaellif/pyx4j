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

import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaDeploymentId;

import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.selenium.DefaultSeleniumTestConfiguration;

/**
 * This is local Global configurator for all tests.
 * 
 * Do not commit the modifications to this to SVN.
 */
public class VistaSeleniumTestConfiguration extends DefaultSeleniumTestConfiguration {

    protected final ApplicationId appID;

    protected final VistaDeploymentId deploymentId;

    public VistaSeleniumTestConfiguration() {
        this(ApplicationId.portal);
    }

    public VistaSeleniumTestConfiguration(ApplicationId appID) {
        // Comment/uncomment lines here during development.  Never commit this file to SVN
        //this(appID, VistaDeploymentId.www22);
        //this(appID, VistaDeploymentId.www33);
        this(appID, VistaDeploymentId.local);
    }

    private VistaSeleniumTestConfiguration(ApplicationId appID, VistaDeploymentId deploymentId) {
        this.appID = appID;
        this.deploymentId = deploymentId;
    }

    @Override
    public String getTestUrl() {
        StringBuilder url = new StringBuilder();
        url.append("http://");

        switch (deploymentId) {
        case local:
            url.append("localhost:8888/vista/");
            break;
        case local_9000:
            url.append("localhost:9000/vista/");
            break;
        case www22:
            url.append("www22.birchwoodsoftwaregroup.com/");
            break;
        case www33:
            url.append("www33.birchwoodsoftwaregroup.com/");
            break;
        }

        switch (appID) {
        case portal:
            break;
        case tester:
            url.append("tester/");
            break;
        case crm:
            url.append("crm/");
            break;
        }

        return url.toString();
    }

    @Override
    public boolean reuseBrowser() {
        return false;
    }

    @Override
    public ProxyConfig getProxyConfig() {
        return SystemConfig.instance().getProxyConfig();
    }

}
