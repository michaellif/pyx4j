/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 19, 2010
 * @author vlads
 */
package com.pyx4j.server.contexts;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.LocaleResolver;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.server.RequestDebug;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.server.I18nManager;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.rpc.shared.ContainerHandledUserRuntimeException;
import com.pyx4j.rpc.shared.StatusCodeUserRuntimeException;
import com.pyx4j.server.contexts.AntiDoS.AccessCounter;

/**
 * Setup Context thread locals.
 */
public class LifecycleFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(LifecycleFilter.class);

    private static String LIFECYCLE_SETUP_REQUEST_ATR = LifecycleFilter.class.getName();

    private AntiDoS antiDoS;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        antiDoS = new AntiDoS();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long requestStart = System.currentTimeMillis();
        AccessCounter counter = antiDoS.beginRequest(request, requestStart);
        if (counter == null) {
            if (response instanceof HttpServletResponse) {
                if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, ApplicationMode.DEV + antiDoS.debugRequest(request));
                } else {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                }
            }
            return;
        }

        try {
            if (!(request instanceof HttpServletRequest) || (request.getAttribute(LIFECYCLE_SETUP_REQUEST_ATR) != null)) {
                chain.doFilter(request, response);
            } else {
                request.setAttribute(LIFECYCLE_SETUP_REQUEST_ATR, Boolean.TRUE);
                HttpServletRequest httprequest = (HttpServletRequest) request;
                HttpServletResponse httpresponse = (HttpServletResponse) response;
                if (!allowRequest(httprequest, httpresponse)) {
                    return;
                }
                try {
                    NamespaceManager.setNamespace(ServerSideConfiguration.instance().getNamespaceResolver().getNamespaceData(httprequest).getNamespace());
                    String remoteAddr = ServletUtils.getActualRequestRemoteAddr(httprequest);
                    if (remoteAddr != null) {
                        LoggerConfig.mdcPut(LoggerConfig.MDC_remoteAddr, remoteAddr);
                    }
                    LocaleResolver lr = ServerSideConfiguration.instance().getLocaleResolver();
                    if (lr != null) {
                        I18nManager.setThreadLocale(lr.getRequestLocale(httprequest));
                    }

                    Lifecycle.beginRequest(httprequest, httpresponse);

                    HttpSession session = httprequest.getSession(false);
                    if (session != null) {
                        LoggerConfig.mdcPut(LoggerConfig.MDC_sessionNum, session.getId());
                    }

                    chain.doFilter(httprequest, response);
                } catch (Throwable t) {
                    if (t instanceof ContainerHandledUserRuntimeException) {
                        log.debug("return http error {}", t.getClass());
                    } else {
                        log.error("return http error {}", t);
                    }
                    if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                        RequestDebug.debug(request);
                        RequestDebug.debug(httprequest);
                        if (t instanceof UserRuntimeException) {
                            if (t instanceof StatusCodeUserRuntimeException) {
                                httpresponse.sendError(((StatusCodeUserRuntimeException) t).getStatusCode(), t.getMessage());
                            } else if (t instanceof ContainerHandledUserRuntimeException) {
                                throw (ContainerHandledUserRuntimeException) t;
                            } else {
                                httpresponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
                            }
                        } else {
                            httpresponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApplicationMode.DEV + t.getMessage());
                        }
                    } else {
                        if (t instanceof UserRuntimeException) {
                            if (t instanceof StatusCodeUserRuntimeException) {
                                httpresponse.sendError(((StatusCodeUserRuntimeException) t).getStatusCode(), t.getMessage());
                            } else if (t instanceof ContainerHandledUserRuntimeException) {
                                throw (ContainerHandledUserRuntimeException) t;
                            } else {
                                httpresponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t.getMessage());
                            }
                        } else {
                            httpresponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    }
                } finally {
                    // We call LoggerConfig.mdcClear();
                    //LoggerConfig.mdcRemove(LoggerConfig.MDC_userID);
                    //LoggerConfig.mdcRemove(LoggerConfig.MDC_sessionNum);
                    Lifecycle.endRequest();
                }
            }
        } finally {
            antiDoS.endRequest(counter, requestStart);
        }

    }

    protected boolean allowRequest(HttpServletRequest httprequest, HttpServletResponse httpresponse) throws IOException {
        if (httprequest.getRequestURI().endsWith(".rpc")) {
            log.error("access to *.rpc files blocked");
            httpresponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        } else {
            return true;
        }
    }
}
