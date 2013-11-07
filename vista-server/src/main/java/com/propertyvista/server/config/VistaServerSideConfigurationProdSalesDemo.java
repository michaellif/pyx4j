/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-05
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.EnumSet;
import java.util.Set;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaServerSideConfigurationProdSalesDemo extends VistaServerSideConfigurationCustom {

    @Override
    public boolean isVistaDemo() {
        return true;
    }

    @Override
    public boolean enviromentTitleVisible() {
        return getConfigProperties().getBooleanValue("enviromentTitleVisible", true);
    }

    @Override
    public Set<DemoPmc> dbResetPreloadPmc() {
        return EnumSet.of(DemoPmc.star, DemoPmc.redridge, DemoPmc.rockville, DemoPmc.gondor);
    }

    @Override
    public String getDefaultApplicationURL(VistaApplication application, String pmcDnsName) {
        switch (application) {
        case crm:
            return "http://" + pmcDnsName + ".propertyvista.biz/";
        case site:
            return "http://" + pmcDnsName + ".residentportal.info/";
        case resident:
            return "https://" + pmcDnsName + ".residentportal.info/";
        case prospect:
            return "http://" + pmcDnsName + ".prospectportal.info/";
        case onboarding:
            return "http://start.propertyvista.biz/";
        default:
            return super.getDefaultApplicationURL(application, pmcDnsName);
        }
    }

    @Override
    public int interfaceSSHDPort() {
        return 0;
    }
}
