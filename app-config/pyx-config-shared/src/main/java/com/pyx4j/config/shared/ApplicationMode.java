/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-08-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.shared;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.server.ServerSideFactory;

public class ApplicationMode {

    /**
     * Marker for error messages that are available only in development mode.
     */
    public static final String DEV = "(DEV:)";

    private final static Mode impl;

    static {
        if (hasGWT()) {
            if (GWT.isClient()) {
                impl = GWT.create(Mode.class);
            } else {
                impl = ServerSideFactory.create(Mode.class);
            }
        } else {
            impl = ServerSideFactory.create(Mode.class);
        }
    }

    public static final boolean hasGWT() {
        try {
            GWT.isClient();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static interface Mode {
        boolean isDevelopment();
    }

    static final class DevelopmentMode implements Mode {

        @Override
        public boolean isDevelopment() {
            return true;
        }
    }

    static final class ProductionMode implements Mode {

        @Override
        public boolean isDevelopment() {
            return false;
        }
    }

    /**
     * Used as dev/prod switch. For production build include GWT module
     * com.pyx4j.gwt.ApplicationProductionMode
     */
    public final static boolean isDevelopment() {
        return impl.isDevelopment();
    }
}
