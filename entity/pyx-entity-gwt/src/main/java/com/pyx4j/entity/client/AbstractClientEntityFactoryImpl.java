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
 * Created on Dec 29, 2009
 * @author vlads
 */
package com.pyx4j.entity.client;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.impl.IEntityFactoryImpl;
import com.pyx4j.entity.core.meta.EntityMeta;

/**
 * Base implementation for generated classes by GWT rebind.
 */
public abstract class AbstractClientEntityFactoryImpl implements IEntityFactoryImpl {

    private final Map<Class<? extends IEntity>, IEntityFactoryImpl> implementationsMap;

    protected AbstractClientEntityFactoryImpl() {
        implementationsMap = new HashMap<Class<? extends IEntity>, IEntityFactoryImpl>();
    }

    @Override
    public <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName) {
        IEntityFactoryImpl implCreator = implementationsMap.get(clazz);
        if (implCreator == null) {
            if (clazz == null) {
                throw new NullPointerException("Can't create {null} class");
            } else {
                throw new Error("Class " + clazz.getName() + " implementation not found");
            }
        }
        return implCreator.create(clazz, parent, fieldName);
    }

    @Override
    public EntityMeta createEntityMeta(Class<? extends IEntity> clazz) {
        IEntityFactoryImpl implCreator = implementationsMap.get(clazz);
        if (implCreator == null) {
            throw new Error("Class " + clazz.getName() + " implementation not found");
        }
        return implCreator.createEntityMeta(clazz);
    }

    protected void addClassFactory(Class<? extends IEntity> clazz, IEntityFactoryImpl implCreator) {
        implementationsMap.put(clazz, implCreator);
    }
}
