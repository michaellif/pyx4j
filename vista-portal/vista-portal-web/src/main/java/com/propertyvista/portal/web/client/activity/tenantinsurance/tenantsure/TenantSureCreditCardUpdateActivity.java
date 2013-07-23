/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;

public class TenantSureCreditCardUpdateActivity extends SecurityAwareActivity implements TenantSureCreditCardUpdateView.Presenter {

    private final TenantSureCreditCardUpdateView view;

    private final TenantSureManagementService service;

    public TenantSureCreditCardUpdateActivity() {
        view = PortalWebSite.getViewFactory().instantiate(TenantSureCreditCardUpdateView.class);
        service = GWT.<TenantSureManagementService> create(TenantSureManagementService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        view.setPresenter(this);

        service.getPreAuthorizedPaymentsAgreement(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String agreementTextHtml) {
                InsurancePaymentMethod pm = EntityFactory.create(InsurancePaymentMethod.class);
                pm.type().setValue(PaymentType.CreditCard);

                view.populate(pm);
                view.setPreAuthorizedDebitAgreement(agreementTextHtml);
                panel.setWidget(view);
            }
        });

    }

    @Override
    public void save(InsurancePaymentMethod paymentMethod) {
        service.updatePaymentMethod(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                view.reportCCUpdateSuccess();
            }

        }, paymentMethod.duplicate(InsurancePaymentMethod.class));
    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void onTenantAddressRequested() {
        // TODO there should be some generic service to do this in Portal APP
        GWT.<TenantSurePurchaseService> create(TenantSurePurchaseService.class).getCurrentTenantAddress(new DefaultAsyncCallback<AddressStructured>() {

            @Override
            public void onSuccess(AddressStructured result) {
                view.setTenantAddress(result);
            }

        });
    }

    @Override
    public void onCCUpdateSuccessAcknowledged() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance.TenantSure.Management());
    }
}
