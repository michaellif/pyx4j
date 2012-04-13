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

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
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

        // Try Save owner without parent
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
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);
        {
            BidirectionalOneToOneNCPParent parent = srv.retrieve(BidirectionalOneToOneNCPParent.class, o.getPrimaryKey());
            Assert.assertEquals("data did not changed", childName, parent.child().name().getValue());
        }

    }

}
