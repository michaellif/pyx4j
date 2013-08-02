/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-01
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.shared.config.VistaDemo;

public class VistaTenantSureConfiguration extends TenantSureConfiguration {

    private final VistaServerSideConfiguration serverSideConfiguration;

    public VistaTenantSureConfiguration(VistaServerSideConfiguration serverSideConfiguration) {
        this.serverSideConfiguration = serverSideConfiguration;
    }

    @Override
    public boolean useCfcApiAdapterMockup() {
        if (VistaDeployment.isVistaProduction()) {
            return false;
        } else {
            return serverSideConfiguration.getConfigProperties().getBooleanValue("tenantsure.cfcApiMockup", VistaDemo.isDemo());
        }
    }

    @Override
    public String cfcApiEndpointUrl() {
        if (VistaDeployment.isVistaProduction()) {
            return serverSideConfiguration.getConfigProperties().getValue("tenantsure.cfcApiEndpointUrl", "https://api.cfcprograms.com/cfc_api.asmx");
        } else {
            return serverSideConfiguration.getConfigProperties().getValue("tenantsure.cfcApiEndpointUrl", "http://testapi.cfcprograms.com/cfc_api.asmx");
        }
    }

}
