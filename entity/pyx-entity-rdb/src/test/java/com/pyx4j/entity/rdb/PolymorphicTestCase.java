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
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;

public abstract class PolymorphicTestCase extends DatastoreTestBase {

    public void testMemeberPersist() {
        testMemeber(TestCaseMethod.Persist);
    }

    public void testMemeberMerge() {
        testMemeber(TestCaseMethod.Merge);
    }

    private void testMemeber(TestCaseMethod testCaseMethod) {

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent2.nameB2().setValue("b2:" + uniqueString());
        ent.refference().set(ent2);

        srvSave(ent, testCaseMethod);

        RefferenceEntity entr = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());

        Assert.assertFalse("Value retrieved", entr.refference().isValuesDetached());

        Assert.assertEquals("Proper instance", Concrete2Entity.class, entr.refference().getInstanceValueClass());

        Concrete2Entity ent2r = entr.refference().cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r.nameC2().getValue());
    }

    public void testMemeberDetachedL1Persist() {
        testMemeberDetachedL1(TestCaseMethod.Persist);
    }

    public void testMemeberDetachedL1Merge() {
        testMemeberDetachedL1(TestCaseMethod.Merge);
    }

    private void testMemeberDetachedL1(TestCaseMethod testCaseMethod) {

        Concrete2Entity ent = EntityFactory.create(Concrete2Entity.class);
        ent.nameC2().setValue("c1:" + uniqueString());

        Concrete1Entity ent2 = EntityFactory.create(Concrete1Entity.class);
        ent2.nameC1().setValue("c1:" + uniqueString());
        ent2.nameB1().setValue("n1:" + uniqueString());
        ent.refference().set(ent2);

        srv.persist(ent2);

        srvSave(ent, testCaseMethod);

        Concrete2Entity entr = srv.retrieve(Concrete2Entity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper value", ent.nameC2().getValue(), entr.nameC2().getValue());
        Assert.assertTrue("Value retrieved", entr.refference().isValuesDetached());
        Assert.assertEquals("Proper instance", Concrete1Entity.class, entr.refference().getInstanceValueClass());

        Concrete1Entity ent2r = entr.refference().cast();
        Assert.assertEquals("Proper PK value", ent2.getPrimaryKey(), ent2r.getPrimaryKey());

        srv.retrieve(entr.refference());
        Assert.assertEquals("Proper PK value", ent2.id(), ent2r.id());
        Assert.assertEquals("Proper value", ent2.nameC1().getValue(), ent2r.nameC1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());

    }

    public void testMemeberDetachedL2Persist() {
        testMemeberDetachedL2(TestCaseMethod.Persist);
    }

    public void testMemeberDetachedL2Merge() {
        testMemeberDetachedL2(TestCaseMethod.Merge);
    }

    private void testMemeberDetachedL2(TestCaseMethod testCaseMethod) {
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

        srvSave(ent, testCaseMethod);

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

    public void testListMemeberPersist() {
        //TODO
        //testListMemeber(TestCaseMethod.Persist);
    }

    public void testListMemeberMerge() {
        //TODO
        //testListMemeber(TestCaseMethod.Merge);
    }

    private void testListMemeber(TestCaseMethod testCaseMethod) {

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.refferences().add(ent1);

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent2.nameB2().setValue("b2:" + uniqueString());
        ent.refferences().add(ent2);

        srvSave(ent, testCaseMethod);
        Assert.assertEquals("Proper size", 2, ent.refferences().size());

        // Save with no changes
        srvSave(ent, testCaseMethod);

        RefferenceEntity entr1 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Assert.assertEquals("Proper size", 2, entr1.refferences().size());

        Base1Entity ent1br1 = entr1.refferences().get(0);

        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1br1.getInstanceValueClass());
        Concrete1Entity ent1r1 = ent1br1.cast();
        Assert.assertEquals("Proper PK value", ent1.id(), ent1r1.id());
        Assert.assertEquals("Proper value", ent1.nameC1().getValue(), ent1r1.nameC1().getValue());
        Assert.assertEquals("Proper value", ent1.nameB1().getValue(), ent1r1.nameB1().getValue());

        Base1Entity ent2br1 = entr1.refferences().get(1);

        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent2br1.getInstanceValueClass());
        Concrete2Entity ent2r1 = ent2br1.cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r1.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r1.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r1.nameC2().getValue());

        assertFalse("Items of different type are different", ent1br1.equals(ent2br1));

        // test change order
        entr1.refferences().remove(ent1r1);
        Assert.assertEquals("Item was removed", 1, entr1.refferences().size());

        entr1.refferences().add(ent1r1);
        srvSave(entr1, testCaseMethod);

        RefferenceEntity entr2 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Assert.assertEquals("Proper size", 2, entr2.refferences().size());

        Base1Entity ent1br2 = entr2.refferences().get(1);

        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1br2.getInstanceValueClass());
        Concrete1Entity ent1r2 = ent1br2.cast();
        Assert.assertEquals("Proper PK value", ent1.id(), ent1r2.id());
        Assert.assertEquals("Proper value", ent1.nameC1().getValue(), ent1r2.nameC1().getValue());
        Assert.assertEquals("Proper value", ent1.nameB1().getValue(), ent1r2.nameB1().getValue());

        Base1Entity ent2r2b = entr2.refferences().get(0);

        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent2r2b.getInstanceValueClass());
        Concrete2Entity ent2r2 = ent2r2b.cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r2.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r2.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r2.nameC2().getValue());

    }

    public void testOwnedListUpdate() {
        testOwnedListUpdate(TestCaseMethod.Persist);
    }

    public void testOwnedListMerge() {
        testOwnedListUpdate(TestCaseMethod.Merge);
    }

    public void testOwnedListUpdate(TestCaseMethod testCaseMethod) {

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.refference().set(ent1);

        Task task11 = EntityFactory.create(Task.class);
        task11.description().setValue("Do Nothing");
        ent1.tasksSorted().add(task11);

        Task task21 = EntityFactory.create(Task.class);
        task21.description().setValue("Do Something");
        ent1.tasksSorted().add(task21);

        srv.persist(ent);

        RefferenceEntity entr1 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br1 = entr1.refference();
        Concrete1Entity ent1r1 = ent1br1.cast();

        Assert.assertEquals("retrieved List size", 2, ent1r1.tasksSorted().size());
        Task task1r1 = ent1r1.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r1.getPrimaryKey());
        Assert.assertFalse("Values retrieved", task1r1.isValuesDetached());

        String description = "Work " + uniqueString();
        task1r1.description().setValue(description);

        ent1r1.tasksSorted().remove(task21);

        srvSave(entr1, testCaseMethod);

        RefferenceEntity entr2 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br2 = entr2.refference();
        Concrete1Entity ent1r2 = ent1br2.cast();

        Assert.assertEquals("retrieved List size", 1, ent1r2.tasksSorted().size());
        Task task1r2 = ent1r2.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r2.getPrimaryKey());
        Assert.assertEquals("description update", description, task1r2.description().getValue());

        Assert.assertNull("Owned entity removed?", srv.retrieve(Task.class, task21.getPrimaryKey()));

    }

    public void testInListOwnedListUpdate() {
        //TODO
        //testInListOwnedListUpdate(TestCaseMethod.Persist);
    }

    public void testInListOwnedListMerge() {
        //TODO
        //testInListOwnedListUpdate(TestCaseMethod.Merge);
    }

    public void testInListOwnedListUpdate(TestCaseMethod testCaseMethod) {

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.refferences().add(ent1);

        Task task11 = EntityFactory.create(Task.class);
        task11.description().setValue("Do Nothing");
        ent1.tasksSorted().add(task11);

        Task task21 = EntityFactory.create(Task.class);
        task21.description().setValue("Do Something");
        ent1.tasksSorted().add(task21);

        srv.persist(ent);

        RefferenceEntity entr1 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br1 = entr1.refferences().get(0);
        Concrete1Entity ent1r1 = ent1br1.cast();

        Assert.assertEquals("retrieved List size", 2, ent1r1.tasksSorted().size());
        Task task1r1 = ent1r1.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r1.getPrimaryKey());
        Assert.assertFalse("Values retrieved", task1r1.isValuesDetached());

        String description = "Work " + uniqueString();
        task1r1.description().setValue(description);

        ent1r1.tasksSorted().remove(task21);

        srvSave(entr1, testCaseMethod);

        RefferenceEntity entr2 = srv.retrieve(RefferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br2 = entr2.refferences().get(0);
        Concrete1Entity ent1r2 = ent1br2.cast();

        Assert.assertEquals("retrieved List size", 1, ent1r2.tasksSorted().size());
        Task task1r2 = ent1r2.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r2.getPrimaryKey());
        Assert.assertEquals("description update", description, task1r2.description().getValue());

        Assert.assertNull("Owned entity removed?", srv.retrieve(Task.class, task21.getPrimaryKey()));

    }

}
