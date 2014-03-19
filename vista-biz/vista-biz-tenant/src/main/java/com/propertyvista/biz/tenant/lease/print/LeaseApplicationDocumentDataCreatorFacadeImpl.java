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

import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;
import com.propertyvista.dto.LeaseApplicationDocumentDataDTO;
import com.propertyvista.dto.LeaseApplicationDocumentDataLeaseSectionDTO;
import com.propertyvista.dto.LeaseApplicationDocumentDataSectionsDTO;

public class LeaseApplicationDocumentDataCreatorFacadeImpl implements LeaseApplicationDocumentDataCreatorFacade {

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationDataForSignedForm(LeaseApplication application) {
        return createApplicationDataForBlankForm(application); // TODO 
    }

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationDataForBlankForm(LeaseApplication application) {
        LeaseApplicationDocumentDataDTO leaseApplicationData = EntityFactory.create(LeaseApplicationDocumentDataDTO.class);

        Persistence.ensureRetrieve(application.lease().unit().building().landlord(), AttachLevel.Attached);
        leaseApplicationData.landlordName().setValue(application.lease().unit().building().landlord().name().getValue());
        leaseApplicationData.landlordAddress().setValue(application.lease().unit().building().landlord().address().getStringView());

        // TODO add landlord's logo
        LeaseApplicationDocumentDataSectionsDTO details = EntityFactory.create(LeaseApplicationDocumentDataSectionsDTO.class);
        LeaseApplicationDocumentDataLeaseSectionDTO leaseSection = EntityFactory.create(LeaseApplicationDocumentDataLeaseSectionDTO.class);
        details.leaseSection().add(leaseSection);
        leaseApplicationData.sections().add(details);

        fillLeaseData(application, leaseSection);

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

        if (signaturesMode == null) {
            signaturesMode = SignaturesMode.None;
        }
        switch (signaturesMode) {
        case SignaturesOnly:
            if (PrintableSignatureChecker.isPrintable(signedTerm.signature())) {
                term4print.signatures().add(signedTerm.signature());
            }
            break;

        case PlaceholdersOnly:
            if (PrintableSignatureChecker.needsPlaceholder(signedTerm.signature())) {
                LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO signaturePlaceHolder = EntityFactory
                        .create(LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO.class);
                signaturePlaceHolder.tenantName().setValue(customer.person().name().getStringView());
                term4print.signaturePlaceholders().add(signaturePlaceHolder);
            }
            break;

        default:
            break;
        }

        return term4print;
    }

    private void fillLeaseData(LeaseApplication application, LeaseApplicationDocumentDataLeaseSectionDTO leaseSection) {
        Persistence.ensureRetrieve(application.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(application.lease().unit().floorplan(), AttachLevel.Attached);
        Persistence.ensureRetrieve(application.lease().unit().building(), AttachLevel.Attached);

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, application.lease().currentTerm().getPrimaryKey());
        leaseSection.unitNumber().setValue(application.lease().unit().info().number().getValue());
        leaseSection.address().setValue(application.lease().unit().building().info().address().getStringView());
        leaseSection.floorplan().setValue(application.lease().unit().floorplan().marketingName().getValue());
        leaseSection.includedUtilities().setValue(retrieveUtilities(term));

        leaseSection.leaseFrom().setValue(application.lease().leaseFrom().getValue());
        leaseSection.leaseTo().setValue(application.lease().leaseTo().getValue());

        leaseSection.unitRent().setValue(term.version().leaseProducts().serviceItem().agreedPrice().getValue());
    }

    private String retrieveUtilities(LeaseTerm term) {
        assert (!term.isValueDetached());

        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.ToStringMembers);

        String res = new String();
        for (BuildingUtility utility : term.version().utilities()) {
            if (!res.isEmpty()) {
                res += ";";
            }
            res += utility.getStringView();
        }

        return res;
    }

}
