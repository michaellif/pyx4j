/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.paymentmethod;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class NewPaymentMethodActivity extends EditPaymentMethodActivity {

    public NewPaymentMethodActivity(Place place) {
        super(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        securityAwareStart(panel, eventBus);
        panel.setWidget(view);

        // create default empty method:
        PaymentMethod method = EntityFactory.create(PaymentMethod.class);
        method.type().setValue(PaymentType.Echeck);
        view.populate(method);
    }

    @Override
    public void save(PaymentMethod paymentmethod) {
        srv.create(new DefaultAsyncCallback<PaymentMethod>() {
            @Override
            public void onSuccess(PaymentMethod result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());
            }
        }, paymentmethod);
    }

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());
    }
}
