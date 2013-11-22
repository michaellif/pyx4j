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

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4.N4FieldsMapping;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4LandlordsData;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.legal.n4.N4Signature.SignedBy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4GenerationFacadeImpl implements N4GenerationFacade {

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
    public N4FormFieldsData populateFormData(N4LeaseData leaseData, N4LandlordsData landlordsData) {
        N4FormFieldsData fieldsData = EntityFactory.create(N4FormFieldsData.class);
        fieldsData.to().setValue(
                SimpleMessageFormat.format("{0}\n{1}", formatTenants(leaseData.leaseTenants()), formatRentalAddress(leaseData.rentalUnitAddress())));
        fieldsData.from().setValue(
                SimpleMessageFormat.format("{0}\n{1}", landlordsData.landlordsLegalName().getValue(), formatLandlordAddress(landlordsData.landlordsAddress())));

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

        fieldsData.signature().signedBy().setValue(landlordsData.isLandlord().isBooleanTrue() ? SignedBy.Landlord : SignedBy.Agent);
        fieldsData.signature().signature().setValue(landlordsData.signature().getValue());
        fieldsData.signature().signatureDate().setValue(landlordsData.signatureDate().getValue());

        fieldsData.landlordsContactInfo().firstName().setValue(landlordsData.signingEmployee().name().firstName().getStringView());
        fieldsData.landlordsContactInfo().lastName().setValue(landlordsData.signingEmployee().name().lastName().getStringView());
        fieldsData.landlordsContactInfo().companyName().setValue(landlordsData.landlordsLegalName().getStringView());

        fieldsData.landlordsContactInfo().mailingAddress().setValue(landlordsData.landlordsAddress().street1().getValue());
        fieldsData.landlordsContactInfo().unit().setValue(landlordsData.landlordsAddress().street2().getValue());
        fieldsData.landlordsContactInfo().municipality().setValue(landlordsData.landlordsAddress().city().getValue());
        fieldsData.landlordsContactInfo().province().setValue(landlordsData.landlordsAddress().province().code().getStringView());
        fieldsData.landlordsContactInfo().postalCode().setValue(landlordsData.landlordsAddress().postalCode().getValue());

        fieldsData.landlordsContactInfo().phoneNumber().setValue(landlordsData.landlordsPhoneNumber().getValue());
        fieldsData.landlordsContactInfo().faxNumber().setValue(landlordsData.faxNumber().getValue());

        fieldsData.landlordsContactInfo().email().setValue(landlordsData.emailAddress().getValue());

        return fieldsData;
    }

    @Override
    public N4LeaseData prepareN4LeaseData(Lease leaseId, LogicalDate noticeDate, int terminationAdvanceDays, Collection<ARCode> acceptableArCodes) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        N4LeaseData n4LeaseData = EntityFactory.create(N4LeaseData.class);

        for (LeaseTermTenant termTenantIdStub : lease.currentTerm().version().tenants()) {
            LeaseTermTenant termTenant = Persistence.service().retrieve(LeaseTermTenant.class, termTenantIdStub.getPrimaryKey());
            n4LeaseData.leaseTenants().add(termTenant.leaseParticipant().<Tenant> createIdentityStub());
        }

        n4LeaseData.rentalUnitAddress().set(AddressRetriever.getUnitLegalAddress(lease.unit()));

        int noticeAdvanceDays = terminationDaysAdvance(lease);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(noticeDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, noticeAdvanceDays + terminationAdvanceDays);
        LogicalDate terminationDate = new LogicalDate(cal.getTime());

        n4LeaseData.terminationDate().setValue(terminationDate);

        List<InvoiceDebit> filteredDebits = invoiceDebitFetcher.getInvoiceDebits(acceptableArCodes, lease.billingAccount(), SystemDateManager.getLogicalDate());
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        n4LeaseData.rentOwingBreakdown().addAll(debitAggregator.debitsForPeriod(debitAggregator.aggregate(filteredDebits)));

        BigDecimal totalRentOwning = BigDecimal.ZERO;
        for (RentOwingForPeriod rentOwingForPeriod : n4LeaseData.rentOwingBreakdown()) {
            totalRentOwning = totalRentOwning.add(rentOwingForPeriod.rentOwing().getValue());
        }
        n4LeaseData.totalRentOwning().setValue(totalRentOwning);

        return n4LeaseData;
    }

    private String formatTenants(Iterable<Tenant> tenants) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tenant tenantIdStub : tenants) {
            Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantIdStub.getPrimaryKey());
            stringBuilder.append(tenant.customer().person().name().getStringView());
            stringBuilder.append("; ");
        }
        return stringBuilder.toString();
    }

    private String formatRentalAddress(AddressStructured address) {
        return address.getStringView();
    }

    private String formatLandlordAddress(AddressSimple address) {
        return address.getStringView();
    }

    private int terminationDaysAdvance(Lease lease) {
        if (isByMonthLease(lease) || isByYearLease(lease)) {
            return 14;
        } else if (isByDayLease(lease) || isByWeekLease(lease)) {
            return 7;
        } else {
            throw new IllegalArgumentException("lease must be either 'by year', 'by month', 'by week' or 'by day'");
        }
    }

    private boolean isByMonthLease(Lease lease) {
        return true; // TODO fix this
    }

    private boolean isByYearLease(Lease lease) {
        return true; // TODO fix this
    }

    private boolean isByDayLease(Lease lease) {
        return false; // TODO fix this
    }

    private boolean isByWeekLease(Lease lease) {
        return false; // TODO fix this
    }

}
