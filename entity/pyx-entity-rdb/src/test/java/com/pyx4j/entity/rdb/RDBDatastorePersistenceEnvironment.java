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
 * Created on 2010-07-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import org.junit.After;
import org.junit.Before;

import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.test.server.PersistenceEnvironment;
import com.pyx4j.server.contexts.NamespaceManager;

public class RDBDatastorePersistenceEnvironment extends PersistenceEnvironment {

    private final Configuration configuration;

    public RDBDatastorePersistenceEnvironment(Configuration cfg) {
        configuration = cfg;
    }

    @Override
    public ApplicationBackendType getBackendType() {
        return ApplicationBackendType.RDB;
    }

    @Override
    @Before
    public IEntityPersistenceService setupDatastore() {
        NamespaceManager.setNamespace("-t");
        return new EntityPersistenceServiceRDB(configuration);
    }

    @Override
    @After
    public void teardownDatastore(IEntityPersistenceService srv) {
        if (srv instanceof IEntityPersistenceServiceExt) {
            ((IEntityPersistenceServiceExt) srv).dispose();
        }
        NamespaceManager.remove();
    }

}
