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
 * Created on Sep 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;

public abstract class PolymorphicTestCase extends DatastoreTestBase {

    public void testMemeber() {

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent2.nameB2().setValue("b2:" + uniqueString());
        ent.refference().set(ent2);

        srv.persist(ent);

        RefferenceEntity entr = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());

        Assert.assertFalse("Value retrived", entr.refference().isValuesDetached());

        Assert.assertEquals("Proper instance", Concrete2Entity.class, entr.refference().getInstanceValueClass());

        Concrete2Entity ent2r = entr.refference().cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r.nameC2().getValue());
    }

    public void testMemeberDetachedL1() {

        Concrete2Entity ent = EntityFactory.create(Concrete2Entity.class);
        ent.nameC2().setValue("c1:" + uniqueString());

        Concrete1Entity ent2 = EntityFactory.create(Concrete1Entity.class);
        ent2.nameC1().setValue("c1:" + uniqueString());
        ent2.nameB1().setValue("n1:" + uniqueString());
        ent.refference().set(ent2);

        srv.persist(ent2);

        srv.persist(ent);

        Concrete2Entity entr = srv.retrieve(Concrete2Entity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper value", ent.nameC2().getValue(), entr.nameC2().getValue());
        Assert.assertTrue("Value retrived", entr.refference().isValuesDetached());
        Assert.assertEquals("Proper instance", Concrete1Entity.class, entr.refference().getInstanceValueClass());

        Concrete1Entity ent2r = entr.refference().cast();
        Assert.assertEquals("Proper PK value", ent2.getPrimaryKey(), ent2r.getPrimaryKey());

        srv.retrieve(entr.refference());
        Assert.assertEquals("Proper PK value", ent2.id(), ent2r.id());
        Assert.assertEquals("Proper value", ent2.nameC1().getValue(), ent2r.nameC1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());

    }

    public void testMemeberDetachedL2() {
        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        ent.name().setValue("r:" + uniqueString());

        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent1.nameC2().setValue("c2:" + uniqueString());
        ent1.nameB1().setValue("b1:" + uniqueString());
        ent1.nameB2().setValue("b2:" + uniqueString());
        ent.refference().set(ent1);

        Concrete1Entity ent2 = EntityFactory.create(Concrete1Entity.class);
        ent2.nameC1().setValue("c1:" + uniqueString());
        ent2.nameB1().setValue("n1:" + uniqueString());
        ent1.refference().set(ent2);

        srv.persist(ent2);

        srv.persist(ent);

        RefferenceEntity entr = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper value", ent.name().getValue(), entr.name().getValue());

        Concrete2Entity ent1r = entr.refference().cast();
        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1r.refference().getInstanceValueClass());

        Concrete1Entity ent2r = ent1r.refference().cast();
        Assert.assertTrue("Value retrived", ent2r.isValuesDetached());
        Assert.assertEquals("Proper PK value", ent2.getPrimaryKey(), ent2r.getPrimaryKey());

        srv.retrieve(ent1r.refference());
        Assert.assertEquals("Proper PK value", ent2.id(), ent2r.id());
        Assert.assertEquals("Proper value", ent2.nameC1().getValue(), ent2r.nameC1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());

    }
}
