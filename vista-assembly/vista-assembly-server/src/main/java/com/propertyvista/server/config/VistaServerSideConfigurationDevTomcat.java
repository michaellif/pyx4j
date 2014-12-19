/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.security.server.ThrottleConfig;

public class VistaServerSideConfigurationDevTomcat extends VistaServerSideConfigurationDev {

    @Override
    public boolean openIdRequired() {
        return false;
    }

    @Override
    public boolean isDepoymentApplicationDispatcher() {
        return getConfigProperties().getBooleanValue("vista.depoymentApplicationDispatcher", false);
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return getConfigProperties().getBooleanValue("vista.appsContextlessDepoyment", false);
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return getConfigProperties().getValue("ApplicationURLNamespace", ".local.devpv.com:9000/vista/");
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationPostgreSQL();
    }

    @Override
    public int interfaceSSHDPort() {
        //return super.interfaceSSHDPort();
        return 0;
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return null;
    }
}
