/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Oct 5, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.join.psj.PolymorphicSelfJoinChildA;
import com.pyx4j.entity.test.shared.domain.join.psj.PolymorphicSelfJoinChildB;
import com.pyx4j.entity.test.shared.domain.join.psj.PolymorphicSelfJoinChildBase;
import com.pyx4j.entity.test.shared.domain.join.psj.PolymorphicSelfJoinOwner;

public abstract class QueryPolymorphicJoinTestCase extends DatastoreTestBase {

    public void testSelfJoin() {
        String testId = uniqueString();
        // -- setup
        PolymorphicSelfJoinOwner o = EntityFactory.create(PolymorphicSelfJoinOwner.class);
        o.testId().setValue(testId);
        o.name().setValue("O1");
        srv.persist(o);

        {
            PolymorphicSelfJoinChildA item = EntityFactory.create(PolymorphicSelfJoinChildA.class);
            item.testId().setValue(testId);
            item.name().setValue("A1");
            item.bRefOwner().set(o);
            srv.persist(item);
        }

        {
            PolymorphicSelfJoinChildA item = EntityFactory.create(PolymorphicSelfJoinChildA.class);
            item.testId().setValue(testId);
            item.name().setValue("A2");
            item.bRefOwner().set(o);
            srv.persist(item);
        }

        {
            PolymorphicSelfJoinChildB item = EntityFactory.create(PolymorphicSelfJoinChildB.class);
            item.testId().setValue(testId);
            item.name().setValue("B1");
            item.bRefOwner().set(o);
            srv.persist(item);
        }

        // --- test
        {
            EntityQueryCriteria<PolymorphicSelfJoinChildBase> criteria = EntityQueryCriteria.create(PolymorphicSelfJoinChildBase.class);
            criteria.eq(criteria.proto().bRefOwner().testId(), testId);
            criteria.eq(criteria.proto().bRefOwner().as(), criteria.proto().id());

            List<PolymorphicSelfJoinChildBase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 2, r.size());
        }

        // the same as above different syntaxes
        {
            EntityQueryCriteria<PolymorphicSelfJoinChildBase> criteria = EntityQueryCriteria.create(PolymorphicSelfJoinChildBase.class);
            criteria.eq(criteria.proto().bRefOwner().testId(), testId);
            criteria.eq(criteria.proto().bRefOwner().as().$().id(), criteria.proto().id());

            List<PolymorphicSelfJoinChildBase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 2, r.size());
        }

        {
            EntityQueryCriteria<PolymorphicSelfJoinChildBase> criteria = EntityQueryCriteria.create(PolymorphicSelfJoinChildBase.class);
            criteria.eq(criteria.proto().bRefOwner().testId(), testId);
            criteria.eq(criteria.proto().bRefOwner().bs(), criteria.proto().id());

            List<PolymorphicSelfJoinChildBase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 1, r.size());
        }

        {
            EntityQueryCriteria<PolymorphicSelfJoinChildBase> criteria = EntityQueryCriteria.create(PolymorphicSelfJoinChildBase.class);
            criteria.eq(criteria.proto().bRefOwner().testId(), testId);
            OrCriterion or = criteria.or();
            or.eq(criteria.proto().bRefOwner().as(), criteria.proto().id());
            or.eq(criteria.proto().bRefOwner().bs(), criteria.proto().id());

            List<PolymorphicSelfJoinChildBase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());
        }

        {
            EntityQueryCriteria<PolymorphicSelfJoinChildBase> criteria = EntityQueryCriteria.create(PolymorphicSelfJoinChildBase.class);
            criteria.eq(criteria.proto().bRefOwner().testId(), testId);
            OrCriterion or = criteria.or();
            or.eq(criteria.proto().bRefOwner().as().$().id(), criteria.proto().id());
            or.eq(criteria.proto().bRefOwner().bs().$().id(), criteria.proto().id());

            List<PolymorphicSelfJoinChildBase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());
        }
    }

}
