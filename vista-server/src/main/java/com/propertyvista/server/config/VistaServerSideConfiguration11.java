/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;

public class VistaServerSideConfiguration11 extends VistaServerSideConfiguration {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationPostgreSQL() {
            @Override
            public String dbName() {
                return "vista11";
            }

            @Override
            public String userName() {
                return "vista11";
            }

            @Override
            public String password() {
                return "vista11";
            }

            @Override
            public int tablesItentityOffset() {
                return 0;
            }
        };
    }

    @Override
    public boolean openIdrequired() {
        return true;
    }

    @Override
    protected String getApplicationDeploymentProtocol() {
        return "https";
    }

    @Override
    public String getApplicationURLNamespace() {
        return "-11.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support11\" <support.www22@birchwoodsoftwaregroup.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("www22-");
    }

    @Override
    public String openIdProviderDomain() {
        return "11.birchwoodsoftwaregroup.com";
    }
}
