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
package com.propertyvista.biz.financial.maintenance.internal;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceInternalFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestCategory getMaintenanceRequestCategory() {
        return MaintenanceInternalCategoryManager.instance().getMaintenanceRequestCategories();
    }

    @Override
    public List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        return MaintenanceInternalManager.instance().getOpenMaintenanceRequests(tenant);
    }

    @Override
    public List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        return MaintenanceInternalManager.instance().getClosedMaintenanceRequests(tenant);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Tenant tenant) {
        MaintenanceInternalManager.instance().postMaintenanceRequest(maintenanceRequest, tenant);
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dto.id()));
        List<MaintenanceRequest> rs = Persistence.service().query(criteria);
        if (rs.size() > 0) {
            MaintenanceRequest req = rs.get(0);
            req.status().setValue(MaintenanceRequestStatus.Cancelled);
            req.updated().setValue(new LogicalDate(SystemDateManager.getDate()));
            Persistence.service().merge(req);
            Persistence.service().commit();
            callback.onSuccess(null);
        } else {
            callback.onFailure(new Throwable("Ticket not found."));
        }
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dto.id()));
        List<MaintenanceRequest> rs = Persistence.service().query(criteria);
        if (rs.size() > 0) {
            MaintenanceRequest req = rs.get(0);
            req.surveyResponse().rating().setValue(rate);
            Persistence.service().merge(req);
            Persistence.service().commit();
            callback.onSuccess(null);
        } else {
            callback.onFailure(new Throwable("Ticket not found."));
        }
    }
}
