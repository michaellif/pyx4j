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
package com.propertyvista.portal.client.activity.residents.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;

public class PaymentMethodsActivity extends SecurityAwareActivity implements PaymentMethodsView.Presenter {

    private final PaymentMethodsView view;

    private final PaymentMethodCrudService srv;

    public PaymentMethodsActivity(Place place) {
        this.view = PortalSite.getViewFactory().instantiate(PaymentMethodsView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PaymentMethodCrudService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.list(new DefaultAsyncCallback<EntitySearchResult<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(EntitySearchResult<LeasePaymentMethod> result) {
                view.populate(result.getData());
            }
        }, new EntityListCriteria<LeasePaymentMethod>(LeasePaymentMethod.class));
    }

    @Override
    public void addPaymentMethod() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.PaymentMethods.NewPaymentMethod());
    }

    @Override
    public void viewPaymentMethod(LeasePaymentMethod paymentMethod) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.PaymentMethods.ViewPaymentMethod().formPlace(paymentMethod.id().getValue()));
    }

    @Override
    public void deletePaymentMethod(LeasePaymentMethod paymentMethod) {
        srv.delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, paymentMethod.getPrimaryKey());
    }
}
