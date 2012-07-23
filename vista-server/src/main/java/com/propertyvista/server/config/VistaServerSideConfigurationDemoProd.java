/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaServerSideConfigurationDemoProd extends VistaServerSideConfigurationDevCustom {

    @Override
    public boolean isVistaDemo() {
        return true;
    }

    @Override
    public boolean openDBReset() {
        return true;
    }

    @Override
    public String getDefaultBaseURLvistaCrm(String pmcDnsName) {
        return "https://demo.propertyvista.com/";
    }

    @Override
    public String getDefaultBaseURLresidentPortal(String pmcDnsName, boolean secure) {
        String protocol;
        if (secure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        return protocol + "demo.residentportalsite.com/";
    }

    @Override
    public String getDefaultBaseURLprospectPortal(String pmcDnsName) {
        return "https://demo.prospectportalsite.com/";
    }

    @Override
    public String getDefaultBaseURLvistaAdmin() {
        return "https://demo.birchwoodsoftwaregroup.com/" + LoggerConfig.getContextName() + "/" + DeploymentConsts.ADMIN_URL;
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista\" <no-reply@propertyvista.com>";
    }

    @Override
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return VistaSMTPMailServiceConfig.getGmailConfig("demo-prod-");
    }

}
