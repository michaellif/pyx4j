/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.domain.dto.BillingHistoryDTO;
import com.propertyvista.portal.domain.dto.PaymentDataDTO;
import com.propertyvista.portal.domain.dto.PaymentDataDTO.PaymentStatus;
import com.propertyvista.portal.rpc.portal.services.resident.BillingHistoryService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class BillingHistoryServiceImpl implements BillingHistoryService {

    @Override
    public void getBillingHistory(AsyncCallback<BillingHistoryDTO> callback) {
        BillingHistoryDTO billingHistory = EntityFactory.create(BillingHistoryDTO.class);
        billingHistory.bills().addAll(listBills());
        billingHistory.payments().addAll(listPayments());
        callback.onSuccess(billingHistory);
    }

    private List<BillDataDTO> listBills() {
        Vector<BillDataDTO> bills = new Vector<BillDataDTO>();
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);

        LeaseTermTenant tenant = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenant.leaseTermV());
        Persistence.service().retrieve(tenant.leaseTermV().holder().lease());

        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), tenant.leaseTermV().holder().lease().billingAccount()));
        for (Bill bill : Persistence.service().query(criteria)) {
            BillDataDTO dto = EntityFactory.create(BillDataDTO.class);
            dto.setPrimaryKey(bill.getPrimaryKey());
            dto.referenceNo().setValue(bill.billSequenceNumber().getValue());
            dto.amount().setValue(bill.totalDueAmount().getValue());
            dto.dueDate().setValue(bill.dueDate().getValue());
            //TODO 1
            // dto.fromDate().setValue(bill.billingCycle().executionDate().getValue());

            dto.transactionId();
            dto.transactionStatus();

            bills.add(dto);
        }
        return bills;
    }

    private List<PaymentDataDTO> listPayments() {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().billingAccount().lease(), TenantAppContext.getCurrentUserLeaseIdStub());
        criteria.desc(criteria.proto().createdDate());

        List<PaymentRecord> paymentRecords = Persistence.service().query(criteria);
        List<PaymentDataDTO> payments = new ArrayList<PaymentDataDTO>();
        for (PaymentRecord paymentRecord : paymentRecords) {
            PaymentDataDTO payment = EntityFactory.create(PaymentDataDTO.class);
            payment.paidOn().setValue(paymentRecord.receivedDate().getValue());
            payment.total().setValue(paymentRecord.amount().getValue());
            payment.transactionId().setValue(paymentRecord.transactionAuthorizationNumber().getValue());
            payment.paymentMethod().set(paymentRecord.paymentMethod());
            payment.status().setValue(paymentRecord.paymentStatus().getValue().isProcessed() ? PaymentStatus.Confirmed : PaymentStatus.Pending);
            payments.add(payment);
        }

        return payments;
    }

}
