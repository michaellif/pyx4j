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
package com.propertyvista.portal.web.client.activity.residents.payment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentListService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentsView;

public class PreauthorizedPaymentsActivity extends SecurityAwareActivity implements PreauthorizedPaymentsView.Presenter {

    private final PreauthorizedPaymentsView view;

    private final PreauthorizedPaymentListService srv;

    public PreauthorizedPaymentsActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(PreauthorizedPaymentsView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PreauthorizedPaymentListService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.getData(new DefaultAsyncCallback<PreauthorizedPaymentListDTO>() {
            @Override
            public void onSuccess(PreauthorizedPaymentListDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewPaymentMethod(PreauthorizedPaymentListDTO.ListItemDTO preauthorizedPayment) {
        AppPlace place = new PortalSiteMap.Resident.PaymentMethods.ViewPaymentMethod();
        AppSite.getPlaceController().goTo(place.formPlace(preauthorizedPayment.paymentMethod().id().getValue()));
    }

    @Override
    public void addPreauthorizedPayment() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
    }

    @Override
    public void deletePreauthorizedPayment(PreauthorizedPaymentListDTO.ListItemDTO preauthorizedPayment) {
        srv.delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, preauthorizedPayment.getPrimaryKey());
    }
}
