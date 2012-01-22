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
 * Created on Jul 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mysql;

import com.pyx4j.config.server.ServerSideConfiguration;

public class TestConfigurationMySQL extends com.pyx4j.entity.rdb.cfg.ConfigurationMySQL {

    @Override
    public String dbHost() {
        return "localhost";
    }

    @Override
    public String dbName() {
        return "tst_entity";
    }

    @Override
    public String userName() {
        return "tst_entity";
    }

    @Override
    public String password() {
        return "tst_entity";
    }

    @Override
    public int minPoolSize() {
        return 1;
    }

    @Override
    public int maxPoolSize() {
        return 1;
    }

    @Override
    public boolean isMultitenant() {
        return true;
    }

    @Override
    public int tablesItentityOffset() {
        return 937;
    }

    @Override
    public boolean showSql() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int unreturnedConnectionTimeout() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            return 0;
        } else {
            return super.unreturnedConnectionTimeout();
        }
    }
}
