/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.rmi.RemoteException;
import java.util.Date;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.services.YardiMaintenanceRequestsService;

public class YardiMaintenanceFacadeImpl implements YardiMaintenanceFacade {

    @Override
    public Date getMetaTimestamp(Building building) {
        return YardiMaintenanceRequestsService.getInstance().getMetaTimestamp(VistaDeployment.getPmcYardiCredential(building));
    }

    @Override
    public Date getTicketTimestamp(Building building) {
        return YardiMaintenanceRequestsService.getInstance().getTicketTimestamp(VistaDeployment.getPmcYardiCredential(building));
    }

    @Override
    public MaintenanceRequest postMaintenanceRequest(MaintenanceRequest request) throws YardiServiceException, RemoteException {
        return YardiMaintenanceRequestsService.getInstance().postMaintenanceRequest(VistaDeployment.getPmcYardiCredential(request.building()), request);
    }

    @Override
    public void loadMaintenanceRequests(Building building) throws YardiServiceException, RemoteException {
        if (building == null) {
            for (PmcYardiCredential yc : VistaDeployment.getPmcYardiCredentials()) {
                YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequests(yc);
            }
        } else {
            YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequests(VistaDeployment.getPmcYardiCredential(building));
        }
    }
}
