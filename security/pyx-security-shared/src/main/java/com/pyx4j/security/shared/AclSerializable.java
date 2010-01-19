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
 * Created on Jan 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.shared;

import java.io.Serializable;
import java.util.Set;

@SuppressWarnings("serial")
public class AclSerializable implements Acl, Serializable {

    private Set<Behavior> behaviors;

    private Set<Permission> permissions;

    private Set<Restriction> restrictions;

    AclSerializable() {

    }

    AclSerializable(Set<Behavior> behaviors, Set<Permission> permissions, Set<Restriction> restrictions) {
        super();
        this.behaviors = behaviors;
        this.permissions = permissions;
        this.restrictions = restrictions;
    }

    @Override
    public boolean checkBehavior(Behavior behavior) {
        return behaviors.contains(behavior);
    }

    @Override
    //TODO Optimize by Permission.class
    public boolean checkPermission(Permission permission) {
        for (Permission p : permissions) {
            if (p.implies(permission)) {
                for (Restriction r : restrictions) {
                    if (r.implies(permission)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Behavior> getBehaviors() {
        return behaviors;
    }

}