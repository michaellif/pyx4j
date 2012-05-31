/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.entity.rdb.cfg.ConfigurationPostgreSQLProperties;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;

/**
 * See the files https://svn.pyx4j.com/svn-configs/trunk/vista/testenv/apps/catalina.base/tomcatA/conf/vista33
 * 
 */
public class VistaServerSideConfiguration33 extends VistaServerSideConfigurationCustom {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        ConfigurationPostgreSQLProperties config = new ConfigurationPostgreSQLProperties() {

            {
                readProperties("db", VistaServerSideConfiguration33.this.getConfigProperties().getProperties());

                File dbCredentialsFile = new File(getConfigDirectory(), "db-credentials.properties");
                if (dbCredentialsFile.canRead()) {
                    Credentials credentials = J2SEServiceConnector.getCredentials(dbCredentialsFile.getAbsolutePath());
                    this.user = credentials.email;
                    this.password = credentials.password;
                }

            }

            @Override
            public NamingConvention namingConvention() {
                return new NamingConventionOracle(63, null, false, false, '$');
            }

        };

        return config;
    }

    @Override
    protected String getApplicationDeploymentProtocol() {
        return "https";
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".33.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support33\" <support.www33@birchwoodsoftwaregroup.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("www33-");
    }
}
