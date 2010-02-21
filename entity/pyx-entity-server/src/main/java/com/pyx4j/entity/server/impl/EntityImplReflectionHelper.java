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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

/**
 * We do use runtime reflection to collect meta data.
 * 
 * Ideally EntityImplGenerator can be made much more complex to move this to compile time.
 */
public class EntityImplReflectionHelper {

    static Class<?> toClass(Type type) {
        if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            return (Class<?>) type;
        }
    }

    static Class<?> primitiveValueClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else {
            if (type instanceof GenericArrayType) {
                return toClass(type);
            } else {
                // e.g. generic Collection<String> 
                return (Class<?>) ((ParameterizedType) type).getRawType();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static IObject<?> lazyCreateMember(Class<?> interfaceClass, SharedEntityHandler implHandler, String memberName) {
        Method method;
        try {
            method = interfaceClass.getMethod(memberName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
        Class<?> memberClass = method.getReturnType();
        if (IPrimitive.class.equals(memberClass)) {
            Class<?> paramType = primitiveValueClass(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
            return implHandler.lazyCreateMemberIPrimitive(method.getName(), paramType);
        } else if (IEntity.class.isAssignableFrom(memberClass)) {
            return implHandler.lazyCreateMemberIEntity(method.getName(), (Class<IEntity>) method.getReturnType());
        } else if (ISet.class.equals(memberClass)) {
            Type paramType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            return implHandler.lazyCreateMemberISet(method.getName(), (Class<IEntity>) paramType);
        } else if (IList.class.equals(memberClass)) {
            Type paramType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            return implHandler.lazyCreateMemberIList(method.getName(), (Class<IEntity>) paramType);
        } else if (IPrimitiveSet.class.equals(memberClass)) {
            Type paramType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            return implHandler.lazyCreateMemberIPrimitiveSet(method.getName(), (Class<IEntity>) paramType);
        } else {
            throw new RuntimeException("Unknown member type" + memberClass);
        }
    }

}
