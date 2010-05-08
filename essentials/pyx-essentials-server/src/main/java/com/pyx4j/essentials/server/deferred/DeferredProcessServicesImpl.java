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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.deferred;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

public class DeferredProcessServicesImpl implements DeferredProcessServices {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessServicesImpl.class);

    private static final String DEFERRED_PROCESS_SESSION_ATTRIBUTE = DeferredProcessServicesImpl.class.getName();

    @SuppressWarnings("unchecked")
    private static synchronized HashMap<String, IDeferredProcess> getMap() {
        HashMap<String, IDeferredProcess> m = (HashMap<String, IDeferredProcess>) Context.getVisit().getAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE);
        if (m == null) {
            m = new HashMap<String, IDeferredProcess>();
            Context.getVisit().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, m);
        }
        return m;
    }

    public static class GetStatusImpl implements DeferredProcessServices.GetStatus {

        @Override
        public DeferredProcessProgressResponse execute(String deferredCorrelationID) {
            IDeferredProcess process = getMap().get(deferredCorrelationID);
            if (process != null) {
                return process.status();
            } else {
                throw new RuntimeException("Process " + deferredCorrelationID + " not found");
            }
        }

    };

    public static class CancelImpl implements DeferredProcessServices.Cancel {

        @Override
        public VoidSerializable execute(String deferredCorrelationID) {
            HashMap<String, IDeferredProcess> map = getMap();
            IDeferredProcess process = map.get(deferredCorrelationID);
            if (process != null) {
                process.cancel();
                map.remove(deferredCorrelationID);
                saveMap(map);
                return null;
            } else {
                throw new RuntimeException("Process " + deferredCorrelationID + " not found");
            }
        }

    };

    public static class ContinueExecutionImpl implements DeferredProcessServices.ContinueExecution {

        @Override
        public DeferredProcessProgressResponse execute(String deferredCorrelationID) {
            HashMap<String, IDeferredProcess> map = getMap();
            IDeferredProcess process = map.get(deferredCorrelationID);
            if (process != null) {
                try {
                    log.debug("execute process {}", deferredCorrelationID);
                    process.execute();
                    DeferredProcessProgressResponse r = process.status();
                    if (r.isCompleted()) {
                        map.remove(deferredCorrelationID);
                    }
                    return r;
                } catch (Throwable e) {
                    log.error("execute error", e);
                    DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
                    r.setError();
                    map.remove(deferredCorrelationID);
                    return r;
                } finally {
                    saveMap(map);
                }
            } else {
                throw new RuntimeException("Process " + deferredCorrelationID + " not found");
            }
        }

    };

    /**
     * This is required on GAE?
     */
    private static void saveMap(HashMap<String, IDeferredProcess> map) {
        Context.getVisit().setAttribute(DEFERRED_PROCESS_SESSION_ATTRIBUTE, map);
    }

    /**
     * 
     * @return DeferredCorrelationID
     */
    public static synchronized String register(IDeferredProcess process) {
        HashMap<String, IDeferredProcess> map = getMap();
        String deferredCorrelationID = String.valueOf(System.currentTimeMillis());
        map.put(deferredCorrelationID, process);
        saveMap(map);
        log.debug("process created {}", deferredCorrelationID);
        return deferredCorrelationID;
    }
}
