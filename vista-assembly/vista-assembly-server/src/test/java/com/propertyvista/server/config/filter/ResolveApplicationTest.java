/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2015
 * @author vlads
 */
package com.propertyvista.server.config.filter;

import com.propertyvista.domain.security.common.VistaApplication;

public class ResolveApplicationTest {

    public void testDev() {
        //setConfig(new NamesConfigNumberedEnv(".local.devpv.com"));
        assertApp("http://start.local.devpv.com", VistaApplication.onboarding);
        assertApp("http://vista-crm.local.devpv.com", VistaApplication.crm);
    }

    public void testEnv11() {
        //setConfig(new NamesConfigNumberedEnv("-11.devpv.com"));
        assertApp("http://start-11.devpv.com", VistaApplication.onboarding);
        assertApp("https://vista-crm-11.devpv.com/", VistaApplication.crm);
    }

    public void testProd() {
        //setConfig(new NamesConfigProd());
        assertApp("https://interfaces.propertyvista.com", VistaApplication.interfaces);
        assertApp("https://vista.propertyvista.com/interfaces", VistaApplication.crm);

        assertApp("https://operations.propertyvista.com", VistaApplication.operations);

        assertApp("https://env.propertyvista.com", VistaApplication.env);
        assertApp("https://env.my-community.co", VistaApplication.env);
        assertApp("https://env-staging.propertyvista.net", VistaApplication.env);
        assertApp("https://env.propertyvista.net", null);

        assertApp("https://static.propertyvista.com", VistaApplication.staticContext);
        assertApp("https://static.my-community.co", VistaApplication.staticContext);
        assertApp("https://static-staging.propertyvista.net", VistaApplication.staticContext);
        assertApp("https://static.propertyvista.net", null);

        assertApp("https://vista.propertyvista.com", VistaApplication.crm);
        assertApp("https://vista-crm-staging.propertyvista.net", VistaApplication.crm);
    }

    private void assertApp(String string, VistaApplication onboarding) {
        // TODO Auto-generated method stub

    }
}
