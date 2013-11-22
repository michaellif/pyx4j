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
package com.propertyvista.biz.legal.forms.l1;

import java.util.Arrays;

import com.propertyvista.biz.legal.forms.ltbcommon.LtbFormFieldsMapping;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.CreditCardExpiryDateFormatter;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.CreditCardExpiryDatePartitioner;
import com.propertyvista.domain.legal.NsfChargeDetails;
import com.propertyvista.domain.legal.RentOwingForPeriod;
import com.propertyvista.domain.legal.l1.L1AgentContactInfo;
import com.propertyvista.domain.legal.l1.L1ApplicationSchedule;
import com.propertyvista.domain.legal.l1.L1FormFieldsData;
import com.propertyvista.domain.legal.l1.L1OwedNsfCharges;
import com.propertyvista.domain.legal.l1.L1OwedRent;
import com.propertyvista.domain.legal.l1.L1OwedSummary;
import com.propertyvista.domain.legal.l1.L1PaymentInfo;
import com.propertyvista.domain.legal.l1.L1ReasonForApplication;
import com.propertyvista.domain.legal.l1.L1RentalUnitInfo;
import com.propertyvista.domain.legal.l1.L1ScheduleAndPayment;
import com.propertyvista.domain.legal.l1.L1SignatureData;
import com.propertyvista.domain.legal.l1.L1TenantContactInfo;
import com.propertyvista.domain.legal.l1.L1TenantInfo;
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
        mapping(proto().rentalUnitInfo(), new LtbFormFieldsMapping<L1RentalUnitInfo>(L1RentalUnitInfo.class) {@Override protected void configure() {
            text(proto().streetNumber()).mapTo("b12c96nfapp_street_no").define();            
            text(proto().streetName()).mapTo("b12c96nfapp_street_name").define();        
            text(proto().streetType()).mapTo("b12c96nfapp_street_label").define();        
            text(proto().direction()).mapTo("b12c96nfapp_street_direction").define();        
            text(proto().unit()).mapTo("b12c96nfapp_unit_no").define();        
            text(proto().municipality()).mapTo("b12c96nfapp_city").define();                
            
            text(proto().postalCode())           
                .partitionBy(new CanadianPostalCodePartitioner())
                    .mapTo("b12c96nfapp_postal_code_1{3}", "@@b12c96nfapp_postal_code_2.0{3}")
                .define();
            
            text(proto().relatedApplicationFileNumber1())            
                .partitionBy(new FileNumberPartitioner())
                    .mapTo("b12c96nfdivision_code_1{3}", "@@b12c96nfcase_number_1.0{5}")
                .define();
            
            text(proto().relatedApplicationFileNumber2())           
                .partitionBy(new FileNumberPartitioner())
                    .mapTo("b12c96nfdivision_code_2{3}", "@@b12c96nfcase_number_2.0{5}")
                 .define();
        }});
        
        // PART2
        table(proto().tenants()).rowMapping(Arrays.<LtbFormFieldsMapping<L1TenantInfo>>asList(
                new LtbFormFieldsMapping<L1TenantInfo>(L1TenantInfo.class) { @Override protected void configure() {
                    text(proto().firstName()).mapTo("b12c96nfP2_first_name").define();        
                    text(proto().lastName()).mapTo("b12c96nfP2_last_name").define();        
                    field(proto().gender()).states("M", "F").mapTo("b12c96nfP2_1_gender").define();                                       
                }},
                new LtbFormFieldsMapping<L1TenantInfo>(L1TenantInfo.class) { @Override protected void configure() {
                    text(proto().firstName()).mapTo("b12c96nfP2_2_first_name").define();        
                    text(proto().lastName()).mapTo("b12c96nfP2_2_last_name").define();        
                    field(proto().gender()).states("M", "F").mapTo("b12c96nfP2_2_gender").define();                                       
                }}                
        )).define();
        
        mapping(proto().tenantContactInfo(), new LtbFormFieldsMapping<L1TenantContactInfo>(L1TenantContactInfo.class) { @Override protected void configure() {
            text(proto().mailingAddress()).mapTo("b12c96nfP2_st_address").define();       
            text(proto().unit()).mapTo("b12c96nfP2_unit_no").define();   
            text(proto().municipality()).mapTo("b12c96nfP2_city").define();
            text(proto().province()).mapTo("b12c96nfP2_prov").define();        
            text(proto().postalCode()).mapTo("b12c96nfP2_postal").define();
            
            phone(proto().dayPhoneNumber()).mapTo(phonePartition("@@b12c96nfP2_day_phone")).define();
            phone(proto().eveningPhoneNumber()).mapTo(phonePartition("@@b12c96nfP2_evg_phone")).define();        
            phone(proto().faxNumber()).mapTo(phonePartition("@@b12c96nfP2_fax")).define();
            
            // TODO? in theory email's recipient is might be case sensitive, but the form requires everything CAPITALIZED
            text(proto().emailAddress()).mapTo("b12c96nfP2_email").define();            
        }});
                
        // PART 3
        mapping(proto().reasonForApplication(), new LtbFormFieldsMapping<L1ReasonForApplication>(L1ReasonForApplication.class) { @Override protected void configure() {
            field(proto().applyingToCollectCharges()).states("1").mapTo("b12c96nfl1_Choice_Arrear").define();
            field(proto().applyingToCollectNsf()).states("2").mapTo("b12c96nfl1_Choice_NSF").define();
            field(proto().isTenatStillInPossesionOfTheUnit())
                    .states("Y", "N")
                    .mapTo("b12c96nfl1_Choice_possession")
                .define();
            
            field(proto().rentPaymentPeriod())
                    .states("W", "M", "O")
                    .mapTo("b12c96nfl1_Choice_Tenancy")
                .define();
            
            text(proto().otherRentPaymentPeriodDescription()).mapTo("b12c96nml1_img_others").define();
            
            money(proto().amountOfRentOnDeposit()).mapTo(fieldsPartition("@@b12c96nfl1_rent_deposit", 1, 3,2)).define();
            date(proto().dateOfDepositCollection()).mapTo(datePartition("@@b12c96nfl1_deposit_collected")).define();
            date(proto().lastPeriodInterestPaidFrom()).mapTo(datePartition("@@b12c96nfl1_deposit_interest_start")).define();
            date(proto().lastPeriodInterestPaidTo()).mapTo(datePartition("@@b12c96nfl1_deposit_interest_end")).define();                                     
        }});
        
        // PART 4
        mapping(proto().owedRent(), new LtbFormFieldsMapping<L1OwedRent>(L1OwedRent.class) {
            @Override
            protected void configure() {
                table(proto().rentOwingBreakdown()).rowMapping(Arrays.<LtbFormFieldsMapping<RentOwingForPeriod>>asList(
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
                
                money(proto().totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_total_rent_owed", 2, 3, 2)).define();
            }
        });

        // section 2
        mapping(proto().owedNsfCharges(), new LtbFormFieldsMapping<L1OwedNsfCharges>(L1OwedNsfCharges.class) {
            @Override
            protected void configure() {
                table(proto().nsfChargesBreakdown()).rowMapping(Arrays.<LtbFormFieldsMapping<NsfChargeDetails>>asList(
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
                
                money(proto().nsfTotalChargeOwed()).mapTo(fieldsPartition("@@b12c96nfl1_t_nsfadm_charge", 2, 3, 2)).define();
            }
        });
        
        // PART 5
        mapping(proto().owedSummary(), new LtbFormFieldsMapping<L1OwedSummary>(L1OwedSummary.class) {
            @Override
            protected void configure() {
                money(proto().totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfl1_renttotal", 2, 3, 2)).define();
                money(proto().totalNsfChequeChargesOwing()).mapTo(fieldsPartition("@@b12c96nfl1_nsf_total", 1, 3, 2)).define();
                money(proto().total()).mapTo(fieldsPartition("@@b12c96nfl1_totalown", 2, 3, 2)).define();
            }
        });
        
        // PART 6
        table(proto().landlordsContactInfos()).rowMapping(Arrays.<LtbFormFieldsMapping<L1LandlordsContactInfo>>asList(
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
        
        mapping(proto().agentContactInfo(), new LtbFormFieldsMapping<L1AgentContactInfo>(L1AgentContactInfo.class) {
            @Override
            protected void configure() {
                text(proto().firstName()).mapTo("b12c96nfpersonnel_first_name").define();
                text(proto().lastName()).mapTo("b12c96nfpersonnel_last_name").define();
                text(proto().companyName()).mapTo("b12c96nforg_name").define();
                text(proto().mailingAddress()).mapTo("b12c96nforg_address").define();
                text(proto().unit()).mapTo("b12c96nforg_unit_no").define();
                text(proto().municipality()).mapTo("b12c96nforg_city").define();
                text(proto().province()).mapTo("b12c96nforg_prov").define();
                text(proto().postalCode()).mapTo("b12c96nforg_postal").define();
                phone(proto().phoneNumber()).mapTo(phonePartition("@@b12c96nfpersonnel_phone")).define();
                phone(proto().faxNumber()).mapTo(phonePartition("@@b12c96nfpersonnel_fax_number")).define();
                text(proto().email()).mapTo("b12c96nfpersonnel_email").define();            }
        });
        
        
        // PART 7
        mapping(proto().signatureData(), new LtbFormFieldsMapping<L1SignatureData>(L1SignatureData.class) {
            @Override
            protected void configure() {
                field(proto().signature()).mapTo("b12c96nmlandlord_agent_signature").define();
                field(proto().landlordOrAgent()).states("L", "A").mapTo("b12c96nfsigned_by").define();
                date(proto().date()).mapTo(datePartition("@@b12c96nfdate_signed")).define();            }
        });
        
        
        // L1 Payment and Schedule Subform Fields
        mapping(proto().scheduleAndPayment(), new LtbFormFieldsMapping<L1ScheduleAndPayment>(L1ScheduleAndPayment.class) {
            @Override
            protected void configure() {
                mapping(proto().paymentInfo(), new LtbFormFieldsMapping<L1PaymentInfo>(L1PaymentInfo.class) {
                    @Override
                    protected void configure() {
                        field(proto().paymentMethod()).states("CASH", "DEBT", "MORD", "CHEQ", "VI", "MAST", "AMEX").mapTo("b12c96nfpayment_method").define();
                        text(proto().creditCardNumber()).mapTo("b12c96nfcardholder_card_no").define();
                        field(proto().expiryDate())
                            .formatBy(new CreditCardExpiryDateFormatter())
                            .partitionBy(new CreditCardExpiryDatePartitioner())
                            .mapTo("@@b12c96nfcreditcard_expiry_date_mm.0{2}", "b12c96nfcreditcard_expiry_date_yy{2}")
                            .define();
                        text(proto().cardholdersName()).mapTo("b12c96nfcardholder_name").define();                
                    }
                });
                
                mapping(proto().appplicationSchedule(), new LtbFormFieldsMapping<L1ApplicationSchedule>(L1ApplicationSchedule.class) {
                    @Override
                    protected void configure() {
                        field(proto().applicationPackageDeliveryMethodToLandlord())
                            .states("T", "M", "F")
                            .mapTo("b12c96nfdeliver_NOH_method")
                            .define();        
                        date(proto().pickupDate()).mapTo(datePartition("@@b12c96nfdeliver_NOH_pickup_date")).define();
                        text(proto().officeName()).mapTo("b12c96nfdeliver_NOH_pickup_Office").define();
                        phone(proto().fax()).mapTo(phonePartition("@@b12c96nfdeliver_NOH_fax_no")).define();
                        field(proto().isSameDayDeliveryToTenant()).states("y", "n").mapTo("b12c96nfServeWhenReceived").define();
                        date(proto().toTenantDeliveryDate()).mapTo(datePartition("@@b12c96nfdate_of_service")).define();
                        field(proto().applicationPackageDeliveryMethodToTenant()).states("M", "C", "A").mapTo("b12c96nfmethod_of_service").define();                
                    }
                });
                
                field(proto().languageServices()).states("F", "S").mapTo("b12c96nflanguage").define();
            }            
        });
        
        
        
    }//@formatter:on
}
