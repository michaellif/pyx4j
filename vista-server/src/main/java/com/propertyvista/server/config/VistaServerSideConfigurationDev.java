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
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.server.ThrottleConfig;

import com.propertyvista.misc.VistaTODO;

public class VistaServerSideConfigurationDev extends VistaServerSideConfiguration {

    public static int devServerPort = 8888;

    public static String devContextPath = "/vista";

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.
        return this;
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

            @Override
            public int minPoolSize() {
                return 1;
            }

            @Override
            public int maxPoolSize() {
                return 2;
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
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public boolean openIdrequired() {
        return false;
    }

    @Override
    public String openIdDomain() {
        return "dev.birchwoodsoftwaregroup.com";
    }

    @Override
    protected String getAppUrlSeparator() {
        return ".";
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return false;
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".dev.birchwoodsoftwaregroup.com:" + devServerPort + devContextPath + "/";
    }

    @Override
    public String getGoogleAnalyticsKey() {
        // If there are problem with tracker enable here to test locally
        //return VistaSettings.googleAnalyticsDevKey;
        return null;
    }
}
