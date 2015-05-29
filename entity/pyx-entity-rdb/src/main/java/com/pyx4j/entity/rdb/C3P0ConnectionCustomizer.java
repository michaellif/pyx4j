/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 29, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.util.Map;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

import com.pyx4j.entity.rdb.cfg.ConnectionCustomizer;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class C3P0ConnectionCustomizer extends AbstractConnectionCustomizer {

    @Override
    public void onAcquire(Connection c, String dataSourceIdentityToken) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenExtensions = extensionsForToken(dataSourceIdentityToken);
        ConnectionCustomizer connectionCustomizer = (ConnectionCustomizer) tokenExtensions.get(ConnectionCustomizer.class.getName());
        if (connectionCustomizer != null) {
            ConnectionPoolType connectionPoolType = (ConnectionPoolType) tokenExtensions.get(ConnectionPoolType.class.getName());
            connectionCustomizer.pooledConnectionAcquired(c, connectionPoolType);
        }
    }

}
