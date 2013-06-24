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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.ServletContext;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.LifecycleListener;
import com.pyx4j.config.server.LocaleResolver;
import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.EssentialsRPCServiceFactory;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.config.VistaCookieLocaleResolver;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.ci.bugs.MemoryLeakJAXBContextLifecycleListener;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.security.VistaAccessControlList;
import com.propertyvista.server.security.VistaAclRevalidator;

public class VistaServerSideConfiguration extends AbstractVistaServerSideConfiguration {

    private PropertiesConfiguration configProperties;

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        if ("vista11".equals(contextName)) {
            return new VistaServerSideConfiguration11();
        } else if ("vista22".equals(contextName)) {
            return new VistaServerSideConfiguration22();
        } else if ("vista33".equals(contextName)) {
            return new VistaServerSideConfiguration33();
        } else if ("vista44".equals(contextName)) {
            return new VistaServerSideConfiguration44();
        } else if ("vista55".equals(contextName)) {
            return new VistaServerSideConfiguration55();
        } else if ("vista66".equals(contextName)) {
            return new VistaServerSideConfiguration66();
        } else if ("vista77".equals(contextName)) {
            return new VistaServerSideConfiguration77();

        } else if ("vista-prod-demo".equals(contextName)) {
            return new VistaServerSideConfigurationProdCustomersDemo();
        } else if ("vista-sales-demo".equals(contextName)) {
            return new VistaServerSideConfigurationProdSalesDemo();

        } else if ("vistad11".equals(contextName)) {
            return new VistaServerSideConfigurationD11();
        } else if ("vistad22".equals(contextName)) {
            return new VistaServerSideConfigurationD22();
        } else if ("vistad33".equals(contextName)) {
            return new VistaServerSideConfigurationD33();
        } else if ("vistad44".equals(contextName)) {
            return new VistaServerSideConfigurationD44();

        } else if (servletContext.getServerInfo().contains("jetty")) {
            return new VistaServerSideConfigurationDev();
        } else if ("vista".equals(contextName)) {
            return new VistaServerSideConfiguration22();
        }
        return this;
    }

    @Override
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
        return getApplicationDeploymentProtocol() + "://" + NamespaceManager.getNamespace() + getApplicationURLNamespace(true);
    }

    protected String getApplicationDeploymentProtocol() {
        return "http";
    }

    @Override
    public String getDevelopmentSessionCookieName() {
        if (getApplicationURLNamespace(true) == null) {
            return "dev_access";
        } else {
            return "dev_access" + getApplicationURLNamespace(true).replaceAll("[\\-\\./:]", "_");
        }
    }

    public String getApplicationURLNamespace(boolean secure) {
        return ".birchwoodsoftwaregroup.com/";
    }

    protected String getAppUrlSeparator() {
        return "-";
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return true;
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        String base = getApplicationDeploymentProtocol() + "://" + pmcDnsName + getAppUrlSeparator() + "crm" + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.CRM_URL;
        }
    }

    @Override
    public String getDefaultBaseURLvistaField(String pmcDnsName) {
        String base = getApplicationDeploymentProtocol() + "://" + pmcDnsName + getAppUrlSeparator() + "field" + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.FIELD_URL;
        }
    }

    @Override
    public String getDefaultBaseURLresidentPortalSite(String pmcDnsName, boolean secure) {
        String base = secure ? getApplicationDeploymentProtocol() : "http";
        base += "://" + pmcDnsName + getAppUrlSeparator() + "portal" + getApplicationURLNamespace(secure);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.PORTAL_URL;
        }
    }

    @Override
    public String getDefaultBaseURLresidentPortalWeb(String pmcDnsName) {
        String base = getApplicationDeploymentProtocol() + "://" + pmcDnsName + getAppUrlSeparator() + "portal" + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.PORTAL_URL + DeploymentConsts.RESIDENT_URL_PATH;
        }
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        String base = getApplicationDeploymentProtocol() + "://" + pmcDnsName + getAppUrlSeparator() + "ptapp" + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.PTAPP_URL;
        }
    }

    @Override
    public String getDefaultBaseURLvistaOperations() {
        return getApplicationDeploymentProtocol() + "://" + "operations" + getApplicationURLNamespace(true) + DeploymentConsts.OPERATIONS_URL;
    }

    @Override
    public String getCardServiceSimulatorUrl() {
        return getConfigProperties().getValue("simulator.cardServiceSimulatorUrl",
                "http://" + "operations" + getApplicationURLNamespace(false) + "o/" + "CardServiceSimulation");
    }

    @Override
    public String getDefaultBaseURLvistaOnboarding() {
        String base = getApplicationDeploymentProtocol() + "://" + "start" + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment()) {
            return base;
        } else {
            return base + DeploymentConsts.ONBOARDING_URL;
        }
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
    public LocaleResolver getLocaleResolver() {
        return new VistaCookieLocaleResolver();
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
        return VistaSMTPMailServiceConfig.getGmailConfig(this);
    }

    @Override
    public String getTenantSureEmailSender() {
        return getConfigProperties().getValue("mail.tenantsure.sender", getApplicationEmailSender());
    }

    @Override
    public IMailServiceConfigConfiguration getTenantSureMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getCustomConfig("mail.tenantsure", this);
    }

    @Override
    public TenantSureConfiguration getTenantSureConfiguration() {
        return new VistaTenantSureConfiguration(this);
    }

    @Override
    public Collection<LifecycleListener> getLifecycleListeners() {
        Collection<LifecycleListener> rc = new ArrayList<LifecycleListener>(super.getLifecycleListeners());
        if (ApplicationMode.isDevelopment()) {
            rc.add(new MemoryLeakJAXBContextLifecycleListener());
        }
        return rc;
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
    public boolean isVistaDemo() {
        return false;
    }

    @Override
    public boolean isVistaQa() {
        return false;
    }

    @Override
    public boolean isProductionBackend() {
        return VistaDeployment.isVistaProduction();
    }

    @Override
    public Class<? extends SystemMaintenanceState> getSystemMaintenanceStateClass() {
        return VistaSystemMaintenanceState.class;
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
    public Set<DemoPmc> dbResetPreloadPmc() {
        return EnumSet.of(DemoPmc.vista, DemoPmc.star, DemoPmc.redridge, DemoPmc.rockville, DemoPmc.gondor);
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

    @Override
    public int interfaceSSHDPort() {
        return 8822;
    }

    @Override
    public String openIdProviderDomain() {
        return "dev.birchwoodsoftwaregroup.com";
    }

    @Override
    public String openIdDomain() {
        String configDomain = System.getProperty("com.propertyvista.dev.domain");
        if (CommonsStringUtils.isStringSet(configDomain)) {
            return configDomain;
        } else {
            return "propertyvista.com";
            //return "dev.birchwoodsoftwaregroup.com";
        }
    }

    @Override
    public String openIdDomainIdentifier(String userDomain) {
        if (CommonsStringUtils.isStringSet(userDomain)) {
            if (userDomain.equals("dev.birchwoodsoftwaregroup.com")) {
                return "http://static.dev.birchwoodsoftwaregroup.com:8888/vista/static/accounts/idp";
            } else if (userDomain.equals("static.propertyvista.com")) {
                return "https://static.propertyvista.com/accounts/idp";
            } else if (userDomain.equals("11.birchwoodsoftwaregroup.com")) {
                return "https://static-11.birchwoodsoftwaregroup.com/static/accounts/idp";
            } else {
                return "https://www.google.com/accounts/o8/site-xrds?hd=" + userDomain;
            }
        } else {
            return "https://www.google.com/accounts/o8/id";
        }
    }

    protected File vistaWorkDir() {
        String dirName = getConfigProperties().getValue("vista-work.dir");
        if (CommonsStringUtils.isStringSet(dirName)) {
            return new File(dirName);
        } else {
            return new File(new File("vista-work"), LoggerConfig.getContextName());
        }
    }

    @Override
    public File getCaledonInterfaceWorkDirectory() {
        String dirName = getConfigProperties().getValue("vista-work.caledon.dir");
        if (CommonsStringUtils.isStringSet(dirName)) {
            return new File(dirName);
        } else {
            return new File(vistaWorkDir(), "caledon");
        }
    }

    @Override
    public File getTenantSureInterfaceSftpDirectory() {
        String dirName = getConfigProperties().getValue("vista-work.tenant-sure.dir");
        if (CommonsStringUtils.isStringSet(dirName)) {
            return new File(dirName);
        } else {
            return new File(vistaWorkDir(), "tenant-sure");
        }
    }

    @Override
    public EncryptedStorageConfiguration getEncryptedStorageConfiguration() {
        return new VistaEncryptedStorageConfiguration(this);
    }

}
