/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.idp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

@SuppressWarnings("serial")
public class IdpXrdsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/xrds+xml");
        PrintWriter out = response.getWriter();

        String body = IOUtils.getTextResource("xrds.xml", IdpXrdsServlet.class);

        body = body.replace("${endpoint_uri}", getOPEndpointUrl());
        response.setContentLength(body.length());
        out.print(body);
        out.flush();
    }

    static String getOPEndpointUrl() {
        String domain = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdProviderDomain();
        String endpoint = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdDomainIdentifier(domain);
        return endpoint.replace("/idp", "/endpoint");
    }
}
