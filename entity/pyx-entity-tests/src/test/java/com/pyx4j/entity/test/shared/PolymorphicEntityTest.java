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
 * Created on Oct 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Base2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntityDTO;

public class PolymorphicEntityTest extends InitializerTestCase {

    public void testAssignments() {

        RefferenceEntityDTO entDTO = EntityFactory.create(RefferenceEntityDTO.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2");
        ent2.nameB1().setValue("b1");
        ent2.nameB2().setValue("b2");
        entDTO.refference().set(ent2);

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        ent.setValue(entDTO.getValue());

        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent.refference().getInstanceValueClass());

        Concrete2Entity ent2r = ent.refference().cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r.nameC2().getValue());
    }

    public void testIsAssignableFrom() {
        RefferenceEntityDTO entDTO = EntityFactory.create(RefferenceEntityDTO.class);

        assertTrue("Member isAssignableFrom ConcreteEntity", entDTO.refference().isAssignableFrom(Concrete2Entity.class));
        assertTrue("Member isAssignableFrom Base2Entity its base", entDTO.refference().isAssignableFrom(Base2Entity.class));
        assertTrue("Member isAssignableFrom Base1Entity its base", entDTO.refference().isAssignableFrom(Base1Entity.class));

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        entDTO.refference().set(ent2);

        assertTrue("Member isAssignableFrom ConcreteEntity", entDTO.refference().isAssignableFrom(Concrete2Entity.class));
        assertTrue("Member isAssignableFrom Base2Entity its base", entDTO.refference().isAssignableFrom(Base2Entity.class));
        assertTrue("Member isAssignableFrom Base1Entity its base", entDTO.refference().isAssignableFrom(Base1Entity.class));

        assertTrue("Member instanceOf ConcreteEntity", entDTO.refference().isInstanceOf(Concrete2Entity.class));
        assertTrue("Member instanceOf Base2Entity its base", entDTO.refference().isInstanceOf(Base2Entity.class));
        assertTrue("Member instanceOf Base1Entity its base", entDTO.refference().isInstanceOf(Base1Entity.class));
    }
}
