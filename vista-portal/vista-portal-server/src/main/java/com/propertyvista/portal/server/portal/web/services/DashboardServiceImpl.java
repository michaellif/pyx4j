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

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.PaymentInfoDTO;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.shared.config.VistaFeatures;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public void retrieveMainDashboard(AsyncCallback<MainDashboardDTO> callback) {
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
        FinancialSummaryDTO billingSummary = retrieve();
        dashboard.billingSummary().currentBalance().setValue(billingSummary.currentBalance().getValue());
        if (!VistaFeatures.instance().yardiIntegration()) {
            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenantInLease.leaseTermV().holder().lease());
            dashboard.billingSummary().dueDate().setValue(bill.dueDate().getValue());
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            dashboard
                    .residentServicesInfo()
                    .tenantInsuranceStatus()
                    .set(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                            TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));

        }

        callback.onSuccess(dashboard);
    }

    @Override
    public void retrieveFinancialDashboard(AsyncCallback<FinancialDashboardDTO> callback) {
        FinancialDashboardDTO dashboard = EntityFactory.create(FinancialDashboardDTO.class);

        FinancialSummaryDTO billingSummary = retrieve();
        dashboard.billingSummary().currentBalance().setValue(billingSummary.currentBalance().getValue());
        if (!VistaFeatures.instance().yardiIntegration()) {
            LeaseTermTenant tenantInLease = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenantInLease.leaseTermV());
            Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());

            Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenantInLease.leaseTermV().holder().lease());
            dashboard.billingSummary().dueDate().setValue(bill.dueDate().getValue());
        }

        callback.onSuccess(dashboard);
    }

    private static FinancialSummaryDTO retrieve() {
        FinancialSummaryDTO financialSummary = VistaFeatures.instance().yardiIntegration() ? EntityFactory.create(YardiFinancialSummaryDTO.class)
                : EntityFactory.create(PvBillingFinancialSummaryDTO.class);

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserTenant().lease().getPrimaryKey());

        financialSummary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        financialSummary.currentAutoPayments().addAll(retrieveCurrentAutoPayments(lease));

        // TODO has to stay here until billing facade and AR facade merged together
        if (financialSummary.isInstanceOf(YardiFinancialSummaryDTO.class)) {
            ((YardiFinancialSummaryDTO) financialSummary).transactionsHistory().set(
                    ServerSideFactory.create(ARFacade.class).getTransactionHistory(lease.billingAccount()));
            ((YardiFinancialSummaryDTO) financialSummary).latestActivities().addAll(
                    ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));
        } else if (financialSummary.isInstanceOf(PvBillingFinancialSummaryDTO.class)) {
            ((PvBillingFinancialSummaryDTO) financialSummary).currentBill().set(ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(lease));
            ((PvBillingFinancialSummaryDTO) financialSummary).latestActivities().addAll(
                    ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));
        }

        return financialSummary;

    }

    private static List<PaymentInfoDTO> retrieveCurrentAutoPayments(Lease lease) {
        List<PaymentInfoDTO> currentAutoPayments = new ArrayList<PaymentInfoDTO>();
        LogicalDate excutionDate = ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease);
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveCurrentPreauthorizedPayments(lease)) {
            PaymentInfoDTO paymentInfo = EntityFactory.create(PaymentInfoDTO.class);

            paymentInfo.amount().setValue(BigDecimal.ZERO);
            for (PreauthorizedPaymentCoveredItem ci : pap.coveredItems()) {
                paymentInfo.amount().setValue(paymentInfo.amount().getValue().add(ci.amount().getValue()));
            }

            paymentInfo.paymentDate().setValue(excutionDate);
            paymentInfo.payer().set(pap.tenant());
            Persistence.ensureRetrieve(paymentInfo.payer(), AttachLevel.ToStringMembers);
            if (paymentInfo.payer().equals(TenantAppContext.getCurrentUserTenant())) {
                paymentInfo.paymentMethod().set(pap.paymentMethod());
            }

            currentAutoPayments.add(paymentInfo);
        }

        return currentAutoPayments;
    }
}
