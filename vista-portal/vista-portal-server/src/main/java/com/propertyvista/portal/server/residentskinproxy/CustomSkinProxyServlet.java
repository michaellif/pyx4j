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
package com.propertyvista.portal.server.residentskinproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.portal.services.SiteThemeServicesImpl;
import com.propertyvista.server.domain.CustomSkinResourceBlob;

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
            execute(request, response);
        } catch (Throwable e) {
            log.error("Proxy GET error", e);
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    protected String getServiceURL(HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String path = request.getPathInfo();
        int hostEnd = path.indexOf('/', 1);
        String host = path.substring(1, hostEnd);
        ensureWhitelisted(host);
        String servicePath = path.substring(hostEnd);
        String url = new URL("http", host, 80, servicePath).toExternalForm();
//        if (request.getQueryString() != null) {
//            url += "?" + request.getQueryString();
//        }
        return url;
    }

    private void ensureWhitelisted(String host) {
        // match host against white list
        SiteDescriptor site = SiteThemeServicesImpl.getSiteDescriptorFromCache();
        try {
            if (site.residentPortalSettings().proxyWhitelist().contains(host.toLowerCase(Locale.ENGLISH))) {
                return;
            }
        } catch (Exception ignore) {
        }
        log.warn("Unknown host: {}", host);
        throw new RuntimeException("Unknown host: " + host);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = getServiceURL(request);
        log.debug("proxy request {} -> {}", request.getRequestURI(), url);
        CustomSkinResourceBlob blob = retrieveResource(url);
        int status = HttpURLConnection.HTTP_OK;
        if (blob == null) {
            long startTime = System.currentTimeMillis();
            boolean sucsess = false;
            GetMethod method = new GetMethod(url);
            method.setFollowRedirects(true);
            try {
                status = client.executeMethod(method);
                log.debug("proxy service " + method.getName() + " response status and duration", status, TimeUtils.secSince(startTime));
                blob = saveResource(url, method.getResponseHeader("Content-Type").getValue(), ResourceConverter.convert(method));
                sucsess = true;
            } finally {
                if (!sucsess) {
                    log.warn("Exception in accessing: {} {}", method.getName(), method.getURI().toString());
                }
                method.releaseConnection();
            }
        }
        response.setStatus(status);
        if (blob != null) {
            response.setContentType(blob.contentType().getValue());
            response.getOutputStream().write(blob.data().getValue());
        }
    }

    private CustomSkinResourceBlob retrieveResource(String url) {
        // get resource from DB
        EntityQueryCriteria<CustomSkinResourceBlob> criteria = EntityQueryCriteria.create(CustomSkinResourceBlob.class);
        criteria.eq(criteria.proto().url(), url);
        return Persistence.service().retrieve(criteria);
    }

    private CustomSkinResourceBlob saveResource(final String url, final String contentType, final InputStream rcStream) throws IOException {
        return new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<CustomSkinResourceBlob, IOException>() {
            @Override
            public CustomSkinResourceBlob execute() throws IOException {
                CustomSkinResourceBlob blob = EntityFactory.create(CustomSkinResourceBlob.class);
                blob.url().setValue(url);
                blob.contentType().setValue(contentType);
                blob.data().setValue(org.apache.commons.io.IOUtils.toByteArray(rcStream));
                Persistence.service().persist(blob);
                return blob;
            }
        });
    }
}
