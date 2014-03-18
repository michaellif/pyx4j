/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;
import com.propertyvista.dto.LeaseApplicationDocumentDataDTO;

public class LeaseApplicationDocumentDataCreatorFacadeImpl implements LeaseApplicationDocumentDataCreatorFacade {

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationData(OnlineApplication onlineApplication, SignaturesMode signaturesMode) {
        retrive(onlineApplication);
        LeaseApplicationDocumentDataDTO leaseApplicationData = EntityFactory.create(LeaseApplicationDocumentDataDTO.class);

        leaseApplicationData.landlordName().setValue(onlineApplication.masterOnlineApplication().building().landlord().name().getValue());
        leaseApplicationData.landlordAddress().setValue(onlineApplication.masterOnlineApplication().building().landlord().address().getStringView());

        leaseApplicationData.applicants().add(makeApplicant(onlineApplication));

        for (SignedOnlineApplicationLegalTerm signedTerm : onlineApplication.legalTerms()) {
            leaseApplicationData.terms().add(makeTermForPrint(onlineApplication.customer(), signedTerm, signaturesMode));
        }

        return leaseApplicationData;
    }

    private void retrive(OnlineApplication onlineApplication) {
        Persistence.ensureRetrieve(onlineApplication.masterOnlineApplication(), AttachLevel.Attached);
        Persistence.ensureRetrieve(onlineApplication.masterOnlineApplication().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(onlineApplication.masterOnlineApplication().building().landlord(), AttachLevel.Attached);

        for (SignedOnlineApplicationLegalTerm signedTerm : onlineApplication.legalTerms()) {
            Persistence.ensureRetrieve(signedTerm.signature(), AttachLevel.Attached);
        }

    }

    private LeaseAgreementDocumentLegalTermTenantDTO makeApplicant(OnlineApplication onlineApplication) {
        LeaseAgreementDocumentLegalTermTenantDTO applicant = EntityFactory.create(LeaseAgreementDocumentLegalTermTenantDTO.class);
        applicant.fullName().setValue(onlineApplication.customer().person().name().getStringView());
        return applicant;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeTermForPrint(Customer customer, SignedOnlineApplicationLegalTerm signedTerm,
            SignaturesMode signaturesMode) {
        LeaseAgreementDocumentLegalTerm4PrintDTO term4print = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        term4print.title().setValue(signedTerm.term().title().getValue());
        term4print.body().setValue(signedTerm.term().body().getValue());

        if (signaturesMode == SignaturesMode.SignaturesOnly) {
            term4print.signatures().add(signedTerm.signature());
        } else if (signaturesMode == SignaturesMode.PlaceholdersOnly) {
            LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO signaturePlaceHolder = EntityFactory
                    .create(LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO.class);
            signaturePlaceHolder.tenantName().setValue(customer.person().name().getStringView());
            term4print.signaturePlaceholders().add(signaturePlaceHolder);
        }

        return term4print;
    }

}
