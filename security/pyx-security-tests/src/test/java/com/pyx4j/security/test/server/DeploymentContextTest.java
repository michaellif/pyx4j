/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 10, 2014
 * @author vlads
 */
package com.pyx4j.security.test.server;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.server.contexts.DeploymentContextHttpServletRequestWrapper;
import com.pyx4j.unit.server.mock.MockHttpServletRequest;

public class DeploymentContextTest {

    @Test
    public void testNotForwarded() {
        HttpServletRequest r = request("http://localhost:8080/warContext/appContext/srvc").build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/warContext/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://localhost:8080/warContext/appContext/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/warContext/appContext/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/warContext/appContext/srvc"));
    }

    @Test
    public void testJettyContextLessontext() {
        HttpServletRequest r = request("http://localhost:8080/appContext/srvc").jettyContextLess(true).build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://localhost:8080/appContext/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/appContext/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/appContext/srvc"));
    }

    @Test
    public void testForwardedPath() {
        HttpServletRequest r = request("http://localhost:8080/warContext/appContext/srvc").forwardedPath("/appContext").build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/warContext/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://localhost:8080/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/warContext/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/warContext/srvc"));
    }

    @Test
    public void testHost() {
        HttpServletRequest r = request("http://localhost:8080/warContext/appContext/srvc").forwardedHost("pyx4j.com").build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/warContext/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://pyx4j.com/warContext/appContext/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/warContext/appContext/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/warContext/appContext/srvc"));
    }

    @Test
    public void testHostAndContext() {
        HttpServletRequest r = request("http://localhost:8080/warContext/appContext/srvc").forwardedHost("pyx4j.com").forwardedContext("/warContext").build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/warContext/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://pyx4j.com/appContext/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/appContext/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/appContext/srvc"));
    }

    @Test
    public void testHostAndContextAndApp() {
        HttpServletRequest r = request("http://localhost:8080/warContext/appContext/srvc").forwardedHost("pyx4j.com").forwardedContext("/warContext/appContext")
                .build();
        Assert.assertEquals("/warContext", r.getContextPath());
        Assert.assertEquals("/warContext/appContext/srvc", r.getRequestURI());
        Assert.assertEquals("/appContext/srvc", r.getServletPath());
        Assert.assertEquals("http://pyx4j.com/srvc", r.getRequestURL().toString());
        Assert.assertEquals("/bob", ServletUtils.getRelativeServletPath(r, "/appContext/bob"));

        Assert.assertEquals("http://h:80/warContext/appContext/srvc", ServletUtils.toServletContainerInternalURL(r, "http://h:80/srvc"));
    }

    private RequestBuilder request(String url) {
        return new RequestBuilder(url);
    }

    static class RequestBuilder {

        private String url;

        private boolean jettyContextLess;

        private String forwardedHost;

        private String forwardedContext;

        private String forwardedPath;

        public RequestBuilder(String url) {
            this.url = url;
        }

        public RequestBuilder jettyContextLess(boolean jettyContextLess) {
            this.jettyContextLess = jettyContextLess;
            return this;
        }

        public RequestBuilder forwardedHost(String forwardedHost) {
            this.forwardedHost = forwardedHost;
            return this;
        }

        public RequestBuilder forwardedContext(String forwardedContext) {
            this.forwardedContext = forwardedContext;
            return this;
        }

        public RequestBuilder forwardedPath(String forwardedPath) {
            this.forwardedPath = forwardedPath;
            return this;
        }

        HttpServletRequest build() {
            MockHttpServletRequest httprequest = new MockHttpServletRequest("/warContext", url);
            if (jettyContextLess) {
                httprequest.setAttribute(ServletUtils.x_jetty_contextLess, "/warContext");
            }
            if (forwardedPath != null) {
                httprequest.setAttribute(ServletUtils.x_forwarded_path, forwardedPath);
            }
            httprequest.setHeader(ServletUtils.x_forwarded_host, forwardedHost);
            httprequest.setHeader(ServletUtils.x_forwarded_context, forwardedContext);
            return new DeploymentContextHttpServletRequestWrapper(httprequest);
        }
    }
}
