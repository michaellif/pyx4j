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
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationDispatcherFilterInterfacesTest extends VistaApplicationDispatcherFilterTestBase {

    @Test
    public final void testInterfaces() throws IOException, ServletException {
        // ***********************************************************************************************
        //                                          INTERFACES
        //
        // URL Formats:
        // Production   ->  http://secure-interfaces.propertyvista.com/
        // Dev          ->  http://secure-interfaces-99.devpv.com/
        // Local        ->  http://secure-interfaces.local.devpv.com/  & http://interfaces.local.devpv.com/
        // ***********************************************************************************************

        testForward("https://secure-interfaces.propertyvista.com/", VistaApplication.interfaces);
        testForward("http://secure-interfaces-99.local.devpv.com/", VistaApplication.interfaces);
        testForward("http://secure-interfaces.local.devpv.com/", VistaApplication.interfaces);
        testForward("http://interfaces.local.devpv.com/", VistaApplication.interfaces);
    }

}
