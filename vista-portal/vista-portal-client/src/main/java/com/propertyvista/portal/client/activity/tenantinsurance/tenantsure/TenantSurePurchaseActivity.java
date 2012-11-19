/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePersonalDisclaimerHolderDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public class TenantSurePurchaseActivity extends AbstractActivity implements TenantSurePurchaseView.Presenter {

    private final TenantSurePurchaseView view;

    private final TenantSurePurchaseService service;

    public TenantSurePurchaseActivity() {
        view = PortalViewFactory.instance(TenantSurePurchaseView.class);
        service = GWT.<TenantSurePurchaseService> create(TenantSurePurchaseService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(TenantSurePurchaseActivity.this);
        service.getQuotationRequestParams(new DefaultAsyncCallback<TenantSureQuotationRequestParamsDTO>() {
            @Override
            public void onSuccess(TenantSureQuotationRequestParamsDTO quotationRequestParams) {
                PaymentMethod paymentMethod = EntityFactory.create(PaymentMethod.class);
                paymentMethod.type().setValue(PaymentType.CreditCard);

                TenantSurePersonalDisclaimerHolderDTO disclaimerHolder = EntityFactory.create(TenantSurePersonalDisclaimerHolderDTO.class);
                disclaimerHolder.set(quotationRequestParams.personalDisclaimerTerms().get(0).duplicate(TenantSurePersonalDisclaimerHolderDTO.class));

                view.init(disclaimerHolder, quotationRequestParams, paymentMethod);

                panel.setWidget(view);
            }
        });

    }

    @Override
    public void onCoverageRequestChanged() {
        view.waitForQuote();
        service.getQuote(new DefaultAsyncCallback<TenantSureQuoteDTO>() {
            @Override
            public void onSuccess(TenantSureQuoteDTO quote) {
                view.setQuote(quote);
            }
        }, view.getCoverageRequest());
    }

    @Override
    public void onQuoteAccepted() {
        view.waitForPaymentProcessing();
        service.acceptQuote(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO go to paymnet insurance processed screen
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.populatePaymentProcessingError(caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, view.getAcceptedQuote(), view.getPaymentMethod());
    }

    @Override
    public void onBillingAddressSameAsCurrentSelected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void onPaymentProcessingSuccessAccepted() {
        // TODO Auto-generated method stub

    }

}
