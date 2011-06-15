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

import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionModern;

public class VistaConfigurationMySQL extends com.pyx4j.entity.rdb.cfg.ConfigurationMySQL {

    private static final String LOG_CONFIG = "&logger=com.mysql.jdbc.log.Slf4JLogger";

    @Override
    public String dbHost() {
        return "localhost";
    }

    private boolean showSql() {
        return false;
    }

    @Override
    public String connectionUrl() {
        return super.connectionUrl() + "?autoReconnect=true" + LOG_CONFIG + (showSql() ? "&autoGenerateTestcaseScript=true" : "");
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
    public boolean isMultitenant() {
        return true;
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
    public NamingConvention namingConvention() {
        return new NamingConventionModern();
    }

}
