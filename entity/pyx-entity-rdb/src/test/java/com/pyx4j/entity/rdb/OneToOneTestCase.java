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
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.ownership.managed.Om0OneToOneChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.Om0OneToOneOwner;

public abstract class OneToOneTestCase extends DatastoreTestBase {

    public void testOm0Persist() {
        testOm0Save(TestCaseMethod.Persist);
    }

    public void testOm0Merge() {
        testOm0Save(TestCaseMethod.Merge);
    }

    public void testOm0Save(TestCaseMethod testCaseMethod) {
        // hide the tests for now
        if (Owned.TODO) {
            return;
        }

        String testId = uniqueString();
        Om0OneToOneOwner o = EntityFactory.create(Om0OneToOneOwner.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        // Get Owner and see that child is retrieved, then verify values
        {
            Om0OneToOneOwner oR1 = srv.retrieve(Om0OneToOneOwner.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", oR1);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, oR1.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), oR1.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, oR1.child().owner().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), oR1.child().owner().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), oR1.child().owner().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            Om0OneToOneChild cR1 = srv.retrieve(Om0OneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", cR1);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, cR1.owner().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), cR1.owner().name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Owner and see that child is retrieved, then verify values
        {
            Om0OneToOneOwner oR1 = srv.retrieve(Om0OneToOneOwner.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", oR1);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, oR1.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), oR1.child().name());
            Assert.assertEquals("owner in child data retrieved", AttachLevel.Attached, oR1.child().owner().getAttachLevel());
            Assert.assertEquals("owner in child correct data retrieved", o.getPrimaryKey(), oR1.child().owner().getPrimaryKey());
            Assert.assertEquals("owner in child correct data retrieved", o.name(), oR1.child().owner().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            Om0OneToOneChild cR1 = srv.retrieve(Om0OneToOneChild.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", cR1);
            Assert.assertEquals("owner data retrieved", AttachLevel.Attached, cR1.owner().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.name(), cR1.owner().name());
        }
    }

    public void TODO_testOmQuery() {

    }

}
