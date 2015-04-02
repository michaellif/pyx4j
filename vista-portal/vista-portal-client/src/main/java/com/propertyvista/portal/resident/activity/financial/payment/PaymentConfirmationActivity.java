/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 */
package com.propertyvista.portal.resident.activity.financial.payment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;

import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentConfirmationView;
import com.propertyvista.portal.resident.ui.financial.payment.PaymentConfirmationView.PaymentConfirmationPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class PaymentConfirmationActivity extends SecurityAwareActivity implements PaymentConfirmationPresenter {

    private static final I18n i18n = I18n.get(PaymentConfirmationActivity.class);

    private final PaymentConfirmationView view;

    private Timer progressTimer;

    public PaymentConfirmationActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().getView(PaymentConfirmationView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        if (ClientContext.visit(ResidentUserVisit.class).getPaymentDeferredCorrelationId() != null) {
            paymentProcessMonotoring();
        } else {
            populatePaymentData();
        }
    }

    @Override
    public void goToAutoPay() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PreauthorizedPayments.AutoPayWizard());
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub
    }

    private void populatePaymentData() {
        assert (ClientContext.visit(ResidentUserVisit.class).getPaymentRecord() != null);
        GWT.<PaymentService> create(PaymentService.class).retrievePayment(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                view.populate(result);
            }
        }, ClientContext.visit(ResidentUserVisit.class).getPaymentRecord());
    }

    private void paymentProcessMonotoring() {
        if (progressTimer != null) {
            progressTimer.cancel();
        }
        progressTimer = new Timer() {
            @Override
            public void run() {
                GWT.<DeferredProcessService> create(DeferredProcessService.class).getStatus(new AsyncCallback<DeferredProcessProgressResponse>() {
                    @Override
                    public void onSuccess(DeferredProcessProgressResponse progress) {
                        if (progress.isError()) {
                            onPaymentProcessingFinish();
                            view.displayError(progress.getMessage());
                        } else if (progress.isCompleted()) {
                            onPaymentProcessingFinish();
                            populatePaymentData();
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        onPaymentProcessingFinish();
                        view.displayError(caught.getMessage());
                    }
                }, ClientContext.visit(ResidentUserVisit.class).getPaymentDeferredCorrelationId(), false);
            }
        };
        progressTimer.scheduleRepeating(1000);
        GlassPanel.show(GlassStyle.SemiTransparent, i18n.tr("Please Wait for Payment Processing..."));
    }

    private void onPaymentProcessingFinish() {
        progressTimer.cancel();
        GlassPanel.hide();
        ClientContext.visit(ResidentUserVisit.class).setPaymentDeferredCorrelationId(null);
    }
}