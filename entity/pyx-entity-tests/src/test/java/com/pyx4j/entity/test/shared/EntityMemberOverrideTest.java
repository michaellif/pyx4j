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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.test.shared.domain.inherit.override.O1Base;
import com.pyx4j.entity.test.shared.domain.inherit.override.O1Concrete1;
import com.pyx4j.entity.test.shared.domain.inherit.override.O2Concrete1;

public class EntityMemberOverrideTest extends InitializerTestBase {

    public void testOveloadedMemeberAccess() {

        {
            O1Concrete1 o1c1 = EntityFactory.create(O1Concrete1.class);
            // Just test that we can access it.
            o1c1.o2Base().set(EntityFactory.create(O2Concrete1.class));
        }

        {
            O1Base o1c1 = EntityFactory.create(O1Concrete1.class);
            // Just test that we can access it.
            o1c1.o2Base().set(EntityFactory.create(O2Concrete1.class));
        }

    }

    public void testOveloadedMemeberMeta() {
        {
            EntityMeta meta = EntityFactory.getEntityMeta(O1Base.class);
            assertEquals("members", 3, meta.getMemberNames().size());
        }

        {
            EntityMeta meta = EntityFactory.getEntityMeta(O1Concrete1.class);
            assertEquals("no new members added", 3, meta.getMemberNames().size());
        }
    }

}
