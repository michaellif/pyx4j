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
 * Created on Sep 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.detached.DetachedEntity;
import com.pyx4j.entity.test.shared.domain.detached.MainEnity;
import com.pyx4j.entity.test.shared.domain.detached.MainHolderEnity;

//TODO move to GAE tests as well
public abstract class DetachedTestCase extends DatastoreTestBase {

    private void testDetachedMemeber(TestCaseMethod testCaseMethod) {
        MainEnity main = EntityFactory.create(MainEnity.class);
        main.detachedEntity().name().setValue("Test1");

        srv.persist(main);

        MainEnity mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.detachedEntity().isValuesDetached());
        Assert.assertTrue("is detached value null", mainR.detachedEntity().name().isNull());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.detachedEntity());

        Assert.assertFalse("is retrieved", mainR.detachedEntity().isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntity().name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntity().name().getValue(), mainR.detachedEntity().name().getValue());
    }

    public void testDetachedMemeberPersist() {
        testDetachedMemeber(TestCaseMethod.Persist);
    }

    public void testDetachedMemeberMerge() {
        testDetachedMemeber(TestCaseMethod.Merge);
    }

    private void testDetachedMemeberInList(TestCaseMethod testCaseMethod) {
        MainEnity main = EntityFactory.create(MainEnity.class);
        DetachedEntity d0 = EntityFactory.create(DetachedEntity.class);
        d0.name().setValue("Test2.0");
        main.detachedEntities().add(d0);

        DetachedEntity d1 = EntityFactory.create(DetachedEntity.class);
        d1.name().setValue("Test2.1");
        main.detachedEntities().add(d1);

        srv.persist(main);

        MainEnity mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.detachedEntities().get(0).isValuesDetached());
        Assert.assertTrue("is detached value null", mainR.detachedEntities().get(0).name().isNull());

        Assert.assertTrue("is detached", mainR.detachedEntities().get(1).isValuesDetached());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.detachedEntities());

        Assert.assertFalse("is retrieved", mainR.detachedEntities().get(0).isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntities().get(0).name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntities().get(0).name().getValue(), mainR.detachedEntities().get(0).name().getValue());

        Assert.assertFalse("is retrieved", mainR.detachedEntities().get(1).isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntities().get(1).name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntities().get(1).name().getValue(), mainR.detachedEntities().get(1).name().getValue());
    }

    public void testDetachedMemeberInListPersist() {
        testDetachedMemeberInList(TestCaseMethod.Persist);
    }

    public void testDetachedMemebeInListrMerge() {
        testDetachedMemeberInList(TestCaseMethod.Merge);
    }

    public void testDetachedMemeberInOwnedObject(TestCaseMethod testCaseMethod) {
        MainHolderEnity main = EntityFactory.create(MainHolderEnity.class);
        main.ownedEntity().name().setValue("Test2");
        main.ownedEntity().detachedEntity().name().setValue("Test2.C");

        srv.persist(main);

        MainHolderEnity mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntity().isValuesDetached());
        Assert.assertTrue("is detached value null", mainR.ownedEntity().detachedEntity().name().isNull());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.ownedEntity().detachedEntity());

        Assert.assertEquals("main value", main.ownedEntity().name().getValue(), mainR.ownedEntity().name().getValue());
        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntity().isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntity().name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntity().name().getValue(), mainR.ownedEntity().detachedEntity().name().getValue());
    }

    public void testDetachedMemeberInOwnedObjectPersist() {
        testDetachedMemeberInOwnedObject(TestCaseMethod.Persist);
    }

    public void testDetachedMemeberInOwnedObjectMerge() {
        testDetachedMemeberInOwnedObject(TestCaseMethod.Merge);
    }

    public void testDetachedMemeberInListInOwnedObject(TestCaseMethod testCaseMethod) {
        MainHolderEnity main = EntityFactory.create(MainHolderEnity.class);
        main.ownedEntity().name().setValue("Test4");

        DetachedEntity d0 = EntityFactory.create(DetachedEntity.class);
        d0.name().setValue("Test4.0");
        main.ownedEntity().detachedEntities().add(d0);

        DetachedEntity d1 = EntityFactory.create(DetachedEntity.class);
        d1.name().setValue("Test4.1");
        main.ownedEntity().detachedEntities().add(d1);

        srv.persist(main);

        MainHolderEnity mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntities().get(0).isValuesDetached());
        Assert.assertTrue("is detached value null", mainR.ownedEntity().detachedEntities().get(0).name().isNull());

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntities().get(1).isValuesDetached());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.ownedEntity().detachedEntities());

        Assert.assertEquals("main value", main.ownedEntity().name().getValue(), mainR.ownedEntity().name().getValue());

        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntities().get(0).isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntities().get(0).name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntities().get(0).name().getValue(), mainR.ownedEntity().detachedEntities().get(0)
                .name().getValue());

        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntities().get(1).isValuesDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntities().get(1).name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntities().get(1).name().getValue(), mainR.ownedEntity().detachedEntities().get(1)
                .name().getValue());
    }

    public void testDetachedMemeberInListInOwnedObjectPersist() {
        testDetachedMemeberInListInOwnedObject(TestCaseMethod.Persist);
    }

    public void testDetachedMemeberInListInOwnedObjectMerge() {
        testDetachedMemeberInListInOwnedObject(TestCaseMethod.Merge);
    }

}
