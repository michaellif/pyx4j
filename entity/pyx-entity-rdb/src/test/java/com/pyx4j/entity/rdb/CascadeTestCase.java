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

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.test.shared.domain.ownership.cascade.BidirectionalOneToManyNCPChild;
import com.pyx4j.entity.test.shared.domain.ownership.cascade.BidirectionalOneToManyNCPParent;
import com.pyx4j.entity.test.shared.domain.ownership.cascade.BidirectionalOneToOneNCPParent;

public abstract class CascadeTestCase extends AssociationMappingTestCase {

    //================================================ Bidirectional One-to-One =========================================================//

    public void testBidirectionalOneToOneNCPersist() {
        testBidirectionalOneToOneNCSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToOneNCMerge() {
        testBidirectionalOneToOneNCSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToOneNCSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToOneNCPParent o = EntityFactory.create(BidirectionalOneToOneNCPParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());

        String childName = uniqueString();
        o.child().testId().setValue(testId);
        o.child().name().setValue(childName);

        // Try Save owner without child
        boolean saved = false;
        try {
            srvSave(o, testCaseMethod);
            saved = true;
        } catch (Error e) {
            // OK
        }
        if (saved) {
            fail("Should not save entity with non cascade child");
        }

        srvSave(o.child(), testCaseMethod);
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToOneNCPParent parent = srv.retrieve(BidirectionalOneToOneNCPParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, parent.child().parent().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), parent.child().parent().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), parent.child().parent().name());
        }

        // Try update child
        BidirectionalOneToOneNCPParent o2 = o.duplicate();
        o2.child().name().setValue(uniqueString());
        srvSave(o2, testCaseMethod);
        {
            BidirectionalOneToOneNCPParent parent = srv.retrieve(BidirectionalOneToOneNCPParent.class, o.getPrimaryKey());
            Assert.assertEquals("data did not changed", childName, parent.child().name().getValue());
            Assert.assertEquals("data did not changed", testId, parent.child().testId().getValue());
        }

        {
            // See that modifications not firered
            BidirectionalOneToOneNCPParent o3 = o.duplicate();
            o3.child().testId().setValue(uniqueString());
            srvSave(o3, testCaseMethod);
            {
                BidirectionalOneToOneNCPParent parent = srv.retrieve(BidirectionalOneToOneNCPParent.class, o.getPrimaryKey());
                Assert.assertEquals("data did not changed", childName, parent.child().name().getValue());
                Assert.assertEquals("data did not changed", testId, parent.child().testId().getValue());
            }
        }

    }

    public void testBidirectionalOneToManyNCPersist() {
        testBidirectionalOneToManyNCSave(TestCaseMethod.Persist);
    }

    public void testBidirectionalOneToManyNCMerge() {
        testBidirectionalOneToManyNCSave(TestCaseMethod.Merge);
    }

    public void testBidirectionalOneToManyNCSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        BidirectionalOneToManyNCPParent o = EntityFactory.create(BidirectionalOneToManyNCPParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.children().add(EntityFactory.create(BidirectionalOneToManyNCPChild.class));
        o.children().add(EntityFactory.create(BidirectionalOneToManyNCPChild.class));

        o.children().get(0).testId().setValue(testId);
        String child0Name = "c0-" + uniqueString();
        o.children().get(0).name().setValue(child0Name);
        o.children().get(0).orderInParent().setValue(0);

        o.children().get(1).testId().setValue(testId);
        String child1Name = "c1-" + uniqueString();
        o.children().get(1).name().setValue(child1Name);
        o.children().get(1).orderInParent().setValue(1);

        // Save owner without children
        srvSave(o, testCaseMethod);

        Assert.assertNull("children id Not Assigned", o.children().get(0).getPrimaryKey());
        Assert.assertNull("children id Not Assigned", o.children().get(1).getPrimaryKey());

        // Save children
        srvSave(o.children().get(0), testCaseMethod);
        srvSave(o.children().get(1), testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            BidirectionalOneToManyNCPParent parent = srv.retrieve(BidirectionalOneToManyNCPParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("correct data retrieved", o.name(), parent.name());
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.children().getAttachLevel());
            Assert.assertEquals("child data size", o.children().size(), parent.children().size());
            Assert.assertEquals("correct data retrieved", o.children().get(0).name(), parent.children().get(0).name());
            Assert.assertEquals("correct data retrieved", o.children().get(1).name(), parent.children().get(1).name());
            for (BidirectionalOneToManyNCPChild child : o.children()) {
                Assert.assertEquals("correct data retrieved", o.id(), child.parent().id());
                Assert.assertEquals("correct data retrieved", o.name(), child.parent().name());
            }
        }

        // Try update child
        o.children().get(1).name().setValue("u1-" + uniqueString());
        srvSave(o, testCaseMethod);
        {
            BidirectionalOneToManyNCPParent parent = srv.retrieve(BidirectionalOneToManyNCPParent.class, o.getPrimaryKey());
            Assert.assertEquals("data did not changed", child1Name, parent.children().get(1).name().getValue());
        }
    }

}
