/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.PersistenceEnvironment;

public class LocalDatastorePersistenceEnvironment extends PersistenceEnvironment {

    /** true to store saved changes, default to false */
    protected boolean storeChanges = false;

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private static int uniqueCount = 0;

    @Override
    @Before
    public void setupDatastore() {
        helper.setUp();
        EntityFactory.setImplementation(new ServerEntityFactory());
    }

    @Override
    @After
    public void teardownDatastore() {
        helper.tearDown();
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    protected synchronized String uniqueString() {
        return Integer.toHexString(++uniqueCount) + "_" + Long.toHexString(System.currentTimeMillis());
    }
}