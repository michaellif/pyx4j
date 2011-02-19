/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.access.openId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.server.contexts.Lifecycle;

@SuppressWarnings("serial")
public class OpenIdServlet extends HttpServlet {

    public static String MAPPING = "/o/openid";

    static String DOMAIN = "birchwoodsoftwaregroup.com";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpenIdResponse openIdResponse = OpenId.readResponse(request, DOMAIN);

        if (openIdResponse == null) {
            createResponsePage(response, true, "Login via Google Apps", OpenId.getDestinationUrl(OpenIdServlet.DOMAIN));
        } else {
            Set<Behavior> behaviours = new HashSet<Behavior>();
            behaviours.add(CoreBehavior.USER);
            Lifecycle.beginSession(null, behaviours);
            createResponsePage(response, false, "Login successful Continue to application", "/");
        }
    }

    static void createResponsePage(HttpServletResponse response, boolean signIn, String message, String location) throws IOException {
        if (signIn) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        out.println("<title>Access " + (signIn ? " Restricted" : " Granted") + " </title></head><body>");
        out.println("<a id=\"" + (signIn ? "googleSignIn" : "continue") + "\" href=\"" + location + "\">" + message + "</A>");
        out.print("</body></html>");
        out.flush();
    }

}
