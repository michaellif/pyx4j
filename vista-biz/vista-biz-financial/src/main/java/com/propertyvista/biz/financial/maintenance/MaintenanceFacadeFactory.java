/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-18
 * @author vlads
 */
package com.propertyvista.biz.financial.maintenance;

import com.pyx4j.config.server.FacadeFactory;

import com.propertyvista.biz.financial.maintenance.internal.MaintenanceInternalFacadeImpl;
import com.propertyvista.biz.financial.maintenance.yardi.MaintenanceYardiFacadeImpl;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceFacadeFactory implements FacadeFactory<MaintenanceFacade> {

    @Override
    public MaintenanceFacade getFacade() {
        if (VistaFeatures.instance().yardiIntegration() && VistaFeatures.instance().yardiMaintenance()) {
            return new MaintenanceYardiFacadeImpl();
        } else {
            return new MaintenanceInternalFacadeImpl();
        }
    }

}
