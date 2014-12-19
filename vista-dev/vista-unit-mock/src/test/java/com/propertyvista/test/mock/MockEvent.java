/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-10
 * @author vlads
 */
package com.propertyvista.test.mock;

import java.util.HashMap;
import java.util.Map;

import com.google.web.bindery.event.shared.Event;

public abstract class MockEvent<H> extends Event<H> {

    // The Event.Type has a final hashCode so we can't create a proper equals functions
    @SuppressWarnings("rawtypes")
    private static Map<Class<? extends MockEvent>, Type<?>> typesByClass = new HashMap<Class<? extends MockEvent>, Type<?>>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static synchronized <H> com.google.web.bindery.event.shared.Event.Type<H> getTypeByClass(Class<? extends MockEvent<H>> eventClass) {
        com.google.web.bindery.event.shared.Event.Type<?> type = typesByClass.get(eventClass);
        if (type == null) {
            type = new Type();
            typesByClass.put(eventClass, type);
        }
        return (com.google.web.bindery.event.shared.Event.Type<H>) type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final com.google.web.bindery.event.shared.Event.Type<H> getAssociatedType() {
        return getTypeByClass((Class<? extends MockEvent<H>>) this.getClass());
    }

}
