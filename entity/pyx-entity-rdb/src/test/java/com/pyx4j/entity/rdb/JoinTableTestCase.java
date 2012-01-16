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
 * Created on Jan 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.join.BRefCascadeChild;
import com.pyx4j.entity.test.shared.domain.join.BRefCascadeOwner;
import com.pyx4j.entity.test.shared.domain.join.BRefReadChild;
import com.pyx4j.entity.test.shared.domain.join.BRefReadOwner;

public abstract class JoinTableTestCase extends DatastoreTestBase {

    public void testBackreferencesRead() {
        // Setup data
        String testId = uniqueString();

        BRefReadOwner owner1 = EntityFactory.create(BRefReadOwner.class);
        owner1.name().setValue(uniqueString());
        owner1.testId().setValue(testId);
        srv.persist(owner1);

        BRefReadChild c1 = EntityFactory.create(BRefReadChild.class);
        c1.name().setValue(uniqueString());
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        c1.bRefOwner().set(owner1);
        srv.persist(c1);

        BRefReadChild c2 = EntityFactory.create(BRefReadChild.class);
        c2.name().setValue(uniqueString());
        c2.testId().setValue(testId);
        c2.sortColumn().setValue(1);
        c2.bRefOwner().set(owner1);
        srv.persist(c2);

        {
            BRefReadOwner owner1r = srv.retrieve(BRefReadOwner.class, owner1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 2, owner1r.children().size());
            Assert.assertEquals("Inserted value2 present", c2, owner1r.children().get(0));
            Assert.assertEquals("Inserted value1 present", c1, owner1r.children().get(1));

        }

    }

    public void TODO_testBackreferencesCascade() {
        // Setup data
        String testId = uniqueString();

        BRefCascadeChild c1 = EntityFactory.create(BRefCascadeChild.class);
        c1.name().setValue(uniqueString());
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        srv.persist(c1);

        BRefCascadeChild c2 = EntityFactory.create(BRefCascadeChild.class);
        c2.name().setValue(uniqueString());
        c2.testId().setValue(testId);
        c2.sortColumn().setValue(1);
        srv.persist(c2);

        BRefCascadeOwner owner1 = EntityFactory.create(BRefCascadeOwner.class);
        owner1.name().setValue(uniqueString());
        owner1.testId().setValue(testId);
        owner1.children().add(c2);
        owner1.children().add(c1);
        srv.persist(owner1);

        {

        }
    }
}
