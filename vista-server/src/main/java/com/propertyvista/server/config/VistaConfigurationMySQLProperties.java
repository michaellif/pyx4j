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

import com.pyx4j.entity.rdb.cfg.ConfigurationPostgreSQLProperties;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;

import com.propertyvista.config.VistaDBNamingConvention;

public class VistaConfigurationMySQLProperties extends ConfigurationPostgreSQLProperties {

    public VistaConfigurationMySQLProperties(File configDirectory, Map<String, String> properties) {
        File dbCredentialsFile = new File(configDirectory, "db-credentials.properties");
        if (dbCredentialsFile.canRead()) {
            Credentials credentials = J2SEServiceConnector.getCredentials(dbCredentialsFile.getAbsolutePath());
            this.user = credentials.email;
            this.password = credentials.password;
        }
        readProperties("db", properties);
    }

    @Override
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention();
    }

}
