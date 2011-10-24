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
 * Created on Oct 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.sql.Date;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Task;

public class EntityDtoBinderTest extends InitializerTestCase {

    private class SimpleEntityDtoBinder extends EntityDtoBinder<Employee, Employee> {

        protected SimpleEntityDtoBinder() {
            super(Employee.class, Employee.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.firstName(), dboProto.firstName());
            bind(dtoProto.employmentStatus(), dboProto.employmentStatus());
            bind(dtoProto.tasksSorted(), dboProto.tasksSorted());
            bind(dtoProto.workAddress(), dboProto.workAddress());
        }

    }

    public void testUpdate() {
        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.firstName().setValue("a name");
        emp1.employmentStatus().setValue(Employee.EmploymentStatus.CONTRACT);

        emp1.workAddress().streetName().setValue("street");
        emp1.workAddress().city().name().setValue("city");
        emp1.workAddress().effectiveFrom().setValue(new Date(System.currentTimeMillis()));

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue("Something1");
        task1.notes().add("note1");
        emp1.tasksSorted().add(task1);

        Employee emp2 = new SimpleEntityDtoBinder().createDBO(emp1);
        Assert.assertEquals(emp1.firstName().getValue(), emp2.firstName().getValue());
        Assert.assertEquals(emp1.employmentStatus().getValue(), emp2.employmentStatus().getValue());

        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
        Assert.assertEquals("address.city Value", emp1.workAddress().city().name().getValue(), emp2.workAddress().city().name().getValue());
        Assert.assertEquals("address.effectiveFrom Value", emp1.workAddress().effectiveFrom().getValue(), emp2.workAddress().effectiveFrom().getValue());

        Assert.assertEquals("List size", 1, emp2.tasksSorted().size());

        Assert.assertEquals(task1.description(), emp2.tasksSorted().get(0).description());
        Assert.assertEquals(1, emp2.tasksSorted().get(0).notes().size());
        Assert.assertEquals("note1", emp2.tasksSorted().get(0).notes().iterator().next());

        emp2 = emp2.cloneEntity();

        Assert.assertFalse(new SimpleEntityDtoBinder().updateDBO(emp1, emp2));

        emp1.workAddress().streetName().setValue("street m");

        Assert.assertTrue(new SimpleEntityDtoBinder().updateDBO(emp1, emp2));
        //Second pass will not change the value
        Assert.assertFalse(new SimpleEntityDtoBinder().updateDBO(emp1, emp2));
        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
    }

    public void testUpdateRefference() {
        Employee emp1 = EntityFactory.create(Employee.class);

        emp1.workAddress().setPrimaryKey(new Key(11));
        emp1.workAddress().streetName().setValue("street");
        emp1.workAddress().country().name().setValue("country1");
        emp1.workAddress().country().setPrimaryKey(new Key(22));
        emp1.workAddress().city().name().setValue("city");

        Employee emp2 = new SimpleEntityDtoBinder().createDBO(emp1);
        emp2 = emp2.cloneEntity();

        Assert.assertFalse(new SimpleEntityDtoBinder().updateDBO(emp1, emp2));
        Assert.assertEquals("address.country Value", emp1.workAddress().country().name().getValue(), emp2.workAddress().country().name().getValue());
        Assert.assertEquals("address.country Value", emp1.workAddress().country().id().getValue(), emp2.workAddress().country().id().getValue());
        Assert.assertEquals("owned PK Value", emp1.workAddress().id().getValue(), emp2.workAddress().id().getValue());

        emp1.workAddress().country().name().setValue("country2");
        emp1.workAddress().country().setPrimaryKey(null);
        emp1.workAddress().streetName().setValue("street m");

        Assert.assertTrue(new SimpleEntityDtoBinder().updateDBO(emp1, emp2));
        Assert.assertEquals("address.country Value", emp1.workAddress().country().name().getValue(), emp2.workAddress().country().name().getValue());
        Assert.assertTrue("address.country Value", emp2.workAddress().country().id().isNull());

        Assert.assertEquals("owned PK Value", emp1.workAddress().id().getValue(), emp2.workAddress().id().getValue());
        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
    }
}
