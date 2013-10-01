/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.financial.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.paymentmethod.PaymentMethodView;

public class PaymentMethodViewActivity extends SecurityAwareActivity implements PaymentMethodView.Presenter {

    private final PaymentMethodView view;

    private final Key entityId;

    public PaymentMethodViewActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(PaymentMethodView.class);
        this.view.setPresenter(this);

        this.entityId = place.getItemId();
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
}
