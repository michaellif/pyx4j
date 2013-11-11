/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.financial.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.financial.dashboard.FinancialDashboardView;
import com.propertyvista.portal.resident.ui.financial.dashboard.FinancialDashboardView.FinancialDashboardPresenter;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class FinancialDashboardActivity extends SecurityAwareActivity implements FinancialDashboardPresenter {

    private final FinancialDashboardView view = ResidentPortalSite.getViewFactory().instantiate(FinancialDashboardView.class);

    private final BillingService billingService = GWT.<BillingService> create(BillingService.class);

    private final PaymentService paymentService = GWT.<PaymentService> create(PaymentService.class);

    public FinancialDashboardActivity(Place place) {
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        billingService.retreiveBillingSummary(new DefaultAsyncCallback<BillingSummaryDTO>() {
            @Override
            public void onSuccess(BillingSummaryDTO result) {
                view.populate(result);
            }
        });

        billingService.retreiveLatestActivities(new DefaultAsyncCallback<LatestActivitiesDTO>() {
            @Override
            public void onSuccess(LatestActivitiesDTO result) {
                view.populate(result);
            }
        });

        paymentService.getAutoPaySummary(new DefaultAsyncCallback<AutoPaySummaryDTO>() {
            @Override
            public void onSuccess(AutoPaySummaryDTO result) {
                view.populate(result);
            }
        });

        paymentService.getPaymentMethodSummary(new DefaultAsyncCallback<PaymentMethodSummaryDTO>() {
            @Override
            public void onSuccess(PaymentMethodSummaryDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewCurrentBill() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.BillingHistory.BillView());
    }

    @Override
    public void viewBillilngHistory() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.BillingHistory());
    }

    @Override
    public void viewTransactionHistory() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.TransactionHistory());
    }

    @Override
    public void makePayment() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.Payment.PayNow());
        }
    }

    @Override
    public void addAutoPay() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
        }
    }

    @Override
    public void deletePreauthorizedPayment(AutoPayInfoDTO autoPay) {
        paymentService.deleteAutoPay(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // TODO Auto-generated method stub
            }
        }, EntityFactory.createIdentityStub(AutopayAgreement.class, autoPay.getPrimaryKey()));
    }

    @Override
    public void viewPreauthorizedPayment(AutoPayInfoDTO autoPay) {
        AppSite.getPlaceController().goTo(
                new ResidentPortalSiteMap.Financial.PreauthorizedPayments.PreauthorizedPayment().formViewerPlace(autoPay.id().getValue()));
    }

    @Override
    public void addPaymentMethod() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PaymentMethods.NewPaymentMethod());
        }
    }

    @Override
    public void deletePaymentMethod(PaymentMethodInfoDTO paymentMethod) {
        paymentService.deletePaymentMethod(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // TODO Auto-generated method stub
            }
        }, EntityFactory.createIdentityStub(LeasePaymentMethod.class, paymentMethod.getPrimaryKey()));
    }

    @Override
    public void viewPaymentMethod(PaymentMethodInfoDTO paymentMethod) {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PaymentMethods.PaymentMethod().formViewerPlace(paymentMethod.id().getValue()));
    }
}
