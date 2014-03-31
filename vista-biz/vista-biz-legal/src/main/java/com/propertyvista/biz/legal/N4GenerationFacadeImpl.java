/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-09-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4.N4FieldsMapping;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.N4BatchData;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.legal.n4.N4Signature.SignedBy;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4GenerationFacadeImpl implements N4GenerationFacade {

    private static final int MAX_ADDRESS_BOX_LINE_LENGTH = 24; // only 24 captial 'M' letters can fit in to the 'to' box

    private static final String N4_FORM_FILE = "n4.pdf";

    private final InternalBillingInvoiceDebitFetcherImpl invoiceDebitFetcher;

    public N4GenerationFacadeImpl() {
        invoiceDebitFetcher = new InternalBillingInvoiceDebitFetcherImpl();
    }

    @Override
    public byte[] generateN4Letter(N4FormFieldsData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(N4_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4FieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    @Override
    public N4FormFieldsData prepareFormData(N4LeaseData leaseData, N4BatchData batchData) throws FormFillError {
        N4FormFieldsData fieldsData = EntityFactory.create(N4FormFieldsData.class);
        fieldsData.to().setValue(formatTo(leaseData.leaseTenants(), leaseData.rentalUnitAddress()));
        validateToField(fieldsData.to().getValue());
        fieldsData.from().setValue(
                SimpleMessageFormat.format("{0}\n{1}", leaseData.landlordName().getValue(), formatBuildingOwnerAddress(leaseData.landlordAddress())));

        // TODO review this: refactor to eliminate unnecessary code duplication
        fieldsData.rentalUnitAddress().streetNumber()
                .setValue(leaseData.rentalUnitAddress().streetNumber().getStringView() + leaseData.rentalUnitAddress().streetNumberSuffix().getStringView());
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

        fieldsData.signature().signedBy().setValue(batchData.isLandlord().getValue(false) ? SignedBy.Landlord : SignedBy.Agent);
        fieldsData.signature().signature().setValue(batchData.signature().getValue());
        fieldsData.signature().signatureDate().setValue(batchData.signatureDate().getValue());

        fieldsData.landlordsContactInfo().firstName().setValue(batchData.signingEmployee().name().firstName().getStringView());
        fieldsData.landlordsContactInfo().lastName().setValue(batchData.signingEmployee().name().lastName().getStringView());
        fieldsData.landlordsContactInfo().companyName().setValue(batchData.companyLegalName().getStringView());

        fieldsData.landlordsContactInfo().mailingAddress().setValue(getStreetAddress(batchData.companyAddress()));
        fieldsData.landlordsContactInfo().unit().setValue(batchData.companyAddress().suiteNumber().getValue());
        fieldsData.landlordsContactInfo().municipality().setValue(batchData.companyAddress().city().getValue());
        fieldsData.landlordsContactInfo().province().setValue(batchData.companyAddress().province().code().getStringView());
        fieldsData.landlordsContactInfo().postalCode().setValue(batchData.companyAddress().postalCode().getValue());

        fieldsData.landlordsContactInfo().phoneNumber().setValue(batchData.companyPhoneNumber().getValue());
        fieldsData.landlordsContactInfo().faxNumber().setValue(batchData.companyFaxNumber().getValue());

        fieldsData.landlordsContactInfo().email().setValue(batchData.companyEmailAddress().getValue());

        return fieldsData;
    }

    @Override
    public N4LeaseData prepareN4LeaseData(Lease leaseId, LogicalDate noticeDate, N4DeliveryMethod deliveryMethod, Collection<ARCode> acceptableArCodes) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        N4LeaseData n4LeaseData = EntityFactory.create(N4LeaseData.class);

        for (LeaseTermTenant termTenantIdStub : lease.currentTerm().version().tenants()) {
            LeaseTermTenant termTenant = Persistence.service().retrieve(LeaseTermTenant.class, termTenantIdStub.getPrimaryKey());
            n4LeaseData.leaseTenants().add(termTenant.<LeaseTermTenant> createIdentityStub());
        }

        n4LeaseData.rentalUnitAddress().set(AddressRetriever.getUnitLegalAddress(lease.unit()));
        n4LeaseData.terminationDate().setValue(computeTerminationDate(lease, noticeDate, deliveryMethod));

        List<InvoiceDebit> filteredDebits = invoiceDebitFetcher.getInvoiceDebits(acceptableArCodes, lease.billingAccount(), SystemDateManager.getLogicalDate());
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        n4LeaseData.rentOwingBreakdown().addAll(debitAggregator.debitsForPeriod(debitAggregator.aggregate(filteredDebits)));

        BigDecimal totalRentOwning = BigDecimal.ZERO;
        for (RentOwingForPeriod rentOwingForPeriod : n4LeaseData.rentOwingBreakdown()) {
            totalRentOwning = totalRentOwning.add(rentOwingForPeriod.rentOwing().getValue());
        }
        n4LeaseData.totalRentOwning().setValue(totalRentOwning);

        Persistence.ensureRetrieve(lease.unit().building().landlord(), AttachLevel.Attached);
        n4LeaseData.landlordName().setValue(lease.unit().building().landlord().name().getValue());
        n4LeaseData.landlordAddress().setValue(lease.unit().building().landlord().address().getValue());

        return n4LeaseData;
    }

    private String formatTo(IList<LeaseTermTenant> leaseTenants, AddressStructured rentalUnitAddress) {
        StringBuilder toField = new StringBuilder();
        toField.append(formatTenants(leaseTenants));
        toField.append("\n");
        toField.append(formatRentalAddress(rentalUnitAddress));
        return toField.toString().toUpperCase(Locale.CANADA);
    }

    private String formatTenants(Iterable<LeaseTermTenant> tenants) {
        StringBuilder lineBuilder = new StringBuilder();
        for (LeaseTermTenant tenantIdStub : tenants) {
            LeaseTermTenant tenant = Persistence.service().retrieve(LeaseTermTenant.class, tenantIdStub.getPrimaryKey());
            Persistence.ensureRetrieve(tenant.leaseParticipant(), AttachLevel.Attached);
            if (tenant.role().getValue() == Role.Applicant /* TODO ? || tenant.role().getValue() == Role.CoApplicant */) {
                if (lineBuilder.length() > 0) {
                    lineBuilder.append(", ");
                }
                lineBuilder.append(tenant.leaseParticipant().customer().person().name().firstName().getValue() + " "
                        + tenant.leaseParticipant().customer().person().name().lastName().getValue());
            }
        }
        return lineBuilder.toString();
    }

    private String formatRentalAddress(AddressStructured address) {
        StringBuilder formattedAddress = new StringBuilder();
        formattedAddress.append(SimpleMessageFormat.format(//@formatter:off
               "{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,null#|!null# {4}}{5,choice,null#|!null# {5}}",
               sanitzeSuiteNumber(address.suiteNumber().getValue()),
               address.streetNumber().getValue(),
               address.streetNumberSuffix().getValue(),
               address.streetName().getValue(),
               (address.streetType().getValue() != null || address.streetType().getValue() == StreetType.other)? address.streetType().getValue().toAbbr() : null,
               address.streetDirection().getValue() != null ? address.streetDirection().getValue().toAbbr() : null
       ));//@formatter:on
        formattedAddress.append("\n");
        formattedAddress.append(address.city().getValue());
        formattedAddress.append(" ");
        formattedAddress.append(address.province().code().getValue());
        formattedAddress.append("  ");
        formattedAddress.append(address.postalCode().getValue());
        return formattedAddress.toString();
    }

    private String sanitzeSuiteNumber(String suiteNumber) {
        return suiteNumber.replaceFirst("^[^\\d]*", ""); // remove all non starting digits, i.e. "Suite, Apt, APARTMENT, #" etc.
    }

    private String formatBuildingOwnerAddress(AddressStructured address) {
        return address.getStringView();
    }

    private LogicalDate computeTerminationDate(Lease lease, LogicalDate noticeDate, N4DeliveryMethod deliveryMethod) {
        int advanceDays = terminationAdvanceDaysForDeliveryMethod(deliveryMethod, lease) + terminationAdvanceDaysForLeaseType(lease);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(noticeDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, advanceDays);
        LogicalDate terminationDate = new LogicalDate(cal.getTime());

        return terminationDate;
    }

    private int terminationAdvanceDaysForDeliveryMethod(N4DeliveryMethod deliveryMethod, Lease lease) {
        N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), N4Policy.class);
        if (policy == null) {
            throw new RuntimeException("Failed to compute n4 termination date advance days for lease '" + lease.getPrimaryKey() + "': N4 Policy wasn't found");
        }
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

    private int terminationAdvanceDaysForLeaseType(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), N4Policy.class);

        // TODO this is value for Yearly or Month-to-Month lease (we don't have other kinds of leases therefore it's fine now, but later can become a problem)
        return policy.terminationDateAdvanceDaysLongRentPeriod().getValue();
    }

    private String getStreetAddress(AddressStructured companyAddress) {
        return new AddressConverter.StructuredToSimpleAddressConverter().getStreetAddress(companyAddress);
    }

    // We can fit to this field only three lines 
    private void validateToField(String toFieldString) throws FormFillError {
        boolean checkFailed = false;
        for (String line : toFieldString.split("\n")) {
            if (line.length() > MAX_ADDRESS_BOX_LINE_LENGTH) {
                checkFailed = true;
                break;
            }
        }
        if (checkFailed) {
            throw new FormFillError("'to' field of N4 form won't fit the the box (line length exceeds maximum):\n" + toFieldString);
        }
    }
}
