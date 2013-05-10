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
 * @version $Id$
 */
package com.propertyvista.test.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class MockEventBus {

    private static Logger log = LoggerFactory.getLogger(MockEventBus.class);

    public static EventBus instance = new SimpleEventBus();

    public static <H> HandlerRegistration addHandler(Type<H> type, H handler) {
        return instance.addHandler(type, handler);
    }

    public static void fireEvent(Event<?> event) {
        log.trace("fireEvent {}", event.getClass().getSimpleName());
        instance.fireEvent(event);
    }

}
