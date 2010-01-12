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

public class EntityPermission extends AbstractCRUDPermission {

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

    public EntityPermission(String name, String actions) {
        super(name, actions);
    }

    protected EntityPermission(Class<?> entityClass, int actions) {
        super(entityClass.getName(), actions);
    }

    protected EntityPermission(String name, int actions) {
        super(name, actions);
    }
}
