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
 * Created on Mar 10, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.cached.CE1;
import com.pyx4j.entity.test.shared.domain.cached.CE2;

public abstract class CachedTestCase extends DatastoreTestBase {

    @Override
    protected void setUp() throws Exception {
        ServerSideConfiguration.setInstance(new ServerSideConfiguration() {
            @Override
            public IPersistenceConfiguration getPersistenceConfiguration() {
                return new IPersistenceConfiguration() {
                };
            }
        });
        CacheService.reset();
        ServerSideConfiguration.setInstance(null);
        super.setUp();
    }

    public void testPartialRetrival() {
        String testId = uniqueString();

        List<CE1> ents = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CE1 ent1 = EntityFactory.create(CE1.class);
            ent1.testId().setValue(testId);
            ent1.name().setValue("P" + i);
            for (int c = 0; c < 2; c++) {
                CE2 ent2 = EntityFactory.create(CE2.class);
                ent2.testId().setValue(testId);
                ent2.name().setValue("C" + c);
                ent1.children().add(ent2);
            }
            srv.persist(ent1);
            ents.add(ent1);
        }
        CacheService.reset();

        {
            EntityQueryCriteria<CE1> criteria = EntityQueryCriteria.create(CE1.class);
            srv.query(criteria, AttachLevel.ToStringMembers);
        }

        // See if we can access it
        {
            CE1 ent1 = Persistence.service().retrieve(CE1.class, ents.get(0).getPrimaryKey());
            Persistence.ensureRetrieve(ent1.children(), AttachLevel.Attached);
            Assert.assertEquals(testId, ent1.children().get(1).testId().getValue());
        }
    }
}
