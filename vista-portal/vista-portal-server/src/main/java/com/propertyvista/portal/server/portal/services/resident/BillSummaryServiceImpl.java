/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-31
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.BillSummaryDTO;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class BillSummaryServiceImpl implements BillSummaryService {

    @Override
    public void retrieve(AsyncCallback<BillSummaryDTO> callback) {
        callback.onSuccess(retrieve());
    }

    static BillSummaryDTO retrieve() {
        Tenant tenant = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenant.leaseTermV());
        Persistence.service().retrieve(tenant.leaseTermV().holder().lease());

        Lease lease = tenant.leaseTermV().holder().lease();
        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);

        BillSummaryDTO entity = EntityFactory.create(BillSummaryDTO.class);
        entity.currentBill().set(ServerSideFactory.create(BillingFacade.class).getLatestBill(lease));
        entity.currentBalance().setValue(arFacade.getCurrentBalance(lease.billingAccount()));
        entity.latestActivities().addAll(arFacade.getNotAcquiredLineItems(lease.billingAccount()));

        return entity;
    }
}
