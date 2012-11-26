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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.domain.tenant.ptapp.IAgree;
import com.propertyvista.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantSurePurchaseServiceImpl implements TenantSurePurchaseService {

    private static final I18n i18n = I18n.get(TenantSurePurchaseServiceImpl.class);

    @Deprecated
    // TODO this is just a mockup
    private final static String PERSONAL_DISCLAIMER =//@formatter:off
            "In order to continue and obatain a quote and insurance certificate from TenantSure, we need you to review and agree to the following disclaimers:<br/>" +
            "<ol>" +
                "<li>" +
                    "I understand and agree that quotes received and/or insurance coverage I purchase on this web site are based on accurate and true information tha I provide. " +
                    "I will provide accurate information to the best of my ability and degree that any of false information could cause my premiums to increase my insturance coverage to be invalid." +
                "</li>" +
                "<li>" +
                    "I understand that if I choose to purchase insurance thought this web site, my previous insurance history, my previous landlord history, and any other background information may be obtained. " +
                    "This information will be held in strict confidence and will only be used to assess my risk as an insurance consumer and to determine my insturance premiums." +
                 "</li>" +            
                 "<li>" +
                     "I understand that if I choose to purchase insurance throught this web site, my landlord will be advised that I am insured throught this web site. Only basic policy details will be provided to my landlord. " +
                     "No financial or specific coverage information will be shared." +
                 "</li>" +
                 "<li>" +
                     "I acknowledge that I have the authority to determine coverage for myself and my roommates." +
                 "</li>" +
                 "<li>" +
                     "I have read and agree with privacy policy of this site <a href=\"javascript:void();\">Privacy Policy</a>\n" +
                 "</li>" +
              "</ol>";
    //@formatter:on

    @Override
    public void getQuotationRequestParams(AsyncCallback<TenantSureQuotationRequestParamsDTO> callback) {
        TenantSureQuotationRequestParamsDTO params = EntityFactory.create(TenantSureQuotationRequestParamsDTO.class);

        // these values are taken from the TenantSure API document: Appendix I
        params.generalLiabilityCoverageOptions().addAll(Arrays.asList(//@formatter:off
                new BigDecimal("1000000"),
                new BigDecimal("2000000"),
                new BigDecimal("5000000")
        ));//@formatter:on
        params.contentsCoverageOptions().addAll(Arrays.asList(//@formatter:off
                null,
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

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(TenantAppContext.getCurrentUserCustomer().person().duplicate());

        LegalTermsDescriptorDTO personalDisclaimerTerms = params.personalDisclaimerTerms().$();
        personalDisclaimerTerms.content().localizedCaption().setValue("Personal Disclaimer");
        personalDisclaimerTerms.content().content().setValue(PERSONAL_DISCLAIMER);
        params.personalDisclaimerTerms().add(personalDisclaimerTerms);
        personalDisclaimerTerms.agrees().add(agreeHolder.duplicate(IAgree.class));

        DigitalSignature signature = params.digitalSignatures().$();
        signature.timestamp().setValue(new LogicalDate());
        signature.ipAddress().setValue(Context.getRequestRemoteAddr());
        signature.person().set(TenantAppContext.getCurrentUserCustomer().duplicate());
        params.digitalSignatures().add(signature);

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
        ServerSideFactory.create(TenantSureFacade.class).updatePaymentMethod(paymentMethod,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());

        ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, TenantAppContext.getCurrentUserTenantInLease().<Tenant> createIdentityStub());

        callback.onSuccess(null);
    }

    @Override
    public void getCurrentTenantAddress(AsyncCallback<AddressStructured> callback) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, TenantAppContext.getCurrentUserTenantInLease());
    }
}
