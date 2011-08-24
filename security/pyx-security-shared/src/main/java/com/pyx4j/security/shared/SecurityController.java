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
 * Created on Jan 13, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.shared;

import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.server.SecurityControllerCreator;

public abstract class SecurityController {

    private static final SecurityController controller = init();

    private static final SecurityController init() {
        if (GWT.isClient()) {
            // Use Controller defined in module "pyx-security-gwt"
            return GWT.create(SecurityController.class);
        } else {
            // Use Reflection to create Controller defined in module "pyx-security-server"
            return SecurityControllerCreator.createSecurityController();
        }
    }

    public static SecurityController instance() {
        return controller;
    }

    public abstract Acl getAcl();

    public abstract Acl authenticate(Set<Behavior> behaviours);

    public abstract Set<Behavior> getAllBehaviors(Set<Behavior> behaviors);

    public static boolean checkBehavior(Behavior behavior) {
        return controller.getAcl().checkBehavior(behavior);
    }

    public static boolean checkAnyBehavior(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (controller.getAcl().checkBehavior(behavior)) {
                return true;
            }
        }
        return false;
    }

    public static void assertBehavior(Behavior behavior) {
        if (!checkBehavior(behavior)) {
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + behavior);
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }
    }

    public static Set<Behavior> getBehaviors() {
        return controller.getAcl().getBehaviours();
    }

    public static boolean checkPermission(Permission permission) {
        return controller.getAcl().checkPermission(permission);
    }

    public static void assertPermission(Permission permission) throws SecurityViolationException {
        if (!checkPermission(permission)) {
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + permission);
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }
    }
}
