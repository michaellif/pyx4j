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
 * Created on Aug 29, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.deferred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.Context;

public class DeferredProcessorThread extends Thread {

    private final static Logger log = LoggerFactory.getLogger(DeferredProcessorThread.class);

    private final IDeferredProcess process;

    private final Runnable target;

    public DeferredProcessorThread(String name, IDeferredProcess process, Runnable target) {
        super(name + " processor");
        this.process = process;
        this.target = target;
    }

    @Override
    public final void run() {
        try {
            target.run();
            process.status().setCompleted();
            log.debug("processor completed");
        } catch (UserRuntimeException e) {
            log.error("processor error", e);
            process.status().setErrorStatusMessage(e.getMessage());
        } catch (Throwable e) {
            log.error("processor error", e);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                process.status().setErrorStatusMessage(e.getClass().getName() + " " + e.getMessage());
            } else {
                process.status().setError();
            }
        } finally {
            Context.cleanup();
        }
    }
}
