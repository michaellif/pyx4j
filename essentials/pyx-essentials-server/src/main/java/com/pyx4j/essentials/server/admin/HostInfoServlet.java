/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 23, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.i18n.annotations.I18n;

@SuppressWarnings("serial")
public class HostInfoServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(HostInfoServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum HostInfoType {

        hostName,

        hostIPLocal,

        hostIPAll,

        javaVersion,

        containerVersion

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
            for (HostInfoType t : EnumSet.allOf(HostInfoType.class)) {
                buf.append("<tr><td><a href=\"");
                buf.append("?v=").append(t.name()).append("\">");
                buf.append("?v=").append(t.name());
                buf.append("</a></td><td>").append(t.toString());
                buf.append("</td><td>").append(getInfo(t, request));
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
                    buf.append(getInfo(HostInfoType.valueOf(param), request));
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

    private String getInfo(HostInfoType type, HttpServletRequest request) {
        switch (type) {
        case hostName:
            return getLocalHostName();
        case hostIPLocal:
            return getLocalHostIP();
        case hostIPAll:
            return getHostIP();
        case javaVersion:
            return System.getProperty("java.version");
        case containerVersion:
            String serverInfo = request.getServletContext().getServerInfo();
            if (serverInfo.startsWith("Apache Tomcat/")) {
                serverInfo = serverInfo.substring(serverInfo.indexOf("/") + 1);
            }
            return serverInfo;
        default:
            return "";
        }
    }

    //TODO use HostConfig
    public static String getLocalHostName() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostName().toLowerCase(Locale.ENGLISH);
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    //TODO use HostConfig
    public static String getLocalHostIP() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostAddress();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    private static String getHostIP() {
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
