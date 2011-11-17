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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.PaymentMethodsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.PaymentMethodDTO;
import com.propertyvista.portal.domain.dto.PaymentMethodListDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents.PaymentMethods;

public class PaymentMethodsActivity extends SecurityAwareActivity implements PaymentMethodsView.Presenter {

    private final PaymentMethodsView view;

    public PaymentMethodsActivity(Place place) {
        this.view = (PaymentMethodsView) PortalViewFactory.instance(PaymentMethodsView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        //TODO Implement a service call
        PaymentMethodListDTO paymentMethods = EntityFactory.create(PaymentMethodListDTO.class);

        PaymentMethodDTO paymentmethod = EntityFactory.create(PaymentMethodDTO.class);
        paymentmethod.id().setValue(new Key(1l));
        paymentmethod.type().setValue(PaymentType.Visa);
        paymentmethod.cardNumber().setValue("XXX 5566");
        paymentmethod.primary().setValue(true);
        paymentmethod.billingAddress().city().setValue("Toronto");
        paymentmethod.billingAddress().streetName().setValue("Clark Ave");
        paymentmethod.billingAddress().streetNumber().setValue("55");
        paymentmethod.billingAddress().postalCode().setValue("M4R9L3");
        paymentMethods.paymentMethods().add(paymentmethod);

        paymentmethod = EntityFactory.create(PaymentMethodDTO.class);
        paymentmethod.id().setValue(new Key(2l));
        paymentmethod.type().setValue(PaymentType.MasterCard);
        paymentmethod.cardNumber().setValue("XXX 1290");
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

    @Override
    public void editPaymentMethod(PaymentMethodDTO paymentmethod) {
        AppPlace place = new PaymentMethods.EditPaymentMethod();
        place.arg(PortalSiteMap.ARG_PAYMENT_METHOD_ID, paymentmethod.id().getValue().toString());
        AppSite.getPlaceController().goTo(place);

    }

    @Override
    public void addPaymentMethod() {
        AppSite.getPlaceController().goTo(new PaymentMethods.NewPaymentMethod());

    }

    @Override
    public void removePaymentMethod(PaymentMethodDTO paymentmethod) {
        // TODO Auto-generated method stub

    }

}
