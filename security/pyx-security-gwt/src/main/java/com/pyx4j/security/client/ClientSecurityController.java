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
package com.pyx4j.security.client;

import java.util.Set;

import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class ClientSecurityController extends SecurityController {

    //TODO implement something
    private final Acl acl = new AclImpl();

    // Allow everything
    private static class AclImpl implements Acl {

        @Override
        public boolean checkBehavior(Behavior behavior) {
            return false;
        }

        @Override
        public boolean checkPermission(Permission permission) {
            return true;
        }

        @Override
        public Set<Behavior> getBehaviors() {
            return null;
        }

    }

    @Override
    public Acl getAcl() {
        return acl;
    }

}
