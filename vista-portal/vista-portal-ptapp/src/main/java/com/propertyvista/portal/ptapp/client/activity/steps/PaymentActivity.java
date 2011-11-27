/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView.PaymentPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;

public class PaymentActivity extends WizardStepActivity<PaymentInformation, PaymentPresenter> implements PaymentPresenter {

    public PaymentActivity(AppPlace place) {
        super((PaymentView) WizardStepsViewFactory.instance(PaymentView.class), PaymentInformation.class, (PaymentService) GWT.create(PaymentService.class));
        withPlace(place);
    }

    @Override
    public void onBillingAddressSameAsCurrentOne(boolean set) {
        final PaymentInformation currentValue = getView().getValue();
        if (set) {
            ((PaymentService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                @Override
                public void onSuccess(AddressStructured result) {
                    currentValue.paymentMethod().billingAddress().set(result);
                    getView().populate(currentValue);
                }
            });
        } else {
            currentValue.paymentMethod().billingAddress().set(EntityFactory.create(AddressStructured.class));
            getView().populate(currentValue);
        }
    }
}
