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
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.server.contexts.Context;

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
        b.append("Application Version      : ").append(ApplicationVersion.getProductVersion()).append("\n");
        b.append("Application Build        : ").append(ApplicationVersion.getBuildLabel()).append("\n");
        b.append("Application BuildTime    : ").append(ApplicationVersion.getBuildDate()).append("\n");
        b.append("Application ScmRevision  : ").append(ApplicationVersion.getScmRevision()).append("\n");
        b.append("\n");

        b.append("Pyx4j Version            : ").append(ApplicationVersion.getPyxBuildLabel()).append("\n");
        b.append("Pyx4j BuildTime          : ").append(ApplicationVersion.getPyxBuildDate()).append("\n");
        b.append("Pyx4j ScmRevision        : ").append(ApplicationVersion.getPyxScmRevision()).append("\n");
        b.append("\n");

        b.append("ContextName              : ").append(LoggerConfig.getContextName()).append("\n");
        try {
            b.append("ServerInfo               : ").append(Context.getRequest().getServletContext().getServerInfo()).append("\n");
        } catch (NoSuchMethodError ignoreOldTomcat) {
        }
        b.append("System Date              : ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(SystemDateManager.getDate())).append("\n");
        b.append("System Uptime            : ").append(systemUptime()).append("\n");
        b.append("Application Uptime       : ").append(applicationUptime()).append("\n");
        b.append("\n");

        ServerSideConfiguration conf = ServerSideConfiguration.instance();
        b.append("ServerSideConfiguration  : ").append(conf.getClass().getName()).append("\n");
        b.append("DevelopmentBehavior      : ").append(conf.isDevelopmentBehavior()).append("\n");
        b.append("datastoreReadOnly        : ").append(conf.datastoreReadOnly()).append("\n");
        b.append("MainApplicationURL       : ").append(conf.getMainApplicationURL()).append("\n");
        b.append("SessionCookieName        : ").append(conf.getSessionCookieName()).append("\n");
        b.append("persistenceNamePrefix    : ").append(conf.persistenceNamePrefix()).append("\n");
        b.append("\n");

        b.append("PersistenceConfiguration :\n  ").append(conf.getPersistenceConfiguration().toString().replaceAll("\n", "\n  ")).append("\n");

        b.append("MailService              :\n  ").append(conf.getMailServiceConfigConfiguration().toString().replaceAll("\n", "\n  ")).append("\n");

        b.append(applicationConfigurationText());
        b.append("\n");
        b.append("\n");

        b.append("System:\n");
        b.append("Free memory              : ").append(Runtime.getRuntime().freeMemory() / (1024 * 1024)).append(" MB\n");
        b.append("Total memory             : ").append(Runtime.getRuntime().totalMemory() / (1024 * 1024)).append(" MB\n");
        b.append("Max memory               : ").append(Runtime.getRuntime().maxMemory() / (1024 * 1024)).append(" MB\n");
        b.append("\n");

        b.append("System Properties:\n").append(ServerSideConfiguration.getSystemProperties());

        return b.toString();
    }

    private String applicationUptime() {
        return TimeUtils.durationFormatSeconds((int) (System.currentTimeMillis() - ServerSideConfiguration.getStartTime()) / Consts.SEC2MSEC) + ", since: "
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(new Date(ServerSideConfiguration.getStartTime()));
    }

    private String systemUptime() {
        try {
            long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
            return TimeUtils.durationFormatSeconds((int) jvmUpTime / Consts.SEC2MSEC) + ", since: "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(new Date(jvmStartTime));
        } catch (Throwable e) {
            return "n/a " + e.getMessage();
        }
    }

    protected String applicationConfigurationText() {
        return "";
    }
}
