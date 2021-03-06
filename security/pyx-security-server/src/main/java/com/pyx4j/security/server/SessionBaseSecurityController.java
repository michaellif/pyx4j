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
 */
package com.pyx4j.security.server;

import java.util.Set;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.server.contexts.Visit;

public class SessionBaseSecurityController extends SecurityController {

    private final boolean debugMode;

    private AclCreator aclCreator;

    public SessionBaseSecurityController() {
        debugMode = (ServerSideConfiguration.isStartedUnderUnitTest() || ServerSideConfiguration.isStartedUnderJvmDebugMode())
                || ServerSideConfiguration.isRunningInDeveloperEnviroment();
        createAclCreator();
    }

    private final void createAclCreator() {
        AclCreator ac = ServerSideConfiguration.instance().getAclCreator();
        if (ac == null) {
            ac = new AclCreatorAllowAll();
        }
        aclCreator = ac;
    }

    @Override
    public Acl authorize(Set<Behavior> behaviors) {
        if (debugMode) {
            createAclCreator();
        }
        return aclCreator.createAcl(behaviors);
    }

    @Override
    public Set<Behavior> getAllBehaviors(Set<Behavior> behaviors) {
        return aclCreator.getAllBehaviors(behaviors);
    }

    @Override
    public Acl getAcl() {
        Visit v = ServerContext.getVisit();
        Acl userAcl = null;
        if (v != null) {
            userAcl = v.getAcl();
        }
        if (userAcl == null) {
            if (debugMode) {
                // Create new Public ACL
                createAclCreator();
            }
            return aclCreator.createAcl(null);
        } else {
            return userAcl;
        }
    }

}
