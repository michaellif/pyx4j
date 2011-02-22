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
package com.propertyvista.portal.server;

import com.propertyvista.config.VistaSMTPMailServiceConfig;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;

public class VistaServerSideConfiguration33 extends VistaServerSideConfiguration {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL() {
            @Override
            public String dbName() {
                return "vista_stable";
            }

            @Override
            public String userName() {
                return "vista_stable";
            }

            @Override
            public String password() {
                return "vista_stable";
            }
        };
    }

    @Override
    public String getMainApplicationURL() {
        return "http://www33.birchwoodsoftwaregroup.com/";
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
