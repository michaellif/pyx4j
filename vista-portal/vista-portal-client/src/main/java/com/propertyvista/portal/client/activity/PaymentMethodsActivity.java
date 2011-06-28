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
package com.propertyvista.portal.client.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.client.ui.residents.PaymentMethodsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;
import com.propertyvista.portal.domain.payment.PaymentType;

public class PaymentMethodsActivity extends SecurityAwareActivity {

    private final PaymentMethodsView view;

    public PaymentMethodsActivity(Place place) {
        this.view = (PaymentMethodsView) PortalViewFactory.instance(PaymentMethodsView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        //TODO Implement a service call
        PaymentMethodListDTO paymentMethods = EntityFactory.create(PaymentMethodListDTO.class);

        PaymentMethodDTO paymentmethod = EntityFactory.create(PaymentMethodDTO.class);
        paymentmethod.type().setValue(PaymentType.Visa);
        paymentmethod.primary().setValue(true);
        paymentmethod.billingAddress().city().setValue("Toronto");
        paymentmethod.billingAddress().streetName().setValue("Clark Ave");
        paymentmethod.billingAddress().streetNumber().setValue("55");
        paymentmethod.billingAddress().postalCode().setValue("M4R9L3");
        paymentMethods.paymentMethods().add(paymentmethod);

        paymentmethod = EntityFactory.create(PaymentMethodDTO.class);
        paymentmethod.type().setValue(PaymentType.MasterCard);
        paymentmethod.primary().setValue(false);
        paymentmethod.billingAddress().city().setValue("Richmond Hill");
        paymentmethod.billingAddress().streetName().setValue("Some Street");
        paymentmethod.billingAddress().streetNumber().setValue("3155");
        paymentmethod.billingAddress().postalCode().setValue("L7U9O8");
        paymentMethods.paymentMethods().add(paymentmethod);
        view.populate(paymentMethods);
    }

    public PaymentMethodsActivity withPlace(Place place) {
        return this;
    }

}
