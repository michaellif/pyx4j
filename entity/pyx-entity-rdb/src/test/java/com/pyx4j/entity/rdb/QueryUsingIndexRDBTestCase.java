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
 * Created on Oct 25, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.idx.IndexedIgnoreCase;

public abstract class QueryUsingIndexRDBTestCase extends DatastoreTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testIngleIndexSingleValue() {
        //TODO make this work
        //Assume.assumeTrue(((EntityPersistenceServiceRDB) srv).getDialect().isFunctionIndexesSupported());

        String testId = uniqueString();
        {
            IndexedIgnoreCase item = EntityFactory.create(IndexedIgnoreCase.class);
            item.testId().setValue(testId);
            item.value().setValue("a");
            srv.persist(item);
        }
        {
            IndexedIgnoreCase item = EntityFactory.create(IndexedIgnoreCase.class);
            item.testId().setValue(testId);
            item.value().setValue("B");
            srv.persist(item);
        }
        {
            IndexedIgnoreCase item = EntityFactory.create(IndexedIgnoreCase.class);
            item.testId().setValue(testId);
            item.value().setValue("b");
            srv.persist(item);
        }

        {
            EntityQueryCriteria<IndexedIgnoreCase> criteria = EntityQueryCriteria.create(IndexedIgnoreCase.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.eq(criteria.proto().value(), "A");

            List<IndexedIgnoreCase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 1, r.size());
        }

        {
            EntityQueryCriteria<IndexedIgnoreCase> criteria = EntityQueryCriteria.create(IndexedIgnoreCase.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.eq(criteria.proto().value(), "b");

            List<IndexedIgnoreCase> r = srv.query(criteria);
            Assert.assertEquals("result set size", 2, r.size());
        }
    }

}
