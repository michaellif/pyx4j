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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.ownership.managed.Om0OneToOneOwner;

public abstract class OneToOneTestCase extends DatastoreTestBase {

    public void testOm0Persist() {
        Om0OneToOneOwner o = EntityFactory.create(Om0OneToOneOwner.class);
        // Save child and owner

        // Get Owner and see that child is retrieved, then verify values
        {

        }

        // Get Child and see that child is retrieved, then verify values
        {

        }

    }

    public void testOm0Merge() {

    }

    public void testOm0QueryByOwner() {

    }

    public void testOm0QueryByChild() {

    }
}
