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
 * Created on Jan 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AclBuilder {

    private static class PermissionsGroup {

        private Set<Permission> permissions = new HashSet<Permission>();

        private Set<Restriction> restrictions = new HashSet<Restriction>();

        protected void add(PermissionsGroup pg) {
            permissions.addAll(pg.permissions);
            restrictions.addAll(pg.restrictions);
        }

        protected void freeze() {
            permissions = Collections.unmodifiableSet(permissions);
            restrictions = Collections.unmodifiableSet(restrictions);
        }
    }

    private static class AclImpl implements Acl {

        private final Set<Behavior> behaviors;

        private final Set<Permission> permissions;

        private final Set<Restriction> restrictions;

        private AclImpl(Set<Behavior> behaviors, Set<Permission> permissions, Set<Restriction> restrictions) {
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

    }

    private Map<Behavior, PermissionsGroup> groups = new HashMap<Behavior, PermissionsGroup>();

    private final PermissionsGroup global = new PermissionsGroup();

    protected AclBuilder() {

    }

    protected void freeze() {
        global.freeze();
        for (Map.Entry<Behavior, PermissionsGroup> me : groups.entrySet()) {
            me.getValue().freeze();
        }
        groups = Collections.unmodifiableMap(groups);
    }

    private void addRoles(Role r, Set<Role> set) {
        set.add(r);
        Set<Role> memebers = r.getMemberRoles();
        for (Role m : memebers) {
            addRoles(m, set);
        }
    }

    public Acl createAcl(Set<Role> roles) {
        PermissionsGroup g = new PermissionsGroup();
        g.add(global);
        Set<Behavior> behaviors = new HashSet<Behavior>();
        Set<Role> allRoles = new HashSet<Role>();
        for (Role r : roles) {
            addRoles(r, allRoles);
        }
        for (Role r : allRoles) {
            behaviors.addAll(r.getAssignedBehaviors());
        }
        for (Behavior behavior : behaviors) {
            PermissionsGroup bg = groups.get(behavior);
            if (bg != null) {
                g.add(bg);
            }

        }
        g.freeze();
        return new AclImpl(Collections.unmodifiableSet(behaviors), g.permissions, g.restrictions);
    }

    protected void grant(Permission permission) {
        global.permissions.add(permission);
    }

    protected void revoke(Permission restriction) {
        revoke(new PermitionAntipode(restriction));
    }

    protected void revoke(Restriction restriction) {
        global.restrictions.add(restriction);
    }

    private PermissionsGroup getGroup(Behavior behavior) {
        PermissionsGroup g = groups.get(behavior);
        if (g == null) {
            g = new PermissionsGroup();
        }
        return g;
    }

    protected void grant(Behavior behavior, Permission permission) {
        getGroup(behavior).permissions.add(permission);
    }

    protected void revoke(Behavior behavior, Permission restriction) {
        revoke(behavior, new PermitionAntipode(restriction));
    }

    protected void revoke(Behavior behavior, Restriction restriction) {
        getGroup(behavior).restrictions.add(restriction);
    }
}
