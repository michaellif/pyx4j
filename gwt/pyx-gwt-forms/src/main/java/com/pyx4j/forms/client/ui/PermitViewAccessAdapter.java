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
 * Created on Jun 9, 2014
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class PermitViewAccessAdapter implements IAccessAdapter {

    private final Permission[] permission;

    public PermitViewAccessAdapter(Permission... permission) {
        this.permission = permission;
    }

    @Override
    public Boolean isEnabled() {
        return null;
    }

    @Override
    public Boolean isEditable() {
        return null;
    }

    @Override
    public Boolean isVisible() {
        return SecurityController.check(permission);
    }

    @Override
    public Boolean isViewable() {
        return null;
    }

}
