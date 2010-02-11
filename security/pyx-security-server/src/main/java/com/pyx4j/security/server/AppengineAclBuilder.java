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
 * Created on Feb 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.AclBuilder;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.CoreBehavior;

public class AppengineAclBuilder extends AclBuilder {

    @Override
    public Acl createAcl(Set<Behavior> behaviors) {

        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            if (behaviors == null) {
                behaviors = new HashSet<Behavior>();
            }
            if (userService.isUserAdmin()) {
                behaviors.add(CoreBehavior.DEVELOPER);
            } else {
                behaviors.add(CoreBehavior.USER);
            }
        }

        return super.createAcl(behaviors);
    }
}
