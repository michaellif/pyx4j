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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class NewPaymentMethodActivity extends SecurityAwareActivity implements NewPaymentMethodView.Presenter {

    private final NewPaymentMethodView view;

    public NewPaymentMethodActivity(Place place) {
        this.view = (NewPaymentMethodView) PortalViewFactory.instance(NewPaymentMethodView.class);
        this.view.setPresenter(this);
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
        // TODO Implement
        //Just for presentation
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());

    }

}
