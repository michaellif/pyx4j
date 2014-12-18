/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-09
 * @author vlads
 */
package com.propertyvista.test.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

import com.pyx4j.config.server.Trace;

public class MockEventBus {

    private static Logger log = LoggerFactory.getLogger(MockEventBus.class);

    private final EventBus eventBus;

    private MockEventBus() {
        eventBus = new SimpleEventBus();
    }

    private static class SingletonHolder {
        public static final MockEventBus INSTANCE = new MockEventBus();
    }

    static MockEventBus instance() {
        return SingletonHolder.INSTANCE;
    }

    public static <H> HandlerRegistration addHandler(Class<? extends MockEvent<H>> eventClass, H handler) {
        return instance().eventBus.addHandler(MockEvent.getTypeByClass(eventClass), handler);
    }

    public static void fireEvent(MockEvent<?> event) {
        log.debug("fireEvent {}, from {}", event.getClass().getSimpleName(), Trace.getCallOrigin(MockEventBus.class));
        instance().eventBus.fireEvent(event);
    }

}
