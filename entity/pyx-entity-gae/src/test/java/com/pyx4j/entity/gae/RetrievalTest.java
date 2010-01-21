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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public class RetrievalTest extends LocalDatastoreTest {

    private IEntityPersistenceService srv;

    @Before
    public void setupPersistenceService() {
        srv = PersistenceServicesFactory.getPersistenceService();
    }

    @Test
    public void testOwnedSet() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");

        Task task = EntityFactory.create(Task.class);
        Date today = new Date();
        task.deadLine().setValue(today);
        task.status().setValue(Status.DEACTIVATED);

        emp.tasks().add(task);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Value", "Bob", emp2.firstName().getValue());

        Assert.assertEquals("Retr. Set size", 1, emp2.tasks().size());
        Assert.assertTrue("Retr. contains", emp2.tasks().contains(task));

        Task task2 = emp2.tasks().iterator().next();

        Assert.assertEquals("deadLine", today, task2.deadLine().getValue());
        Assert.assertEquals("Status", Status.DEACTIVATED, task2.status().getValue());
    }
}
