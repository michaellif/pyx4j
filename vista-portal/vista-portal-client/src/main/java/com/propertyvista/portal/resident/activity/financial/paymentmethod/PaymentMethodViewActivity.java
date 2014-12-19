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
 */
package com.propertyvista.portal.resident.activity.financial.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.portal.resident.ui.financial.paymentmethod.PaymentMethodView;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentMethodWizardService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class PaymentMethodViewActivity extends AbstractEditorActivity<PaymentMethodDTO> implements PaymentMethodView.Presenter {

    public PaymentMethodViewActivity(AppPlace place) {
        super(PaymentMethodView.class, GWT.<PaymentMethodWizardService> create(PaymentMethodWizardService.class), place);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<InternationalAddress> callback) {
        ((PaymentMethodWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
            @Override
            public void onSuccess(InternationalAddress result) {
                callback.onSuccess(result);
            }
        });
    }
}
