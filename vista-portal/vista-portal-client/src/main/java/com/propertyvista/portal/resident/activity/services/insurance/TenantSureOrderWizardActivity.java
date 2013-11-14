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
package com.propertyvista.portal.resident.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSureOrderWizardView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardFormCrudActivity;

public class TenantSureOrderWizardActivity extends AbstractWizardFormCrudActivity<TenantSureInsurancePolicyDTO> implements
        TenantSureOrderWizardView.TenantSureOrderWizardPersenter {

    public TenantSureOrderWizardActivity(AppPlace place) {
        super(TenantSureOrderWizardView.class, GWT.<TenantSureInsurancePolicyCrudService> create(TenantSureInsurancePolicyCrudService.class),
                TenantSureInsurancePolicyDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSureWizardConfirmation());
    }

    @Override
    public void sendQuoteDetailsEmail() {
        ((TenantSureInsurancePolicyCrudService) getService()).sendQuoteDetails(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String email) {
                ((TenantSureOrderWizardView) getView()).acknowledgeSendQuoteDetailsSucess(email);
            }
        }, getView().getValue().quote().quoteId().getValue());
    }

    @Override
    public void getNewQuote() {
        ((TenantSureOrderWizardView) getView()).waitForQuote();
        ((TenantSureInsurancePolicyCrudService) getService()).getQuote(new DefaultAsyncCallback<TenantSureQuoteDTO>() {
            @Override
            public void onSuccess(TenantSureQuoteDTO quote) {
                ((TenantSureOrderWizardView) getView()).setQuote(quote);
            }
        }, getView().getValue().tenantSureCoverageRequest().<TenantSureCoverageDTO> duplicate());
    }

    @Override
    public void populateCurrentAddressAsBillingAddress() {
        ((TenantSureInsurancePolicyCrudService) getService()).getCurrentTenantAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple billingAddress) {
                ((TenantSureOrderWizardView) getView()).setBillingAddress(billingAddress);
            }
        });
    }

}
