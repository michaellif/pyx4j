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
 * Created on Aug 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Organization;

public abstract class DeleteTestCase extends DatastoreTestBase {

    public void testOwnedSetCascadeDelete() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org" + uniqueString());

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept" + uniqueString();
        department.name().setValue(deptName);

        org.departments().add(department);

        srv.persist(org);
        Department department1 = srv.retrieve(Department.class, department.getPrimaryKey());
        assertNotNull("found by pk", department1);

        // test starts here
        srv.delete(org);

        // Department is removed as well.
        Department department2 = srv.retrieve(Department.class, department.getPrimaryKey());
        assertNull("found by pk", department2);
    }
}
