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
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
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
        emp.hiredate().setValue(today);
        emp.reliable().setValue(Boolean.TRUE);
        emp.holidays().setValue(7L);
        emp.rating().setValue(5);
        emp.salary().setValue(77.8);
        emp.accessStatus().setValue(Status.SUSPENDED);
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        final AsyncCallback<Serializable> callback = new AsyncCallback<Serializable>() {

            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            public void onSuccess(Serializable result) {
                Assert.assertTrue("Employee class expected", (result instanceof Employee));
                Employee emp2 = (Employee) result;

                Assert.assertEquals("String Class of Value", String.class, emp2.firstName().getValue().getClass());
                Assert.assertEquals("String Value", "Bob", emp2.firstName().getValue());

                Assert.assertEquals("Date Class of Value", Date.class, emp2.hiredate().getValue().getClass());
                Assert.assertEquals("Date Value", today, emp2.hiredate().getValue());

                Assert.assertEquals("Boolean Class of Value", Boolean.class, emp2.reliable().getValue().getClass());
                Assert.assertEquals("Boolean Value", Boolean.TRUE, emp2.reliable().getValue());

                Assert.assertEquals("Long Class of Value", Long.class, emp2.holidays().getValue().getClass());
                Assert.assertEquals("Long Value", Long.valueOf(7), emp2.holidays().getValue());

                Assert.assertEquals("Integer Class of Value", Integer.class, emp2.rating().getValue().getClass());
                Assert.assertEquals("Integer Value", Integer.valueOf(5), emp2.rating().getValue());

                Assert.assertEquals("Double Class of Value", Double.class, emp2.salary().getValue().getClass());
                Assert.assertEquals("Double Value", 77.8, emp2.salary().getValue());

                // This filed is RpcTransient
                Assert.assertEquals("Enum Ext Class of Value", Status.class, emp2.accessStatus().getValue().getClass());
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

            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

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
}
