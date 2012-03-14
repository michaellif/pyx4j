/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.ConfigurationHSQL;

public class VistaTestsDBConfigurationHSQLMemory extends ConfigurationHSQL {

    @Override
    public String dbName() {
        return "vista_tst";
    }

    @Override
    public boolean isMultitenant() {
        return true;
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
    public int maxPoolPreparedStatements() {
        return 2000;
    }

    @Override
    public int tablesItentityOffset() {
        return 1000;
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
}
