/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-01-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

@SuppressWarnings("serial")
public class ServletContainerLoginServlet extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(ServletContainerLoginServlet.class);

    public static final String ROLE_SESSION_ATTRIBUTE = "com.pyx4j.keep." + "userRole";

    public static final String ROLE_ADMIN = "admin";

    public static final String ROLE_USER = "user";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getRequestURI().endsWith("login")) {
            if (Context.getRequest().isUserInRole(ROLE_ADMIN)) {
                createContainerSession(CoreBehavior.DEVELOPER);
                log.info("login as admin");
            } else if (Context.getRequest().isUserInRole(ROLE_USER)) {
                createContainerSession(CoreBehavior.USER);
                log.info("login as user");
            } else {
                log.info("login as other");
            }
            response.sendRedirect(request.getParameter("return"));
        } else if (request.getRequestURI().endsWith("logout")) {
            response.sendRedirect(request.getParameter("return"));
        }
    }

    private void createContainerSession(Behavior behavior) {
        HttpSession newSession = Context.getRequest().getSession(true);
        newSession.setAttribute(ROLE_SESSION_ATTRIBUTE, behavior);
        if (SecurityController.checkBehavior(behavior)) {
            return;
        }
        if (Context.getVisit() != null) {
            Set<Behavior> behaviours = new HashSet<Behavior>();
            behaviours.addAll(SecurityController.getBehaviors());
            behaviours.add(behavior);
            Lifecycle.beginSession(Context.getVisit().getUserVisit(), behaviours);
        }
    }
}
