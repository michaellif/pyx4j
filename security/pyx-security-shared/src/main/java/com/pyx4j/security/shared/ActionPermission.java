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
 * Created on Jun 5, 2014
 * @author vlads
 */
package com.pyx4j.security.shared;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.security.annotations.ActionId;

public class ActionPermission extends BasicPermission implements HasProtectionDomain {

    private static final long serialVersionUID = 1L;

    private ProtectionDomain<?> protectionDomain;

    //TODO Make serializable using  ActionId class

    @GWTSerializable
    protected ActionPermission() {
        super("");
        this.protectionDomain = null;
    }

    public ActionPermission(Class<? extends ActionId> actionClass) {
        super(actionClass.getName());
        this.protectionDomain = null;
    }

    public ActionPermission(Class<? extends ActionId> actionClass, ProtectionDomain<?> protectionDomain) {
        super(actionClass.getName());
        this.protectionDomain = protectionDomain;
    }

    @Override
    public ProtectionDomain<?> getProtectionDomain() {
        return protectionDomain;
    }

    @GWTSerializable
    @Deprecated
    private void setProtectionDomain(ProtectionDomain<?> instanceAccess) {
        this.protectionDomain = instanceAccess;
    }

}
