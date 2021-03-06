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
 */
package com.pyx4j.config.shared;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.server.ServerSideFactory;

public class ApplicationMode {

    /**
     * Marker for error messages that are available only in development mode.
     */
    public static final String DEV = "(DEV:)";

    /**
     * Use this for testing application on computer completely disconnected from network.
     *
     * Change this if you want to make it work temporary. Tests in Build will fail!
     * local build run like this: mvn -Dmaven.test.failure.ignore=true
     */
    public static final boolean offlineDevelopment = false;

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

    /**
     * Server side helper to avoid missing GWTBridge
     *
     * @see GWT#isClient()
     */
    public static final boolean isGWTClient() {
        if (hasGWT()) {
            return GWT.isClient();
        } else {
            return false;
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

        boolean isQa();

        boolean isDemo();
    }

    /**
     * Used as dev/demo/prod switch.
     *
     * Affects:
     * - error message visibility to application user
     * - Ctrl+Q helpers
     * - data preloaded to application
     * - encryption algorithms are intentionally incompatibly with production
     * - enables debug messages in browser
     */
    public final static boolean isDevelopment() {
        return impl.isDevelopment();
    }

    public final static boolean isDemo() {
        return impl.isDemo();
    }

    public final static boolean isQa() {
        return impl.isQa();
    }

    public final static boolean isProduction() {
        return !impl.isDevelopment() && !impl.isDemo();
    }

    public final static String getModeInfo() {
        return (isDevelopment() ? "Development" : " ") //
                + (isDemo() ? "Demo" : " ") //
                + (isQa() ? "Qa" : " ") //
                + (isProduction() ? "Production" : " ");
    }
}
