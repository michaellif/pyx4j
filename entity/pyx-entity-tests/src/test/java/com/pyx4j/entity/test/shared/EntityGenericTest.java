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
 * Created on Feb 16, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.ItemA.ItemAVersion;

public class EntityGenericTest extends InitializerTestBase {

    public void testVersionedEntityManipulations() {
        EntityMeta itemAMeta = EntityFactory.getEntityMeta(ItemA.class);

        //version is generic member
        MemberMeta versionMember = itemAMeta.getMemberMeta("version");
        assertEquals("Meta valueClass", ItemAVersion.class, versionMember.getValueClass());

        ItemA itemA1 = EntityFactory.create(ItemA.class);
        // Test Meta
        assertEquals("Meta valueClass", ItemAVersion.class, itemA1.version().getValueClass());

        itemA1.version().testId().setValue("1");
        itemA1.version().name().setValue("2");

    }
}
