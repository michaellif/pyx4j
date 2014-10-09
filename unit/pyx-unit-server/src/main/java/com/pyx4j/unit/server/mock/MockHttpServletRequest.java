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
 * @version $Id$
 */
package com.pyx4j.unit.server.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    protected String _url;

    protected String _scheme;

    protected String _contextPath;

    protected String _httpMethodString;

    protected String _pathInfo;

    protected int _port;

    protected String _queryString;

    protected String _requestURI;

    protected String _serverName;

    protected String _servletPath;

    public MockHttpServletRequest() {
    }

    public MockHttpServletRequest(String url) {
        splitUrl(url);
    }

    public void setContextPath(String context) {
        this._contextPath = context;
        updateServletPath();
    }

    protected void splitUrl(String url) {
        checkUrlPattern(url);
        setScheme();
        setServerAndPort();
        setRequestURIParts();
    }

    protected void updateServletPath() {
        if (_contextPath != null) {
            _servletPath = _requestURI.replace(_contextPath, "");
        }
    }

    protected void setServerAndPort() {

        String urlNoScheme = _url.replaceFirst(this._scheme, "");
        urlNoScheme = urlNoScheme.substring(3, (urlNoScheme.length() - 1));

        String[] urlTokens = urlNoScheme.split("/");
        String serverName = urlTokens[0];

        String[] domainTokens = serverName.split(":");
        _serverName = domainTokens[0];

        if (domainTokens.length > 1) {
            _port = Integer.valueOf(domainTokens[1]).intValue();
        } else {
            _port = 80;
        }
    }

    protected void setScheme() {
        if (_url.startsWith("https")) {
            _scheme = "https";
        } else {
            _scheme = "http";
        }
    }

    protected void setRequestURIParts() {
        String[] urlParts = _url.split("/");

        if (urlParts.length <= 2) {
            _requestURI = "/";
            _queryString = "";
        } else {
            String domainUrl = urlParts[0] + "//" + urlParts[2] + "/";
            if (_url.contains("?")) {
                _requestURI = "/" + (_url.replaceFirst(domainUrl, "")).split("?")[0];
                _queryString = (_url.replaceFirst(domainUrl, "")).split("?")[1];
            } else {
                _requestURI = "/" + _url.replaceFirst(domainUrl, "");
            }
        }

        if (_contextPath != null) {
            _servletPath = _requestURI.replace(_contextPath, "");
        } else {
            _servletPath = _requestURI;
        }

    }

    @SuppressWarnings("unused")
    protected void checkUrlPattern(String url) {
        // TODO Look for URL pattern in PYX and checks against that to see if URL is well formed
        if (false) {
            throw new Error("URL has not a valid URL format");
        } else {
            this._url = url;
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
        if (_url != null) {
            return _scheme;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
    }

    @Override
    public String getServerName() {
        if (_url != null) {
            return _serverName;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        if (_url != null) {
            // Default local address
            return "127.0.0.1";
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
    }

    @Override
    public int getLocalPort() {
        if (_url != null) {
            return _port;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
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
        headers.put(name, value);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
        if (_url != null) {
            return _contextPath;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
    }

    @Override
    public String getQueryString() {
        if (_url != null) {
            return _queryString;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
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
        if (_url != null) {
            return _requestURI;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(ServerSideConfiguration.instance().getMainApplicationURL());
    }

    @Override
    public String getServletPath() {
        if (_url != null) {
            return _servletPath;
        } else {
            throw new Error("URL for this mock request has not been set yet");
        }
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
