/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.financial.autopay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.PreauthorizedPaymentListService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.autopay.AutoPayListView;

public class AutoPayListActivity extends SecurityAwareActivity implements AutoPayListView.Presenter {

    private final AutoPayListView view;

    private final PreauthorizedPaymentListService srv;

    public AutoPayListActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(AutoPayListView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PreauthorizedPaymentListService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.getData(new DefaultAsyncCallback<AutoPaySummaryDTO>() {
            @Override
            public void onSuccess(AutoPaySummaryDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewPaymentMethod(AutoPayDTO preauthorizedPayment) {
        AppPlace place = new PortalSiteMap.Resident.PaymentMethods.ViewPaymentMethod();
        AppSite.getPlaceController().goTo(place.formPlace(preauthorizedPayment.paymentMethod().id().getValue()));
    }

    @Override
    public void addPreauthorizedPayment() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
    }

    @Override
    public void deletePreauthorizedPayment(AutoPayDTO preauthorizedPayment) {
        srv.delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, preauthorizedPayment.getPrimaryKey());
    }
}
