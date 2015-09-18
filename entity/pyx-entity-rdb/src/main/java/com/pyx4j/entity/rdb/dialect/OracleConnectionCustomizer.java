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
 * Created on May 3, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionCustomizer;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class OracleConnectionCustomizer implements ConnectionCustomizer {

    private static final Logger log = LoggerFactory.getLogger(OracleConnectionCustomizer.class);

    @Override
    public void initConnectionProperties(Properties properties, ConnectionPoolType connectionType) {
        String programName = CommonsStringUtils.nvl_concat(ServerSideConfiguration.instance().getApplicationName(), //
                ServerSideConfiguration.instance().getEnviromentName(), " ");
        if (CommonsStringUtils.isStringSet(programName)) {
            properties.put("v$session.program", programName);

        }
        if (connectionType != null) {
            properties.put("v$session.terminal", connectionType.name());
        }
    }

    @Override
    public void pooledConnectionAcquired(Connection connection, ConnectionPoolType connectionPoolType) {
        try {
            String moduleName = CommonsStringUtils.nvl_concat(ServerSideConfiguration.instance().getApplicationName(), //
                    ApplicationVersion.getProductBuild(), " ");
            moduleName = CommonsStringUtils.nvl_concat(moduleName, connectionPoolType, " ");
            connection.setClientInfo("OCSID.MODULE", moduleName);
        } catch (Throwable e) {
            log.error("JDBC setClientInfo failed", e);
        }
    }

    @Override
    public void pooledConnectionOnDestroy(Connection connection, ConnectionPoolType connectionPoolType) throws SQLException {
        AsynchronousConnectionsDestoryer.instance().destroyConnection(connection);
    }
}
