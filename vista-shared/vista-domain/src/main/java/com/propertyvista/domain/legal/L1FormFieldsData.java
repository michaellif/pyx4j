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
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.legal.utils.L1LandlordsContactInfo;

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
    IList<RentOwingForPeriod> part4_rentOwingBreakdown();

    IPrimitive<BigDecimal> part4_totalRentOwing();

    IList<NsfChargeDetails> part4_nsfChargesBreakdown();

    IPrimitive<BigDecimal> part4_nsfTotalChargeOwed();

    // Part5
    IPrimitive<BigDecimal> part5_TotalRentOwing();

    IPrimitive<BigDecimal> part5_TotalNsfChequeChargesOwing();

    IPrimitive<BigDecimal> part5_Total();

    // Part6
    /** L1 form can hold only one of these */
    IList<L1LandlordsContactInfo> part6_landlordsContactInfos();

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
