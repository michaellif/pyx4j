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
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityFactory {

    private static final IEntityFactory impl;

    private static final Map<Class<?>, EntityMeta> entityMetaCache = new HashMap<Class<?>, EntityMeta>();

    private static final Map<Class<?>, IEntity> entityPrototypeCache = new HashMap<Class<?>, IEntity>();

    static {
        if (ApplicationMode.hasGWT()) {
            if (GWT.isClient()) {
                impl = GWT.create(IEntityFactory.class);
            } else {
                impl = ServerSideFactory.create(IEntityFactory.class);
            }
        } else {
            impl = ServerSideFactory.create(IEntityFactory.class);
        }
    }

    public static <T extends IEntity> T create(Class<T> entityClass) {
        return impl.create(entityClass, null, null);
    }

    public static <T extends IEntity> T create(Class<T> entityClass, IObject<?> parent, String fieldName) {
        return impl.create(entityClass, parent, fieldName);
    }

    public static synchronized EntityMeta getEntityMeta(Class<? extends IEntity> entityClass) {
        assert (entityClass != null) : "Get EntityMeta for null";
        EntityMeta meta = entityMetaCache.get(entityClass);
        if (meta == null) {
            meta = impl.createEntityMeta(entityClass);
            entityMetaCache.put(entityClass, meta);
        }
        return meta;
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T extends IEntity> T getEntityPrototype(Class<T> entityClass) {
        assert (entityClass != null) : "Get EntityPrototype for null";
        T meta = (T) entityPrototypeCache.get(entityClass);
        if (meta == null) {
            meta = create(entityClass, null, ".");
            entityPrototypeCache.put(entityClass, meta);
        }
        return meta;
    }

    /**
     * Resolve Entity class from DTO Class from @DTO expands
     */
    public static Class<? extends IEntity> resolveDTOClass(IEntity entity) {
        Class<? extends IEntity> instanceClass = entity.getInstanceValueClass();
        EntityMeta meta = EntityFactory.getEntityMeta(instanceClass);
        if (meta.getExpandedFromClass() != null) {
            return meta.getExpandedFromClass();
        } else if (meta.isTransient()) {
            throw new Error("Unable to resolve DBO class from DTO, use @DTO annotation on DTO class");
        } else {
            return instanceClass;
        }
    }
}
