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

import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.portal.rpc.DeploymentConsts;

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
        return EnumSet.of(DemoPmc.star, DemoPmc.redridge, DemoPmc.rockville);
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        return "http://" + pmcDnsName + ".propertyvista.biz/";
    }

    @Override
    public String getDefaultBaseURLresidentPortal(String pmcDnsName, boolean secure) {
        String protocol;
        if (secure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        return protocol + pmcDnsName + ".residentportal.info/";
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        return "http://" + pmcDnsName + ".prospectportal.info/";
    }

    @Override
    public String getDefaultBaseURLvistaAdmin() {
        return "https://prod-demo.birchwoodsoftwaregroup.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.ADMIN_URL;
    }
}
