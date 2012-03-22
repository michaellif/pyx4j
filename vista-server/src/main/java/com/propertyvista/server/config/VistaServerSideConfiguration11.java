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
import com.pyx4j.server.contexts.NamespaceManager;

public class VistaServerSideConfiguration11 extends VistaServerSideConfiguration {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationMySQL() {
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
        };
    }

    @Override
    public String getApplicationURLDefault() {
        return "http://www11.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".11.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getMainApplicationURL() {
        return "https://" + NamespaceManager.getNamespace() + getApplicationURLNamespace();
    }

    @Override
    public String getDefaultBaseURLresidentPortal(boolean secure) {
        String url = super.getDefaultBaseURLresidentPortal(secure);
        if (secure) {
            return url;
        } else {
            return url.replace("https://", "http://");
        }
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support22\" <support.www22@birchwoodsoftwaregroup.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("www22-");
    }
}
