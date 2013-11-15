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

        return fieldsData;
    }
}
