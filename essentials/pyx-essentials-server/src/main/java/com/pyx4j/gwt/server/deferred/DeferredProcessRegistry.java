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
 * Created on Aug 24, 2011
 * @author vlads
 */
package com.pyx4j.gwt.server.deferred;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.ServerContext;

public class DeferredProcessRegistry {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessRegistry.class);

    private static final String DEFERRED_PROCESS_SESSION_ATTRIBUTE = DeferredProcessServicesImpl.class.getName();

    public static final String THREAD_POOL_DOWNLOADS = "Download";

    @SuppressWarnings("unchecked")
    private static synchronized HashMap<String, DeferredProcessInfo> getMap() {
        if (ServerContext.getSession() == null) {
            Lifecycle.beginAnonymousSession();
        }
        HashMap<String, DeferredProcessInfo> m = (HashMap<String, DeferredProcessInfo>) ServerContext.getSession().getAttribute(
                DEFERRED_PROCESS_SESSION_ATTRIBUTE);
        if (m == null) {
            m = new HashMap<>();
            ServerContext.getSession().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, m);
        }
        return m;
    }

    /**
     * This is required on GAE. Otherwise the session is not stored.
     */
    public static synchronized void saveMap() {
        if (ApplicationBackend.getBackendType() == ApplicationBackendType.GAE) {
            ServerContext.getSession().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, getMap());
        }
    }

    /**
     *
     * @return DeferredCorrelationId to be used by client to query for status
     */
    public static synchronized String register(IDeferredProcess process) {
        HashMap<String, DeferredProcessInfo> map = getMap();
        String deferredCorrelationId = String.valueOf(System.currentTimeMillis());
        map.put(deferredCorrelationId, new DeferredProcessInfo(process));
        saveMap();
        log.debug("process {} created in session {}", deferredCorrelationId, ServerContext.getSessionId());
        return deferredCorrelationId;
    }

    /**
     * Implementation dependent fork. For now just creates a new Thread.
     * In future we may move this execution to another server.
     *
     * @return DeferredCorrelationId to be used by client to query for status
     */
    public static synchronized String fork(IDeferredProcess process, String threadPoolName) {
        String deferredCorrelationId = register(process);

        DeferredProcessInfo info = getMap().get(deferredCorrelationId);
        //TODO use ThreadPools
        Thread t = new DeferredProcessWorkThread(threadPoolName + deferredCorrelationId, info);
        t.setDaemon(true);
        t.start();
        return deferredCorrelationId;
    }

    public static synchronized void start(String deferredCorrelationId, IDeferredProcess process, String threadPoolName) {
        DeferredProcessInfo info = getMap().get(deferredCorrelationId);
        //TODO use ThreadPools
        Thread t = new DeferredProcessWorkThread(threadPoolName + deferredCorrelationId, info);
        t.setDaemon(true);
        t.start();
    }

    public static synchronized IDeferredProcess get(String deferredCorrelationId) {
        DeferredProcessInfo info = getMap().get(deferredCorrelationId);
        if (info == null) {
            return null;
        } else {
            return info.process;
        }
    }

    public static synchronized DeferredProcessProgressResponse getStatus(String deferredCorrelationId) {
        DeferredProcessInfo info = getMap().get(deferredCorrelationId);
        if (info != null) {
            if (info.process != null) {
                return info.process.status();
            } else {
                return info.status;
            }
        } else {
            return null;
        }
    }

    public static synchronized void remove(String deferredCorrelationId) {
        getMap().remove(deferredCorrelationId);
        log.debug("process {} remove from session {}", deferredCorrelationId, ServerContext.getSessionId());
        saveMap();
    }
}
