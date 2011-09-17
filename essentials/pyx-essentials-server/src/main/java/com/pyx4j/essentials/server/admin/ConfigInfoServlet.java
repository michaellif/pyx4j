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
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.log4j.LoggerConfig;

@SuppressWarnings("serial")
public class ConfigInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-Date", System.currentTimeMillis());

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(buildConfigurationText());
        out.flush();
    }

    protected String buildConfigurationText() {
        StringBuilder b = new StringBuilder();
        b.append("ContextName:").append(LoggerConfig.getContextName()).append("\n");

        ServerSideConfiguration conf = ServerSideConfiguration.instance();
        b.append("ServerSideConfiguration  : ").append(conf.getClass().getName()).append("\n");
        b.append("DevelopmentBehavior      : ").append(conf.isDevelopmentBehavior()).append("\n");
        b.append("datastoreReadOnly        : ").append(conf.datastoreReadOnly()).append("\n");
        b.append("MainApplicationURL       : ").append(conf.getMainApplicationURL()).append("\n");
        b.append("SessionCookieName        : ").append(conf.getSessionCookieName()).append("\n");
        b.append("persistenceNamePrefix    : ").append(conf.persistenceNamePrefix()).append("\n");
        b.append("\nPersistenceConfiguration :\n  ").append(conf.getPersistenceConfiguration().toString().replaceAll("\n", "\n  ")).append("\n");

        b.append("\n\nSystem Properties:\n").append(ServerSideConfiguration.getSystemProperties());

        return b.toString();
    }
}
