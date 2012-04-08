/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionModern;

public class VistaConfigurationMySQL extends com.pyx4j.entity.rdb.cfg.ConfigurationMySQL {

    @Override
    public String dbHost() {
        return "localhost";
    }

    @Override
    public boolean showSql() {
        return false;
    }

    @Override
    public boolean createForeignKeys() {
        return true;
    }

    @Override
    public boolean isAutoReconnect() {
        return true;
    }

    @Override
    public String dbName() {
        return "vista";
    }

    @Override
    public String userName() {
        return "vista";
    }

    @Override
    public String password() {
        return "vista";
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SharedSchema;
    }

    @Override
    public int minPoolSize() {
        return 3;
    }

    @Override
    public int maxPoolSize() {
        return 10;
    }

    @Override
    public int tablesItentityOffset() {
        return 997;
    }

    @Override
    public NamingConvention namingConvention() {
        return new NamingConventionModern();
    }

    @Override
    public int unreturnedConnectionTimeout() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode() && ServerSideConfiguration.isStartedUnderEclipse()) {
            return 0;
        } else {
            return super.unreturnedConnectionTimeout();
        }
    }
}
