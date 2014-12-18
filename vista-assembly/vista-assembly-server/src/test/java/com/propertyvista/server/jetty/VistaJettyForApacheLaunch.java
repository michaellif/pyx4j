/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 8, 2014
 * @author vlads
 */
package com.propertyvista.server.jetty;

import com.pyx4j.jetty.JettyLaunch;

import com.propertyvista.server.config.VistaServerSideConfigurationDev;

public class VistaJettyForApacheLaunch extends VistaPortalJettyLaunch {

    public static void main(String[] args) throws Exception {
        VistaServerSideConfigurationDev.devServerPort = 8889;
        JettyLaunch.launch(new VistaJettyForApacheLaunch());
    }

    @Override
    public int getServerSslPort() {
        return 0;
    }

    @Override
    protected boolean getRewriteRootToContext() {
        return false;
    }
}
