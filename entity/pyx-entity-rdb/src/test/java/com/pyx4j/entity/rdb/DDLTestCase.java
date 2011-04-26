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
 * Created on 2010-07-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.Date;

import junit.framework.Assert;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.server.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public abstract class DDLTestCase extends DatastoreTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            if (((EntityPersistenceServiceRDB) srv).isTableExists(TaskAlt1.class)) {
                ((EntityPersistenceServiceRDB) srv).dropTable(TaskAlt1.class);
            }
        } catch (RuntimeExceptionSerializable ignore) {
        }
    }

    @Table(prefix = "test", name = "ddl")
    public interface TaskAlt1 extends IEntity {

        IPrimitive<Boolean> finished();

        @ToString
        IPrimitive<String> description();

        @ToString(index = 1)
        IPrimitive<Status> status();

        IPrimitive<Date> deadLine();

        IPrimitiveSet<String> notes();

        @Owned
        ISet<Task> tasks();
    }

    @Table(prefix = "test", name = "ddl")
    public interface TaskAlt2 extends IEntity {

        IPrimitive<Boolean> finished();

        @ToString
        IPrimitive<String> description();

        @ToString(index = 1)
        IPrimitive<Status> status();

        IPrimitive<Date> deadLine();

        IPrimitiveSet<String> notes();

        IPrimitive<String> notes2();

        @Owned
        IList<Task> tasks();
    }

    public void testCreateAndAlterTable() {
        TaskAlt1 task1 = EntityFactory.create(TaskAlt1.class);
        srv.persist(task1);

        TaskAlt2 task2 = EntityFactory.create(TaskAlt2.class);
        task2.notes2().setValue(uniqueString());
        srv.persist(task2);

        TaskAlt2 task22 = srv.retrieve(TaskAlt2.class, task2.getPrimaryKey());
        Assert.assertEquals("Value", task2.notes2().getValue(), task22.notes2().getValue());
    }

    public void testAlterCollectionsTable() {
        TaskAlt1 task1 = EntityFactory.create(TaskAlt1.class);

        Task subTask11 = EntityFactory.create(Task.class);
        subTask11.deadLine().setValue(TimeUtils.getRoundedNow());
        subTask11.status().setValue(Status.DEACTIVATED);
        task1.tasks().add(subTask11);

        srv.persist(task1);

        TaskAlt2 task2 = EntityFactory.create(TaskAlt2.class);
        srv.persist(task2);

        // retrieve original Set as List
        TaskAlt2 task12 = srv.retrieve(TaskAlt2.class, task1.getPrimaryKey());
    }
}
