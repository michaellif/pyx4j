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

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface L1FormFieldsData extends IEntity {

    public enum Gender {

        Male, Female

    }

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

    IPrimitive<String> totalRentOwing();

    IPrimitive<String> totalRentOwingAsOf();

    IPrimitive<String> fillingDate();

    // Part1
    IPrimitive<String> streetNumber();

    IPrimitive<String> streetName();

    IPrimitive<String> streetType();

    IPrimitive<String> direction();

    IPrimitive<String> unit();

    IPrimitive<String> municipality();

    IPrimitive<String> postalCode();

    // Part2
    IPrimitive<String> tenant1FirstName();

    IPrimitive<String> tenant1LastName();

    IPrimitive<Gender> tenant1Gender();

    IPrimitive<String> tenant2FirstName();

    IPrimitive<String> tenant2LastName();

    IPrimitive<Gender> tenant2Gender();

    IPrimitive<String> part2MailingAddress();

    IPrimitive<String> part2unit();

    IPrimitive<String> part2municipality();

    IPrimitive<String> part2provice();

    IPrimitive<String> part2postalCode();

    IPrimitive<String> part2dayPhoneNumber();

    IPrimitive<String> part2eveningPhoneNumber();

    IPrimitive<String> part2faxNumber();

    IPrimitive<String> part2emailAddress();

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
