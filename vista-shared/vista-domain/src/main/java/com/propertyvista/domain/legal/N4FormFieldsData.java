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

import com.propertyvista.domain.legal.utils.PdfFormField;

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
    @PdfFormField("Text1")
    IPrimitive<String> to();

    /**
     * Landlord's name.
     */
    @PdfFormField("Text2")
    IPrimitive<String> from();

    // ADDRESS OF THE RENTAL UNIT
    @PdfFormField("b12c96nfn4_app_street_no")
    IPrimitive<String> tenantStreetNumber();

    @PdfFormField("b12c96nfn4_app_street_name")
    IPrimitive<String> tenantStreetName();

    @PdfFormField("b12c96nfn4_app_street_label")
    IPrimitive<String> tenantStreetType();

    @PdfFormField("b12c96nfn4_app_street_direction")
    IPrimitive<String> tenantStreetDirection();

    @PdfFormField("b12c96nfn4_app_unit_no")
    IPrimitive<String> tenantUnit();

    @PdfFormField("b12c96nfn4_app_city")
    IPrimitive<String> tenantMunicipality();

//    /** Actually this is not required */
//    IPrimitive<String> tenantProvince();

    /** First part of postal code */
    @PdfFormField("b12c96nfn4_app_postal_code_1")
    IPrimitive<String> tenantPostalCodeADA();

    /** SecondPart of Postal code */
    @PdfFormField("@@b12c96nfn4_app_postal_code_2.0")
    IPrimitive<String> tenantPostalCodeDAD();

    // THIS INFO. IS FROM YOUR LANDLORD

    @PdfFormField("@@b12c96nfn4_termination_date_g.0")
    IPrimitive<String> terminationDateDD();

    @PdfFormField("@@b12c96nfn4_termination_date_g.1")
    IPrimitive<String> terminationDateMM();

    @PdfFormField("@@b12c96nfn4_termination_date_g.2")
    IPrimitive<String> terminationDateYYYY();

    @PdfFormField("@@b12c96nfn4_total_rent_owed_g.0")
    IPrimitive<String> globalTotalOwedThousands();

    @PdfFormField("@@b12c96nfn4_total_rent_owed_g.1")
    IPrimitive<String> globalTotalOwedHundreds();

    @PdfFormField("@@b12c96nfn4_total_rent_owed_g.2")
    IPrimitive<String> globalTotalOwedCents();

    // OWED RENT BREAKDOWN

    // A        

    @PdfFormField("@@b12c96nfn4_a1_start.0")
    IPrimitive<String> owedFromDDA();

    @PdfFormField("@@b12c96nfn4_a1_start.1")
    IPrimitive<String> owedFromMMA();

    @PdfFormField("@@b12c96nfn4_a1_start.2")
    IPrimitive<String> owedFromYYYYA();

    @PdfFormField("@@b12c96nfn4_a1_end.0")
    IPrimitive<String> owedToDDA();

    @PdfFormField("@@b12c96nfn4_a1_end.1")
    IPrimitive<String> owedToMMA();

    @PdfFormField("@@b12c96nfn4_a1_end.2")
    IPrimitive<String> owedToYYYYA();

    @PdfFormField("@@b12c96nfn4_a1_charged.0")
    IPrimitive<String> rentChargedThousandsA();

    @PdfFormField("@@b12c96nfn4_a1_charged.1")
    IPrimitive<String> rentChargedHundredsA();

    @PdfFormField("@@b12c96nfn4_a1_charged.2")
    IPrimitive<String> rentChargedCentsA();

    @PdfFormField("@@b12c96nfn4_a1_paid.0")
    IPrimitive<String> rentPaidThousandsA();

    @PdfFormField("@@b12c96nfn4_a1_paid.1")
    IPrimitive<String> rentPaidHundredsA();

    @PdfFormField("@@b12c96nfn4_a1_paid.2")
    IPrimitive<String> rentPaidCentsA();

    @PdfFormField("@@b12c96nfn4_a1_owing.0")
    IPrimitive<String> rentOwingThousandsA();

    @PdfFormField("@@b12c96nfn4_a1_owing.1")
    IPrimitive<String> rentOwingHundredsA();

    @PdfFormField("@@b12c96nfn4_a1_owing.2")
    IPrimitive<String> rentOwingCentsA();

    // B        

    @PdfFormField("@@b12c96nfn4_a2_start.0")
    IPrimitive<String> owedFromDDB();

    @PdfFormField("@@b12c96nfn4_a2_start.1")
    IPrimitive<String> owedFromMMB();

    @PdfFormField("@@b12c96nfn4_a2_start.2")
    IPrimitive<String> owedFromYYYYB();

    @PdfFormField("@@b12c96nfn4_a2_end.0")
    IPrimitive<String> owedToDDB();

    @PdfFormField("@@b12c96nfn4_a2_end.1")
    IPrimitive<String> owedToMMB();

    @PdfFormField("@@b12c96nfn4_a2_end.2")
    IPrimitive<String> owedToYYYYB();

    @PdfFormField("@@b12c96nfn4_a2_charged.0")
    IPrimitive<String> rentChargedThousandsB();

    @PdfFormField("@@b12c96nfn4_a2_charged.1")
    IPrimitive<String> rentChargedHundredsB();

    @PdfFormField("@@b12c96nfn4_a2_charged.2")
    IPrimitive<String> rentChargedCentsB();

    @PdfFormField("@@b12c96nfn4_a2_paid.0")
    IPrimitive<String> rentPaidThousandsB();

    @PdfFormField("@@b12c96nfn4_a2_paid.1")
    IPrimitive<String> rentPaidHundredsB();

    @PdfFormField("@@b12c96nfn4_a2_paid.2")
    IPrimitive<String> rentPaidCentsB();

    @PdfFormField("@@b12c96nfn4_a2_owing.0")
    IPrimitive<String> rentOwingThousandsB();

    @PdfFormField("@@b12c96nfn4_a2_owing.1")
    IPrimitive<String> rentOwingHundredsB();

    @PdfFormField("@@b12c96nfn4_a2_owing.2")
    IPrimitive<String> rentOwingCentsB();

    // C       

    @PdfFormField("@@b12c96nfn4_a3_start.0")
    IPrimitive<String> owedFromDDC();

    @PdfFormField("@@b12c96nfn4_a3_start.1")
    IPrimitive<String> owedFromMMC();

    @PdfFormField("@@b12c96nfn4_a3_start.2")
    IPrimitive<String> owedFromYYYYC();

    @PdfFormField("@@b12c96nfn4_a3_end.0")
    IPrimitive<String> owedToDDC();

    @PdfFormField("@@b12c96nfn4_a3_end.1")
    IPrimitive<String> owedToMMC();

    @PdfFormField("@@b12c96nfn4_a3_end.2")
    IPrimitive<String> owedToYYYYC();

    @PdfFormField("@@b12c96nfn4_a3_charged.0")
    IPrimitive<String> rentChargedThousandsC();

    @PdfFormField("@@b12c96nfn4_a3_charged.1")
    IPrimitive<String> rentChargedHundredsC();

    @PdfFormField("@@b12c96nfn4_a3_charged.2")
    IPrimitive<String> rentChargedCentsC();

    @PdfFormField("@@b12c96nfn4_a3_paid.0")
    IPrimitive<String> rentPaidThousandsC();

    @PdfFormField("@@b12c96nfn4_a3_paid.1")
    IPrimitive<String> rentPaidHundredsC();

    @PdfFormField("@@b12c96nfn4_a3_paid.2")
    IPrimitive<String> rentPaidCentsC();

    @PdfFormField("@@b12c96nfn4_a3_owing.0")
    IPrimitive<String> rentOwingThousandsC();

    @PdfFormField("@@b12c96nfn4_a3_owing.1")
    IPrimitive<String> rentOwingHundredsC();

    @PdfFormField("@@b12c96nfn4_a3_owing.2")
    IPrimitive<String> rentOwingCentsC();

    // Total Rent Owing       
    @PdfFormField("@@b12c96nfn4_total_rent_owed.0")
    IPrimitive<String> rentOwingThousandsTotal();

    @PdfFormField("@@b12c96nfn4_total_rent_owed.1")
    IPrimitive<String> rentOwingHundredsTotal();

    @PdfFormField("@@b12c96nfn4_total_rent_owed.2")
    IPrimitive<String> rentOwingCentsTotal();

    // SIGNATURE

    @PdfFormField(value = "b12c96nfn4_signed_by")
    IPrimitive<SignedBy> signedBy();

    @PdfFormField(value = "b12c96nmn4_signature")
    IPrimitive<byte[]> signature();

    /** dd/mm/yyyy */
    @PdfFormField("Text3")
    IPrimitive<String> signatureDate();

    @PdfFormField("b12c96nfn4_personnel_first_name")
    IPrimitive<String> signatureFirstName();

    @PdfFormField("b12c96nfn4_personnel_last_name")
    IPrimitive<String> signatureLastName();

    @PdfFormField("b12c96nfn4_org_name")
    IPrimitive<String> signatureCompanyName();

    @PdfFormField("b12c96nfn4_org_address")
    IPrimitive<String> signatureAddress();

    @PdfFormField("b12c96nfn4_org_unit_no")
    IPrimitive<String> signatureUnit();

    @PdfFormField("b12c96nfn4_org_city")
    IPrimitive<String> signatureMunicipality();

    @PdfFormField("b12c96nfn4_org_prov")
    IPrimitive<String> signatureProvince();

    @PdfFormField("b12c96nfn4_org_postal")
    IPrimitive<String> signaturePostalCode();

    @PdfFormField("@@b12c96nfn4_personnel_phone.0")
    IPrimitive<String> signaturePhoneNumberAreaCode();

    @PdfFormField("@@b12c96nfn4_personnel_phone.1")
    IPrimitive<String> signaturePhoneNumberCombA();

    @PdfFormField("@@b12c96nfn4_personnel_phone.2")
    IPrimitive<String> signaturePhoneNumberCombB();

    @PdfFormField("@@b12c96nfn4_personnel_fax_number.0")
    IPrimitive<String> signatureFaxNumberAreaCode();

    @PdfFormField("@@b12c96nfn4_personnel_fax_number.1")
    IPrimitive<String> signatureFaxNumberCombA();

    @PdfFormField("@@b12c96nfn4_personnel_fax_number.2")
    IPrimitive<String> signatureFaxNumberCombB();

    @PdfFormField("b12c96nfn4_personnel_email")
    IPrimitive<String> signatureEmailAddress();

}
