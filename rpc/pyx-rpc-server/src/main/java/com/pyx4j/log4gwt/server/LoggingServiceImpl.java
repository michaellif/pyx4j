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
 * Created on Nov 12, 2014
 * @author vlads
 */
package com.pyx4j.log4gwt.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.log4gwt.rpc.LoggingService;
import com.pyx4j.log4gwt.shared.LogEvent;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.ServerContext;

@IgnoreSessionToken
public class LoggingServiceImpl implements LoggingService {

    public static final String CLIENT_LOGGER_NAME = "client";

    private static final Logger log = LoggerFactory.getLogger(LoggingServiceImpl.class);

    private static final Logger clientLog = LoggerFactory.getLogger(CLIENT_LOGGER_NAME);

    private static Map<String, StackTraceDeobfuscator> deobfuscators = new HashMap<>();

    @Override
    public void log(AsyncCallback<VoidSerializable> callback, Vector<LogEvent> events) {
        boolean useLog4j = (clientLog.getClass().getName().contains("Log4j"));
        deobfuscateLogRecords(events);
        if (useLog4j) {
            for (LogEvent event : events) {
                ClientLog4j.log(event);
            }
        } else if ((clientLog.getClass().getName().contains("logback"))) {
            for (LogEvent event : events) {
                ClientLogback.log(event);
            }
        } else {
            for (LogEvent event : events) {
                ClientLog.log(event);
            }
        }
        callback.onSuccess(null);
    }

    private static void deobfuscateLogRecords(Collection<LogEvent> events) {
        try {
            String strongName = ServerContext.getRequestHeader(RpcRequestBuilder.STRONG_NAME_HEADER);
            StackTraceDeobfuscator stackTraceDeobfuscator = null;
            for (LogEvent event : events) {
                if (event.getStackTrace() != null) {
                    if (stackTraceDeobfuscator == null) {
                        stackTraceDeobfuscator = getDeobfuscator(strongName);
                    }

                    Throwable throwable = new Throwable(event.getThrowableMessage());
                    throwable.setStackTrace(stackTraceDeobfuscator.resymbolize(event.getStackTrace(), strongName));
                    event.resymbolize(throwable);
                }
            }
        } catch (Throwable e) {
            log.error("Unable to deobfuscate client log", e);
        }
    }

    private static StackTraceDeobfuscator getDeobfuscator(String strongName) throws MalformedURLException {
        StackTraceDeobfuscator stackTraceDeobfuscator = deobfuscators.get(strongName);
        if (stackTraceDeobfuscator == null) {
            URL urlPath = ServerContext.getRequest().getServletContext().getResource("/WEB-INF/deploy" + getGwtModuleRelativePath(strongName) + "symbolMaps/");
            log.debug("Load Deobfuscator fromUrl {}", urlPath);
            stackTraceDeobfuscator = StackTraceDeobfuscator.fromUrl(urlPath);

            deobfuscators.put(strongName, stackTraceDeobfuscator);
        }
        return stackTraceDeobfuscator;
    }

    private static String getGwtModuleRelativePath(String strongName) throws MalformedURLException {
        HttpServletRequest request = ServerContext.getRequest();
        String moduleBaseURL = ServletUtils.toServletContainerInternalURL(request, ServerContext.getRequestHeader(RpcRequestBuilder.MODULE_BASE_HEADER));

        String contextPath = request.getContextPath();

        String modulePath = new URL(moduleBaseURL).getPath();
        if (!modulePath.startsWith(contextPath)) {
            throw new Error("GWT modulePath " + modulePath + " is not from this context " + contextPath);
        }

        String contextRelativePath = modulePath.substring(contextPath.length());
        log.debug("Use contextRelativePath '{}' for strongName '{}'", contextRelativePath, strongName);
        return contextRelativePath;
    }
}
