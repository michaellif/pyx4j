/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceFacade {

    IssueElement getMaintenanceRequestCategoryMeta();

    List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant);

    List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant);

    void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Tenant tenant);

    void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto);

    void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate);

}
