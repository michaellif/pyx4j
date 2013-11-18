/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.legal.utils.CanadianPostalCodePartitioner;
import com.propertyvista.domain.legal.utils.DateFormatter;
import com.propertyvista.domain.legal.utils.DatePartitioner;
import com.propertyvista.domain.legal.utils.FileNumberPartitioner;
import com.propertyvista.domain.legal.utils.MoneyFormatter;
import com.propertyvista.domain.legal.utils.MoneyPartitioner;
import com.propertyvista.domain.legal.utils.PdfFormFieldFormatter;
import com.propertyvista.domain.legal.utils.PdfFormFieldMapping;
import com.propertyvista.domain.legal.utils.PdfFormFieldPartitioner;
import com.propertyvista.domain.legal.utils.PhoneNumberPartitioner;
import com.propertyvista.domain.legal.utils.UppercaseFormatter;

@Transient
public interface L1FormFieldsData extends IEntity {

    public enum Gender {//@formatter:off
        Male {@Override public String toString() { return "M"; }},
        Female {@Override public String toString() { return "F"; }},
    }//@formatter:on

    public enum YesNo {

        Yes, No

    }

    public enum RentPaymentPeriod {

        week, month, other

    }

    public enum TypeOfLandlord {

        Male, Female, Company

    }

    public enum LandlordOrAgent {

        Landlord, Agent
    }

    @PdfFormFieldFormatter(MoneyFormatter.class)
    @PdfFormFieldPartitioner(MoneyPartitioner.class)
    @PdfFormFieldMapping("@@b12c96nfl1_rentOwn.0{2},@@b12c96nfl1_rentOwn.1{3},@@b12c96nfl1_rentOwn.2{2}")
    IPrimitive<BigDecimal> totalRentOwing();

    @PdfFormFieldFormatter(DateFormatter.class)
    @PdfFormFieldPartitioner(DatePartitioner.class)
    @PdfFormFieldMapping("@@b12c96nfl1_LastDateRentOwn.0{2},@@b12c96nfl1_LastDateRentOwn.1{2},@@b12c96nfl1_LastDateRentOwn.2{4}")
    IPrimitive<LogicalDate> totalRentOwingAsOf();

    @PdfFormFieldFormatter(DateFormatter.class)
    @PdfFormFieldMapping("b12c96nmfiling_date")
    IPrimitive<LogicalDate> fillingDate();

    // Part1
    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfapp_street_no")
    IPrimitive<String> part1_streetNumber();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfapp_street_name")
    IPrimitive<String> part1_streetName();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfapp_street_label")
    IPrimitive<String> part1_streetType();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfapp_street_direction")
    IPrimitive<String> part1_direction();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfapp_unit_no")
    IPrimitive<String> part1_unit();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfapp_city")
    IPrimitive<String> part1_municipality();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldPartitioner(CanadianPostalCodePartitioner.class)
    @PdfFormFieldMapping("b12c96nfapp_postal_code_1{3},@@b12c96nfapp_postal_code_2.0{3}")
    IPrimitive<String> part1_postalCode();

    @PdfFormFieldPartitioner(FileNumberPartitioner.class)
    @PdfFormFieldMapping("b12c96nfdivision_code_1{3},@@b12c96nfcase_number_1.0{5}")
    IPrimitive<String> part1_fileNumber1();

    @PdfFormFieldPartitioner(FileNumberPartitioner.class)
    @PdfFormFieldMapping("b12c96nfdivision_code_2{3},@@b12c96nfcase_number_2.0{5}")
    IPrimitive<String> part1_fileNumber2();

    // Part2
    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_first_name")
    IPrimitive<String> part2_tenant1FirstName();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_last_name")
    IPrimitive<String> part2_tenant1LastName();

    @PdfFormFieldMapping(value = "b12c96nfP2_1_gender")
    IPrimitive<Gender> part2_tenant1Gender();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_2_first_name")
    IPrimitive<String> part2_tenant2FirstName();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_2_last_name")
    IPrimitive<String> part2_tenant2LastName();

    @PdfFormFieldMapping(value = "b12c96nfP2_2_gender")
    IPrimitive<Gender> part2_tenant2Gender();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_st_address")
    IPrimitive<String> part2_MailingAddress();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_unit_no")
    IPrimitive<String> part2_unit();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_city")
    IPrimitive<String> part2_municipality();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping(value = "b12c96nfP2_prov")
    IPrimitive<String> part2_provice();

    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfP2_postal")
    IPrimitive<String> part2_postalCode();

    @PdfFormFieldPartitioner(PhoneNumberPartitioner.class)
    @PdfFormFieldMapping("@@b12c96nfP2_day_phone.0{3},@@b12c96nfP2_day_phone.1{3},@@b12c96nfP2_day_phone.2{4}")
    IPrimitive<String> part2_dayPhoneNumber();

    @PdfFormFieldPartitioner(PhoneNumberPartitioner.class)
    @PdfFormFieldMapping("@@b12c96nfP2_evg_phone.0{3},@@b12c96nfP2_evg_phone.1{3},@@b12c96nfP2_evg_phone.2{4}")
    IPrimitive<String> part2_eveningPhoneNumber();

    @PdfFormFieldPartitioner(PhoneNumberPartitioner.class)
    @PdfFormFieldMapping("@@b12c96nfP2_fax.0{3},@@b12c96nfP2_fax.1{3},@@b12c96nfP2_fax.2{4}")
    IPrimitive<String> part2_faxNumber();

    // TODO? in theory email's recipent is might be case sensitive, but the form requires everything CAPITALIZED
    @PdfFormFieldFormatter(UppercaseFormatter.class)
    @PdfFormFieldMapping("b12c96nfP2_email")
    IPrimitive<String> part2_emailAddress();

    // Part3
    IPrimitive<Boolean> part3ApplyingToCollectCharges();

    IPrimitive<Boolean> part3ApplyingToCollectNSF();

    IPrimitive<YesNo> part3IsTenatStillInPossesionOfTheUnit();

    IPrimitive<RentPaymentPeriod> part3RentPaymentPeriod();

    IPrimitive<String> part3otherRentPaymentPeriod();

    IPrimitive<String> part3AmountOfRentOnDeposit();

    IPrimitive<String> part3dateOfDepositCollection();

    IPrimitive<String> part3lastPeriodInterestPaidFrom();

    IPrimitive<String> part3lastPeriodInterestPaidTo();

    // Part4
    IPrimitive<String> part4period1From();

    IPrimitive<String> part4period1To();

    IPrimitive<String> part4period1RentCharged();

    IPrimitive<String> part4period1RentRentPaid();

    IPrimitive<String> part4period1RentRentRentOwing();

    //
    IPrimitive<String> part4period2From();

    IPrimitive<String> part4period2To();

    IPrimitive<String> part4period2RentCharged();

    IPrimitive<String> part4period2RentRentPaid();

    IPrimitive<String> part4period2RentRentRentOwing();

    //
    IPrimitive<String> part4period3From();

    IPrimitive<String> part4period3To();

    IPrimitive<String> part4period3RentCharged();

    IPrimitive<String> part4period3RentRentPaid();

    IPrimitive<String> part4period3RentRentRentOwing();

    //
    IPrimitive<String> part4nsf1ChequeAmount();

    IPrimitive<String> part4nsf1DateOfCheque();

    IPrimitive<String> part4nsf1DateOfNsfCharge();

    IPrimitive<String> part4nsf1BankCharge();

    IPrimitive<String> part4nsf1LandlordsCharge();

    IPrimitive<String> part4nsf1TotalCharge();

    //
    IPrimitive<String> part4nsf2ChequeAmount();

    IPrimitive<String> part4nsf2DateOfCheque();

    IPrimitive<String> part4nsf2DateOfNsfCharge();

    IPrimitive<String> part4nsf2BankCharge();

    IPrimitive<String> part4nsf2LandlordsCharge();

    IPrimitive<String> part4nsf2TotalCharge();

    //
    IPrimitive<String> part4nsf3ChequeAmount();

    IPrimitive<String> part4nsf3DateOfCheque();

    IPrimitive<String> part4nsf3DateOfNsfCharge();

    IPrimitive<String> part4nsf3BankCharge();

    IPrimitive<String> part4nsf3LandlordsCharge();

    IPrimitive<String> part4nsf3TotalCharge();

    IPrimitive<String> part4nsf4ChequeAmount();

    IPrimitive<String> part4nsf4DateOfCheque();

    IPrimitive<String> part4nsf4DateOfNsfCharge();

    IPrimitive<String> part4nsf4BankCharge();

    IPrimitive<String> part4nsf4LandlordsCharge();

    IPrimitive<String> part4nsf4TotalCharge();

    IPrimitive<String> part4nsfTotalChargeOwed();

    // Part5
    IPrimitive<String> part5TotalRentOwing();

    IPrimitive<String> part5TotalNSFChequeChargesOwing();

    IPrimitive<String> part5Total();

    // Part6
    IPrimitive<String> part6_firstName();

    IPrimitive<String> part6_lastName();

    IPrimitive<TypeOfLandlord> part6_typeOfLandlord();

    IPrimitive<String> part6_streetAddress();

    IPrimitive<String> part6_unit();

    IPrimitive<String> part6_municipality();

    IPrimitive<String> part6_provice();

    IPrimitive<String> part6_postalCode();

    IPrimitive<String> part6_dayPhoneNumber();

    IPrimitive<String> part6_eveningPhoneNumber();

    IPrimitive<String> part6_faxNumber();

    IPrimitive<String> part6_emailAddress();

    IPrimitive<String> part6_agentsFirstName();

    IPrimitive<String> part6_agentsLastName();

    IPrimitive<String> part6_agentsCompanyName();

    IPrimitive<String> part6_agentsMailingAddress();

    IPrimitive<String> part6_agentsUnit();

    IPrimitive<String> part6_agentsMunicipality();

    IPrimitive<String> part6_agentsProvince();

    IPrimitive<String> part6_agentsPostalCode();

    IPrimitive<String> part6_agentsPhoneNumber();

    IPrimitive<String> part6_agentsFaxNumber();

    IPrimitive<String> part6_agentsEmail();

    // Part7

    IPrimitive<byte[]> part7_signature();

    IPrimitive<LandlordOrAgent> part7_landlordOrAgent();

    IPrimitive<String> part7_date();

}
