/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-20
 * @author vlads
 */
package com.pyx4j.tester.server.crud;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.tester.server.TestServerSideConfiguration;

public class DBTestsSetup {

    private static ServerSideConfiguration initOnce = null;

    public static synchronized void defaultInit() {
        if (initOnce == null) {
            DatabaseType databaseType;
            databaseType = DatabaseType.HSQLDB;
            //databaseType = DatabaseType.MySQL;
            //databaseType = DatabaseType.PostgreSQL;

            // Fail safe if somebody committed the file by mistake 
            if (System.getProperty("bamboo.buildNumber") != null) {
                databaseType = DatabaseType.HSQLDB;
            }
            initOnce = new TestServerSideConfiguration(databaseType);
            ServerSideConfiguration.setInstance(initOnce);
        }
    }

}
