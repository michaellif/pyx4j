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

    public enum SignedBy {

        Landlord {
            @Override
            public String toString() {
                return "PL";
            }
        },

        Agent {
            @Override
            public String toString() {
                return "RA";
            }
        }

    }

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

    @PdfFormFieldName("@@b12c96nfn4_a1_start.0")
    IPrimitive<String> owedFromDDA();

    @PdfFormFieldName("@@b12c96nfn4_a1_start.1")
    IPrimitive<String> owedFromMMA();

    @PdfFormFieldName("@@b12c96nfn4_a1_start.2")
    IPrimitive<String> owedFromYYYYA();

    @PdfFormFieldName("@@b12c96nfn4_a1_end.0")
    IPrimitive<String> owedToDDA();

    @PdfFormFieldName("@@b12c96nfn4_a1_end.1")
    IPrimitive<String> owedToMMA();

    @PdfFormFieldName("@@b12c96nfn4_a1_end.2")
    IPrimitive<String> owedToYYYYA();

    @PdfFormFieldName("@@b12c96nfn4_a1_charged.0")
    IPrimitive<String> rentChargedThousandsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_charged.1")
    IPrimitive<String> rentChargedHundredsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_charged.2")
    IPrimitive<String> rentChargedCentsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_paid.0")
    IPrimitive<String> rentPaidThousandsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_paid.1")
    IPrimitive<String> rentPaidHundredsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_paid.2")
    IPrimitive<String> rentPaidCentsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_owing.0")
    IPrimitive<String> rentOwingThousandsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_owing.1")
    IPrimitive<String> rentOwingHundredsA();

    @PdfFormFieldName("@@b12c96nfn4_a1_owing.2")
    IPrimitive<String> rentOwingCentsA();

    // B        

    @PdfFormFieldName("@@b12c96nfn4_a2_start.0")
    IPrimitive<String> owedFromDDB();

    @PdfFormFieldName("@@b12c96nfn4_a2_start.1")
    IPrimitive<String> owedFromMMB();

    @PdfFormFieldName("@@b12c96nfn4_a2_start.2")
    IPrimitive<String> owedFromYYYYB();

    @PdfFormFieldName("@@b12c96nfn4_a2_end.0")
    IPrimitive<String> owedToDDB();

    @PdfFormFieldName("@@b12c96nfn4_a2_end.1")
    IPrimitive<String> owedToMMB();

    @PdfFormFieldName("@@b12c96nfn4_a2_end.2")
    IPrimitive<String> owedToYYYYB();

    @PdfFormFieldName("@@b12c96nfn4_a2_charged.0")
    IPrimitive<String> rentChargedThousandsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_charged.1")
    IPrimitive<String> rentChargedHundredsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_charged.2")
    IPrimitive<String> rentChargedCentsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_paid.0")
    IPrimitive<String> rentPaidThousandsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_paid.1")
    IPrimitive<String> rentPaidHundredsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_paid.2")
    IPrimitive<String> rentPaidCentsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_owing.0")
    IPrimitive<String> rentOwingThousandsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_owing.1")
    IPrimitive<String> rentOwingHundredsB();

    @PdfFormFieldName("@@b12c96nfn4_a2_owing.2")
    IPrimitive<String> rentOwingCentsB();

    // C       

    @PdfFormFieldName("@@b12c96nfn4_a3_start.0")
    IPrimitive<String> owedFromDDC();

    @PdfFormFieldName("@@b12c96nfn4_a3_start.1")
    IPrimitive<String> owedFromMMC();

    @PdfFormFieldName("@@b12c96nfn4_a3_start.2")
    IPrimitive<String> owedFromYYYYC();

    @PdfFormFieldName("@@b12c96nfn4_a3_end.0")
    IPrimitive<String> owedToDDC();

    @PdfFormFieldName("@@b12c96nfn4_a3_end.1")
    IPrimitive<String> owedToMMC();

    @PdfFormFieldName("@@b12c96nfn4_a3_end.2")
    IPrimitive<String> owedToYYYYC();

    @PdfFormFieldName("@@b12c96nfn4_a3_charged.0")
    IPrimitive<String> rentChargedThousandsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_charged.1")
    IPrimitive<String> rentChargedHundredsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_charged.2")
    IPrimitive<String> rentChargedCentsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_paid.0")
    IPrimitive<String> rentPaidThousandsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_paid.1")
    IPrimitive<String> rentPaidHundredsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_paid.2")
    IPrimitive<String> rentPaidCentsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_owing.0")
    IPrimitive<String> rentOwingThousandsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_owing.1")
    IPrimitive<String> rentOwingHundredsC();

    @PdfFormFieldName("@@b12c96nfn4_a3_owing.2")
    IPrimitive<String> rentOwingCentsC();

    // Total Rent Owing       
    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed.0")
    IPrimitive<String> rentOwingThousandsTotal();

    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed.1")
    IPrimitive<String> rentOwingHundredsTotal();

    @PdfFormFieldName("@@b12c96nfn4_total_rent_owed.2")
    IPrimitive<String> rentOwingCentsTotal();

    // SIGNATURE

    @PdfFormFieldName(value = "b12c96nfn4_signed_by")
    IPrimitive<SignedBy> signedBy();

    @PdfFormFieldName(value = "b12c96nmn4_signature")
    IPrimitive<byte[]> signature();

    /** dd/mm/yyyy */
    @PdfFormFieldName("Text3")
    IPrimitive<String> signatureDate();

    @PdfFormFieldName("b12c96nfn4_personnel_first_name")
    IPrimitive<String> signatureFirstName();

    @PdfFormFieldName("b12c96nfn4_personnel_last_name")
    IPrimitive<String> signatureLastName();

    @PdfFormFieldName("b12c96nfn4_org_name")
    IPrimitive<String> signatureCompanyName();

    @PdfFormFieldName("b12c96nfn4_org_address")
    IPrimitive<String> signatureAddress();

    @PdfFormFieldName("b12c96nfn4_org_unit_no")
    IPrimitive<String> signatureUnit();

    @PdfFormFieldName("b12c96nfn4_org_city")
    IPrimitive<String> signatureMunicipality();

    @PdfFormFieldName("b12c96nfn4_org_prov")
    IPrimitive<String> signatureProvince();

    @PdfFormFieldName("b12c96nfn4_org_postal")
    IPrimitive<String> signaturePostalCode();

    @PdfFormFieldName("@@b12c96nfn4_personnel_phone.0")
    IPrimitive<String> signaturePhoneNumberAreaCode();

    @PdfFormFieldName("@@b12c96nfn4_personnel_phone.1")
    IPrimitive<String> signaturePhoneNumberCombA();

    @PdfFormFieldName("@@b12c96nfn4_personnel_phone.2")
    IPrimitive<String> signaturePhoneNumberCombB();

    @PdfFormFieldName("@@b12c96nfn4_personnel_fax_number.0")
    IPrimitive<String> signatureFaxNumberAreaCode();

    @PdfFormFieldName("@@b12c96nfn4_personnel_fax_number.1")
    IPrimitive<String> signatureFaxNumberCombA();

    @PdfFormFieldName("@@b12c96nfn4_personnel_fax_number.2")
    IPrimitive<String> signatureFaxNumberCombB();

    @PdfFormFieldName("b12c96nfn4_personnel_email")
    IPrimitive<String> signatureEmailAddress();

}
