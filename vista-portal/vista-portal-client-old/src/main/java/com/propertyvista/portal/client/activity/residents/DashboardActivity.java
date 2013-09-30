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
package com.propertyvista.portal.client.activity.residents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.resident.DashboardService;

public class DashboardActivity extends SecurityAwareActivity implements DashboardView.Presenter {

    private final DashboardView view;

    private final DashboardService srv;

    public DashboardActivity(Place place) {
        this.view = PortalSite.getViewFactory().instantiate(DashboardView.class);
        this.view.setPresenter(this);
        srv = GWT.create(DashboardService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieveMainDashboard(new DefaultAsyncCallback<MainDashboardDTO>() {
            @Override
            public void onSuccess(MainDashboardDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewCurrentBill() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.BillingHistory.BillView());
    }

    @Override
    public void payNow() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new Financial.Payment.PayNow());
        }
    }

    @Override
    public void edit(Key id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }
}
