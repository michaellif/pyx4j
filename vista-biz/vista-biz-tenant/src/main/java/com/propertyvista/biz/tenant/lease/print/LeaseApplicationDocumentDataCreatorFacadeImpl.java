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

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.blob.LandlordMediaBlob;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.LeaseApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAboutYouSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAdditionalInfoSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataCoApplicantDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDependentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataEmergencyContactsSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataFinancialSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataGeneralQuestionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataGuarantorDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataIdentificationDocumentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLeaseSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLegalSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLegalTermDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataPeopleSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataResidenceDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataSectionsDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseApplicationDocumentDataCreatorFacadeImpl implements LeaseApplicationDocumentDataCreatorFacade {

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationData(DocumentMode documentMode, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        LeaseApplicationDocumentDataDTO data = makeDocumentData();

        Persistence.ensureRetrieve(application.lease().unit().building().landlord(), AttachLevel.Attached);
        data.landlordName().setValue(application.lease().unit().building().landlord().name().getValue());
        data.landlordAddress().setValue(application.lease().unit().building().landlord().address().getStringView());
        if (!application.lease().unit().building().landlord().logo().isEmpty()) {
            Persistence.ensureRetrieve(application.lease().unit().building().landlord().logo().file(), AttachLevel.Attached);
            LandlordMediaBlob blob = Persistence.service().retrieve(LandlordMediaBlob.class,
                    application.lease().unit().building().landlord().logo().file().blobKey().getValue());
            data.landlordLogo().setValue((blob.data().getValue()));
        }

        if (false /* TODO && (documentMode == blank) */) {
            makeDataPlaceholders(data.sections().get(0)); // TODO not sure it's supposed to work like that at all...

        } else {
            fillLeaseSection(data.sections().get(0).leaseSection().get(0), application);
            fillPeopleSection(data.sections().get(0).peopleSection().get(0), application, subjectParticipant);
            fillAboutYouSection(data.sections().get(0).aboutYouSection().get(0), application, subjectParticipant);
            fillAdditionalInfoSection(data.sections().get(0).additionalInfoSection().get(0), application, subjectParticipant);
            fillFinaincialSection(data.sections().get(0).financialSection().get(0), application, subjectParticipant);
            fillEmergencyContacts(data.sections().get(0).emergencyContactsSection().get(0), application, subjectParticipant);
            fillLegalSection(data.sections().get(0).legalSection().get(0), application, subjectParticipant, documentMode == DocumentMode.InkSinging);
        }
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

        sections.additionalInfoSection().get(0).currentResidence().add(EntityFactory.create(LeaseApplicationDocumentDataResidenceDTO.class));
        sections.additionalInfoSection().get(0).currentResidence().get(0).isRented().setValue(true);
        sections.additionalInfoSection().get(0).currentResidence().get(0).isOwned().setValue(true);

        // TODO add questions for filling ?
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
        details.additionalInfoSection().add(additionalInfoSection);

        LeaseApplicationDocumentDataFinancialSectionDTO financialSection = EntityFactory.create(LeaseApplicationDocumentDataFinancialSectionDTO.class);
        details.financialSection().add(financialSection);

        LeaseApplicationDocumentDataEmergencyContactsSectionDTO emergencyContactsSection = EntityFactory
                .create(LeaseApplicationDocumentDataEmergencyContactsSectionDTO.class);
        details.emergencyContactsSection().add(emergencyContactsSection);

        LeaseApplicationDocumentDataLegalSectionDTO legalSection = EntityFactory.create(LeaseApplicationDocumentDataLegalSectionDTO.class);
        details.legalSection().add(legalSection);
        return data;
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

        leaseSection.unitRent().setValue(ServerSideFactory.create(BillingFacade.class).getActualPrice(term.version().leaseProducts().serviceItem()));
    }

    private void fillPeopleSection(LeaseApplicationDocumentDataPeopleSectionDTO peopleSection, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), application.lease().currentTerm());

        for (LeaseTermTenant leaseTermTenant : Persistence.service().query(criteria)) {
            if (leaseTermTenant.role().getValue() == Role.Dependent) {
                LeaseApplicationDocumentDataDependentDTO dependent = peopleSection.dependents().$();
                dependent.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getStringView());
                dependent.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getStringView());
                dependent.relationship().setValue(leaseTermTenant.relationship().getStringView());
                dependent.birthDate().setValue(leaseTermTenant.leaseParticipant().customer().person().birthDate().getValue());
                peopleSection.dependents().add(dependent);
            } else if (leaseTermTenant.role().getValue() != Role.Guarantor && !leaseTermTenant.getPrimaryKey().equals(subjectParticipant.getPrimaryKey())) {
                LeaseApplicationDocumentDataCoApplicantDTO coapplicant = peopleSection.coApplicants().$();
                coapplicant.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getStringView());
                coapplicant.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getStringView());
                coapplicant.relationship().setValue(leaseTermTenant.relationship().getStringView());
                coapplicant.email().setValue(leaseTermTenant.leaseParticipant().customer().person().email().getStringView());
                peopleSection.coApplicants().add(coapplicant);
            }
        }
    }

    private void fillAboutYouSection(LeaseApplicationDocumentDataAboutYouSectionDTO aboutYou, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        // Personal Information
        aboutYou.firstName().setValue(subjectParticipant.leaseParticipant().customer().person().name().firstName().getStringView());
        aboutYou.lastName().setValue(subjectParticipant.leaseParticipant().customer().person().name().lastName().getStringView());
        aboutYou.middleName().setValue(subjectParticipant.leaseParticipant().customer().person().name().middleName().getStringView());
        aboutYou.namePrefix().setValue(subjectParticipant.leaseParticipant().customer().person().name().namePrefix().getStringView());
        aboutYou.nameSuffix().setValue(subjectParticipant.leaseParticipant().customer().person().name().nameSuffix().getStringView());
        aboutYou.gender().setValue(subjectParticipant.leaseParticipant().customer().person().sex().getStringView());
        aboutYou.birthDate().setValue(subjectParticipant.leaseParticipant().customer().person().birthDate().getValue());

        // Contact Information:
        aboutYou.homePhone().setValue(subjectParticipant.leaseParticipant().customer().person().homePhone().getValue());
        aboutYou.mobilePhone().setValue(subjectParticipant.leaseParticipant().customer().person().mobilePhone().getValue());
        aboutYou.workPhone().setValue(subjectParticipant.leaseParticipant().customer().person().workPhone().getValue());
        aboutYou.email().setValue(subjectParticipant.leaseParticipant().customer().person().email().getValue());

        // Identification Documents
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), application.lease().unit().building());
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);
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

        additionalInfo.currentResidence().add(EntityFactory.create(LeaseApplicationDocumentDataResidenceDTO.class));

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

    private void fillFinaincialSection(LeaseApplicationDocumentDataFinancialSectionDTO financialInfo, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {

        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), application.lease().unit().building());
        Persistence.ensureRetrieve(screening.version().incomes(), AttachLevel.Attached);

        for (CustomerScreeningIncome income : screening.version().incomes()) {
            Persistence.ensureRetrieve(income, AttachLevel.Attached);
            financialInfo.incomeSources().add(income);
        }

        Persistence.ensureRetrieve(screening.version().assets(), AttachLevel.Attached);
        financialInfo.assets().addAll(screening.version().assets());

        // guarantors:

        if (subjectParticipant.role().getValue() == Role.Guarantor) {
            return;
        }

        EntityQueryCriteria<LeaseTermGuarantor> criteria = new EntityQueryCriteria<LeaseTermGuarantor>(LeaseTermGuarantor.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), application.lease().currentTerm());
        criteria.eq(criteria.proto().tenant().customer(), subjectParticipant.leaseParticipant().customer());

        for (LeaseTermGuarantor ltg : Persistence.service().query(criteria)) {
            LeaseApplicationDocumentDataGuarantorDTO guarantor = EntityFactory.create(LeaseApplicationDocumentDataGuarantorDTO.class);

            guarantor.relationship().setValue(ltg.relationship().getValue());

            guarantor.firstName().setValue(ltg.leaseParticipant().customer().person().name().firstName().getValue());
            guarantor.lastName().setValue(ltg.leaseParticipant().customer().person().name().lastName().getValue());
            guarantor.email().setValue(ltg.leaseParticipant().customer().person().email().getValue());

            financialInfo.guarantors().add(guarantor);
        }

    }

    private void fillResidence(LeaseApplicationDocumentDataResidenceDTO residence, PriorAddress address) {
        residence.suiteNumber().setValue(address.suiteNumber().getValue());
        residence.streetNumber().setValue(address.streetNumber().getValue());
        residence.streetName().setValue(address.streetName().getValue());
        residence.city().setValue(address.city().getValue());
        residence.province().setValue(address.province().getStringView());
        residence.postalCode().setValue(address.postalCode().getStringView());
        residence.country().setValue(address.country().getValue().name);

        residence.moveInDate().setValue(address.moveInDate().getValue());
        residence.moveOutDate().setValue(address.moveOutDate().getValue());
        residence.isOwned().setValue(address.rented().getValue() == OwnedRented.owned);
        residence.isRented().setValue(address.rented().getValue() == OwnedRented.rented);
    }

    private void fillEmergencyContacts(LeaseApplicationDocumentDataEmergencyContactsSectionDTO emergencyContacts, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        Persistence.ensureRetrieve(subjectParticipant.leaseParticipant().customer().emergencyContacts(), AttachLevel.Attached);
        emergencyContacts.emergencyContacts().addAll(subjectParticipant.leaseParticipant().customer().emergencyContacts());
    }

    private void fillLegalSection(LeaseApplicationDocumentDataLegalSectionDTO legalSection, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant, boolean makeWithSignaturePlaceholders) {

        List<SignedOnlineApplicationLegalTerm> signedLegalTerms;

        // fetch terms
        if (makeWithSignaturePlaceholders) {
            // TODO copied from OnlineApplicationFacade.getOnlineApplicationLegalTerms(), probably should be merged
            Building policyNode = application.lease().unit().building();

            LeaseApplicationLegalPolicy leaseApplicationPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode,
                    LeaseApplicationLegalPolicy.class);
            signedLegalTerms = new ArrayList<SignedOnlineApplicationLegalTerm>();
            for (LeaseApplicationLegalTerm term : leaseApplicationPolicy.legalTerms()) {
                TargetRole termRole = term.applyToRole().getValue();
                if (termRole.matchesApplicationRole(subjectParticipant.role().getValue())) {
                    SignedOnlineApplicationLegalTerm signedTerm = EntityFactory.create(SignedOnlineApplicationLegalTerm.class);
                    signedTerm.term().set(term);
                    signedTerm.signature().signatureFormat().set(term.signatureFormat());
                    signedLegalTerms.add(signedTerm);
                }
            }

        } else {
            EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
            criteria.eq(criteria.proto().masterOnlineApplication().leaseApplication(), application);
            criteria.eq(criteria.proto().customer(), subjectParticipant.leaseParticipant().customer());

            OnlineApplication onlineApplication = Persistence.service().retrieve(criteria);
            if (onlineApplication == null) {
                throw new RuntimeException("online application for application=" + "" + " customer=" + ""
                        + " was not found, can't create printable legal terms");
            }
            signedLegalTerms = onlineApplication.legalTerms();
        }

        // convert for printing
        for (SignedOnlineApplicationLegalTerm signedLegalTerm : signedLegalTerms) {
            LeaseApplicationDocumentDataLegalTermDTO legalTerm = EntityFactory.create(LeaseApplicationDocumentDataLegalTermDTO.class);
            legalTerm.title().setValue(signedLegalTerm.term().title().getValue());
            legalTerm.wordingHtml().setValue(signedLegalTerm.term().body().getValue());
            Persistence.ensureRetrieve(signedLegalTerm.signature(), AttachLevel.Attached);
            legalTerm.signature().set(signedLegalTerm.signature().duplicate());
            legalTerm.makeWithSignaturePlaceholder().setValue(makeWithSignaturePlaceholders);
            legalSection.legalTerms().add(legalTerm);
        }
    }

    private String retrieveUtilities(LeaseTerm term) {
        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.ToStringMembers);
        List<String> utilites = new ArrayList<>(term.version().utilities().size());
        for (BuildingUtility utility : term.version().utilities()) {
            utilites.add(utility.getStringView());
        }
        return StringUtils.join(utilites, "; ");
    }

}
