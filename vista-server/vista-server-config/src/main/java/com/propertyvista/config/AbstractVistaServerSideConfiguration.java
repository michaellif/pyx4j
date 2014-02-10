/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.LifecycleListener;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.VistaFeaturesLifecycleListener;
import com.propertyvista.shared.config.VistaSettings;

public abstract class AbstractVistaServerSideConfiguration extends EssentialsServerSideConfiguration {

    public File getConfigDirectory() {
        return new File(new File(LoggerConfig.getContainerHome(), "conf"), LoggerConfig.getContextName());
    }

    public abstract PropertiesConfiguration getConfigProperties();

    @Override
    public Collection<LifecycleListener> getLifecycleListeners() {
        Collection<LifecycleListener> rc = new ArrayList<LifecycleListener>(super.getLifecycleListeners());
        rc.add(new VistaFeaturesLifecycleListener());
        return rc;
    }

    public abstract Integer enviromentId();

    public abstract boolean isVistaDemo();

    public abstract boolean isVistaQa();

    public abstract boolean openDBReset();

    public abstract Set<DemoPmc> dbResetPreloadPmc();

    public abstract boolean openIdRequired();

    public abstract boolean openIdRequiredMedia();

    public abstract String openIdDomain();

    public abstract String openIdDomainIdentifier(String userDomain);

    public abstract String openIdProviderDomain();

    public abstract boolean isAppsContextlessDepoyment();

    public abstract String getApplicationURLNamespace(boolean secure);

    public abstract String getDefaultApplicationURL(VistaApplication application, String pmcDnsName);

    public abstract File vistaWorkDir();

    public abstract int interfaceSSHDPort();

    public abstract File getTenantSureInterfaceSftpDirectory();

    public abstract String getTenantSureEmailSender();

    public abstract IMailServiceConfigConfiguration getTenantSureMailServiceConfiguration();

    public abstract TenantSureConfiguration getTenantSureConfiguration();

    public abstract BankingSimulatorConfiguration getBankingSimulatorConfiguration();

    public abstract File getCaledonInterfaceWorkDirectory();

    public abstract File getBmoInterfaceWorkDirectory();

    public abstract CaledonFundsTransferConfiguration getCaledonFundsTransferConfiguration();

    public abstract BmoInterfaceConfiguration getBmoInterfaceConfiguration();

    public boolean isGoogleAnalyticDisableForEmployee() {
        return false;
    }

    public String getGoogleAnalyticsKey() {
        return VistaSettings.googleAnalyticsDevKey;
    }

    public boolean enviromentTitleVisible() {
        return true;
    }

    public abstract String rdateServer();

    public abstract EncryptedStorageConfiguration getEncryptedStorageConfiguration();

}
