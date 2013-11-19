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
 * Created on Dec 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Singleton application event Bus for Security, RPC and Navigation
 */
public class ClientEventBus {

    private static Logger log = LoggerFactory.getLogger(ClientEventBus.class);

    public static EventBus instance = new SimpleEventBus();

    public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
        return instance.addHandler(type, handler);
    }

    public static void fireEvent(GwtEvent<?> event) {
        log.trace("fireEvent {}", event.getClass().getName());
        instance.fireEvent(event);
    }

}
