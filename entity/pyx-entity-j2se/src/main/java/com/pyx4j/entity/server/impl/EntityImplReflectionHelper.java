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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Hashtable;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.impl.SharedEntityHandler;

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

    public static Class<?> primitiveValueClass(Type type, Class<?> interfaceClass) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            return toClass(type);
        } else if (type instanceof ParameterizedType) {
            // Allow to use  our Pair<,> class
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            // e.g. generic Collection<String> 
            return resolveGenericType(type, interfaceClass);
        }
    }

    public static Class<?> resolveGenericType(Type genericType, Class<?> interfaceClass) {
        return resolveGenericType(genericType, interfaceClass, interfaceClass);
    }

    public static Class<?> resolveGenericType(Type genericType, Class<?> interfaceClass, Class<?> topInterfaceClass) {
        if (genericType instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) genericType;
            for (Type extendedInterfaceType : interfaceClass.getGenericInterfaces()) {
                if (extendedInterfaceType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) extendedInterfaceType;
                    if (typeVariable.getGenericDeclaration() == parameterizedType.getRawType()) {
                        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        for (int i = 0; i < actualTypeArguments.length; i++) {
                            //System.out.println("Actual Type:" + typeParameters[i] + "->" + actualTypeArguments[i]);
                            if (typeVariable == typeParameters[i]) {
                                if (actualTypeArguments[i] instanceof TypeVariable) {
                                    Class<?> resolved = resolveGenericType(actualTypeArguments[i], topInterfaceClass);
                                    if (resolved != null) {
                                        return resolved;
                                    }
                                } else {
                                    return (Class<?>) actualTypeArguments[i];
                                }
                            }
                        }
                    }
                }
            }
            // if not found try super class
            for (Class<?> superInterfaceClass : interfaceClass.getInterfaces()) {
                Class<?> resolved = resolveGenericType(genericType, superInterfaceClass, topInterfaceClass);
                if (resolved != null) {
                    return resolved;
                }
            }

            // Not found return Raw Type of generic
            for (Type boundType : typeVariable.getBounds()) {
                if (boundType instanceof Class<?>) {
                    return (Class<?>) boundType;
                } else if (boundType instanceof ParameterizedType) {
                    return (Class<?>) ((ParameterizedType) boundType).getRawType();
                }
            }
        }
        return null;
    }

    public static Class<?> resolveTypeGenericArgumentType(Type genericType, Class<?> interfaceClass) {
        return resolveType(((ParameterizedType) genericType).getActualTypeArguments()[0], interfaceClass);
    }

    public static Class<?> resolveType(Type genericType, Class<?> interfaceClass) {
        if (genericType instanceof Class<?>) {
            return (Class<?>) genericType;
        } else if (genericType instanceof ParameterizedType) {
            // Allow to use  our IList<Somthing<?>> class
            return (Class<?>) ((ParameterizedType) genericType).getRawType();
        } else {
            return resolveGenericType(genericType, interfaceClass);
        }
    }

    private static class MethodInfo {

        Class<?> klass;

        Class<?> valueCalss;

        //Class<?> genericValueCalss;
    }

    private static Map<String, MethodInfo> members = new Hashtable<String, MethodInfo>();

    @SuppressWarnings("unchecked")
    public static IObject<?> lazyCreateMember(Class<?> interfaceClass, SharedEntityHandler implHandler, String memberName) {
        String uName = interfaceClass.getName() + "." + memberName;
        MethodInfo methodInfo = members.get(uName);
        if (methodInfo == null) {
            Method method;
            try {
                method = interfaceClass.getMethod(memberName, (Class[]) null);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown member " + memberName + " in " + interfaceClass.getName());
            }
            methodInfo = new MethodInfo();
            methodInfo.klass = method.getReturnType();
            if (!IEntity.class.isAssignableFrom(methodInfo.klass)) {
                ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
                Type paramType = genericType.getActualTypeArguments()[0];
                if (IPrimitive.class.equals(methodInfo.klass)) {
                    methodInfo.valueCalss = primitiveValueClass(paramType, interfaceClass);
                } else if (paramType instanceof ParameterizedType) {
                    methodInfo.valueCalss = (Class<?>) ((ParameterizedType) paramType).getRawType();
                } else if (paramType instanceof Class) {
                    methodInfo.valueCalss = (Class<?>) paramType;
                } else if (paramType instanceof TypeVariable) {
                    methodInfo.valueCalss = resolveGenericType(paramType, interfaceClass);
                } else {
                    throw new AssertionError("Unexpected type '" + paramType + "' for member '" + memberName + "' of " + implHandler.debugString());
                }
            } else {
                Class<?> genericClass = resolveGenericType(method.getGenericReturnType(), interfaceClass);
                if (genericClass != null) {
                    methodInfo.klass = genericClass;
                }
            }
            members.put(uName, methodInfo);
        }
        if (IPrimitive.class.equals(methodInfo.klass)) {
            return implHandler.lazyCreateMemberIPrimitive(memberName, (Class<Serializable>) methodInfo.valueCalss);
        } else if (IEntity.class.isAssignableFrom(methodInfo.klass)) {
            return implHandler.lazyCreateMemberIEntity(memberName, (Class<IEntity>) methodInfo.klass);
        } else if (ISet.class.equals(methodInfo.klass)) {
            return implHandler.lazyCreateMemberISet(memberName, (Class<IEntity>) methodInfo.valueCalss);
        } else if (IList.class.equals(methodInfo.klass)) {
            return implHandler.lazyCreateMemberIList(memberName, (Class<IEntity>) methodInfo.valueCalss);
        } else if (IPrimitiveSet.class.equals(methodInfo.klass)) {
            return implHandler.lazyCreateMemberIPrimitiveSet(memberName, (Class<IEntity>) methodInfo.valueCalss);
        } else {
            throw new RuntimeException("Unknown member " + memberName + " type " + methodInfo.klass);
        }
    }
}
