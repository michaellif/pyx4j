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
 * Created on 2013-11-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.util.List;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.biz.legal.form.fieldadapters.MoneyShortFormatter;
import com.propertyvista.biz.legal.form.fieldadapters.MoneyShortPartitioner;
import com.propertyvista.biz.legal.form.utils.LandlordAndTenantBoardPdfFormUtils;
import com.propertyvista.domain.legal.L1FormFieldsData;
import com.propertyvista.domain.legal.utils.CanadianPostalCodePartitioner;
import com.propertyvista.domain.legal.utils.DateFormatter;
import com.propertyvista.domain.legal.utils.DatePartitioner;
import com.propertyvista.domain.legal.utils.FileNumberPartitioner;
import com.propertyvista.domain.legal.utils.MoneyFormatter;
import com.propertyvista.domain.legal.utils.MoneyPartitioner;
import com.propertyvista.domain.legal.utils.PhoneNumberPartitioner;
import com.propertyvista.domain.legal.utils.UppercaseFormatter;

public class L1FieldsMapping extends PdfFieldsMapping<L1FormFieldsData> {

    public L1FieldsMapping() {
        super(L1FormFieldsData.class);
    }

    @Override
    protected void configure() {//@formatter:off        
        money(proto().totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_rentOwn", 2, 3, 2)).define();        
        date(proto().totalRentOwingAsOf()).mapTo(fieldsPartition("@@b12c96nfl1_LastDateRentOwn", 2, 2, 4)).define();        
        date(proto().fillingDate()).mapTo("b12c96nmfiling_date").partitionBy(null).define();
        
        // PART1        
        text(proto().part1_streetNumber()).mapTo("b12c96nfapp_street_no").define();            
        text(proto().part1_streetName()).mapTo("b12c96nfapp_street_name").define();        
        text(proto().part1_streetType()).mapTo("b12c96nfapp_street_label").define();        
        text(proto().part1_direction()).mapTo("b12c96nfapp_street_direction").define();        
        text(proto().part1_unit()).mapTo("b12c96nfapp_unit_no").define();        
        text(proto().part1_municipality()).mapTo("b12c96nfapp_city").define();                
        
        text(proto().part1_postalCode())           
            .partitionBy(new CanadianPostalCodePartitioner())
                .mapTo("b12c96nfapp_postal_code_1{3}")
                .mapTo("@@b12c96nfapp_postal_code_2.0{3}")
            .define();
        
        text(proto().part1_fileNumber1())            
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_1{3}")
                .mapTo("@@b12c96nfcase_number_1.0{5}")
            .define();
        
        text(proto().part1_fileNumber2())           
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_2{3}")
                .mapTo("@@b12c96nfcase_number_2.0{5}")
             .define();
                
        // PART2        
        text(proto().part2_tenant1FirstName()).mapTo("b12c96nfP2_first_name").define();        
        text(proto().part2_tenant1LastName()).mapTo("b12c96nfP2_last_name").define();        
        field(proto().part2_tenant1Gender()).mapTo("b12c96nfP2_1_gender").define();
        
        text(proto().part2_tenant2FirstName()).mapTo("b12c96nfP2_2_first_name").define();        
        text(proto().part2_tenant2LastName()).mapTo("b12c96nfP2_2_last_name").define();        
        field(proto().part2_tenant2Gender()).mapTo("b12c96nfP2_2_gender").define();
        
        text(proto().part2_MailingAddress()).mapTo("b12c96nfP2_st_address").define();       
        text(proto().part2_unit()).mapTo("b12c96nfP2_unit_no").define();   
        text(proto().part2_municipality()).mapTo("b12c96nfP2_city").define();
        text(proto().part2_provice()).mapTo("b12c96nfP2_prov").define();        
        text(proto().part2_postalCode()).mapTo("b12c96nfP2_postal").define();
        
        phone(proto().part2_dayPhoneNumber()).mapTo(phonePartition("@@b12c96nfP2_day_phone")).define();
        phone(proto().part2_eveningPhoneNumber()).mapTo(phonePartition("@@b12c96nfP2_evg_phone")).define();        
        phone(proto().part2_faxNumber()).mapTo(phonePartition("@@b12c96nfP2_fax")).define();
        
        // TODO? in theory email's recipient is might be case sensitive, but the form requires everything CAPITALIZED
        text(proto().part2_emailAddress()).mapTo("b12c96nfP2_email").define();
                
        // PART 3
        field(proto().part3ApplyingToCollectCharges()).states("1").mapTo("b12c96nfl1_Choice_Arrear").define();
        field(proto().part3ApplyingToCollectNSF()).states("2").mapTo("b12c96nfl1_Choice_NSF").define();
        field(proto().part3IsTenatStillInPossesionOfTheUnit())
                .states("Y", "N")
                .mapTo("b12c96nfl1_Choice_possession")
            .define();
        
        field(proto().part3RentPaymentPeriod())
                .states("W", "M", "O")
                .mapTo("b12c96nfl1_Choice_Tenancy")
            .define();
        
        text(proto().part3otherRentPaymentPeriod()).mapTo("b12c96nml1_img_others").define();
        
        money(proto().part3AmountOfRentOnDeposit()).mapTo(fieldsPartition("@@b12c96nfl1_rent_deposit", 1, 3,2)).define();
        date(proto().part3dateOfDepositCollection()).mapTo(datePartition("@@b12c96nfl1_deposit_collected")).define();
        date(proto().part3lastPeriodInterestPaidFrom()).mapTo(datePartition("@@b12c96nfl1_deposit_interest_start")).define();
        date(proto().part3lastPeriodInterestPaidTo()).mapTo(datePartition("@@b12c96nfl1_deposit_interest_end")).define();
        
        // PART 4

        // section 1
        date(proto().part4period1From()).mapTo(datePartition("@@b12c96nfl1_a1_start")).define();
        date(proto().part4period1To()).mapTo(datePartition("@@b12c96nfl1_a1_end")).define();
        money(proto().part4period1RentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a1_charged", 1,3, 2)).define();
        money(proto().part4period1RentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a1_paid", 1, 3, 2)).define();
        money(proto().part4period1RentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a1_owing", 1, 3, 2)).define();

        date(proto().part4period2From()).mapTo(datePartition("@@b12c96nfl1_a2_start")).define();
        date(proto().part4period2To()).mapTo(datePartition("@@b12c96nfl1_a2_end")).define();
        money(proto().part4period2RentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a2_charged", 1,3, 2)).define();
        money(proto().part4period2RentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a2_paid", 1, 3, 2)).define();
        money(proto().part4period2RentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a2_owing", 1, 3, 2)).define();

        date(proto().part4period3From()).mapTo(datePartition("@@b12c96nfl1_a3_start")).define();
        date(proto().part4period3To()).mapTo(datePartition("@@b12c96nfl1_a3_end")).define();
        money(proto().part4period3RentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a3_charged", 1,3, 2)).define();
        money(proto().part4period3RentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a3_paid", 1, 3, 2)).define();
        money(proto().part4period3RentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a3_owing", 1, 3, 2)).define();
        
        
        money(proto().part4totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_total_rent_owed", 2, 3, 2)).define();

        // section 2
        moneyShort(proto().part4nsf1ChequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a1_chq", 4, 2)).define();
        date(proto().part4nsf1DateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d1_chq")).define();
        date(proto().part4nsf1DateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d1_nsf_l")).define();
        moneyShort(proto().part4nsf1BankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_nsf", 2, 2)).define();
        moneyShort(proto().part4nsf1LandlordsCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_adm", 2, 2)).define();
        moneyShort(proto().part4nsf1TotalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_t_charge", 3, 2)).define();
        
        moneyShort(proto().part4nsf2ChequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a2_chq", 4, 2)).define();
        date(proto().part4nsf2DateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d2_chq")).define();
        date(proto().part4nsf2DateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d2_nsf_l")).define();
        moneyShort(proto().part4nsf2BankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_nsf", 2, 2)).define();
        moneyShort(proto().part4nsf2LandlordsCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_adm", 2, 2)).define();
        moneyShort(proto().part4nsf2TotalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_t_charge", 3, 2)).define();
        
        moneyShort(proto().part4nsf3ChequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a3_chq", 4, 2)).define();
        date(proto().part4nsf3DateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d3_chq")).define();
        date(proto().part4nsf3DateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d3_nsf_l")).define();
        moneyShort(proto().part4nsf3BankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_nsf", 2, 2)).define();
        moneyShort(proto().part4nsf3LandlordsCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_adm", 2, 2)).define();
        moneyShort(proto().part4nsf3TotalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_t_charge", 3, 2)).define();
        
        moneyShort(proto().part4nsf4ChequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a4_chq", 4, 2)).define();
        date(proto().part4nsf4DateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d4_chq")).define();
        date(proto().part4nsf4DateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d4_nsf_l")).define();
        moneyShort(proto().part4nsf4BankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_nsf", 2, 2)).define();
        moneyShort(proto().part4nsf4LandlordsCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_adm", 2, 2)).define();
        moneyShort(proto().part4nsf4TotalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_t_charge", 3, 2)).define();
        
        moneyShort(proto().part4nsf5ChequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a5_chq", 4, 2)).define();
        date(proto().part4nsf5DateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d5_chq")).define();
        date(proto().part4nsf5DateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d5_nsf_l")).define();
        moneyShort(proto().part4nsf5BankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_nsf", 2, 2)).define();
        moneyShort(proto().part4nsf5LandlordsCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_adm", 2, 2)).define();
        moneyShort(proto().part4nsf5TotalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_t_charge", 3, 2)).define();
        
        money(proto().part4nsfTotalChargeOwed()).mapTo(fieldsPartition("@@b12c96nfl1_t_nsfadm_charge", 2, 3, 2)).define();        
    }//@formatter:on

    /** generate names of partitioned fields according to convention used in L1 form */
    private List<String> fieldsPartition(String fieldName, int... partLengthsVector) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, partLengthsVector);
    }

    private List<String> phonePartition(String fieldName) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, 3, 3, 4);
    }

    private List<String> datePartition(String fieldName) {
        return LandlordAndTenantBoardPdfFormUtils.split(fieldName, 2, 2, 4);
    }

    private PdfFieldDescriptorBuilder money(IObject<?> member) {
        return field(member).formatBy(new MoneyFormatter()).partitionBy(new MoneyPartitioner());
    }

    private PdfFieldDescriptorBuilder moneyShort(IObject<?> member) {
        return field(member).formatBy(new MoneyShortFormatter()).partitionBy(new MoneyShortPartitioner());
    }

    private PdfFieldDescriptorBuilder date(IObject<?> member) {
        return field(member).formatBy(new DateFormatter()).partitionBy(new DatePartitioner());
    }

    private PdfFieldDescriptorBuilder text(IObject<?> member) {
        return field(member).formatBy(new UppercaseFormatter());
    }

    private PdfFieldDescriptorBuilder phone(IObject<?> member) {
        return field(member).partitionBy(new PhoneNumberPartitioner());
    }

}
