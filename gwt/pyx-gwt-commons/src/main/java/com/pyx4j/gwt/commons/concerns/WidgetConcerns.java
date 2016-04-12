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

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.Permission;

public class WidgetConcerns implements VisibilityConcern, EnablingConcern, HasSecureConcern {

    protected final Collection<AbstractConcern> concerns;

    private ExplicitVisibilityConcern visible;

    private ExplicitEnablingConcern enabled;

    public WidgetConcerns() {
        concerns = new ArrayList<>();
    }

    public void setVisible(boolean visible) {
        if (this.visible == null) {
            concerns.add(this.visible = new ExplicitVisibilityConcern());
        }
        this.visible.setVisible(visible);
    }

    @Override
    public final Boolean isVisible() {
        return VisibilityConcern.isVisible(concerns);
    }

    public void visible(VisibilityConcern visibilityConcern, String... adapterName) {
        // TODO Wrapper with 'adapterName' to simplify debug
        concerns.add(visibilityConcern);
    }

    public void setVisiblePermission(Permission... permissions) {
        if (!AbstractPermissionDrivenConcern.arraysIsEmpty(permissions)) {
            visible(new VisibilitySecureConcern(permissions), permissions[0].toString());
        }
    }

    @Override
    public Boolean isEnabled() {
        return EnablingConcern.isEnabled(concerns);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == null) {
            concerns.add(this.enabled = new ExplicitEnablingConcern());
        }
        this.enabled.setEnabled(enabled);
    }

    // TODO setEnabledPermission

    @Override
    public void setSecurityContext(AccessControlContext context) {
        HasSecureConcern.setSecurityContext(concerns, context);
    }

}
