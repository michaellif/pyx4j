/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.financial.payment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.domain.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial.Payment;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentWizardService;
import com.propertyvista.portal.web.client.activity.AbstractWizardActivity;
import com.propertyvista.portal.web.client.ui.financial.payment.PaymentWizardView;

public class PaymentWizardActivity extends AbstractWizardActivity<PaymentDTO> implements PaymentWizardView.Persenter {

    public PaymentWizardActivity(AppPlace place) {
        super(PaymentWizardView.class, GWT.<PaymentWizardService> create(PaymentWizardService.class), PaymentDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((PaymentWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback) {
        ((PaymentWizardService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        AppSite.getPlaceController().goTo(new Payment.PaymentSubmitting(result));
    }
}
