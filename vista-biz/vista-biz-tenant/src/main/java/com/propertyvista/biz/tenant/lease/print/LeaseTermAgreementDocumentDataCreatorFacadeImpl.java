/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;

import com.propertyvista.domain.blob.LandlordMediaBlob;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseTermAgreementDocumentDataCreatorFacadeImpl implements LeaseTermAgreementDocumentDataCreatorFacade {

    @Override
    public LeaseAgreementDocumentDataDTO createAgreementData(LeaseTerm leaseTerm, LeaseTermAgreementSignaturesMode signaturesMode, boolean makeDraft) {
        Persistence.service().retrieve(leaseTerm.lease());
        Persistence.service().retrieve(leaseTerm.version().tenants());
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            Persistence.service().retrieveMember(tenant.agreementSignatures(), AttachLevel.Attached);
            if (tenant.agreementSignatures().isInstanceOf(AgreementDigitalSignatures.class)) {
                AgreementDigitalSignatures agreementSignatures = tenant.agreementSignatures().duplicate(AgreementDigitalSignatures.class);
                for (SignedAgreementLegalTerm legalTermSignature : agreementSignatures.legalTermsSignatures()) {
                    Persistence.ensureRetrieve(legalTermSignature.signature(), AttachLevel.Attached);
                }
                tenant.agreementSignatures().set(agreementSignatures);
            }
        }
        Persistence.service().retrieve(leaseTerm.version().guarantors());
        Persistence.service().retrieve(leaseTerm.version().agreementLegalTerms());
        Persistence.service().retrieve(leaseTerm.version().agreementConfirmationTerm());
        Persistence.service().retrieve(leaseTerm.version().utilities());

        LeaseAgreementDocumentDataDTO leaseAgreementData = EntityFactory.create(LeaseAgreementDocumentDataDTO.class);

        Persistence.ensureRetrieve(leaseTerm.lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.lease().unit().building().landlord(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.lease().unit().building().landlord().logo(), AttachLevel.Attached);

        leaseAgreementData.landlordName().setValue(leaseTerm.lease().unit().building().landlord().name().getValue());
        leaseAgreementData.landlordAddress().setValue(leaseTerm.lease().unit().building().landlord().address().getStringView());
        if (!leaseTerm.lease().unit().building().landlord().logo().isEmpty()) {
            Persistence.ensureRetrieve(leaseTerm.lease().unit().building().landlord().logo().file(), AttachLevel.Attached);
            LandlordMediaBlob blob = Persistence.service().retrieve(LandlordMediaBlob.class,
                    leaseTerm.lease().unit().building().landlord().logo().file().blobKey().getValue());
            leaseAgreementData.landlordLogo().setValue((blob.data().getValue()));
        }

        if (!makeDraft) {
            leaseAgreementData.applicants().addAll(makeApplicants(leaseTerm));
        }
        leaseAgreementData.terms().add(makeOccupantsTerm(leaseTerm));
        leaseAgreementData.terms().add(makePremisesTerm(AddressRetriever.getUnitLegalAddress(leaseTerm.lease().unit())));
        leaseAgreementData.terms().add(makeTermTerm(leaseTerm));
        leaseAgreementData.terms().add(makeRentTerm(leaseTerm));
        leaseAgreementData.terms().addAll(makeTermsForPrint(leaseTerm, !makeDraft ? signaturesMode : LeaseTermAgreementSignaturesMode.None));

        if (makeDraft) {
            byte[] watermarkBytes;
            InputStream is = null;
            try {
                is = LeaseTermAgreementDocumentDataCreatorFacadeImpl.class.getResourceAsStream("draft-watermark.png");
                watermarkBytes = IOUtils.toByteArray(is);
                leaseAgreementData.leaseAgreementBackground().setValue(watermarkBytes);
            } catch (IOException e) {
                throw new RuntimeException("failed to load draft background image", e);
            } finally {
                com.pyx4j.gwt.server.IOUtils.closeQuietly(is);
            }
        }
        return leaseAgreementData;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makePremisesTerm(AddressStructured premisesAddress) {
        LeaseAgreementDocumentLegalTerm4PrintDTO premisesTerm = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        StringBuilder premisesTermBody = new StringBuilder();
        premisesTermBody.append("The Landlord agrees to rent to the Tenant:<br>");
        premisesTermBody.append("Suite No: ").append(premisesAddress.suiteNumber().getValue()).append("<br>");

        String streetAddress = premisesAddress.streetNumber().getValue() + premisesAddress.streetNumberSuffix().getValue("");
        streetAddress += " " + premisesAddress.streetName().getValue();
        if (!premisesAddress.streetType().isNull()) {
            streetAddress += " " + premisesAddress.streetType().getValue().toString();
        }
        if (!premisesAddress.streetDirection().isNull()) {
            streetAddress += " " + premisesAddress.streetDirection().getValue().toString();
        }

        premisesTermBody.append("Address: ").append(streetAddress).append("<br>");
        premisesTermBody.append("City: ").append(premisesAddress.city().getValue()).append("<br>");
        premisesTermBody.append("Province: ").append(premisesAddress.province().name().getValue()).append("<br>");
        premisesTermBody.append("Postal Code: ").append(premisesAddress.postalCode().getValue()).append("<br>");

        premisesTerm.title().setValue("Premises / Rental Unit to be Rented");
        premisesTerm.body().setValue(premisesTermBody.toString());
        return premisesTerm;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeTermTerm(LeaseTerm leaseTerm) {
        LeaseAgreementDocumentLegalTerm4PrintDTO term4print = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        term4print.title().setValue("Term");
        String beginningDay = SimpleMessageFormat.format("{0,date,dd}", leaseTerm.lease().leaseFrom().getValue());
        String beginningMonth = SimpleMessageFormat.format("{0,date,MMMM}", leaseTerm.lease().leaseFrom().getValue());
        String beginningYear = SimpleMessageFormat.format("{0,date,YYYY}", leaseTerm.lease().leaseFrom().getValue());
        String endingDay = SimpleMessageFormat.format("{0,date,dd}", leaseTerm.lease().leaseTo().getValue());
        String endingMonth = SimpleMessageFormat.format("{0,date,MMMM}", leaseTerm.lease().leaseTo().getValue());
        String endingYear = SimpleMessageFormat.format("{0,date,YYYY}", leaseTerm.lease().leaseTo().getValue());
        term4print.body().setValue(
                "The Tenant shall occupy the Premises, subject to the present Tenant vacating, for a term beginning on the " + beginningDay + " day of "
                        + beginningMonth + " " + beginningYear + ", and ending on the " + endingDay + " day of " + endingMonth + " " + endingYear
                        + ", subject to the terms of this Agreement.");
        return term4print;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeRentTerm(LeaseTerm leaseTerm) {
        LeaseAgreementDocumentLegalTerm4PrintDTO term4print = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        term4print.title().setValue("Rent");

        StringBuilder termBuilder = new StringBuilder();
        termBuilder
                .append("The Tenant agrees to pay to the Landlord, at the Landlord’s office or such place as directed in writing from time to time by the Landlord:<br>");
        termBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        termBuilder
                .append(SimpleMessageFormat.format("Rent: ${0,number,#,##0.00}", leaseTerm.version().leaseProducts().serviceItem().agreedPrice().getValue()));

        termBuilder.append("<br>");
        if (!leaseTerm.version().leaseProducts().featureItems().isEmpty()) {
            termBuilder.append("<br>");
            for (BillableItem featureItem : leaseTerm.version().leaseProducts().featureItems()) {
                termBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                termBuilder.append(SimpleMessageFormat.format("{0} {1}: ${2,number,#,##0.00}", featureItem.item().name().getValue(), featureItem.description()
                        .getValue(), featureItem.agreedPrice().getValue()));

                if (!featureItem.effectiveDate().isNull() || !featureItem.expirationDate().isNull()) {
                    termBuilder.append(" (");
                }
                if (!featureItem.effectiveDate().isNull()) {
                    termBuilder.append(SimpleMessageFormat.format("Effective Date: {0,date,dd/MM/YYYY}", featureItem.effectiveDate().getValue()));
                }
                if (!featureItem.expirationDate().isNull()) {
                    if (!featureItem.expirationDate().isNull()) {
                        termBuilder.append(", ");
                    }
                    termBuilder.append(SimpleMessageFormat.format("Expiration Date: {0,date,dd/MM/YYYY}", featureItem.expirationDate().getValue()));
                }
                if (!featureItem.effectiveDate().isNull() || !featureItem.expirationDate().isNull()) {
                    termBuilder.append(")");
                }
                termBuilder.append("<br>");
            }
        }
        if (!leaseTerm.version().utilities().isEmpty()) {
            termBuilder.append("<br>");
            termBuilder.append("Included Utilities:<br>");
            for (BuildingUtility utility : leaseTerm.version().utilities()) {
                termBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(
                        SimpleMessageFormat.format(SimpleMessageFormat.format("{0}: {1} ({2})<br>", utility.type().getValue(), utility.name().getValue(),
                                utility.description().getValue())));
            }
        }
        term4print.body().setValue(termBuilder.toString());

        return term4print;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeOccupantsTerm(LeaseTerm leaseTerm) {
        LeaseAgreementDocumentLegalTerm4PrintDTO occupantsTerm = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        occupantsTerm.title().setValue("Occupants");
        StringBuilder occupantsTermBody = new StringBuilder();
        occupantsTermBody.append("It is understood and agreed that only the following persons shall occupy the Premises in addition to the Tenant(s):<br>");
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (tenant.role().getValue() != Role.Applicant && tenant.role().getValue() != Role.CoApplicant) {
                occupantsTermBody.append(tenant.leaseParticipant().customer().person().name().getStringView());
                occupantsTermBody.append("<br>");
            }
        }
        occupantsTerm.body().setValue(occupantsTermBody.toString());
        return occupantsTerm;
    }

    private List<LeaseAgreementDocumentLegalTermTenantDTO> makeApplicants(LeaseTerm leaseTerm) {
        List<LeaseAgreementDocumentLegalTermTenantDTO> agreementApplicants = new LinkedList<LeaseAgreementDocumentLegalTermTenantDTO>();
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (tenant.role().getValue() == Role.Applicant || tenant.role().getValue() == Role.CoApplicant) {
                LeaseAgreementDocumentLegalTermTenantDTO agreementTenant = EntityFactory.create(LeaseAgreementDocumentLegalTermTenantDTO.class);
                agreementTenant.fullName().setValue(tenant.leaseParticipant().customer().person().name().getStringView());
                agreementApplicants.add(agreementTenant);
            }
        }
        return agreementApplicants;
    }

    private Collection<? extends LeaseAgreementDocumentLegalTerm4PrintDTO> makeTermsForPrint(LeaseTerm leaseTerm,
            LeaseTermAgreementSignaturesMode signaturesMode) {
        List<LeaseAgreementDocumentLegalTerm4PrintDTO> legalTerms4Print = new LinkedList<LeaseAgreementDocumentLegalTerm4PrintDTO>();
        for (LeaseAgreementLegalTerm legalTerm : leaseTerm.version().agreementLegalTerms()) {
            legalTerms4Print.add(makeTermForPrint(leaseTerm, legalTerm, signaturesMode));
        }
        return legalTerms4Print;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeTermForPrint(LeaseTerm leaseTerm, LeaseAgreementLegalTerm legalTerm,
            LeaseTermAgreementSignaturesMode signaturesMode) {

        LeaseAgreementDocumentLegalTerm4PrintDTO legalTerm4Print = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        legalTerm4Print.id().setValue(legalTerm.id().getValue());
        legalTerm4Print.title().setValue(legalTerm.title().getValue());
        legalTerm4Print.body().setValue(legalTerm.body().getValue());

        List<LeaseTermParticipant<?>> leaseTermParticipants = new ArrayList<>();
        leaseTermParticipants.addAll(leaseTerm.version().tenants());
        leaseTermParticipants.addAll(leaseTerm.version().guarantors());

        for (LeaseTermParticipant<?> participant : leaseTermParticipants) {
            if (shouldSign(participant)) {
                switch (signaturesMode) {
                case SignaturesOnly: {
                    CustomerSignature tenantsSignature = getSignature(legalTerm, participant);
                    if (tenantsSignature != null) {
                        legalTerm4Print.signatures().add(tenantsSignature);
                    }
                    break;
                }

                case PlaceholdersOnly: {
                    if (needsPlaceholder(legalTerm.signatureFormat().getValue())) {
                        legalTerm4Print.signaturePlaceholders().add(makeSignaturePlaceholder(participant));
                    }
                    break;
                }

                case PlaceholdersAndAvailableSignatures: {
                    CustomerSignature tenantsSignature = getSignature(legalTerm, participant);
                    if (tenantsSignature != null) {
                        legalTerm4Print.signatures().add(tenantsSignature);
                    } else if (needsPlaceholder(legalTerm.signatureFormat().getValue())) {
                        legalTerm4Print.signaturePlaceholders().add(makeSignaturePlaceholder(participant));
                    }
                    break;
                }

                default: {
                    break;
                }
                }
            }

        }

        return legalTerm4Print;
    }

    private boolean shouldSign(LeaseTermParticipant<?> participant) {
        return participant.role().getValue() == Role.Applicant || participant.role().getValue() == Role.CoApplicant
                || participant.role().getValue() == Role.Guarantor;
    }

    private boolean needsPlaceholder(SignatureFormat signatureFormat) {
        return (signatureFormat == SignatureFormat.FullName || signatureFormat == SignatureFormat.AgreeBoxAndFullName);
    }

    private boolean isPrintableSignature(ISignature signature) {
        return signature.signatureFormat().getValue() == SignatureFormat.AgreeBoxAndFullName
                || signature.signatureFormat().getValue() == SignatureFormat.AgreeBoxAndFullName
                || signature.signatureFormat().getValue() == SignatureFormat.Initials;
    }

    private CustomerSignature getSignature(LeaseAgreementLegalTerm legalTerm, LeaseTermParticipant<?> participant) {
        if (!participant.agreementSignatures().isNull() && participant.agreementSignatures().isInstanceOf(AgreementDigitalSignatures.class)) {
            // find a signature that belongs to the term
            for (SignedAgreementLegalTerm signedLegalTerm : (participant.agreementSignatures().duplicate(AgreementDigitalSignatures.class)
                    .legalTermsSignatures())) {
                if (signedLegalTerm.term().getPrimaryKey().equals(legalTerm.getPrimaryKey())) {
                    if (isPrintableSignature(signedLegalTerm.signature())) {
                        CustomerSignature signature = signedLegalTerm.signature().duplicate(CustomerSignature.class);
                        String roleName = participant.role().getValue() == Role.Guarantor ? "Guarantor" : "Tenant";
                        signature.fullName().setValue(signature.fullName().getValue() + " (" + roleName + ")");
                        return signature;
                    }
                    break;
                }
            }
        }

        return null;
    }

    private LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO makeSignaturePlaceholder(LeaseTermParticipant<?> participant) {
        LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO signaturePlaceholder = EntityFactory
                .create(LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO.class);

        String roleName = participant.role().getValue() == Role.Guarantor ? "Guarantor" : "Tenant";
        String participantName = participant.leaseParticipant().customer().person().name().getStringView();

        signaturePlaceholder.tenantName().setValue(participantName + " (" + roleName + ")");

        return signaturePlaceholder;
    }
}
