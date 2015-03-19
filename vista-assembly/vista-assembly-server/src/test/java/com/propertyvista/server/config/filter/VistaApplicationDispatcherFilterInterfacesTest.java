/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 14, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Ignore;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.base.VistaApplicationDispatcherFilterTestBase;

public class VistaApplicationDispatcherFilterInterfacesTest extends VistaApplicationDispatcherFilterTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log.info("VistaApplicationDispatcherFilterInterfacesTest initialized");
    }

    @Ignore
    public final void testInterfaces() throws IOException, ServletException {
        // ***********************************************************************************************
        //                                          INTERFACES
        //
        // URL Formats:
        // Production   ->  http://secure-interfaces.propertyvista.com/
        // Dev          ->  http://secure-interfaces-99.devpv.com/
        // Local        ->  http://secure-interfaces.local.devpv.com/  & http://interfaces.local.devpv.com/
        // ***********************************************************************************************

        testForward("https://secure-interfaces.propertyvista.com/interfaces", VistaApplication.interfaces);
        testForward("http://secure-interfaces-99.local.devpv.com/interfaces", VistaApplication.interfaces);
        testForward("http://secure-interfaces.local.devpv.com/interfaces", VistaApplication.interfaces);
        testForward("http://interfaces.local.devpv.com/interfaces", VistaApplication.interfaces);
    }

}
