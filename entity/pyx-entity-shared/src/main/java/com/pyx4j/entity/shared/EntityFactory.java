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

import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityFactory {

    private static IEntityFactoryImpl impl;

    private static final Map<Class<?>, EntityMeta> entityMetaCache = new HashMap<Class<?>, EntityMeta>();

    public static void setImplementation(IEntityFactoryImpl impl) {
        EntityFactory.impl = impl;
    }

    public static <T extends IEntity> T create(Class<T> clazz) {
        return impl.create(clazz, null, null);
    }

    public static <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName) {
        return impl.create(clazz, parent, fieldName);
    }

    public static synchronized EntityMeta getEntityMeta(Class<? extends IEntity> clazz) {
        assert (clazz != null) : "Get meta for null";
        EntityMeta meta = entityMetaCache.get(clazz);
        if (meta == null) {
            meta = impl.createEntityMeta(clazz);
            entityMetaCache.put(clazz, meta);
        }
        return meta;
    }
}
