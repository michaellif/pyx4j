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
package com.propertyvista.portal.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;

public class NewPaymentMethodActivity extends SecurityAwareActivity implements NewPaymentMethodView.Presenter {

    private final NewPaymentMethodView view;

    private final TenantPaymentMethodCrudService srv;

    public NewPaymentMethodActivity(Place place) {
        this.view = PortalViewFactory.instance(NewPaymentMethodView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantPaymentMethodCrudService.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
    }

    public NewPaymentMethodActivity withPlace(Place place) {
        return this;
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
    public void onBillingAddressSameAsCurrentOne(boolean set) {
        if (!set) {
            return;
        }
        srv.getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {

            @Override
            public void onSuccess(AddressStructured result) {
                final PaymentMethod currentValue = EntityFactory.create(PaymentMethod.class);
                currentValue.set(view.getValue());
                currentValue.billingAddress().set(result);
                currentValue.sameAsCurrent().setValue(true);
                view.populate(currentValue);

            }
        });
    }
}
