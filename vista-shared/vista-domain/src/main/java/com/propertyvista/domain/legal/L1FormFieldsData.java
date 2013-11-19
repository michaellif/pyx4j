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

    IPrimitive<BigDecimal> totalRentOwing();

    IPrimitive<LogicalDate> totalRentOwingAsOf();

    IPrimitive<LogicalDate> fillingDate();

    // Part1
    IPrimitive<String> part1_streetNumber();

    IPrimitive<String> part1_streetName();

    IPrimitive<String> part1_streetType();

    IPrimitive<String> part1_direction();

    IPrimitive<String> part1_unit();

    IPrimitive<String> part1_municipality();

    IPrimitive<String> part1_postalCode();

    IPrimitive<String> part1_fileNumber1();

    IPrimitive<String> part1_fileNumber2();

    // Part2
    IPrimitive<String> part2_tenant1FirstName();

    IPrimitive<String> part2_tenant1LastName();

    IPrimitive<Gender> part2_tenant1Gender();

    IPrimitive<String> part2_tenant2FirstName();

    IPrimitive<String> part2_tenant2LastName();

    IPrimitive<Gender> part2_tenant2Gender();

    IPrimitive<String> part2_MailingAddress();

    IPrimitive<String> part2_unit();

    IPrimitive<String> part2_municipality();

    IPrimitive<String> part2_provice();

    IPrimitive<String> part2_postalCode();

    IPrimitive<String> part2_dayPhoneNumber();

    IPrimitive<String> part2_eveningPhoneNumber();

    IPrimitive<String> part2_faxNumber();

    IPrimitive<String> part2_emailAddress();

    // Part3
    IPrimitive<Boolean> part3ApplyingToCollectCharges();

    IPrimitive<Boolean> part3ApplyingToCollectNSF();

    IPrimitive<YesNo> part3IsTenatStillInPossesionOfTheUnit();

    IPrimitive<RentPaymentPeriod> part3RentPaymentPeriod();

    IPrimitive<String> part3otherRentPaymentPeriod();

    IPrimitive<BigDecimal> part3AmountOfRentOnDeposit();

    IPrimitive<LogicalDate> part3dateOfDepositCollection();

    IPrimitive<LogicalDate> part3lastPeriodInterestPaidFrom();

    IPrimitive<LogicalDate> part3lastPeriodInterestPaidTo();

    // Part4
    IPrimitive<LogicalDate> part4period1From();

    IPrimitive<LogicalDate> part4period1To();

    IPrimitive<BigDecimal> part4period1RentCharged();

    IPrimitive<BigDecimal> part4period1RentPaid();

    IPrimitive<BigDecimal> part4period1RentOwing();

    //
    IPrimitive<LogicalDate> part4period2From();

    IPrimitive<LogicalDate> part4period2To();

    IPrimitive<BigDecimal> part4period2RentCharged();

    IPrimitive<BigDecimal> part4period2RentPaid();

    IPrimitive<BigDecimal> part4period2RentOwing();

    //
    IPrimitive<LogicalDate> part4period3From();

    IPrimitive<LogicalDate> part4period3To();

    IPrimitive<BigDecimal> part4period3RentCharged();

    IPrimitive<BigDecimal> part4period3RentPaid();

    IPrimitive<BigDecimal> part4period3RentOwing();

    IPrimitive<BigDecimal> part4totalRentOwing();

    //
    IPrimitive<BigDecimal> part4nsf1ChequeAmount();

    IPrimitive<LogicalDate> part4nsf1DateOfCheque();

    IPrimitive<LogicalDate> part4nsf1DateOfNsfCharge();

    IPrimitive<BigDecimal> part4nsf1BankCharge();

    IPrimitive<BigDecimal> part4nsf1LandlordsCharge();

    IPrimitive<BigDecimal> part4nsf1TotalCharge();

    //
    IPrimitive<BigDecimal> part4nsf2ChequeAmount();

    IPrimitive<LogicalDate> part4nsf2DateOfCheque();

    IPrimitive<LogicalDate> part4nsf2DateOfNsfCharge();

    IPrimitive<BigDecimal> part4nsf2BankCharge();

    IPrimitive<BigDecimal> part4nsf2LandlordsCharge();

    IPrimitive<BigDecimal> part4nsf2TotalCharge();

    //
    IPrimitive<BigDecimal> part4nsf3ChequeAmount();

    IPrimitive<LogicalDate> part4nsf3DateOfCheque();

    IPrimitive<LogicalDate> part4nsf3DateOfNsfCharge();

    IPrimitive<BigDecimal> part4nsf3BankCharge();

    IPrimitive<BigDecimal> part4nsf3LandlordsCharge();

    IPrimitive<BigDecimal> part4nsf3TotalCharge();

    //
    IPrimitive<BigDecimal> part4nsf4ChequeAmount();

    IPrimitive<LogicalDate> part4nsf4DateOfCheque();

    IPrimitive<LogicalDate> part4nsf4DateOfNsfCharge();

    IPrimitive<BigDecimal> part4nsf4BankCharge();

    IPrimitive<BigDecimal> part4nsf4LandlordsCharge();

    IPrimitive<BigDecimal> part4nsf4TotalCharge();

    //
    IPrimitive<BigDecimal> part4nsf5ChequeAmount();

    IPrimitive<LogicalDate> part4nsf5DateOfCheque();

    IPrimitive<LogicalDate> part4nsf5DateOfNsfCharge();

    IPrimitive<BigDecimal> part4nsf5BankCharge();

    IPrimitive<BigDecimal> part4nsf5LandlordsCharge();

    IPrimitive<BigDecimal> part4nsf5TotalCharge();

    //
    IPrimitive<BigDecimal> part4nsfTotalChargeOwed();

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
