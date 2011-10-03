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
package com.pyx4j.entity.rdb.hsql;

import com.pyx4j.entity.rdb.PersistenceEnvironmentFactory;
import com.pyx4j.entity.rdb.PolymorphicTestCase;
import com.pyx4j.entity.test.server.PersistenceEnvironment;

public class PolymorphicTest extends PolymorphicTestCase {

    @Override
    protected PersistenceEnvironment getPersistenceEnvironment() {
        return PersistenceEnvironmentFactory.getHSQLPersistenceEnvironment();
    }

    /**
     * TODO HyperSQL bug https://sourceforge.net/tracker/?func=detail&aid=3417469&group_id=23316&atid=378131
     */
    @Override
    public void testListMemeberPersist() {
    }

    /**
     * TODO HyperSQL bug https://sourceforge.net/tracker/?func=detail&aid=3417469&group_id=23316&atid=378131
     */
    @Override
    public void testListMemeberMerge() {
    }

}
