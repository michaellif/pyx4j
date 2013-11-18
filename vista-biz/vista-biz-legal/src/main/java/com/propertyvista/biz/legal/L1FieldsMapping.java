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
        
        field(proto().totalRentOwing())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_rentOwn.0{2}")
                .mapTo("@@b12c96nfl1_rentOwn.1{3}")
                .mapTo("@@b12c96nfl1_rentOwn.2{2}")
            .define();
        
        field(proto().totalRentOwingAsOf())
            .formatBy(new DateFormatter())
            .partitionBy(new DatePartitioner())
                .mapTo("@@b12c96nfl1_LastDateRentOwn.0{2}")
                .mapTo("@@b12c96nfl1_LastDateRentOwn.1{2}")
                .mapTo("@@b12c96nfl1_LastDateRentOwn.2{4}")
            .define();
        
        field(proto().fillingDate())
            .formatBy(new DateFormatter())
                .mapTo("b12c96nmfiling_date")
            .define();
        
        // PART1
        
        field(proto().part1_streetNumber())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_street_no")
            .define();    
        
        field(proto().part1_streetName())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_street_name")
            .define();
        
        field(proto().part1_streetType())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_street_label")
            .define();
        
        field(proto().part1_direction())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_street_direction")
            .define();
        
        field(proto().part1_unit())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_unit_no")
            .define();
        
        field(proto().part1_municipality())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfapp_city")
            .define();                
        
        field(proto().part1_postalCode())
            .formatBy(new UppercaseFormatter())
            .partitionBy(new CanadianPostalCodePartitioner())
                .mapTo("b12c96nfapp_postal_code_1{3}")
                .mapTo("@@b12c96nfapp_postal_code_2.0{3}")
            .define();
        
        field(proto().part1_fileNumber1())
            .formatBy(new UppercaseFormatter())
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_1{3}")
                .mapTo("@@b12c96nfcase_number_1.0{5}")
            .define();
        
        field(proto().part1_fileNumber2())
            .formatBy(new UppercaseFormatter())
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_2{3}")
                .mapTo("@@b12c96nfcase_number_2.0{5}")
             .define();
                
        // PART2
        
        field(proto().part2_tenant1FirstName())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_first_name")
            .define();
        
        field(proto().part2_tenant1LastName())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_last_name")
            .define();
        
        field(proto().part2_tenant1Gender())
                .mapTo("b12c96nfP2_1_gender")
            .define();
        
        field(proto().part2_tenant2FirstName())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_2_first_name")
            .define();
        
        field(proto().part2_tenant2LastName())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_2_last_name")
            .define();
        
        field(proto().part2_tenant2Gender())
                .mapTo("b12c96nfP2_2_gender")
             .define();
        
        field(proto().part2_MailingAddress())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_st_address")
             .define();
        
        
        field(proto().part2_unit())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_unit_no")
            .define();
        
        field(proto().part2_municipality())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_city")
            .define();
        
        field(proto().part2_provice())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_prov")
            .define();
        
        field(proto().part2_postalCode())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_postal")
            .define();
        
        field(proto().part2_dayPhoneNumber())
            .partitionBy(new PhoneNumberPartitioner())
                .mapTo("@@b12c96nfP2_day_phone.0{3}")
                .mapTo("@@b12c96nfP2_day_phone.1{3}")
                .mapTo("@@b12c96nfP2_day_phone.2{4}")
            .define();
        
        field(proto().part2_eveningPhoneNumber())
            .partitionBy(new PhoneNumberPartitioner())
                .mapTo("@@b12c96nfP2_evg_phone.0{3}")
                .mapTo("@@b12c96nfP2_evg_phone.1{3}")
                .mapTo("@@b12c96nfP2_evg_phone.2{4}")
            .define();
        
        field(proto().part2_faxNumber())
            .partitionBy(new PhoneNumberPartitioner())
                .mapTo("@@b12c96nfP2_fax.0{3}")
                .mapTo("@@b12c96nfP2_fax.1{3}")
                .mapTo("@@b12c96nfP2_fax.2{4}")
            .define();
        
        // TODO? in theory email's recipent is might be case sensitive, but the form requires everything CAPITALIZED
        field(proto().part2_emailAddress())
            .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nfP2_email")
            .define();
        
        
        // PART 3
        field(proto().part3ApplyingToCollectCharges())
                .states("1")
                .mapTo("b12c96nfl1_Choice_Arrear")
            .define();
        
        field(proto().part3ApplyingToCollectNSF())
                .states("2")
                .mapTo("b12c96nfl1_Choice_NSF")
            .define();
        
        field(proto().part3IsTenatStillInPossesionOfTheUnit())
                .states("Y", "N")
                .mapTo("b12c96nfl1_Choice_possession")
            .define();
        
        field(proto().part3RentPaymentPeriod())
                .states("W", "M", "O")
                .mapTo("b12c96nfl1_Choice_Tenancy")
            .define();
        
        field(proto().part3otherRentPaymentPeriod())
                .formatBy(new UppercaseFormatter())
                .mapTo("b12c96nml1_img_others")
            .define();
        
        field(proto().part3AmountOfRentOnDeposit())
                .formatBy(new MoneyFormatter())
                .partitionBy(new MoneyPartitioner())
                    .mapTo("@@b12c96nfl1_rent_deposit.0{1}")
                    .mapTo("@@b12c96nfl1_rent_deposit.1{3}")
                    .mapTo("@@b12c96nfl1_rent_deposit.2{2}")
                .define();
        
        field(proto().part3dateOfDepositCollection())
                .formatBy(new DateFormatter())
                .partitionBy(new DatePartitioner())
                    .mapTo("@@b12c96nfl1_deposit_collected.0{2}")
                    .mapTo("@@b12c96nfl1_deposit_collected.1{2}")
                    .mapTo("@@b12c96nfl1_deposit_collected.2{4}")
                .define();
        
        field(proto().part3lastPeriodInterestPaidFrom())
                .formatBy(new DateFormatter())
                .partitionBy(new DatePartitioner())
                    .mapTo("@@b12c96nfl1_deposit_interest_start.0{2}")
                    .mapTo("@@b12c96nfl1_deposit_interest_start.1{2}")
                    .mapTo("@@b12c96nfl1_deposit_interest_start.2{4}")
                .define();
        
        field(proto().part3lastPeriodInterestPaidTo())
                .formatBy(new DateFormatter())
                .partitionBy(new DatePartitioner())
                    .mapTo("@@b12c96nfl1_deposit_interest_end.0{2}")
                    .mapTo("@@b12c96nfl1_deposit_interest_end.1{2}")
                    .mapTo("@@b12c96nfl1_deposit_interest_end.2{4}")
                .define();
        
        // PART 4

        field(proto().part4period1From())
                .formatBy(new DateFormatter())
                .partitionBy(new DatePartitioner())
                    .mapTo("@@b12c96nfl1_a1_start.0{2}")
                    .mapTo("@@b12c96nfl1_a1_start.1{2}")
                    .mapTo("@@b12c96nfl1_a1_start.2{4}")
                .define();
        
        field(proto().part4period1To())
                .formatBy(new DateFormatter())
                .partitionBy(new DatePartitioner())
                    .mapTo("@@b12c96nfl1_a1_end.0{2}")
                    .mapTo("@@b12c96nfl1_a1_end.1{2}")
                    .mapTo("@@b12c96nfl1_a1_end.2{4}")
                .define();
        
        field(proto().part4period1RentCharged())
                .formatBy(new MoneyFormatter())
                .partitionBy(new MoneyPartitioner())
                    .mapTo("@@b12c96nfl1_a1_charged.0{1}")
                    .mapTo("@@b12c96nfl1_a1_charged.1{3}")
                    .mapTo("@@b12c96nfl1_a1_charged.2{2}")
                .define();
        
        field(proto().part4period1RentPaid())
                .formatBy(new MoneyFormatter())
                .partitionBy(new MoneyPartitioner())
                    .mapTo("@@b12c96nfl1_a1_paid.0{1}")
                    .mapTo("@@b12c96nfl1_a1_paid.1{3}")
                    .mapTo("@@b12c96nfl1_a1_paid.2{2}")
                .define();
        
        field(proto().part4period1RentOwing())
                .formatBy(new MoneyFormatter())
                .partitionBy(new MoneyPartitioner())
                    .mapTo("@@b12c96nfl1_a1_owing.0{1}")
                    .mapTo("@@b12c96nfl1_a1_owing.1{3}")
                    .mapTo("@@b12c96nfl1_a1_owing.2{2}")
                .define();
        
        field(proto().part4period2From())
            .formatBy(new DateFormatter())
            .partitionBy(new DatePartitioner())
                .mapTo("@@b12c96nfl1_a2_start.0{2}")
                .mapTo("@@b12c96nfl1_a2_start.1{2}")
                .mapTo("@@b12c96nfl1_a2_start.2{4}")
            .define();
        
        field(proto().part4period2To())
            .formatBy(new DateFormatter())
            .partitionBy(new DatePartitioner())
                .mapTo("@@b12c96nfl1_a2_end.0{2}")
                .mapTo("@@b12c96nfl1_a2_end.1{2}")
                .mapTo("@@b12c96nfl1_a2_end.2{4}")
            .define();
        
        field(proto().part4period2RentCharged())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a2_charged.0{1}")
                .mapTo("@@b12c96nfl1_a2_charged.1{3}")
                .mapTo("@@b12c96nfl1_a2_charged.2{2}")
            .define();
        
        field(proto().part4period2RentPaid())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a2_paid.0{1}")
                .mapTo("@@b12c96nfl1_a2_paid.1{3}")
                .mapTo("@@b12c96nfl1_a2_paid.2{2}")
            .define();
        
        field(proto().part4period2RentOwing())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a2_owing.0{1}")
                .mapTo("@@b12c96nfl1_a2_owing.1{3}")
                .mapTo("@@b12c96nfl1_a2_owing.2{2}")
            .define();
        
        field(proto().part4period3From())
            .formatBy(new DateFormatter())
            .partitionBy(new DatePartitioner())
                .mapTo("@@b12c96nfl1_a3_start.0{2}")
                .mapTo("@@b12c96nfl1_a3_start.1{2}")
                .mapTo("@@b12c96nfl1_a3_start.2{4}")
            .define();
        
        field(proto().part4period3To())
            .formatBy(new DateFormatter())
            .partitionBy(new DatePartitioner())
                .mapTo("@@b12c96nfl1_a3_end.0{2}")
                .mapTo("@@b12c96nfl1_a3_end.1{2}")
                .mapTo("@@b12c96nfl1_a3_end.2{4}")
            .define();
        
        field(proto().part4period3RentCharged())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a3_charged.0{1}")
                .mapTo("@@b12c96nfl1_a3_charged.1{3}")
                .mapTo("@@b12c96nfl1_a3_charged.2{2}")
            .define();
        
        field(proto().part4period3RentPaid())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a3_paid.0{1}")
                .mapTo("@@b12c96nfl1_a3_paid.1{3}")
                .mapTo("@@b12c96nfl1_a3_paid.2{2}")
            .define();
            
        field(proto().part4period3RentOwing())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_a3_owing.0{1}")
                .mapTo("@@b12c96nfl1_a3_owing.1{3}")
                .mapTo("@@b12c96nfl1_a3_owing.2{2}")
            .define();
        
        field(proto().part4totalRentOwing())
            .formatBy(new MoneyFormatter())
            .partitionBy(new MoneyPartitioner())
                .mapTo("@@b12c96nfl1_total_rent_owed.0{2}")
                .mapTo("@@b12c96nfl1_total_rent_owed.1{3}")
                .mapTo("@@b12c96nfl1_total_rent_owed.2{2}")
            .define();

    }//@formatter:on
}
