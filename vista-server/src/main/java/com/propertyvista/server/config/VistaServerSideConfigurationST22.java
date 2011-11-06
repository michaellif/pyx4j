/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.IPersistenceConfiguration;

import com.propertyvista.config.VistaSMTPMailServiceConfig;

public class VistaServerSideConfigurationST22 extends VistaServerSideConfiguration {

    @Override
    public String getApplicationURLDefault() {
        return "http://st34289.pyx4j.com/";
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".st22.birchwoodsoftwaregroup.com/";
    }

    @Override
    public boolean isDevelopmentBehavior() {
        return true;
    }

    @Override
    public boolean openIdrequired() {
        return true;
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support22\" <support.www22@birchwoodsoftwaregroup.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("www22-");
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        boolean useStarClone = true;
        if (!useStarClone) {
            return new VistaConfigurationMySQL() {

                @Override
                public String dbName() {
                    return "vista_st22";
                }

                @Override
                public String userName() {
                    return "vista-st22";
                }

                @Override
                public String password() {
                    return "vista-st22";
                }
            };
        } else {
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
                    return "vista_star";
                }
            };
        }
    }

}
