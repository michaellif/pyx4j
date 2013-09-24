/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
/** This maps directly to the fields of PDF template of N4 Form */
public interface N4FormFieldsData extends IEntity {

    /**
     * Tenant names and address
     */
    @PdfFormFieldName("Text1")
    IPrimitive<String> to();

    /**
     * Landlord's name.
     */
    @PdfFormFieldName("Text2")
    IPrimitive<String> from();

    // ADDRESS OF THE RENTAL UNIT
    @PdfFormFieldName("b12c96nfn4_app_street_no")
    IPrimitive<String> tenantStreetNumber();

    @PdfFormFieldName("b12c96nfn4_app_street_name")
    IPrimitive<String> tenantStreetName();

    @PdfFormFieldName("b12c96nfn4_app_street_label")
    IPrimitive<String> tenantStreetType();

    @PdfFormFieldName("b12c96nfn4_app_street_direction")
    IPrimitive<String> tenantStreetDirection();

    @PdfFormFieldName("b12c96nfn4_app_unit_no")
    IPrimitive<String> tenantUnit();

    @PdfFormFieldName("b12c96nfn4_app_city")
    IPrimitive<String> tenantMunicipality();

//    /** Actually this is not required */
//    IPrimitive<String> tenantProvince();

    /** First part of postal code */
    @PdfFormFieldName("b12c96nfn4_app_postal_code_1")
    IPrimitive<String> tenantPostalCodeADA();

    /** SecondPart of Postal code */
    @PdfFormFieldName("@@b12c96nfn4_app_postal_code_2.0")
    IPrimitive<String> tenantPostalCodeDAD();

    // THIS INFO. IS FROM YOUR LANDLORD

    @PdfFormFieldName("@@b12c96nfn4_termination_date_g.0")
    IPrimitive<String> terminationDateDD();

    @PdfFormFieldName("@@b12c96nfn4_termination_date_g.1")
    IPrimitive<String> terminationDateMM();

    @PdfFormFieldName("@@b12c96nfn4_termination_date_g.2")
    IPrimitive<String> terminationDateYYYY();

    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed_g.0")
    IPrimitive<String> globalTotalOwedThousands();

    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed_g.1")
    IPrimitive<String> globalTotalOwedHundreds();

    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed_g.2")
    IPrimitive<String> globalTotalOwedCents();

    // OWED RENT BREAKDOWN

    // A
    IPrimitive<String> owedFromDDA();

    IPrimitive<String> owedFromMMA();

    IPrimitive<String> owedFromYYYYA();

    IPrimitive<String> owedToDDA();

    IPrimitive<String> owedToMMA();

    IPrimitive<String> owedToYYYYA();

    IPrimitive<String> rentChargedThousandsA();

    IPrimitive<String> rentChargedHundredsA();

    IPrimitive<String> rentChargedCentsA();

    IPrimitive<String> rentPaidThousandsA();

    IPrimitive<String> rentPaidHundredsA();

    IPrimitive<String> rentPaidCentsA();

    IPrimitive<String> rentOwingThousandsA();

    IPrimitive<String> rentOwingHundredsA();

    IPrimitive<String> rentOwingCentsA();

    // B
    IPrimitive<String> owedFromDDB();

    IPrimitive<String> owedFromMMB();

    IPrimitive<String> owedFromYYYYB();

    IPrimitive<String> owedToDDB();

    IPrimitive<String> owedToMMB();

    IPrimitive<String> owedToYYYYB();

    IPrimitive<String> rentChargedThousandsB();

    IPrimitive<String> rentChargedHundredsB();

    IPrimitive<String> rentChargedCentsB();

    IPrimitive<String> rentPaidThousandsB();

    IPrimitive<String> rentPaidHundredsB();

    IPrimitive<String> rentPaidCentsB();

    IPrimitive<String> rentOwingThousandsB();

    IPrimitive<String> rentOwingHundredsB();

    IPrimitive<String> rentOwingCentsB();

    // C
    IPrimitive<String> owedFromDDC();

    IPrimitive<String> owedFromMMC();

    IPrimitive<String> owedFromYYYYC();

    IPrimitive<String> owedToDDC();

    IPrimitive<String> owedToMMC();

    IPrimitive<String> owedToYYYYC();

    IPrimitive<String> rentChargedThousandsC();

    IPrimitive<String> rentChargedHundredsC();

    IPrimitive<String> rentChargedCentsC();

    IPrimitive<String> rentPaidThousandsC();

    IPrimitive<String> rentPaidHundredsC();

    IPrimitive<String> rentPaidCentsC();

    IPrimitive<String> rentOwingThousandsC();

    IPrimitive<String> rentOwingHundredsC();

    IPrimitive<String> rentOwingCentsC();

    // Total Rent Owing
    IPrimitive<String> rentOwingThousandsTotal();

    IPrimitive<String> rentOwingHundredsTotal();

    IPrimitive<String> rentOwingCentsTotal();

    // SIGNATURE

    IPrimitive<Boolean> isLandlord();

    IPrimitive<Boolean> isAgent();

    IPrimitive<byte[]> signature();

    /** dd/mm/yyyy */
    IPrimitive<String> signatureDate();

    IPrimitive<String> signatureFirstName();

    IPrimitive<String> signatureLastName();

    IPrimitive<String> signatureCompanyName();

    IPrimitive<String> signatureAddress();

    IPrimitive<String> signatureUnit();

    IPrimitive<String> signatureMunicipality();

    IPrimitive<String> signaturePostalCode();

    IPrimitive<String> signaturePhoneNumberAreaCode();

    IPrimitive<String> signaturePhoneNumberCombA();

    IPrimitive<String> signaturePhoneNumberCombB();

    IPrimitive<String> signatureFaxNumberAreaCode();

    IPrimitive<String> signatureFaxNumberCombA();

    IPrimitive<String> signatureFaxNumberCombB();

    IPrimitive<String> signatureEmailAddress();

}
