/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.resident.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public void retrieveTenantDashboard(AsyncCallback<TenantDashboardDTO> callback) {
        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);

        Tenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenantInLease.leaseV());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit().floorplan());
        Persistence.service().retrieve(tenantInLease.leaseV().holder().unit().building());

        dashboard.general().tenantName().setValue(tenantInLease.customer().person().name().getStringView());
        dashboard.general().floorplanName().set(tenantInLease.leaseV().holder().unit().floorplan().marketingName());
        AddressStructured address = tenantInLease.leaseV().holder().unit().building().info().address().duplicate();
        address.suiteNumber().set(tenantInLease.leaseV().holder().unit().info().number());
        dashboard.general().tenantAddress().setValue(address.getStringView());

        dashboard.billSummary().set(BillSummaryServiceImpl.retrieve());
        dashboard.maintanances().addAll(MaintenanceServiceImpl.listOpenIssues());

        callback.onSuccess(dashboard);
    }

    @Override
    public void acknowledgeMessage(AsyncCallback<TenantDashboardDTO> callback, Key messageId) {
        retrieveTenantDashboard(callback);
    }
}
