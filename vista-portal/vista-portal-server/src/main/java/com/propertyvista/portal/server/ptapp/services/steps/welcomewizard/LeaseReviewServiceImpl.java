/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.moveinwizardmockup.LeaseReviewDTO;
import com.propertyvista.domain.tenant.ptapp.IAgree;
import com.propertyvista.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizard.LeaseReviewService;

public class LeaseReviewServiceImpl implements LeaseReviewService {

    public static final String RESOURCES_PACKAGE = "";

    @Override
    public void retrieve(AsyncCallback<LeaseReviewDTO> callback, Key tenantId) {
        LeaseReviewDTO mockupLeaseReview = EntityFactory.create(LeaseReviewDTO.class);

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(WelcomeWizardDemoData.applicantsCustomer().person().duplicate());

        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("Agreement Definition");

            term.content()
                    .content()
                    .setValue(
                            readResource("lease-definition.html").replaceAll("#TENANT_NAME",
                                    WelcomeWizardDemoData.applicantsCustomer().person().name().getStringView()));
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }
        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("Agreement Terms");
            term.content()
                    .content()
                    .setValue(
                            readResource("lease-terms.html").replaceAll("#TENANT_NAME",
                                    WelcomeWizardDemoData.applicantsCustomer().person().name().getStringView()));
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }
        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("More Terms and Rules and Ammendments");
            term.content().content().setValue(readResource("lease-more-terms.html"));
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }

        callback.onSuccess(mockupLeaseReview);
    }

    @Override
    public void save(AsyncCallback<LeaseReviewDTO> callback, LeaseReviewDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

    public String readResource(String resourceName) {
        StringBuilder resourceAsText = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(RESOURCES_PACKAGE + resourceName)));
            String line = null;
            while ((line = br.readLine()) != null) {
                resourceAsText.append(line).append('\n');
            }
        } catch (Throwable e) {
            resourceAsText.append("\nERROR reading resource : ").append(resourceName).append("\n");
        }
        return resourceAsText.toString();

    }

}
