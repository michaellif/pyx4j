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

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.legal.l1.L1ApplicationSchedule.ApplicationPackageDeliveryMethodToLandlord;
import com.propertyvista.domain.legal.l1.L1ApplicationSchedule.ApplicationPackageDeliveryMethodToTenant;
import com.propertyvista.domain.legal.l1.L1FormFieldsData;
import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo;
import com.propertyvista.domain.legal.l1.L1PaymentInfo;
import com.propertyvista.domain.legal.l1.L1ReasonForApplication;
import com.propertyvista.domain.legal.l1.NsfChargeDetails;
import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo.TypeOfLandlord;
import com.propertyvista.domain.legal.l1.L1ReasonForApplication.YesNo;
import com.propertyvista.domain.legal.l1.L1ScheduleAndPayment.LanguageServiceType;
import com.propertyvista.domain.legal.l1.L1SignatureData;
import com.propertyvista.domain.legal.l1.L1TenantInfo;
import com.propertyvista.domain.legal.l1.L1TenantInfo.Gender;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

public class MockL1FormDataFactory {

    private final static String SIGNATURE = "mock-signature.jpg";

    public static L1FormFieldsData makeMockL1FormFieldsData() {
        L1FormFieldsData fieldsData = EntityFactory.create(L1FormFieldsData.class);

        fieldsData.totalRentOwing().setValue(new BigDecimal("10000.67"));
        fieldsData.totalRentOwingAsOf().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.fillingDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));

        // Part1
        fieldsData.rentalUnitInfo().streetNumber().setValue("1100");
        fieldsData.rentalUnitInfo().streetName().setValue("The West Mall");
        fieldsData.rentalUnitInfo().streetType().setValue("Street");
        fieldsData.rentalUnitInfo().direction().setValue("North");
        fieldsData.rentalUnitInfo().unit().setValue("1234");
        fieldsData.rentalUnitInfo().municipality().setValue("Toronto");
        fieldsData.rentalUnitInfo().postalCode().setValue("A1A 1A1");

        // TODO I don't really know if this 'file number' includes alpha symbols
        fieldsData.rentalUnitInfo().relatedApplicationFileNumber1().setValue("111-11111");
        fieldsData.rentalUnitInfo().relatedApplicationFileNumber2().setValue("222-22222");

        // Part2
        L1TenantInfo tenantInfo1 = fieldsData.tenants().$();
        tenantInfo1.firstName().setValue("Don Quixote");
        tenantInfo1.lastName().setValue("De La Mancha");
        tenantInfo1.gender().setValue(Gender.Male);
        fieldsData.tenants().add(tenantInfo1);

        L1TenantInfo tenantInfo2 = fieldsData.tenants().$();
        tenantInfo2.firstName().setValue("Sancho");
        tenantInfo2.lastName().setValue("Panza");
        tenantInfo2.gender().setValue(Gender.Female);
        fieldsData.tenants().add(tenantInfo2);

        fieldsData.tenantContactInfo().mailingAddress().setValue("401 The West Mall");
        fieldsData.tenantContactInfo().unit().setValue("1100");
        fieldsData.tenantContactInfo().municipality().setValue("Toronto");
        fieldsData.tenantContactInfo().province().setValue("Ontario");
        fieldsData.tenantContactInfo().postalCode().setValue("A1A 1A1");
        fieldsData.tenantContactInfo().dayPhoneNumber().setValue("(647) 123-1234");
        fieldsData.tenantContactInfo().eveningPhoneNumber().setValue("(647) 123-1234");
        fieldsData.tenantContactInfo().faxNumber().setValue("(647) 123-1234");
        fieldsData.tenantContactInfo().emailAddress().setValue("don.quixote@mancha.gov.es");

        // Part3
        fieldsData.reasonForApplication().applyingToCollectCharges().setValue(true);
        fieldsData.reasonForApplication().applyingToCollectNsf().setValue(true);
        fieldsData.reasonForApplication().isTenatStillInPossesionOfTheUnit().setValue(L1ReasonForApplication.YesNo.Yes);
        fieldsData.reasonForApplication().rentPaymentPeriod().setValue(L1ReasonForApplication.RentPaymentPeriod.other);
        fieldsData.reasonForApplication().otherRentPaymentPeriodDescription().setValue("muhahaha");
        fieldsData.reasonForApplication().amountOfRentOnDeposit().setValue(new BigDecimal("9123.57"));
        fieldsData.reasonForApplication().dateOfDepositCollection().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.reasonForApplication().lastPeriodInterestPaidFrom().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));
        fieldsData.reasonForApplication().lastPeriodInterestPaidTo().setValue(new LogicalDate(DateUtils.detectDateformat("2013-01-31")));

        // Part4

        RentOwingForPeriod period1Owing = fieldsData.owedRent().rentOwingBreakdown().$();
        period1Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-01")));
        period1Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-31")));
        period1Owing.rentCharged().setValue(new BigDecimal("1111.99"));
        period1Owing.rentPaid().setValue(new BigDecimal("2222.88"));
        period1Owing.rentOwing().setValue(new BigDecimal("3333.77"));
        fieldsData.owedRent().rentOwingBreakdown().add(period1Owing);

        RentOwingForPeriod period2Owing = fieldsData.owedRent().rentOwingBreakdown().$();
        period2Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-01")));
        period2Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-31")));
        period2Owing.rentCharged().setValue(new BigDecimal("1111.11"));
        period2Owing.rentPaid().setValue(new BigDecimal("2222.22"));
        period2Owing.rentOwing().setValue(new BigDecimal("3333.33"));
        fieldsData.owedRent().rentOwingBreakdown().add(period2Owing);

        RentOwingForPeriod period3Owing = fieldsData.owedRent().rentOwingBreakdown().$();
        period3Owing.from().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-01")));
        period3Owing.to().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-31")));
        period3Owing.rentCharged().setValue(new BigDecimal("1111.00"));
        period3Owing.rentPaid().setValue(new BigDecimal("2222.66"));
        period3Owing.rentOwing().setValue(new BigDecimal("3333.44"));
        fieldsData.owedRent().rentOwingBreakdown().add(period3Owing);

        fieldsData.owedRent().totalRentOwing().setValue(new BigDecimal("12345.67"));

        // section 2
        NsfChargeDetails nsfDetail1 = fieldsData.owedNsfCharges().nsfChargesBreakdown().$();
        nsfDetail1.chequeAmount().setValue(new BigDecimal("6666.66"));
        nsfDetail1.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-01")));
        nsfDetail1.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2009-01-05")));
        nsfDetail1.bankCharge().setValue(new BigDecimal("10.99"));
        nsfDetail1.landlordsAdministrationCharge().setValue(new BigDecimal("16.99"));
        nsfDetail1.totalCharge().setValue(new BigDecimal("777.99"));
        fieldsData.owedNsfCharges().nsfChargesBreakdown().add(nsfDetail1);

        NsfChargeDetails nsfDetail2 = fieldsData.owedNsfCharges().nsfChargesBreakdown().$();
        nsfDetail2.chequeAmount().setValue(new BigDecimal("7776.77"));
        nsfDetail2.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-01")));
        nsfDetail2.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2010-01-05")));
        nsfDetail2.bankCharge().setValue(new BigDecimal("13.11"));
        nsfDetail2.landlordsAdministrationCharge().setValue(new BigDecimal("26.99"));
        nsfDetail2.totalCharge().setValue(new BigDecimal("712.99"));
        fieldsData.owedNsfCharges().nsfChargesBreakdown().add(nsfDetail2);

        NsfChargeDetails nsfDetail3 = fieldsData.owedNsfCharges().nsfChargesBreakdown().$();
        nsfDetail3.chequeAmount().setValue(new BigDecimal("8888.88"));
        nsfDetail3.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2018-01-01")));
        nsfDetail3.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2080-01-05")));
        nsfDetail3.bankCharge().setValue(new BigDecimal("88.81"));
        nsfDetail3.landlordsAdministrationCharge().setValue(new BigDecimal("86.98"));
        nsfDetail3.totalCharge().setValue(new BigDecimal("882.98"));
        fieldsData.owedNsfCharges().nsfChargesBreakdown().add(nsfDetail3);

        NsfChargeDetails nsfDetail4 = fieldsData.owedNsfCharges().nsfChargesBreakdown().$();
        nsfDetail4.chequeAmount().setValue(new BigDecimal("4444.44"));
        nsfDetail4.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2044-01-01")));
        nsfDetail4.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2044-01-05")));
        nsfDetail4.bankCharge().setValue(new BigDecimal("44.41"));
        nsfDetail4.landlordsAdministrationCharge().setValue(new BigDecimal("44.55"));
        nsfDetail4.totalCharge().setValue(new BigDecimal("444.44"));
        fieldsData.owedNsfCharges().nsfChargesBreakdown().add(nsfDetail4);

        NsfChargeDetails nsfDetail5 = fieldsData.owedNsfCharges().nsfChargesBreakdown().$();
        nsfDetail5.chequeAmount().setValue(new BigDecimal("5555.55"));
        nsfDetail5.dateOfCheque().setValue(new LogicalDate(DateUtils.detectDateformat("2055-01-01")));
        nsfDetail5.dateOfNsfCharge().setValue(new LogicalDate(DateUtils.detectDateformat("2055-01-05")));
        nsfDetail5.bankCharge().setValue(new BigDecimal("54.41"));
        nsfDetail5.landlordsAdministrationCharge().setValue(new BigDecimal("54.55"));
        nsfDetail5.totalCharge().setValue(new BigDecimal("355.10"));
        fieldsData.owedNsfCharges().nsfChargesBreakdown().add(nsfDetail5);

        fieldsData.owedNsfCharges().nsfTotalChargeOwed().setValue(new BigDecimal("53362.10"));

        // PART 5
        fieldsData.owedSummary().totalRentOwing().setValue(new BigDecimal("29876.54"));
        fieldsData.owedSummary().totalNsfChequeChargesOwing().setValue(new BigDecimal("9876.54"));
        fieldsData.owedSummary().total().setValue(new BigDecimal("39876.54"));

        // PART 6
        L1LandlordsContactInfo landlordsContactInfo = fieldsData.landlordsContactInfos().$();
        landlordsContactInfo.typeOfLandlord().setValue(TypeOfLandlord.Company);
        landlordsContactInfo.firstName().setValue("Art");
        landlordsContactInfo.lastName().setValue("Vanderlay");
        landlordsContactInfo.streetAddress().setValue("1100 The West Mall");
        landlordsContactInfo.unit().setValue("123");
        landlordsContactInfo.municipality().setValue("Toronto");
        landlordsContactInfo.province().setValue("on");
        landlordsContactInfo.postalCode().setValue("a1a 1a1");
        landlordsContactInfo.dayPhoneNumber().setValue("(647) 123-5555");
        landlordsContactInfo.eveningPhoneNumber().setValue("(647) 123-6666");
        landlordsContactInfo.faxNumber().setValue("(647) 123-7777");
        landlordsContactInfo.emailAddress().setValue("art.vanderlay@vanderlay-inc.com");
        fieldsData.landlordsContactInfos().add(landlordsContactInfo);

        fieldsData.agentContactInfo().firstName().setValue("Mad");
        fieldsData.agentContactInfo().lastName().setValue("Hatter");
        fieldsData.agentContactInfo().companyName().setValue("I haven't the slightest idea");
        fieldsData.agentContactInfo().mailingAddress().setValue("777 Wonderland");
        fieldsData.agentContactInfo().unit().setValue("555");
        fieldsData.agentContactInfo().municipality().setValue("Somewhere");
        fieldsData.agentContactInfo().province().setValue("on");
        fieldsData.agentContactInfo().postalCode().setValue("a1a 1a1");
        fieldsData.agentContactInfo().phoneNumber().setValue("(647) 123-5555");
        fieldsData.agentContactInfo().faxNumber().setValue("(647) 123-6666");
        fieldsData.agentContactInfo().email().setValue("mad.hatter@wonderland.net");

        // PART 7
        try {
            fieldsData.signatureData().signature().setValue(IOUtils.toByteArray(MockL1FormDataFactory.class.getResourceAsStream(SIGNATURE)));
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fieldsData.signatureData().landlordOrAgent().setValue(L1SignatureData.LandlordOrAgent.Agent);
        fieldsData.signatureData().date().setValue(new LogicalDate(DateUtils.detectDateformat("2055-01-01")));

        // L1 Schedule and Payment
        // PART1
        fieldsData.scheduleAndPayment().paymentInfo().paymentMethod().setValue(L1PaymentInfo.PaymentMethod.MasterCard);
        fieldsData.scheduleAndPayment().paymentInfo().creditCardNumber().setValue("5123 1231 1231 1231");
        fieldsData.scheduleAndPayment().paymentInfo().expiryDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-05-01")));
        fieldsData.scheduleAndPayment().paymentInfo().cardholdersName().setValue("YOZHIK V TUMANE");

        // PART2        
        fieldsData.scheduleAndPayment().appplicationSchedule().applicationPackageDeliveryMethodToLandlord()
                .setValue(ApplicationPackageDeliveryMethodToLandlord.ByMail);
        fieldsData.scheduleAndPayment().appplicationSchedule().pickupDate().setValue(new LogicalDate(DateUtils.detectDateformat("2413-01-01")));
        fieldsData.scheduleAndPayment().appplicationSchedule().officeName().setValue("whatever");
        fieldsData.scheduleAndPayment().appplicationSchedule().fax().setValue("(416) 123-1234");
        fieldsData.scheduleAndPayment().appplicationSchedule().applicationPackageDeliveryMethodToTenant()
                .setValue(ApplicationPackageDeliveryMethodToTenant.ByCourier);

        fieldsData.scheduleAndPayment().appplicationSchedule().isSameDayDeliveryToTenant().setValue(YesNo.No);
        fieldsData.scheduleAndPayment().appplicationSchedule().toTenantDeliveryDate().setValue(new LogicalDate(DateUtils.detectDateformat("2013-05-01")));

        //PART3
        fieldsData.scheduleAndPayment().languageServices().setValue(LanguageServiceType.Sign);

        return fieldsData;
    }
}
