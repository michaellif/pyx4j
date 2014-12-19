/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2014
 * @author vlads
 */
package com.propertyvista.server.config;

import com.pyx4j.entity.rdb.cfg.ConfigurationHSQL;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

import com.propertyvista.config.VistaDBNamingConvention;

class VistaDBConfigurationHSQLMemory extends ConfigurationHSQL {

    @Override
    public String dbName() {
        return "vista";
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SharedSchema;
    }

    @Override
    public int tablesIdentityOffset() {
        return 997;
    }

    @Override
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention(true);
    }
}
