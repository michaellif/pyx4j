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
package com.propertyvista.portal.client.activity.residents;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.payment.PaymentView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentCrudService;

public class PaymentActivity extends SecurityAwareActivity implements PaymentView.Presenter {

    protected final PaymentView view;

    protected final PaymentCrudService srv;

    private Key entityId;

    public PaymentActivity(Place place) {
        this.view = PortalViewFactory.instance(PaymentView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PaymentCrudService.class);

        String val;
        assert (place instanceof AppPlace);
        if ((val = ((AppPlace) place).getFirstArg(PortalSiteMap.ARG_ENTITY_ID)) != null) {
            entityId = new Key(val);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        securityAwareStart(panel, eventBus);
        panel.setWidget(view);

        srv.initNew(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                view.populate(result);
            }
        });
    }

    protected final void securityAwareStart(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
    }

    @Override
    public void save(PaymentRecordDTO paymentmethod) {
        srv.save(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());
            }
        }, paymentmethod);
    }

    @Override
    public void cancel() {
        History.back();
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

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<PaymentMethod>> callback) {
        srv.getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<PaymentMethod>>() {
            @Override
            public void onSuccess(Vector<PaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }
}
