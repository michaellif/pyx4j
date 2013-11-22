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
package com.propertyvista.domain.legal.n4;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.legal.utils.PdfFormFieldMapping;

@Transient
/** This maps directly to the fields of PDF template of N4 Form */
public interface N4FormFieldsDataDepr extends IEntity {

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
    @PdfFormFieldMapping("Text1")
    IPrimitive<String> to();

    /**
     * Landlord's name.
     */
    @PdfFormFieldMapping("Text2")
    IPrimitive<String> from();

    // ADDRESS OF THE RENTAL UNIT
    @PdfFormFieldMapping("b12c96nfn4_app_street_no")
    IPrimitive<String> tenantStreetNumber();

    @PdfFormFieldMapping("b12c96nfn4_app_street_name")
    IPrimitive<String> tenantStreetName();

    @PdfFormFieldMapping("b12c96nfn4_app_street_label")
    IPrimitive<String> tenantStreetType();

    @PdfFormFieldMapping("b12c96nfn4_app_street_direction")
    IPrimitive<String> tenantStreetDirection();

    @PdfFormFieldMapping("b12c96nfn4_app_unit_no")
    IPrimitive<String> tenantUnit();

    @PdfFormFieldMapping("b12c96nfn4_app_city")
    IPrimitive<String> tenantMunicipality();

//    /** Actually this is not required */
//    IPrimitive<String> tenantProvince();

    /** First part of postal code */
    @PdfFormFieldMapping("b12c96nfn4_app_postal_code_1")
    IPrimitive<String> tenantPostalCodeADA();

    /** SecondPart of Postal code */
    @PdfFormFieldMapping("@@b12c96nfn4_app_postal_code_2.0")
    IPrimitive<String> tenantPostalCodeDAD();

    // THIS INFO. IS FROM YOUR LANDLORD

    @PdfFormFieldMapping("@@b12c96nfn4_termination_date_g.0")
    IPrimitive<String> terminationDateDD();

    @PdfFormFieldMapping("@@b12c96nfn4_termination_date_g.1")
    IPrimitive<String> terminationDateMM();

    @PdfFormFieldMapping("@@b12c96nfn4_termination_date_g.2")
    IPrimitive<String> terminationDateYYYY();

    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed_g.0")
    IPrimitive<String> globalTotalOwedThousands();

    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed_g.1")
    IPrimitive<String> globalTotalOwedHundreds();

    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed_g.2")
    IPrimitive<String> globalTotalOwedCents();

    // OWED RENT BREAKDOWN

    // A        

    @PdfFormFieldMapping("@@b12c96nfn4_a1_start.0")
    IPrimitive<String> owedFromDDA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_start.1")
    IPrimitive<String> owedFromMMA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_start.2")
    IPrimitive<String> owedFromYYYYA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_end.0")
    IPrimitive<String> owedToDDA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_end.1")
    IPrimitive<String> owedToMMA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_end.2")
    IPrimitive<String> owedToYYYYA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_charged.0")
    IPrimitive<String> rentChargedThousandsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_charged.1")
    IPrimitive<String> rentChargedHundredsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_charged.2")
    IPrimitive<String> rentChargedCentsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_paid.0")
    IPrimitive<String> rentPaidThousandsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_paid.1")
    IPrimitive<String> rentPaidHundredsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_paid.2")
    IPrimitive<String> rentPaidCentsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_owing.0")
    IPrimitive<String> rentOwingThousandsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_owing.1")
    IPrimitive<String> rentOwingHundredsA();

    @PdfFormFieldMapping("@@b12c96nfn4_a1_owing.2")
    IPrimitive<String> rentOwingCentsA();

    // B        

    @PdfFormFieldMapping("@@b12c96nfn4_a2_start.0")
    IPrimitive<String> owedFromDDB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_start.1")
    IPrimitive<String> owedFromMMB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_start.2")
    IPrimitive<String> owedFromYYYYB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_end.0")
    IPrimitive<String> owedToDDB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_end.1")
    IPrimitive<String> owedToMMB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_end.2")
    IPrimitive<String> owedToYYYYB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_charged.0")
    IPrimitive<String> rentChargedThousandsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_charged.1")
    IPrimitive<String> rentChargedHundredsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_charged.2")
    IPrimitive<String> rentChargedCentsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_paid.0")
    IPrimitive<String> rentPaidThousandsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_paid.1")
    IPrimitive<String> rentPaidHundredsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_paid.2")
    IPrimitive<String> rentPaidCentsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_owing.0")
    IPrimitive<String> rentOwingThousandsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_owing.1")
    IPrimitive<String> rentOwingHundredsB();

    @PdfFormFieldMapping("@@b12c96nfn4_a2_owing.2")
    IPrimitive<String> rentOwingCentsB();

    // C       

    @PdfFormFieldMapping("@@b12c96nfn4_a3_start.0")
    IPrimitive<String> owedFromDDC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_start.1")
    IPrimitive<String> owedFromMMC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_start.2")
    IPrimitive<String> owedFromYYYYC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_end.0")
    IPrimitive<String> owedToDDC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_end.1")
    IPrimitive<String> owedToMMC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_end.2")
    IPrimitive<String> owedToYYYYC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_charged.0")
    IPrimitive<String> rentChargedThousandsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_charged.1")
    IPrimitive<String> rentChargedHundredsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_charged.2")
    IPrimitive<String> rentChargedCentsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_paid.0")
    IPrimitive<String> rentPaidThousandsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_paid.1")
    IPrimitive<String> rentPaidHundredsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_paid.2")
    IPrimitive<String> rentPaidCentsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_owing.0")
    IPrimitive<String> rentOwingThousandsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_owing.1")
    IPrimitive<String> rentOwingHundredsC();

    @PdfFormFieldMapping("@@b12c96nfn4_a3_owing.2")
    IPrimitive<String> rentOwingCentsC();

    // Total Rent Owing       
    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed.0")
    IPrimitive<String> rentOwingThousandsTotal();

    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed.1")
    IPrimitive<String> rentOwingHundredsTotal();

    @PdfFormFieldMapping("@@b12c96nfn4_total_rent_owed.2")
    IPrimitive<String> rentOwingCentsTotal();

    // SIGNATURE

    @PdfFormFieldMapping(value = "b12c96nfn4_signed_by")
    IPrimitive<SignedBy> signedBy();

    @PdfFormFieldMapping(value = "b12c96nmn4_signature")
    IPrimitive<byte[]> signature();

    /** dd/mm/yyyy */
    @PdfFormFieldMapping("Text3")
    IPrimitive<String> signatureDate();

    @PdfFormFieldMapping("b12c96nfn4_personnel_first_name")
    IPrimitive<String> signatureFirstName();

    @PdfFormFieldMapping("b12c96nfn4_personnel_last_name")
    IPrimitive<String> signatureLastName();

    @PdfFormFieldMapping("b12c96nfn4_org_name")
    IPrimitive<String> signatureCompanyName();

    @PdfFormFieldMapping("b12c96nfn4_org_address")
    IPrimitive<String> signatureAddress();

    @PdfFormFieldMapping("b12c96nfn4_org_unit_no")
    IPrimitive<String> signatureUnit();

    @PdfFormFieldMapping("b12c96nfn4_org_city")
    IPrimitive<String> signatureMunicipality();

    @PdfFormFieldMapping("b12c96nfn4_org_prov")
    IPrimitive<String> signatureProvince();

    @PdfFormFieldMapping("b12c96nfn4_org_postal")
    IPrimitive<String> signaturePostalCode();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_phone.0")
    IPrimitive<String> signaturePhoneNumberAreaCode();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_phone.1")
    IPrimitive<String> signaturePhoneNumberCombA();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_phone.2")
    IPrimitive<String> signaturePhoneNumberCombB();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_fax_number.0")
    IPrimitive<String> signatureFaxNumberAreaCode();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_fax_number.1")
    IPrimitive<String> signatureFaxNumberCombA();

    @PdfFormFieldMapping("@@b12c96nfn4_personnel_fax_number.2")
    IPrimitive<String> signatureFaxNumberCombB();

    @PdfFormFieldMapping("b12c96nfn4_personnel_email")
    IPrimitive<String> signatureEmailAddress();

}
