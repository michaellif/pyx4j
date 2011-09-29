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
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;

public abstract class PolymorphicTestCase extends DatastoreTestBase {

    public void testMemeber() {
    }

    public void TODOtestMemeber() {
        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.name().setValue("n:" + uniqueString());
        ent2.name1().setValue("n1:" + uniqueString());
        ent2.name2().setValue("n2:" + uniqueString());
        ent.refference().set(ent2);

        srv.persist(ent);

        RefferenceEntity entr = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());

        Assert.assertFalse("Value retrived", entr.refference().isValuesDetached());

        Assert.assertEquals("Proper instance", Concrete2Entity.class, entr.refference().getInstanceValueClass());

        Concrete2Entity ent2r = entr.refference().cast();

        Assert.assertEquals("Proper value", ent2.name1().getValue(), ent2r.name1().getValue());
        Assert.assertEquals("Proper value", ent2.name2().getValue(), ent2r.name2().getValue());
        Assert.assertEquals("Proper value", ent2.name().getValue(), ent2r.name().getValue());
    }
}
