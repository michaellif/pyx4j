/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 17, 2014
 * @author vlads
 */
package com.pyx4j.security.shared;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.server.ContextCreator;

public abstract class Context {

    public static <E extends UserVisit> E visit(Class<E> userVisitClass) {
        return instance().getUserVisit(userVisitClass);
    }

    /**
     * Never returns null
     *
     * @param userVisitClass
     */
    @SuppressWarnings("unchecked")
    public static <E extends UserVisitPreferences> E userPreferences(Class<E> userPreferencesClass) {
        UserVisit v = visit(UserVisit.class);
        if ((v == null) || v.getPreferences() == null) {
            return instance().defaultUserPreferences(userPreferencesClass);
        } else {
            return (E) v.getPreferences();
        }
    }

    private static final Context instance = init();

    private static final Context init() {
        if (ApplicationMode.isGWTClient()) {
            // Use Controller defined in module "pyx-security-gwt"
            return GWT.create(Context.class);
        } else {
            // Use Reflection to create Controller defined in module "pyx-security-server"
            return ContextCreator.create();
        }
    }

    public static Context instance() {
        return instance;
    }

    protected abstract <E extends UserVisit> E getUserVisit(Class<E> userVisitClass);

    protected abstract <E extends UserVisitPreferences> E defaultUserPreferences(Class<E> userPreferencesClass);
}
