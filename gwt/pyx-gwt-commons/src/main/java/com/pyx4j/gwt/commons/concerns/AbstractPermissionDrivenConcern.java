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
 * Created on Apr 12, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.concerns;

import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

/**
 * Permission Driven Concern
 *
 * Simplification of former SecureConcern
 *
 */
public abstract class AbstractPermissionDrivenConcern implements HasSecureConcern {

    private final Permission[] permissions;

    boolean securityControllerDecision;

    public AbstractPermissionDrivenConcern(Class<? extends ActionId> actionId) {
        this(new Permission[] { new ActionPermission(actionId) });
    }

    public AbstractPermissionDrivenConcern(Permission[] permissions) {
        assert !arraysIsEmpty(permissions) : "Use factory to optimize concern creation, see HasWidgetConcerns";
        this.permissions = permissions;

        // This may change and re evaluated base on context
        securityControllerDecision = SecurityController.check(permissions);
    }

    // java varargs creates empty arrays,  so consider it as no permissions set
    // Where is the same function in JDK, GWT / Guava ?
    public static boolean arraysIsEmpty(Permission[] permissions) {
        return (permissions == null || permissions.length == 0);
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        if (context == null) {
            securityControllerDecision = false;
        } else {
            securityControllerDecision = SecurityController.check(context, permissions);
        }
    }

    protected boolean getSecurityControllerDecision() {
        return securityControllerDecision;
    }

}
