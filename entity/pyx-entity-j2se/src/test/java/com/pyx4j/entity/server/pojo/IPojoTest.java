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
 * Created on Sep 10, 2011
 * @author vlads
 */
package com.pyx4j.entity.server.pojo;

import java.util.Collection;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtils;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityPojoWrapperGenerator;
import com.pyx4j.entity.server.pojo.model.PojoMemberTestEntity;
import com.pyx4j.entity.server.pojo.model.PojoTestEntity;

public class IPojoTest extends TestCase {

    public void testPojoPrimitives() throws Exception {

        PojoTestEntity entity = EntityFactory.create(PojoTestEntity.class);
        entity.name().setValue("Bob 25");

        IPojo<PojoTestEntity> pojo = ServerEntityFactory.getPojo(entity);

        assertEquals("Access to entity value via Pjo", "Bob 25", BeanUtils.getProperty(pojo, entity.name().getFieldName()));

        BeanUtils.setProperty(pojo, entity.name().getFieldName(), "Petia 10");
        assertEquals("Modify Entit value via Pojo", "Petia 10", entity.name().getValue());

        assertNull(BeanUtils.getProperty(pojo, entity.entMember().getFieldName() + "." + entity.entMember().description().getFieldName()));
        entity.entMember().description().setValue("Kolia 7");
        assertEquals("Kolia 7", BeanUtils.getProperty(pojo, entity.entMember().getFieldName() + "." + entity.entMember().description().getFieldName()));

    }

    public void testPojoPrimitiveSet() throws Exception {

        PojoTestEntity entity = EntityFactory.create(PojoTestEntity.class);
        entity.aliases().add("Bob 21");
        entity.aliases().add("Bob 22");

        IPojo<PojoTestEntity> pojo = ServerEntityFactory.getPojo(entity);

        if (EntityPojoWrapperGenerator.useCollectionForPrimitiveSet) {
            Collection<String> data = new Vector<String>();
            data.add("v1");
            data.add("v2");

            //BeanUtils.setProperty(pojo, entity.aliases().getFieldName(), data);

            //assertEquals("v1", entity.aliases().iterator().next());
        } else {
            assertEquals("Bob 21", BeanUtils.getProperty(pojo, entity.aliases().getFieldName() + "[0]"));
            assertEquals("Bob 22", BeanUtils.getProperty(pojo, entity.aliases().getFieldName() + "[1]"));

            String[] data = new String[] { "v1", "v2" };
            BeanUtils.setProperty(pojo, entity.aliases().getFieldName(), data);

            assertEquals("v1", BeanUtils.getProperty(pojo, entity.aliases().getFieldName() + "[0]"));
            assertEquals("v2", BeanUtils.getProperty(pojo, entity.aliases().getFieldName() + "[1]"));
            assertEquals("v1", entity.aliases().iterator().next());
        }

    }

    public void testPojoArrays() throws Exception {
        PojoTestEntity entity = EntityFactory.create(PojoTestEntity.class);
        PojoMemberTestEntity arrayItem = EntityFactory.create(PojoMemberTestEntity.class);
        arrayItem.description().setValue("Me Item");
        entity.entList().add(arrayItem);
        IPojo<PojoTestEntity> pojo = ServerEntityFactory.getPojo(entity);

        Object pojoItemInArrayValue = BeanUtils.getProperty(pojo, entity.entList().getFieldName() + "[0]" + arrayItem.description().getFieldName());
        assertEquals("Should get value", pojoItemInArrayValue, arrayItem.description().getValue());
    }
}
