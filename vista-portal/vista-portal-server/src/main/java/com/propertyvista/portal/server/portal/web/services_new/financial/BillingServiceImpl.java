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
package com.propertyvista.portal.server.portal.web.services_new.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services_new.financial.BillingService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingServiceImpl implements BillingService {

    @Override
    public void retreiveBillingSummary(AsyncCallback<BillingSummaryDTO> callback) {
        if (true) {
            new BillingServiceMockImpl().retreiveBillingSummary(callback);
        } else {
            BillingSummaryDTO billingSummary = EntityFactory.create(BillingSummaryDTO.class);

            LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenantInLease.leaseTermV());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());

            Lease lease = tenantInLease.leaseTermV().holder().lease();

            billingSummary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);
                billingSummary.dueDate().setValue(bill.dueDate().getValue());
            }

            callback.onSuccess(billingSummary);
        }

    }

}
