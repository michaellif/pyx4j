/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Apr 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.tests.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.tests.domain.EmployeeQuery;
import com.pyx4j.entity.server.tests.rpc.PersistableQueryTestListService;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

@Ignore
public class PersistableQueryServiceTest {

    @Before
    public void setUp() {
        // Need DB
    }

    @After
    public void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    @Test
    public void testPersistableQueryUsage() {
        PersistableQueryTestListService service = TestServiceFactory.create(PersistableQueryTestListService.class);

        EmployeeQuery criteria = EntityFactory.create(EmployeeQuery.class);
        criteria.firstName().stringValue().setValue("Bob");

        service.list(new AsyncCallbackAssertion<EntitySearchResult<Employee>>() {

            @Override
            public void onSuccess(EntitySearchResult<Employee> result) {
                // TODO Auto-generated method stub
            }
        }, criteria);
    }
}
