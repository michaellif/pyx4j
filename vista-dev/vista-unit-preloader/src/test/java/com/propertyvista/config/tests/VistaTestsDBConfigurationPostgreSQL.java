/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

import com.propertyvista.config.VistaDBNamingConvention;

public class VistaTestsDBConfigurationPostgreSQL extends com.pyx4j.entity.rdb.cfg.ConfigurationPostgreSQL {

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
    public int minPoolSize() {
        return 1;
    }

    @Override
    public int maxPoolSize() {
        return 2;
    }

    @Override
    public int maxBackgroundProcessPoolSize() {
        return 1;
    }

    @Override
    public int unreturnedConnectionTimeout() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            return 0;
        } else {
            return super.unreturnedConnectionTimeout();
        }
    }

    @Override
    public int unreturnedConnectionBackgroundProcessTimeout() {
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            return 0;
        } else {
            return super.unreturnedConnectionBackgroundProcessTimeout();
        }
    }

    @Override
    public int tablesIdentityOffset() {
        return 997;
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SeparateSchemas;
    }

    @Override
    public String sharedSequencesSchema() {
        return "public";
    }

    @Override
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention();
    }

}
