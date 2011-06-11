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
package com.propertyvista.server.security.openId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.server.common.security.DevelopmentSecurity;

@SuppressWarnings("serial")
public class OpenIdServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(OpenIdServlet.class);

    public static String MAPPING = "/o/openid";

    static String DOMAIN = "birchwoodsoftwaregroup.com";

    public static String USER_EMAIL_ATTRIBUTE = DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpenIdResponse openIdResponse = OpenId.readResponse(request, DOMAIN);
        if (openIdResponse == null) {
            log.debug("Can't find authentication information in OpenId URL");
            createResponsePage(response, true, "Login via Google Apps", OpenId.getDestinationUrl(OpenIdServlet.DOMAIN));
        } else {
            log.info("openIdResponse.email [{}]", openIdResponse.email);
            String receivingURL = (String) Context.getVisit().getAttribute(OpenIdFilter.REQUESTED_URL_ATTRIBUTE);
            Set<Behavior> behaviours = new HashSet<Behavior>();
            behaviours.add(CoreBehavior.USER);
            Lifecycle.beginSession(null, behaviours);
            if (receivingURL == null) {
                receivingURL = ServerSideConfiguration.instance().getMainApplicationURL();
            } else {
                Context.getVisit().removeAttribute(OpenIdFilter.REQUESTED_URL_ATTRIBUTE);
            }
            if (openIdResponse.email != null) {
                Context.getVisit().setAttribute(OpenIdServlet.USER_EMAIL_ATTRIBUTE, openIdResponse.email.toLowerCase(Locale.ENGLISH));
            }
            createResponsePage(response, false, "Login successful Continue to application", receivingURL);
        }
    }

    static void createResponsePage(HttpServletResponse response, boolean signIn, String message, String location) throws IOException {
        if (signIn) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        out.println("<title>Access " + (signIn ? " Restricted" : " Granted") + " </title></head><body>");
        out.println("<a id=\"" + (signIn ? "googleSignIn" : "continue") + "\" href=\"" + location + "\">" + message + "</A>");
        out.print("</body></html>");
        out.flush();
    }

}
