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
 * Created on Jan 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public class EntityEqualsTest extends InitializerTestCase {

    public void testNewEntity() {

        Task task1 = EntityFactory.create(Task.class);
        task1.status().setValue(Status.ACTIVE);

        Task task2 = EntityFactory.create(Task.class);
        task2.status().setValue(Status.ACTIVE);

        assertFalse("new Items are diferent", task1.equals(task2));
    }

    public void testSameValue() {

        Task task1 = EntityFactory.create(Task.class);
        task1.status().setValue(Status.ACTIVE);

        Task task2 = EntityFactory.create(Task.class);
        task2.set(task1);

        assertTrue("new Items have same value", task1.equals(task2));
    }
}
