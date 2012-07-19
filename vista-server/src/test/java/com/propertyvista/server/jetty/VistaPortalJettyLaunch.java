/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jetty;

import com.pyx4j.jetty.JettyLaunch;

public class VistaPortalJettyLaunch extends JettyLaunch {

    public static void main(String[] args) throws Exception {
        JettyLaunch.launch(new VistaPortalJettyLaunch());
    }

    @Override
    public int getServerPort() {
        return 8888;
    }

    @Override
    public int getServerSslPort() {
        if (OSValidator.isWindows()) {
            return 443;
        } else if (OSValidator.isMac()) {
            return 0;
        } else if (OSValidator.isUnix()) {
            return 0;
        } else {
            return 0;
        }
    }

    @Override
    protected int getSessionMaxAge() {
        return 240 * 60;
    }

    @Override
    public String getContextPath() {
        return "/vista";
    }

    @Override
    public boolean isRunningInDeveloperEnviroment() {
        // return false to test wicket applications as in Tomcat
        return true;
    }
}
