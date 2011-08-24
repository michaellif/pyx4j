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
 * @version $Id$
 */
package com.pyx4j.essentials.server.deferred;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

public class DeferredProcessRegistry {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessRegistry.class);

    private static final String DEFERRED_PROCESS_SESSION_ATTRIBUTE = DeferredProcessServicesImpl.class.getName();

    @SuppressWarnings("unchecked")
    private static synchronized HashMap<String, IDeferredProcess> getMap() {
        if (Context.getSession() == null) {
            Lifecycle.beginAnonymousSession();
        }
        HashMap<String, IDeferredProcess> m = (HashMap<String, IDeferredProcess>) Context.getSession().getAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE);
        if (m == null) {
            m = new HashMap<String, IDeferredProcess>();
            Context.getSession().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, m);
        }
        return m;
    }

    /**
     * This is required on GAE?
     */
    public static void saveMap() {
        Context.getSession().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, getMap());
    }

    /**
     * 
     * @return DeferredCorrelationID
     */
    public static synchronized String register(IDeferredProcess process) {
        HashMap<String, IDeferredProcess> map = getMap();
        String deferredCorrelationID = String.valueOf(System.currentTimeMillis());
        map.put(deferredCorrelationID, process);
        saveMap();
        log.debug("process created {}", deferredCorrelationID);
        return deferredCorrelationID;
    }

    public static synchronized IDeferredProcess get(String deferredCorrelationID) {
        return getMap().get(deferredCorrelationID);
    }

    public static synchronized void remove(String deferredCorrelationID) {
        getMap().remove(deferredCorrelationID);
    }
}
