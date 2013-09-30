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
package com.propertyvista.portal.server.portal.web.services.financial;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillViewDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.shared.config.VistaFeatures;

public class BillingServiceImpl implements BillingService {

    @Override
    public void retreiveBillingSummary(AsyncCallback<BillingSummaryDTO> callback) {
        BillingSummaryDTO summary = EntityFactory.create(BillingSummaryDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        summary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (!VistaFeatures.instance().yardiIntegration()) {
            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);
            summary.dueDate().setValue(bill.dueDate().getValue());
        }

        callback.onSuccess(summary);
    }

    @Override
    public void retreiveBillingHistory(AsyncCallback<BillingHistoryDTO> callback) {
        BillingHistoryDTO history = EntityFactory.create(BillingHistoryDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        history.bills().addAll(retrieveBillHistory(lease));

        callback.onSuccess(history);
    }

    @Override
    public void retreiveTransactionHistory(AsyncCallback<TransactionHistoryDTO> callback) {
        Lease lease = TenantAppContext.getCurrentUserLease();

        callback.onSuccess(ServerSideFactory.create(ARFacade.class).getTransactionHistory(lease.billingAccount()));
    }

    @Override
    public void retreiveLatestActivities(AsyncCallback<LatestActivitiesDTO> callback) {
        LatestActivitiesDTO activities = EntityFactory.create(LatestActivitiesDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        activities.lineItems().addAll(ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));

        callback.onSuccess(activities);
    }

    // Internals:

    private static List<BillDataDTO> retrieveBillHistory(Lease lease) {
        List<BillDataDTO> bills = new ArrayList<BillDataDTO>();
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        for (Bill bill : Persistence.service().query(criteria)) {
            BillDataDTO dto = EntityFactory.create(BillDataDTO.class);

            dto.setPrimaryKey(bill.getPrimaryKey());
            dto.referenceNo().setValue(bill.billSequenceNumber().getValue());
            dto.amount().setValue(bill.totalDueAmount().getValue());
            dto.dueDate().setValue(bill.dueDate().getValue());
            dto.fromDate().setValue(bill.executionDate().getValue());

            bills.add(dto);
        }

        return bills;
    }

    @Override
    public void retreiveBill(AsyncCallback<BillViewDTO> callback, Bill entityId) {
        Bill bill = null;
        if (entityId == null) {
            // find current bill instead:
            bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(TenantAppContext.getCurrentUserLease());
        } else {
            bill = Persistence.secureRetrieve(Bill.class, entityId.getPrimaryKey());
        }

        // create and fill resulting DTO:
        BillViewDTO result = EntityFactory.create(BillViewDTO.class);
        result.billData().set(new EntityBinder<Bill, BillDTO>(Bill.class, BillDTO.class) {
            @Override
            protected void bind() {
                bindCompleteObject();
            }
        }.createTO(bill));

        // load detached entities:
        Persistence.service().retrieve(result.billData().lineItems());
        Persistence.service().retrieve(result.billData().billingAccount());
        Persistence.service().retrieve(result.billData().billingAccount().lease());
        Persistence.service().retrieve(result.billData().billingCycle().building(), AttachLevel.ToStringMembers);

        callback.onSuccess(result);
    }
}
