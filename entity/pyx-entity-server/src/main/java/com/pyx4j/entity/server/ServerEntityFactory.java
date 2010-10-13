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
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.server.impl.EntityMetaImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IEntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class ServerEntityFactory implements IEntityFactory {

    private static final Logger log = LoggerFactory.getLogger(ServerEntityFactory.class);

    @SuppressWarnings("unchecked")
    public <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName) {
        if (IEntity.class.equals(clazz)) {
            throw new Error("Should not use abstract IEntity class");
        }
        String handlerClassName = clazz.getName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        Class<?> handlerClass;
        try {
            handlerClass = Class.forName(handlerClassName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            log.debug("generate impl class {}", clazz.getName());
            handlerClass = EntityImplGenerator.instance().generateImplementation(clazz.getName());
        }
        try {
            if ((parent == null) && (fieldName == null)) {
                return (T) handlerClass.newInstance();
            } else {
                Constructor childConstructor = handlerClass.getConstructor(IObject.class, String.class);
                return (T) childConstructor.newInstance(parent, fieldName);
            }
        } catch (Throwable e) {
            log.error(handlerClassName + " instantiation error", e);
            throw new Error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> Class<T> entityClass(String domainName) {
        try {
            return (Class<T>) Class.forName(domainName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Not an Entity");
        }
    }

    @Override
    public EntityMeta createEntityMeta(Class<? extends IEntity> clazz) {
        return new EntityMetaImpl(clazz);
    }

}
