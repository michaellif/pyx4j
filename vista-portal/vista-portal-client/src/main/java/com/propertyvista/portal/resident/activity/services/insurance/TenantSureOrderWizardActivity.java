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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSureOrderWizardView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class TenantSureOrderWizardActivity extends AbstractWizardCrudActivity<TenantSureInsurancePolicyDTO, TenantSureOrderWizardView> implements
        TenantSureOrderWizardView.TenantSureOrderWizardPersenter {

    private final TenantSureCoverageDTO previousCoverageRequest = EntityFactory.create(TenantSureCoverageDTO.class);

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
                getView().acknowledgeSendQuoteDetailsSucess(email);
            }
        }, getView().getValue().quote().quoteId().getValue());
    }

    @Override
    public void getNewQuote() {
        TenantSureCoverageDTO coverageRequest = getView().getValue().tenantSureCoverageRequest().<TenantSureCoverageDTO> duplicate();

        // validate quote request data and avoid multiple quotation for the same data:
        if (isValidForQuote(coverageRequest) && !previousCoverageRequest.businessEquals(coverageRequest)) {
            previousCoverageRequest.set(coverageRequest);

            getView().waitForQuote();
            ((TenantSureInsurancePolicyCrudService) getService()).getQuote(new DefaultAsyncCallback<TenantSureQuoteDTO>() {
                @Override
                public void onSuccess(TenantSureQuoteDTO quote) {
                    getView().setQuote(quote);
                }
            }, coverageRequest);
        }
    }

    @Override
    public void populateCurrentAddressAsBillingAddress() {
        ((TenantSureInsurancePolicyCrudService) getService()).getCurrentTenantAddress(new DefaultAsyncCallback<InternationalAddress>() {
            @Override
            public void onSuccess(InternationalAddress billingAddress) {
                getView().setBillingAddress(billingAddress);
            }
        });
    }

    // checks the data validness for quote
    // look at @link TenantSureCoverageRequestForm.isReadyForQuote() also!
    private boolean isValidForQuote(TenantSureCoverageDTO coverageRequest) {//@formatter:off
        return !(coverageRequest.tenantName().isNull() 
                || coverageRequest.tenantPhone().isNull()
                || coverageRequest.personalLiabilityCoverage().isNull()
                || coverageRequest.contentsCoverage().isNull()
                || coverageRequest.deductible().isNull()
//                || coverageRequest.inceptionDate().isNull()
                || coverageRequest.numberOfPreviousClaims().isNull()
                || coverageRequest.smoker().isNull()
                || coverageRequest.paymentSchedule().isNull());
    }//@formatter:on
}
