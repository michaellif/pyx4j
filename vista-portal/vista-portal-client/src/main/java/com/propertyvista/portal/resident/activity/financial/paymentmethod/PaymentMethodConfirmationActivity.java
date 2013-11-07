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
package com.propertyvista.portal.resident.activity.financial.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.activity.SecurityAwareActivity;
import com.propertyvista.portal.resident.ui.financial.paymentmethod.PaymentMethodConfirmationView;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService;

public class PaymentMethodConfirmationActivity extends SecurityAwareActivity implements PaymentMethodConfirmationView.PaymentMethodConfirmationPresenter {

    private final PaymentMethodConfirmationView view;

    private final Key entityId;

    public PaymentMethodConfirmationActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().instantiate(PaymentMethodConfirmationView.class);
        this.view.setPresenter(this);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        GWT.<PaymentService> create(PaymentService.class).retrievePaymentMethod(new DefaultAsyncCallback<PaymentMethodDTO>() {
            @Override
            public void onSuccess(PaymentMethodDTO result) {
                view.populate(result);
            }
        }, EntityFactory.createIdentityStub(LeasePaymentMethod.class, entityId));
    }

    @Override
    public void goToAutoPay() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }

}
