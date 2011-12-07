/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.openId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.essentials.server.dev.DevSession;

@SuppressWarnings("serial")
public class VistaDevSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());

        DevSession devSession = DevSession.getSession();
        devSession.setAttribute(OpenIdFilter.ACCESS_GRANTED_ATTRIBUTE, Boolean.TRUE);
        devSession.setAttribute(OpenIdServlet.USER_EMAIL_ATTRIBUTE, "tester-a@" + OpenIdServlet.DOMAIN);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Dev System</title></head>");
        out.println("<body>");
        out.println("<span>OK</span><p/>");
        out.println("<span>" + new Date().toString() + "</span><p/>");
        out.println("</body>");
        out.println("</html>");
    }
}
