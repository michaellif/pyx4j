/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.portal.domain.ptapp.IAgree;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizard.InsuranceService;

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

    @Override
    public void retrieve(AsyncCallback<InsuranceDTO> callback, Key tenantId) {
        InsuranceDTO insurance = EntityFactory.create(InsuranceDTO.class);
        insurance.purchaseInsurance().paymentMethod().type().setValue(PaymentType.Echeck);

        LegalTermsDescriptorDTO personalDisclaimerDescriptor = EntityFactory.create(LegalTermsDescriptorDTO.class);
        LegalTermsContent personalDisclaimerContent = EntityFactory.create(LegalTermsContent.class);
        personalDisclaimerContent.content().setValue(PERSONAL_DISCLAIMER);
        personalDisclaimerContent.localizedCaption().setValue("Terms");
        personalDisclaimerDescriptor.content().set(personalDisclaimerContent);

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(WelcomeWizardDemoData.applicantsPerson());
        personalDisclaimerDescriptor.agrees().add(agreeHolder);

        insurance.purchaseInsurance().personalDisclaimerTerms().add(personalDisclaimerDescriptor);

        callback.onSuccess(insurance);
    }

    @Override
    public void save(AsyncCallback<InsuranceDTO> callback, InsuranceDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

}
