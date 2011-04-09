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
 * Created on 2011-04-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.security.shared.SecurityViolationException;

public abstract class EntityGraphsSecurityTestCase extends DatastoreTestBase {

    public void testOwnedOneToOneMerge() {
        Employee employee0 = EntityFactory.create(Employee.class);
        employee0.firstName().setValue("Firstname A. " + uniqueString());
        employee0.homeAddress().streetName().setValue("Home Street 000 " + uniqueString());

        srv.merge(employee0);

        //Test insert
        {
            Employee employee2 = EntityFactory.create(Employee.class);
            employee2.firstName().setValue("Firstname B. " + uniqueString());
            employee2.homeAddress().streetName().setValue("Home Street 200 " + uniqueString());
            employee2.homeAddress().setPrimaryKey(employee0.homeAddress().getPrimaryKey());

            boolean saved = false;
            try {
                srv.merge(employee2);
                saved = true;
            } catch (SecurityViolationException e) {
                // OK
            }
            if (saved) {
                fail("Should not save OwnedMember with Id");
            }
        }

        // Test update
        {
            Employee employee3 = EntityFactory.create(Employee.class);
            employee3.firstName().setValue("Firstname C. " + uniqueString());
            srv.merge(employee3);

            employee3.homeAddress().streetName().setValue("Home Street 300 " + uniqueString());
            employee3.homeAddress().setPrimaryKey(employee0.homeAddress().getPrimaryKey());
            boolean saved = false;
            try {
                srv.merge(employee3);
                saved = true;
            } catch (SecurityViolationException e) {
                // OK
            }
            if (saved) {
                fail("Should not save OwnedMember with Id");
            }
        }

        // Test change
        {
            Employee employee3 = EntityFactory.create(Employee.class);
            employee3.firstName().setValue("Firstname C. " + uniqueString());
            employee3.homeAddress().streetName().setValue("Home Street 400 " + uniqueString());
            srv.merge(employee3);

            employee3.homeAddress().streetName().setValue("Home Street 500 " + uniqueString());
            employee3.homeAddress().setPrimaryKey(employee0.homeAddress().getPrimaryKey());
            boolean saved = false;
            try {
                srv.merge(employee3);
                saved = true;
            } catch (SecurityViolationException e) {
                // OK
            }
            if (saved) {
                fail("Should not save OwnedMember with Id");
            }
        }

        Employee employee1mod = srv.retrieve(Employee.class, employee0.getPrimaryKey());
        Assert.assertEquals("streetName is changed", employee0.homeAddress().streetName().getValue(), employee1mod.homeAddress().streetName().getValue());
    }
}
