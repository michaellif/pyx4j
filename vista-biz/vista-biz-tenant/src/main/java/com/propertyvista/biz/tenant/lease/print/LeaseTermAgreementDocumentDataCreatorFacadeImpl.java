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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class LeaseTermAgreementDocumentDataCreatorFacadeImpl implements LeaseTermAgreementDocumentDataCreatorFacade {

    @Override
    public LeaseAgreementDocumentDataDTO createAgreementData(LeaseTerm leaseTerm) {
        Persistence.service().retrieve(leaseTerm.lease());
        Persistence.service().retrieve(leaseTerm.version().tenants());
        Persistence.service().retrieve(leaseTerm.version().guarantors());
        Persistence.service().retrieve(leaseTerm.version().agreementLegalTerms());
        Persistence.service().retrieve(leaseTerm.version().utilities());

        LeaseAgreementDocumentDataDTO leaseAgreementData = EntityFactory.create(LeaseAgreementDocumentDataDTO.class);
        leaseAgreementData.landlordName().setValue("TODO Landlord Name");
        leaseAgreementData.landlordAddress().setValue("TODO Landlord Address");

        leaseAgreementData.applicants().addAll(makeApplicants(leaseTerm));
        leaseAgreementData.terms().add(makeOccupantsTerm(leaseTerm));
        leaseAgreementData.terms().add(makePremisesTerm(AddressRetriever.getUnitLegalAddress(leaseTerm.lease().unit())));
        leaseAgreementData.terms().add(makeTermTerm(leaseTerm));
        leaseAgreementData.terms().add(makeRentTerm(leaseTerm));
        leaseAgreementData.terms().addAll(makeTermsForPrint(leaseTerm));
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
            if (tenant.role().getValue() != Role.Applicant || tenant.role().getValue() != Role.CoApplicant) {
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
            }
        }
        return agreementApplicants;
    }

    private Collection<? extends LeaseAgreementDocumentLegalTerm4PrintDTO> makeTermsForPrint(LeaseTerm leaseTerm) {
        List<LeaseAgreementDocumentLegalTerm4PrintDTO> legalTerms4Print = new LinkedList<LeaseAgreementDocumentLegalTerm4PrintDTO>();
        for (AgreementLegalTerm legalTerm : leaseTerm.version().agreementLegalTerms()) {
            legalTerms4Print.add(makeTermForPrint(legalTerm));
        }
        return legalTerms4Print;
    }

    private LeaseAgreementDocumentLegalTerm4PrintDTO makeTermForPrint(AgreementLegalTerm legalTerm) {
        LeaseAgreementDocumentLegalTerm4PrintDTO legalTerm4Print = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        legalTerm4Print.id().setValue(legalTerm.id().getValue());
        legalTerm4Print.title().setValue(legalTerm.title().getValue());
        legalTerm4Print.body().setValue(legalTerm.body().getValue());
        // TODO deal with signatures
        return legalTerm4Print;
    }
}
