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
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import junit.framework.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.attach.AttachToStringMain;

public abstract class AttachToStringTestCase extends DatastoreTestBase {

    public void testToStringMembers() {
        String testId = uniqueString();
        AttachToStringMain o = EntityFactory.create(AttachToStringMain.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());
        o.child().testId().setValue(testId);
        o.child().name().setValue(uniqueString());

        o.children().add(o.children().$());
        o.children().get(0).testId().setValue(testId);
        o.children().get(0).name().setValue(uniqueString());

        // Save owner children
        srv.persist(o);

        {
            AttachToStringMain o1 = srv.retrieve(AttachToStringMain.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", o1);
            Assert.assertEquals("correct data retrieved", o.child().getStringView(), o1.child().getStringView());
            Assert.assertEquals("correct data retrieved", o.children().get(0).getStringView(), o1.children().get(0).getStringView());
        }

    }
}
