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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.config.VistaSettings;

public class VistaServerSideConfigurationProd extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        return this;
    }

    @Override
    public boolean openDBReset() {
        return false;
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return false;
    }

    @Override
    public boolean openIdRequired() {
        return false;
    }

    @Override
    public String getDefaultApplicationURL(VistaApplication application, String pmcDnsName) {
        String protocol;
        if (application == VistaApplication.site) {
            protocol = "http";
        } else {
            protocol = "https";
        }

        String hostName;
        switch (application) {
        case onboarding:
            hostName = "start";
        case operations:
            hostName = application.name();
            break;
        default:
            hostName = pmcDnsName;
            if (VistaDeployment.isVistaStaging()) {
                hostName += "-" + application.name();
            }
        }
        if (VistaDeployment.isVistaStaging()) {
            hostName += "-staging03";
        }
        String base = protocol + "://" + hostName;

        String dnsName;
        switch (application) {
        case crm:
            dnsName = ".propertyvista.com";
            break;
        case onboarding:
            dnsName = ".propertyvista.com";
            break;
        case site:
            dnsName = ".residentportalsite.com";
            break;
        case portal:
            dnsName = ".my-community.co";
            break;
        case prospect:
            dnsName = ".prospectportalsite.com";
            break;
        case operations:
            if (VistaDeployment.isVistaProduction()) {
                hostName += "-prod03";
            }
            dnsName = ".propertyvista.com";
            break;
        default:
            throw new IllegalArgumentException();
        }
        return base + dnsName + dnsName + "/";
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "-prod03.propertyvista.com/";
        case staging:
            return "-staging03.propertyvista.com/";
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista\" <no-reply@propertyvista.com>";
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        if (getConfigProperties().getValue("db.type").equals("PostgreSQL")) {
            return new VistaConfigurationPostgreSQLProperties(getConfigDirectory(), getConfigProperties().getProperties());
        } else {
            return new VistaConfigurationMySQLProperties(getConfigDirectory(), getConfigProperties().getProperties());
        }
    }

    @Override
    public String openIdProviderDomain() {
        return "static.propertyvista.com";
    }

    @Override
    public boolean isGoogleAnalyticDisableForEmployee() {
        return true;
    }

    @Override
    public String getGoogleAnalyticsKey() {
        return VistaSettings.googleAnalyticsProdKey;
    }

    @Override
    public CaledonFundsTransferConfiguration getCaledonFundsTransferConfiguration() {
        if (VistaDeployment.isVistaProduction()) {
            return new CaledonFundsTransferConfigurationProd(this);
        } else {
            throw new UserRuntimeException("FundsTransfer is disabled");
        }
    }

    @Override
    public BmoInterfaceConfiguration getBmoInterfaceConfiguration() {
        if (VistaDeployment.isVistaProduction()) {
            return new BmoInterfaceConfigurationProd(this);
        } else {
            throw new UserRuntimeException("FundsTransfer is disabled");
        }
    }
}
