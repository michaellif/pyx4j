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
 */
package com.pyx4j.security.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AclSerializable implements Acl, Serializable {

    private static final long serialVersionUID = -1200104334832884170L;

    private Set<Behavior> behaviors;

    private Set<Permission> permissions;

    private Set<Restriction> restrictions;

    private Map<Object, Map<Class<? extends AccessRule>, List<AccessRule>>> accessRules;

    AclSerializable() {

    }

    AclSerializable(Set<Behavior> behaviors, Set<Permission> permissions, Set<Restriction> restrictions,
            Map<Object, Map<Class<? extends AccessRule>, List<AccessRule>>> accessRules) {
        super();
        this.behaviors = behaviors;
        this.permissions = permissions;
        this.restrictions = restrictions;
        this.accessRules = accessRules;
    }

    @Override
    public boolean checkBehavior(Behavior behavior) {
        return behaviors.contains(behavior);
    }

    @Override
    //TODO Optimize by Permission.class
    public boolean checkPermission(AccessControlContext context, Permission permission) {
        for (Permission p : permissions) {
            if (p.implies(permission)) {
                if ((p instanceof HasProtectionDomain) && (((HasProtectionDomain) p).getProtectionDomain() != null)
                        && (context != null && !context.implies(((HasProtectionDomain) p).getProtectionDomain()))) {
                    continue;
                }
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
    public Set<Behavior> getBehaviours() {
        return behaviors;
    }

    @Override
    public Collection<Permission> getPermissions() {
        return permissions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AccessRule> List<T> getAccessRules(Class<T> accessRuleInterfaceClass, Object subject) {
        assert accessRuleInterfaceClass.isInterface();
        Map<Class<? extends AccessRule>, List<AccessRule>> rulesBySubject = accessRules.get(subject);
        if (rulesBySubject != null) {
            return (List<T>) rulesBySubject.get(accessRuleInterfaceClass);
        }
        return null;
    }

}