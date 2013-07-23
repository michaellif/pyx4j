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
package com.propertyvista.portal.web.client.activity.residents.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.ViewPaymentMethodView;

public class ViewPaymentMethodActivity extends SecurityAwareActivity implements ViewPaymentMethodView.Presenter {

    protected final ViewPaymentMethodView view;

    protected final PaymentMethodCrudService srv;

    private final Key entityId;

    public ViewPaymentMethodActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(ViewPaymentMethodView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PaymentMethodCrudService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<LeasePaymentMethod>() {
            @Override
            public void onSuccess(LeasePaymentMethod result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTarget.View);
    }

    @Override
    public void edit(Key id) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.PaymentMethods.EditPaymentMethod().formPlace(id));
    }

    @Override
    public void back() {
        History.back();
    }

    @Override
    public void save(LeasePaymentMethod value) {
        // TODO Auto-generated method stub

    }
}
