/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.ConfigurationMySQLProperties;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionModern;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;
import com.pyx4j.log4j.LoggerConfig;

public class VistaServerSideConfigurationCustom extends VistaServerSideConfiguration {

    private PropertiesConfiguration configProperties;

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.  All defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=Custom
        return this;
    }

    public File getConfigDirectory() {
        return new File(new File(LoggerConfig.getContainerHome(), "conf"), LoggerConfig.getContextName());
    }

    public PropertiesConfiguration getConfigProperties() {
        if (configProperties == null) {
            configProperties = new PropertiesConfiguration(null, PropertiesConfiguration.loadProperties(new File(getConfigDirectory(), "config.properties")));
        }
        return configProperties;
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return getConfigProperties().getBooleanValue("isDevelopmentBehavior", true);
    }

    @Override
    public boolean openIdrequired() {
        return getConfigProperties().getBooleanValue("openIdrequired", true);
    }

    @Override
    public String getApplicationURLDefault() {
        return getConfigProperties().getValue("ApplicationURLDefault");
    }

    @Override
    public String getApplicationURLNamespace() {
        return getConfigProperties().getValue("ApplicationURLNamespace");
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        ConfigurationMySQLProperties config = new ConfigurationMySQLProperties() {

            {
                readProperties("db", VistaServerSideConfigurationCustom.this.getConfigProperties().getProperties());

                File dbCredentialsFile = new File(getConfigDirectory(), "db-credentials.properties");
                if (dbCredentialsFile.canRead()) {
                    Credentials credentials = J2SEServiceConnector.getCredentials(dbCredentialsFile.getAbsolutePath());
                    this.user = credentials.email;
                    this.password = credentials.password;
                }

            }

            @Override
            public NamingConvention namingConvention() {
                return new NamingConventionModern();
            }

        };

        return config;
    }
}
