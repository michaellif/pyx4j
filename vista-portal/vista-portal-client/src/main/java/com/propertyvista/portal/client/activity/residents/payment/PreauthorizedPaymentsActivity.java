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
package com.propertyvista.portal.client.activity.residents.payment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentListService;

public class PreauthorizedPaymentsActivity extends SecurityAwareActivity implements PreauthorizedPaymentsView.Presenter {

    private final PreauthorizedPaymentsView view;

    private final PreauthorizedPaymentListService srv;

    public PreauthorizedPaymentsActivity(Place place) {
        this.view = ResidentsViewFactory.instance(PreauthorizedPaymentsView.class);
        this.view.setPresenter(this);
        srv = GWT.create(PreauthorizedPaymentListService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.list(new DefaultAsyncCallback<EntitySearchResult<PreauthorizedPayment>>() {
            @Override
            public void onSuccess(EntitySearchResult<PreauthorizedPayment> result) {
                view.populate(result.getData());
            }
        }, new EntityListCriteria<PreauthorizedPayment>(PreauthorizedPayment.class));
    }

    @Override
    public void viewPaymentMethod(LeasePaymentMethod paymentMethod) {
        AppPlace place = new PortalSiteMap.Residents.PaymentMethods.EditPaymentMethod();
        AppSite.getPlaceController().goTo(place.formPlace(paymentMethod.id().getValue()));
    }

    @Override
    public void addPreauthorizedPayment() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Financial.AutoPay.NewPreauthorizedPayment());
    }

    @Override
    public void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPayment) {
        srv.delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, preauthorizedPayment.getPrimaryKey());
    }
}
