/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityDiffTest extends InitializerTestBase {

    public void testDirectMember() {
        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.firstName().setValue("F1");
        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.firstName().setValue("F2");

        assertEquals("Simple member", "First Name: F1 -> F2", EntityDiff.getChanges(emp1, emp2));

    }

    public void testRefference() {
        Employee empM1 = EntityFactory.create(Employee.class);
        empM1.firstName().setValue("F1");
        Employee empM2 = EntityFactory.create(Employee.class);
        empM2.firstName().setValue("F2");

        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.manager().set(empM1);

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.manager().set(empM2);

        assertEquals("Entity member", "Manager: F1 -> F2", EntityDiff.getChanges(emp1, emp2));

    }

    public void testSetRefference() {
        Employee empM1 = EntityFactory.create(Employee.class);
        empM1.setPrimaryKey(new Key(1));
        empM1.firstName().setValue("F1");
        Employee empM2 = EntityFactory.create(Employee.class);
        empM1.setPrimaryKey(new Key(2));
        empM2.firstName().setValue("F2");

        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.employees().add(empM1);

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.employees().add(empM2);

        assertEquals("List", "Employees: removed F1\nEmployees: added F2", EntityDiff.getChanges(emp1, emp2));
    }

}
