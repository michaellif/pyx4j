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

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaServerSideConfigurationProd extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // This environment selector defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=Prod
        if ("vista".equals(contextName)) {
            return this;
        } else if ("vista-pangroup".equals(contextName)) {
            return new VistaServerSideConfigurationProdPangroup();
        } else if ("vista-main".equals(contextName)) {
            return new VistaServerSideConfigurationProdMain();
        } else {
            return new VistaServerSideConfigurationCustom();
        }
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
            return "https://" + pmcDnsName + ".staging02-crm.birchwoodsoftwaregroup.com/";
        default:
            return super.getDefaultBaseURLvistaCrm(pmcDnsName);
        }
    }

    @Override
    public String getDefaultBaseURLresidentPortal(String pmcDnsName, boolean secure) {
        String protocol;
        if (secure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return protocol + pmcDnsName + ".residentportalsite.com/";
        case staging:
            return protocol + pmcDnsName + ".staging02.birchwoodsoftwaregroup.com/";
        default:
            return super.getDefaultBaseURLresidentPortal(pmcDnsName, secure);
        }
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://" + pmcDnsName + ".prospectportalsite.com/";
        case staging:
            return "https://" + pmcDnsName + ".staging02-ptapp.birchwoodsoftwaregroup.com/";
        default:
            return super.getDefaultBaseURLvistaCrm(pmcDnsName);
        }
    }

    @Override
    public String getDefaultBaseURLvistaAdmin() {
        switch (VistaDeployment.getSystemIdentification()) {
        case production:
            return "https://prod02.birchwoodsoftwaregroup.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.ADMIN_URL;
        case staging:
            return "https://staging02.birchwoodsoftwaregroup.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.ADMIN_URL;
        default:
            return super.getDefaultBaseURLvistaAdmin();
        }
    }

    @Override
    public String getCaledonCompanyId() {
        return "BIRCHWOOD";
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".prod02.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista\" <no-reply@propertyvista.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("prod-");
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
}
