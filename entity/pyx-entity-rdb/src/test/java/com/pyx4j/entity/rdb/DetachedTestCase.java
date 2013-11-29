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

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.detached.DetachedCompletely;
import com.pyx4j.entity.test.shared.domain.detached.DetachedEntity;
import com.pyx4j.entity.test.shared.domain.detached.MainEnity;
import com.pyx4j.entity.test.shared.domain.detached.MainHolderEnity;
import com.pyx4j.entity.test.shared.domain.detached.OwnedWithBackReference;

//TODO move to GAE tests as well
public abstract class DetachedTestCase extends DatastoreTestBase {

    private void testDetachedMember(TestCaseMethod testCaseMethod) {
        MainEnity main = EntityFactory.create(MainEnity.class);
        main.detachedEntity().name().setValue("Test1");

        srv.persist(main);

        MainEnity mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.detachedEntity().isValueDetached());
        Assert.assertTrue("is detached value null", mainR.detachedEntity().name().isNull());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.detachedEntity());

        Assert.assertFalse("is retrieved", mainR.detachedEntity().isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntity().name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntity().name().getValue(), mainR.detachedEntity().name().getValue());
    }

    public void testDetachedMemberPersist() {
        testDetachedMember(TestCaseMethod.Persist);
    }

    public void testDetachedMemberMerge() {
        testDetachedMember(TestCaseMethod.Merge);
    }

    private void testDetachedMemberInList(TestCaseMethod testCaseMethod) {
        MainEnity main = EntityFactory.create(MainEnity.class);
        DetachedEntity d0 = EntityFactory.create(DetachedEntity.class);
        d0.name().setValue("Test2.0");
        main.detachedEntities().add(d0);

        DetachedEntity d1 = EntityFactory.create(DetachedEntity.class);
        d1.name().setValue("Test2.1");
        main.detachedEntities().add(d1);

        srv.persist(main);

        MainEnity mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());

        Assert.assertTrue("is detached", mainR.detachedEntities().get(0).isValueDetached());
        Assert.assertTrue("is detached value null", mainR.detachedEntities().get(0).name().isNull());

        Assert.assertTrue("is detached", mainR.detachedEntities().get(1).isValueDetached());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.detachedEntities());

        Assert.assertFalse("is retrieved", mainR.detachedEntities().get(0).isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntities().get(0).name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntities().get(0).name().getValue(), mainR.detachedEntities().get(0).name().getValue());

        Assert.assertFalse("is retrieved", mainR.detachedEntities().get(1).isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.detachedEntities().get(1).name().isNull());
        Assert.assertEquals("did retrieve value", main.detachedEntities().get(1).name().getValue(), mainR.detachedEntities().get(1).name().getValue());
    }

    public void testDetachedMemberInListPersist() {
        testDetachedMemberInList(TestCaseMethod.Persist);
    }

    public void testDetachedMembeInListrMerge() {
        testDetachedMemberInList(TestCaseMethod.Merge);
    }

    public void testDetachedMemberInOwnedObject(TestCaseMethod testCaseMethod) {
        MainHolderEnity main = EntityFactory.create(MainHolderEnity.class);
        main.ownedEntity().name().setValue("Test2");
        main.ownedEntity().detachedEntity().name().setValue("Test2.C");

        srv.persist(main);

        MainHolderEnity mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());
        Assert.assertNotNull("retrieved by PK", mainR);

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntity().isValueDetached());
        Assert.assertTrue("is detached value null", mainR.ownedEntity().detachedEntity().name().isNull());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.ownedEntity().detachedEntity());

        Assert.assertEquals("main value", main.ownedEntity().name().getValue(), mainR.ownedEntity().name().getValue());
        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntity().isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntity().name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntity().name().getValue(), mainR.ownedEntity().detachedEntity().name().getValue());
    }

    public void testDetachedMemberInOwnedObjectPersist() {
        testDetachedMemberInOwnedObject(TestCaseMethod.Persist);
    }

    public void testDetachedMemberInOwnedObjectMerge() {
        testDetachedMemberInOwnedObject(TestCaseMethod.Merge);
    }

    public void testDetachedMemberInListInOwnedObject(TestCaseMethod testCaseMethod) {
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

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntities().get(0).isValueDetached());
        Assert.assertTrue("is detached value null", mainR.ownedEntity().detachedEntities().get(0).name().isNull());

        Assert.assertTrue("is detached", mainR.ownedEntity().detachedEntities().get(1).isValueDetached());

        // See if it did not saved value
        srvSave(mainR, testCaseMethod);
        mainR = srv.retrieve(MainHolderEnity.class, main.getPrimaryKey());
        srv.retrieve(mainR.ownedEntity().detachedEntities());

        Assert.assertEquals("main value", main.ownedEntity().name().getValue(), mainR.ownedEntity().name().getValue());

        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntities().get(0).isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntities().get(0).name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntities().get(0).name().getValue(), mainR.ownedEntity().detachedEntities().get(0)
                .name().getValue());

        Assert.assertFalse("is retrieved", mainR.ownedEntity().detachedEntities().get(1).isValueDetached());
        Assert.assertFalse("did retrieve value", mainR.ownedEntity().detachedEntities().get(1).name().isNull());
        Assert.assertEquals("did retrieve value", main.ownedEntity().detachedEntities().get(1).name().getValue(), mainR.ownedEntity().detachedEntities().get(1)
                .name().getValue());
    }

    public void testDetachedMemberInListInOwnedObjectPersist() {
        testDetachedMemberInListInOwnedObject(TestCaseMethod.Persist);
    }

    public void testDetachedMemberInListInOwnedObjectMerge() {
        testDetachedMemberInListInOwnedObject(TestCaseMethod.Merge);
    }

    public void testDetachedOwnerMember() {
        MainHolderEnity main1 = EntityFactory.create(MainHolderEnity.class);
        main1.name().setValue("m1 " + uniqueString());
        main1.ownedWithBackReference().name().setValue("c1 " + uniqueString());

        srv.persist(main1);

        MainHolderEnity main1R1 = srv.retrieve(MainHolderEnity.class, main1.getPrimaryKey());

        Assert.assertFalse("is detached", main1R1.ownedWithBackReference().detachedOwner().isValueDetached());
        Assert.assertEquals(main1.name().getValue(), main1R1.ownedWithBackReference().detachedOwner().name().getValue());

        OwnedWithBackReference ownedDetachedR = srv.retrieve(OwnedWithBackReference.class, main1R1.ownedWithBackReference().getPrimaryKey());
        Assert.assertTrue("is detached", ownedDetachedR.detachedOwner().isValueDetached());

        // Test data corruption error
        MainHolderEnity main2 = EntityFactory.create(MainHolderEnity.class);
        main2.name().setValue("m2 " + uniqueString());
        srv.persist(main2);

        // Try Move do different owner
        OwnedWithBackReference ownedDetached = main1R1.ownedWithBackReference().detach();
        ownedDetached.detachedOwner().set(main2);
        srv.persist(ownedDetached);

        // verify that child is still with the same owner
        {
            MainHolderEnity main1r2 = srv.retrieve(MainHolderEnity.class, main1.getPrimaryKey());
            Assert.assertEquals(main1.name().getValue(), main1r2.ownedWithBackReference().detachedOwner().name().getValue());
        }
    }

    public void testDetachedCompletelyPersist() {
        testDetachedCompletelySave(TestCaseMethod.Persist);
    }

    public void testDetachedCompletelyMerge() {
        testDetachedCompletelySave(TestCaseMethod.Merge);
    }

    public void testDetachedCompletelySave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        DetachedCompletely o = EntityFactory.create(DetachedCompletely.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());

        Assert.assertEquals("default child data", AttachLevel.Attached, o.child().getAttachLevel());
        Assert.assertEquals("default children data", AttachLevel.Attached, o.children().getAttachLevel());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertEquals("child data set by persist", AttachLevel.Detached, o.child().getAttachLevel());
        Assert.assertEquals("children data set by persist", AttachLevel.Detached, o.children().getAttachLevel());

        // child data not created
        srv.retrieveMember(o.child());
        Assert.assertTrue("child data not created", o.child().isNull());

        // Create child
        o.child().testId().setValue(testId);
        String cName1 = "c" + uniqueString();
        o.child().name().setValue(cName1);
        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertEquals(testId, o.child().testId().getValue());
        Assert.assertEquals(cName1, o.child().name().getValue());

        {
            DetachedCompletely or1 = srv.retrieve(DetachedCompletely.class, o.getPrimaryKey());
            Assert.assertEquals("child data is not retrieved", AttachLevel.Detached, or1.child().getAttachLevel());
            srv.retrieveMember(or1.child());
            Assert.assertEquals(testId, or1.child().testId().getValue());
            Assert.assertEquals(cName1, or1.child().name().getValue());

            Assert.assertEquals("child data is not retrieved", AttachLevel.Detached, or1.children().getAttachLevel());
            boolean accessed = false;
            try {
                or1.children().iterator();
                accessed = true;
            } catch (AssertionError ok) {
            }
            Assert.assertFalse("access should have fail", accessed);

        }

        // Update child
        String cName2 = "u" + uniqueString();
        o.child().name().setValue(cName2);
        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertEquals(testId, o.child().testId().getValue());
        Assert.assertEquals(cName2, o.child().name().getValue());
    }

    public void testDetachedMerge() {
        String testId = uniqueString();
        DetachedCompletely o = EntityFactory.create(DetachedCompletely.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        srv.merge(o);

        Date initialDate = o.updated().getValue();

        SystemDateManager.setDate(DateUtils.addHours(SystemDateManager.getDate(), 1));

        srv.merge(o);
        Assert.assertEquals("Entity should not be saved", initialDate, o.updated().getValue());

    }
}
