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
 * Created on Sep 13, 2007
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.security;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.shared.Permission;

public class EntityPermission extends AbstractCRUDPermission {

    private static final long serialVersionUID = 7095635694477738182L;

    private final InstanceAccess instanceAccess;

    private final transient IEntity entityInstance;

    public static EntityPermission permissionCreate(Class<?> entityClass) {
        return new EntityPermission(entityClass, CREATE);
    }

    public static EntityPermission permissionRead(Class<?> entityClass) {
        return new EntityPermission(entityClass, READ);
    }

    public static EntityPermission permissionUpdate(Class<?> entityClass) {
        return new EntityPermission(entityClass, UPDATE);
    }

    public static EntityPermission permissionDelete(Class<?> entityClass) {
        return new EntityPermission(entityClass, DELETE);
    }

    public static EntityPermission permissionCreate(IEntity entity) {
        return new EntityPermission(entity, CREATE);
    }

    public static EntityPermission permissionRead(IEntity entity) {
        return new EntityPermission(entity, READ);
    }

    public static EntityPermission permissionUpdate(IEntity entity) {
        return new EntityPermission(entity, UPDATE);
    }

    public static EntityPermission permissionDelete(IEntity entity) {
        return new EntityPermission(entity, DELETE);
    }

    public EntityPermission(String name, String actions) {
        super(name, actions);
        this.entityInstance = null;
        instanceAccess = null;
    }

    public EntityPermission(Class<?> entityClass, int actions) {
        super(entityClass.getName(), actions);
        this.entityInstance = null;
        instanceAccess = null;
    }

    public EntityPermission(String name, int actions) {
        super(name, actions);
        this.entityInstance = null;
        instanceAccess = null;
    }

    public EntityPermission(IEntity entity, int actions) {
        super(entity.getObjectClass().getName(), actions);
        entityInstance = entity;
        instanceAccess = null;
    }

    public EntityPermission(String name, InstanceAccess instanceAccess, int actions) {
        super(name, actions);
        this.entityInstance = null;
        this.instanceAccess = instanceAccess;
    }

    public EntityPermission(Class<?> entityClass, InstanceAccess instanceAccess, int actions) {
        super(entityClass.getName(), actions);
        this.entityInstance = null;
        this.instanceAccess = instanceAccess;
    }

    @Override
    public boolean implies(Permission p) {
        if (super.implies(p)) {
            if (((EntityPermission) p).entityInstance != null) {
                if (this.instanceAccess == null) {
                    return true;
                } else {
                    return this.instanceAccess.allow(((EntityPermission) p).entityInstance);
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getPath() + ((instanceAccess != null) ? " " + instanceAccess.getClass().getSimpleName() : "") + " " + getActions();
    }

}
