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
 * Created on 2010-07-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.cfg;

import com.pyx4j.entity.rdb.dialect.NamingConvention;

public abstract class ConfigurationMySQL implements Configuration {

    @Override
    public String driverClass() {
        return "com.mysql.jdbc.Driver";
    }

    public int dbPort() {
        return 3306;
    }

    @Override
    public String connectionUrl() {
        return "jdbc:mysql://" + dbHost() + ":" + dbPort() + "/" + dbName();
    }

    @Override
    public String connectionValidationQuery() {
        return "SELECT 1";
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public NamingConvention namingConvention() {
        return null;
    }
}
