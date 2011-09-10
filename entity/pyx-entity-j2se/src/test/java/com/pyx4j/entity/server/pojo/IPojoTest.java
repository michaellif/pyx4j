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
 * @version $Id$
 */
package com.pyx4j.entity.server.pojo;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtils;

import com.pyx4j.entity.server.impl.EntityPojoWrapperGenerator;
import com.pyx4j.entity.server.pojo.model.PojoTestEntity;
import com.pyx4j.entity.shared.EntityFactory;

public class IPojoTest extends TestCase {

    public void testPojoCreation() throws Exception {

        PojoTestEntity entity = EntityFactory.create(PojoTestEntity.class);
        entity.name().setValue("Bob 25");

        Class<IPojo<PojoTestEntity>> pojoClass = EntityPojoWrapperGenerator.getPojoClass(PojoTestEntity.class);

        IPojo<PojoTestEntity> pojo = pojoClass.newInstance();
        pojo.setEntityValue(entity);

        assertEquals("Access to entity value via Pjo", BeanUtils.getProperty(pojo, entity.name().getFieldName()), "Bob 25");

        BeanUtils.setProperty(pojo, entity.name().getFieldName(), "Petia 10");
        assertEquals("Modify Entit value via Pojo", entity.name().getValue(), "Petia 10");
    }
}
