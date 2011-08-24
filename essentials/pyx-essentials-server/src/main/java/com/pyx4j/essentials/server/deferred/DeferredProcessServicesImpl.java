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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.rpc.shared.VoidSerializable;

public class DeferredProcessServicesImpl implements DeferredProcessServices {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessServicesImpl.class);

    public static class GetStatusImpl implements DeferredProcessServices.GetStatus {

        @Override
        public DeferredProcessProgressResponse execute(String deferredCorrelationID) {
            IDeferredProcess process = DeferredProcessRegistry.get(deferredCorrelationID);
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
            IDeferredProcess process = DeferredProcessRegistry.get(deferredCorrelationID);
            if (process != null) {
                process.cancel();
                DeferredProcessRegistry.remove(deferredCorrelationID);
                DeferredProcessRegistry.saveMap();
                return null;
            } else {
                throw new RuntimeException("Process " + deferredCorrelationID + " not found");
            }
        }

    };

    public static class ContinueExecutionImpl implements DeferredProcessServices.ContinueExecution {

        @Override
        public DeferredProcessProgressResponse execute(String deferredCorrelationID) {
            IDeferredProcess process = DeferredProcessRegistry.get(deferredCorrelationID);
            if (process != null) {
                try {
                    log.debug("execute process {}", deferredCorrelationID);
                    process.execute();
                    DeferredProcessProgressResponse r = process.status();
                    if (r.isCompleted()) {
                        DeferredProcessRegistry.remove(deferredCorrelationID);
                    }
                    return r;
                } catch (Throwable e) {
                    log.error("execute error", e);
                    DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
                    r.setError();
                    DeferredProcessRegistry.remove(deferredCorrelationID);
                    return r;
                } finally {
                    DeferredProcessRegistry.saveMap();
                }
            } else {
                throw new RuntimeException("Process " + deferredCorrelationID + " not found");
            }
        }

    };

}
