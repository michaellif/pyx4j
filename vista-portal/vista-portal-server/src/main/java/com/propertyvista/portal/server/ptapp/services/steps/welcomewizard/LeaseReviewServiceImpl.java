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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.domain.ptapp.IAgree;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.LeaseReviewDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizard.LeaseReviewService;

public class LeaseReviewServiceImpl implements LeaseReviewService {

    @Override
    public void retrieve(AsyncCallback<LeaseReviewDTO> callback, Key tenantId) {
        LeaseReviewDTO mockupLeaseReview = EntityFactory.create(LeaseReviewDTO.class);

        IAgree agreeHolder = EntityFactory.create(IAgree.class);
        agreeHolder.person().set(WelcomeWizardDemoData.applicantsCustomer().person().duplicate());

        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("Agreement Definition");
            term.content().content().setValue("TBD");
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }
        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("Agreement Terms");
            term.content().content().setValue("TBD");
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }
        {
            LegalTermsDescriptorDTO term = mockupLeaseReview.leaseAgreementTerms().$();
            term.content().localizedCaption().setValue("More Terms and Rules and Ammendments");
            term.content().content().setValue("TBD");
            term.agrees().add(agreeHolder.duplicate(IAgree.class));
            mockupLeaseReview.leaseAgreementTerms().add(term);
        }

        callback.onSuccess(mockupLeaseReview);
    }

    @Override
    public void save(AsyncCallback<LeaseReviewDTO> callback, LeaseReviewDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

}
