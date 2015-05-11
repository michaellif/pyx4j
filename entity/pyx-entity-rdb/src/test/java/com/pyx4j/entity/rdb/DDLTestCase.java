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

import org.junit.Assert;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.gwt.server.DateUtils;

public abstract class DDLTestCase extends DatastoreTestBase {

    protected void setUpAltAdd() {
        try {
            if (((EntityPersistenceServiceRDB) srv).isTableExists(TaskAlt1.class)) {
                ((EntityPersistenceServiceRDB) srv).dropTable(TaskAlt1.class);
            }
        } catch (RuntimeExceptionSerializable ignore) {
        }
    }

    @Table(prefix = "test", name = "ddl_add")
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

    @Table(prefix = "test", name = "ddl_add")
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
        setUpAltAdd();

        TaskAlt1 task1 = EntityFactory.create(TaskAlt1.class);
        srv.persist(task1);

        TaskAlt2 task2 = EntityFactory.create(TaskAlt2.class);
        task2.notes2().setValue(uniqueString());
        srv.persist(task2);

        TaskAlt2 task22 = srv.retrieve(TaskAlt2.class, task2.getPrimaryKey());
        Assert.assertEquals("Value", task2.notes2().getValue(), task22.notes2().getValue());
    }

    public void testAlterCollectionsTable() {
        setUpAltAdd();

        TaskAlt1 task1 = EntityFactory.create(TaskAlt1.class);

        Task subTask11 = EntityFactory.create(Task.class);
        subTask11.deadLine().setValue(DateUtils.getRoundedNow());
        subTask11.status().setValue(Status.DEACTIVATED);
        task1.tasks().add(subTask11);

        srv.persist(task1);

        TaskAlt2 task2 = EntityFactory.create(TaskAlt2.class);
        srv.persist(task2);

        // retrieve original Set as List
        TaskAlt2 task12 = srv.retrieve(TaskAlt2.class, task1.getPrimaryKey());
        Assert.assertNotNull("can get it", task12);
    }

    @Table(prefix = "test", name = "ddl_str1")
    public interface EntStrAlt1 extends IEntity {

        IPrimitive<String> testId();

        @ToString
        @Length(165)
        IPrimitive<String> name();
    }

    @Table(prefix = "test", name = "ddl_str1")
    public interface EntStrAlt2 extends IEntity {

        IPrimitive<String> testId();

        @ToString
        @Length(487)
        IPrimitive<String> name();
    }

    protected void setUpStrAlt() {
        try {
            if (((EntityPersistenceServiceRDB) srv).isTableExists(EntStrAlt1.class)) {
                ((EntityPersistenceServiceRDB) srv).dropTable(EntStrAlt1.class);
            }
        } catch (RuntimeExceptionSerializable ignore) {
        }
    }

    public void testAlterStringColumnType() {
        setUpStrAlt();

        String setId = uniqueString();

        EntStrAlt1 ent1 = EntityFactory.create(EntStrAlt1.class);
        ent1.testId().setValue(setId);
        ent1.name().setValue("A1" + uniqueString());
        srv.persist(ent1);

        // Try to store more
        {
            EntStrAlt1 ent1m = srv.retrieve(EntStrAlt1.class, ent1.getPrimaryKey());
            ent1m.name().setValue(CommonsStringUtils.paddingRight("M1", 200, 'z'));
            try {
                srv.persist(ent1m);
                Assert.fail("Should not be able to save large column");
            } catch (RuntimeException ok) {

            }
        }

        ((IEntityPersistenceServiceRDB) srv).resetMapping();
        ((IEntityPersistenceServiceRDB) srv).resetConnectionPool();

        // see if data preserved
        {
            EntStrAlt2 ent2 = srv.retrieve(EntStrAlt2.class, ent1.getPrimaryKey());
            Assert.assertEquals("data preserved", ent2.name().getValue(), ent1.name().getValue());
        }

        // Try to store more
        {
            EntStrAlt2 ent1m = srv.retrieve(EntStrAlt2.class, ent1.getPrimaryKey());
            ent1m.name().setValue(CommonsStringUtils.paddingRight("M2", 200, 'z'));
            srv.persist(ent1m);
        }
    }

    @Table(prefix = "test", name = "ddl_str2")
    public interface EntStrNNAlt1 extends IEntity {

        IPrimitive<String> testId();

        @ToString
        IPrimitive<String> name();
    }

    @Table(prefix = "test", name = "ddl_str2")
    public interface EntStrNNAlt2 extends IEntity {

        IPrimitive<String> testId();

        @ToString
        @MemberColumn(notNull = true)
        IPrimitive<String> name();
    }

    protected void setUpStrNNAlt() {
        try {
            if (((EntityPersistenceServiceRDB) srv).isTableExists(EntStrNNAlt1.class)) {
                ((EntityPersistenceServiceRDB) srv).dropTable(EntStrNNAlt1.class);
            }
        } catch (RuntimeExceptionSerializable ignore) {
        }
    }

    public void testAlterStringColumnAddNotNull() {
        setUpStrAlt();

        String setId = uniqueString();

        EntStrNNAlt1 ent1 = EntityFactory.create(EntStrNNAlt1.class);
        ent1.testId().setValue(setId);
        ent1.name().setValue("A1" + uniqueString());
        srv.persist(ent1);

        ((IEntityPersistenceServiceRDB) srv).resetMapping();
        ((IEntityPersistenceServiceRDB) srv).resetConnectionPool();

        // see if data preserved
        {
            EntStrNNAlt2 ent2 = srv.retrieve(EntStrNNAlt2.class, ent1.getPrimaryKey());
            Assert.assertEquals("data preserved", ent2.name().getValue(), ent1.name().getValue());
        }

        // Try to store null
        {
            EntStrNNAlt2 ent1m = srv.retrieve(EntStrNNAlt2.class, ent1.getPrimaryKey());
            ent1m.name().setValue(null);
            try {
                srv.persist(ent1m);
                Assert.fail("Should not be able to save null value");
            } catch (RuntimeException ok) {

            }
        }

        // changing it no nullable

        ((IEntityPersistenceServiceRDB) srv).resetMapping();
        ((IEntityPersistenceServiceRDB) srv).resetConnectionPool();

        {
            EntStrNNAlt1 ent1m = srv.retrieve(EntStrNNAlt1.class, ent1.getPrimaryKey());
            ent1m.name().setValue(null);
            srv.persist(ent1m);
        }
    }
}
