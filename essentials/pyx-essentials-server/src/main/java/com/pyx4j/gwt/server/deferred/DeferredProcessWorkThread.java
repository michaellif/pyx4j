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
 * Created on Jan 29, 2012
 * @author vlads
 */
package com.pyx4j.gwt.server.deferred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.server.contexts.InheritableUserContext;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.ServerContext;

//TODO rename in master to DeferredProcessTask
class DeferredProcessWorkThread implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessWorkThread.class);

    private final DeferredProcessInfo info;

    InheritableUserContext inheritableUserContext;

    DeferredProcessWorkThread(DeferredProcessInfo info) {
        this.info = info;
        inheritableUserContext = ServerContext.getInheritableUserContext();
    }

    @Override
    public final void run() {
        try {
            Lifecycle.inheritUserContext(inheritableUserContext);
            if (!info.process.status().isCanceled()) {
                info.process.started();
                do {
                    info.process.execute();
                } while (!info.process.status().isCompleted());
                log.debug("process completed");
            }
        } catch (UserRuntimeException e) {
            log.error("processor error", e);
            info.setProcessErrorWithStatusMessage(e.getMessage());
        } catch (Throwable e) {
            log.error("processor error", e);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                info.setProcessErrorWithStatusMessage(e.getClass().getName() + " " + e.getMessage());
            } else {
                info.setProcessErrorWithStatusMessage(null);
            }
        } finally {
            Lifecycle.endContext();
        }
    }
}
