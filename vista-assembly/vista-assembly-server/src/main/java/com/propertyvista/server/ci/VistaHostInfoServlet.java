/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2015
 * @author vlads
 */
package com.propertyvista.server.ci;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.config.SystemConfig;

@SuppressWarnings("serial")
public class VistaHostInfoServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(VistaHostInfoServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
        response.setDateHeader("X-StatusDate", System.currentTimeMillis());

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(buildHostInfoText(request));
        out.flush();
    }

    private String buildHostInfoText(HttpServletRequest request) {
        StringBuilder buf = new StringBuilder();

        buf.append(SystemConfig.getLocalHostName()).append(" ");

        buf.append(getNetworkInfo());

        return buf.toString();
    }

    private static String getNetworkInfo() {
        StringBuilder b = new StringBuilder();
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface itf = en.nextElement();
                if (isUp(itf) && !itf.isLoopback() && !itf.isVirtual()) {
                    List<InterfaceAddress> adrs = itf.getInterfaceAddresses();
                    if (!adrs.isEmpty()) {
                        for (InterfaceAddress a : adrs) {
                            if (b.length() > 0) {
                                b.append(" ");
                            }
                            b.append(a.getAddress().getHostAddress());
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Unable to read NetworkInfo ", e);
            b.append(e);
        }
        return b.toString();
    }

    private static boolean isUp(NetworkInterface itf) {
        try {
            return itf.isUp();
        } catch (Throwable e) {
            return false;
        }
    }
}
