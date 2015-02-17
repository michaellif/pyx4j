/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 */
package com.propertyvista.config.tests;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.EmptyNamespaceDataResolver;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.ReCaptchaAntiBot;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.shared.AclCreator;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.BankingSimulatorConfiguration;
import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.CaledonCardsConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.config.EquifaxInterfaceConfiguration;
import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.config.VistaSystemsDNSConfig;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;

public class VistaTestsServerSideConfiguration extends AbstractVistaServerSideConfiguration {

    static {
        LoggerConfig.setContextName("tests");
    }

    @Override
    public Integer enviromentId() {
        return null;
    }

    private final static Logger log = LoggerFactory.getLogger(VistaTestsServerSideConfiguration.class);

    private final DatabaseType databaseType;

    public VistaTestsServerSideConfiguration(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public NamespaceResolver getNamespaceResolver(HttpServletRequest httpRequest) {
        return new EmptyNamespaceDataResolver();
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        switch (databaseType) {
        case MySQL:
            return new VistaTestsDBConfigurationMySQL();
        case PostgreSQL:
            return new VistaTestsDBConfigurationPostgreSQL();
        case HSQLDB:
            boolean hsqlFiles = false;
            if (hsqlFiles) {
                return new VistaTestsDBConfigurationHSQLFile();
            } else {
                return new VistaTestsDBConfigurationHSQLMemory();
            }
        case Derby:
            return new VistaTestsDBConfigurationDerbyMemory();
        default:
            throw new Error("Unsupported test DB configuration");
        }
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public AbstractAntiBot getAntiBot() {
        return new ReCaptchaAntiBot() {

            @Override
            public void assertCaptcha(String challenge, String response) {
                if (ServerSideConfiguration.instance().isDevelopmentBehavior() && "x".equals(response)) {
                    log.debug("Development CAPTCHA Ok");
                } else {
                    super.assertCaptcha(challenge, response);
                }
            }
        };
    }

    @Override
    public AclCreator getAclCreator() {
        final String SERVER_SIDE_TESTS_ACL_CREATOR = this.getClass().getPackage().getName() + ".VistaTestModuleAclCreator";
        try {
            @SuppressWarnings("unchecked")
            Class<TestAclCreator> klass = (Class<TestAclCreator>) Class.forName(SERVER_SIDE_TESTS_ACL_CREATOR);
            return klass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + SERVER_SIDE_TESTS_ACL_CREATOR, e);
        }
    }

    @Override
    public Class<? extends SystemMaintenanceState> getSystemMaintenanceStateClass() {
        return VistaSystemMaintenanceState.class;
    }

    @Override
    public boolean openDBReset() {
        return true;
    }

    @Override
    public boolean openIdRequired() {
        return false;
    }

    @Override
    public boolean openIdRequiredMedia() {
        return false;
    }

    @Override
    public boolean isDemoBehavior() {
        return false;
    }

    @Override
    public boolean isVistaQa() {
        return false;
    }

    @Override
    public String getMainApplicationURL() {
        return "http://www.propertyvista.com/";
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return ".birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationDeploymentProtocol() {
        return "http";
    }

    @Override
    public boolean isDepoymentHttps() {
        return false;
    }

    @Override
    public boolean isDepoymentApplicationDispatcher() {
        return false;
    }

    @Override
    public boolean isDepoymentUseNewDevDomains() {
        return false;
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return true;
    }

    @Override
    public String getDefaultApplicationURL(VistaApplication application, String pmcDnsName) {
        return getMainApplicationURL() + application.name();
    }

    @Override
    public int interfaceSSHDPort() {
        return 8822;
    }

    @Override
    public String openIdDomain() {
        return null;
    }

    @Override
    public String openIdDomainIdentifier(String userDomain) {
        return null;
    }

    @Override
    public String openIdProviderDomain() {
        return null;
    }

    @Override
    public Set<DemoPmc> dbResetPreloadPmc() {
        return null;
    }

    @Override
    public File vistaWorkDir() {
        return null;
    }

    @Override
    public File getCaledonInterfaceWorkDirectory() {
        return null;
    }

    @Override
    public File getBmoInterfaceWorkDirectory() {
        return null;
    }

    @Override
    public File getTenantSureInterfaceSftpDirectory() {
        return null;
    }

    @Override
    public String getTenantSureEmailSender() {
        return getApplicationEmailSender();
    }

    @Override
    public IMailServiceConfigConfiguration getOperationsAlertMailServiceConfiguration() {
        return getMailServiceConfigConfiguration();
    }

    @Override
    public IMailServiceConfigConfiguration getTenantSureMailServiceConfiguration() {
        return getMailServiceConfigConfiguration();
    }

    @Override
    public EncryptedStorageConfiguration getEncryptedStorageConfiguration() {
        return new VistaTestsEncryptedStorageConfiguration();
    }

    @Override
    public TenantSureConfiguration getTenantSureConfiguration() {
        return new TenantSureConfiguration() {

            @Override
            public boolean useCfcApiAdapterMockup() {
                return true;
            }

            @Override
            public String cfcApiEndpointUrl() {
                return null;
            }
        };
    }

    @Override
    public PropertiesConfiguration getConfigProperties() {
        return new PropertiesConfiguration(Collections.<String, String> emptyMap());
    }

    @Override
    public void reloadProperties() {
        getConfigProperties();
    }

    @Override
    public CaledonFundsTransferConfiguration getCaledonFundsTransferConfiguration() {
        return new CaledonFundsTransferConfigurationTests();
    }

    @Override
    public CaledonCardsConfiguration getCaledonCardsConfiguration() {
        return new CaledonCardsConfigurationTests(this);
    }

    @Override
    public BmoInterfaceConfiguration getBmoInterfaceConfiguration() {
        throw new Error("not supported in tests");
    }

    @Override
    public BankingSimulatorConfiguration getBankingSimulatorConfiguration() {
        throw new Error("not supported in tests");
    }

    @Override
    public EquifaxInterfaceConfiguration getEquifaxInterfaceConfiguration() {
        throw new Error("not supported in tests");
    }

    @Override
    public VistaSystemsDNSConfig getVistaSystemDNSConfig() {
        return new VistaSystemDNSConfigTests(this);
    }

    @Override
    public String rdateServer() {
        return "rdate.birchwoodsoftwaregroup.com";
    }

    @Override
    public int yardiConnectionTimeout() {
        return Consts.MIN2SEC * 9;
    }

    @Override
    public PropertiesConfiguration yardiInterfaceProperties() {
        return new PropertiesConfiguration(Collections.<String, String> emptyMap());
    }

    @Override
    public boolean walkMeEnabled(VistaApplication application) {
        return false;
    }

    @Override
    public String walkMeJsAPIUrl(VistaApplication application) {
        return null;
    }

}
