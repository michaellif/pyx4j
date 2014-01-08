/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.server.common.util.AddressRetriever;

@SuppressWarnings("serial")
class LeaseTermAgreementPrinterDeferredProcess extends AbstractDeferredProcess {

    private final LeaseTerm leaseTerm;

    private final LeaseTermAgreementDocument agreementDocument;

    public LeaseTermAgreementPrinterDeferredProcess(LeaseTerm leaseTerm) {
        super();
        this.leaseTerm = leaseTerm;
        this.agreementDocument = EntityFactory.create(LeaseTermAgreementDocument.class);
        this.agreementDocument.leaseTermV().set(this.leaseTerm.version());
    }

    @Override
    public void execute() {
        //TODO Do actual blob creation

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    byte[] agreementPdf = LeaseTermAgreementPdfCreator.createPdf(makeAgreementData(leaseTerm));
                    saveBlob(agreementPdf);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

        });

    }

    private void saveBlob(byte[] bytes) {
        LeaseTermAgreementDocumentBlob blob = EntityFactory.create(LeaseTermAgreementDocumentBlob.class);
        blob.data().setValue(bytes);
        blob.contentType().setValue(MimeMap.getContentType(DownloadFormat.PDF));
        Persistence.service().persist(blob);
        agreementDocument.file().fileName().setValue("agreement.pdf");
        agreementDocument.file().fileSize().setValue(bytes.length);
        agreementDocument.file().blobKey().set(blob.id());
        FileUploadRegistry.register(agreementDocument.file());
        Persistence.service().persist(agreementDocument);
    }

    private LeaseAgreementData makeAgreementData(LeaseTerm leaseTerm) {
        Persistence.service().retrieve(leaseTerm.lease());
        Persistence.service().retrieve(leaseTerm.version().tenants());
        Persistence.service().retrieve(leaseTerm.version().agreementLegalTerms());

        LeaseAgreementData leaseAgreementData = EntityFactory.create(LeaseAgreementData.class);
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

    private AgreementLegalTerm4Print makePremisesTerm(AddressStructured premisesAddress) {
        AgreementLegalTerm4Print premisesTerm = EntityFactory.create(AgreementLegalTerm4Print.class);
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

    private AgreementLegalTerm4Print makeTermTerm(LeaseTerm leaseTerm) {
        AgreementLegalTerm4Print term4print = EntityFactory.create(AgreementLegalTerm4Print.class);
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

    private AgreementLegalTerm4Print makeRentTerm(LeaseTerm leaseTerm) {
        AgreementLegalTerm4Print term4print = EntityFactory.create(AgreementLegalTerm4Print.class);
        term4print.title().setValue("Rent");
        term4print.body().setValue(
                "The Tenant agrees to pay to the Landlord, at the Landlordís office or such place as directed in writing from time to time by the Landlord:<br>"
                        + "TODO<br>"); // TODO implement lease rent term
        return term4print;
    }

    private AgreementLegalTerm4Print makeOccupantsTerm(LeaseTerm leaseTerm) {
        AgreementLegalTerm4Print occupantsTerm = EntityFactory.create(AgreementLegalTerm4Print.class);
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

    private List<AgreementLegalTermTenant> makeApplicants(LeaseTerm leaseTerm) {
        List<AgreementLegalTermTenant> agreementApplicants = new LinkedList<AgreementLegalTermTenant>();
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (tenant.role().getValue() == Role.Applicant || tenant.role().getValue() == Role.CoApplicant) {
                AgreementLegalTermTenant agreementTenant = EntityFactory.create(AgreementLegalTermTenant.class);
                agreementTenant.fullName().setValue(tenant.leaseParticipant().customer().person().name().getStringView());
            }
        }
        return agreementApplicants;
    }

    private Collection<? extends AgreementLegalTerm4Print> makeTermsForPrint(LeaseTerm leaseTerm) {
        List<AgreementLegalTerm4Print> legalTerms4Print = new LinkedList<AgreementLegalTerm4Print>();
        for (AgreementLegalTerm legalTerm : leaseTerm.version().agreementLegalTerms()) {
            legalTerms4Print.add(makeTermForPrint(legalTerm));
        }
        return legalTerms4Print;
    }

    private AgreementLegalTerm4Print makeTermForPrint(AgreementLegalTerm legalTerm) {
        AgreementLegalTerm4Print legalTerm4Print = EntityFactory.create(AgreementLegalTerm4Print.class);
        legalTerm4Print.id().setValue(legalTerm.id().getValue());
        legalTerm4Print.title().setValue(legalTerm.title().getValue());
        legalTerm4Print.body().setValue(legalTerm.body().getValue());
        // TODO deal with signatures
        return legalTerm4Print;
    }

}
