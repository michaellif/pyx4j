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
 * Created on Jul 21, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class SecureConcern {

    private Permission[] permissions;

    // User/Developer Decision.
    boolean decision = true;

    Boolean securityControllerDecision;

    public SecureConcern() {

    }

    public SecureConcern(boolean decision) {
        setDecision(decision);
    }

    public void setPermission(Permission... permissions) {
        // java varargs creates empty arrays,  so consider it as no permissions set
        if (permissions == null || permissions.length == 0) {
            this.permissions = null;
        } else {
            this.permissions = permissions;
        }
        resetDecision();
    }

    private void resetDecision() {
        if (permissions == null) {
            securityControllerDecision = true;
        } else {
            securityControllerDecision = null;
        }
    }

    public void setContext(AccessControlContext context) {
        if ((context == null) || (permissions == null)) {
            resetDecision();
        } else {
            securityControllerDecision = SecurityController.check(context, permissions);
        }
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
    }

    public boolean getDecision() {
        // assert (securityControllerDecision != null) : "setSecurityContext() had not been called";
        // return decision && securityControllerDecision;

        if (securityControllerDecision == null) {
            return false;
        } else {
            return decision && securityControllerDecision;
        }
    }

    // TODO Bad function for bad selectTab in CTabbedEntityForm constructor
    @Deprecated
    public boolean getDecision2() {
        return decision;
    }

    public boolean hasDecision() {
        return securityControllerDecision != null;
    }
}
