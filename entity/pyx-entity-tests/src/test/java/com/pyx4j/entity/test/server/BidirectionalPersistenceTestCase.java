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

import com.pyx4j.commons.IFullDebug;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Organization;

public abstract class BidirectionalPersistenceTestCase extends DatastoreTestBase {

    public void testOwnerValue() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org" + uniqueString());

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept1" + uniqueString();
        department.name().setValue(deptName);

        org.departments().add(department);
        srv.persist(org);

        Department department1 = srv.retrieve(Department.class, department.getPrimaryKey());
        assertNotNull("found by pk", department1);
        assertFalse("Owned now", department1.organization().isNull());

        //retrieve department by Organization
        EntityCriteria<Department> criteria = EntityCriteria.create(Department.class);
        criteria.add(PropertyCriterion.eq(criteria.meta().organization(), org));
        Department department2 = srv.retrieve(criteria);
        assertNotNull("found by owner", department2);
        // see if data really in DB
        assertNotNull("Direct value access", department2.getMemberValue("organization"));
        assertFalse("Owned now", department2.organization().isNull());
        assertEquals("Owned properly", org.getPrimaryKey(), department2.organization().getPrimaryKey());

        // Test Recursive print, StackOverflowError
        assertNotNull("toString No StackOverflowError", department2.toString());
        assertNotNull("IFullDebug No StackOverflowError", ((IFullDebug) department2).debugString());
    }

}
