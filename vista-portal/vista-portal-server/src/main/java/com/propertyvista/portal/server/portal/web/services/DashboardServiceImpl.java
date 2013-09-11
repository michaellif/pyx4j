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
package com.propertyvista.portal.server.portal.web.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.LeaseParticipantUtils;
import com.propertyvista.shared.config.VistaFeatures;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public void retrieveFinancialDashboard(AsyncCallback<FinancialDashboardDTO> callback) {

        FinancialDashboardDTO dashboard = EntityFactory.create(FinancialDashboardDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        dashboard.billingSummary().set(createBillingSummary(lease));
        dashboard.autoPaySummary().set(createAutoPaySummary(lease));
        dashboard.latestActivities().set(createLatestActivities(lease));

        if (VistaFeatures.instance().yardiIntegration()) {
            dashboard.transactionsHistory().set(createTransactionsHistory(lease));
        } else {
            dashboard.billingHistory().set(createBillingHistory(lease));
        }

        dashboard.paymentMethodSummary().set(createPaymentMethodSummary(lease));

        callback.onSuccess(dashboard);
    }

    // Internals:

    private static BillingSummaryDTO createBillingSummary(Lease lease) {
        BillingSummaryDTO summary = EntityFactory.create(BillingSummaryDTO.class);

        summary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (!VistaFeatures.instance().yardiIntegration()) {
            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);
            summary.dueDate().setValue(bill.dueDate().getValue());
        }

        return summary;
    }

    private static BillingHistoryDTO createBillingHistory(Lease lease) {
        BillingHistoryDTO history = EntityFactory.create(BillingHistoryDTO.class);

        history.bills().addAll(retrieveBillHistory(lease));

        return history;
    }

    private static TransactionHistoryDTO createTransactionsHistory(Lease lease) {
        return ServerSideFactory.create(ARFacade.class).getTransactionHistory(lease.billingAccount());
    }

    private static LatestActivitiesDTO createLatestActivities(Lease lease) {
        LatestActivitiesDTO history = EntityFactory.create(LatestActivitiesDTO.class);

        history.lineItems().addAll(ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));

        return history;
    }

    private static AutoPaySummaryDTO createAutoPaySummary(Lease lease) {
        AutoPaySummaryDTO summary = EntityFactory.create(AutoPaySummaryDTO.class);

        summary.currentAutoPayments().addAll(retrieveCurrentAutoPayments(lease));
        summary.currentAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease));
        summary.nextAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(lease));
        summary.modificationsAllowed().setValue(!ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(lease));

        return summary;
    }

    private IEntity createPaymentMethodSummary(Lease lease) {
        PaymentMethodSummaryDTO summary = EntityFactory.create(PaymentMethodSummaryDTO.class);

        summary.paymentMethods().addAll(retrievePaymentMethods(lease));

        return summary;
    }

    private static List<AutoPayInfoDTO> retrieveCurrentAutoPayments(Lease lease) {
        List<AutoPayInfoDTO> currentAutoPayments = new ArrayList<AutoPayInfoDTO>();
        LogicalDate excutionDate = ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease);
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveCurrentPreauthorizedPayments(lease)) {
            AutoPayInfoDTO autoPayInfo = EntityFactory.create(AutoPayInfoDTO.class);
            autoPayInfo.id().setValue(pap.id().getValue());

            autoPayInfo.amount().setValue(BigDecimal.ZERO);
            for (PreauthorizedPaymentCoveredItem ci : pap.coveredItems()) {
                autoPayInfo.amount().setValue(autoPayInfo.amount().getValue().add(ci.amount().getValue()));
            }

            autoPayInfo.paymentDate().setValue(excutionDate);
            autoPayInfo.payer().set(pap.tenant());
            Persistence.ensureRetrieve(autoPayInfo.payer(), AttachLevel.ToStringMembers);
            if (autoPayInfo.payer().equals(TenantAppContext.getCurrentUserTenant())) {
                autoPayInfo.paymentMethod().set(pap.paymentMethod());
            }
            autoPayInfo.expiring().setValue(pap.expiring().getValue());

            currentAutoPayments.add(autoPayInfo);
        }

        return currentAutoPayments;
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

    private static List<PaymentMethodInfoDTO> retrievePaymentMethods(Lease lease) {
        List<PaymentMethodInfoDTO> paymentMethods = new ArrayList<PaymentMethodInfoDTO>();

        for (LeasePaymentMethod pm : LeaseParticipantUtils.getProfiledPaymentMethods(TenantAppContext.getCurrentUserTenantInLease())) {
            PaymentMethodInfoDTO pmi = EntityFactory.create(PaymentMethodInfoDTO.class);

            pmi.id().setValue(pm.id().getValue());
            pmi.paymentMethod().set(pm);

            paymentMethods.add(pmi);
        }

        return paymentMethods;
    }

    // Gadgets utilities:

    @Override
    public void deletePreauthorizedPayment(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment itemId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(itemId);
        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void deletePaymentMethod(AsyncCallback<VoidSerializable> callback, LeasePaymentMethod itemId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(itemId);
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
