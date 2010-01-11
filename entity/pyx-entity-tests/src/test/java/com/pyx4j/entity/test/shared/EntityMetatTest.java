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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityMetatTest extends InitializerTestCase {

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);

        assertEquals("Entity Caption", "Laborer", EntityFactory.getEntityMeta(Employee.class).getCaption());

        assertEquals("Memeber Caption defined", "Home address", emp.homeAddress().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Hiredate", emp.hiredate().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Work Address", emp.workAddress().getMeta().getCaption());
    }
}
