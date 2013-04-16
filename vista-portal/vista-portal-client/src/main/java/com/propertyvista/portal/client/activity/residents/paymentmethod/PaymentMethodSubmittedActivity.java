/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodSubmittedView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodSubmittedService;

public class PaymentMethodSubmittedActivity extends SecurityAwareActivity implements PaymentMethodSubmittedView.Presenter {

    private final PaymentMethodSubmittedView view;

    protected final PaymentMethodSubmittedService srv;

    private final Key entityId;

    public PaymentMethodSubmittedActivity(AppPlace place) {
        this.view = ResidentsViewFactory.instance(PaymentMethodSubmittedView.class);
        this.view.setPresenter(this);

        srv = GWT.create(PaymentMethodSubmittedService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<PaymentMethodDTO>() {
            @Override
            public void onSuccess(PaymentMethodDTO result) {
                view.populate(result);
            }
        }, entityId);
    }

    @Override
    public void save(PaymentMethodDTO entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

    @Override
    public void goToAutoPay() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Financial.AutoPay.NewPreauthorizedPayment());
    }
}
