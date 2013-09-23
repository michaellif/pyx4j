/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceService;

public class MaintenanceServiceMockImpl implements MaintenanceService {

    @Override
    public void retreiveMaintenanceSummary(AsyncCallback<MaintenanceSummaryDTO> callback) {
        MaintenanceSummaryDTO maintenanceSummary = EntityFactory.create(MaintenanceSummaryDTO.class);

        callback.onSuccess(maintenanceSummary);
    }

}
