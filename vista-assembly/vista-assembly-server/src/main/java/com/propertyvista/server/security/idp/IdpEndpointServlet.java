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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class IdpEndpointServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(IdpEndpointServlet.class);

    private OpenIDProviderServer server;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        server = new OpenIDProviderServer();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            server.processRequest(req, resp);
        } catch (OpenIDException e) {
            log.error("Error", e);
            throw new Error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            server.processRequest(req, resp);
        } catch (OpenIDException e) {
            log.error("Error", e);
            throw new Error(e);
        }
    }

}
