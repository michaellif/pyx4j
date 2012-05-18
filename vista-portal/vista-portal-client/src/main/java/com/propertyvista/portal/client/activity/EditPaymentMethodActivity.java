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
package com.propertyvista.portal.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;

public class EditPaymentMethodActivity extends SecurityAwareActivity implements EditPaymentMethodView.Presenter {

    private final EditPaymentMethodView view;

    private final TenantPaymentMethodCrudService srv;

    private Key entityId;

    public EditPaymentMethodActivity(Place place) {
        this.view = PortalViewFactory.instance(EditPaymentMethodView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantPaymentMethodCrudService.class);

        String val;
        assert (place instanceof AppPlace);
        if ((val = ((AppPlace) place).getFirstArg(PortalSiteMap.ARG_PAYMENT_METHOD_ID)) != null) {
            entityId = new Key(val);
        }

        assert (entityId != null);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<PaymentMethod>() {
            @Override
            public void onSuccess(PaymentMethod result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTraget.Edit);

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

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressStructured> callback) {
        srv.getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
            @Override
            public void onSuccess(AddressStructured result) {
                callback.onSuccess(result);
            }
        });
    }
}
