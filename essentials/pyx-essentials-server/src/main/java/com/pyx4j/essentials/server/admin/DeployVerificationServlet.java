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
 * Created on 2013-02-19
 * @author vlads
 */
package com.pyx4j.essentials.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ApplicationVersion;

/**
 * Used to verify version of application after automatic deployment by build server.
 * 
 * see pingURL in cargo-maven2-plugin configuration of server pom.xml
 * 
 */
@SuppressWarnings("serial")
public class DeployVerificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());

        String productBuild = request.getParameter("productBuild");
        if (!CommonsStringUtils.isEmpty(productBuild)) {
            if (ApplicationVersion.getProductBuild().equals(productBuild)) {
                //TODO test DB connection status
                replyOk(response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            String buildLabel = request.getParameter("buildLabel");
            if (CommonsStringUtils.isEmpty(buildLabel)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            if (ApplicationVersion.getBuildLabel().equals(buildLabel)) {
                //TODO test DB connection status
                replyOk(response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }

    }

    private void replyOk(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>System status</title></head>");
        out.println("<body>");
        out.println("<span>OK</span><p/>");
        out.println("<span>" + new Date().toString() + "</span><p/>");
        out.println("</body>");
        out.println("</html>");
    }
}
