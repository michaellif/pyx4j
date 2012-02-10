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

import junit.framework.Assert;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneParent;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.UnidirectionalOneToOneParent;

public abstract class OwnedAssociationMappingTestCase extends AssociationMappingTestCase {

    public void testUnidirectionalOneToOneTable() {
        if (Owned.TODO) {
            return;
        }
        Assert.assertTrue(
                "UnidirectionalOneToOneParent existance",
                testColumnExists(UnidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(UnidirectionalOneToOneParent.class).name().getFieldName()));
        Assert.assertTrue(
                "Child column existance",
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
        // hide the tests for now
        if (Owned.TODO) {
            return;
        }

        String testId = uniqueString();
        UnidirectionalOneToOneParent o = EntityFactory.create(UnidirectionalOneToOneParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        // Get Owner and see that child is retrieved, then verify values
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
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Owner and see that child is retrieved, then verify values
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
        }
    }

    public void TODO_testOmQuery() {

    }

    public void testBidirectionalOneToOneTable() {
        if (Owned.TODO) {
            return;
        }
        Assert.assertTrue("BidirectionalOneToOneParent existance",
                testColumnExists(BidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneParent.class).name().getFieldName()));
        Assert.assertTrue("Child column existance",
                testColumnExists(BidirectionalOneToOneParent.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneParent.class).child().getFieldName()));
        Assert.assertFalse("Parent column existance",
                testColumnExists(BidirectionalOneToOneChild.class, EntityFactory.getEntityPrototype(BidirectionalOneToOneChild.class).parent().getFieldName()));

    }

    public void testBidirectionalOneToOnePersist() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToOneMerge() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToOneSave(TestCaseMethod testCaseMethod) {
        // hide the tests for now
        if (Owned.TODO) {
            return;
        }

        String testId = uniqueString();
        BidirectionalOneToOneParent o = EntityFactory.create(BidirectionalOneToOneParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        // Get Owner and see that child is retrieved, then verify values
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

        // Get Owner and see that child is retrieved, then verify values
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
    }

}
