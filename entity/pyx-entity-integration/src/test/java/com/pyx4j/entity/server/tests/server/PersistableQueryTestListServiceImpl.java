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
package com.pyx4j.entity.server.tests.server;

import com.pyx4j.entity.server.AbstractListService2Impl;
import com.pyx4j.entity.server.query.AbstractQueryBinder;
import com.pyx4j.entity.server.tests.domain.EmployeeQuery;
import com.pyx4j.entity.server.tests.rpc.PersistableQueryTestListService;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class PersistableQueryTestListServiceImpl extends AbstractListService2Impl<Employee, Employee> implements PersistableQueryTestListService {

    // TODO need builder pattern for this.
    public static class QueryBinder extends AbstractQueryBinder<Employee, EmployeeQuery> {

        public QueryBinder() {
            super(Employee.class, EmployeeQuery.class);
        }

        protected void bind() {
            map(boProto.firstName(), criteriaProto.firstName());
        }
    }

}
