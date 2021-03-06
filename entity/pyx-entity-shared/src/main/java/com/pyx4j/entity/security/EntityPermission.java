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
 */
package com.pyx4j.entity.security;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.shared.HasProtectionDomain;
import com.pyx4j.security.shared.ProtectionDomain;

public class EntityPermission extends AbstractCRUDPermission implements HasProtectionDomain {

    private static final long serialVersionUID = 7095635694477738182L;

    private final transient InstanceAccess instanceAccess;

    public static EntityPermission permissionCreate(Class<? extends IEntity> entityClass) {
        return new EntityPermission(entityClass, CREATE);
    }

    public static EntityPermission permissionRead(Class<? extends IEntity> entityClass) {
        return new EntityPermission(entityClass, READ);
    }

    public static EntityPermission permissionUpdate(Class<? extends IEntity> entityClass) {
        return new EntityPermission(entityClass, UPDATE);
    }

    public static EntityPermission permissionDelete(Class<? extends IEntity> entityClass) {
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

    @GWTSerializable
    protected EntityPermission() {
        super();
        this.instanceAccess = null;
    }

    public EntityPermission(String name, String actions) {
        super(name, actions);
        this.instanceAccess = null;
    }

    public EntityPermission(Class<? extends IEntity> entityClass, int actions) {
        super(entityClass.getName(), actions);
        this.instanceAccess = null;
    }

    public EntityPermission(String name, int actions) {
        super(name, actions);
        this.instanceAccess = null;
    }

    public EntityPermission(IEntity entity, int actions) {
        super(entity.getObjectClass().getName(), actions);
        this.instanceAccess = null;
    }

    public EntityPermission(String name, InstanceAccess instanceAccess, int actions) {
        super(name, actions);
        this.instanceAccess = instanceAccess;
    }

    public EntityPermission(Class<?> entityClass, InstanceAccess instanceAccess, int actions) {
        super(entityClass.getName(), actions);
        this.instanceAccess = instanceAccess;
    }

    @Override
    public ProtectionDomain<?> getProtectionDomain() {
        return instanceAccess;
    }

    @Override
    public String toString() {
        return getPath() + ((instanceAccess != null) ? " " + GWTJava5Helper.getSimpleName(instanceAccess.getClass()) : "") + " " + getActions();
    }

}
