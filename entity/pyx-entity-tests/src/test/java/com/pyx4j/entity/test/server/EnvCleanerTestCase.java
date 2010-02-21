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
 * Created on Feb 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.Province;
import com.pyx4j.entity.test.shared.domain.Task;

/**
 * Remove all the tests data at the end of all tests.
 */
public abstract class EnvCleanerTestCase extends DatastoreTestBase {

    public <T extends IEntity> void deleteAll(Class<T> entityClass) {
        EntityCriteria<T> criteria = new EntityCriteria<T>(entityClass);
        srv.delete(criteria);
    }

    public void testCleanAll() {
        deleteAll(Employee.class);
        deleteAll(Task.class);
        deleteAll(Country.class);
        deleteAll(Organization.class);
        deleteAll(Province.class);
        deleteAll(City.class);
        deleteAll(Address.class);
        deleteAll(Department.class);
    }
}
