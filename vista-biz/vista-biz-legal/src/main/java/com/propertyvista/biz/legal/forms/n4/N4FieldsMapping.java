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
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.forms.n4;

import java.util.Arrays;

import com.propertyvista.biz.legal.forms.ltbcommon.LtbFormFieldsMapping;
import com.propertyvista.biz.legal.forms.ltbcommon.fieldadapters.partitioners.CanadianPostalCodePartitioner;
import com.propertyvista.domain.legal.ltbcommon.LtbAgentContactInfo;
import com.propertyvista.domain.legal.ltbcommon.LtbOwedRent;
import com.propertyvista.domain.legal.ltbcommon.LtbRentalUnitAddress;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4Signature;

public class N4FieldsMapping extends LtbFormFieldsMapping<N4FormFieldsData> {

    public N4FieldsMapping() {
        super(N4FormFieldsData.class);
    }

    @Override
    protected void configure() {//@formatter:off        
        field(proto().to()).mapTo("Text1").define();
        field(proto().from()).mapTo("Text2").define();
        
        mapping(proto().rentalUnitAddress(), new LtbFormFieldsMapping<LtbRentalUnitAddress>(LtbRentalUnitAddress.class) {
            @Override
            protected void configure() {
                text(proto().streetNumber()).mapTo("b12c96nfn4_app_street_no").define();
                text(proto().streetName()).mapTo("b12c96nfn4_app_street_name").define();
                text(proto().streetType()).mapTo("b12c96nfn4_app_street_label").define();
                text(proto().direction()).mapTo("b12c96nfn4_app_street_direction").define();
                text(proto().unit()).mapTo("b12c96nfn4_app_unit_no").define();
                text(proto().municipality()).mapTo("b12c96nfn4_app_city").define();
                text(proto().postalCode())
                    .partitionBy(new CanadianPostalCodePartitioner())
                    .mapTo("b12c96nfn4_app_postal_code_1{3}","@@b12c96nfn4_app_postal_code_2.0{3}").define();
            }
        });
        
        date(proto().terminationDate()).mapTo(datePartition("@@b12c96nfn4_termination_date_g")).define();
        money(proto().totalRentOwed()).mapTo(fieldsPartition("@@b12c96nfn4_total_rent_owed_g", 2, 3, 2)).define();
        
        mapping(proto().owedRent(), new LtbFormFieldsMapping<LtbOwedRent>(LtbOwedRent.class) { @SuppressWarnings("unchecked")
        @Override protected void configure() {
            table(proto().rentOwingBreakdown()).rowMapping(Arrays.<LtbFormFieldsMapping<RentOwingForPeriod>>asList(
                    new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                        date(proto().from()).mapTo(datePartition("@@b12c96nfn4_a1_start")).define();
                        date(proto().to()).mapTo(datePartition("@@b12c96nfn4_a1_end")).define();
                        money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfn4_a1_charged", 1, 3, 2)).define();
                        money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfn4_a1_paid", 1, 3, 2)).define();
                        money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfn4_a1_owing", 1, 3, 2)).define();                            
                    }},
                    new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                        date(proto().from()).mapTo(datePartition("@@b12c96nfn4_a2_start")).define();
                        date(proto().to()).mapTo(datePartition("@@b12c96nfn4_a2_end")).define();
                        money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfn4_a2_charged", 1, 3, 2)).define();
                        money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfn4_a2_paid", 1, 3, 2)).define();
                        money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfn4_a2_owing", 1, 3, 2)).define();                            
                    }},
                    new LtbFormFieldsMapping<RentOwingForPeriod>(RentOwingForPeriod.class) { @Override protected void configure() {
                        date(proto().from()).mapTo(datePartition("@@b12c96nfn4_a3_start")).define();
                        date(proto().to()).mapTo(datePartition("@@b12c96nfn4_a3_end")).define();
                        money(proto().rentCharged()).mapTo(fieldsPartition("@@b12c96nfn4_a3_charged", 1, 3, 2)).define();
                        money(proto().rentPaid()).mapTo(fieldsPartition("@@b12c96nfn4_a3_paid", 1, 3, 2)).define();
                        money(proto().rentOwing()).mapTo(fieldsPartition("@@b12c96nfn4_a3_owing", 1, 3, 2)).define();                            
                    }}
            )).define();
            
            money(proto().totalRentOwing()).mapTo(fieldsPartition("@@b12c96nfn4_total_rent_owed", 2, 3, 2)).define();
            
        }});
        
        mapping(proto().signature(), new LtbFormFieldsMapping<N4Signature>(N4Signature.class) { @Override protected void configure() {
            field(proto().signedBy()).states("PL", "RA").mapTo("b12c96nfn4_signed_by").define();
            field(proto().signature()).mapTo("b12c96nmn4_signature").define();
            date(proto().signatureDate()).partitionBy(null).mapTo("Text3").define();
        }});
        
        mapping(proto().landlordsContactInfo(), new LtbFormFieldsMapping<LtbAgentContactInfo>(LtbAgentContactInfo.class) { @Override protected void configure() {
            text(proto().firstName()).mapTo("b12c96nfn4_personnel_first_name").define();
            text(proto().lastName()).mapTo("b12c96nfn4_personnel_last_name").define();
            text(proto().companyName()).mapTo("b12c96nfn4_org_name").define();
            text(proto().mailingAddress()).mapTo("b12c96nfn4_org_address").define();
            text(proto().unit()).mapTo("b12c96nfn4_org_unit_no").define();
            text(proto().municipality()).mapTo("b12c96nfn4_org_city").define();
            text(proto().province()).mapTo("b12c96nfn4_org_prov").define();
            text(proto().postalCode()).mapTo("b12c96nfn4_org_postal").define();
            phone(proto().phoneNumber()).mapTo(phonePartition("@@b12c96nfn4_personnel_phone")).define();
            phone(proto().faxNumber()).mapTo(phonePartition("@@b12c96nfn4_personnel_fax_number")).define();
            text(proto().email()).mapTo("b12c96nfn4_personnel_email").define();
        }});        
        
    }//@formatter:on
}
