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
package com.propertyvista.biz.legal.forms.n4cs;

import com.propertyvista.biz.legal.forms.ltbcommon.LtbFormFieldsMapping;
import com.propertyvista.domain.legal.n4cs.N4CSDocumentType;
import com.propertyvista.domain.legal.n4cs.N4CSFormFieldsData;
import com.propertyvista.domain.legal.n4cs.N4CSServiceMethod;
import com.propertyvista.domain.legal.n4cs.N4CSSignature;
import com.propertyvista.domain.legal.n4cs.N4CSToPersonInfo;

public class N4CSFieldsMapping extends LtbFormFieldsMapping<N4CSFormFieldsData> {

    public N4CSFieldsMapping() {
        super(N4CSFormFieldsData.class);
    }

    @Override
    protected void configure() {

        date(proto().issueDate()).mapTo(datePartition("@@b12c96nfdate_served")).define();
        field(proto().street()).mapTo("b12c96nmstreet_addr").define();
        field(proto().unit()).mapTo("b12c96nmunit_no").define();
        field(proto().municipality()).mapTo("b12c96nmmunicipality").define();
        field(proto().postalCode()).mapTo("b12c96nmpostal_code").define();
        field(proto().reporter()).mapTo("b12c96nmname").define();

        mapping(proto().document(), new LtbFormFieldsMapping<N4CSDocumentType>(N4CSDocumentType.class) {
            @Override
            protected void configure() {
                text(proto().application()).mapTo("appliction_form").define();
                text(proto().termination()).mapTo("notice_of_termination_form").define();
                field(proto().docType()).mapTo("b12c96nfdocuments_served").define();
            }
        });

        mapping(proto().passedTo(), new LtbFormFieldsMapping<N4CSToPersonInfo>(N4CSToPersonInfo.class) {
            @Override
            protected void configure() {
                field(proto().tpType()).states("Tenant", "Landlord", "Other").mapTo("b12c96nfChoice_4").define();
                text(proto().name()).mapTo("name_of_person").define();
            }
        });

        mapping(proto().service(), new LtbFormFieldsMapping<N4CSServiceMethod>(N4CSServiceMethod.class) {
            @Override
            protected void configure() {
                field(proto().method()).states("H", "A", "P", "L", "D", "C", "F", "M", "O").mapTo("b12c96nfmethod_of_service").define();
                phone(proto().fax()).mapTo(phonePartition("fax_number")).define();
                field(proto().lastAddr()).mapTo("b12c96nmlast_known_address").define();
                field(proto().differentMethod()).mapTo("b12c96nmdifferent_method_of_service").define();
            }
        });

        mapping(proto().signature(), new LtbFormFieldsMapping<N4CSSignature>(N4CSSignature.class) {
            @Override
            protected void configure() {
                field(proto().signedBy()).states("PL", "PT", "RA", "PO").mapTo("b12c96nfsigned_by").define();
                field(proto().firstname()).mapTo("b12c96nfap_first_name").define();
                field(proto().lastname()).mapTo("b12c96nfap_last_name").define();
                phone(proto().phone()).mapTo(phonePartition("@@b12c96nfap_phone_number")).define();
                field(proto().signature()).mapTo("b12c96nmsignature").define();
                date(proto().signatureDate()).partitionBy(null).mapTo("b12c96nmsignature_date").define();
            }
        });
    }
}
