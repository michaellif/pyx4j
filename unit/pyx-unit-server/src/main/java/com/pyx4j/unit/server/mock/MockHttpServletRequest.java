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
 * Created on 2011-03-30
 * @author vlads
 */
package com.pyx4j.unit.server.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import com.pyx4j.config.server.ServerSideConfiguration;

public class MockHttpServletRequest implements HttpServletRequest {

    protected Hashtable<String, String> headers = new Hashtable<String, String>();

    protected Hashtable<String, Object> attributes = new Hashtable<String, Object>();

    protected String url;

    protected String scheme;

    protected String contextPath;

    protected String httpMethodString;

    protected String pathInfo;

    protected int port;

    protected String queryString;

    protected String requestURI;

    protected String serverName;

    protected String servletPath;

    public MockHttpServletRequest() {
        String url = ServerSideConfiguration.instance().getMainApplicationURL();
        if (url != null) {
            splitUrl(url);
        }
    }

    public MockHttpServletRequest(String url) {
        splitUrl(url);
    }

    public MockHttpServletRequest(String context, String url) {
        splitUrl(url);
        setContextPath(context);
    }

    public MockHttpServletRequest(String context, String servletPath, String url) {
        splitUrl(url);
        setContextPath(context);
        setServletPath(servletPath);
    }

    public void setContextPath(String context) {
        this.contextPath = context;
        updateServletPath();
    }

    private void setServletPath(String servletPath) {
        this.pathInfo = this.servletPath.substring(servletPath.length());
        this.servletPath = servletPath;
    }

    protected void splitUrl(String url) {
        URL u;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        this.url = url;
        this.scheme = u.getProtocol();
        this.serverName = u.getHost();
        this.port = u.getPort();
        if (u.getPort() == -1) {
            if (this.scheme.equalsIgnoreCase("http")) {
                this.port = 80;
            } else if (this.scheme.equalsIgnoreCase("https")) {
                this.port = 443;
            }
        }

        setRequestURIParts();
    }

    protected void updateServletPath() {
        if (contextPath != null) {
            servletPath = requestURI.replace(contextPath, "");
        }
    }

    protected void setRequestURIParts() {
        String[] urlParts = url.split("/");

        if (urlParts.length <= 2) {
            requestURI = "/";
            queryString = "";
        } else {
            String domainUrl = urlParts[0] + "//" + urlParts[2] + "/";
            if (url.contains("?")) {
                requestURI = "/" + (url.replaceFirst(domainUrl, "")).split("\\?")[0];
                queryString = (url.replaceFirst(domainUrl, "")).split("\\?")[1];
            } else {
                requestURI = "/" + url.replaceFirst(domainUrl, "");
            }
        }

        if (contextPath != null) {
            servletPath = requestURI.replace(contextPath, "");
        } else {
            servletPath = requestURI;
        }

    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return attributes.keys();
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException();

    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getScheme() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return scheme;
    }

    @Override
    public String getServerName() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return serverName;
    }

    @Override
    public int getServerPort() {
        return port;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteAddr() {
        return "1.1.1.1";
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        return "https".equals(getScheme());
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalAddr() {
        // Default local address
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return port;
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setHeader(String name, String value) {
        if (value == null) {
            headers.remove(name);
        } else {
            headers.put(name, value);
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathInfo() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return pathInfo;
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return contextPath;
    }

    @Override
    public String getQueryString() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURI() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(url);
    }

    @Override
    public String getServletPath() {
        assert (url != null) : "URL for this mock request has not been set yet";
        return servletPath;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (create && TestLifecycle.threadLocalContext.get().session == null) {
            TestLifecycle.threadLocalContext.get().session = new MockHttpSession();
        }
        return TestLifecycle.threadLocalContext.get().session;
    }

    @Override
    public HttpSession getSession() {
        return TestLifecycle.threadLocalContext.get().session;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getContentLengthLong() {
        return -1;
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

}
