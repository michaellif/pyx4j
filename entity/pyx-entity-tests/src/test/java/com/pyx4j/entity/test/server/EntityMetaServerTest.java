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
 * Created on 2010-04-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.test.shared.InitializerTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.inherit.single.SBase;
import com.pyx4j.entity.test.shared.domain.inherit.single.SConcrete1;
import com.pyx4j.entity.test.shared.domain.inherit.single.SConcrete1Ext;

public class EntityMetaServerTest extends InitializerTestBase {

    public void testPersistenceName() {
        EntityMeta empMeta = EntityFactory.getEntityMeta(Employee.class);
        assertEquals("PersistenceName", "testEmployee", empMeta.getPersistenceName());
    }

    public void testPersistableSuperClass() {
        {
            EntityMeta empMeta = EntityFactory.getEntityMeta(SBase.class);
            assertEquals("Self", null, empMeta.getPersistableSuperClass());
        }
        {
            EntityMeta empMeta = EntityFactory.getEntityMeta(SConcrete1.class);
            assertEquals("Direct Super", SBase.class, empMeta.getPersistableSuperClass());
        }
        {
            EntityMeta empMeta = EntityFactory.getEntityMeta(SConcrete1Ext.class);
            assertEquals("Direct Super", SBase.class, empMeta.getPersistableSuperClass());
        }
    }
}
