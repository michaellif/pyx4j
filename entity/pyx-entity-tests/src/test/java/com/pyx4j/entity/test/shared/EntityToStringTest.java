/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.format.FormattedInheritanceChild1;
import com.pyx4j.entity.test.shared.domain.format.FormattedInheritanceChild2;
import com.pyx4j.entity.test.shared.domain.format.StringFields;
import com.pyx4j.entity.test.shared.domain.format.StringFieldsFormated;
import com.pyx4j.entity.test.shared.domain.format.StringIntFields;
import com.pyx4j.entity.test.shared.domain.format.StringIntFieldsChoiceFormated;
import com.pyx4j.entity.test.shared.domain.format.StringIntFieldsFormated;
import com.pyx4j.entity.test.shared.domain.sort.SortBy;

public class EntityToStringTest extends InitializerTestBase {

    public void testToStringOrderByDeclaration() {
        SortBy entity = EntityFactory.create(SortBy.class);
        List<String> toStringMember = entity.getEntityMeta().getToStringMemberNames();
        assertEquals("member 1 Order", 0, toStringMember.indexOf(entity.name().getFieldName()));
        assertEquals("member 2 Order", 1, toStringMember.indexOf(entity.amount().getFieldName()));
    }

    public void testStringView() {
        City city = EntityFactory.create(City.class);
        String cityName = "Toronto";
        city.name().setValue(cityName);
        assertEquals("City StringView", cityName, city.getStringView());

        Task task = EntityFactory.create(Task.class);
        task.id().setValue(new Key(10));
        task.description().setValue("Something");
        task.status().setValue(Status.DEACTIVATED);
        assertEquals("Task StringView", "10 Deactivated Something", task.getStringView());

        // ---

        Address address = EntityFactory.create(Address.class);
        String streetName = "1 Bloor St.";
        address.streetName().setValue(streetName);

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada";
        country.name().setValue(countryName);
        address.country().set(country);
        address.city().set(city);

        assertEquals("Address StringView", "1 Bloor St. " + cityName + " " + countryName, address.getStringView());
    }

    public void testCollectionStringView() {
        Employee emp = EntityFactory.create(Employee.class);

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue("Something1");

        emp.tasks().add(task1);
        assertTrue("ISet StringView", emp.tasks().getStringView().contains("Something1"));

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue("nothing2");

        emp.tasksSorted().add(task2);
        assertTrue("IList StringView", emp.tasksSorted().getStringView().contains("nothing2"));
    }

    public void testToStringDefaultFormatTwoStrings() {
        StringFields ent = EntityFactory.create(StringFields.class);
        ent.strField1().setValue("One");
        ent.strField2().setValue(null);
        assertEquals("StringView", "One ", ent.getStringView());

        ent.strField2().setValue("Two");
        assertEquals("StringView", "One Two", ent.getStringView());
    }

    public void testToStringFormatTwoStrings() {
        StringFieldsFormated ent = EntityFactory.create(StringFieldsFormated.class);
        ent.strField1().setValue("One");
        ent.strField2().setValue(null);
        assertEquals("StringView", "One ", ent.getStringView());

        ent.strField2().setValue("Two");
        assertEquals("StringView", "One Two", ent.getStringView());
    }

    public void testToStringDefaultFormatStringInt() {
        StringIntFields ent = EntityFactory.create(StringIntFields.class);
        ent.strField().setValue("One");
        ent.intField().setValue(null);
        assertEquals("Primitive.StringView", "", ent.intField().getStringView());
        assertEquals("StringView", "One ", ent.getStringView());

        ent.intField().setValue(2);
        assertEquals("StringView", "One 2", ent.getStringView());
    }

    public void testToStringFormatStringInt() {
        StringIntFieldsFormated ent = EntityFactory.create(StringIntFieldsFormated.class);
        ent.strField().setValue("One");
        ent.intField().setValue(null);
        assertEquals("Primitive.StringView", "", ent.intField().getStringView());
        assertEquals("StringView", "One ", ent.getStringView());

        ent.intField().setValue(2);
        assertEquals("StringView", "One 2", ent.getStringView());
    }

    public void testToStringFormatStringIntChoice() {
        StringIntFieldsChoiceFormated ent = EntityFactory.create(StringIntFieldsChoiceFormated.class);
        ent.strField().setValue("One");

        ent.intField().setValue(2);
        assertEquals("StringView", "One val2", ent.getStringView());

        ent.intField().setValue(null);
        assertEquals("Primitive.StringView", "", ent.intField().getStringView());
        assertEquals("StringView", "One Nan", ent.getStringView());

    }

    public void testToStringFormatInheritance() {
        FormattedInheritanceChild1 ent = EntityFactory.create(FormattedInheritanceChild1.class);
        ent.strField1().setValue("One");
        ent.strField2().setValue("Two");
        assertEquals("StringView", "One - Two", ent.getStringView());

    }

    public void testToStringFormatInheritanceLEvel2() {
        FormattedInheritanceChild2 ent = EntityFactory.create(FormattedInheritanceChild2.class);
        ent.strField1().setValue("One");
        ent.strField2().setValue("Two");
        assertEquals("StringView", "One - Two", ent.getStringView());
    }
}
