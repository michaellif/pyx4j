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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentServicesDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.portal.web.services.mock.DashboardServiceMockImpl;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.shared.config.VistaFeatures;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public void retrieveMainDashboard(AsyncCallback<MainDashboardDTO> callback) {
        if (false) {
            new DashboardServiceMockImpl().retrieveMainDashboard(callback);
        } else {
            MainDashboardDTO dashboard = EntityFactory.create(MainDashboardDTO.class);

            LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenantInLease.leaseTermV());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().floorplan());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());

            dashboard.profileInfo().tenantName().setValue(tenantInLease.leaseParticipant().customer().person().name().getStringView());
            dashboard.profileInfo().floorplanName().set(tenantInLease.leaseTermV().holder().lease().unit().floorplan().marketingName());
            dashboard.profileInfo().tenantAddress().setValue(AddressRetriever.getLeaseParticipantCurrentAddress(tenantInLease).getStringView());

            // fill stuff for the new web portal
            dashboard.billingSummary().currentBalance()
                    .setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(TenantAppContext.getCurrentUserLease().billingAccount()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenantInLease.leaseTermV().holder().lease());
                dashboard.billingSummary().dueDate().setValue(bill.dueDate().getValue());
            }

            dashboard.insuranceStatus().set(
                    ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                            TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));

            callback.onSuccess(dashboard);
        }
    }

    @Override
    public void retrieveFinancialDashboard(AsyncCallback<FinancialDashboardDTO> callback) {
        if (false) {
            new DashboardServiceMockImpl().retrieveFinancialDashboard(callback);
        } else {
            FinancialDashboardDTO dashboard = EntityFactory.create(FinancialDashboardDTO.class);

            Lease lease = TenantAppContext.getCurrentUserLease();

            dashboard.billingSummary().set(createBillingSummary());
            dashboard.autoPaySummary().set(createAutoPaySummary());

            dashboard.latestActivities().addAll(ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));

            if (VistaFeatures.instance().yardiIntegration()) {
                dashboard.transactionsHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(lease.billingAccount()));
            } else {
                dashboard.billingHistory().addAll(retrieveBillHistory());
            }

            callback.onSuccess(dashboard);
        }
    }

    @Override
    public void retrieveServicesDashboard(AsyncCallback<ResidentServicesDashboardDTO> callback) {
        if (true) {
            new DashboardServiceMockImpl().retrieveServicesDashboard(callback);
        } else {
            ResidentServicesDashboardDTO dto = EntityFactory.create(ResidentServicesDashboardDTO.class);
            dto.insuranceStatus().set(
                    ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                            TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));
        }

    }

    // Internals:

    private static BillingSummaryDTO createBillingSummary() {
        BillingSummaryDTO summary = EntityFactory.create(BillingSummaryDTO.class);

        summary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(TenantAppContext.getCurrentUserLease().billingAccount()));
        if (!VistaFeatures.instance().yardiIntegration()) {
            LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenantInLease.leaseTermV());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());

            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenantInLease.leaseTermV().holder().lease());
            summary.dueDate().setValue(bill.dueDate().getValue());
        }

        return summary;
    }

    private static AutoPaySummaryDTO createAutoPaySummary() {
        AutoPaySummaryDTO summary = EntityFactory.create(AutoPaySummaryDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        summary.currentAutoPayments().addAll(retrieveCurrentAutoPayments(lease));
        summary.currentAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease));
        summary.nextAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(lease));
        summary.modificationsAllowed().setValue(!ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(lease));

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

    private static List<BillDataDTO> retrieveBillHistory() {
        List<BillDataDTO> bills = new ArrayList<BillDataDTO>();
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
            dto.fromDate().setValue(bill.executionDate().getValue());

            bills.add(dto);
        }
        return bills;
    }

    // Gadgets utilities:

    @Override
    public void deletePreauthorizedPayment(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment itemId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(itemId);
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
