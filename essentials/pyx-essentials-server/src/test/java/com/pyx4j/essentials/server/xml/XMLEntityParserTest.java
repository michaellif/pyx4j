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
 * Created on May 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.test.shared.InitializerTestBase;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.EmployeePhoto;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.temporal.Schedule;
import com.pyx4j.entity.xml.XMLEntityConverter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.DateUtils;

public class XMLEntityParserTest extends TestCase {

    @SuppressWarnings("unchecked")
    protected <T extends IEntity> T xmlSerialize(T entity) {
        XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
        XMLEntityConverter.write(xml, entity);
        //System.out.println(xml.toString());
        try {
            return (T) XMLEntityConverter.parse(xml.toString());
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void testString() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.firstName().getValue());
        Assert.assertEquals("Class of Value", String.class, emp.firstName().getValueClass());
        emp.firstName().setValue("Bob");

        Employee emp2 = xmlSerialize(emp);
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", String.class, emp2.firstName().getValue().getClass());
        Assert.assertEquals("Value", "Bob", emp2.firstName().getValue());
    }

    public void testDate() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.from().getValue());
        Assert.assertEquals("Class of Value", Date.class, emp.from().getValueClass());
        // Round to seconds
        GregorianCalendar c = new GregorianCalendar();
        Date today = DateUtils.getRoundedNow();
        c.setTime(today);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
        Date day = c.getTime();
        emp.from().setValue(day);

        Employee emp2 = xmlSerialize(emp);
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Date.class, emp2.from().getValue().getClass());
        Assert.assertEquals("Value", day, emp2.from().getValue());
    }

    @SuppressWarnings("deprecation")
    public static java.sql.Date createSqlDate(int year, int month, int day) {
        //return new java.sql.Date(DateUtils.createDate(year - 1900, month - 1, day).getTime());
        return new java.sql.Date(new Date(year - 1900, month - 1, day).getTime());
    }

    public void testSqlDate() {
        Schedule s = EntityFactory.create(Schedule.class);
        Assert.assertNull("Initial value", s.startsOn().getValue());
        Assert.assertEquals("Class of Value", java.sql.Date.class, s.startsOn().getValueClass());
        // Round to seconds
        GregorianCalendar c = new GregorianCalendar();
        Date today = DateUtils.getRoundedNow();
        c.setTime(today);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
        java.sql.Date day = new java.sql.Date(TimeUtils.dayStart(c.getTime()).getTime());
        s.startsOn().setValue(day);

        Schedule s2 = xmlSerialize(s);

        Assert.assertNotNull("retrieve by PK " + s.getPrimaryKey(), s2);
        Assert.assertEquals("Class of Value", java.sql.Date.class, s2.startsOn().getValue().getClass());
        Assert.assertEquals("Value", day, s2.startsOn().getValue());

        // Test specific to ETD dates.
        s.startsOn().setValue(createSqlDate(1999, 04, 04));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), s2.startsOn().getValue());

        s.startsOn().setValue(createSqlDate(1962, 03, 12));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), s2.startsOn().getValue());

        s.startsOn().setValue(createSqlDate(1962, 03, 29));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), s2.startsOn().getValue());
    }

    public static LogicalDate createLogicalDate(int year, int month, int day) {
        return new LogicalDate(year - 1900, month - 1, day);
    }

    public void testLogicalDate() {
        Schedule s = EntityFactory.create(Schedule.class);
        Assert.assertNull("Initial value", s.endsOn().getValue());
        Assert.assertEquals("Class of Value", LogicalDate.class, s.endsOn().getValueClass());
        // Round to seconds
        GregorianCalendar c = new GregorianCalendar();
        Date today = DateUtils.getRoundedNow();
        c.setTime(today);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
        LogicalDate day = new LogicalDate(TimeUtils.dayStart(c.getTime()).getTime());
        s.endsOn().setValue(day);

        Schedule s2 = xmlSerialize(s);

        Assert.assertNotNull("retrieve by PK " + s.getPrimaryKey(), s2);
        Assert.assertEquals("Class of Value", LogicalDate.class, s2.endsOn().getValue().getClass());
        Assert.assertEquals("Value", day, s2.endsOn().getValue());

        // Test specific to ETD dates.
        s.endsOn().setValue(createLogicalDate(1999, 04, 04));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), s2.endsOn().getValue());

        s.endsOn().setValue(createLogicalDate(1962, 03, 12));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), s2.endsOn().getValue());

        s.endsOn().setValue(createLogicalDate(1962, 03, 29));
        s2 = xmlSerialize(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), s2.endsOn().getValue());
    }

    public void testSqlTime() {
        Schedule s = EntityFactory.create(Schedule.class);
        Assert.assertNull("Initial value", s.time().getValue());
        Assert.assertEquals("Class of Value", java.sql.Time.class, s.time().getValueClass());
        // Round to seconds
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(DateUtils.getRoundedNow());
        c.set(Calendar.YEAR, 1970);
        c.set(Calendar.DAY_OF_YEAR, 1);

        java.sql.Time time = new java.sql.Time(c.getTime().getTime());

        s.time().setValue(time);

        Schedule s2 = xmlSerialize(s);
        Assert.assertEquals("Class of Value", java.sql.Time.class, s2.time().getValue().getClass());
        Assert.assertEquals("Value", time, s2.time().getValue());
    }

    public void testBoolean() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.reliable().getValue());
        Assert.assertEquals("Class of Value", Boolean.class, emp.reliable().getValueClass());
        emp.reliable().setValue(Boolean.TRUE);

        Employee emp2 = xmlSerialize(emp);
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Boolean.class, emp2.reliable().getValue().getClass());
        Assert.assertEquals("Value", Boolean.TRUE, emp2.reliable().getValue());
    }

    public void testLong() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.holidays().getValue());
        Assert.assertEquals("Class of Value", Long.class, emp.holidays().getValueClass());
        emp.holidays().setValue(7L);

        Employee emp2 = xmlSerialize(emp);
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Long.class, emp2.holidays().getValue().getClass());
        Assert.assertEquals("Value", Long.valueOf(7), emp2.holidays().getValue());
    }

    public void testInteger() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.rating().getValue());
        Assert.assertEquals("Class of Value", Integer.class, emp.rating().getValueClass());
        emp.rating().setValue(5);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Integer.class, emp2.rating().getValue().getClass());
        Assert.assertEquals("Value", Integer.valueOf(5), emp2.rating().getValue());
    }

    public void testShort() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagShort().getValue());
        Assert.assertEquals("Class of Value", Short.class, emp.flagShort().getValueClass());
        emp.flagShort().setValue((short) 5);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Short.class, emp2.flagShort().getValue().getClass());
        Assert.assertEquals("Value", Short.valueOf((short) 5), emp2.flagShort().getValue());
    }

    public void testByte() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagByte().getValue());
        Assert.assertEquals("Class of Value", Byte.class, emp.flagByte().getValueClass());
        emp.flagByte().setValue((byte) 51);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Byte.class, emp2.flagByte().getValue().getClass());
        Assert.assertEquals("Value", Byte.valueOf((byte) 51), emp2.flagByte().getValue());
    }

    public void testDouble() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagDouble().getValue());
        Assert.assertEquals("Class of Value", Double.class, emp.flagDouble().getValueClass());
        emp.flagDouble().setValue(77.8);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Double.class, emp2.flagDouble().getValue().getClass());
        Assert.assertEquals("Value", 77.8, emp2.flagDouble().getValue());
    }

    public void testBigDecimal() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.salary().getValue());
        Assert.assertEquals("Class of Value", BigDecimal.class, emp.salary().getValueClass());
        emp.salary().setValue(new BigDecimal("23.51"));

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", BigDecimal.class, emp2.salary().getValue().getClass());
        InitializerTestBase.assertValueEquals("Value", new BigDecimal("23.51"), emp2.salary().getValue());
    }

    public void testEnumExternal() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(String.valueOf(System.currentTimeMillis()));
        Assert.assertNull("Initial value", emp.accessStatus().getValue());
        Assert.assertEquals("Class of Value", Status.class, emp.accessStatus().getValueClass());
        emp.accessStatus().setValue(Status.SUSPENDED);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Status.class, emp2.accessStatus().getValue().getClass());
        Assert.assertEquals("Value", Status.SUSPENDED, emp2.accessStatus().getValue());
    }

    public void testEnumEmbeded() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(String.valueOf(System.currentTimeMillis()));
        Assert.assertNull("Initial value", emp.employmentStatus().getValue());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp.employmentStatus().getValueClass());
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        Employee emp2 = xmlSerialize(emp);

        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertNotNull("Value retrived", emp2.employmentStatus().getValue());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp2.employmentStatus().getValue().getClass());
        Assert.assertEquals("Value", EmploymentStatus.FULL_TIME, emp2.employmentStatus().getValue());
    }

    public void testBlob() {
        EmployeePhoto empPhoto = EntityFactory.create(EmployeePhoto.class);
        empPhoto.name().setValue("1");
        byte[] value = new byte[] { 1, 2, 3 };
        empPhoto.image().setValue(value);

        EmployeePhoto emp2 = xmlSerialize(empPhoto);
        Assert.assertNotNull("retrieve by PK " + empPhoto.getPrimaryKey(), emp2);
        assertEquals("Class of Value", byte[].class, emp2.image().getValueClass());
        assertEquals("Class of Value", byte[].class, emp2.image().getValue().getClass());
        for (int i = 0; i < value.length; i++) {
            assertEquals("Value " + i, value[i], emp2.image().getValue()[i]);
        }

        empPhoto.image().setValue(null);
        emp2 = xmlSerialize(empPhoto);
        assertNull("Erase Blob value Value", emp2.image().getValue());
    }

    public void testGeoPoint() {
        Address address = EntityFactory.create(Address.class);
        GeoPoint value = new GeoPoint(23, 45);
        address.location().setValue(value);

        Address address2 = xmlSerialize(address);

        Assert.assertEquals("Class of Value", GeoPoint.class, address2.location().getValue().getClass());
        Assert.assertEquals("Value", value, address2.location().getValue());
    }
}
