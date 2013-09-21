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
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureAgreementService;
import com.propertyvista.portal.web.client.activity.AbstractWizardActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSureOrderWizardView;

public class TenantSureOrderWizardActivity extends AbstractWizardActivity<TenantSureAgreementDTO> implements TenantSureOrderWizardView.TenantSureOrderWizardPersenter {

    public TenantSureOrderWizardActivity(AppPlace place) {
        super(TenantSureOrderWizardView.class, GWT.<TenantSureAgreementService> create(TenantSureAgreementService.class), TenantSureAgreementDTO.class);
    }

    @Override
    protected void onSaved(Key result) {
        AppSite.getPlaceController().goTo(new Financial.PaymentSubmitting(result));
    }

    @Override
    public void sendQuoteDetailsEmail() {
        ((TenantSureAgreementService) getService()).sendQuoteDetails(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String email) {
                ((TenantSureOrderWizardView) getView()).onSendQuoteDetailsSucess(email);
            }
        }, getView().getValue().quote().quoteId().getValue());
    }

    @Override
    public void getNewQuote() {
        ((TenantSureAgreementService) getService()).getQuote(new DefaultAsyncCallback<TenantSureQuoteDTO>() {
            @Override
            public void onSuccess(TenantSureQuoteDTO quote) {
                ((TenantSureOrderWizardView) getView()).setQuote(quote);
            }
        }, getView().getValue().tenantSureCoverageRequest().<TenantSureCoverageDTO> duplicate());
    }

    @Override
    public void populateCurrentAddressAsBillingAddress() {
        ((TenantSureAgreementService) getService()).getCurrentTenantAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple billingAddress) {
                ((TenantSureOrderWizardView) getView()).setBillingAddress(billingAddress);
            }
        });
    }
}
