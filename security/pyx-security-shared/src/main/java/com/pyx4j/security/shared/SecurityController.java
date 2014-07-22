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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.server.SecurityControllerCreator;

public abstract class SecurityController {

    private static final SecurityController controller = init();

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    private static final SecurityController init() {
        if (ApplicationMode.isGWTClient()) {
            // Use Controller defined in module "pyx-security-gwt"
            return GWT.create(SecurityController.class);
        } else {
            // Use Reflection to create Controller defined in module "pyx-security-server"
            return SecurityControllerCreator.create();
        }
    }

    public static SecurityController instance() {
        return controller;
    }

    //TODO redesign to make it non accessible
    public abstract Acl getAcl();

    public abstract Acl authorize(Set<Behavior> behaviours);

    public abstract Set<Behavior> getAllBehaviors(Set<Behavior> behaviors);

    public static <T extends Behavior> boolean check(Collection<T> list) {
        for (Behavior behavior : list) {
            if (controller.getAcl().checkBehavior(behavior)) {
                return true;
            }
        }
        return false;
    }

    public static boolean check(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (controller.getAcl().checkBehavior(behavior)) {
                return true;
            }
        }
        return false;
    }

    public static void assertBehavior(Behavior... behaviors) throws SecurityViolationException {
        if (!check(behaviors)) {
            log.warn("Permission denied {}", ConverterUtils.convertArray(behaviors, " or "));
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + ApplicationMode.DEV + ConverterUtils.convertArray(behaviors, " or "));
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }
    }

    //TODO redesign to make it non accessible
    public static Set<Behavior> getBehaviors() {
        return controller.getAcl().getBehaviours();
    }

    //TODO redesign to make it non accessible
    public static Collection<Permission> getPermissions() {
        return controller.getAcl().getPermissions();
    }

    public final static boolean check(AccessControlContext context, Permission... permissions) {
        for (Permission permission : permissions) {
            if (controller.getAcl().checkPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    public final static boolean check(Permission... permissions) {
        for (Permission permission : permissions) {
            if (controller.getAcl().checkPermission(null, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void assertPermission(AccessControlContext context, Permission... permissions) throws SecurityViolationException {
        if (!check(context, permissions)) {
            log.warn("Permission denied {} to access {}", ConverterUtils.convertArray(permissions, " or "), context);
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + ApplicationMode.DEV + ConverterUtils.convertArray(permissions, " or "));
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }
    }

    public static void assertPermission(Permission... permissions) throws SecurityViolationException {
        if (!check(permissions)) {
            log.warn("Permission denied {}", ConverterUtils.convertArray(permissions, " or "));
            if (ApplicationMode.isDevelopment()) {
                throw new SecurityViolationException("Permission denied " + ApplicationMode.DEV + ConverterUtils.convertArray(permissions, " or "));
            } else {
                throw new SecurityViolationException("Permission denied");
            }
        }
    }

    public static <T extends AccessRule> List<T> getAccessRules(Class<T> accessRuleClass, Object subject) {
        return controller.getAcl().getAccessRules(accessRuleClass, subject);
    }

}
