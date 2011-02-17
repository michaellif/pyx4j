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

import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Status;

public abstract class DDLTestCase extends DatastoreTestBase {

    @Table(prefix = "test", name = "ddl")
    public interface TaskAlt1 extends IEntity {

        IPrimitive<Boolean> finished();

        @ToString
        IPrimitive<String> description();

        @ToString(index = 1)
        IPrimitive<Status> status();

        IPrimitive<Date> deadLine();

        IPrimitiveSet<String> notes();
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
    }

    public void testCreateTable() {
        TaskAlt1 task1 = EntityFactory.create(TaskAlt1.class);
        srv.persist(task1);

        TaskAlt2 task2 = EntityFactory.create(TaskAlt2.class);
        srv.persist(task2);
    }

}
