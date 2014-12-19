/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author ArtyomB
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.config.server.FacadeFactory;
import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.TenantSureConfiguration;

public class CfcApiAdapterFacadeFactory implements FacadeFactory<CfcApiAdapterFacade> {

    private static TenantSureConfiguration getTenantSureConfig() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureConfiguration();
    }

    @Override
    public CfcApiAdapterFacade getFacade() {
        TenantSureConfiguration cfgConfiguration = getTenantSureConfig();
        if (cfgConfiguration.useCfcApiAdapterMockup()) {
            return new CfcApiAdapterFacadeMockupImpl();
        } else {
            return new CfcApiAdapterFacadeImpl(cfgConfiguration);
        }

    }

}
