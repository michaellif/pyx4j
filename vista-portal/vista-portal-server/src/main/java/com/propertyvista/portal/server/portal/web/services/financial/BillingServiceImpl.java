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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.domain.dto.financial.PaymentInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillViewDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.LatestActivitiesDTO.InvoicePaymentDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.server.security.TenantAppContext;
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

        for (InvoicePayment item : ServerSideFactory.create(PaymentFacade.class).getLatestPaymentActivity(lease.billingAccount())) {
            InvoicePaymentDTO paymentInfo = EntityFactory.create(InvoicePaymentDTO.class);

            paymentInfo.id().set(item.paymentRecord().id());
            paymentInfo.amount().setValue(item.paymentRecord().amount().getValue());
            paymentInfo.date().setValue(item.paymentRecord().receivedDate().getValue());
            paymentInfo.status().setValue(item.paymentRecord().paymentStatus().getValue());

            Persistence.ensureRetrieve(item.paymentRecord().leaseTermParticipant(), AttachLevel.Attached);
            paymentInfo.payer().set(item.paymentRecord().leaseTermParticipant().leaseParticipant().customer().person().name());

            activities.payments().add(paymentInfo);
        }

        callback.onSuccess(activities);
    }

    // Internals:

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

        BillingUtils.enhanceBillDto(bill, result.billData());

        callback.onSuccess(result);
    }

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

    public static List<PaymentInfoDTO> retrieveCurrentAutoPayments(Lease lease) {
        List<PaymentInfoDTO> currentAutoPayments = new ArrayList<PaymentInfoDTO>();
        LogicalDate excutionDate = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease);
        for (AutopayAgreement pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(lease)) {
            PaymentInfoDTO pi = EntityFactory.create(PaymentInfoDTO.class);

            pi.amount().setValue(BigDecimal.ZERO);
            for (AutopayAgreementCoveredItem ci : pap.coveredItems()) {
                pi.amount().setValue(pi.amount().getValue().add(ci.amount().getValue()));
            }

            pi.paymentDate().setValue(excutionDate);
            pi.payer().set(pap.tenant());
            Persistence.ensureRetrieve(pi.payer(), AttachLevel.ToStringMembers);
            if (pi.payer().equals(TenantAppContext.getCurrentUserTenant())) {
                pi.paymentMethod().set(pap.paymentMethod());
            }

            currentAutoPayments.add(pi);
        }

        return currentAutoPayments;
    }

}
