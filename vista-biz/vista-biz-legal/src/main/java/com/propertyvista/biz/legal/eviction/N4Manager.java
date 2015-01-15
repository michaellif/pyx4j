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
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.legal.LeaseLegalFacade;
import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4.N4FieldsMapping;
import com.propertyvista.biz.legal.forms.n4cs.N4CSFieldsMapping;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.legal.LegalStatusN4;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.legal.n4.pdf.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.pdf.N4LeaseData;
import com.propertyvista.domain.legal.n4.pdf.N4RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.pdf.N4Signature;
import com.propertyvista.domain.legal.n4cs.N4CSDocumentType.DocumentType;
import com.propertyvista.domain.legal.n4cs.N4CSFormFieldsData;
import com.propertyvista.domain.legal.n4cs.N4CSServiceMethod.ServiceMethod;
import com.propertyvista.domain.legal.n4cs.N4CSSignature;
import com.propertyvista.domain.legal.n4cs.N4CSToPersonInfo.ToType;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4Manager {

    private static final String N4_FORM_FILE = "n4.pdf";

    private static final String N4_CS_FORM_FILE = "n4cs.pdf";

    void issueN4ForLease(N4BatchItem item, N4Batch batch, N4Policy policy, LogicalDate deliveryDate, Date generationDate) throws FormFillError {
        N4LeaseData n4LeaseData = prepareN4LeaseData(item, deliveryDate, policy);

        N4FormFieldsData n4FormData = prepareFormData(n4LeaseData, batch);
        N4CSFormFieldsData n4csFormData = prepareN4CSData(n4FormData, ServiceMethod.M);

        // generate N4
        LegalLetterBlob blob = EntityFactory.create(LegalLetterBlob.class);
        blob.data().setValue(generateN4Letter(n4FormData));
        blob.contentType().setValue("application/pdf");
        Persistence.service().persist(blob);

        N4LegalLetter n4Letter = EntityFactory.create(N4LegalLetter.class);
        n4Letter.lease().set(item.lease());
        n4Letter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
        n4Letter.terminationDate().setValue(n4LeaseData.terminationDate().getValue());
        n4Letter.generatedOn().setValue(generationDate);
        n4Letter.file().blobKey().setValue(blob.getPrimaryKey());
        n4Letter.file().fileSize().setValue(blob.data().getValue().length);
        n4Letter.file().fileName().setValue(MessageFormat.format( //
                "n4_{0}_{1}_{2,date,yyyy-MM-dd_HH-mm-ss}.pdf", //
                item.lease().unit().building().propertyCode().getValue(), //
                item.lease().unit().info().number().getValue(), //
                generationDate //
                ));
        Persistence.service().persist(n4Letter);

        // generate Certificate of Service
        LegalLetterBlob csBlob = EntityFactory.create(LegalLetterBlob.class);
        csBlob.data().setValue(generateN4CSLetter(n4csFormData));
        csBlob.contentType().setValue("application/pdf");
        Persistence.service().persist(csBlob);

        N4LegalLetter n4csLetter = EntityFactory.create(N4LegalLetter.class);
        n4csLetter.lease().set(item.lease());
        n4csLetter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
        n4csLetter.terminationDate().setValue(n4LeaseData.terminationDate().getValue());
        n4csLetter.generatedOn().setValue(generationDate);
        n4csLetter.file().blobKey().setValue(csBlob.getPrimaryKey());
        n4csLetter.file().fileSize().setValue(csBlob.data().getValue().length);
        n4csLetter.file().fileName().setValue(MessageFormat.format( //
                "n4cs_{0}_{1}_{2,date,yyyy-MM-dd_HH-mm-ss}.pdf", //
                item.lease().unit().building().propertyCode().getValue(), //
                item.lease().unit().info().number().getValue(), //
                generationDate //
                ));
        Persistence.service().persist(n4csLetter);

        // TODO - review eviction status concept
        if (false) {
            LegalStatusN4 n4Status = EntityFactory.create(LegalStatusN4.class);
            n4Status.status().setValue(Status.N4);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(generationDate);
            cal.add(GregorianCalendar.DAY_OF_YEAR, policy.expiryDays().getValue());
            n4Status.expiry().setValue(cal.getTime());
            n4Status.cancellationThreshold().setValue(policy.cancellationThreshold().getValue());
            n4Status.terminationDate().setValue(n4LeaseData.terminationDate().getValue());

            n4Status.notes().setValue("created via N4 notice batch");
            n4Status.setBy().set(EntityFactory.createIdentityStub(CrmUser.class, VistaContext.getCurrentUserPrimaryKey()));
            n4Status.setOn().setValue(generationDate);

            ServerSideFactory.create(LeaseLegalFacade.class).setLegalStatus(item.lease(), n4Status, Arrays.<LegalLetter> asList(n4Letter));
        }
    }

    // ------ internals -----------
    private N4LeaseData prepareN4LeaseData(N4BatchItem item, LogicalDate deliveryDate, N4Policy policy) {
        Persistence.ensureRetrieve(item.lease(), AttachLevel.Attached);
        N4LeaseData n4LeaseData = EntityFactory.create(N4LeaseData.class);

        for (LeaseTermTenant termTenantIdStub : item.lease().currentTerm().version().tenants()) {
            LeaseTermTenant termTenant = Persistence.service().retrieve(LeaseTermTenant.class, termTenantIdStub.getPrimaryKey());
            n4LeaseData.leaseTenants().add(termTenant.<LeaseTermTenant> createIdentityStub());
        }

        n4LeaseData.rentalUnitAddress().set(AddressRetriever.getUnitLegalAddress(item.lease().unit()));
        n4LeaseData.terminationDate().setValue(calculateTerminationDate(item.lease(), deliveryDate, policy));

        Persistence.ensureRetrieve(item.unpaidCharges(), AttachLevel.Attached);
        n4LeaseData.rentOwingBreakdown().addAll(aggregateCharges(item.unpaidCharges()));

        BigDecimal totalRentOwning = BigDecimal.ZERO;
        for (N4RentOwingForPeriod rentOwingForPeriod : n4LeaseData.rentOwingBreakdown()) {
            totalRentOwning = totalRentOwning.add(rentOwingForPeriod.rentOwing().getValue());
        }
        n4LeaseData.totalRentOwning().setValue(totalRentOwning);

        Persistence.ensureRetrieve(item.lease().unit().building().landlord(), AttachLevel.Attached);
        n4LeaseData.landlordName().setValue(item.lease().unit().building().landlord().name().getValue());
        n4LeaseData.landlordAddress().setValue(item.lease().unit().building().landlord().address().getValue());

        return n4LeaseData;
    }

    private N4FormFieldsData prepareFormData(N4LeaseData leaseData, N4Batch batchData) throws FormFillError {
        N4FormFieldsData fieldsData = EntityFactory.create(N4FormFieldsData.class);
        fieldsData.to().setValue(formatTo(leaseData.leaseTenants(), leaseData.rentalUnitAddress()));
        fieldsData.from().setValue(
                SimpleMessageFormat.format("{0}\n{1}", leaseData.landlordName().getValue(), formatBuildingOwnerAddress(leaseData.landlordAddress())));

        // TODO review this: refactor to eliminate unnecessary code duplication
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

        fieldsData.signature().signedBy().setValue(batchData.isLandlord().getValue(false) ? N4Signature.SignedBy.Landlord : N4Signature.SignedBy.Agent);
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

        fieldsData.landlordsContactInfo().phoneNumber().setValue(batchData.companyPhoneNumber().getValue());
        fieldsData.landlordsContactInfo().faxNumber().setValue(batchData.companyFaxNumber().getValue());

        fieldsData.landlordsContactInfo().email().setValue(batchData.companyEmailAddress().getValue());

        return fieldsData;
    }

    private byte[] generateN4Letter(N4FormFieldsData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4Manager.class.getResourceAsStream(N4_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4FieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    private N4CSFormFieldsData prepareN4CSData(N4FormFieldsData n4, ServiceMethod serviceMethod) {

        N4CSFormFieldsData n4cs = EntityFactory.create(N4CSFormFieldsData.class);
        n4cs.reporter().setValue(n4.landlordsContactInfo().firstName().getValue() + " " + n4.landlordsContactInfo().lastName().getValue());
        n4cs.document().termination().setValue("N4");
        n4cs.document().docType().setValue(DocumentType.TT);
        StringBuilder address = new StringBuilder();
        if (n4.rentalUnitAddress().streetNumber().getValue() != null && !n4.rentalUnitAddress().streetNumber().getValue().equals("")) {
            address.append(n4.rentalUnitAddress().streetNumber().getValue());
        }
        if (n4.rentalUnitAddress().streetName().getValue() != null && !n4.rentalUnitAddress().streetName().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().streetName().getValue());
        }
        if (n4.rentalUnitAddress().streetType().getValue() != null && !n4.rentalUnitAddress().streetType().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().streetType().getValue());
        }
        if (n4.rentalUnitAddress().direction().getValue() != null && !n4.rentalUnitAddress().direction().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().direction().getValue());
        }

        n4cs.street().setValue(address.toString());
        n4cs.unit().setValue(n4.rentalUnitAddress().unit().getValue());
        n4cs.municipality().setValue(n4.rentalUnitAddress().municipality().getValue());
        n4cs.postalCode().setValue(n4.rentalUnitAddress().postalCode().getValue());
        n4cs.issueDate().setValue(SystemDateManager.getLogicalDate());
        n4cs.signature().firstname().setValue(n4.landlordsContactInfo().firstName().getValue());
        n4cs.signature().lastname().setValue(n4.landlordsContactInfo().lastName().getValue());
        n4cs.signature().phone().setValue(n4.landlordsContactInfo().phoneNumber().getValue());
        n4cs.signature().signedBy().setValue(N4CSSignature.SignedBy.RA);
        n4cs.signature().signature().setValue(n4.signature().signature().getValue());
        n4cs.signature().signatureDate().setValue(SystemDateManager.getLogicalDate());
        n4cs.passedTo().tpType().setValue(ToType.Tenant);
        n4cs.passedTo().name().setValue(n4.to().getStringView());
        n4cs.service().method().setValue(serviceMethod);
        if (serviceMethod.equals(ServiceMethod.M)) {
            StringBuilder lastAddress = new StringBuilder(n4.rentalUnitAddress().unit().getValue());
            lastAddress.append(" - " + address);
            n4cs.service().lastAddr().setValue(lastAddress.toString());

        }

        return n4cs;

    }

    private byte[] generateN4CSLetter(N4CSFormFieldsData formData) {
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

    private String formatBuildingOwnerAddress(InternationalAddress address) {
        return address.getStringView(); // TODO maybe use same function as "format street address"
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

    private List<N4RentOwingForPeriod> aggregateCharges(List<N4UnpaidCharge> charges) {
        List<N4RentOwingForPeriod> result = new ArrayList<>();
        Map<LongRange, N4RentOwingForPeriod> owingMap = new HashMap<>();
        for (N4UnpaidCharge charge : charges) {
            LongRange period = new LongRange(charge.fromDate().getValue().getTime(), charge.toDate().getValue().getTime());
            N4RentOwingForPeriod owing = owingMap.get(period);
            if (owing == null) {
                owing = EntityFactory.create(N4RentOwingForPeriod.class);
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
            N4RentOwingForPeriod item = owingMap.get(key);
            if (i == 0 || i >= keys.size() - 2) {
                result.add(item);
            } else {
                N4RentOwingForPeriod base = result.get(0);
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

    LogicalDate calculateDeliveryDate(LogicalDate noticeDate, N4DeliveryMethod deliveryMethod, N4Policy policy) {
        int advanceDays = terminationAdvanceDaysForDeliveryMethod(deliveryMethod, policy);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(noticeDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, advanceDays);
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate calculateTerminationDate(Lease lease, LogicalDate deliveryDate, N4Policy policy) {
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
