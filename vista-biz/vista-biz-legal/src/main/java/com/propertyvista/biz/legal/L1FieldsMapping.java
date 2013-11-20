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

import java.util.Arrays;

import com.propertyvista.biz.legal.form.fieldadapters.CreditCardExpiryDateFormatter;
import com.propertyvista.biz.legal.form.fieldadapters.CreditCardExpiryDatePartitioner;
import com.propertyvista.domain.legal.L1FormFieldsData;
import com.propertyvista.domain.legal.NsfChargeDetails;
import com.propertyvista.domain.legal.RentOwingForPeriod;
import com.propertyvista.domain.legal.utils.CanadianPostalCodePartitioner;
import com.propertyvista.domain.legal.utils.FileNumberPartitioner;
import com.propertyvista.domain.legal.utils.L1LandlordsContactInfo;

public class L1FieldsMapping extends LtbFormFieldsMapping<L1FormFieldsData> {

    public L1FieldsMapping() {
        super(L1FormFieldsData.class);
    }

    @SuppressWarnings("unchecked")
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
                .mapTo("b12c96nfapp_postal_code_1{3}", "@@b12c96nfapp_postal_code_2.0{3}")
            .define();
        
        text(proto().part1_fileNumber1())            
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_1{3}", "@@b12c96nfcase_number_1.0{5}")
            .define();
        
        text(proto().part1_fileNumber2())           
            .partitionBy(new FileNumberPartitioner())
                .mapTo("b12c96nfdivision_code_2{3}", "@@b12c96nfcase_number_2.0{5}")
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
        table(proto().part4_rentOwingBreakdown()).rowMapping(Arrays.<LtbFormFieldsMapping<RentOwingForPeriod>>asList(
                new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                    date(proto().from()).mapTo(datePartition("@@b12c96nfl1_a1_start")).define();
                    date(proto().to()).mapTo(datePartition("@@b12c96nfl1_a1_end")).define();
                    money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a1_charged", 1,3, 2)).define();
                    money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a1_paid", 1, 3, 2)).define();
                    money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a1_owing", 1, 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                    date(proto().from()).mapTo(datePartition("@@b12c96nfl1_a2_start")).define();
                    date(proto().to()).mapTo(datePartition("@@b12c96nfl1_a2_end")).define();
                    money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a2_charged", 1,3, 2)).define();
                    money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a2_paid", 1, 3, 2)).define();
                    money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a2_owing", 1, 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                    date(proto().from()).mapTo(datePartition("@@b12c96nfl1_a3_start")).define();
                    date(proto().to()).mapTo(datePartition("@@b12c96nfl1_a3_end")).define();
                    money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfl1_a3_charged", 1,3, 2)).define();
                    money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfl1_a3_paid", 1, 3, 2)).define();
                    money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_a3_owing", 1, 3, 2)).define();                        
                }}
        )).define();        
        
        money(proto().part4_totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_total_rent_owed", 2, 3, 2)).define();

        // section 2
        table(proto().part4_nsfChargesBreakdown()).rowMapping(Arrays.<LtbFormFieldsMapping<NsfChargeDetails>>asList(
                new LtbFormFieldsMapping<NsfChargeDetails>(NsfChargeDetails.class) {@Override protected void configure() {
                    moneyShort(proto().chequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a1_chq", 4, 2)).define();
                    date(proto().dateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d1_chq")).define();
                    date(proto().dateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d1_nsf_l")).define();
                    moneyShort(proto().bankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_nsf", 2, 2)).define();
                    moneyShort(proto().landlordsAdministrationCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_adm", 2, 2)).define();
                    moneyShort(proto().totalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a1_t_charge", 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<NsfChargeDetails>(NsfChargeDetails.class) {@Override protected void configure() {
                    moneyShort(proto().chequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a2_chq", 4, 2)).define();
                    date(proto().dateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d2_chq")).define();
                    date(proto().dateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d2_nsf_l")).define();
                    moneyShort(proto().bankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_nsf", 2, 2)).define();
                    moneyShort(proto().landlordsAdministrationCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_adm", 2, 2)).define();
                    moneyShort(proto().totalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a2_t_charge", 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<NsfChargeDetails>(NsfChargeDetails.class) {@Override protected void configure() {
                    moneyShort(proto().chequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a3_chq", 4, 2)).define();
                    date(proto().dateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d3_chq")).define();
                    date(proto().dateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d3_nsf_l")).define();
                    moneyShort(proto().bankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_nsf", 2, 2)).define();
                    moneyShort(proto().landlordsAdministrationCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_adm", 2, 2)).define();
                    moneyShort(proto().totalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a3_t_charge", 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<NsfChargeDetails>(NsfChargeDetails.class) {@Override protected void configure() {
                    moneyShort(proto().chequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a4_chq", 4, 2)).define();
                    date(proto().dateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d4_chq")).define();
                    date(proto().dateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d4_nsf_l")).define();
                    moneyShort(proto().bankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_nsf", 2, 2)).define();
                    moneyShort(proto().landlordsAdministrationCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_adm", 2, 2)).define();
                    moneyShort(proto().totalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a4_t_charge", 3, 2)).define();                        
                }},
                new LtbFormFieldsMapping<NsfChargeDetails>(NsfChargeDetails.class) {@Override protected void configure() {
                    moneyShort(proto().chequeAmount()).mapTo(fieldsPartition("@@b12c96nfl1_a5_chq", 4, 2)).define();
                    date(proto().dateOfCheque()).mapTo(datePartition("@@b12c96nfl1_d5_chq")).define();
                    date(proto().dateOfNsfCharge()).mapTo(datePartition("@@b12c96nfl1_d5_nsf_l")).define();
                    moneyShort(proto().bankCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_nsf", 2, 2)).define();
                    moneyShort(proto().landlordsAdministrationCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_adm", 2, 2)).define();
                    moneyShort(proto().totalCharge()).mapTo(fieldsPartition("@@b12c96nfl1_a5_t_charge", 3, 2)).define();                        
                }}
        )).define();
        
        money(proto().part4_nsfTotalChargeOwed()).mapTo(fieldsPartition("@@b12c96nfl1_t_nsfadm_charge", 2, 3, 2)).define();
        
        // PART 5
        money(proto().part5_TotalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_renttotal", 2, 3, 2)).define();
        money(proto().part5_TotalNsfChequeChargesOwing()).mapTo(fieldsPartition("@@b12c96nfl1_nsf_total", 1, 3, 2)).define();
        money(proto().part5_Total()).mapTo(fieldsPartition("@@b12c96nfl1_totalown", 2, 3, 2)).define();
        
        // PART 6
        table(proto().part6_landlordsContactInfos()).rowMapping(Arrays.<LtbFormFieldsMapping<L1LandlordsContactInfo>>asList(
                new LtbFormFieldsMapping<L1LandlordsContactInfo>(L1LandlordsContactInfo.class) {@Override protected void configure() {
                    field(proto().typeOfLandlord())
                        .states("M", "F", "G")
                        .mapTo("b12c96nfP1_gender")
                    .define();
                    
                    text(proto().firstName()).mapTo("b12c96nfP1_first_name").define();
                    text(proto().lastName()).mapTo("b12c96nfP1_last_name").define();
                    text(proto().streetAddress()).mapTo("b12c96nfP1_st_address").define();
                    text(proto().unit()).mapTo("b12c96nfP1_unit_no").define();
                    text(proto().municipality()).mapTo("b12c96nfP1_city").define();
                    text(proto().province()).mapTo("b12c96nfP1_prov").define();
                    text(proto().postalCode()).mapTo("b12c96nfP1_postal").define();
                    phone(proto().dayPhoneNumber()).mapTo(phonePartition("@@b12c96nfP1_day_phone")).define();
                    phone(proto().eveningPhoneNumber()).mapTo(phonePartition("@@b12c96nfP1_evg_phone")).define();
                    phone(proto().faxNumber()).mapTo(phonePartition("@@b12c96nfP1_fax")).define();
                    text(proto().emailAddress()).mapTo("b12c96nfP1_email").define();
                }}
        )).define();
        
        text(proto().part6_agentsFirstName()).mapTo("b12c96nfpersonnel_first_name").define();
        text(proto().part6_agentsLastName()).mapTo("b12c96nfpersonnel_last_name").define();
        text(proto().part6_agentsCompanyName()).mapTo("b12c96nforg_name").define();
        text(proto().part6_agentsMailingAddress()).mapTo("b12c96nforg_address").define();
        text(proto().part6_agentsUnit()).mapTo("b12c96nforg_unit_no").define();
        text(proto().part6_agentsMunicipality()).mapTo("b12c96nforg_city").define();
        text(proto().part6_agentsProvince()).mapTo("b12c96nforg_prov").define();
        text(proto().part6_agentsPostalCode()).mapTo("b12c96nforg_postal").define();
        phone(proto().part6_agentsPhoneNumber()).mapTo(phonePartition("@@b12c96nfpersonnel_phone")).define();
        phone(proto().part6_agentsFaxNumber()).mapTo(phonePartition("@@b12c96nfpersonnel_fax_number")).define();
        text(proto().part6_agentsEmail()).mapTo("b12c96nfpersonnel_email").define();
        
        
        // PART 7
        field(proto().part7_signature()).mapTo("b12c96nmlandlord_agent_signature").define();
        field(proto().part7_landlordOrAgent()).states("L", "A").mapTo("b12c96nfsigned_by").define();
        date(proto().part7_date()).mapTo(datePartition("@@b12c96nfdate_signed")).define();
        
        
        // L1 Payment and Schedule Subform Fields
        field(proto().section2_part1_paymentMethod()).states("CASH", "DEBT", "MORD", "CHEQ", "VI", "MAST", "AMEX").mapTo("b12c96nfpayment_method").define();
        text(proto().section2_part1_creditCardNumber()).mapTo("b12c96nfcardholder_card_no").define();
        field(proto().section2_part1_expiryDate())
            .formatBy(new CreditCardExpiryDateFormatter())
            .partitionBy(new CreditCardExpiryDatePartitioner())
            .mapTo("@@b12c96nfcreditcard_expiry_date_mm.0{2}", "b12c96nfcreditcard_expiry_date_yy{2}")
            .define();
        text(proto().section2_part1_cardholdersName()).mapTo("b12c96nfcardholder_name").define();
        
    }//@formatter:on
}
