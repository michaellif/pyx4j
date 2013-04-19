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
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryMeta;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceYardiFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestCategoryMeta getMaintenanceRequestCategoryMeta(boolean labelsOnly) {
        return MaintenanceYardiCategoryManager.instance().getMaintenanceRequestCategoryMeta(labelsOnly);
    }

    @Override
    public List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        return MaintenanceYardiManager.instance().getOpenMaintenanceRequests(tenant);
    }

    @Override
    public List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        return MaintenanceYardiManager.instance().getClosedMaintenanceRequests(tenant);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Tenant tenant) {
        MaintenanceYardiManager.instance().postMaintenanceRequest(maintenanceRequest, tenant);
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto) {
        MaintenanceYardiManager.instance().cancelMaintenanceRequest(dto);
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate) {
        throw new Error("This method should not be called for Yardi maintenance requests");
    }

    @Override
    public MaintenanceRequest getMaintenanceRequest(String id) {
        // TODO Auto-generated method stub
        return null;
    }

}
