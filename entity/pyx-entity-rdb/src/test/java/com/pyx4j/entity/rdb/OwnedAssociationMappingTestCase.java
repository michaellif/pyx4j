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
 * Created on Feb 8, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyAutoChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyAutoParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToManyParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneParent;

public abstract class OwnedAssociationMappingTestCase extends AssociationMappingTestCase {

    //================================================ Unidirectional One-to-One =========================================================//

    public void testUnidirectionalOneToOneTable() {
        Assert.assertTrue(
                "UnidirectionalOneToOneParent table should exist",
                testColumnExists(UnidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(UnidirectionalOneToOneParent.class).name().getFieldName()));
        Assert.assertTrue(
                "Child column should exist",
                testColumnExists(UnidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(UnidirectionalOneToOneParent.class).child()
                        .getFieldName()));
    }

    public void testUnidirectionalOneToOnePersist() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Persist);
    }

    public void testUnidirectionalOneToOneMerge() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Merge);
    }

    public void testUnidirectionalOneToOneSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        UnidirectionalOneToOneParent o = EntityFactory.create(UnidirectionalOneToOneParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOneParent parent = srv.retrieve(UnidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOneChild child = srv.retrieve(UnidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOneParent parent = srv.retrieve(UnidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOneChild child = srv.retrieve(UnidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<UnidirectionalOneToOneParent> criteria = EntityQueryCriteria.create(UnidirectionalOneToOneParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<UnidirectionalOneToOneParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Get Child, change and save independently
        {
            UnidirectionalOneToOneChild child = srv.retrieve(UnidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(UnidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            UnidirectionalOneToOneParent parent = srv.retrieve(UnidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }

    }

    //================================================ Bidirectional One-to-One =========================================================//

    public void testBidirectionalOneToOneTable() {
        Assert.assertTrue("BidirectionalOneToOneParent table should exist",
                testColumnExists(BidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneParent.class).name().getFieldName()));
        Assert.assertTrue("Child column should exist",
                testColumnExists(BidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneParent.class).child().getFieldName()));
        Assert.assertFalse("Parent column should exist",
                testColumnExists(BidirectionalOneToOneChild.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneChild.class).parent().getFieldName()));

    }

    public void testBidirectionalOneToOnePersist() {
        testBidirectionalOneToOneSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToOneMerge() {
        testBidirectionalOneToOneSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToOneSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToOneParent o = EntityFactory.create(BidirectionalOneToOneParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneParent parent = srv.retrieve(BidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneChild child = srv.retrieve(BidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneParent parent = srv.retrieve(BidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneChild child = srv.retrieve(BidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToOneParent> criteria = EntityQueryCriteria.create(BidirectionalOneToOneParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<BidirectionalOneToOneParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Query Child By Parent
        {
            EntityQueryCriteria<BidirectionalOneToOneChild> criteria = EntityQueryCriteria.create(BidirectionalOneToOneChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToOneChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToOneChild child = srv.retrieve(BidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(BidirectionalOneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());

            BidirectionalOneToOneParent parent = srv.retrieve(BidirectionalOneToOneParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }
    }

    //================================================ Bidirectional One-to-One Inversed =========================================================//

    public void testBidirectionalOneToOneInversedTable() {
        Assert.assertTrue(
                "BidirectionalOneToOneParent table should exist",
                testColumnExists(BidirectionalOneToOneInversedParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneInversedParent.class).name()
                        .getFieldName()));
        Assert.assertFalse(
                "Child column should not exist",
                testColumnExists(BidirectionalOneToOneInversedParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneInversedParent.class).child()
                        .getFieldName()));
        Assert.assertTrue(
                "Parent column should exist",
                testColumnExists(BidirectionalOneToOneInversedChild.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneInversedChild.class).parent()
                        .getFieldName()));

    }

    public void testBidirectionalOneToOneInversedPersist() {
        testBidirectionalOneToOneInversedSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToOneInversedMerge() {
        testBidirectionalOneToOneInversedSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToOneInversedSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToOneInversedParent o = EntityFactory.create(BidirectionalOneToOneInversedParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneInversedParent parent = srv.retrieve(BidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneInversedChild child = srv.retrieve(BidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneInversedParent parent = srv.retrieve(BidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneInversedChild child = srv.retrieve(BidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToOneInversedParent> criteria = EntityQueryCriteria.create(BidirectionalOneToOneInversedParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<BidirectionalOneToOneInversedParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Query Child By Parent
        {
            EntityQueryCriteria<BidirectionalOneToOneInversedChild> criteria = EntityQueryCriteria.create(BidirectionalOneToOneInversedChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToOneInversedChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToOneInversedChild child = srv.retrieve(BidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(BidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());

            BidirectionalOneToOneInversedParent parent = srv.retrieve(BidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }
    }

    //================================================ Unidirectional One-to-Many =========================================================//

    public void testUnidirectionalOneToManyTable() {
        Assert.assertTrue(
                UnidirectionalOneToManyParent.class.getName() + " table should exist",
                testColumnExists(UnidirectionalOneToManyParent.class, EntityFactory.getEntityPrototype(UnidirectionalOneToManyParent.class).name()
                        .getFieldName()));
        Assert.assertTrue(
                "Child column should not exist",
                !testColumnExists(UnidirectionalOneToManyParent.class, EntityFactory.getEntityPrototype(UnidirectionalOneToManyParent.class).children()
                        .getFieldName()));
    }

    public void testUnidirectionalOneToManyPersist() {
        testUnidirectionalOneToManySave(TestCaseMethod.Persist);
    }

    public void testUnidirectionalOneToManyMerge() {
        testUnidirectionalOneToManySave(TestCaseMethod.Merge);
    }

    public void testUnidirectionalOneToManySave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        UnidirectionalOneToManyParent o = EntityFactory.create(UnidirectionalOneToManyParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(UnidirectionalOneToManyChild.class));
        o.children().add(EntityFactory.create(UnidirectionalOneToManyChild.class));

        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue(uniqueString());

        o.children().get(1).testId().setValue(testId);
        o.children().get(1).name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNotNull("Id Assigned", o.children().get(1).getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToManyParent parent = srv.retrieve(UnidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToManyChild child = srv.retrieve(UnidirectionalOneToManyChild.class, o.children().get(0).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.children().get(0).name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToManyParent parent = srv.retrieve(UnidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToManyChild child = srv.retrieve(UnidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<UnidirectionalOneToManyParent> criteria = EntityQueryCriteria.create(UnidirectionalOneToManyParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().name(), o.children().get(0).name()));

            List<UnidirectionalOneToManyParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parents.get(0).children().get(0).name());
        }

        // Get Child, change and save independently
        {
            UnidirectionalOneToManyChild child = srv.retrieve(UnidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(UnidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            UnidirectionalOneToManyParent parent = srv.retrieve(UnidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.children().get(1).name().getValue().endsWith("#"));

        }
    }

    //================================================ Bidirectional One-to-Many =========================================================//

    public void testBidirectionalOneToManyTable() {
        Assert.assertTrue(
                BidirectionalOneToManyParent.class.getName() + " table should exist",
                testColumnExists(BidirectionalOneToManyParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToManyParent.class).name().getFieldName()));

        //TODO this is wrong test for collection table :(
        Assert.assertTrue(
                "Child column should not exist",
                !testColumnExists(BidirectionalOneToManyParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToManyParent.class).children()
                        .getFieldName()));

        Assert.assertTrue(
                "Parent column should exist",
                testColumnExists(BidirectionalOneToManyChild.class, EntityFactory.getEntityPrototype(BidirectionalOneToManyChild.class).parent().getFieldName()));
    }

    public void testBidirectionalOneToManyPersist() {
//        testBidirectionalOneToManySave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyMerge() {
//        testBidirectionalOneToManySave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToManySave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToManyParent o = EntityFactory.create(BidirectionalOneToManyParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(BidirectionalOneToManyChild.class));
        o.children().add(EntityFactory.create(BidirectionalOneToManyChild.class));

        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue(uniqueString());

        o.children().get(1).testId().setValue(testId);
        o.children().get(1).name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNotNull("Id Assigned", o.children().get(1).getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(0).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.children().get(0).name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToManyParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().name(), o.children().get(0).name()));

            List<BidirectionalOneToManyParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parents.get(0).children().get(0).name());
        }

        // Query Child By Parent
        {
            EntityQueryCriteria<BidirectionalOneToManyChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToManyChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.children().get(1).name().getValue().endsWith("#"));

        }
    }

    //================================================ Bidirectional One-to-Many (Join Table) =========================================================//

    public void testBidirectionalOneToManyAutoTable() {
        Assert.assertTrue(
                BidirectionalOneToManyAutoParent.class.getName() + " table should exist",
                testColumnExists(BidirectionalOneToManyAutoParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToManyAutoParent.class).name()
                        .getFieldName()));

        Assert.assertFalse(
                "Parent column should not exist",
                testColumnExists(BidirectionalOneToManyAutoChild.class, EntityFactory.getEntityPrototype(BidirectionalOneToManyAutoChild.class).parent()
                        .getFieldName()));
    }

    public void testBidirectionalOneToManyAutoPersist() {
//        testBidirectionalOneToManyAutoSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyAutoMerge() {
//        testBidirectionalOneToManyAutoSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToManyAutoSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToManyAutoParent o = EntityFactory.create(BidirectionalOneToManyAutoParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(BidirectionalOneToManyAutoChild.class));
        o.children().add(EntityFactory.create(BidirectionalOneToManyAutoChild.class));

        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue(uniqueString());

        o.children().get(1).testId().setValue(testId);
        o.children().get(1).name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNotNull("Id Assigned", o.children().get(1).getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyAutoParent parent = srv.retrieve(BidirectionalOneToManyAutoParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyAutoChild child = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(0).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.children().get(0).name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyAutoParent parent = srv.retrieve(BidirectionalOneToManyAutoParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyAutoChild child = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToManyAutoParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyAutoParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().name(), o.children().get(0).name()));

            List<BidirectionalOneToManyAutoParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parents.get(0).children().get(0).name());
        }

        // Query Child By Parent name
        {
            EntityQueryCriteria<BidirectionalOneToManyAutoChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyAutoChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToManyAutoChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Query Child By Parent id
        {
            EntityQueryCriteria<BidirectionalOneToManyAutoChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyAutoChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));

            List<BidirectionalOneToManyAutoChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToManyAutoChild child = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(1).getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            BidirectionalOneToManyAutoParent parent = srv.retrieve(BidirectionalOneToManyAutoParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.children().get(1).name().getValue().endsWith("#"));

        }
    }
}
