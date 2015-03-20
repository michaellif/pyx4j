/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-18
 * @author vlads
 */
package com.propertyvista.server.config;

import java.util.EnumSet;
import java.util.Set;

import com.propertyvista.config.deployment.VistaApplicationContextResolver;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.appcontext.ProdCustomersDemoResolver;
import com.propertyvista.shared.config.VistaSettings;

public class VistaServerSideConfigurationProdCustomersDemo extends VistaServerSideConfigurationCustom {

    @Override
    public boolean isDemoBehavior() {
        return true;
    }

    @Override
    public Set<DemoPmc> dbResetPreloadPmc() {
        return EnumSet.of(DemoPmc.demo);
    }

    @Override
    public boolean openDBReset() {
        return true;
    }

    @Override
    public String getDefaultApplicationURL(VistaApplication application, String pmcDnsName) {
        switch (application) {
        case crm:
            return "https://demo.propertyvista.com/";
        case site:
            return "http://demo.residentportalsite.com/";
        case resident:
            return "https://demo.my-community.co/";
        case prospect:
            return "https://demo.my-community.co/" + application.name();
        default:
            return super.getDefaultApplicationURL(application, pmcDnsName);
        }
    }

    @Override
    public VistaApplicationContextResolver createApplicationContextResolver() {
        return new ProdCustomersDemoResolver();
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return "-cdemo.propertyvista.biz/";
    }

    @Override
    public String getDevelopmentSessionCookieName() {
        if (getApplicationURLNamespace(true) == null) {
            return "vista_demo";
        } else {
            return "vista_demo" + getApplicationURLNamespace(true).replaceAll("[\\-\\./:]", "_");
        }
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Demo\" <noreply-demo@propertyvista.com>";
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
    public int interfaceSSHDPort() {
        return 0;
    }
}
