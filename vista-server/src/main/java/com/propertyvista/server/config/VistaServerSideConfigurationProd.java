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
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.portal.rpc.DeploymentConsts;
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
    public boolean openIdrequired() {
        return false;
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://" + pmcDnsName + ".propertyvista.com/";
        case staging:
            return "https://" + pmcDnsName + "-crm-staging03.propertyvista.com/";
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getDefaultBaseURLresidentPortalSite(String pmcDnsName, boolean secure) {
        String protocol;
        if (secure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            // TODO Add ".my-community.co/"
            return protocol + pmcDnsName + ".residentportalsite.com/";
        case staging:
            return protocol + pmcDnsName + "-portal-staging03.residentportalsite.com/";
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://" + pmcDnsName + ".prospectportalsite.com/";
        case staging:
            return "https://" + pmcDnsName + "-ptapp-staging03.prospectportalsite.com/";
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getDefaultBaseURLvistaOperations() {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://operations-prod03.propertyvista.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.OPERATIONS_URL;
        case staging:
            return "https://operations-staging03.propertyvista.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.OPERATIONS_URL;
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getDefaultBaseURLvistaOnboarding() {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://start.propertyvista.com/";
        case staging:
            return "https://start-staging03.propertyvista.com/";
        default:
            throw new IllegalArgumentException(VistaDeployment.getSystemIdentification().name());
        }
    }

    @Override
    public String getCaledonCompanyId() {
        return "BIRCHWOOD";
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
