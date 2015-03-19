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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.server.AclRevalidator;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.BankingSimulatorConfiguration;
import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.CaledonCardsConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.config.EquifaxInterfaceConfiguration;
import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.config.VistaCookieLocaleResolver;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsDNSConfig;
import com.propertyvista.config.deployment.VistaApplicationContextResolver;
import com.propertyvista.config.deployment.VistaNamespaceResolver;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.ci.bugs.MemoryLeakJAXBContextLifecycleListener;
import com.propertyvista.server.common.security.VistaAntiBot;
import com.propertyvista.server.config.appcontext.EnvNResolver;
import com.propertyvista.server.security.VistaAccessControlList;
import com.propertyvista.server.security.VistaAclRevalidator;

public class VistaServerSideConfiguration extends AbstractVistaServerSideConfiguration {

    private PropertiesConfiguration configProperties;

    public VistaServerSideConfiguration() {
        if (this.isDevelopmentBehavior()) {
            setOverrideSessionMaxInactiveInterval(2 * Consts.HOURS2SEC);
        }
    }

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
        } else if ("vista88".equals(contextName)) {
            return new VistaServerSideConfiguration88();
        } else if ("vista99".equals(contextName)) {
            return new VistaServerSideConfiguration99();

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
    public Integer enviromentId() {
        return null;
    }

    @Override
    public File getConfigDirectory() {
        return new File(new File(LoggerConfig.getContainerHome(), "conf"), LoggerConfig.getContextName());
    }

    private Map<String, String> readProperties() {
        return PropertiesConfiguration.loadProperties(new File(getConfigDirectory(), "config.properties"));
    }

    @Override
    public PropertiesConfiguration getConfigProperties() {
        if (configProperties == null) {
            configProperties = new PropertiesConfiguration(null, readProperties());
        }
        return configProperties;
    }

    @Override
    public void reloadProperties() {
        getConfigProperties().reloadProperties(readProperties());
    }

    @Override
    public String getMainApplicationURL() {
        return getApplicationDeploymentProtocol() + "://" + NamespaceManager.getNamespace() + getApplicationURLNamespace(true);
    }

    @Override
    public String getApplicationDeploymentProtocol() {
        if (isDepoymentHttps()) {
            return "https";
        } else {
            return "http";
        }
    }

    @Override
    public String getDevelopmentSessionCookieName() {
        if (getApplicationURLNamespace(true) == null) {
            return "dev_access";
        } else {
            return "dev_access" + getApplicationURLNamespace(true).replaceAll("[\\-\\./:]", "_");
        }
    }

    @Override
    public boolean isDepoymentHttps() {
        return getConfigProperties().getBooleanValue("vista.depoymentHttps", true);
    }

    @Override
    public boolean isDepoymentApplicationDispatcher() {
        return getConfigProperties().getBooleanValue("vista.depoymentApplicationDispatcher", true);
    }

    @Override
    public boolean isDepoymentUseNewDevDomains() {
        return getConfigProperties().getBooleanValue("vista.depoymentUseNewDevDomains", true);
    }

    @Override
    public VistaApplicationContextResolver createApplicationContextResolver() {
        return new EnvNResolver((getApplicationURLNamespace(true)));
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        if (isDepoymentUseNewDevDomains()) {
            return ".devpv.com/";
        } else {
            return ".birchwoodsoftwaregroup.com/";
        }
    }

    protected String getAppUrlSeparator() {
        return "-";
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return true;
    }

    @Override
    public String getDefaultApplicationURL(VistaApplication application, String pmcDnsName) {
        String hostName;
        if (application.requirePmcResolution()) {
            hostName = pmcDnsName + "-" + application.getDnsNameFragment();
        } else {
            hostName = application.getDnsNameFragment();
        }
        String base = getApplicationDeploymentProtocol() + "://" + hostName + getApplicationURLNamespace(true);
        if (isAppsContextlessDepoyment() && (application != VistaApplication.prospect)) {
            return base;
        } else {
            return base + application.name();
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
    public NamespaceResolver getNamespaceResolver(HttpServletRequest httpRequest) {
        return VistaNamespaceResolver.instance().getNamespaceResolver(httpRequest);
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
        return new VistaRPCServiceFactory();
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getDefaultConfig(this);
    }

    @Override
    public String getTenantSureEmailSender() {
        return getConfigProperties().getValue("mail.tenantsure.sender", getApplicationEmailSender());
    }

    @Override
    public IMailServiceConfigConfiguration getTenantSureMailServiceConfiguration() {
        return VistaSMTPMailServiceConfig.getCustomConfig("mail.tenantsure", this);
    }

    @Override
    public TenantSureConfiguration getTenantSureConfiguration() {
        return new VistaTenantSureConfiguration(this);
    }

    @Override
    public IMailServiceConfigConfiguration getOperationsAlertMailServiceConfiguration() {
        return VistaSMTPMailServiceConfig.getCustomConfig("mail.operations-alert", this);
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
        if (getConfigProperties().getBooleanValue("vista.hrottleOff", false)) {
            return null;
        } else {
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
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return getConfigProperties().getBooleanValue("isDevelopmentBehavior", true);
    }

    @Override
    public boolean isDemoBehavior() {
        return getConfigProperties().getBooleanValue("isDemoBehavior", false);
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
    public boolean openIdRequired() {
        return true;
    }

    @Override
    public boolean openIdRequiredMedia() {
        return openIdRequired();
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
    public int interfaceSSHDPort() {
        return 8822;
    }

    @Override
    public String rdateServer() {
        return getConfigProperties().getValue("rdateServer", "rdate.birchwoodsoftwaregroup.com");
    }

    @Override
    public String openIdProviderDomain() {
        return "dev.birchwoodsoftwaregroup.com";
    }

    @Override
    public String openIdDomain() {
        String configDomain = getConfigProperties().getValue("vista.depoymentDevOpenIdDomain", System.getProperty("com.propertyvista.dev.domain"));
        if (CommonsStringUtils.isStringSet(configDomain)) {
            return configDomain;
        } else {
            return "crowd.devpv.com";
            //return "propertyvista.com"; // Google
            //return "dev.birchwoodsoftwaregroup.com"; // Vista itself
        }
    }

    @Override
    public String openIdDomainIdentifier(String userDomain) {
        if (CommonsStringUtils.isStringSet(userDomain)) {
            if (userDomain.equals("crowd.devpv.com")) {
                return "https://crowd.devpv.com/openidserver/op";
            } else if (userDomain.equals("crowd-test.devpv.com")) {
                return "https://crowd-test.devpv.com/openidserver/op";
            } else if (userDomain.equals("localhost:8095")) {
                return "http://localhost:8095/openidserver/op";
            } else if (userDomain.equals("dev.birchwoodsoftwaregroup.com")) {
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

    @Override
    public File vistaWorkDir() {
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
    public File getBmoInterfaceWorkDirectory() {
        String dirName = getConfigProperties().getValue("vista-work.bmo.dir");
        if (CommonsStringUtils.isStringSet(dirName)) {
            return new File(dirName);
        } else {
            return new File(vistaWorkDir(), "bmo");
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

    @Override
    public BankingSimulatorConfiguration getBankingSimulatorConfiguration() {
        return new BankingSimulatorConfigurationCustom(this);
    }

    @Override
    public CaledonFundsTransferConfiguration getCaledonFundsTransferConfiguration() {
        return new CaledonFundsTransferConfigurationSimulator(this);
    }

    @Override
    public CaledonCardsConfiguration getCaledonCardsConfiguration() {
        return new CaledonCardsConfigurationSimulator(this);
    }

    @Override
    public BmoInterfaceConfiguration getBmoInterfaceConfiguration() {
        return new BmoInterfaceConfigurationSimulator(this);
    }

    @Override
    public EquifaxInterfaceConfiguration getEquifaxInterfaceConfiguration() {
        return new EquifaxInterfaceConfigurationUatCustom(this);
    }

    @Override
    public int yardiConnectionTimeout() {
        return getConfigProperties().getSecondsValue("yardiConnectionTimeout", Consts.MIN2SEC * 9);
    }

    @Override
    public PropertiesConfiguration yardiInterfaceProperties() {
        return new PropertiesConfiguration("yardiInterface", getConfigProperties());
    }

    @Override
    public VistaSystemsDNSConfig getVistaSystemDNSConfig() {
        return new VistaSystemsDNSConfigSimulator(this);
    }

    @Override
    public boolean walkMeEnabled(VistaApplication application) {
        boolean defaultVistaApplicationEnabled = true;

        // walkMe disabled in Online Application for now
        if (application == VistaApplication.prospect) {
            defaultVistaApplicationEnabled = false;
        }
        return getConfigProperties().getBooleanValue("walkMeEnabled", true)
                && getConfigProperties().getBooleanValue("walkMeEnabled." + application.name(), defaultVistaApplicationEnabled);
    }

    @Override
    public String walkMeJsAPIUrl(VistaApplication application) {
        boolean production = (!ApplicationMode.isDevelopment()) || (ApplicationMode.isDemo());

        String testPart = "";
        if (!production) {
            testPart = "test/";
        }

        String defaultUrl;
        switch (application) {
        case crm:
            defaultUrl = "https://d3b3ehuo35wzeh.cloudfront.net/users/941bfed7d73c45cea7192ffc17c15d77/" + testPart
                    + "walkme_941bfed7d73c45cea7192ffc17c15d77_https.js";
            break;
        case resident:
            defaultUrl = "https://d3b3ehuo35wzeh.cloudfront.net/users/08186ae265d64e18953363c7294ab093/" + testPart
                    + "walkme_08186ae265d64e18953363c7294ab093_https.js";
            break;
        case prospect:
            defaultUrl = "https://d3b3ehuo35wzeh.cloudfront.net/users/04168f1a4dfe43709d560b7942e16b97/" + testPart
                    + "walkme_04168f1a4dfe43709d560b7942e16b97_https.js";
            break;
        default:
            return null;
        }
        return getConfigProperties().getValue("walkMeJsAPIUrl." + application.name(), defaultUrl);
    }

}
