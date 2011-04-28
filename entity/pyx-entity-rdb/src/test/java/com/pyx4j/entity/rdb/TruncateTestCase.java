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
 * Created on 2011-04-27
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.test.server.DatastoreTestBase;

public abstract class TruncateTestCase extends DatastoreTestBase {

    @Table(prefix = "test")
    public interface SimpleTrunk extends IEntity {

        @ToString
        IPrimitive<String> name();
    }

    public void testTruncateTable() {
        SimpleTrunk item1 = EntityFactory.create(SimpleTrunk.class);
        String name = uniqueString();
        item1.name().setValue(name);

        srv.persist(item1);

        EntityQueryCriteria<SimpleTrunk> criteria = EntityQueryCriteria.create(SimpleTrunk.class);
        List<SimpleTrunk> list1 = srv.query(criteria);
        Assert.assertTrue("result set size", list1.size() >= 1);

        srv.truncate(SimpleTrunk.class);

        List<SimpleTrunk> list2 = srv.query(criteria);
        Assert.assertEquals("result set size", 0, list2.size());
    }

    @Table(prefix = "test")
    public interface ComplexTrunk extends IEntity {

        @ToString
        IPrimitive<String> name();

        IPrimitiveSet<String> notes();

        @Owned
        ISet<SimpleTrunk> items();
    }

    public void testTruncateComplexTable() {
        ComplexTrunk item1 = EntityFactory.create(ComplexTrunk.class);
        String name = uniqueString();
        item1.name().setValue(name);
        for (int i = 0; i < 5; i++) {
            SimpleTrunk subItem1 = EntityFactory.create(SimpleTrunk.class);
            subItem1.name().setValue(uniqueString());
            item1.items().add(subItem1);
            item1.notes().add(subItem1.name().getValue());
        }

        srv.persist(item1);

        EntityQueryCriteria<ComplexTrunk> criteria = EntityQueryCriteria.create(ComplexTrunk.class);
        List<ComplexTrunk> list1 = srv.query(criteria);
        Assert.assertTrue("result set size", list1.size() >= 1);

        srv.truncate(ComplexTrunk.class);

        List<ComplexTrunk> list2 = srv.query(criteria);
        Assert.assertEquals("result set size", 0, list2.size());

        EntityQueryCriteria<SimpleTrunk> criteriaChildern = EntityQueryCriteria.create(SimpleTrunk.class);
        List<SimpleTrunk> listChildern = srv.query(criteriaChildern);
        Assert.assertTrue("Childern result set size", listChildern.size() >= 1);
    }
}
