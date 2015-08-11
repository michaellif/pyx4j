/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 4, 2015
 * @author vlads
 */
package com.pyx4j.config.server.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import com.pyx4j.config.server.Trace;

public class ServerEventBus {

    private static Logger log = LoggerFactory.getLogger(ServerEventBus.class);

    private final EventBus eventBus;

    private ServerEventBus() {
        eventBus = new EventBus();
    }

    private static class SingletonHolder {
        public static final ServerEventBus INSTANCE = new ServerEventBus();
    }

    static ServerEventBus instance() {
        return SingletonHolder.INSTANCE;
    }

    public static void register(ServerEvent.Handler subscriber) {
        instance().eventBus.register(subscriber);
    }

    public static void unregister(ServerEvent.Handler subscriber) {
        instance().eventBus.unregister(subscriber);
    }

    public static void fireEvent(ServerEvent event) {
        log.debug("fireEvent {} {}, from {}", event.getClass().getSimpleName(), event, Trace.getCallOrigin(ServerEventBus.class));
        try {
            instance().eventBus.post(event);
        } catch (Throwable e) {
            log.error("failed to fire event {}", event.getClass().getSimpleName(), e);
        }
    }
}
