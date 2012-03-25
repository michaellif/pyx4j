/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContext;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;

public class VistaServerSideConfigurationProdStarlight extends VistaServerSideConfiguration {

    @Override
    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        // Disable environment selection.  All defined in tomcatX.wrapper.conf -Dcom.pyx4j.appConfig=
        return this;
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".prod01.birchwoodsoftwaregroup.com/";
    }

    @Override
    public boolean openIdrequired() {
        return true;
    }

    @Override
    public boolean openDBReset() {
        return false;
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Starlight Support\" <support.www22@birchwoodsoftwaregroup.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("prod-star-");
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL() {

            @Override
            public String dbName() {
                return "vista_star";
            }

            @Override
            public String userName() {
                return "vista_star";
            }

            @Override
            public String password() {
                return "12aswedrqX@sd$fnjSz&";
            }
        };
    }

}
