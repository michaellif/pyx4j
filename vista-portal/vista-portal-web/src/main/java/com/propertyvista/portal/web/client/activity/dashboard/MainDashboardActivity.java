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
package com.propertyvista.portal.web.client.activity.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.dashboard.MainDashboardView;

public class MainDashboardActivity extends SecurityAwareActivity implements MainDashboardView.DashboardPresenter {

    private final MainDashboardView view;

    private final DashboardService srv;

    public MainDashboardActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(MainDashboardView.class);
        this.view.setPresenter(this);
        srv = GWT.create(DashboardService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        srv.retrieveMainDashboard(new DefaultAsyncCallback<MainDashboardDTO>() {
            @Override
            public void onSuccess(MainDashboardDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewCurrentBill() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.BillingHistory.ViewBill());
    }

    @Override
    public void payNow() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new Financial.PayNow());
        }
    }

    @Override
    public void setAutopay() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments());
        }
    }

    @Override
    public void getTenantSure() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePurchase());
    }

}
