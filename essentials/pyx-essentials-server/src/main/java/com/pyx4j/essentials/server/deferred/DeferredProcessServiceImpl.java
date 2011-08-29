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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.VoidSerializable;

public class DeferredProcessServiceImpl implements DeferredProcessService {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessServiceImpl.class);

    @Override
    public void getStatus(AsyncCallback<DeferredProcessProgressResponse> callback, String deferredCorrelationId, boolean finalize) {
        IDeferredProcess process = DeferredProcessRegistry.get(deferredCorrelationId);
        if (process != null) {
            DeferredProcessProgressResponse response = process.status();
            if (response.isCompleted() && finalize) {
                log.debug("process {} is completed and finalized", deferredCorrelationId);
                DeferredProcessRegistry.remove(deferredCorrelationId);
            }
            callback.onSuccess(response);
        } else {
            throw new RuntimeException("Process " + deferredCorrelationId + " not found");
        }
    }

    @Override
    public void cancel(AsyncCallback<VoidSerializable> callback, String deferredCorrelationId) {
        IDeferredProcess process = DeferredProcessRegistry.get(deferredCorrelationId);
        if (process != null) {
            process.cancel();
            log.debug("process {} is canceled", deferredCorrelationId);
            DeferredProcessRegistry.remove(deferredCorrelationId);
            DeferredProcessRegistry.saveMap();
            callback.onSuccess(null);
        } else {
            throw new RuntimeException("Process " + deferredCorrelationId + " not found");
        }

    }

}
