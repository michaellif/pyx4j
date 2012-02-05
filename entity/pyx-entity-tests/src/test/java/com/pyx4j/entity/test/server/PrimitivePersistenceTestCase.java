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
 * Created on Jan 23, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.temporal.Schedule;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.DateUtils;

public abstract class PrimitivePersistenceTestCase extends DatastoreTestBase {

    public void testString() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.firstName().getValue());
        Assert.assertEquals("Class of Value", String.class, emp.firstName().getValueClass());
        emp.firstName().setValue("Bob");

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
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

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
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

        srv.persist(s);
        Schedule s2 = srv.retrieve(Schedule.class, s.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + s.getPrimaryKey(), s2);
        Assert.assertEquals("Class of Value", java.sql.Date.class, s2.startsOn().getValue().getClass());
        Assert.assertEquals("Value", day, s2.startsOn().getValue());

        // Test specific to ETD dates.
        s.startsOn().setValue(createSqlDate(1999, 04, 04));
        srv.persist(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).startsOn().getValue());

        s.startsOn().setValue(createSqlDate(1962, 03, 12));
        srv.persist(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).startsOn().getValue());

        s.startsOn().setValue(createSqlDate(1962, 03, 29));
        srv.persist(s);
        Assert.assertEquals("Value", s.startsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).startsOn().getValue());
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

        srv.persist(s);
        Schedule s2 = srv.retrieve(Schedule.class, s.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + s.getPrimaryKey(), s2);
        Assert.assertEquals("Class of Value", LogicalDate.class, s2.endsOn().getValue().getClass());
        Assert.assertEquals("Value", day, s2.endsOn().getValue());

        // Test specific to ETD dates.
        s.endsOn().setValue(createLogicalDate(1999, 04, 04));
        srv.persist(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).endsOn().getValue());

        s.endsOn().setValue(createLogicalDate(1962, 03, 12));
        srv.persist(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).endsOn().getValue());

        s.endsOn().setValue(createLogicalDate(1962, 03, 29));
        srv.persist(s);
        Assert.assertEquals("Value", s.endsOn().getValue(), srv.retrieve(Schedule.class, s.getPrimaryKey()).endsOn().getValue());
    }

    //TODO Make it work on GAE
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

        srv.persist(s);
        Schedule s2 = srv.retrieve(Schedule.class, s.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + s.getPrimaryKey(), s2);
        Assert.assertEquals("Class of Value", java.sql.Time.class, s2.time().getValue().getClass());
        Assert.assertEquals("Value", time, s2.time().getValue());
    }

    public void testBoolean() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.reliable().getValue());
        Assert.assertEquals("Class of Value", Boolean.class, emp.reliable().getValueClass());
        emp.reliable().setValue(Boolean.TRUE);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Boolean.class, emp2.reliable().getValue().getClass());
        Assert.assertEquals("Value", Boolean.TRUE, emp2.reliable().getValue());
    }

    public void testLong() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.holidays().getValue());
        Assert.assertEquals("Class of Value", Long.class, emp.holidays().getValueClass());
        emp.holidays().setValue(7L);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Long.class, emp2.holidays().getValue().getClass());
        Assert.assertEquals("Value", Long.valueOf(7), emp2.holidays().getValue());
    }

    public void testInteger() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.rating().getValue());
        Assert.assertEquals("Class of Value", Integer.class, emp.rating().getValueClass());
        emp.rating().setValue(5);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Integer.class, emp2.rating().getValue().getClass());
        Assert.assertEquals("Value", Integer.valueOf(5), emp2.rating().getValue());
    }

    public void testShort() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagShort().getValue());
        Assert.assertEquals("Class of Value", Short.class, emp.flagShort().getValueClass());
        emp.flagShort().setValue((short) 5);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Short.class, emp2.flagShort().getValue().getClass());
        Assert.assertEquals("Value", Short.valueOf((short) 5), emp2.flagShort().getValue());
    }

    public void testByte() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagByte().getValue());
        Assert.assertEquals("Class of Value", Byte.class, emp.flagByte().getValueClass());
        emp.flagByte().setValue((byte) 51);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Byte.class, emp2.flagByte().getValue().getClass());
        Assert.assertEquals("Value", Byte.valueOf((byte) 51), emp2.flagByte().getValue());
    }

    public void testDouble() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.flagDouble().getValue());
        Assert.assertEquals("Class of Value", Double.class, emp.flagDouble().getValueClass());
        emp.flagDouble().setValue(77.8);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Double.class, emp2.flagDouble().getValue().getClass());
        Assert.assertEquals("Value", 77.8, emp2.flagDouble().getValue());
    }

    public void testBigDecimal() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.salary().getValue());
        Assert.assertEquals("Class of Value", BigDecimal.class, emp.salary().getValueClass());
        emp.salary().setValue(new BigDecimal("23.51"));

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", BigDecimal.class, emp2.salary().getValue().getClass());
        assertValueEquals("Value", new BigDecimal("23.51"), emp2.salary().getValue());
    }

    public void testEnumExternal() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        Assert.assertNull("Initial value", emp.accessStatus().getValue());
        Assert.assertEquals("Class of Value", Status.class, emp.accessStatus().getValueClass());
        emp.accessStatus().setValue(Status.SUSPENDED);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertEquals("Class of Value", Status.class, emp2.accessStatus().getValue().getClass());
        Assert.assertEquals("Value", Status.SUSPENDED, emp2.accessStatus().getValue());
    }

    public void testEnumEmbeded() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        Assert.assertNull("Initial value", emp.employmentStatus().getValue());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp.employmentStatus().getValueClass());
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        Assert.assertNotNull("Value retrived", emp2.employmentStatus().getValue());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp2.employmentStatus().getValue().getClass());
        Assert.assertEquals("Value", EmploymentStatus.FULL_TIME, emp2.employmentStatus().getValue());
    }

    public void testBlob() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        byte[] value = new byte[] { 1, 2, 3 };
        emp.image().setValue(value);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + emp.getPrimaryKey(), emp2);
        assertEquals("Class of Value", byte[].class, emp2.image().getValueClass());
        assertEquals("Class of Value", byte[].class, emp2.image().getValue().getClass());
        for (int i = 0; i < value.length; i++) {
            assertEquals("Value " + i, value[i], emp2.image().getValue()[i]);
        }

        emp.image().setValue(null);
        srv.persist(emp);
        emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        assertNull("Erase Blob value Value", emp2.image().getValue());
    }

    public void testGeoPoint() {
        Address address = EntityFactory.create(Address.class);
        GeoPoint value = new GeoPoint(23, 45);
        address.location().setValue(value);
        srv.persist(address);
        Address address2 = srv.retrieve(Address.class, address.getPrimaryKey());

        Assert.assertEquals("Class of Value", GeoPoint.class, address2.location().getValue().getClass());
        Assert.assertEquals("Value", value, address2.location().getValue());
    }

    // TODO Support Pair persistence
    // IPrimitive<Pair<Double, Double>> range();
}
