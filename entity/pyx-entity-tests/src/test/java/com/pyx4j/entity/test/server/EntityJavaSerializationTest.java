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
 * Created on Feb 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.InitializerTestCase;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.gwt.server.IOUtils;

public class EntityJavaSerializationTest extends InitializerTestCase {

    public void ZtestObjectOperations() {
        Country country = EntityFactory.create(Country.class);
        assertNotNull("EntityFactory create", country);

        Class<?> cl = country.getClass();
        while ((cl != null) && (cl != Object.class)) {
            assertTrue(cl.getName() + " Serializable", Serializable.class.isAssignableFrom(cl));
            cl = cl.getSuperclass();
        }
    }

    public void testSimpleObjectSerialization() throws Exception {
        City city = EntityFactory.create(City.class);
        String cityName = "Toronto";
        city.name().setValue(cityName);
        System.out.println("hc0: " + System.identityHashCode(city));

        //Create new Instance using using reflection
        City city1 = city.getClass().newInstance();
        assertEquals("City.class", City.class, city1.getObjectClass());

        City city2 = (City) unzip(zip(city));

        assertEquals("City.class", City.class, city2.getObjectClass());
        assertEquals("City.name", "name", city2.name().getFieldName());
        assertEquals("City.name", cityName, city2.name().getValue());
        assertNotNull("EntityMeta", city2.getEntityMeta());
        assertNotNull("MemberMeta", city2.getEntityMeta().getMemberMeta(city.name().getFieldName()));
    }

    public void ZtestComplexObjectSerialization() throws IOException {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("First Name");

        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue("Home Street");
        employee.homeAddress().set(address);

        address = employee.homeAddress();

        Employee employee2 = (Employee) unzip(zip(employee));

        assertEquals("Firstname", "First Name", employee2.firstName().getValue());
        assertEquals("homeAddress.streetName", "Home Street", employee2.homeAddress().streetName().getValue());
    }

    private static byte[] zip(Serializable o) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(buf);
            out.writeObject(o);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(buf);
        }
        return buf.toByteArray();
    }

    private static Object unzip(byte[] buf) throws IOException {
        ByteArrayInputStream b = new ByteArrayInputStream(buf);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(b);
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }
}
