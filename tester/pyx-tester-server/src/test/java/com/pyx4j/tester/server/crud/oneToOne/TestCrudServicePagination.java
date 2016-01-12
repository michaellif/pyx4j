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
 * Created on Jan 12, 2016
 * @author vlads
 */
package com.pyx4j.tester.server.crud.oneToOne;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadOwner;
import com.pyx4j.tester.server.crud.DBTestsSetup;
import com.pyx4j.tester.shared.crud.oneToOne.OneToOneCrudService;
import com.pyx4j.tester.shared.crud.oneToOne.OneToOneCrudWithInMemoryFilterService;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

import junit.framework.TestCase;

public class TestCrudServicePagination extends TestCase {

    private String testId;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DBTestsSetup.defaultInit();
        //        TestLifecycle.beginRequest();

        testId = UUID.randomUUID().toString();

        for (int i = 0; i < 1000; i++) {
            OneToOneReadOwner entity = EntityFactory.create(OneToOneReadOwner.class);
            entity.testId().setValue(testId);
            entity.name().setValue(String.valueOf(i));
            Persistence.service().persist(entity);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    public void testSimplePages() {
        OneToOneCrudService service = TestServiceFactory.create(OneToOneCrudService.class);

        final Map<Integer, String> pageCursorReference = new HashMap<>();
        // page 1
        {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(0);
            criteria.setPageSize(10);

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "0", result.getData().get(0).name().getValue());
                    pageCursorReference.put(0, result.getEncodedCursorReference());
                }
            }, criteria);
        }

        // page 2
        {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(1);
            criteria.setPageSize(10);
            criteria.setEncodedCursorReference(pageCursorReference.get(0));

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "10", result.getData().get(0).name().getValue());
                    Assert.assertEquals("First Result", "11", result.getData().get(1).name().getValue());
                    pageCursorReference.put(1, result.getEncodedCursorReference());
                }
            }, criteria);
        }

        // page 2 no CursorReference  -> Still correct result
        // TODO enable after initial UI testing.
        if (false) {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(1);
            criteria.setPageSize(10);

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "10", result.getData().get(0).name().getValue());
                    Assert.assertEquals("First Result", "11", result.getData().get(1).name().getValue());
                    pageCursorReference.put(1, result.getEncodedCursorReference());
                }
            }, criteria);
        }

    }

    // TODO enable after initial UI testing.
    public void OFF_testInMemoryFilterPages() {
        OneToOneCrudWithInMemoryFilterService service = TestServiceFactory.create(OneToOneCrudWithInMemoryFilterService.class);

        final Map<Integer, String> pageCursorReference = new HashMap<>();
        // page 1
        {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(0);
            criteria.setPageSize(10);

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "0", result.getData().get(0).name().getValue());
                    Assert.assertEquals("First Result", "2", result.getData().get(1).name().getValue());
                    pageCursorReference.put(0, result.getEncodedCursorReference());
                }
            }, criteria);
        }

        // page 2
        {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(1);
            criteria.setPageSize(10);
            criteria.setEncodedCursorReference(pageCursorReference.get(0));

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "20", result.getData().get(0).name().getValue());
                    Assert.assertEquals("First Result", "22", result.getData().get(1).name().getValue());
                    pageCursorReference.put(1, result.getEncodedCursorReference());
                }
            }, criteria);
        }

        // page 2 no CursorReference -> Wrong result
        {
            EntityListCriteria<OneToOneReadOwner> criteria = EntityListCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.setPageNumber(1);
            criteria.setPageSize(10);

            service.list(new AsyncCallbackAssertion<EntitySearchResult<OneToOneReadOwner>>() {

                @Override
                public void onSuccess(EntitySearchResult<OneToOneReadOwner> result) {
                    Assert.assertEquals("Returned size", 10, result.getData().size());
                    Assert.assertEquals("Total Rows", 1000, result.getTotalRows());

                    Assert.assertEquals("First Result", "0", result.getData().get(0).name().getValue());
                    Assert.assertEquals("First Result", "2", result.getData().get(1).name().getValue());
                    pageCursorReference.put(1, result.getEncodedCursorReference());
                }
            }, criteria);
        }

    }

}
