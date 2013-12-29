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
    protected String getApplicationDeploymentProtocol() {
        return "https";
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return ".propertyvista.biz/";
    }

    @Override
    public int interfaceSSHDPort() {
        return 0;
    }
}
