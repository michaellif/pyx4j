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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.server.impl.EntityMetaImpl;
import com.pyx4j.entity.server.impl.EntityPojoWrapperGenerator;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IEntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class ServerEntityFactory implements IEntityFactory {

    private static final Logger log = LoggerFactory.getLogger(ServerEntityFactory.class);

    private static final Map<Class<? extends IEntity>, Class<?>> impClasses = new HashMap<Class<? extends IEntity>, Class<?>>();

    private static final Map<Class<?>, Class<?>> pojoImpClasses = new HashMap<Class<?>, Class<?>>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName) {
        if (IEntity.class.equals(clazz)) {
            throw new Error("Should not use abstract IEntity class");
        }
        Class<?> handlerClass = null;
        handlerClass = impClasses.get(clazz);
        if (handlerClass == null) {
            String handlerClassName = clazz.getName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
            // Try to find class first
            if (parent != null) {
                try {
                    handlerClass = Class.forName(handlerClassName, true, parent.getClass().getClassLoader());
                } catch (ClassNotFoundException ignore1) {
                    if (parent.getClass().getClassLoader() != clazz.getClassLoader()) {
                        try {
                            handlerClass = Class.forName(handlerClassName, true, clazz.getClassLoader());
                        } catch (ClassNotFoundException ignore2) {
                        }
                    }
                }
                if ((handlerClass == null) && (parent.getClass().getClassLoader() != clazz.getClassLoader())) {
                    try {
                        handlerClass = Class.forName(handlerClassName, true, clazz.getClassLoader());
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            } else {
                try {
                    handlerClass = Class.forName(handlerClassName, true, clazz.getClassLoader());
                } catch (ClassNotFoundException ignore) {
                }
            }

            if ((handlerClass == null) && (EntityImplGenerator.instance().getContextClassLoader() != clazz.getClassLoader())) {
                try {
                    handlerClass = Class.forName(handlerClassName, true, EntityImplGenerator.instance().getContextClassLoader());
                } catch (ClassNotFoundException ignore) {
                }
            }

            if (handlerClass == null) {
                log.debug("generate impl class {}", clazz.getName());
                handlerClass = EntityImplGenerator.instance().generateImplementation(clazz);
            }
            impClasses.put(clazz, handlerClass);
        }

        try {
            if ((parent == null) && (fieldName == null)) {
                return (T) handlerClass.newInstance();
            } else {
                Constructor<T> childConstructor = (Constructor<T>) handlerClass.getConstructor(IObject.class, String.class);
                return childConstructor.newInstance(parent, fieldName);
            }
        } catch (Throwable e) {
            log.error(handlerClass.getName() + " instantiation error", e);
            throw new Error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> Class<T> entityClass(String domainName) {
        try {
            return (Class<T>) Class.forName(domainName, true, EntityImplGenerator.instance().getContextClassLoader());
        } catch (ClassNotFoundException e1) {
            try {
                return (Class<T>) Class.forName(domainName, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException("'" + domainName + "' Not an Entity");
            }
        }
    }

    @Override
    public EntityMeta createEntityMeta(Class<? extends IEntity> entityClass) {
        return new EntityMetaImpl(entityClass);
    }

    private static boolean allCalssesFound;

    public static Set<Class<? extends IEntity>> getAllEntityClasses() {
        if (!allCalssesFound) {
            List<String> allClasses = EntityClassFinder.getEntityClassesNames();
            for (String className : allClasses) {
                Class<? extends IEntity> entityClass = entityClass(className);
                if (!impClasses.containsKey(entityClass)) {
                    impClasses.put(entityClass, null);
                }
            }
            allCalssesFound = true;
        }
        return impClasses.keySet();
    }

    public static <T extends IEntity> Class<IPojo<T>> getPojoClass(Class<T> entityClass) {
        synchronized (pojoImpClasses) {
            @SuppressWarnings("unchecked")
            Class<IPojo<T>> pojoClass = (Class<IPojo<T>>) pojoImpClasses.get(entityClass);
            if (pojoClass == null) {
                pojoClass = EntityPojoWrapperGenerator.getPojoClass(entityClass);
                pojoImpClasses.put(entityClass, pojoClass);
            }
            return pojoClass;
        }
    }

    public static <T extends IEntity> IPojo<T> getPojo(T entity) {
        @SuppressWarnings("unchecked")
        Class<IPojo<T>> pojoClass = getPojoClass((Class<T>) entity.getObjectClass());
        IPojo<T> pojo;
        try {
            pojo = pojoClass.getConstructor(entity.getObjectClass()).newInstance(entity);
        } catch (InstantiationException e) {
            throw new Error("Can't create POJO for " + entity.getEntityMeta().getEntityClass(), e);
        } catch (IllegalAccessException e) {
            throw new Error("Can't create POJO for " + entity.getEntityMeta().getEntityClass(), e);
        } catch (IllegalArgumentException e) {
            throw new Error("Can't create POJO for " + entity.getEntityMeta().getEntityClass(), e);
        } catch (InvocationTargetException e) {
            throw new Error("Can't create POJO for " + entity.getEntityMeta().getEntityClass(), e);
        } catch (NoSuchMethodException e) {
            throw new Error("Can't create POJO for " + entity.getEntityMeta().getEntityClass(), e);
        }
        return pojo;
    }
}
