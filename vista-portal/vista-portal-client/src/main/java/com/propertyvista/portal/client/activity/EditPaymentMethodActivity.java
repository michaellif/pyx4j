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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.residents.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class EditPaymentMethodActivity extends SecurityAwareActivity implements EditPaymentMethodView.Presenter {

    private final EditPaymentMethodView view;

    public EditPaymentMethodActivity(Place place) {
        this.view = (EditPaymentMethodView) PortalViewFactory.instance(EditPaymentMethodView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        PaymentMethodDTO paymentMethod = EntityFactory.create(PaymentMethodDTO.class);
        paymentMethod.cardNumber().setValue("XXXX XXXXX XXXX 7890");
        paymentMethod.nameOnAccount().setValue("Mahershalalhashbaz Alibaba");
        paymentMethod.type().setValue(PaymentType.Visa);

        view.populate(paymentMethod);

    }

    @Override
    public void save(PaymentMethodDTO paymentmethod) {
        // TODO Implement
        //Just for presentation
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());

    }

    @Override
    public void cancel() {
        // TODO Implement
        //Just for presentation
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.PaymentMethods());

    }

}
