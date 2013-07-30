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
package com.propertyvista.portal.web.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureAlreadyPurchasedException;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureOnMaintenanceException;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;

public class TenantSurePurchaseActivity extends AbstractActivity implements TenantSurePurchaseView.Presenter {

    private static final I18n i18n = I18n.get(TenantSurePurchaseActivity.class);

    private final TenantSurePurchaseView view;

    private final TenantSurePurchaseService service;

    public TenantSurePurchaseActivity() {
        view = PortalWebSite.getViewFactory().instantiate(TenantSurePurchaseView.class);
        service = GWT.<TenantSurePurchaseService> create(TenantSurePurchaseService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(TenantSurePurchaseActivity.this);
        service.getQuotationRequestParams(new DefaultAsyncCallback<TenantSureQuotationRequestParamsDTO>() {
            @Override
            public void onSuccess(TenantSureQuotationRequestParamsDTO quotationRequestParams) {
                quotationRequestParams.defaultPaymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);

                InsurancePaymentMethod paymentMethod = EntityFactory.create(InsurancePaymentMethod.class);
                paymentMethod.type().setValue(PaymentType.CreditCard);

                view.init(quotationRequestParams, paymentMethod);

                panel.setWidget(view);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof TenantSureOnMaintenanceException) {
                    view.setTenantSureOnMaintenance(((TenantSureOnMaintenanceException) caught).getMessage());
                    panel.setWidget(view);
                } else if (caught instanceof TenantSureAlreadyPurchasedException) {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance.TenantSure.Management());
                } else if (caught instanceof UserRuntimeException) {
                    view.reportError(caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
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

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportError(caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, view.getCoverageRequest());
    }

    @Override
    public void onQuoteAccepted() {
        view.waitForPaymentProcessing();
        service.acceptQuote(new AsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                view.populatePaymentProcessingSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.populatePaymentProcessingError(caught.getMessage());
                } else {
                    view.reportError(i18n.tr("Payment failed due to: {0}", caught.getMessage()));
                }
            }
        }, view.getAcceptedQuote(), view.getCoverageRequest().tenantName().getValue(), view.getCoverageRequest().tenantPhone().getValue(),
                view.getPaymentMethod());
    }

    @Override
    public void onBillingAddressSameAsCurrentSelected() {
        service.getCurrentTenantAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple tenantsAddress) {
                view.setBillingAddress(tenantsAddress);
            }

            @Override
            public void onFailure(Throwable caught) {
                view.reportError(i18n.tr("Failed to retrieve billing address: {0}", caught.getMessage()));
            }
        });

    }

    @Override
    public void sendQuoteDetails(String quoteId) {
        service.sendQuoteDetails(new DefaultAsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }

            @Override
            public void onSuccess(String email) {
                view.populateSendQuoteDetailSuccess(email);
            }

        }, quoteId);

    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void onPaymentProcessingSuccessAccepted() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance());
    }

}
