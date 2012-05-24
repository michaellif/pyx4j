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
package com.propertyvista.portal.client.activity.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;

public class PaymentMethodsActivity extends SecurityAwareActivity implements PaymentMethodsView.Presenter {

    private final PaymentMethodsView view;

    private final TenantPaymentMethodCrudService srv;

    public PaymentMethodsActivity(Place place) {
        this.view = PortalViewFactory.instance(PaymentMethodsView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantPaymentMethodCrudService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.list(new DefaultAsyncCallback<EntitySearchResult<PaymentMethod>>() {
            @Override
            public void onSuccess(EntitySearchResult<PaymentMethod> result) {
                view.populate(result.getData());
            }
        }, new EntityListCriteria<PaymentMethod>(PaymentMethod.class));
    }

    @Override
    public void addPaymentMethod() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods.NewPaymentMethod());
    }

    @Override
    public void editPaymentMethod(PaymentMethod paymentMethod) {
        AppPlace place = new PortalSiteMap.Residents.PaymentMethods.EditPaymentMethod();
        place.placeArg(PortalSiteMap.ARG_ENTITY_ID, paymentMethod.id().getValue().toString());
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void savePaymentMethod(PaymentMethod paymentMethod) {
        srv.save(new DefaultAsyncCallback<PaymentMethod>() {
            @Override
            public void onSuccess(PaymentMethod result) {
            }
        }, paymentMethod);
    }

    @Override
    public void removePaymentMethod(PaymentMethod paymentMethod) {
        srv.delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, paymentMethod.getPrimaryKey());
    }
}
