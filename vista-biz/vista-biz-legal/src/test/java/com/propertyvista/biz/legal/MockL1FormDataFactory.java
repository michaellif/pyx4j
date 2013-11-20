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
 * Created on 2013-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.legal.L1FormFieldsData;
import com.propertyvista.domain.legal.L1FormFieldsData.Gender;
import com.propertyvista.domain.legal.L1FormFieldsData.RentPaymentPeriod;
import com.propertyvista.domain.legal.L1FormFieldsData.YesNo;
import com.propertyvista.domain.legal.NsfChargeDetails;
import com.propertyvista.domain.legal.RentOwingForPeriod;

public class MockL1FormDataFactory {

    public static L1FormFieldsData makeMockL1FormFieldsData() {
        L1FormFieldsData fieldsData = EntityFactory.create(L1FormFieldsData.class);

        fieldsData.totalRentOwing().setValue(new BigDecimal("10000.67"));
        fieldsData.totalRentOwingAsOf().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.fillingDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));

        // Part1
        fieldsData.part1_streetNumber().setValue("1100");
        fieldsData.part1_streetName().setValue("The West Mall");
        fieldsData.part1_streetType().setValue("Street");
        fieldsData.part1_direction().setValue("North");
        fieldsData.part1_unit().setValue("1234");
        fieldsData.part1_municipality().setValue("Toronto");
        fieldsData.part1_postalCode().setValue("A1A 1A1");

        // TODO I don't really know if this 'file number' includes alpha symbols
        fieldsData.part1_fileNumber1().setValue("111-11111");
        fieldsData.part1_fileNumber2().setValue("222-22222");

        // Part2
        fieldsData.part2_tenant1FirstName().setValue("Don Quixote");
        fieldsData.part2_tenant1LastName().setValue("De La Mancha");
        fieldsData.part2_tenant1Gender().setValue(Gender.Male);
        fieldsData.part2_tenant2FirstName().setValue("Sancho");
        fieldsData.part2_tenant2LastName().setValue("Panza");
        fieldsData.part2_tenant2Gender().setValue(Gender.Female);
        fieldsData.part2_MailingAddress().setValue("401 The West Mall");
        fieldsData.part2_unit().setValue("1100");
        fieldsData.part2_municipality().setValue("Toronto");
        fieldsData.part2_provice().setValue("Ontario");
        fieldsData.part2_postalCode().setValue("A1A 1A1");
        fieldsData.part2_dayPhoneNumber().setValue("(647) 123-1234");
        fieldsData.part2_eveningPhoneNumber().setValue("(647) 123-1234");
        fieldsData.part2_faxNumber().setValue("(647) 123-1234");
        fieldsData.part2_emailAddress().setValue("don.quixote@mancha.gov.es");

        // Part3
        fieldsData.part3ApplyingToCollectCharges().setValue(true);
        fieldsData.part3ApplyingToCollectNSF().setValue(true);
        fieldsData.part3IsTenatStillInPossesionOfTheUnit().setValue(YesNo.Yes);
        fieldsData.part3RentPaymentPeriod().setValue(RentPaymentPeriod.other);
        fieldsData.part3otherRentPaymentPeriod().setValue("muhahaha");
        fieldsData.part3AmountOfRentOnDeposit().setValue(new BigDecimal("9123.57"));
        fieldsData.part3dateOfDepositCollection().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.part3lastPeriodInterestPaidFrom().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.part3lastPeriodInterestPaidTo().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));

        // Part4

        RentOwingForPeriod period1Owing = fieldsData.part4_rentOwingBreakdown().$();
        period1Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-01")));
        period1Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-31")));
        period1Owing.rentCharged().setValue(new BigDecimal("1111.99"));
        period1Owing.rentPaid().setValue(new BigDecimal("2222.88"));
        period1Owing.rentOwing().setValue(new BigDecimal("3333.77"));
        fieldsData.part4_rentOwingBreakdown().add(period1Owing);

        RentOwingForPeriod period2Owing = fieldsData.part4_rentOwingBreakdown().$();
        period2Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-01")));
        period2Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-31")));
        period2Owing.rentCharged().setValue(new BigDecimal("1111.11"));
        period2Owing.rentPaid().setValue(new BigDecimal("2222.22"));
        period2Owing.rentOwing().setValue(new BigDecimal("3333.33"));
        fieldsData.part4_rentOwingBreakdown().add(period2Owing);

        RentOwingForPeriod period3Owing = fieldsData.part4_rentOwingBreakdown().$();
        period3Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-01")));
        period3Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-31")));
        period3Owing.rentCharged().setValue(new BigDecimal("1111.00"));
        period3Owing.rentPaid().setValue(new BigDecimal("2222.66"));
        period3Owing.rentOwing().setValue(new BigDecimal("3333.44"));
        fieldsData.part4_rentOwingBreakdown().add(period3Owing);

        fieldsData.part4_totalRentOwing().setValue(new BigDecimal("12345.67"));

        // section 2
        NsfChargeDetails nsfDetail1 = fieldsData.part4_nsfChargesBreakdown().$();
        nsfDetail1.chequeAmount().setValue(new BigDecimal("6666.66"));
        nsfDetail1.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-01")));
        nsfDetail1.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-05")));
        nsfDetail1.bankCharge().setValue(new BigDecimal("10.99"));
        nsfDetail1.landlordsAdministrationCharge().setValue(new BigDecimal("16.99"));
        nsfDetail1.totalCharge().setValue(new BigDecimal("777.99"));
        fieldsData.part4_nsfChargesBreakdown().add(nsfDetail1);

        NsfChargeDetails nsfDetail2 = fieldsData.part4_nsfChargesBreakdown().$();
        nsfDetail2.chequeAmount().setValue(new BigDecimal("7776.77"));
        nsfDetail2.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-01")));
        nsfDetail2.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-05")));
        nsfDetail2.bankCharge().setValue(new BigDecimal("13.11"));
        nsfDetail2.landlordsAdministrationCharge().setValue(new BigDecimal("26.99"));
        nsfDetail2.totalCharge().setValue(new BigDecimal("712.99"));
        fieldsData.part4_nsfChargesBreakdown().add(nsfDetail2);

        NsfChargeDetails nsfDetail3 = fieldsData.part4_nsfChargesBreakdown().$();
        nsfDetail3.chequeAmount().setValue(new BigDecimal("8888.88"));
        nsfDetail3.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2018-01-01")));
        nsfDetail3.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2080-01-05")));
        nsfDetail3.bankCharge().setValue(new BigDecimal("88.81"));
        nsfDetail3.landlordsAdministrationCharge().setValue(new BigDecimal("86.98"));
        nsfDetail3.totalCharge().setValue(new BigDecimal("882.98"));
        fieldsData.part4_nsfChargesBreakdown().add(nsfDetail3);

        NsfChargeDetails nsfDetail4 = fieldsData.part4_nsfChargesBreakdown().$();
        nsfDetail4.chequeAmount().setValue(new BigDecimal("4444.44"));
        nsfDetail4.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2044-01-01")));
        nsfDetail4.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2044-01-05")));
        nsfDetail4.bankCharge().setValue(new BigDecimal("44.41"));
        nsfDetail4.landlordsAdministrationCharge().setValue(new BigDecimal("44.55"));
        nsfDetail4.totalCharge().setValue(new BigDecimal("444.44"));
        fieldsData.part4_nsfChargesBreakdown().add(nsfDetail4);

        NsfChargeDetails nsfDetail5 = fieldsData.part4_nsfChargesBreakdown().$();
        nsfDetail5.chequeAmount().setValue(new BigDecimal("5555.55"));
        nsfDetail5.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2055-01-01")));
        nsfDetail5.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2055-01-05")));
        nsfDetail5.bankCharge().setValue(new BigDecimal("54.41"));
        nsfDetail5.landlordsAdministrationCharge().setValue(new BigDecimal("54.55"));
        nsfDetail5.totalCharge().setValue(new BigDecimal("355.10"));
        fieldsData.part4_nsfChargesBreakdown().add(nsfDetail5);

        fieldsData.part4_nsfTotalChargeOwed().setValue(new BigDecimal("53362.10"));

        // PART 5
        fieldsData.part5_TotalRentOwing().setValue(new BigDecimal("29876.54"));
        fieldsData.part5_TotalNsfChequeChargesOwing().setValue(new BigDecimal("9876.54"));
        fieldsData.part5_Total().setValue(new BigDecimal("39876.54"));

        return fieldsData;
    }
}
