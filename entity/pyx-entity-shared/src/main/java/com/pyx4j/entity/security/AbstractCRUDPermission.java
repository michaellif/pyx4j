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
 * Created on Sep 14, 2007
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.security;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.security.shared.BasicPermission;
import com.pyx4j.security.shared.Permission;

public abstract class AbstractCRUDPermission extends BasicPermission {

    private static final long serialVersionUID = -868170178884486842L;

    public final static int CREATE = 0x1;

    public final static int READ = 0x2;

    public final static int UPDATE = 0x4;

    public final static int DELETE = 0x8;

    /**
     * All actions (C, R, U, D);
     */
    public final static int ALL = CREATE | READ | UPDATE | DELETE;

    //Not final because of GWT
    @GWTSerializable
    private int mask;

    //Not final because of GWT
    @GWTSerializable
    private String actions;

    @GWTSerializable
    protected AbstractCRUDPermission() {
        super("");
    }

    public AbstractCRUDPermission(String name, int actions) {
        super(name);
        this.mask = actions;
        this.actions = getActionsFromMask();
    }

    public AbstractCRUDPermission(String name, String actions) {
        this(name, decodeActions(actions));
    }

    @Override
    public boolean implies(Permission p) {
        if ((!(p instanceof AbstractCRUDPermission)) || (p.getClass() != this.getClass())) {
            return false;
        }
        AbstractCRUDPermission other = (AbstractCRUDPermission) p;
        return ((this.mask & other.mask) == other.mask) && super.implies(other);
    }

    @Override
    public String getActions() {
        return this.actions;
    }

    @Override
    public String toString() {
        return getPath() + " " + getActions();
    }

    private static int decodeActions(String actions) {
        String actionsLowerCase = actions.toLowerCase().trim();
        int mask = 0;
        for (String t : actionsLowerCase.split(",")) {
            mask |= decodeAction(t.trim());
        }
        return mask;
    }

    private static int decodeAction(String actionName) {
        if (actionName.equals("*")) {
            return ALL;
        } else if (actionName.equals("read")) {
            return READ;
        } else if ((actionName.equals("create")) || (actionName.equals("insert"))) {
            return CREATE;
        } else if ((actionName.equals("update")) || (actionName.equals("modify")) || (actionName.equals("write"))) {
            return UPDATE;
        } else if (actionName.equals("delete")) {
            return DELETE;
        } else {
            throw new IllegalArgumentException("unrecognized action: '" + actionName + "'");
        }
    }

    private void addAction(final StringBuffer sb, int maskConst, String name) {
        if ((this.mask & maskConst) == maskConst) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(name);
        }
    }

    private String getActionsFromMask() {
        StringBuffer sb = new StringBuffer();
        addAction(sb, CREATE, "create");
        addAction(sb, READ, "read");
        addAction(sb, UPDATE, "update");
        addAction(sb, DELETE, "delete");
        return sb.toString();
    }

    @GWTSerializable
    @Deprecated
    private void setMask(int mask) {
        this.mask = mask;
    }

    @GWTSerializable
    @Deprecated
    private void setActions(String actions) {
        this.actions = actions;
    }
}
