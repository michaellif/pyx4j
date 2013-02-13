/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.portal.rpc.DeploymentConsts;

/**
 * @see DeploymentConsts#portalInectionProxy
 */
@SuppressWarnings("serial")
public class CustomSkinProxyServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CustomSkinProxyServlet.class);

    private static MultiThreadedHttpConnectionManager connectionManager;

    protected HttpClient client;

    @Override
    public void init(ServletConfig servletConfig) {
        synchronized (CustomSkinProxyServlet.class) {
            if (connectionManager == null) {
                connectionManager = new MultiThreadedHttpConnectionManager();
            }
            client = new HttpClient(connectionManager);
        }
        client.getParams().setParameter("http.socket.timeout", new Integer(10 * 60 * 1000));

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            GetMethod method = new GetMethod(getServiceURL(request));
            method.setFollowRedirects(true);
            execute(method, request, response, null, 0);
        } catch (Throwable e) {
            log.error("Proxy GET error", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    protected String getServiceURL(HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String path = request.getPathInfo();
        int hostEnd = path.indexOf('/', 1);
        String host = path.substring(1, hostEnd);
        String servicePath = path.substring(hostEnd);
        String url = new URL("http", host, 80, servicePath).toExternalForm();
//        if (request.getQueryString() != null) {
//            url += "?" + request.getQueryString();
//        }
        return url;
    }

    private void execute(HttpMethodBase method, HttpServletRequest request, HttpServletResponse response, String debugContext, int id) throws IOException {
        log.debug("proxy request", request.getRequestURI(), method.getURI().toString());
        long startTime = System.currentTimeMillis();
        boolean sucsess = false;
        try {
            int status = client.executeMethod(method);
            log.debug("proxy service " + method.getName() + " response status and duration", status, TimeUtils.secSince(startTime));
            response.setStatus(status);
            OutputStream out = response.getOutputStream();
            try {
                IOUtils.copyStream(method.getResponseBodyAsStream(), out, 1024);
            } finally {
                IOUtils.closeQuietly(out);
            }
            sucsess = true;
        } finally {
            if (!sucsess) {
                log.warn("Exception in proxy " + method.getName() + " " + method.getURI().toString());
            }
            method.releaseConnection();
        }
    }
}
