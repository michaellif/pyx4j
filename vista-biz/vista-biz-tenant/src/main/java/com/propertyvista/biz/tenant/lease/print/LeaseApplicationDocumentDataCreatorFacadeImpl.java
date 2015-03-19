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
 */
package com.propertyvista.biz.tenant.lease.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.domain.blob.LandlordMediaBlob;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.policy.policies.LeaseApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestion;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAboutYouSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAdditionalInfoSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAdjustmentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataAdjustmentsSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataCoApplicantDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDependentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataEmergencyContactsSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataFinancialSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataFirstPaymentLineItemDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataFirstPaymentSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataGeneralQuestionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataGuarantorDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataIdentificationDocumentDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLeaseSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLegalSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataLegalTermDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataPeopleSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataRentalItemDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataRentalItemsSectionDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataResidenceDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataSectionsDTO;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationDocumentDataCreatorFacadeImpl implements LeaseApplicationDocumentDataCreatorFacade {

    @Override
    public LeaseApplicationDocumentDataDTO createApplicationData(DocumentMode documentMode, LeaseApplication application,
            LeaseTermParticipant<?> subjectParticipant) {
        LeaseApplicationDocumentDataDTO data = makeDocumentData();
        Persistence.ensureRetrieve(application.lease().unit().building(), AttachLevel.Attached);
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(application.lease(), false);
        Persistence.ensureRetrieve(lease.unit().building().landlord().logo(), AttachLevel.Attached);
        data.landlordName().setValue(lease.unit().building().landlord().name().getValue());
        data.landlordAddress().setValue(lease.unit().building().landlord().address().getStringView());
        data.name().setValue(
                subjectParticipant.leaseParticipant().customer().person().name().firstName().getStringView() + " "
                        + subjectParticipant.leaseParticipant().customer().person().name().lastName().getStringView());

        data.date().setValue(application.submission().decisionDate().getValue());

        byte[] logo = null;
        if (!lease.unit().building().landlord().logo().isEmpty()) {
            Persistence.ensureRetrieve(lease.unit().building().landlord().logo().file(), AttachLevel.Attached);
            LandlordMediaBlob blob = Persistence.service().retrieve(LandlordMediaBlob.class,
                    lease.unit().building().landlord().logo().file().blobKey().getValue());
            logo = blob.data().getValue();
            data.landlordLogo().setValue(logo);
        }
        if (documentMode == DocumentMode.InkSinging) {
            data.submissionDate().setValue(retrieveOnlineApplication(application, subjectParticipant).submissionDate().getValue());
        }
        data.leaseId().setValue(lease.leaseApplication().applicationId().getValue());

        if (false /* TODO && (documentMode == blank) */) {
            makeDataPlaceholders(data.sections().get(0)); // TODO not sure it's supposed to work like that at all...
        } else {
            fillLeaseSection(data.sections().get(0).leaseSection().get(0), lease);
            fillRentalItemsSection(data.sections().get(0).rentalItemsSection().get(0), lease);
            fillAdjustmentsSection(data.sections().get(0).adjustmentsSection().get(0), lease);
            fillFirstPaymentData(data.sections().get(0).firstPaymentSection().get(0), lease);
            fillPeopleSection(data.sections().get(0).peopleSection().get(0), lease, subjectParticipant);
            fillAboutYouSection(data.sections().get(0).aboutYouSection().get(0), lease, subjectParticipant, application.submission().decisionDate().getValue(),
                    logo);
            fillAdditionalInfoSection(data.sections().get(0).additionalInfoSection().get(0), lease, subjectParticipant);
            fillFinaincialSection(data.sections().get(0).financialSection().get(0), lease, subjectParticipant);
            fillEmergencyContacts(data.sections().get(0).emergencyContactsSection().get(0), lease, subjectParticipant);
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

        LeaseApplicationDocumentDataRentalItemsSectionDTO rentalItemsSection = EntityFactory.create(LeaseApplicationDocumentDataRentalItemsSectionDTO.class);
        details.rentalItemsSection().add(rentalItemsSection);

        LeaseApplicationDocumentDataAdjustmentsSectionDTO adjustmentsSection = EntityFactory.create(LeaseApplicationDocumentDataAdjustmentsSectionDTO.class);
        details.adjustmentsSection().add(adjustmentsSection);

        LeaseApplicationDocumentDataFirstPaymentSectionDTO firstPayemntSection = EntityFactory.create(LeaseApplicationDocumentDataFirstPaymentSectionDTO.class);
        details.firstPaymentSection().add(firstPayemntSection);

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

    private void fillLeaseSection(LeaseApplicationDocumentDataLeaseSectionDTO leaseSection, Lease lease) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.unit().floorplan(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, lease.currentTerm().getPrimaryKey());
        leaseSection.landlordName().setValue(lease.unit().building().landlord().name().getValue());
        leaseSection.unitNumber().setValue(lease.unit().info().number().getValue());
        leaseSection.address().setValue(AddressRetriever.getLeaseLegalAddress(lease).getStringView());
        leaseSection.floorplan().setValue(lease.unit().floorplan().marketingName().getValue());
        leaseSection.city().setValue(AddressRetriever.getLeaseLegalAddress(lease).city().getValue());
        leaseSection.province().setValue(AddressRetriever.getLeaseLegalAddress(lease).province().getValue());
        leaseSection.postalCode().setValue(AddressRetriever.getLeaseLegalAddress(lease).postalCode().getValue());
        leaseSection.street().setValue(
                AddressRetriever.getLeaseLegalAddress(lease).streetNumber().getValue() + " "
                        + (AddressRetriever.getLeaseLegalAddress(lease).streetName().getValue()));
        leaseSection.includedUtilities().setValue(retrieveUtilities(term));

        leaseSection.leaseFrom().setValue(lease.currentTerm().termFrom().getValue());
        leaseSection.leaseTo().setValue(lease.currentTerm().termTo().getValue());
        leaseSection.totalMonths().setValue(String.valueOf(getTotalMonths(lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue())));

        leaseSection.unitRent().setValue(ServerSideFactory.create(BillingFacade.class).getActualPrice(term.version().leaseProducts().serviceItem()));
    }

    private void fillRentalItemsSection(LeaseApplicationDocumentDataRentalItemsSectionDTO rentalItemsSectionDTO, Lease lease) {
        rentalItemsSectionDTO.rentalItems().add(
                getRentalItem(lease.currentTerm().version().leaseProducts().serviceItem(), lease.currentTerm().termFrom().getValue(), lease.currentTerm()
                        .termTo().getValue()));
        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {
            rentalItemsSectionDTO.rentalItems().add(getRentalItem(feature, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue()));
        }
    }

    private LeaseApplicationDocumentDataRentalItemDTO getRentalItem(BillableItem billableItem, LogicalDate from, LogicalDate to) {
        LeaseApplicationDocumentDataRentalItemDTO rentalItem = EntityFactory.create(LeaseApplicationDocumentDataRentalItemDTO.class);
        rentalItem.item().setValue(billableItem.item().name().getValue());
        rentalItem.price().setValue(billableItem.agreedPrice().getStringView());
        rentalItem.effectiveDate().setValue(billableItem.effectiveDate().getValue() == null ? from : billableItem.effectiveDate().getValue());
        rentalItem.expirationDate().setValue(billableItem.expirationDate().getValue() == null ? to : billableItem.expirationDate().getValue());
        return rentalItem;
    }

    private void fillAdjustmentsSection(LeaseApplicationDocumentDataAdjustmentsSectionDTO leaseApplicationDocumentDataAdjustmentsSectionDTO, Lease lease) {
        leaseApplicationDocumentDataAdjustmentsSectionDTO.adjustments().addAll(
                getAdjustmentsList(lease.currentTerm().version().leaseProducts().serviceItem(), lease.currentTerm().termFrom().getValue(), lease.currentTerm()
                        .termTo().getValue()));
        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {
            leaseApplicationDocumentDataAdjustmentsSectionDTO.adjustments().addAll(
                    getAdjustmentsList(feature, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue()));
        }
    }

    private Collection<LeaseApplicationDocumentDataAdjustmentDTO> getAdjustmentsList(BillableItem billableItem, LogicalDate from, LogicalDate to) {
        Collection<LeaseApplicationDocumentDataAdjustmentDTO> adjustments = new Vector<LeaseApplicationDocumentDataAdjustmentDTO>();
        String item = billableItem.item().name().getValue();
        for (BillableItemAdjustment currentAdjustment : billableItem.adjustments()) {
            LeaseApplicationDocumentDataAdjustmentDTO adjustment = EntityFactory.create(LeaseApplicationDocumentDataAdjustmentDTO.class);
            adjustment.item().setValue(item);
            adjustment.value().setValue(currentAdjustment.value().getStringView());
            adjustment.effectiveDate().setValue(currentAdjustment.effectiveDate().getValue() == null ? from : currentAdjustment.effectiveDate().getValue());
            adjustment.expirationDate().setValue(currentAdjustment.expirationDate().getValue() == null ? to : currentAdjustment.expirationDate().getValue());
            adjustments.add(adjustment);
        }
        return adjustments;
    }

    private void fillPeopleSection(LeaseApplicationDocumentDataPeopleSectionDTO peopleSection, Lease lease, LeaseTermParticipant<?> subjectParticipant) {
        EntityQueryCriteria<LeaseTermTenant> criteria = new EntityQueryCriteria<LeaseTermTenant>(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseTermV().holder(), lease.currentTerm());
        peopleSection.leaseId().setValue(lease.leaseApplication().applicationId().getValue());
        for (LeaseTermTenant leaseTermTenant : Persistence.service().query(criteria)) {
            if (leaseTermTenant.role().getValue() == Role.Dependent) {
                LeaseApplicationDocumentDataDependentDTO dependent = peopleSection.dependents().$();
                dependent.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getStringView());
                dependent.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getStringView());
                dependent.relationship().setValue(leaseTermTenant.relationship().getStringView());
                dependent.birthDate().setValue(leaseTermTenant.leaseParticipant().customer().person().birthDate().getValue());
                peopleSection.dependents().add(dependent);
            } else {
                LeaseApplicationDocumentDataCoApplicantDTO coapplicant = peopleSection.coApplicants().$();
                coapplicant.firstName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().firstName().getStringView());
                coapplicant.lastName().setValue(leaseTermTenant.leaseParticipant().customer().person().name().lastName().getStringView());
                coapplicant.relationship().setValue(
                        (leaseTermTenant.relationship().getStringView() == null) ? "" : leaseTermTenant.relationship().getStringView());
                coapplicant.birthDate().setValue(leaseTermTenant.leaseParticipant().customer().person().birthDate().getValue());
                peopleSection.coApplicants().add(coapplicant);
            }
        }
    }

    private void fillAboutYouSection(LeaseApplicationDocumentDataAboutYouSectionDTO aboutYou, Lease lease, LeaseTermParticipant<?> subjectParticipant,
            LogicalDate date, byte[] logo) {
        //header Information
        aboutYou.leaseId().setValue(lease.leaseApplication().applicationId().getValue());
        if (logo != null) {
            aboutYou.landlordLogo().setValue(new String(logo));
        }
        aboutYou.submissionDate().setValue(date);

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
                subjectParticipant.leaseParticipant().customer(), lease.unit().building());
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);
        for (IdentificationDocument id : screening.version().documents()) {
            LeaseApplicationDocumentDataIdentificationDocumentDTO idForPrint = EntityFactory
                    .create(LeaseApplicationDocumentDataIdentificationDocumentDTO.class);
            String documentType = id.idType().name().getValue() != null ? id.idType().name().getValue() : id.idType().type().getValue().toString();
            idForPrint.documentType().setValue(documentType);
            idForPrint.documentNumber().setValue(id.idNumber().getValue());
            aboutYou.identificationDocuments().add(idForPrint);
        }

    }

    private void fillAdditionalInfoSection(LeaseApplicationDocumentDataAdditionalInfoSectionDTO additionalInfo, Lease lease,
            LeaseTermParticipant<?> subjectParticipant) {
        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), lease.unit().building());

        additionalInfo.currentResidence().add(EntityFactory.create(LeaseApplicationDocumentDataResidenceDTO.class));

        fillResidence(additionalInfo.currentResidence().get(0), screening.version().currentAddress());
        if (!screening.version().previousAddress().isNull()) {
            LeaseApplicationDocumentDataResidenceDTO residence = EntityFactory.create(LeaseApplicationDocumentDataResidenceDTO.class);
            additionalInfo.previousResidences().add(residence);
            fillResidence(residence, screening.version().previousAddress());
        }

        Persistence.ensureRetrieve(screening.version().legalQuestions(), AttachLevel.Attached);
        for (CustomerScreeningLegalQuestion item : screening.version().legalQuestions()) {
            LeaseApplicationDocumentDataGeneralQuestionDTO question = EntityFactory.create(LeaseApplicationDocumentDataGeneralQuestionDTO.class);

            question.question().setValue(item.question().getValue());
            question.answerYes().setValue(item.answer().getValue() == Boolean.TRUE);
            question.answerNo().setValue(item.answer().getValue() == Boolean.FALSE);

            additionalInfo.generalQuestions().add(question);
        }
    }

    private void fillFinaincialSection(LeaseApplicationDocumentDataFinancialSectionDTO financialInfo, Lease lease, LeaseTermParticipant<?> subjectParticipant) {

        CustomerScreening screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(
                subjectParticipant.leaseParticipant().customer(), lease.unit().building());
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
        criteria.eq(criteria.proto().leaseTermV().holder(), lease.currentTerm());
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
        if (!address.country().isNull()) {
            residence.country().setValue(address.country().getValue().name);
        }

        residence.moveInDate().setValue(address.moveInDate().getValue());
        residence.moveOutDate().setValue(address.moveOutDate().getValue());
        residence.isOwned().setValue(address.rented().getValue() == OwnedRented.owned);
        residence.isRented().setValue(address.rented().getValue() == OwnedRented.rented);
    }

    private void fillEmergencyContacts(LeaseApplicationDocumentDataEmergencyContactsSectionDTO emergencyContacts, Lease lease,
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
            signedLegalTerms = retrieveOnlineApplication(application, subjectParticipant).legalTerms();
        }

        // convert for printing
        for (SignedOnlineApplicationLegalTerm signedLegalTerm : signedLegalTerms) {
            LeaseApplicationDocumentDataLegalTermDTO legalTerm = EntityFactory.create(LeaseApplicationDocumentDataLegalTermDTO.class);
            legalTerm.title().setValue(signedLegalTerm.term().title().getValue());
            legalTerm.wordingHtml().setValue(signedLegalTerm.term().content().getValue());
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

    private void fillFirstPaymentData(LeaseApplicationDocumentDataFirstPaymentSectionDTO firstPaymentSection, Lease lease) {

        BigDecimal total = new BigDecimal(0);
        BillDTO bill = retrieveBillData(lease);
        if (bill.serviceChargeLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.serviceChargeLineItems()));
            total = total.add(bill.serviceChargeLineItems().total().getValue());
        }
        if (bill.recurringFeatureChargeLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.recurringFeatureChargeLineItems()));
            total = total.add(bill.recurringFeatureChargeLineItems().total().getValue());
        }
        if (bill.onetimeFeatureChargeLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.onetimeFeatureChargeLineItems()));
            total = total.add(bill.onetimeFeatureChargeLineItems().total().getValue());
        }
        if (bill.productCreditLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.productCreditLineItems()));
            total = total.add(bill.productCreditLineItems().total().getValue());
        }
        if (bill.depositLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.depositLineItems()));
            total = total.add(bill.depositLineItems().total().getValue());
        }
        if (bill.depositRefundLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.depositRefundLineItems()));
            total = total.add(bill.depositRefundLineItems().total().getValue());
        }
        if (bill.immediateAccountAdjustmentLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.immediateAccountAdjustmentLineItems()));
            total = total.add(bill.immediateAccountAdjustmentLineItems().total().getValue());
        }
        if (bill.pendingAccountAdjustmentLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.pendingAccountAdjustmentLineItems()));
            total = total.add(bill.pendingAccountAdjustmentLineItems().total().getValue());
        }
        if (bill.previousChargeRefundLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.previousChargeRefundLineItems()));
            total = total.add(bill.previousChargeRefundLineItems().total().getValue());
        }
        if (bill.nsfChargeLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.nsfChargeLineItems()));
            total = total.add(bill.nsfChargeLineItems().total().getValue());
        }
        if (bill.withdrawalLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.withdrawalLineItems()));
            total = total.add(bill.withdrawalLineItems().total().getValue());
        }
        if (bill.rejectedPaymentLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.rejectedPaymentLineItems()));
            total = total.add(bill.rejectedPaymentLineItems().total().getValue());
        }
        if (bill.paymentLineItems() != null) {
            firstPaymentSection.lineItems().addAll(retrieveLineItems(bill.paymentLineItems()));
            total = total.add(bill.paymentLineItems().total().getValue());
        }

        firstPaymentSection.total().setValue(total);
    }

    private BillDTO retrieveBillData(Lease lease) {
        BillDTO billData = EntityFactory.create(BillDTO.class);

        if (VistaFeatures.instance().yardiIntegration()) {
            billData = BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(lease));
        } else {
            if (lease.status().getValue().isDraft() && Lease.Status.isApplicationUnitSelected(lease)) {
                // create bill preview for draft leases/applications:
                billData = BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(lease));
            } else if (lease.status().getValue().isCurrent()) {
                // get first bill for current leases:
                Bill bill = ServerSideFactory.create(BillingFacade.class).getBill(lease, 1);
                if (bill != null) {
                    billData = BillingUtils.createBillDto(bill);
                }
            }
        }

        return billData;
    }

    private OnlineApplication retrieveOnlineApplication(LeaseApplication application, LeaseTermParticipant<?> subjectParticipant) {
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
        criteria.eq(criteria.proto().masterOnlineApplication().leaseApplication(), application);
        criteria.eq(criteria.proto().customer(), subjectParticipant.leaseParticipant().customer());

        OnlineApplication onlineApplication = Persistence.service().retrieve(criteria);
        if (onlineApplication == null) {
            throw new RuntimeException("online application for application=" + "" + " customer=" + "" + " was not found, can't create printable legal terms");
        }
        return onlineApplication;
    }

    private Collection<LeaseApplicationDocumentDataFirstPaymentLineItemDTO> retrieveLineItems(InvoiceLineItemGroupDTO lineItemsGroup) {
        Collection<LeaseApplicationDocumentDataFirstPaymentLineItemDTO> lineItems = new Vector<LeaseApplicationDocumentDataFirstPaymentLineItemDTO>();
        for (InvoiceLineItem current : lineItemsGroup.lineItems()) {
            LeaseApplicationDocumentDataFirstPaymentLineItemDTO lineItem = EntityFactory.create(LeaseApplicationDocumentDataFirstPaymentLineItemDTO.class);
            lineItem.item().setValue(lineItemsGroup.getMeta().getCaption());
            lineItem.description().setValue(current.description().getValue());
            lineItem.amount().setValue(current.amount().getStringView());
            lineItems.add(lineItem);
        }
        return lineItems;
    }

    private int getTotalMonths(LogicalDate start, LogicalDate end) {
        return 12 * (end.getYear() - start.getYear()) + end.getMonth() - start.getMonth();
    }
}