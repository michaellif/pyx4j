/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.portal.domain.ptapp.IAgree;
import com.propertyvista.portal.rpc.portal.dto.insurancemockup.TenantInsuranceDTO;
import com.propertyvista.portal.rpc.portal.dto.insurancemockup.TenantInsuranceDTO.InsuranceStatus;
import com.propertyvista.portal.rpc.portal.services.InsuranceService;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.ExistingInsurance;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsurancePaymentMethodDTO.PaymentMethod;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO.FormOfCoverage;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO.HomeBuisnessOptions;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.ptapp.services.steps.welcomewizard.WelcomeWizardDemoData;

public class InsuranceServiceImpl implements InsuranceService {

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

    private final static String AGREEMENT_LEGAL_BLURB_AND_PRE_AUTHORIZATION_AGREEMENT =//@formatter:off
            "TBD";
    //@formatter:on

    @Override
    public void retrieveInsurance(AsyncCallback<TenantInsuranceDTO> callback) {
        TenantInsuranceDTO dto = EntityFactory.create(TenantInsuranceDTO.class);

        if (TenantAppContext.getCurrentUser().email().getValue().startsWith("t001")) {
            dto.status().setValue(InsuranceStatus.unknown);
            dto.newInsuranceRequest().set(createDefaultNewInsuranceRequestValues());
        } else if (TenantAppContext.getCurrentUser().email().getValue().startsWith("t002")) {
            dto.status().setValue(InsuranceStatus.independant);
            dto.independant().set(createDefaultIndependantInsuranceValues());
        } else if (TenantAppContext.getCurrentUser().email().getValue().startsWith("t003")) {
            dto.status().setValue(InsuranceStatus.tenantSure);
        }
        callback.onSuccess(dto);
    }

    private ExistingInsurance createDefaultIndependantInsuranceValues() {
        ExistingInsurance insurance = EntityFactory.create(ExistingInsurance.class);
        insurance.insuranceProvider().setValue("Mockup Insurance Provider");
        insurance.insuranceCertificateNumber().setValue("123-1235098A");
        insurance.personalLiability().setValue(new BigDecimal("10000000"));
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new LogicalDate());
        cal.add(Calendar.YEAR, -1);
        insurance.insuranceStartDate().setValue(new LogicalDate(cal.getTime()));

        cal.add(Calendar.YEAR, 3);
        insurance.insuranceExpirationDate().setValue(new LogicalDate(cal.getTime()));
        return insurance;
    }

    private IEntity createDefaultNewInsuranceRequestValues() {
        InsuranceDTO insurance = EntityFactory.create(InsuranceDTO.class);

        // INSURANCE PURCHASE PART INITIALIZATION
        insurance.purchaseInsurance().personalContentsLimit().setValue(asMoney(80000));
        insurance.purchaseInsurance().propertyAwayFromPremises().setValue(asMoney(10000));
        insurance.purchaseInsurance().additionalLivingExpenses().setValue(asMoney(40000));

        insurance.purchaseInsurance().deductible().setValue(asMoney(500));

        insurance.purchaseInsurance().formOfCoverage().setValue(FormOfCoverage.basicCoverage);

        insurance.purchaseInsurance().jewleryAndFurs().setValue(asMoney(5000));
        insurance.purchaseInsurance().bicycles().setValue(asMoney(2000));
        insurance.purchaseInsurance().personalComputers().setValue(asMoney(6000));
        insurance.purchaseInsurance().moneyOrGiftGardsOrGiftCertificates().setValue(asMoney(500));
        insurance.purchaseInsurance().securities().setValue(asMoney(500));
        insurance.purchaseInsurance().utilityTraders().setValue(asMoney(2000));
        insurance.purchaseInsurance().spareAutomobileParts().setValue(asMoney(2000));
        insurance.purchaseInsurance().coinBanknoteOrStampCollections().setValue(asMoney(2000));
        insurance.purchaseInsurance().collectibleCardsAndComics().setValue(asMoney(2000));

        insurance.purchaseInsurance().freezerFoodSpoilage().setValue(asMoney(1000));
        insurance.purchaseInsurance().animalsBirdsAndFish().setValue(asMoney(1000));
        insurance.purchaseInsurance().personalLiability().setValue(asMoney(5000000));

        insurance.purchaseInsurance().homeBuiness().setValue(HomeBuisnessOptions.no);
        insurance.purchaseInsurance().numOfPrevClaims().setValue(0);

        insurance.purchaseInsurance().paymentMethod().paymentMethod().setValue(PaymentMethod.Visa);

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(WelcomeWizardDemoData.applicantsCustomer().person().duplicate());

        LegalTermsDescriptorDTO personalDisclaimerDescriptor = EntityFactory.create(LegalTermsDescriptorDTO.class);
        LegalTermsContent personalDisclaimerContent = EntityFactory.create(LegalTermsContent.class);
        personalDisclaimerContent.content().setValue(PERSONAL_DISCLAIMER);
        personalDisclaimerContent.localizedCaption().setValue("Personal Disclaimer");
        personalDisclaimerDescriptor.content().set(personalDisclaimerContent);
        personalDisclaimerDescriptor.agrees().add(agreeHolder.duplicate(IAgree.class));

        insurance.purchaseInsurance().personalDisclaimerTerms().add(personalDisclaimerDescriptor);

        LegalTermsDescriptorDTO agreement = insurance.purchaseInsurance().agreementLegalBlurbAndPreAuthorizationAgreeement().$();
        agreement.content().content().setValue(AGREEMENT_LEGAL_BLURB_AND_PRE_AUTHORIZATION_AGREEMENT);
        agreement.content().localizedCaption().setValue("AGREEMENT LEGAL BLURB AND PRE-AUTHORIZATION AGREEMENT");
        agreement.agrees().add(agreeHolder.duplicate(IAgree.class));
        insurance.purchaseInsurance().agreementLegalBlurbAndPreAuthorizationAgreeement().add(agreement);

        DigitalSignature signature = insurance.purchaseInsurance().digitalSignatures().$();
        signature.ipAddress().setValue(Context.getRequestRemoteAddr());
        signature.timestamp().setValue(new Date());
        signature.person().set(WelcomeWizardDemoData.applicantsCustomer());
        insurance.purchaseInsurance().digitalSignatures().add(signature);

        insurance.purchaseInsurance().paymentMethod().currentAddress().set(WelcomeWizardDemoData.applicantsAddress());

        // EXISTING INSURACE PART INTIALIZATION
        insurance.existingInsurance().documents().add(insurance.existingInsurance().documents().$());

        return insurance;
    }

    private static BigDecimal asMoney(Integer amount) {
        return new BigDecimal(new BigInteger(amount.toString()), new MathContext(2));
    }

}
