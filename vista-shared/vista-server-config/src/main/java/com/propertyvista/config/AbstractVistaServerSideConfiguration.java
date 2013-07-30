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

    public abstract boolean openIdrequired();

    public abstract String openIdDomain();

    public abstract String openIdDomainIdentifier(String userDomain);

    public abstract String openIdProviderDomain();

    public abstract boolean isAppsContextlessDepoyment();

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.TenantPortal, true);
     */
    public abstract String getDefaultBaseURLresidentPortalSite(String pmcDnsName, boolean secure);

    public abstract String getDefaultBaseURLresidentPortalWeb(String pmcDnsName);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.crm, true);
     */
    public abstract String getDefaultBaseURLvistaCrm(String pmcDnsName);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.crm, true);
     */
    public abstract String getDefaultBaseURLvistaField(String pmcDnsName);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.ProspectiveApp, true);
     */
    public abstract String getDefaultBaseURLprospectPortal(String pmcDnsName);

    /**
     * This method should not be used directly
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.Operations, true);
     */
    public abstract String getDefaultBaseURLvistaOperations();

    /**
     * This method should not be used directly
     * Use @see VistaDeployment.getBaseApplicationURL(VistaApplication.Onboarding, true);
     */
    public abstract String getDefaultBaseURLvistaOnboarding();

    public abstract String getCardServiceSimulatorUrl();

    public abstract File getCaledonInterfaceWorkDirectory();

    public abstract int interfaceSSHDPort();

    public abstract File getTenantSureInterfaceSftpDirectory();

    public abstract File getCaledonSimulatorSftpDirectory();

    public abstract File getBmoSimulatorSftpDirectory();

    public abstract String getTenantSureEmailSender();

    public abstract IMailServiceConfigConfiguration getTenantSureMailServiceConfigConfiguration();

    public abstract TenantSureConfiguration getTenantSureConfiguration();

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

    public abstract EncryptedStorageConfiguration getEncryptedStorageConfiguration();

}
