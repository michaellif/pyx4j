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
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;

public class TenantSureCreditCardUpdateActivity extends SecurityAwareActivity implements TenantSureCreditCardUpdateView.Presenter {

    private final TenantSureCreditCardUpdateView view;

    private final TenantSureManagementService service;

    public TenantSureCreditCardUpdateActivity() {
        view = PortalViewFactory.instance(TenantSureCreditCardUpdateView.class);
        service = GWT.<TenantSureManagementService> create(TenantSureManagementService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        view.setPresenter(this);

        LeasePaymentMethod pm = EntityFactory.create(LeasePaymentMethod.class);
        pm.type().setValue(PaymentType.CreditCard);
        view.populate(pm);

        panel.setWidget(view);
    }

    @Override
    public void save(LeasePaymentMethod paymentMethod) {
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
        // TODO add tenant address fetching routine
    }

    @Override
    public void onCCUpdateSuccessAcknowledged() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.TenantInsurance.TenantSure.Management());
    }
}
