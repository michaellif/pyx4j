/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;
import java.util.Map;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.entity.rdb.cfg.ConfigurationPostgreSQLProperties;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.config.VistaDBNamingConvention;

public class VistaConfigurationPostgreSQLProperties extends ConfigurationPostgreSQLProperties {

    public VistaConfigurationPostgreSQLProperties(File configDirectory, Map<String, String> properties) {

        this.properties.connectionPoolConfiguration(ConnectionPoolType.BackgroundProcess).unreturnedConnectionTimeout = 4 * Consts.HOURS2SEC;
        this.properties.allowForeignKeyDeferrable = true;

        readProperties("db", properties);

        File dbCredentialsFile = new File(configDirectory, "db-credentials.properties");

        if (dbCredentialsFile.canRead()) {
            Credentials credentials = CredentialsFileStorage.getCredentials(dbCredentialsFile);
            this.properties.user = credentials.userName;
            this.properties.password = credentials.password;
        }
    }

    @Override
    public String connectionAutomaticTestTable() {
        return null;
    }

    @Override
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention();
    }
}
