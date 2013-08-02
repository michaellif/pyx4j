/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.server.contexts.Context;

public class VistaServerSideConfigurationDemo extends VistaServerSideConfiguration {

    protected boolean demoUsePostgreSQL = true;

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.  All defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=
        return this;
    }

    @Override
    public boolean openIdRequired() {
        return false;
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return null;
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        if (Context.getRequest() != null) {
            // Default tomcat port
            return Context.getRequest().getServerPort() != 8080;
        } else {
            return true;
        }
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        if (Context.getRequest() != null) {
            String serverName = Context.getRequest().getServerName();
            String[] serverNameParts = serverName.split("\\.");

            String dnsBase = serverName;

            if (serverNameParts.length > 3) {
                dnsBase = serverNameParts[serverNameParts.length - 3] + "." + serverNameParts[serverNameParts.length - 2] + "."
                        + serverNameParts[serverNameParts.length - 1];
            }

            StringBuilder b = new StringBuilder();
            b.append(".");
            b.append(dnsBase);

            b.append(":").append(Context.getRequest().getServerPort());

            b.append("/");
            b.append(LoggerConfig.getContextName()).append("/");
            return b.toString();
        } else {
            return ".dev.birchwoodsoftwaregroup.com";
        }
    }
}
