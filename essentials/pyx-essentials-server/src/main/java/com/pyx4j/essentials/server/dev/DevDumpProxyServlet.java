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
 * Created on 2013-02-26
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.log4j.LoggerConfig;

@SuppressWarnings("serial")
public class DevDumpProxyServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DevDumpProxyServlet.class);

    private static MultiThreadedHttpConnectionManager connectionManager;

    protected HttpClient client;

    private int requestCount = 0;

    @Override
    public void init(ServletConfig servletConfig) {
        synchronized (DevDumpProxyServlet.class) {
            if (connectionManager == null) {
                connectionManager = new MultiThreadedHttpConnectionManager();
            }
            client = new HttpClient(connectionManager);
        }
        client.getParams().setParameter("http.socket.timeout", new Integer(10 * 60 * 1000));

    }

    @Override
    public void destroy() {
        if (connectionManager != null) {
            connectionManager.shutdown();
            connectionManager = null;
        }
        client = null;
        super.destroy();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            GetMethod method = new GetMethod(getServiceURL(request));
            setRequestHeaders(request, method);
            int id = ++requestCount;
            execute(method, request, response, id);
        } catch (Throwable e) {
            log.error("Proxy GET error", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            PostMethod method = new PostMethod(getServiceURL(request));
            setRequestHeaders(request, method);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int id = ++requestCount;
            copyStream(request.getInputStream(), out, "request", id);
            method.setRequestEntity(new ByteArrayRequestEntity(out.toByteArray()));
            method.setRequestHeader(new Header("Content-Length", String.valueOf(out.toByteArray().length)));
            execute(method, request, response, id);
        } catch (Throwable e) {
            log.error("Proxy GET error", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    protected String getServiceURL(HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String path = request.getPathInfo();
        String protocol = "http";
        int port = 80;

        int hostEnd = path.indexOf('/', 1);
        String host = path.substring(1, hostEnd);
        if (host.equals("http") || host.equals("https")) {
            protocol = host;
            path = path.substring(hostEnd);
            hostEnd = path.indexOf('/', 1);
            host = path.substring(1, hostEnd);
        }
        if (protocol.equals("https")) {
            port = 80;
        }
        ensureWhitelisted(host);
        String servicePath = path.substring(hostEnd);
        String url = new URL(protocol, host, port, servicePath).toExternalForm();
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }
        return url;
    }

    protected String getServiceHost(HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String path = request.getPathInfo();
        int hostEnd = path.indexOf('/', 1);
        String host = path.substring(1, hostEnd);
        ensureWhitelisted(host);
        return host;
    }

    protected void ensureWhitelisted(String host) {
//        log.warn("Unknown host: {}", host);
//        throw new RuntimeException("Unknown host: " + host);
    }

    private void setRequestHeaders(HttpServletRequest request, HttpMethodBase method) throws MalformedURLException, UnsupportedEncodingException {
        method.getParams().setVersion(HttpVersion.HTTP_1_0);
        method.setRequestHeader(new Header("Host", getServiceHost(request)));
        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            if (("host".equalsIgnoreCase(name))) {
                continue;
            }
            Enumeration<?> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = (String) values.nextElement();
                method.setRequestHeader(new Header(name, value));
            }
        }
    }

    private void execute(HttpMethodBase method, HttpServletRequest request, HttpServletResponse response, int id) throws IOException {
        log.debug("proxy request {} {}", request.getRequestURI(), method.getURI().toString());
        method.setFollowRedirects(false);
        DeferredExecuteHttpMethod deferredExecute = null;
        boolean sucsess = false;
        try {
            deferredExecute = new DeferredExecuteHttpMethod(client, method, this, id);
            synchronized (deferredExecute.getNotify()) {
                deferredExecute.start();
                try {
                    deferredExecute.getNotify().wait(60 * 1000);
                } catch (InterruptedException e) {
                    throw new InterruptedIOException(e.toString());
                }
            }
            if (deferredExecute.isCompleated()) {
                deferredExecute.copyResponce(response);
            } else {
                //response.sendRedirect(DeferredProxyServlet.createRedirect(request, deferredExecute));
                log.warn("redirect not supported");
            }
            sucsess = true;
        } finally {
            if (!sucsess) {
                log.warn("Exception in proxy {} {}", method.getName(), method.getURI().toString());
            }
            if ((!sucsess) && (deferredExecute != null)) {
                deferredExecute.clenup();
            }
        }
    }

    protected FileOutputStream createDebugOutputStream(String debugContext, String name, int id) throws IOException {
        if ((debugContext == null) || (id == 0)) {
            return null;
        }
        File dir;
        if (LoggerConfig.getContextName() != null) {
            dir = new File("logs", LoggerConfig.getContextName());
        } else {
            dir = new File("logs");
        }
        dir = new File(dir, debugContext);
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            log.error("debug write", e);
            return null;
        }
        NumberFormat nf = new DecimalFormat("0000");
        StringBuffer fname = new StringBuffer(nf.format(id));
        fname.append('-').append(name);
        fname.append(".xml");
        File out = new File(dir, fname.toString());
        log.debug("context trace file", out.getAbsolutePath());
        return new FileOutputStream(out);
    }

    protected String requestLogsDir() {
        return "ws-access";
    }

    void copyStream(InputStream in, OutputStream out, String name, int id) throws IOException {
        if (in == null) {
            return;
        }
        byte[] buffer = new byte[0xFFFF];
        int len = 0;
        FileOutputStream fis = createDebugOutputStream(requestLogsDir(), name, id);
        int copied = 0;
        try {
            while (true) {
                len = in.read(buffer, 0, buffer.length);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
                if (fis != null) {
                    fis.write(buffer, 0, len);
                }
                copied += len;
            }
            out.flush();
            if (fis != null) {
                fis.flush();
            }
        } finally {
            IOUtils.closeQuietly(fis);
            log.debug("context copied bytes", copied);
        }
    }
}
