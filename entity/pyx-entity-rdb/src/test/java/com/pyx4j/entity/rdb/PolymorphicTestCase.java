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

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete3AssignedPKEntity;
import com.pyx4j.entity.test.shared.domain.inherit.ReferenceEntity;
import com.pyx4j.entity.test.shared.domain.inherit.ReferenceNotOwnerEntity;
import com.pyx4j.entity.test.shared.domain.inherit.single.SBaseEntity;
import com.pyx4j.entity.test.shared.domain.inherit.single.SConcrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.single.SReferenceEntity;

public abstract class PolymorphicTestCase extends DatastoreTestBase {

    public void testMemeberPersist() {
        testMemeber(TestCaseMethod.Persist);
    }

    public void testMemeberMerge() {
        testMemeber(TestCaseMethod.Merge);
    }

    private void testMemeber(TestCaseMethod testCaseMethod) {

        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent2.nameB2().setValue("b2:" + uniqueString());
        ent.reference().set(ent2);

        srvSave(ent, testCaseMethod);

        ReferenceEntity entr = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());

        Assert.assertFalse("Value retrieved", entr.reference().isValueDetached());

        Assert.assertEquals("Proper instance", Concrete2Entity.class, entr.reference().getInstanceValueClass());

        Concrete2Entity ent2r = entr.reference().cast();

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
        ent.reference().set(ent2);

        srv.persist(ent2);

        srvSave(ent, testCaseMethod);

        Concrete2Entity entr = srv.retrieve(Concrete2Entity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper value", ent.nameC2().getValue(), entr.nameC2().getValue());
        Assert.assertTrue("Value retrieved", entr.reference().isValueDetached());
        Assert.assertEquals("Proper instance", Concrete1Entity.class, entr.reference().getInstanceValueClass());

        Concrete1Entity ent2r = entr.reference().cast();
        Assert.assertEquals("Proper PK value", ent2.getPrimaryKey(), ent2r.getPrimaryKey());

        srv.retrieve(entr.reference());
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
        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);
        ent.name().setValue("r:" + uniqueString());

        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent1.nameC2().setValue("c2:" + uniqueString());
        ent1.nameB1().setValue("b1:" + uniqueString());
        ent1.nameB2().setValue("b2:" + uniqueString());
        ent.reference().set(ent1);

        Concrete1Entity ent2 = EntityFactory.create(Concrete1Entity.class);
        ent2.nameC1().setValue("c1:" + uniqueString());
        ent2.nameB1().setValue("n1:" + uniqueString());
        ent1.reference().set(ent2);

        srv.persist(ent2);

        srvSave(ent, testCaseMethod);

        ReferenceEntity entr = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper value", ent.name().getValue(), entr.name().getValue());

        Concrete2Entity ent1r = entr.reference().cast();
        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1r.reference().getInstanceValueClass());

        Concrete1Entity ent2r = ent1r.reference().cast();
        Assert.assertTrue("Value retrived", ent2r.isValueDetached());
        Assert.assertEquals("Proper PK value", ent2.getPrimaryKey(), ent2r.getPrimaryKey());

        srv.retrieve(ent1r.reference());
        Assert.assertEquals("Proper PK value", ent2.id(), ent2r.id());
        Assert.assertEquals("Proper value", ent2.nameC1().getValue(), ent2r.nameC1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());

    }

    public void testListMemeberPersist() {
        testListMemeber(TestCaseMethod.Persist);
    }

    public void testListMemeberMerge() {
        testListMemeber(TestCaseMethod.Merge);
    }

    private void testListMemeber(TestCaseMethod testCaseMethod) {

        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.references().add(ent1);

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent2.nameB2().setValue("b2:" + uniqueString());
        ent.references().add(ent2);

        srvSave(ent, testCaseMethod);
        Assert.assertEquals("Proper size", 2, ent.references().size());

        // Save with no changes
        srvSave(ent, testCaseMethod);

        ReferenceEntity entr1 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Assert.assertEquals("Proper size", 2, entr1.references().size());

        Base1Entity ent1br1 = entr1.references().get(0);

        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1br1.getInstanceValueClass());
        Concrete1Entity ent1r1 = ent1br1.cast();
        Assert.assertEquals("Proper PK value", ent1.id(), ent1r1.id());
        Assert.assertEquals("Proper value", ent1.nameC1().getValue(), ent1r1.nameC1().getValue());
        Assert.assertEquals("Proper value", ent1.nameB1().getValue(), ent1r1.nameB1().getValue());

        Base1Entity ent2br1 = entr1.references().get(1);

        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent2br1.getInstanceValueClass());
        Concrete2Entity ent2r1 = ent2br1.cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r1.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameB2().getValue(), ent2r1.nameB2().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r1.nameC2().getValue());

        assertFalse("Items of different type are different", ent1br1.equals(ent2br1));

        // test change order
        entr1.references().remove(ent1r1);
        Assert.assertEquals("Item was removed", 1, entr1.references().size());

        entr1.references().add(ent1r1);
        srvSave(entr1, testCaseMethod);

        ReferenceEntity entr2 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Assert.assertEquals("Proper size", 2, entr2.references().size());

        Base1Entity ent1br2 = entr2.references().get(1);

        Assert.assertEquals("Proper instance", Concrete1Entity.class, ent1br2.getInstanceValueClass());
        Concrete1Entity ent1r2 = ent1br2.cast();
        Assert.assertEquals("Proper PK value", ent1.id(), ent1r2.id());
        Assert.assertEquals("Proper value", ent1.nameC1().getValue(), ent1r2.nameC1().getValue());
        Assert.assertEquals("Proper value", ent1.nameB1().getValue(), ent1r2.nameB1().getValue());

        Base1Entity ent2r2b = entr2.references().get(0);

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

        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.reference().set(ent1);

        Task task11 = EntityFactory.create(Task.class);
        task11.description().setValue("Do Nothing");
        ent1.tasksSorted().add(task11);

        Task task21 = EntityFactory.create(Task.class);
        task21.description().setValue("Do Something");
        ent1.tasksSorted().add(task21);

        srv.persist(ent);

        ReferenceEntity entr1 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br1 = entr1.reference();
        Concrete1Entity ent1r1 = ent1br1.cast();

        Assert.assertEquals("retrieved List size", 2, ent1r1.tasksSorted().size());
        Task task1r1 = ent1r1.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r1.getPrimaryKey());
        Assert.assertFalse("Values retrieved", task1r1.isValueDetached());

        String description = "Work " + uniqueString();
        task1r1.description().setValue(description);

        ent1r1.tasksSorted().remove(task21);

        srvSave(entr1, testCaseMethod);

        ReferenceEntity entr2 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br2 = entr2.reference();
        Concrete1Entity ent1r2 = ent1br2.cast();

        Assert.assertEquals("retrieved List size", 1, ent1r2.tasksSorted().size());
        Task task1r2 = ent1r2.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r2.getPrimaryKey());
        Assert.assertEquals("description update", description, task1r2.description().getValue());

        Assert.assertNull("Owned entity removed?", srv.retrieve(Task.class, task21.getPrimaryKey()));

    }

    public void testInListOwnedListUpdate() {
        testInListOwnedListUpdate(TestCaseMethod.Persist);
    }

    public void testInListOwnedListMerge() {
        testInListOwnedListUpdate(TestCaseMethod.Merge);
    }

    public void testInListOwnedListUpdate(TestCaseMethod testCaseMethod) {

        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.nameC1().setValue("c1:" + uniqueString());
        ent1.nameB1().setValue("n1:" + uniqueString());
        ent.references().add(ent1);

        Task task11 = EntityFactory.create(Task.class);
        task11.description().setValue("Do Nothing");
        ent1.tasksSorted().add(task11);

        Task task21 = EntityFactory.create(Task.class);
        task21.description().setValue("Do Something");
        ent1.tasksSorted().add(task21);

        srv.persist(ent);

        ReferenceEntity entr1 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br1 = entr1.references().get(0);
        Concrete1Entity ent1r1 = ent1br1.cast();

        Assert.assertEquals("retrieved List size", 2, ent1r1.tasksSorted().size());
        Task task1r1 = ent1r1.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r1.getPrimaryKey());
        Assert.assertFalse("Values retrieved", task1r1.isValueDetached());

        String description = "Work " + uniqueString();
        task1r1.description().setValue(description);

        ent1r1.tasksSorted().remove(task21);

        srvSave(entr1, testCaseMethod);

        ReferenceEntity entr2 = srv.retrieve(ReferenceEntity.class, ent.getPrimaryKey());
        Base1Entity ent1br2 = entr2.references().get(0);
        Concrete1Entity ent1r2 = ent1br2.cast();

        Assert.assertEquals("retrieved List size", 1, ent1r2.tasksSorted().size());
        Task task1r2 = ent1r2.tasksSorted().get(0);
        Assert.assertEquals("Owned value Pk", task11.getPrimaryKey(), task1r2.getPrimaryKey());
        Assert.assertEquals("description update", description, task1r2.description().getValue());

        Assert.assertNull("Owned entity removed?", srv.retrieve(Task.class, task21.getPrimaryKey()));

    }

    public void testQueryByPolymorphicEntity() {
        String testId = uniqueString();

        // Prepare data
        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent1.testId().setValue(testId);
        Concrete1Entity ent11 = EntityFactory.create(Concrete1Entity.class);
        ent11.nameC1().setValue("c1:" + uniqueString());
        ent1.reference().set(ent11);
        srv.persist(ent11);
        srv.persist(ent1);

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.testId().setValue(testId);
        // Force creation of second entity of different type with the same key
        Concrete3AssignedPKEntity ent21 = EntityFactory.create(Concrete3AssignedPKEntity.class);
        ent21.setPrimaryKey(ent11.getPrimaryKey());
        ent21.nameC3().setValue("c3:" + uniqueString());
        ent2.reference().set(ent21);
        srv.persist(ent21);
        srv.persist(ent2);

        // test retrieval
        {
            EntityQueryCriteria<Concrete2Entity> criteria = EntityQueryCriteria.create(Concrete2Entity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().reference(), ent21));
            List<Concrete2Entity> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
            Assert.assertEquals(ent21, found.get(0).reference());
        }

        {
            EntityQueryCriteria<Concrete2Entity> criteria = EntityQueryCriteria.create(Concrete2Entity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().reference(), ent11));
            Concrete2Entity found = srv.retrieve(criteria);
            Assert.assertEquals(ent1, found);
            Assert.assertEquals(ent11, found.reference());
        }

        //Query by Class
        {
            EntityQueryCriteria<Concrete2Entity> criteria = EntityQueryCriteria.create(Concrete2Entity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().reference(), Concrete1Entity.class));
            Concrete2Entity found = srv.retrieve(criteria);
            Assert.assertEquals(ent1, found);
            Assert.assertEquals(ent11, found.reference());
        }
    }

    public void testQueryByPolymorphicEntityInList() {
        String testId = uniqueString();

        // Prepare data
        ReferenceNotOwnerEntity ent1 = EntityFactory.create(ReferenceNotOwnerEntity.class);
        ent1.testId().setValue(testId);
        Concrete1Entity ent11 = EntityFactory.create(Concrete1Entity.class);
        ent11.nameC1().setValue("c1:" + uniqueString());
        ent1.references().add(ent11);
        srv.persist(ent11);
        srv.persist(ent1);

        ReferenceNotOwnerEntity ent2 = EntityFactory.create(ReferenceNotOwnerEntity.class);
        ent2.testId().setValue(testId);
        // Force creation of second entity of different type with the same key
        Concrete3AssignedPKEntity ent21 = EntityFactory.create(Concrete3AssignedPKEntity.class);
        ent21.setPrimaryKey(ent11.getPrimaryKey());
        ent21.nameC3().setValue("c3:" + uniqueString());
        ent2.references().add(ent21);
        srv.persist(ent21);
        srv.persist(ent2);

        {
            EntityQueryCriteria<ReferenceNotOwnerEntity> criteria = EntityQueryCriteria.create(ReferenceNotOwnerEntity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().references(), ent21));
            List<ReferenceNotOwnerEntity> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
            Assert.assertEquals(ent21, found.get(0).references().get(0));
        }

        //Query by Class
        {
            EntityQueryCriteria<ReferenceNotOwnerEntity> criteria = EntityQueryCriteria.create(ReferenceNotOwnerEntity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().references(), Concrete1Entity.class));
            ReferenceNotOwnerEntity found = srv.retrieve(criteria);
            Assert.assertEquals(ent1, found);
            Assert.assertEquals(ent11, found.references().get(0));
        }
    }

    public void testSingleTablePersist() {
        testSingleTable(TestCaseMethod.Persist);
    }

    public void testSingleTableMerge() {
        testSingleTable(TestCaseMethod.Merge);
    }

    private void testSingleTable(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();

        SConcrete2Entity ent = EntityFactory.create(SConcrete2Entity.class);
        ent.testId().setValue(testId);
        ent.nameC2().setValue("c2:" + uniqueString());
        ent.nameB1().setValue("b1:" + uniqueString());

        srvSave(ent, testCaseMethod);

        SBaseEntity entr = srv.retrieve(SBaseEntity.class, ent.getPrimaryKey());

        Assert.assertEquals("Proper instance", SConcrete2Entity.class, entr.getInstanceValueClass());

        SConcrete2Entity ent2r = entr.cast();

        Assert.assertEquals("Proper value", ent.nameB1().getValue(), ent2r.nameB1().getValue());
        Assert.assertEquals("Proper value", ent.nameC2().getValue(), ent2r.nameC2().getValue());
    }

    public void testSingleTableMemeberPersist() {
        testSingleTableMemeber(TestCaseMethod.Persist);
    }

    public void testSingleTableMemeberMerge() {
        testSingleTableMemeber(TestCaseMethod.Merge);
    }

    private void testSingleTableMemeber(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();

        SReferenceEntity ent = EntityFactory.create(SReferenceEntity.class);
        ent.testId().setValue(testId);

        SConcrete2Entity ent2 = EntityFactory.create(SConcrete2Entity.class);
        ent2.nameC2().setValue("c2:" + uniqueString());
        ent2.nameB1().setValue("b1:" + uniqueString());
        ent.reference().set(ent2);

        srvSave(ent, testCaseMethod);

        SReferenceEntity entr = srv.retrieve(SReferenceEntity.class, ent.getPrimaryKey());

        Assert.assertFalse("Value retrieved", entr.reference().isValueDetached());

        Assert.assertEquals("Proper instance", SConcrete2Entity.class, entr.reference().getInstanceValueClass());

        SConcrete2Entity ent2r = entr.reference().cast();

        Assert.assertEquals("Proper value", ent2.nameB1().getValue(), ent2r.nameB1().getValue());
        Assert.assertEquals("Proper value", ent2.nameC2().getValue(), ent2r.nameC2().getValue());
    }
}
