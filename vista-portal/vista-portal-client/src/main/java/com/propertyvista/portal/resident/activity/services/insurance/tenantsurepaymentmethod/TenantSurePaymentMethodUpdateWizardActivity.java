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
package com.propertyvista.portal.resident.activity.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodWizardView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class TenantSurePaymentMethodUpdateWizardActivity extends AbstractWizardCrudActivity<InsurancePaymentMethodDTO, TenantSurePaymentMethodWizardView>
        implements TenantSurePaymentMethodWizardView.Persenter {

    public TenantSurePaymentMethodUpdateWizardActivity(AppPlace place) {
        super(TenantSurePaymentMethodWizardView.class, GWT.<TenantSurePaymentMethodCrudService> create(TenantSurePaymentMethodCrudService.class),
                InsurancePaymentMethodDTO.class);
    }

    @Override
    public void getCurrentAddress() {
        ((TenantSurePaymentMethodCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                getView().setBillingAddress(result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCardConfirmation());
    }
}
