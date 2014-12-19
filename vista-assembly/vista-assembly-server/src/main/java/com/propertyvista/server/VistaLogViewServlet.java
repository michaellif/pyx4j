/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 2, 2014
 * @author vlads
 */
package com.propertyvista.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaOperationsBehavior;

public class VistaLogViewServlet extends com.pyx4j.essentials.server.download.LogViewServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void authentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!ApplicationMode.isDevelopment() && !SecurityController.check(VistaOperationsBehavior.SystemAdmin)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ServletException("Request requires authentication.");
        }
    }

}
