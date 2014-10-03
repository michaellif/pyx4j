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
package com.propertyvista.portal.resident.activity.financial.payment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentWizardView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Financial.Payment;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService;
import com.propertyvista.portal.rpc.portal.shared.dto.PaymentConvenienceFeeDTO;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class PaymentWizardActivity extends AbstractWizardCrudActivity<PaymentDTO, PaymentWizardView> implements PaymentWizardView.Presenter {

    private static final I18n i18n = I18n.get(PaymentWizardActivity.class);

    public PaymentWizardActivity(AppPlace place) {
        super(PaymentWizardView.class, GWT.<PaymentWizardService> create(PaymentWizardService.class), PaymentDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<InternationalAddress> callback) {
        ((PaymentWizardService) getService()).getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
            @Override
            public void onSuccess(InternationalAddress result) {
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
    public void getConvenienceFee(AsyncCallback<ConvenienceFeeCalculationResponseTO> callback, PaymentConvenienceFeeDTO inData) {
        ((PaymentWizardService) getService()).getConvenienceFee(callback, inData);
    }

    @Override
    protected void onFinish(final Key paymentRecordId) {
        // run deferred process for actual payment processing:  
        ((PaymentWizardService) getService()).processPayment(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Payment Processing..."), null, false) {
                    @Override
                    public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        hide(); // automatically hide dialog on completion...
                        AppSite.getPlaceController().goTo(new Payment.PaymentSubmitting(paymentRecordId));
                    }
                };
                d.getCancelButton().setVisible(false);
                d.show();
            }
        }, paymentRecordId);
    }
}
