/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-08-31
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.i18n.annotations.I18n;

@SuppressWarnings("serial")
public class ApplicationVersionServlet extends HttpServlet {

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum ApplicationVersionType {

        productVersion,

        buildLabel,

        buildDate,

        scmRevision,

        buildLabelPyx,

        buildDatePyx,

        scmRevisionPyx
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-Date", System.currentTimeMillis());

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(buildConfigurationText(request));
        out.flush();
    }

    private String buildConfigurationText(HttpServletRequest request) {
        StringBuilder buf = new StringBuilder();
        String[] params = request.getParameterValues("v");
        if (params == null) {
            buf.append("Usage:<br/><table>");
            for (ApplicationVersionType t : EnumSet.allOf(ApplicationVersionType.class)) {
                buf.append("<tr><td><a href=\"");
                buf.append("?v=").append(t.name()).append("\">");
                buf.append("?v=").append(t.name());
                buf.append("</a></td><td>").append(t.toString());
                buf.append("</td><td>").append(getVersionInfo(t));
                buf.append("</td></tr>");
            }
            buf.append("</table>");
        } else {

            String format = request.getParameter("html");
            if (format != null) {
                buf.append("<html><body>");
                buf.append("<pre style=\"white-space: nowrap\">");
            }

            for (String param : params) {
                try {
                    buf.append(getVersionInfo(ApplicationVersionType.valueOf(param)));
                } catch (IllegalArgumentException e) {
                    buf.append(param);
                }
            }

            if (format != null) {
                buf.append("</pre></body></html>");
            }
        }
        return buf.toString();

    }

    private String getVersionInfo(ApplicationVersionType type) {
        switch (type) {
        case productVersion:
            return ApplicationVersion.getProductVersion();
        case buildDate:
            return ApplicationVersion.getBuildTime();
        case buildLabel:
            return ApplicationVersion.getBuildLabel();
        case scmRevision:
            return ApplicationVersion.getScmRevision();
        case buildLabelPyx:
            return ApplicationVersion.getPyxBuildLabel();
        case buildDatePyx:

        case scmRevisionPyx:
            return ApplicationVersion.getPyxScmRevision();
        default:
            return "";
        }
    }

}
