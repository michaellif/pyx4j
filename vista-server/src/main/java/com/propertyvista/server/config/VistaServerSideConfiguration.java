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

import java.io.File;

import javax.servlet.ServletContext;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.EssentialsRPCServiceFactory;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.security.VistaAccessControlList;
import com.propertyvista.server.security.VistaAclRevalidator;

public class VistaServerSideConfiguration extends AbstractVistaServerSideConfiguration {

    private PropertiesConfiguration configProperties;

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        if ("vista-star".equals(contextName)) {
            return new VistaServerSideConfigurationProdStarlight();
        } else if ("vista11".equals(contextName)) {
            return new VistaServerSideConfiguration11();
        } else if ("vista22".equals(contextName)) {
            return new VistaServerSideConfiguration22();
        } else if ("vista33".equals(contextName)) {
            return new VistaServerSideConfiguration33();
        } else if ("vista44".equals(contextName)) {
            return new VistaServerSideConfiguration44();

        } else if ("vistad11".equals(contextName)) {
            return new VistaServerSideConfigurationD11();
        } else if ("vistad22".equals(contextName)) {
            return new VistaServerSideConfigurationD22();
        } else if ("vistad33".equals(contextName)) {
            return new VistaServerSideConfigurationD33();
        } else if ("vistad44".equals(contextName)) {
            return new VistaServerSideConfigurationD44();

        } else if ("vistast22".equals(contextName)) {
            return new VistaServerSideConfigurationST22();
        } else if (servletContext.getServerInfo().contains("jetty")) {
            return new VistaServerSideConfigurationDev();
        } else if ("vista".equals(contextName)) {
            return new VistaServerSideConfiguration22();
        }
        return this;
    }

    public File getConfigDirectory() {
        return new File(new File(LoggerConfig.getContainerHome(), "conf"), LoggerConfig.getContextName());
    }

    public PropertiesConfiguration getConfigProperties() {
        if (configProperties == null) {
            configProperties = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(new File(getConfigDirectory(), "config.properties")));
        }
        return configProperties;
    }

    @Override
    public String getMainApplicationURL() {
        return getApplicationDeploymentProtocol() + "://" + NamespaceManager.getNamespace() + getApplicationURLNamespace();
    }

    protected String getApplicationDeploymentProtocol() {
        return "http";
    }

    public String getApplicationURLNamespace() {
        return ".22.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getDefaultBaseURLresidentPortal(String pmcDnsName, boolean secure) {
        if (secure) {
            return getApplicationDeploymentProtocol() + "://" + "portal." + pmcDnsName + getApplicationURLNamespace() + DeploymentConsts.PORTAL_URL;
        } else {
            return "http" + "://" + "portal." + pmcDnsName + getApplicationURLNamespace() + DeploymentConsts.PORTAL_URL;
        }
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        return getApplicationDeploymentProtocol() + "://" + "crm." + pmcDnsName + getApplicationURLNamespace() + DeploymentConsts.CRM_URL;
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        return getApplicationDeploymentProtocol() + "://" + "pt." + pmcDnsName + getApplicationURLNamespace() + DeploymentConsts.PTAPP_URL;
    }

    @Override
    public String getDefaultBaseURLvistaAdmin() {
        return getApplicationDeploymentProtocol() + "://" + "internal" + getApplicationURLNamespace() + DeploymentConsts.ADMIN_URL;
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support\" <nobody@birchwoodsoftwaregroup.com>";
    }

    @Override
    public AclCreator getAclCreator() {
        return new VistaAccessControlList();
    }

    @Override
    public AclRevalidator getAclRevalidator() {
        return new VistaAclRevalidator();
    }

    @Override
    public NamespaceResolver getNamespaceResolver() {
        return new VistaNamespaceResolver();
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL();
    }

    @Override
    public DataPreloaderCollection getDataPreloaders() {
        return new VistaDataPreloaders(VistaDevPreloadConfig.createDefault());
    }

    @Override
    public IServiceFactory getRPCServiceFactory() {
        return new EssentialsRPCServiceFactory();
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("");
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig() {

            @Override
            public long getInterval() {
                return 1 * Consts.MIN2MSEC;
            }

            @Override
            public long getMaxTimeUsage() {
                return 2 * Consts.MIN2MSEC;
            }

            @Override
            public long getMaxRequests() {
                return 1000;
            }
        };
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public boolean isProductionBackend() {
        return VistaDeployment.isVistaProduction();
    }

    @Override
    public boolean openIdrequired() {
        return true;
    }

    @Override
    public boolean openDBReset() {
        return true;
    }

    @Override
    public AbstractAntiBot getAntiBot() {
        return new VistaAntiBot();
    }

    @Override
    public String getReCaptchaPrivateKey() {
        return RecaptchaConfig.getReCaptchaPrivateKey();
    }

    @Override
    public String getReCaptchaPublicKey() {
        return RecaptchaConfig.getReCaptchaPublicKey();
    }

    @Override
    public String getCaledonCompanyId() {
        return "BIRCHWOODTEST";
    }

}
