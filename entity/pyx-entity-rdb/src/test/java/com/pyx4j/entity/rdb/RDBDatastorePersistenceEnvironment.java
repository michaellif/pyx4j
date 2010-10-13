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

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.test.server.PersistenceEnvironment;

public class RDBDatastorePersistenceEnvironment extends PersistenceEnvironment {

    private static Configuration configuration;

    static {
        ServerSideConfiguration.setInstance(new ServerSideConfiguration() {
            @Override
            public IPersistenceConfiguration getPersistenceConfiguration() {
                return configuration;
            }
        });
    }

    public RDBDatastorePersistenceEnvironment(Configuration cfg) {
        configuration = cfg;
    }

    @Override
    @Before
    public void setupDatastore() {
    }

    @Override
    @After
    public void teardownDatastore() {
        PersistenceServicesFactory.dispose();
    }

}
