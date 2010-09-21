/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-09-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.pyx4j.server.contexts.Lifecycle;

@SuppressWarnings("serial")
public class GoogleAccountsLoginHttpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getRequestURI().endsWith(getLoginPath())) {
            response.sendRedirect(createLoginURL(request));
        } else if (request.getRequestURI().endsWith(getLoginCompletedPath())) {
            doLoginCompleted(request, response);
        } else if (request.getRequestURI().endsWith(getLogoutPath())) {
            response.sendRedirect(createLogoutURL(request));
        } else if (request.getRequestURI().endsWith(getLogoutCompletedPath())) {
            doLogoutCompleted(request, response);
        }
    }

    protected String createLoginURL(HttpServletRequest request) {
        StringBuilder returnURL = new StringBuilder();
        returnURL.append('/');
        returnURL.append(getLoginCompletedPath());
        return UserServiceFactory.getUserService().createLoginURL(returnURL.toString());
    }

    protected String createLogoutURL(HttpServletRequest request) {
        StringBuilder returnURL = new StringBuilder();
        returnURL.append('/');
        returnURL.append(getLogoutCompletedPath());
        return UserServiceFactory.getUserService().createLogoutURL(returnURL.toString());
    }

    protected String getLoginPath() {
        return "login";
    }

    protected String getLoginCompletedPath() {
        return "loginCompleted";
    }

    protected String getLogoutPath() {
        return "logout";
    }

    protected String getLogoutCompletedPath() {
        return "logoutCompleted";
    }

    /**
     * Apps should actually identify user in their DB and create session.
     */
    protected void onLoginCompleted() {

    }

    protected void doLoginCompleted(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // assume loginCompleted
        onLoginCompleted();
        response.setContentType("text/html");
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        PrintWriter out = response.getWriter();
        out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        out.println("<meta http-equiv=\"PRAGMA\" content=\"NO-CACHE\">");
        out.println("<meta http-equiv=\"CACHE-CONTROL\" content=\"NO-CACHE\">");
        out.println("<title>Login Completed</title></head><body>");
        out.println("<script type=\"text/javascript\">");
        out.println("window.opener.popupWindowSelectionMade('loginCompleated');window.close();");
        out.println("</script>");
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            out.print("<h1>You have successfully signed in!</h1>");
            out.print("<p>User Id: " + userService.getCurrentUser().getUserId() + "</p>");
            out.print("<p>Name: " + userService.getCurrentUser().getNickname() + "</p>");
            out.print("<p>Email: " + userService.getCurrentUser().getEmail() + "</p>");
        }
        out.print("</body></html>");
        out.flush();
    }

    protected void onLogoutCompleted() {
        Lifecycle.endSession();
    }

    protected void doLogoutCompleted(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        onLogoutCompleted();
        response.setContentType("text/html");
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        PrintWriter out = response.getWriter();
        out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        out.println("<meta http-equiv=\"PRAGMA\" content=\"NO-CACHE\">");
        out.println("<meta http-equiv=\"CACHE-CONTROL\" content=\"NO-CACHE\">");
        out.println("<title>Logout Completed</title></head><body>");
        out.println("<script type=\"text/javascript\">");
        out.println("window.opener.popupWindowSelectionMade('logoutCompleated');window.close();");
        out.println("</script>");
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            out.print("<h1>You are still signed in!</h1>");
            out.print("<p>User Id: " + userService.getCurrentUser().getUserId() + "</p>");
            out.print("<p>Name: " + userService.getCurrentUser().getNickname() + "</p>");
            out.print("<p>Email: " + userService.getCurrentUser().getEmail() + "</p>");
        } else {
            out.print("<h1>You have successfully signed out!</h1>");
        }
        out.print("</body></html>");
        out.flush();
    }
}
