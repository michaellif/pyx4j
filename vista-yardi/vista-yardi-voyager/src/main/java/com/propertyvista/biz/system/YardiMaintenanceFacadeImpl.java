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

import java.util.Date;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.yardi.services.YardiMaintenanceRequestsService;

public class YardiMaintenanceFacadeImpl implements YardiMaintenanceFacade {

    @Override
    public Date getMetaTimestamp() {
        return YardiMaintenanceRequestsService.getInstance().getMetaTimestamp();
    }

    @Override
    public Date getTicketTimestamp() {
        return YardiMaintenanceRequestsService.getInstance().getTicketTimestamp();
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request) throws YardiServiceException {
        YardiMaintenanceRequestsService.getInstance().postMaintenanceRequest(request);
    }

    @Override
    public void loadMaintenanceRequests() throws YardiServiceException {
        YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequests();
    }
}
