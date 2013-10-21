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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.N4FormFieldsData.SignedBy;
import com.propertyvista.domain.legal.N4LandlordsData;
import com.propertyvista.domain.legal.N4LeaseData;
import com.propertyvista.domain.legal.N4RentOwingForPeriod;
import com.propertyvista.domain.legal.PdfFormFieldName;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.common.util.AddressRetriever;

public class N4GenerationFacadeImpl implements N4GenerationFacade {

    private static final String N4_FORM_FILE = "n4.pdf";

    @Override
    public byte[] generateN4Letter(N4FormFieldsData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(N4_FORM_FILE));
            filledForm = fillForm(formData, formTemplate);
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

        fieldsData.tenantStreetNumber().setValue(
                leaseData.rentalUnitAddress().streetNumber().getStringView() + leaseData.rentalUnitAddress().streetNumberSuffix().getStringView());
        fieldsData.tenantStreetName().setValue(leaseData.rentalUnitAddress().streetName().getStringView());
        fieldsData.tenantStreetType().setValue(leaseData.rentalUnitAddress().streetType().getStringView());
        fieldsData.tenantStreetDirection().setValue(leaseData.rentalUnitAddress().streetDirection().getStringView());
        fieldsData.tenantUnit().setValue(leaseData.rentalUnitAddress().suiteNumber().getStringView());
        fieldsData.tenantMunicipality().setValue(leaseData.rentalUnitAddress().city().getValue());
        fieldsData.tenantPostalCodeADA().setValue(leaseData.rentalUnitAddress().postalCode().getValue().substring(0, 3));
        fieldsData.tenantPostalCodeDAD().setValue(leaseData.rentalUnitAddress().postalCode().getValue().substring(4, 7));

        // create a date in the following format: dd/MM/YYYY        
        String[] globalTerminationDate = N4GenerationUtils.splitDate(leaseData.terminationDate().getValue());
        fieldsData.terminationDateDD().setValue(globalTerminationDate[0]);
        fieldsData.terminationDateMM().setValue(globalTerminationDate[1]);
        fieldsData.terminationDateYYYY().setValue(globalTerminationDate[2]);

        String[] totalOwed = N4GenerationUtils.splitCurrency(leaseData.totalRentOwning().getValue());
        fieldsData.globalTotalOwedThousands().setValue(totalOwed[0]);
        fieldsData.globalTotalOwedHundreds().setValue(totalOwed[1]);
        fieldsData.globalTotalOwedCents().setValue(totalOwed[2]);

        if (leaseData.rentOwingBreakdown().size() >= 1) {
            N4RentOwingForPeriod rentOwningForPeriod = leaseData.rentOwingBreakdown().get(0);
            String[] owedFrom = N4GenerationUtils.splitDate(rentOwningForPeriod.from().getValue());
            fieldsData.owedFromDDA().setValue(owedFrom[0]);
            fieldsData.owedFromMMA().setValue(owedFrom[1]);
            fieldsData.owedFromYYYYA().setValue(owedFrom[2]);

            String[] owedTo = N4GenerationUtils.splitDate(rentOwningForPeriod.to().getValue());
            fieldsData.owedToDDA().setValue(owedTo[0]);
            fieldsData.owedToMMA().setValue(owedTo[1]);
            fieldsData.owedToYYYYA().setValue(owedTo[2]);

            String[] charged = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentCharged().getValue());
            fieldsData.rentChargedThousandsA().setValue(charged[0]);
            fieldsData.rentChargedHundredsA().setValue(charged[1]);
            fieldsData.rentChargedCentsA().setValue(charged[2]);

            String[] paid = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentPaid().getValue());
            fieldsData.rentPaidThousandsA().setValue(paid[0]);
            fieldsData.rentPaidHundredsA().setValue(paid[1]);
            fieldsData.rentPaidCentsA().setValue(paid[2]);

            String[] owing = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentOwing().getValue());
            fieldsData.rentOwingThousandsA().setValue(owing[0]);
            fieldsData.rentOwingHundredsA().setValue(owing[1]);
            fieldsData.rentOwingCentsA().setValue(owing[2]);
        }

        if (leaseData.rentOwingBreakdown().size() >= 2) {
            N4RentOwingForPeriod rentOwningForPeriod = leaseData.rentOwingBreakdown().get(1);
            String[] owedFrom = N4GenerationUtils.splitDate(rentOwningForPeriod.from().getValue());
            fieldsData.owedFromDDB().setValue(owedFrom[0]);
            fieldsData.owedFromMMB().setValue(owedFrom[1]);
            fieldsData.owedFromYYYYB().setValue(owedFrom[2]);

            String[] owedTo = N4GenerationUtils.splitDate(rentOwningForPeriod.to().getValue());
            fieldsData.owedToDDB().setValue(owedTo[0]);
            fieldsData.owedToMMB().setValue(owedTo[1]);
            fieldsData.owedToYYYYB().setValue(owedTo[2]);

            String[] charged = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentCharged().getValue());
            fieldsData.rentChargedThousandsB().setValue(charged[0]);
            fieldsData.rentChargedHundredsB().setValue(charged[1]);
            fieldsData.rentChargedCentsB().setValue(charged[2]);

            String[] paid = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentPaid().getValue());
            fieldsData.rentPaidThousandsB().setValue(paid[0]);
            fieldsData.rentPaidHundredsB().setValue(paid[1]);
            fieldsData.rentPaidCentsB().setValue(paid[2]);

            String[] owing = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentOwing().getValue());
            fieldsData.rentOwingThousandsB().setValue(owing[0]);
            fieldsData.rentOwingHundredsB().setValue(owing[1]);
            fieldsData.rentOwingCentsB().setValue(owing[2]);
        }
        if (leaseData.rentOwingBreakdown().size() == 3) {
            N4RentOwingForPeriod rentOwningForPeriod = leaseData.rentOwingBreakdown().get(2);
            String[] owedFrom = N4GenerationUtils.splitDate(rentOwningForPeriod.from().getValue());
            fieldsData.owedFromDDC().setValue(owedFrom[0]);
            fieldsData.owedFromMMC().setValue(owedFrom[1]);
            fieldsData.owedFromYYYYC().setValue(owedFrom[2]);

            String[] owedTo = N4GenerationUtils.splitDate(rentOwningForPeriod.to().getValue());
            fieldsData.owedToDDC().setValue(owedTo[0]);
            fieldsData.owedToMMC().setValue(owedTo[1]);
            fieldsData.owedToYYYYC().setValue(owedTo[2]);

            String[] charged = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentCharged().getValue());
            fieldsData.rentChargedThousandsC().setValue(charged[0]);
            fieldsData.rentChargedHundredsC().setValue(charged[1]);
            fieldsData.rentChargedCentsC().setValue(charged[2]);

            String[] paid = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentPaid().getValue());
            fieldsData.rentPaidThousandsC().setValue(paid[0]);
            fieldsData.rentPaidHundredsC().setValue(paid[1]);
            fieldsData.rentPaidCentsC().setValue(paid[2]);

            String[] owing = N4GenerationUtils.splitCurrency(rentOwningForPeriod.rentOwing().getValue());
            fieldsData.rentOwingThousandsC().setValue(owing[0]);
            fieldsData.rentOwingHundredsC().setValue(owing[1]);
            fieldsData.rentOwingCentsC().setValue(owing[2]);
        }
        fieldsData.rentOwingThousandsTotal().setValue(totalOwed[0]);
        fieldsData.rentOwingHundredsTotal().setValue(totalOwed[0]);
        fieldsData.rentOwingCentsTotal().setValue(totalOwed[0]);

        fieldsData.signedBy().setValue(landlordsData.isLandlord().isBooleanTrue() ? SignedBy.Landlord : SignedBy.Agent);
        fieldsData.signature().setValue(landlordsData.signature().getValue());

        fieldsData.signatureDate().setValue(SimpleMessageFormat.format("{0,date,dd/MM/YYYY}", landlordsData.signatureDate().getValue()));

        fieldsData.signatureFirstName().setValue(landlordsData.signingEmployee().name().firstName().getStringView());
        fieldsData.signatureLastName().setValue(landlordsData.signingEmployee().name().lastName().getStringView());
        fieldsData.signatureCompanyName().setValue(landlordsData.landlordsLegalName().getStringView());

        fieldsData.signatureAddress().setValue(landlordsData.landlordsAddress().street1().getValue());
        fieldsData.signatureUnit().setValue(landlordsData.landlordsAddress().street2().getValue());
        fieldsData.signatureMunicipality().setValue(landlordsData.landlordsAddress().city().getValue());
        fieldsData.signatureProvince().setValue(landlordsData.landlordsAddress().province().code().getStringView());
        fieldsData.signaturePostalCode().setValue(landlordsData.landlordsAddress().postalCode().getValue());

        String[] signaturPhoneNumber = N4GenerationUtils.splitPhoneNumber(landlordsData.landlordsPhoneNumber().getValue());
        fieldsData.signaturePhoneNumberAreaCode().setValue(signaturPhoneNumber[0]);
        fieldsData.signaturePhoneNumberCombA().setValue(signaturPhoneNumber[1]);
        fieldsData.signaturePhoneNumberCombB().setValue(signaturPhoneNumber[2]);

        if (!CommonsStringUtils.isEmpty(landlordsData.faxNumber().getValue())) {
            String[] signatureFaxNumber = N4GenerationUtils.splitPhoneNumber(landlordsData.faxNumber().getValue());
            fieldsData.signatureFaxNumberAreaCode().setValue(signatureFaxNumber[0]);
            fieldsData.signatureFaxNumberCombA().setValue(signatureFaxNumber[1]);
            fieldsData.signatureFaxNumberCombB().setValue(signatureFaxNumber[2]);
        }

        fieldsData.signatureEmailAddress().setValue(landlordsData.emailAddress().getValue());

        // add singuature image
        return fieldsData;
    }

    @Override
    public N4LeaseData prepareN4LeaseData(Lease leaseId, LogicalDate noticeDate) {
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
        cal.add(GregorianCalendar.DAY_OF_YEAR, noticeAdvanceDays);
        LogicalDate terminationDate = new LogicalDate(cal.getTime());

        n4LeaseData.terminationDate().setValue(terminationDate);

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        // TODO filter out non-rent related debits
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        n4LeaseData.rentOwingBreakdown().addAll(debitAggregator.debitsForPeriod(debitAggregator.aggregate(debits)));

        BigDecimal totalRentOwning = BigDecimal.ZERO;
        for (N4RentOwingForPeriod rentOwingForPeriod : n4LeaseData.rentOwingBreakdown()) {
            totalRentOwning = totalRentOwning.add(rentOwingForPeriod.rentOwing().getValue());
        }
        n4LeaseData.totalRentOwning().setValue(totalRentOwning);

        return n4LeaseData;
    }

    // TODO factor out this method 
    public byte[] fillForm(IEntity fieldsData, byte[] form) throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(form);
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields fields = stamper.getAcroFields();

        for (String memberName : fieldsData.getEntityMeta().getMemberNames()) {
            try {
                if (fieldsData.getMember(memberName).getValueClass().equals(String.class)) {
                    String value = fieldsData.getMember(memberName).isNull() ? "" : fieldsData.getMember(memberName).getValue().toString();
                    fields.setField(pdfFieldName(fieldsData, memberName), value);
                } else if (fieldsData.getMember(memberName).getValueClass().isEnum()) {
                    // this code should work the same as for sting but it was left here just to denote it's a different field type
                    String value = fieldsData.getMember(memberName).isNull() ? "" : fieldsData.getMember(memberName).getValue().toString();
                    fields.setField(pdfFieldName(fieldsData, memberName), value);

                } else if (fieldsData.getMember(memberName).getValueClass().isArray()) {
                    do {
                        if (fieldsData.getMember(memberName).isNull()) {
                            break;
                        }

                        List<FieldPosition> fieldPositions = fields.getFieldPositions(pdfFieldName(fieldsData, memberName));
                        if (fieldPositions == null || fieldPositions.size() == 0) {
                            break;
                        }

                        FieldPosition signaturePosition = fieldPositions.get(0);
                        PdfContentByte canvas = stamper.getOverContent(signaturePosition.page);
                        Image signature = Image.getInstance((byte[]) fieldsData.getMember(memberName).getValue());
                        signature.scaleAbsolute(signaturePosition.position.getWidth(), signaturePosition.position.getHeight());
                        signature.setAbsolutePosition(signaturePosition.position.getLeft(), signaturePosition.position.getBottom());
                        canvas.addImage(signature);
                    } while (false);

                }

            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        bos.close();
        return bos.toByteArray();
    }

    private String pdfFieldName(IEntity fieldsData, String memberName) throws NoSuchMethodException, SecurityException {
        Class<?> fieldsDataClass = fieldsData.getInstanceValueClass();
        Method member = fieldsDataClass.getDeclaredMethod(memberName, (Class<?>[]) null);
        PdfFormFieldName fieldName = member.getAnnotation(PdfFormFieldName.class);
        return fieldName != null ? fieldName.value() : memberName;
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
