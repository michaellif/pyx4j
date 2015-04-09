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
 * Created on May 26, 2014
 * @author vlads
 */
package com.pyx4j.entity.security;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.security.shared.HasProtectionDomain;
import com.pyx4j.security.shared.ProtectionDomain;

public class DataModelPermission<E extends IEntity> extends AbstractCRUDPermission implements HasProtectionDomain {

    private static final long serialVersionUID = 1L;

    private final transient E entityPrototype;

    //Not final because of GWT
    @GWTSerializable
    private InstanceAccess instanceAccess;

    public static <T extends IEntity> DataModelPermission<T> permissionCreate(Class<T> entityClass) {
        return new DataModelPermission<T>(entityClass, null, CREATE);
    }

    public static <T extends IEntity> DataModelPermission<T> permissionRead(Class<T> entityClass) {
        return new DataModelPermission<T>(entityClass, null, READ);
    }

    public static <T extends IEntity> DataModelPermission<T> permissionUpdate(Class<T> entityClass) {
        return new DataModelPermission<T>(entityClass, null, UPDATE);
    }

    public static <T extends IEntity> DataModelPermission<T> permissionDelete(Class<T> entityClass) {
        return new DataModelPermission<T>(entityClass, null, DELETE);
    }

    @GWTSerializable
    protected DataModelPermission() {
        this.entityPrototype = null;
        this.instanceAccess = null;
    }

    public DataModelPermission(Class<E> entityClass, InstanceAccess instanceAccess, int actions) {
        super(entityClass.getName(), actions);
        this.entityPrototype = EntityFactory.getEntityPrototype(entityClass);
        this.instanceAccess = instanceAccess;
    }

    public DataModelPermission(IEntity contextEntity, int actions) {
        super(contextEntity.getObjectClass().getName(), actions);
        this.entityPrototype = null;
        this.instanceAccess = null;
    }

    public static <T extends IEntity> DataModelPermission<T> create(Class<T> entityClass, InstanceAccess instanceAccess, int actions) {
        return new DataModelPermission<T>(entityClass, instanceAccess, actions);
    }

    public static <T extends IEntity> DataModelPermission<T> create(Class<T> entityClass, int actions) {
        return new DataModelPermission<T>(entityClass, null, actions);
    }

    public E proto() {
        assert (entityPrototype != null) : "not available after serialization";
        return entityPrototype;
    }

    @GWTSerializable
    @Deprecated
    private void setInstanceAccess(InstanceAccess instanceAccess) {
        this.instanceAccess = instanceAccess;
    }

    /**
     * TODO in future
     * 
     * @param member
     */
    public void exclude(IObject<?> proto_member) {
        //TODO in future
        throw new UnsupportedOperationException();
    }

    @Override
    public ProtectionDomain<?> getProtectionDomain() {
        return instanceAccess;
    }
}
