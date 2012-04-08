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
 * Created on 2011-06-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.server.config;

import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionModern;

public class PaymentPadConfigurationMySQL extends com.pyx4j.entity.rdb.cfg.ConfigurationMySQL {

    @Override
    public String dbHost() {
        return "localhost";
    }

    @Override
    public String dbName() {
        return "paypad";
    }

    @Override
    public String userName() {
        return "paypad";
    }

    @Override
    public String password() {
        return "paypad";
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SharedSchema;
    }

    @Override
    public int minPoolSize() {
        return 2;
    }

    @Override
    public int maxPoolSize() {
        return 10;
    }

    @Override
    public NamingConvention namingConvention() {
        return new NamingConventionModern();
    }

}
