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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.person.Name.Prefix;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAboutYouSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAdditionalInfoSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataCoApplicantDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDependentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataGeneralQuestionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataIdentificationDocumentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLeaseSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataPeopleSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataResidenceDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataSectionsDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseApplicationDocumentDataCreatorFacadeImpl implements LeaseApplicationDocumentDataCreatorFacade {

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationDataForSignedForm(LeaseApplication application, LeaseTermParticipant<?> participant) {
        return createApplicationDataForBlankForm(application, participant); // TODO 
    }

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationDataForBlankForm(LeaseApplication application, LeaseTermParticipant<?> subjectParticipant) {
        LeaseApplicationDocumentDataDTO data = makeDocumentData();

        Persistence.ensureRetrieve(application.lease().unit().building().landlord(), AttachLevel.Attached);
        data.landlordName().setValue(application.lease().unit().building().landlord().name().getValue());
        data.landlordAddress().setValue(application.lease().unit().building().landlord().address().getStringView());
        // TODO add landlord's LOGO

        makeDataPlaceholders(data.sections().get(0));

        // TODO move this to only signed form
        fillLeaseSection(data.sections().get(0).leaseSection().get(0), application);
        fillPeopleSection(data.sections().get(0).peopleSection().get(0), application, subjectParticipant);
        fillAboutYouSection(data.sections().get(0).aboutYouSection().get(0), application, subjectParticipant);
        fillAdditionalInfoSection(data.sections().get(0).additionalInfoSection().get(0), application, subjectParticipant);

        return data;
    }

    private void makeDataPlaceholders(LeaseApplicationDocumentDataSectionsDTO sections) {
        sections.peopleSection().get(0).coApplicants().add(EntityFactory.create(LeaseApplicationDocumentDataCoApplicantDTO.class));
        sections.peopleSection().get(0).coApplicants().add(EntityFactory.create(LeaseApplicationDocumentDataCoApplicantDTO.class));

        sections.peopleSection().get(0).dependents().add(EntityFactory.create(LeaseApplicationDocumentDataDependentDTO.class));
        sections.peopleSection().get(0).dependents().add(EntityFactory.create(LeaseApplicationDocumentDataDependentDTO.class));
        sections.peopleSection().get(0).dependents().add(EntityFactory.create(LeaseApplicationDocumentDataDependentDTO.class));
        sections.peopleSection().get(0).dependents().add(EntityFactory.create(LeaseApplicationDocumentDataDependentDTO.class));

        sections.aboutYouSection().get(0).identificationDocuments().add(EntityFactory.create(LeaseApplicationDocumentDataIdentificationDocumentDTO.class));
        sections.aboutYouSection().get(0).identificationDocuments().add(EntityFactory.create(LeaseApplicationDocumentDataIdentificationDocumentDTO.class));

        // TODO add questions for filling
    }

    /**
     * Makes a structured document ready for filling with information.
     */
    private LeaseApplicationDocumentDataDTO makeDocumentData() {
        LeaseApplicationDocumentDataDTO data = EntityFactory.create(LeaseApplicationDocumentDataDTO.class);
        LeaseApplicationDocumentDataSectionsDTO details = EntityFactory.create(LeaseApplicationDocumentDataSectionsDTO.class);
        data.sections().add(details);

        LeaseApplicationDocumentDataLeaseSectionDTO leaseSection = EntityFactory.create(LeaseApplicationDocumentDataLeaseSectionDTO.class);
        details.leaseSection().add(leaseSection);

        LeaseApplicationDocumentDataPeopleSectionDTO peopleSection = EntityFactory.create(LeaseApplicationDocumentDataPeopleSectionDTO.class);
        details.peopleSection().add(peopleSection);

        LeaseApplicationDocumentDataAboutYouSectionDTO aboutYouSection = EntityFactory.create(LeaseApplicationDocumentDataAboutYouSectionDTO.class);
        details.aboutYouSection().add(aboutYouSection);

        LeaseApplicationDocumentDataAdditionalInfoSectionDTO additionalInfoSection = EntityFactory
                .create(LeaseApplicationDocumentDataAdditionalInfoSectionDTO.class);
        additionalInfoSection.currentResidence().add(new EntityFactory().create(LeaseApplicationDocumentDataResidenceDTO.class));
        additionalInfoSection.currentResidence().get(0).isRented().setValue(false);
        additionalInfoSection.currentResidence().get(0).isRented().setValue(false);
        details.additionalInfoSection().add(additionalInfoSection);

        return data;
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

    private void fillLeaseSection(LeaseApplicationDocumentDataLeaseSectionDTO leaseSection, LeaseApplication application) {
        Persistence.ensureRetrieve(application.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(application.lease().unit().floorplan(), AttachLevel.Attached);
        Persistence.ensureRetrieve(application.lease().unit().building(), AttachLevel.Attached);

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, application.lease().currentTerm().getPrimaryKey());
        leaseSection.unitNumber().setValue(application.lease().unit().info().number().getValue());
        leaseSection.address().setValue(AddressRetriever.getLeaseLegalAddress(application.lease()).getStringView());
        leaseSection.floorplan().setValue(application.lease().unit().floorplan().marketingName().getValue());
        leaseSection.includedUtilities().setValue(retrieveUtilities(term));

        leaseSection.leaseFrom().setValue(application.lease().leaseFrom().getValue());
        leaseSection.leaseTo().setValue(application.lease().leaseTo().getValue());

        leaseSection.unitRent().setValue(term.version().leaseProducts().serviceItem().agreedPrice().getValue());
    }

    private void fillPeopleSection(LeaseApplicationDocumentDataPeopleSectionDTO peopleSection, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), application.lease().currentTerm());

        for (LeaseTermTenant leaseTermTenant : Persistence.service().query(criteria)) {
            if (leaseTermTenant.role().getValue() == Role.Dependent) {
                LeaseApplicationDocumentDataDependentDTO dependent = peopleSection.dependents().$();
                dependent.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getValue());
                dependent.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getValue());
                dependent.relationship().setValue(leaseTermTenant.relationship().getValue().toString());
                dependent.birthDate().setValue(leaseTermTenant.leaseParticipant().customer().person().birthDate().getValue());
                peopleSection.dependents().add(dependent);
            } else if (leaseTermTenant.role().getValue() != Role.Guarantor && !leaseTermTenant.getPrimaryKey().equals(subjectParticipant.getPrimaryKey())) {
                LeaseApplicationDocumentDataCoApplicantDTO coapplicant = peopleSection.coApplicants().$();
                coapplicant.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getValue());
                coapplicant.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getValue());
                coapplicant.relationship().setValue(leaseTermTenant.relationship().getValue().toString());
                coapplicant.email().setValue(leaseTermTenant.leaseParticipant().customer().person().email().getValue());
                peopleSection.coApplicants().add(coapplicant);
            }
        }
    }

    private void fillAboutYouSection(LeaseApplicationDocumentDataAboutYouSectionDTO aboutYou, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        // Personal Information
        aboutYou.firstName().setValue(subjectParticipant.leaseParticipant().customer().person().name().firstName().getValue());
        aboutYou.lastName().setValue(subjectParticipant.leaseParticipant().customer().person().name().lastName().getValue());
        aboutYou.middleName().setValue(subjectParticipant.leaseParticipant().customer().person().name().middleName().getValue());
        Prefix prefix = subjectParticipant.leaseParticipant().customer().person().name().namePrefix().getValue();
        aboutYou.namePrefix().setValue(prefix != null ? prefix.toString() : "");
        aboutYou.nameSuffix().setValue(subjectParticipant.leaseParticipant().customer().person().name().nameSuffix().getValue());
        aboutYou.gender().setValue(subjectParticipant.leaseParticipant().customer().person().sex().getValue().toString());
        aboutYou.birthDate().setValue(subjectParticipant.leaseParticipant().customer().person().birthDate().getValue());

        // Contact Information:
        aboutYou.homePhone().setValue(subjectParticipant.leaseParticipant().customer().person().homePhone().getValue());
        aboutYou.mobilePhone().setValue(subjectParticipant.leaseParticipant().customer().person().mobilePhone().getValue());
        aboutYou.workPhone().setValue(subjectParticipant.leaseParticipant().customer().person().workPhone().getValue());
        aboutYou.email().setValue(subjectParticipant.leaseParticipant().customer().person().email().getValue());

        // Identification Documents
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), application.lease().unit().building());
        for (IdentificationDocumentFolder id : screening.version().documents()) {
            LeaseApplicationDocumentDataIdentificationDocumentDTO idForPrint = EntityFactory
                    .create(LeaseApplicationDocumentDataIdentificationDocumentDTO.class);
            String documentType = id.idType().name().getValue() != null ? id.idType().name().getValue() : id.idType().type().getValue().toString();
            idForPrint.documentType().setValue(documentType);
            idForPrint.documentNumber().setValue(id.idNumber().getValue());
            aboutYou.identificationDocuments().add(idForPrint);
        }

    }

    private void fillAdditionalInfoSection(LeaseApplicationDocumentDataAdditionalInfoSectionDTO additionalInfo, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), application.lease().unit().building());

        fillResidence(additionalInfo.currentResidence().get(0), screening.version().currentAddress());
        if (!screening.version().previousAddress().isNull()) {
            LeaseApplicationDocumentDataResidenceDTO residence = EntityFactory.create(LeaseApplicationDocumentDataResidenceDTO.class);
            additionalInfo.previousResidences().add(residence);
            fillResidence(residence, screening.version().previousAddress());
        }

        Persistence.ensureRetrieve(screening.version().legalQuestions(), AttachLevel.Attached);
        for (String memberName : screening.version().legalQuestions().getEntityMeta().getMemberNames()) {
            LeaseApplicationDocumentDataGeneralQuestionDTO question = EntityFactory.create(LeaseApplicationDocumentDataGeneralQuestionDTO.class);
            question.question().setValue(screening.version().legalQuestions().getMember(memberName).getMeta().getCaption());
            question.answerYes().setValue(screening.version().legalQuestions().getMember(memberName).getValue() == Boolean.TRUE);
            question.answerNo().setValue(screening.version().legalQuestions().getMember(memberName).getValue() == Boolean.FALSE);

            additionalInfo.generalQuestions().add(question);
        }

    }

    private void fillResidence(LeaseApplicationDocumentDataResidenceDTO residence, PriorAddress address) {
        residence.suiteNumber().setValue(address.suiteNumber().getValue());
        residence.streetNumber().setValue(address.streetNumber().getValue());

        residence.streetNumberSuffix().setValue(address.streetNumberSuffix().getValue());
        residence.streetName().setValue(address.streetName().getValue());
        residence.streetType().setValue(address.streetType().getValue() != null ? address.streetType().getValue().toString() : "");
        residence.streetDirection().setValue(address.streetDirection().getValue() != null ? address.streetDirection().getValue().toString() : "");
        residence.city().setValue(address.city().getValue());
        residence.province().setValue(!address.province().name().isNull() ? address.province().name().getValue() : "");
        residence.postalCode().setValue(address.postalCode().getValue());
        residence.country().setValue(!address.province().country().name().isNull() ? address.province().country().name().getValue() : "");

        residence.moveInDate().setValue(address.moveInDate().getValue());
        residence.moveOutDate().setValue(address.moveOutDate().getValue());
        residence.isOwned().setValue(address.rented().getValue() == OwnedRented.owned);
        residence.isRented().setValue(address.rented().getValue() == OwnedRented.rented);
    }

    private String retrieveUtilities(LeaseTerm term) {
        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.ToStringMembers);
        List<String> utilites = new ArrayList<>(term.version().utilities().size());
        for (BuildingUtility utility : term.version().utilities()) {
            utilites.add(utility.getStringView());
        }
        return StringUtils.join(utilites, ";");
    }

}
