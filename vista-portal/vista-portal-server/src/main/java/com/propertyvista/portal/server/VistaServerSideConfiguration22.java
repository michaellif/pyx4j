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
package com.propertyvista.portal.server;

import com.propertyvista.config.VistaSMTPMailServiceConfig;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;

public class VistaServerSideConfiguration22 extends VistaServerSideConfiguration {

    @Override
    public String getMainApplicationURL() {
        return "http://www22.birchwoodsoftwaregroup.com/vista";
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
