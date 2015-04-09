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

import java.util.HashMap;
import java.util.Map;

import com.google.web.bindery.event.shared.Event;

public abstract class ServerEvent<H> extends Event<H> {

    // The Event.Type has a final hashCode so we can't create a proper equals functions
    @SuppressWarnings("rawtypes")
    private static Map<Class<? extends ServerEvent>, Type<?>> typesByClass = new HashMap<Class<? extends ServerEvent>, Type<?>>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static synchronized <H> com.google.web.bindery.event.shared.Event.Type<H> getTypeByClass(Class<? extends ServerEvent<H>> eventClass) {
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
        return getTypeByClass((Class<? extends ServerEvent<H>>) this.getClass());
    }

}
