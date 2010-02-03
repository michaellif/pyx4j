/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public class EntityMetaTest extends InitializerTestCase {

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);

        assertEquals("Entity Caption", "Laborer", EntityFactory.getEntityMeta(Employee.class).getCaption());

        assertEquals("Memeber Caption defined", "Home address", emp.homeAddress().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Hiredate", emp.hiredate().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Work Address", emp.workAddress().getMeta().getCaption());
    }

    public void testStringView() {
        City city = EntityFactory.create(City.class);
        String cityName = "Toronto";
        city.name().setValue(cityName);
        assertEquals("City StringView", cityName, city.getStringView());

        Task task = EntityFactory.create(Task.class);
        task.description().setValue("Something");
        task.status().setValue(Status.DEACTIVATED);
        assertEquals("Task StringView", "Something Deactivated", task.getStringView());

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
}
