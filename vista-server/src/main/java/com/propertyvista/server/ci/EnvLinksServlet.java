/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.system.WorldDateManager;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;

@SuppressWarnings("serial")
public class EnvLinksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String body = IOUtils.getTextResource("index.html", EnvLinksServlet.class);

        String envName = "";
        Integer enviromentId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).enviromentId();
        if (enviromentId != null) {
            envName = "env-" + enviromentId;
        } else {
            envName = VistaDeployment.getSystemIdentification().name();
        }
        body = body.replace("${title}", envName);
        body = body.replace("${name}", "Environment: " + envName);
        body = body.replace("${version}", "version: " + ApplicationVersion.getProductVersion() + " " + ApplicationVersion.getBuildLabel() + " "
                + ApplicationVersion.getBuildTime());

        body = body.replace("${systemDate}", buildSystemDate());

        body = body.replace("${text}", new EnvLinksBuilder().toString());

        out.print(body);
        out.flush();
    }

    private String buildSystemDate() {
        Date systemTime = new Date();
        Date realTime = WorldDateManager.getWorldTime();

        StringBuilder b = new StringBuilder();
        b.append("System time: ");
        b.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(systemTime));

        if (Math.abs(systemTime.getTime() - realTime.getTime()) > 1 * Consts.MIN2MSEC) {
            b.append("; World time: ");
            b.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(realTime));

            b.append("; System time offset: ").append(TimeUtils.durationFormat(systemTime.getTime() - realTime.getTime()));
        }

        return b.toString();
    }
}
