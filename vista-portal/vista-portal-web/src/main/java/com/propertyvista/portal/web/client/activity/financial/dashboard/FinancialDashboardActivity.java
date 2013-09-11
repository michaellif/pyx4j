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
package com.propertyvista.portal.web.client.activity.financial.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.dashboard.FinancialDashboardView;
import com.propertyvista.portal.web.client.ui.financial.dashboard.FinancialDashboardView.FinancialDashboardPresenter;

public class FinancialDashboardActivity extends SecurityAwareActivity implements FinancialDashboardPresenter {

    private final FinancialDashboardView view;

    private final DashboardService srv;

    public FinancialDashboardActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(FinancialDashboardView.class);
        this.view.setPresenter(this);
        srv = GWT.create(DashboardService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        srv.retrieveFinancialDashboard(new DefaultAsyncCallback<FinancialDashboardDTO>() {
            @Override
            public void onSuccess(FinancialDashboardDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewCurrentBill() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.BillingHistory.ViewBill());
    }

    @Override
    public void viewBillingHistory() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.BillingHistory());

    }

    @Override
    public void payNow() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new Financial.PayNow());
        }
    }

    @Override
    public void addAutoPay() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
        }
    }

    @Override
    public void deletePreauthorizedPayment(AutoPayInfoDTO autoPay) {
        srv.deletePreauthorizedPayment(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO Auto-generated method stub
            }
        }, EntityFactory.createIdentityStub(PreauthorizedPayment.class, autoPay.getPrimaryKey()));
    }

    @Override
    public void viewPreauthorizedPayment(AutoPayInfoDTO autoPay) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPaymentMethod() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.PaymentMethods.NewPaymentMethod());
        }
    }

    @Override
    public void deletePaymentMethod(PaymentMethodInfoDTO paymentMethod) {
        srv.deletePaymentMethod(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO Auto-generated method stub
            }
        }, EntityFactory.createIdentityStub(LeasePaymentMethod.class, paymentMethod.getPrimaryKey()));
    }

    @Override
    public void viewPaymentMethod(PaymentMethodInfoDTO paymentMethod) {
        AppPlace place = new PortalSiteMap.Resident.PaymentMethods.ViewPaymentMethod();
        AppSite.getPlaceController().goTo(place.formPlace(paymentMethod.id().getValue()));
    }
}
