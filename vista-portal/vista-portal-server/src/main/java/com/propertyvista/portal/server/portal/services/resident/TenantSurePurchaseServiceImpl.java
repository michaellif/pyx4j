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
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureTextFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.ptapp.IAgree;
import com.propertyvista.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantSurePurchaseServiceImpl implements TenantSurePurchaseService {

    private static I18n i18n = I18n.get(TenantSurePurchaseServiceImpl.class);

    @Override
    public void getQuotationRequestParams(AsyncCallback<TenantSureQuotationRequestParamsDTO> callback) {
        TenantSureQuotationRequestParamsDTO params = EntityFactory.create(TenantSureQuotationRequestParamsDTO.class);

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                lease.unit().<AptUnit> createIdentityStub(), TenantInsurancePolicy.class);
        // these values are taken from the TenantSure API document: Appendix I        
        for (BigDecimal libilityCoverage : Arrays.asList(new BigDecimal("1000000"), new BigDecimal("2000000"), new BigDecimal("5000000"))) {
            if (!tenantInsurancePolicy.requireMinimumLiability().isBooleanTrue()
                    | (tenantInsurancePolicy.requireMinimumLiability().isBooleanTrue() && libilityCoverage.compareTo(tenantInsurancePolicy
                            .minimumRequiredLiability().getValue()) >= 0)) {
                params.generalLiabilityCoverageOptions().add(libilityCoverage);
            }
        }

        params.contentsCoverageOptions().addAll(Arrays.asList(//@formatter:off
                BigDecimal.ZERO,
                new BigDecimal("10000"),
                new BigDecimal("20000"),
                new BigDecimal("30000"),
                new BigDecimal("40000"),
                new BigDecimal("50000")
        ));//@formatter:on

        params.deductibleOptions().addAll(Arrays.asList(//@formatter:off
                        new BigDecimal("500"),
                        new BigDecimal("1000"),
                        new BigDecimal("2500")
        ));//@formatter:on

        LegalTermsDescriptorDTO personalDisclaimerTerms = params.personalDisclaimerTerms().$();
        personalDisclaimerTerms.content().localizedCaption().setValue(i18n.tr("Personal Disclaimer"));
        personalDisclaimerTerms.content().content().setValue(ServerSideFactory.create(TenantSureTextFacade.class).getPersonalDisclaimerText());
        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(TenantAppContext.getCurrentUserCustomer().person().duplicate());
        personalDisclaimerTerms.agrees().add(agreeHolder.duplicate(IAgree.class));
        params.personalDisclaimerTerms().add(personalDisclaimerTerms);

        callback.onSuccess(params);

    }

    @Override
    public void getQuote(AsyncCallback<TenantSureQuoteDTO> callback, TenantSureCoverageDTO quotationRequest) {
        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());
        callback.onSuccess(quote);
    }

    @Override
    public void acceptQuote(AsyncCallback<VoidSerializable> callback, TenantSureQuoteDTO quote, InsurancePaymentMethod paymentMethod) {
        paymentMethod.tenant().set(TenantAppContext.getCurrentUserTenant());
        ServerSideFactory.create(TenantSureFacade.class).updatePaymentMethod(paymentMethod,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());

        ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());

        callback.onSuccess(null);
    }

    @Override
    public void getCurrentTenantAddress(AsyncCallback<AddressStructured> callback) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, TenantAppContext.getCurrentUserTenantInLease());
    }

}
