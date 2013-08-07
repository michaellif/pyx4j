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

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.building.Building;

public interface YardiMaintenanceFacade {

    Date getMetaTimestamp(Building building);

    Date getTicketTimestamp(Building building);

    MaintenanceRequest postMaintenanceRequest(MaintenanceRequest request) throws YardiServiceException, RemoteException;

    void loadMaintenanceRequests(Building building) throws YardiServiceException, RemoteException;
}
