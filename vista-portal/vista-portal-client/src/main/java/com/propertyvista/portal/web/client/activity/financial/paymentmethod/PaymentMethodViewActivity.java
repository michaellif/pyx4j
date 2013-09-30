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
package com.propertyvista.portal.web.client.activity.financial.paymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.financial.paymentmethod.PaymentMethodView;

public class PaymentMethodViewActivity extends AbstractEditorActivity<LeasePaymentMethod> implements PaymentMethodView.Presenter {

    public PaymentMethodViewActivity(AppPlace place) {
        super(PaymentMethodView.class, GWT.<PaymentMethodCrudService> create(PaymentMethodCrudService.class), place);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((PaymentMethodCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }
}
