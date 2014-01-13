/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.oapi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class OAPIFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(OAPIFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final HttpServletResponse httpResponse = (HttpServletResponse) response;

            final AtomicBoolean rc = new AtomicBoolean(false);
            final String auth = httpRequest.getHeader("Authorization");
            if (auth != null) {

                try {
                    final int index = auth.indexOf(' ');
                    if (index > 0) {
                        final String[] credentials = StringUtils.split(new String(Base64.decodeBase64(auth.substring(index)), StandardCharsets.UTF_8), ':');
                        if (credentials.length != 3) {
                            log.warn("invalid credentials format");
                        } else {

                            NamespaceManager.setNamespace(credentials[1]);

                            AuthenticationRequest authenticationRequest = EntityFactory.create(AuthenticationRequest.class);
                            authenticationRequest.email().setValue(credentials[0]);
                            authenticationRequest.password().setValue(credentials[2]);

                            LocalService.create(CrmAuthenticationService.class).authenticate(new AsyncCallback<AuthenticationResponse>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    rc.set(false);
                                }

                                @Override
                                public void onSuccess(AuthenticationResponse result) {
                                    rc.set(SecurityController.checkBehavior(VistaCrmBehavior.OAPI));
                                }
                            }, new ClientSystemInfo(), authenticationRequest);
                        }
                    }
                } catch (Throwable t) {
                    log.error("Login failed", t);
                }
            }

            if (rc.get()) {
                try {
                    chain.doFilter(httpRequest, httpResponse);
                } finally {
                    Lifecycle.endSession();
                }
            } else {
                httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Vista Realm\"");
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
