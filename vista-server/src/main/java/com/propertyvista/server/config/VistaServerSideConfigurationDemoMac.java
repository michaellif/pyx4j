/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaServerSideConfigurationDemoMac extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        return this;
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL() {
            @Override
            public String dbName() {
                return "vista44";
            }

            @Override
            public String userName() {
                return "vista44";
            }

            @Override
            public String password() {
                return "vista44";
            }
        };
    }

    @Override
    public boolean openDBReset() {
        return true;
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public boolean openIdRequired() {
        return false;
    }

    @Override
    protected String getApplicationDeploymentProtocol() {
        return "http";
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        return "http://" + pmcDnsName + ".propertyvista.ca/";
    }

    @Override
    public String getDefaultBaseURLresidentPortalSite(String pmcDnsName, boolean secure) {
        String protocol;
        if (secure) {
            protocol = "http://";
        } else {
            protocol = "http://";
        }
        return protocol + pmcDnsName + ".residentportalsite.ca/";
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        return "http://" + pmcDnsName + ".prospectportalsite.ca/";
    }

    @Override
    public String getDefaultBaseURLvistaOperations() {
        return "https://localhost:8080/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.OPERATIONS_URL;
    }

}
