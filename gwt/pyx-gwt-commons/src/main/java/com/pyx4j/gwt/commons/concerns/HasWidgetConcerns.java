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
 * Created on Apr 13, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.concerns;

import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.Permission;

public interface HasWidgetConcerns extends HasEnablingConcerns, HasVisibilityConcerns, HasSecureConcern {

    @Override
    default void setSecurityContext(AccessControlContext context) {
        HasSecureConcern.setSecurityContext(concerns(), context);
        applyConcernRules();
    }

    // Permissions base Concern builder

    default void setVisiblePermission(Permission... permissions) {
        if (!AbstractPermissionDrivenConcern.arraysIsEmpty(permissions)) {
            visible(new VisibilitySecureConcern(permissions), permissions[0].toString());
        }
    }

    default void setEnabledPermission(Permission... permissions) {
        if (!AbstractPermissionDrivenConcern.arraysIsEmpty(permissions)) {
            enabled(new EnablingSecureConcern(permissions), permissions[0].toString());
        }
    }

    @Override
    default void inserConcernedParent(AbstractConcern parentConcern) {
        HasSecureConcern.super.inserConcernedParent(parentConcern);
        if (concerns().size() == 0) {
            concerns().add(parentConcern);
        } else {
            concerns().add(0, parentConcern);
        }
    }

    @Override
    default void applyConcernRules() {
        applyVisibilityRules();
        applyEnablingRules();
    }
}
