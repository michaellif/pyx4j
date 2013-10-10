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
package com.propertyvista.portal.web.client.activity.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.web.client.activity.AbstractWizardCrudActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodWizardView;

public class TenantSurePaymentMethodWizardActivity extends AbstractWizardCrudActivity<InsurancePaymentMethodDTO> implements
        TenantSurePaymentMethodWizardView.Persenter {

    public TenantSurePaymentMethodWizardActivity(AppPlace place) {
        super(TenantSurePaymentMethodWizardView.class, GWT.<TenantSurePaymentMethodCrudService> create(TenantSurePaymentMethodCrudService.class),
                InsurancePaymentMethodDTO.class);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((TenantSurePaymentMethodCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        getView().reset();
        AppSite.getPlaceController().goTo(new Financial.PaymentMethods.PaymentMethodSubmitted(result));
    }
}
