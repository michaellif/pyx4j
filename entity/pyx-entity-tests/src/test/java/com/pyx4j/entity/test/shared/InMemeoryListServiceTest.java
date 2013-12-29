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
 * Created on Jan 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityCriteriaFilter;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.entity.test.shared.domain.Employee;

public class InMemeoryListServiceTest extends InitializerTestBase {

    private void assertCriteria(String message, Employee input, Criterion criterion, boolean accepted) {
        EntityListCriteria<Employee> criteria = EntityListCriteria.create(Employee.class);
        criteria.add(criterion);

        EntityCriteriaFilter<Employee> filter = new EntityCriteriaFilter<Employee>(criteria);
        assertEquals(message, accepted, filter.accept(input));
    }

    public void testCriteria() {

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");
        assertCriteria("Wild card match", emp, PropertyCriterion.like(emp.firstName(), "B*"), true);
        assertCriteria("Wild card do not math", emp, PropertyCriterion.like(emp.firstName(), "o*"), false);
        assertCriteria("No Wild card math", emp, PropertyCriterion.like(emp.firstName(), "o"), true);
        assertCriteria("No Wild card math", emp, PropertyCriterion.like(emp.firstName(), "b"), true);

    }

    public void testCriteriaAndSort() {
        final List<Employee> emps = new Vector<Employee>();
        for (int i = 0; i < 10; i++) {
            Employee emp = EntityFactory.create(Employee.class);
            emp.firstName().setValue(String.valueOf(i));
            emp.rating().setValue(i);
            emps.add(emp);
        }

        AbstractListService<Employee> srv = new InMemeoryListService<Employee>(emps);

        EntityListCriteria<Employee> criteria = EntityListCriteria.create(Employee.class);
        criteria.setPageNumber(1);
        criteria.setPageSize(3);
        criteria.desc(criteria.proto().firstName());
        criteria.add(PropertyCriterion.ne(criteria.proto().rating(), 0));

        srv.list(new AsyncCallback<EntitySearchResult<Employee>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

            @Override
            public void onSuccess(EntitySearchResult<Employee> result) {
                assertEquals("Total size", emps.size() - 1, result.getTotalRows());
                assertEquals("Set size", 3, result.getData().size());
                assertEquals("first value", "6", result.getData().get(0).firstName().getValue());
            }
        }, criteria);
    }
}
