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
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class EntityImplReflectionHelper {

    public static IObject<?, ?> lazyCreateMember(SharedEntityHandler<?> implHandler, String memberName) {
        Method method;
        try {
            method = implHandler.getObjectClass().getMethod(memberName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
        if (IPrimitive.class.equals(method.getReturnType())) {
            return implHandler.lazyCreateMemberIPrimitive(method.getName(), (Class<?>) ((ParameterizedType) method.getGenericReturnType())
                    .getActualTypeArguments()[0]);
        } else if (ISet.class.equals(method.getReturnType())) {
            return implHandler.lazyCreateMemberISet(method.getName());
        } else if (IEntity.class.isAssignableFrom(method.getReturnType())) {
            return lazyCreateMemberIEntity(implHandler, method.getName(), method.getReturnType());
        } else {
            throw new RuntimeException("Unknown member type" + method.getReturnType());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends IEntity<?>> IEntity<T> lazyCreateMemberIEntity(SharedEntityHandler<?> implHandler, String name, Class<?> valueClass) {
        String handlerClassName = valueClass.getName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        Class<?> handlerClass;
        try {
            handlerClass = Class.forName(handlerClassName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e1) {
            handlerClass = EntityImplGenerator.instance().generateImplementation((Class<IEntity>) valueClass);
        }
        try {
            Constructor childConstructor = handlerClass.getConstructor(IEntity.class, String.class);
            return (IEntity<T>) childConstructor.newInstance(implHandler, name);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
