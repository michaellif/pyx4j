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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentView.PaymentPresenter;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;
import com.propertyvista.portal.rpc.ptapp.dto.PaymentInformationDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.PaymentService;

public class PaymentActivity extends WizardStepActivity<PaymentInformationDTO, PaymentPresenter> implements PaymentPresenter {

    public PaymentActivity(AppPlace place) {
        super(WizardStepsViewFactory.instance(PaymentView.class), PaymentInformationDTO.class, (PaymentService) GWT.create(PaymentService.class));
        withPlace(place);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        final PaymentInformationDTO currentValue = getView().getValue();
        ((PaymentService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }
}
