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
 * @version $Id$
 */
package com.propertyvista.config.tests;

import java.io.File;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.ReCaptchaAntiBot;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.security.shared.AclCreator;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;

public class VistaTestsServerSideConfiguration extends AbstractVistaServerSideConfiguration {

    static {
        LoggerConfig.setContextName("tests");
    }

    private final static Logger log = LoggerFactory.getLogger(VistaTestsServerSideConfiguration.class);

    private final DatabaseType databaseType;

    public VistaTestsServerSideConfiguration(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public NamespaceResolver getNamespaceResolver() {
        return new VistaTestsNamespaceResolver();
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        if (databaseType == DatabaseType.MySQL) {
            return new VistaTestsDBConfigurationMySQL();
        } else if (databaseType == DatabaseType.PostgreSQL) {
            return new VistaTestsDBConfigurationPostgreSQL();
        } else {
            boolean hsqlFiles = false;
            if (hsqlFiles) {
                return new VistaTestsDBConfigurationHSQLFile();
            } else {
                return new VistaTestsDBConfigurationHSQLMemory();
            }
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
    public boolean openDBReset() {
        return true;
    }

    @Override
    public boolean openIdrequired() {
        return false;
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
    public String getMainApplicationURL() {
        return "http://www.propertyvista.com/";
    }

    @Override
    public boolean isAppsContextlessDepoyment() {
        return true;
    }

    @Override
    public String getDefaultBaseURLresidentPortalSite(String pmcDnsName, boolean secure) {
        String url = getMainApplicationURL() + "r";
        if (secure) {
            return url;
        } else {
            return url.replace("https://", "http://");
        }
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        return getMainApplicationURL() + "c";
    }

    @Override
    public String getDefaultBaseURLvistaField(String pmcDnsName) {
        return getMainApplicationURL() + "f";
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        return getMainApplicationURL() + "p";
    }

    @Override
    public String getDefaultBaseURLresidentPortalWeb(String pmcDnsName) {
        return getMainApplicationURL() + "r";
    }

    @Override
    public String getDefaultBaseURLvistaOperations() {
        return getMainApplicationURL() + "a";
    }

    @Override
    public String getDefaultBaseURLvistaOnboarding() {
        return getMainApplicationURL() + "o";
    }

    @Override
    public String getCaledonCompanyId() {
        return "BIRCHWOOD2";
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
    public File getCaledonInterfaceWorkDirectory() {
        return null;
    }

    @Override
    public File getTenantSureInterfaceSftpDirectory() {
        return null;
    }

    @Override
    public String getCardServiceSimulatorUrl() {
        return null;
    }

    @Override
    public String getTenantSureEmailSender() {
        return getApplicationEmailSender();
    }

    @Override
    public IMailServiceConfigConfiguration getTenantSureMailServiceConfigConfiguration() {
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

}
