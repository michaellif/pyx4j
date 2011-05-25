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
 * Created on Jan 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import java.io.Serializable;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.rpc.ComplexPrimitive;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.test.client.TestServices;
import com.pyx4j.unit.client.GUnitTester;

public class EntitySerializationGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    public void testPrimitiveSerialization() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        final Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");
        final Date today = new Date();
        emp.from().setValue(today);
        emp.reliable().setValue(Boolean.TRUE);
        emp.holidays().setValue(7L);
        emp.rating().setValue(5);
        emp.salary().setValue(77.8);
        emp.accessStatus().setValue(Status.SUSPENDED);
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Serializable result) {
                Assert.assertTrue("Employee class expected", (result instanceof Employee));
                Employee emp2 = (Employee) result;

                Assert.assertEquals("String Class of Value", String.class, emp2.firstName().getValue().getClass());
                Assert.assertEquals("String Value", "Bob", emp2.firstName().getValue());

                Assert.assertEquals("Date Class of Value", Date.class, emp2.from().getValue().getClass());
                Assert.assertEquals("Date Value", today, emp2.from().getValue());

                Assert.assertEquals("Boolean Class of Value", Boolean.class, emp2.reliable().getValue().getClass());
                Assert.assertEquals("Boolean Value", Boolean.TRUE, emp2.reliable().getValue());

                Assert.assertEquals("Long Class of Value", Long.class, emp2.holidays().getValue().getClass());
                Assert.assertEquals("Long Value", Long.valueOf(7), emp2.holidays().getValue());

                Assert.assertEquals("Integer Class of Value", Integer.class, emp2.rating().getValue().getClass());
                Assert.assertEquals("Integer Value", Integer.valueOf(5), emp2.rating().getValue());

                Assert.assertEquals("Double Class of Value", Double.class, emp2.salary().getValue().getClass());
                Assert.assertEquals("Double Value", 77.8, emp2.salary().getValue());

                // This field is RpcTransient
                Assert.assertEquals("Enum Ext Class of Value", Status.class, emp2.accessStatus().getValueClass());
                Assert.assertNull("Enum Ext Value", emp2.accessStatus().getValue());

                Assert.assertEquals("Enum Emb Class of Value", EmploymentStatus.class, emp2.employmentStatus().getValue().getClass());
                Assert.assertEquals("Enum Emb Value", EmploymentStatus.FULL_TIME, emp2.employmentStatus().getValue());

                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }
        };

        RPCManager.execute(TestServices.EchoSerializable.class, emp, callback);
    }

    public void testComplexPrimitiveSerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);

        ComplexPrimitive cp = EntityFactory.create(ComplexPrimitive.class);
        final Pair<String, String> pair = new Pair<String, String>("left", "right");
        cp.stringPair().setValue(pair);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Serializable result) {
                Assert.assertTrue("ComplexPrimitive class expected", (result instanceof ComplexPrimitive));
                ComplexPrimitive cp2 = (ComplexPrimitive) result;
                assertEquals("Class of Value", Pair.class, cp2.stringPair().getValueClass());
                assertEquals("Value", pair, cp2.stringPair().getValue());
                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }
        };

        RPCManager.execute(TestServices.EchoSerializable.class, cp, callback);

    }

    public void testISetSerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);
        final Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org1");
        org.setPrimaryKey(String.valueOf(11));
        final Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("dept1");
        department1.setPrimaryKey(String.valueOf(1));
        final Department department2 = EntityFactory.create(Department.class);
        department2.name().setValue("dept2");
        department2.setPrimaryKey(String.valueOf(2));
        org.departments().add(department2);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Serializable result) {
                Assert.assertTrue("Organization class expected", (result instanceof Organization));
                Organization org2 = (Organization) result;
                assertTrue("Not Same data\n" + org2.toString() + "\n" + org.toString(), EntityGraph.fullyEqual(org, org2));
                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }

        };

        RPCManager.execute(TestServices.EchoSerializable.class, org, callback);
    }

    public void testIPrimitiveSetSerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);
        final Task orig = EntityFactory.create(Task.class);
        orig.setPrimaryKey(String.valueOf(22));
        orig.description().setValue("Task1");
        orig.notes().add("Note 1");
        orig.notes().add("Note 2");
        orig.oldStatus().add(Status.SUSPENDED);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Serializable result) {
                Assert.assertTrue("Task class expected", (result instanceof Task));
                Task returned = (Task) result;
                assertTrue("Not Same data\n" + returned.toString() + "\n!=\n" + orig.toString(), EntityGraph.fullyEqual(orig, returned));
                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }

        };

        RPCManager.execute(TestServices.EchoSerializable.class, orig, callback);
    }

    public void testRpcTransient() {
        GUnitTester.delayTestFinish(this, TIME_OUT);

        final Department dept = EntityFactory.create(Department.class);
        dept.name().setValue("R&D");
        final Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("John Doe" + System.currentTimeMillis());
        emp.accessStatus().setValue(Status.DEACTIVATED);
        dept.employees().add(emp);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Serializable result) {
                Assert.assertTrue("Department class expected", (result instanceof Department));
                Department dept2 = (Department) result;

                Assert.assertEquals("dept.employees.size", 1, dept2.employees().size());
                Employee emp2 = dept2.employees().iterator().next();

                Assert.assertEquals("String Class of Value", String.class, emp2.firstName().getValue().getClass());
                Assert.assertEquals("String Value", emp.firstName().getValue(), emp2.firstName().getValue());

                // This field is RpcTransient
                Assert.assertEquals("Enum Ext Class of Value", Status.class, emp2.accessStatus().getValueClass());
                Assert.assertNull("Enum Ext Value", emp2.accessStatus().getValue());

                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }
        };

        RPCManager.execute(TestServices.EchoSerializable.class, dept, callback);
    }

    private void validateServerError(final String message, Serializable data) {
        GUnitTester.delayTestFinish(this, TIME_OUT);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            @Override
            public void onFailure(Throwable caught) {
                GUnitTester.finishTest(EntitySerializationGWTTest.this);
            }

            @Override
            public void onSuccess(Serializable result) {
                fail(message + " error expected");
            }

        };

        RPCManager.execute(TestServices.EchoSerializable.class, data, callback);
    }

    public void testRpcStringTypeValidation() {
        Employee emp = EntityFactory.create(Employee.class);
        // Initialise value map
        emp.firstName().setValue("Name");
        // Hack our way in
        emp.getValue().put(emp.firstName().getFieldName(), new Long(20));
        validateServerError("String -> Long", emp);
    }

    public void testRpcLongTypeValidation() {
        Employee emp = EntityFactory.create(Employee.class);
        // Initialise value map
        emp.firstName().setValue("Name");
        // Hack our way in
        emp.getValue().put(emp.holidays().getFieldName(), new Double(20.0));
        validateServerError("Long -> Double", emp);
    }

    public void testRpcEnumTypeValidation() {
        Employee emp = EntityFactory.create(Employee.class);
        // Initialise value map
        emp.firstName().setValue("Name");
        // Hack our way in
        emp.getValue().put(emp.employmentStatus().getFieldName(), new Double(20.0));
        validateServerError("Enum -> Double", emp);
    }

    public void testRpcEntityTypeValidation() {
        Employee emp = EntityFactory.create(Employee.class);
        // Initialise value map
        emp.firstName().setValue("Name");
        // Hack our way in
        emp.getValue().put(emp.manager().getFieldName(), new Long(20));
        validateServerError("IEntity -> Long", emp);
    }

    public void testRpcUnknownTypeValidation() {
        Employee emp = EntityFactory.create(Employee.class);
        // Initialise value map
        emp.firstName().setValue("Name");
        // Hack our way in
        emp.getValue().put("unknown", new Long(20));
        validateServerError("unknown", emp);
    }
}
