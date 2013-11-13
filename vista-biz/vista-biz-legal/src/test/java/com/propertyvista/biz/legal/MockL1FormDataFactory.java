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

        return fieldsData;
    }
}
