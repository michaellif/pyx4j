/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-01-25
 * @author vlads
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.config.BankingSimulatorConfiguration;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;

public class VistaServerSideConfigurationDev extends VistaServerSideConfiguration {

    public static int devServerPort = 8888 + DevelopmentBranchProfile.jettyServerPortOffset();

    public static String devContextPath = "/vista";

    public static boolean devContextLess = true;

    public static boolean devTestHttps = false;

    @Override
    public boolean walkMeEnabled(VistaApplication application) {
        return false;
    }

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.
        return this;
    }

    @Override
    public boolean isDepoymentHttps() {
        return devTestHttps;
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL() {

            @Override
            public String dbName() {
                if (VistaTODO.codeBaseIsProdBranch) {
                    return super.dbName() + "_prod";
                } else {
                    return super.dbName();
                }
            }

        };
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig() {
            @Override
            public long getInterval() {
                return 30 * Consts.SEC2MSEC;
            }

            @Override
            public long getMaxRequests() {
                return 600;
            }

            @Override
            public long getMaxTimeUsage() {
                return Consts.MIN2MSEC;
            }
        };
    }

    @Override
    public boolean openIdRequired() {
        return false;
        //return true;
    }

    @Override
    public String openIdDomain() {
        //return "crowd.devpv.com";
        //return "dev.birchwoodsoftwaregroup.com";
        //return "propertyvista.com";
        //return "localhost:8095";
        return "crowd-test.devpv.com";

        // For this to work you need to import GD certificates http://drcs.ca/blog/adding-godaddy-intermediate-certificates-to-java-jdk/
        //return "static.propertyvista.com";
        //return "11.birchwoodsoftwaregroup.com";
    }

    @Override
    protected String getAppUrlSeparator() {
        return ".";
    }

    @Override
    public boolean isDepoymentUseNewDevDomains() {
        return true;
    }

    @Override
    public boolean isDepoymentApplicationDispatcher() {
        if (devContextLess) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        if (devContextLess) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        String hostPrefix = ".dev";
        if (isDepoymentUseNewDevDomains()) {
            hostPrefix = ".local";
        }
        if (ServerContext.getRequest() != null) {
            // 192.168.179.1  -> .h.birchwoodsoftwaregroup.com
            // 10.0.2.2  -> .m.birchwoodsoftwaregroup.com
            String serverName = ServerContext.getRequest().getServerName();
            if (serverName.endsWith("m.birchwoodsoftwaregroup.com") || serverName.endsWith("m.pyx4j.com")) {
                hostPrefix = ".m";
            } else if (serverName.endsWith("h.birchwoodsoftwaregroup.com") || serverName.endsWith("h.pyx4j.com")) {
                hostPrefix = ".h";
            }
        }
        StringBuilder b = new StringBuilder();
        b.append(hostPrefix);
        if (isDepoymentUseNewDevDomains()) {
            b.append(".devpv.com");
        } else {
            b.append(".birchwoodsoftwaregroup.com");
        }

        if (!isDepoymentHttps() || !secure) {
            b.append(":").append(devServerPort);
        }

        if (!devContextLess) {
            b.append(devContextPath);
        }

        b.append("/");
        return b.toString();
    }

    @Override
    public int interfaceSSHDPort() {
        if (VistaTODO.codeBaseIsProdBranch) {
            return super.interfaceSSHDPort() + 1;
        } else {
            return super.interfaceSSHDPort() + DevelopmentBranchProfile.jettyServerPortOffset();
        }
    }

    @Override
    public String getGoogleAnalyticsKey() {
        // If there are problem with tracker enable here to test locally
        //return VistaSettings.googleAnalyticsDevKey;
        return null;
    }

    @Override
    public BankingSimulatorConfiguration getBankingSimulatorConfiguration() {
        return new BankingSimulatorConfigurationCustom(this) {

            // The same simulated service will be used even when production is ON.

            @Override
            public boolean isFundsTransferSimulationConfigurable() {
                return true;
            }
        };
    }
}
