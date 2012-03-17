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
import com.pyx4j.entity.test.shared.domain.parametrized.ConcreteParametrizedEntity;
import com.pyx4j.entity.test.shared.domain.parametrized.DetailParameter;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.ItemA.ItemAVersion;
import com.pyx4j.entity.test.shared.domain.version.ItemADTO;

public class EntityGenericTest extends InitializerTestBase {

    public void testParameterizedMembers() {
        EntityMeta meta = EntityFactory.getEntityMeta(ConcreteParametrizedEntity.class);
        assertEquals("Meta entities valueClass", DetailParameter.class, meta.getMemberMeta("entity").getValueClass());
        assertEquals("Meta entities valueClass", DetailParameter.class, meta.getMemberMeta("entities").getValueClass());

        ConcreteParametrizedEntity o = EntityFactory.create(ConcreteParametrizedEntity.class);
        assertEquals("valueClass", DetailParameter.class, o.entity().getValueClass());
        assertEquals("valueClass", DetailParameter.class, o.entities().getValueClass());

        DetailParameter param1 = EntityFactory.create(DetailParameter.class);
        param1.name().setValue("1");

        o.entities().add(param1);

        DetailParameter param2r = o.entities().get(0);
        assertEquals("collection value", param1.name(), param2r.name());

        DetailParameter param2 = o.entities().$();
        assertEquals("valueClass", DetailParameter.class, param2.getValueClass());

        // Test primitive
        assertEquals("Meta entities valueClass", Long.class, meta.getMemberMeta("pvalue").getValueClass());
        assertEquals("Meta entities valueClass", Long.class, meta.getMemberMeta("pvalues").getValueClass());

        assertEquals("valueClass", Long.class, o.pvalue().getValueClass());
        assertEquals("valueClass", Long.class, o.pvalues().getValueClass());

        o.pvalue().setValue(Long.valueOf(10));
        o.pvalues().add(Long.valueOf(11));

    }

    public void testVersionedEntityManipulations() {
        EntityMeta itemAMeta = EntityFactory.getEntityMeta(ItemA.class);

        //version is generic member
        MemberMeta versionMember = itemAMeta.getMemberMeta("version");
        assertEquals("Meta valueClass", ItemAVersion.class, versionMember.getValueClass());

        MemberMeta versionsMember = itemAMeta.getMemberMeta("versions");
        assertEquals("Meta valueClass", ItemAVersion.class, versionsMember.getValueClass());

        ItemA itemA1 = EntityFactory.create(ItemA.class);
        // Test Meta
        assertEquals("Parameterized member valueClass", ItemAVersion.class, itemA1.version().getValueClass());
        assertEquals("Parameterized member objectClass", ItemAVersion.class, itemA1.version().getObjectClass());
        assertEquals("Parameterized member valueClass", ItemA.class, itemA1.version().holder().getValueClass());
        assertEquals("Parameterized member objectClass", ItemA.class, itemA1.version().holder().getObjectClass());

        itemA1.version().testId().setValue("1");
        itemA1.version().name().setValue("2");

        //IVersionInfo<ItemAVersion> vi = itemA1.versionsInfo().$();
        // Will not work
        //vi.version().name().setValue("3");

        //vi.version().set(itemA1.version());

    }

    public void testVersionedEntityDTOManipulations() {
        EntityMeta itemAMeta = EntityFactory.getEntityMeta(ItemADTO.class);

        //version is generic member
        MemberMeta versionMember = itemAMeta.getMemberMeta("version");
        assertEquals("Meta valueClass", ItemAVersion.class, versionMember.getValueClass());

        MemberMeta versionsMember = itemAMeta.getMemberMeta("versions");
        assertEquals("Meta valueClass", ItemAVersion.class, versionsMember.getValueClass());

        ItemADTO itemA1 = EntityFactory.create(ItemADTO.class);

        // Test Meta
        assertEquals("Parameterized member valueClass", ItemAVersion.class, itemA1.version().getValueClass());
        assertEquals("Parameterized member objectClass", ItemAVersion.class, itemA1.version().getObjectClass());
        assertEquals("Parameterized member valueClass", ItemA.class, itemA1.version().holder().getValueClass());
        assertEquals("Parameterized member objectClass", ItemA.class, itemA1.version().holder().getObjectClass());

        itemA1.version().testId().setValue("1");
        itemA1.version().name().setValue("2");
    }
}
