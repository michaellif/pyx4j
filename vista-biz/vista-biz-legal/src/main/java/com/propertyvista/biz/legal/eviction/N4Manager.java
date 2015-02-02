/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author stanp
 */
package com.propertyvista.biz.legal.eviction;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.LongRange;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4.N4FieldsMapping;
import com.propertyvista.biz.legal.forms.n4cs.N4CSFieldsMapping;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.blob.EvictionDocumentBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4Data;
import com.propertyvista.domain.legal.n4.N4Data.TerminationDateOption;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.N4LeaseArrears;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.legal.n4.pdf.N4PdfFormData;
import com.propertyvista.domain.legal.n4.pdf.N4PdfLeaseData;
import com.propertyvista.domain.legal.n4.pdf.N4PdfRentOwingForPeriod;
import com.propertyvista.domain.legal.n4.pdf.N4PdfSignature;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfDocumentType.DocumentType;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfFormData;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfServiceMethod.ServiceMethod;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfSignature;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfToPersonInfo.ToType;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4Manager {
    private static final I18n i18n = I18n.get(N4Manager.class);

    private static final String N4_FORM_FILE = "n4.pdf";

    private static final String N4_CS_FORM_FILE = "n4cs.pdf";

    void issueN4ForBatchItem(N4BatchItem item, N4Policy policy, LogicalDate deliveryDate, Date generationDate) throws FormFillError {
        issueN4(item.batch(), item.leaseArrears(), policy, deliveryDate, generationDate, item.batch().name().getStringView());
    }

    void issueN4ForLease(N4Data n4data, N4LeaseArrears leaseArrears, N4Policy policy, LogicalDate deliveryDate, Date generationDate) throws FormFillError {
        issueN4(n4data, leaseArrears, policy, deliveryDate, generationDate, null);
    }

    // ------ internals -----------
    private void issueN4(N4Data n4data, N4LeaseArrears leaseArrears, N4Policy policy, LogicalDate deliveryDate, Date generationDate, String batchName)
            throws FormFillError {
        N4PdfLeaseData n4LeaseData = prepareN4LeaseData(n4data, leaseArrears, deliveryDate, policy);

        String note = batchName == null ? i18n.tr("Generated for Lease") : i18n.tr("Generated from N4 Batch: {0}", batchName);
        EvictionDocument n4Letter = generateN4Letter(prepareFormData(n4LeaseData, n4data), leaseArrears.lease(), generationDate, note);
        EvictionDocument n4csLetter = generateN4CSLetter(prepareN4CSData(n4LeaseData, n4data), leaseArrears.lease(), generationDate, note);

        // update N4 status with the new info
        EntityQueryCriteria<EvictionStatusN4> n4crit = EntityQueryCriteria.create(EvictionStatusN4.class);
        n4crit.eq(n4crit.proto().evictionCase().lease(), leaseArrears.lease());
        if (n4data instanceof N4Batch) {
            // N4Batch
            n4crit.eq(n4crit.proto().originatingBatch(), n4data);
        } else {
            // N4LeaseData
            n4crit.eq(n4crit.proto().n4Data(), n4data);
        }
        EvictionStatusN4 n4status = Persistence.service().retrieve(n4crit);
        n4status.terminationDate().set(n4LeaseData.terminationDate());
        // add status record
        note = batchName == null ? i18n.tr("N4 issued for Lease") : i18n.tr("N4 issued from Batch: {0}", batchName);
        ServerSideFactory.create(EvictionCaseFacade.class).addEvictionStatusDetails(n4status, note, Arrays.asList(n4Letter, n4csLetter));
        // set the doc references
        n4status.generatedForms().clear();
        n4status.generatedForms().add(n4Letter);
        n4status.generatedForms().add(n4csLetter);

        Persistence.service().persist(n4status);
    }

    private EvictionDocument generateN4Letter(N4PdfFormData n4FormData, Lease lease, Date generationDate, String note) {
        // generate N4
        EvictionDocumentBlob blob = EntityFactory.create(EvictionDocumentBlob.class);
        blob.data().setValue(generateN4Letter(n4FormData));
        blob.contentType().setValue("application/pdf");
        Persistence.service().persist(blob);

        EvictionDocument n4Letter = EntityFactory.create(EvictionDocument.class);
        n4Letter.title().setValue(i18n.tr("Lease Termination Notice - N4"));
        n4Letter.note().setValue(note);
        n4Letter.lease().set(lease);
        n4Letter.file().blobKey().setValue(blob.getPrimaryKey());
        n4Letter.file().fileSize().setValue(blob.data().getValue().length);
        n4Letter.file().fileName().setValue(MessageFormat.format( //
                "n4_{0}_{1}_{2,date,yyyy-MM-dd_HH-mm-ss}.pdf", //
                lease.unit().building().propertyCode().getValue(), //
                lease.unit().info().number().getValue(), //
                generationDate //
                ));
        n4Letter.printOrder().setValue(0);

        return n4Letter;
    }

    private EvictionDocument generateN4CSLetter(N4CSPdfFormData n4csFormData, Lease lease, Date generationDate, String note) {
        // generate Certificate of Service
        EvictionDocumentBlob csBlob = EntityFactory.create(EvictionDocumentBlob.class);
        csBlob.data().setValue(generateN4CSLetter(n4csFormData));
        csBlob.contentType().setValue("application/pdf");
        Persistence.service().persist(csBlob);

        EvictionDocument n4csLetter = EntityFactory.create(EvictionDocument.class);
        n4csLetter.title().setValue(i18n.tr("Certificate of Service"));
        n4csLetter.note().setValue(note);
        n4csLetter.lease().set(lease);
        n4csLetter.file().blobKey().setValue(csBlob.getPrimaryKey());
        n4csLetter.file().fileSize().setValue(csBlob.data().getValue().length);
        n4csLetter.file().fileName().setValue(MessageFormat.format( //
                "n4cs_{0}_{1}_{2,date,yyyy-MM-dd_HH-mm-ss}.pdf", //
                lease.unit().building().propertyCode().getValue(), //
                lease.unit().info().number().getValue(), //
                generationDate //
                ));
        n4csLetter.printOrder().setValue(1);

        return n4csLetter;
    }

    private N4PdfLeaseData prepareN4LeaseData(N4Data n4data, N4LeaseArrears leaseArrears, LogicalDate deliveryDate, N4Policy policy) {
        Persistence.ensureRetrieve(leaseArrears.lease(), AttachLevel.Attached);
        N4PdfLeaseData n4LeaseData = EntityFactory.create(N4PdfLeaseData.class);

        for (LeaseTermTenant termTenantIdStub : leaseArrears.lease().currentTerm().version().tenants()) {
            LeaseTermTenant termTenant = Persistence.service().retrieve(LeaseTermTenant.class, termTenantIdStub.getPrimaryKey());
            n4LeaseData.leaseTenants().add(termTenant.<LeaseTermTenant> createIdentityStub());
        }

        n4LeaseData.rentalUnitAddress().set(AddressRetriever.getUnitLegalAddress(leaseArrears.lease().unit()));
        if (TerminationDateOption.Calculate.equals(n4data.terminationDateOption().getValue())) {
            n4LeaseData.terminationDate().setValue(calculateTerminationDate(leaseArrears.lease(), deliveryDate, policy));
        }

        Persistence.ensureRetrieve(leaseArrears.unpaidCharges(), AttachLevel.Attached);
        n4LeaseData.rentOwingBreakdown().addAll(aggregateCharges(leaseArrears.unpaidCharges()));

        BigDecimal totalRentOwning = BigDecimal.ZERO;
        for (N4PdfRentOwingForPeriod rentOwingForPeriod : n4LeaseData.rentOwingBreakdown()) {
            totalRentOwning = totalRentOwning.add(rentOwingForPeriod.rentOwing().getValue());
        }
        n4LeaseData.totalRentOwning().setValue(totalRentOwning);

        Persistence.ensureRetrieve(leaseArrears.lease().unit().building().landlord(), AttachLevel.Attached);
        n4LeaseData.landlordName().setValue(leaseArrears.lease().unit().building().landlord().name().getValue());
        n4LeaseData.landlordAddress().setValue(leaseArrears.lease().unit().building().landlord().address().getValue());

        return n4LeaseData;
    }

    private N4PdfFormData prepareFormData(N4PdfLeaseData leaseData, N4Data batchData) throws FormFillError {
        N4PdfFormData fieldsData = EntityFactory.create(N4PdfFormData.class);
        fieldsData.to().setValue(formatTo(leaseData.leaseTenants(), leaseData.rentalUnitAddress()));
        fieldsData.from().setValue(formatFrom(leaseData.landlordName().getValue(), leaseData.landlordAddress()));

        fieldsData.rentalUnitAddress().streetNumber().setValue(leaseData.rentalUnitAddress().streetNumber().getStringView());
        fieldsData.rentalUnitAddress().streetName().setValue(leaseData.rentalUnitAddress().streetName().getStringView());
        fieldsData.rentalUnitAddress().streetType().setValue(leaseData.rentalUnitAddress().streetType().getStringView());
        fieldsData.rentalUnitAddress().direction().setValue(leaseData.rentalUnitAddress().streetDirection().getStringView());
        fieldsData.rentalUnitAddress().unit().setValue(leaseData.rentalUnitAddress().suiteNumber().getStringView());
        fieldsData.rentalUnitAddress().municipality().setValue(leaseData.rentalUnitAddress().city().getValue());
        fieldsData.rentalUnitAddress().postalCode().setValue(leaseData.rentalUnitAddress().postalCode().getValue());

        fieldsData.terminationDate().setValue(leaseData.terminationDate().getValue());
        fieldsData.totalRentOwed().setValue(leaseData.totalRentOwning().getValue());

        fieldsData.owedRent().rentOwingBreakdown().addAll(leaseData.rentOwingBreakdown());
        fieldsData.owedRent().totalRentOwing().setValue(leaseData.totalRentOwning().getValue());

        fieldsData.signature().signedBy().setValue(batchData.isLandlord().getValue(false) ? N4PdfSignature.SignedBy.Landlord : N4PdfSignature.SignedBy.Agent);
        fieldsData.signature().signature().setValue(retrieveSignature(batchData.signingAgent()));
        fieldsData.signature().signatureDate().setValue(batchData.signatureDate().getValue());

        fieldsData.landlordsContactInfo().firstName().setValue(batchData.signingAgent().name().firstName().getStringView());
        fieldsData.landlordsContactInfo().lastName().setValue(batchData.signingAgent().name().lastName().getStringView());
        fieldsData.landlordsContactInfo().companyName().setValue(batchData.companyLegalName().getStringView());

        fieldsData.landlordsContactInfo().mailingAddress().setValue(formatStreetAddress(batchData.companyAddress()));
        fieldsData.landlordsContactInfo().unit().setValue(batchData.companyAddress().suiteNumber().getValue());
        fieldsData.landlordsContactInfo().municipality().setValue(batchData.companyAddress().city().getValue());
        fieldsData.landlordsContactInfo().province()
                .setValue(ISOProvince.forName(batchData.companyAddress().province().getValue(), batchData.companyAddress().country().getValue()).code);
        fieldsData.landlordsContactInfo().postalCode().setValue(batchData.companyAddress().postalCode().getValue());

        fieldsData.landlordsContactInfo().phoneNumber().setValue(batchData.phoneNumber().getValue());
        fieldsData.landlordsContactInfo().faxNumber().setValue(batchData.faxNumber().getValue());
        fieldsData.landlordsContactInfo().email().setValue(batchData.emailAddress().getValue());

        return fieldsData;
    }

    private byte[] generateN4Letter(N4PdfFormData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4Manager.class.getResourceAsStream(N4_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4FieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    private N4CSPdfFormData prepareN4CSData(N4PdfLeaseData leaseData, N4Data batchData) {

        N4CSPdfFormData n4cs = EntityFactory.create(N4CSPdfFormData.class);
        n4cs.reporter().setValue(batchData.servicingAgent().name().getStringView());
        n4cs.document().termination().setValue("N4");
        n4cs.document().docType().setValue(DocumentType.TT);

        n4cs.street().setValue(formatStreetAddress(leaseData.rentalUnitAddress()));
        n4cs.unit().setValue(leaseData.rentalUnitAddress().suiteNumber().getStringView());
        n4cs.municipality().setValue(leaseData.rentalUnitAddress().city().getValue());
        n4cs.postalCode().setValue(leaseData.rentalUnitAddress().postalCode().getValue());

        n4cs.issueDate().setValue(SystemDateManager.getLogicalDate());

        n4cs.signature().signedBy().setValue(N4CSPdfSignature.SignedBy.RA);
        n4cs.signature().signature().setValue(retrieveSignature(batchData.servicingAgent()));
        n4cs.signature().firstname().setValue(batchData.servicingAgent().name().firstName().getStringView());
        n4cs.signature().lastname().setValue(batchData.servicingAgent().name().lastName().getStringView());

        n4cs.signature().phone().setValue(batchData.phoneNumberCS().getValue());
        n4cs.signature().signatureDate().setValue(batchData.signatureDate().getValue());

        n4cs.passedTo().tpType().setValue(ToType.Tenant);
        n4cs.passedTo().name().setValue(formatTo(leaseData.leaseTenants(), leaseData.rentalUnitAddress()));

        if (!batchData.deliveryMethod().isNull()) {
            n4cs.service().method().setValue(getServiceMethod(batchData.deliveryMethod().getValue()));
        }
        n4cs.service().lastAddr().setValue(formatLegalAddress(leaseData.rentalUnitAddress()));

        return n4cs;

    }

    private byte[] generateN4CSLetter(N4CSPdfFormData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4Manager.class.getResourceAsStream(N4_CS_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4CSFieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    private String formatTo(IList<LeaseTermTenant> leaseTenants, LegalAddress rentalUnitAddress) {
        StringBuilder toField = new StringBuilder();
        toField.append(formatRecipients(leaseTenants));
        toField.append("\n");
        toField.append(formatRentalAddress(rentalUnitAddress));
        return toField.toString().toUpperCase(Locale.CANADA);
    }

    private String formatRecipients(Iterable<LeaseTermTenant> tenants) {
        StringBuilder lineBuilder = new StringBuilder();
        // include all responsible
        for (LeaseTermTenant tenantIdStub : tenants) {
            LeaseTermTenant tenant = Persistence.service().retrieve(LeaseTermTenant.class, tenantIdStub.getPrimaryKey());
            Persistence.ensureRetrieve(tenant.leaseParticipant(), AttachLevel.Attached);
            if (Role.resposible().contains(tenant.role().getValue())) {
                if (lineBuilder.length() > 0) {
                    lineBuilder.append(", ");
                }

                String firstName = tenant.leaseParticipant().customer().person().name().firstName().getStringView();
                String lastName = tenant.leaseParticipant().customer().person().name().lastName().getStringView();
                String formattedName = firstName + " " + lastName;
                lineBuilder.append(formattedName);
            }
        }
        return lineBuilder.toString();
    }

    /** Try to format according to Canada Post guidelines: <Suite>-<StreetNo> <StreetName> */
    private String formatRentalAddress(LegalAddress address) {
        StringBuilder formattedAddress = new StringBuilder();
        formattedAddress.append(formatLegalAddress(address));
        formattedAddress.append("\n");
        formattedAddress.append(address.city().getValue());
        formattedAddress.append(" ");
        formattedAddress.append(ISOProvince.forName(address.province().getValue(), address.country().getValue()).code);
        formattedAddress.append("  ");
        formattedAddress.append(address.postalCode().getValue());
        return formattedAddress.toString();
    }

    private String formatFrom(String name, InternationalAddress address) {
        return SimpleMessageFormat.format("{0}\n{1}", name, address.getStringView());
    }

    private String formatLegalAddress(LegalAddress address) {
        return SimpleMessageFormat.format( //
                "{0,choice,null#|!null#{0}-}{1} {2}{3,choice,null#|!null# {3}}{4,choice,null#|!null# {4}}", //
                sanitzeSuiteNumber(address.suiteNumber().getValue("")), //
                address.streetNumber().getValue(), //
                address.streetName().getValue(), //
                address.streetType().getValue(), //
                address.streetDirection().getValue() //
                );
    }

    private String formatStreetAddress(InternationalAddress address) {
        return SimpleMessageFormat.format( //
                "{0,choice,null#|!null#{0}-}{1} {2}", //
                sanitzeSuiteNumber(address.suiteNumber().getValue("")), //
                address.streetNumber().getValue(), //
                address.streetName().getValue() //
                );
    }

    private String sanitzeSuiteNumber(String suiteNumber) {
        return suiteNumber.replaceFirst("^[^\\d]*", ""); // remove all non starting digits, i.e. "Suite, Apt, APARTMENT, #" etc.
    }

    private List<N4PdfRentOwingForPeriod> aggregateCharges(List<N4UnpaidCharge> charges) {
        List<N4PdfRentOwingForPeriod> result = new ArrayList<>();
        Map<LongRange, N4PdfRentOwingForPeriod> owingMap = new HashMap<>();
        for (N4UnpaidCharge charge : charges) {
            LongRange period = new LongRange(charge.fromDate().getValue().getTime(), charge.toDate().getValue().getTime());
            N4PdfRentOwingForPeriod owing = owingMap.get(period);
            if (owing == null) {
                owing = EntityFactory.create(N4PdfRentOwingForPeriod.class);
                owing.fromDate().set(charge.fromDate());
                owing.toDate().set(charge.toDate());
                owing.rentCharged().set(charge.rentCharged());
                owing.rentPaid().set(charge.rentPaid());
                owing.rentOwing().set(charge.rentOwing());
                owingMap.put(period, owing);
            } else {
                owing.rentCharged().setValue(owing.rentCharged().getValue().add(charge.rentCharged().getValue()));
                owing.rentPaid().setValue(owing.rentPaid().getValue().add(charge.rentPaid().getValue()));
                owing.rentOwing().setValue(owing.rentOwing().getValue().add(charge.rentOwing().getValue()));
            }
        }
        // sort by period
        List<LongRange> keys = new ArrayList<>(owingMap.keySet());
        Collections.sort(keys, new Comparator<LongRange>() {
            @Override
            public int compare(LongRange o1, LongRange o2) {
                return (int) (o2.getMaximumLong() - o1.getMaximumLong());
            }
        });
        // we only accept the max of 3 entries in the result, so combine the all but last 2 into one
        for (int i = 0; i < keys.size(); i++) {
            LongRange key = keys.get(i);
            N4PdfRentOwingForPeriod item = owingMap.get(key);
            if (i == 0 || i >= keys.size() - 2) {
                result.add(item);
            } else {
                N4PdfRentOwingForPeriod base = result.get(0);
                // extend period if needed and combine amounts
                if (base.fromDate().isNull() || base.fromDate().getValue().after(item.fromDate().getValue())) {
                    base.fromDate().set(item.fromDate());
                }
                if (base.toDate().isNull() || base.toDate().getValue().before(item.toDate().getValue())) {
                    base.toDate().set(item.toDate());
                }
                base.rentCharged().setValue(base.rentCharged().getValue().add(item.rentCharged().getValue()));
                base.rentPaid().setValue(base.rentPaid().getValue().add(item.rentPaid().getValue()));
                base.rentOwing().setValue(base.rentOwing().getValue().add(item.rentOwing().getValue()));
            }
        }
        return result;
    }

    LogicalDate calculateDeliveryDate(Date noticeDate, N4DeliveryMethod deliveryMethod, N4Policy policy) {
        if (deliveryMethod == null) {
            return null;
        }

        int advanceDays = terminationAdvanceDaysForDeliveryMethod(deliveryMethod, policy);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(noticeDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, advanceDays);
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate calculateTerminationDate(Lease lease, LogicalDate deliveryDate, N4Policy policy) {
        if (deliveryDate == null) {
            return null;
        }

        int advanceDays = terminationAdvanceDaysForLeaseType(lease, policy);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(deliveryDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, advanceDays);
        return new LogicalDate(cal.getTime());
    }

    private int terminationAdvanceDaysForDeliveryMethod(N4DeliveryMethod deliveryMethod, N4Policy policy) {
        switch (deliveryMethod) {
        case Hand:
            return policy.handDeliveryAdvanceDays().getValue();
        case Mail:
            return policy.mailDeliveryAdvanceDays().getValue();
        case Courier:
            return policy.courierDeliveryAdvanceDays().getValue();
        default:
            throw new RuntimeException("Unknown delivery method: " + deliveryMethod);
        }

    }

    private ServiceMethod getServiceMethod(N4DeliveryMethod deliveryMethod) {
        switch (deliveryMethod) {
        case Hand:
            return ServiceMethod.H;
        case Mail:
            return ServiceMethod.M;
        case Courier:
            return ServiceMethod.C;
        default:
            return null;
        }
    }

    // TODO - this value may depend on lease term (month-to-month vs 12 months)
    private int terminationAdvanceDaysForLeaseType(Lease leaseId, N4Policy policy) {
        return policy.terminationDateAdvanceDaysLongRentPeriod().getValue();
    }

    /** Retrieves Employee's signature image from the db or returns <code>null</code> if the employee hasn't uploaded a signature image */
    private byte[] retrieveSignature(Employee signingEmployee) {
        if (!signingEmployee.signature().isNull()) {
            EmployeeSignature signature = Persistence.service().retrieve(EmployeeSignature.class, signingEmployee.signature().getPrimaryKey());
            EmployeeSignatureBlob signatureBlob = Persistence.service().retrieve(EmployeeSignatureBlob.class, signature.file().blobKey().getValue());
            return signatureBlob.data().getValue();
        } else {
            return null;
        }
    }
}
