/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

import com.propertyvista.config.VistaDBNamingConvention;

public class VistaTestsDBConfigurationMySQL extends com.pyx4j.entity.rdb.cfg.ConfigurationMySQL {

    @Override
    public String dbHost() {
        return "localhost";
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
    public boolean createForeignKeys() {
        return true;
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
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention();
    }

    @Override
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return new VistaTestsConnectionPoolConfiguration(connectionType);
    }

}
