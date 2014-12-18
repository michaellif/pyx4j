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
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.biz.system.AbstractYardiFacadeImpl;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.services.YardiMaintenanceRequestsService;

public class YardiMaintenanceFacadeImpl extends AbstractYardiFacadeImpl implements YardiMaintenanceFacade {

    private static final Logger log = LoggerFactory.getLogger(YardiMaintenanceFacadeImpl.class);

    @Override
    public Date getMetaTimestamp(Building building) {
        return YardiMaintenanceRequestsService.getInstance().getMetaTimestamp(getPmcYardiCredential(building));
    }

    @Override
    public Date getTicketTimestamp(Building building) {
        return YardiMaintenanceRequestsService.getInstance().getTicketTimestamp(getPmcYardiCredential(building));
    }

    @Override
    public MaintenanceRequest postMaintenanceRequest(MaintenanceRequest request) throws YardiServiceException, RemoteException {
        return YardiMaintenanceRequestsService.getInstance().postMaintenanceRequest(getPmcYardiCredential(request.building()), request);
    }

    @Override
    public void loadMaintenanceRequests(Building building) throws YardiServiceException, RemoteException {
        if (building == null) {
            StringBuilder errors = new StringBuilder();
            for (PmcYardiCredential yc : getPmcYardiCredentials()) {
                try {
                    YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequests(yc);
                } catch (YardiServiceException e) {
                    log.error("Yardi Interface: {} {}", yc.serviceURLBase().getValue(), e);
                    errors.append(e.getMessage() + "\n");
                } catch (RemoteException e) {
                    log.error("Yardi Interface: {} {}", yc.serviceURLBase().getValue(), e);
                    errors.append("Connection Failed\n");
                }
            }
            if (errors.length() > 0) {
                throw new YardiServiceException(errors.toString());
            }
        } else {
            YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequests(getPmcYardiCredential(building));
        }
    }
}
