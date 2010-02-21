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
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Organization;

public class BidirectionalRelationshipTest extends InitializerTestCase {

    public void testOwnerValue() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org");

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept1";
        department.name().setValue(deptName);

        assertTrue("Not owned Yet", department.organization().isNull());

        org.departments().add(department);

        Department orgDepartment = org.departments().iterator().next();
        assertNull("Direct value access", orgDepartment.getMemberValue("organization"));
        assertFalse("Owned now", orgDepartment.organization().isNull());
        assertTrue("Owned properly", orgDepartment.organization() == org);
        assertNull("Direct value access", orgDepartment.getMemberValue("organization"));
    }

}
