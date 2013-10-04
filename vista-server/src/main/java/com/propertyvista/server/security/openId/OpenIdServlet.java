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
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.DevSession;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.shared.config.VistaDemo;

@SuppressWarnings("serial")
public class OpenIdServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(OpenIdServlet.class);

    private static final I18n i18n = I18n.get(OpenIdServlet.class);

    public static final String MAPPING = "/o/openid";

    public static String USER_EMAIL_ATTRIBUTE = DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String domain = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).openIdDomain();
        OpenIdResponse openIdResponse = OpenId.readResponse(request, domain);
        if (openIdResponse == null) {
            log.debug("Can't find authentication information in OpenId URL");
            createResponsePage(response, true, OpenId.getDestinationUrl(domain, ServerSideConfiguration.instance().getMainApplicationURL()));
        } else {
            log.info("openIdResponse.email [{}]", openIdResponse.email);
            DevSession devSession = DevSession.getSession();
            String receivingURL = (String) devSession.getAttribute(OpenIdFilter.REQUESTED_URL_ATTRIBUTE);
            devSession.setAttribute(OpenIdFilter.ACCESS_GRANTED_ATTRIBUTE, Boolean.TRUE);
            if (receivingURL == null) {
                receivingURL = ServerSideConfiguration.instance().getMainApplicationURL();
            } else {
                devSession.removeAttribute(OpenIdFilter.REQUESTED_URL_ATTRIBUTE);
            }
            if (openIdResponse.email != null) {
                openIdResponse.email = openIdResponse.email.toLowerCase(Locale.ENGLISH);
                devSession.setAttribute(OpenIdServlet.USER_EMAIL_ATTRIBUTE, openIdResponse.email);

                if (openIdResponse.email.endsWith("propertyvista.com") && !openIdResponse.email.contains("demo")) {
                    VistaAuthenticationServicesImpl.setVistaEmployeeCookie();
                } else if (openIdResponse.email.endsWith("pyx4j.com")) {
                    VistaAuthenticationServicesImpl.setVistaEmployeeCookie();
                }

            }
            createResponsePage(response, false, receivingURL);
        }
    }

    static void createResponsePage(HttpServletResponse response, boolean signIn, String location) throws IOException {
        String message;
        if (signIn) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            message = VistaDemo.isDemo() ? i18n.tr("Login using your PropertyVista account") : i18n.tr("Login via Google Apps");
        } else {
            message = i18n.tr("Login successful Continue to application");
        }
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String body = IOUtils.getTextResource("body.html", OpenIdServlet.class);
        body = body.replace("${title}", "Access " + (signIn ? " Restricted" : " Granted"));

        String meta = "";
        if ((!signIn) && (!VistaDemo.isDemo() && isOurDeveloper())) {
            //meta = "<meta http-equiv=\"refresh\" content=\"2;url=\"" + location + "\">";
        }

        body = body.replace("${head}", meta);

        body = body.replace("${name}", VistaDemo.isDemo() ? "PropertyVista Demo " : "" + "Access " + (signIn ? " Restricted" : " Granted"));

        body = body.replace("${text}", "<a id=\"" + (signIn ? "googleSignIn" : "continue") + "\" tabindex=\"1\" autofocus=\"autofocus\" href=\"" + location
                + "\">" + message + "</a>");

        String rc_message;
        if (signIn) {
            rc_message = IOUtils.getTextResource("signin-wellcome.html", OpenIdServlet.class);
        } else {
            rc_message = IOUtils.getTextResource("login-successful.html", OpenIdServlet.class);
        }
        body = body.replace("${rc_message}", rc_message);

        out.print(body);
        out.flush();
    }

    private static boolean isOurDeveloper() {
        Object email = DevSession.getSession().getAttribute(OpenIdServlet.USER_EMAIL_ATTRIBUTE);
        return !"tester@propertyvista.com".equals(email);
    }
}
