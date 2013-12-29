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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyAutoChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyAutoParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyUnmaintainedOrderChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyUnmaintainedOrderParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToManyParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneInversedChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneInversedParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.detached.BidirectionalOneToOneDetdInverChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.detached.BidirectionalOneToOneDetdInverParent;

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

        // update only child
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

        // remove child and update owner
        Key oldchildKey = o.child().getPrimaryKey();
        o.child().set(null);
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            UnidirectionalOneToOneChild child = srv.retrieve(UnidirectionalOneToOneChild.class, oldchildKey);
            //TODO The persist method is inconsistent for now
            switch (testCaseMethod) {
            case Merge:
                Assert.assertNull("child NOT removed", child);
                break;
            case Persist:
                Assert.assertNotNull("child removed", child);
                break;
            default:
                Assert.fail("n/a");
            }
        }

    }

    //================================================ Unidirectional One-to-One Inversed =========================================================//

    public void testUnidirectionalOneToOneInversedPersist() {
        testUnidirectionalOneToOneInversedSave(TestCaseMethod.Persist);
    }

    public void testUnidirectionalOneToOneInversedMerge() {
        testUnidirectionalOneToOneInversedSave(TestCaseMethod.Merge);
    }

    public void testUnidirectionalOneToOneInversedSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        UnidirectionalOneToOneInversedParent o = EntityFactory.create(UnidirectionalOneToOneInversedParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOneInversedParent parent = srv.retrieve(UnidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOneInversedChild child = srv.retrieve(UnidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOneInversedParent parent = srv.retrieve(UnidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // update only child
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOneInversedParent parent = srv.retrieve(UnidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOneInversedChild child = srv.retrieve(UnidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<UnidirectionalOneToOneInversedParent> criteria = EntityQueryCriteria.create(UnidirectionalOneToOneInversedParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<UnidirectionalOneToOneInversedParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Get Child, change and save independently
        {
            UnidirectionalOneToOneInversedChild child = srv.retrieve(UnidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(UnidirectionalOneToOneInversedChild.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            UnidirectionalOneToOneInversedParent parent = srv.retrieve(UnidirectionalOneToOneInversedParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }

        // remove child and update owner
        Key oldchildKey = o.child().getPrimaryKey();
        o.child().set(null);
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            UnidirectionalOneToOneInversedChild child = srv.retrieve(UnidirectionalOneToOneInversedChild.class, oldchildKey);
            //TODO The persist method is inconsistent for now
            switch (testCaseMethod) {
            case Merge:
                Assert.assertNull("child NOT removed", child);
                break;
            case Persist:
                Assert.assertNotNull("child removed", child);
                break;
            default:
                Assert.fail("n/a");
            }
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

        // update only child
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

        // remove child and update owner
        Key oldchildKey = o.child().getPrimaryKey();
        o.child().set(null);
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToOneChild child = srv.retrieve(BidirectionalOneToOneChild.class, oldchildKey);
            //TODO The persist method is inconsistent for now
            switch (testCaseMethod) {
            case Merge:
                Assert.assertNull("child NOT removed", child);
                break;
            case Persist:
                Assert.assertNotNull("child removed", child);
            }
        }
    }

    //================================================ Bidirectional One-to-One Inversed =========================================================//

    public void testBidirectionalOneToOneInversedTable() {
        resetTables(BidirectionalOneToOneInversedChild.class, BidirectionalOneToOneInversedParent.class);

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
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
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

        // update only child
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

        // remove child and update owner
        Key oldchildKey = o.child().getPrimaryKey();
        o.child().set(null);
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToOneInversedChild child = srv.retrieve(BidirectionalOneToOneInversedChild.class, oldchildKey);
            //TODO The persist method is inconsistent for now
            switch (testCaseMethod) {
            case Merge:
                Assert.assertNull("child NOT removed", child);
                break;
            case Persist:
                Assert.assertNotNull("child removed", child);
            }
        }
    }

    public void testBidirectionalOneToOneDetachedInversedPersist() {
        testBidirectionalOneToOneDetachedInversedSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToOneDetachedInversedMerge() {
        testBidirectionalOneToOneDetachedInversedSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToOneDetachedInversedSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToOneDetdInverParent o = EntityFactory.create(BidirectionalOneToOneDetdInverParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneDetdInverParent parent = srv.retrieve(BidirectionalOneToOneDetdInverParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.IdOnly, parent.child().getAttachLevel());
            srv.retrieve(parent.child());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneDetdInverChild child = srv.retrieve(BidirectionalOneToOneDetdInverChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.IdOnly, child.parent().getAttachLevel());
            srv.retrieve(child.parent());
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneDetdInverParent parent = srv.retrieve(BidirectionalOneToOneDetdInverParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.IdOnly, parent.child().getAttachLevel());
            srv.retrieve(parent.child());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToOneDetdInverChild child = srv.retrieve(BidirectionalOneToOneDetdInverChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("owner data retrieved", AttachLevel.IdOnly, child.parent().getAttachLevel());
            srv.retrieve(child.parent());
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, child.parent().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToOneDetdInverParent> criteria = EntityQueryCriteria.create(BidirectionalOneToOneDetdInverParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<BidirectionalOneToOneDetdInverParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            srv.retrieve(parents.get(0).child());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Query Child By Parent
        {
            EntityQueryCriteria<BidirectionalOneToOneDetdInverChild> criteria = EntityQueryCriteria.create(BidirectionalOneToOneDetdInverChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToOneDetdInverChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 1, children.size());
            srv.retrieve(children.get(0).parent());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToOneDetdInverChild child = srv.retrieve(BidirectionalOneToOneDetdInverChild.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(BidirectionalOneToOneDetdInverChild.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));
            srv.retrieve(child.parent());
            Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());

            BidirectionalOneToOneDetdInverParent parent = srv.retrieve(BidirectionalOneToOneDetdInverParent.class, o.getPrimaryKey());
            srv.retrieve(parent.child());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }

        // remove child and update owner
        Key oldchildKey = o.child().getPrimaryKey();
        o.child().set(null);
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToOneDetdInverChild child = srv.retrieve(BidirectionalOneToOneDetdInverChild.class, oldchildKey);
            //TODO The persist method is inconsistent for now
            switch (testCaseMethod) {
            case Merge:
                Assert.assertNull("child NOT removed", child);
                break;
            case Persist:
                Assert.assertNotNull("child removed", child);
            }
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

        // remove child and update owner
        Key oldchildKey = o.children().get(0).getPrimaryKey();
        o.children().remove(o.children().get(0));
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            UnidirectionalOneToManyChild child = srv.retrieve(UnidirectionalOneToManyChild.class, oldchildKey);
            Assert.assertNull("child NOT removed", child);
        }
    }

    //================================================ Bidirectional One-to-Many =========================================================//

    public void testBidirectionalOneToManyTable() {
        resetTables(BidirectionalOneToManyChild.class, BidirectionalOneToManyParent.class);

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
        testBidirectionalOneToManySave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyMerge() {
        testBidirectionalOneToManySave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToManySave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToManyParent o = EntityFactory.create(BidirectionalOneToManyParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(BidirectionalOneToManyChild.class));
        o.children().add(EntityFactory.create(BidirectionalOneToManyChild.class));

        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue("c0-" + uniqueString());

        o.children().get(1).testId().setValue(testId);
        o.children().get(1).name().setValue("c1-" + uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNotNull("Id Assigned", o.children().get(1).getPrimaryKey());
        Assert.assertEquals("order is set", Integer.valueOf(0), o.children().get(0).orderInParent().getValue());
        Assert.assertEquals("order is set", Integer.valueOf(1), o.children().get(1).orderInParent().getValue());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("child data size", o.children().size(), parent.children().size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), parent.children().get(1).name());
            for (BidirectionalOneToManyChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(0).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.children().get(0).name().setValue("c0u-" + uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("child data size", o.children().size(), parent.children().size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), parent.children().get(1).name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), child.name());
        }

        // Add Child v1 via owner
        BidirectionalOneToManyChild c2 = EntityFactory.create(BidirectionalOneToManyChild.class);
        c2.parent().set(o);
        c2.testId().setValue(testId);
        c2.name().setValue("c2-" + uniqueString());
        o.children().add(c2);
        srvSave(o, testCaseMethod);

        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 3, parent.children().size());
            for (BidirectionalOneToManyChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Add Child v2
        BidirectionalOneToManyChild c3 = EntityFactory.create(BidirectionalOneToManyChild.class);
        c3.parent().set(o);
        c3.testId().setValue(testId);
        c3.name().setValue("c3" + uniqueString());
        c3.orderInParent().setValue(4);
        srvSave(c3, testCaseMethod);

        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 4, parent.children().size());
            for (BidirectionalOneToManyChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
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

        // Query Child By Parent id
        {
            EntityQueryCriteria<BidirectionalOneToManyChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));

            List<BidirectionalOneToManyChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Query Child By Parent filed
        {
            EntityQueryCriteria<BidirectionalOneToManyChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToManyChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            BidirectionalOneToManyChild child = srv.retrieve(BidirectionalOneToManyChild.class, parent.children().get(1).getPrimaryKey());
            child.name().setValue(uniqueString());
            srvSave(child, testCaseMethod);

            BidirectionalOneToManyChild childR1 = srv.retrieve(BidirectionalOneToManyChild.class, parent.children().get(1).getPrimaryKey());
            Assert.assertEquals("child update", child.name(), childR1.name());

            BidirectionalOneToManyParent parentR1 = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertEquals("child update", child.name(), parentR1.children().get(1).name());
        }

        // Verify number of children
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 4, parent.children().size());

            EntityQueryCriteria<BidirectionalOneToManyChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));
            List<BidirectionalOneToManyChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
        }

        // remove child and update owner
        o = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
        Key oldchildKey = o.children().get(1).getPrimaryKey();
        o.children().remove(o.children().get(1));
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToManyParent child = srv.retrieve(BidirectionalOneToManyParent.class, oldchildKey);
            Assert.assertNull("child NOT removed", child);
        }

        // Verify number of children
        {
            BidirectionalOneToManyParent parent = srv.retrieve(BidirectionalOneToManyParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 3, parent.children().size());

            EntityQueryCriteria<BidirectionalOneToManyChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));
            List<BidirectionalOneToManyChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 3, children.size());
        }
    }

    public void testBidirectionalOneToManyUnmaintainedOrderPersist() {
        testBidirectionalOneToManyUnmaintainedOrderSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyUnmaintainedOrderMerge() {
        testBidirectionalOneToManyUnmaintainedOrderSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToManyUnmaintainedOrderSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToManyUnmaintainedOrderParent o = EntityFactory.create(BidirectionalOneToManyUnmaintainedOrderParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(BidirectionalOneToManyUnmaintainedOrderChild.class));
        o.children().add(EntityFactory.create(BidirectionalOneToManyUnmaintainedOrderChild.class));

        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue("c0-" + uniqueString());

        o.children().get(1).testId().setValue(testId);
        o.children().get(1).name().setValue("c1-" + uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNotNull("Id Assigned", o.children().get(1).getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("child data size", o.children().size(), parent.children().size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), parent.children().get(1).name());
            for (BidirectionalOneToManyUnmaintainedOrderChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyUnmaintainedOrderChild child = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderChild.class, o.children().get(0)
                    .getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.children().get(0).name().setValue("c0u-" + uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("child data size", o.children().size(), parent.children().size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), parent.children().get(1).name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            BidirectionalOneToManyUnmaintainedOrderChild child = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderChild.class, o.children().get(1)
                    .getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), child.name());
        }

        // Add Child v1 via owner
        BidirectionalOneToManyUnmaintainedOrderChild c2 = EntityFactory.create(BidirectionalOneToManyUnmaintainedOrderChild.class);
        c2.parent().set(o);
        c2.testId().setValue(testId);
        c2.name().setValue("c2-" + uniqueString());
        o.children().add(c2);
        srvSave(o, testCaseMethod);

        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 3, parent.children().size());
            for (BidirectionalOneToManyUnmaintainedOrderChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Add Child v2
        BidirectionalOneToManyUnmaintainedOrderChild c3 = EntityFactory.create(BidirectionalOneToManyUnmaintainedOrderChild.class);
        c3.parent().set(o);
        c3.testId().setValue(testId);
        c3.name().setValue("c3" + uniqueString());
        srvSave(c3, testCaseMethod);

        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 4, parent.children().size());
            for (BidirectionalOneToManyUnmaintainedOrderChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<BidirectionalOneToManyUnmaintainedOrderParent> criteria = EntityQueryCriteria
                    .create(BidirectionalOneToManyUnmaintainedOrderParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().name(), o.children().get(0).name()));

            List<BidirectionalOneToManyUnmaintainedOrderParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parents.get(0).children().get(0).name());
        }

        // Query Child By Parent id
        {
            EntityQueryCriteria<BidirectionalOneToManyUnmaintainedOrderChild> criteria = EntityQueryCriteria
                    .create(BidirectionalOneToManyUnmaintainedOrderChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));

            List<BidirectionalOneToManyUnmaintainedOrderChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Query Child By Parent filed
        {
            EntityQueryCriteria<BidirectionalOneToManyUnmaintainedOrderChild> criteria = EntityQueryCriteria
                    .create(BidirectionalOneToManyUnmaintainedOrderChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToManyUnmaintainedOrderChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            BidirectionalOneToManyUnmaintainedOrderChild child = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderChild.class, parent.children().get(1)
                    .getPrimaryKey());
            child.name().setValue(uniqueString());
            srvSave(child, testCaseMethod);

            BidirectionalOneToManyUnmaintainedOrderChild childR1 = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderChild.class, parent.children().get(1)
                    .getPrimaryKey());
            Assert.assertEquals("child update", child.name(), childR1.name());

            BidirectionalOneToManyUnmaintainedOrderParent parentR1 = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertEquals("child update", child.name(), parentR1.children().get(1).name());
        }

        // Verify number of children
        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 4, parent.children().size());

            EntityQueryCriteria<BidirectionalOneToManyUnmaintainedOrderChild> criteria = EntityQueryCriteria
                    .create(BidirectionalOneToManyUnmaintainedOrderChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));
            List<BidirectionalOneToManyUnmaintainedOrderChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 4, children.size());
        }

        // remove child and update owner
        o = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
        Key oldchildKey = o.children().get(1).getPrimaryKey();
        o.children().remove(o.children().get(1));
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToManyUnmaintainedOrderParent child = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, oldchildKey);
            Assert.assertNull("child NOT removed", child);
        }

        // Verify number of children
        {
            BidirectionalOneToManyUnmaintainedOrderParent parent = srv.retrieve(BidirectionalOneToManyUnmaintainedOrderParent.class, o.getPrimaryKey());
            Assert.assertEquals("child data size", 3, parent.children().size());

            EntityQueryCriteria<BidirectionalOneToManyUnmaintainedOrderChild> criteria = EntityQueryCriteria
                    .create(BidirectionalOneToManyUnmaintainedOrderChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));
            List<BidirectionalOneToManyUnmaintainedOrderChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 3, children.size());
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
        testBidirectionalOneToManyAutoSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyAutoMerge() {
        testBidirectionalOneToManyAutoSave(TestCaseMethod.Merge);
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

        // Query Child By Parent field
        {
            EntityQueryCriteria<BidirectionalOneToManyAutoChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyAutoChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent().name(), o.name()));

            List<BidirectionalOneToManyAutoChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 2, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Query Child By Parent id
        {
            EntityQueryCriteria<BidirectionalOneToManyAutoChild> criteria = EntityQueryCriteria.create(BidirectionalOneToManyAutoChild.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().parent(), o));

            List<BidirectionalOneToManyAutoChild> children = srv.query(criteria);
            Assert.assertEquals("result set size", 2, children.size());
            Assert.assertEquals("correct data retrieved", o.name(), children.get(0).parent().name());
        }

        // Get Child, change and save independently
        {
            BidirectionalOneToManyAutoChild child = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(1).getPrimaryKey());
            child.name().setValue(uniqueString());
            srvSave(child, testCaseMethod);

            BidirectionalOneToManyAutoChild childR1 = srv.retrieve(BidirectionalOneToManyAutoChild.class, o.children().get(1).getPrimaryKey());
            Assert.assertEquals("child update", child.name(), childR1.name());

            BidirectionalOneToManyAutoParent parent = srv.retrieve(BidirectionalOneToManyAutoParent.class, o.getPrimaryKey());
            Assert.assertEquals("child update", child.name(), parent.children().get(1).name());
        }

        // remove child and update owner
        Key oldchildKey = o.children().get(1).getPrimaryKey();
        o.children().remove(o.children().get(1));
        srvSave(o, testCaseMethod);

        // See that Child was removed
        {
            BidirectionalOneToManyAutoChild child = srv.retrieve(BidirectionalOneToManyAutoChild.class, oldchildKey);
            Assert.assertNull("child NOT removed", child);
        }
    }
}
