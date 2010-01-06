/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import com.pyx4j.entity.server.proxies.MemberMetaImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityImplReflectionHelper {

    public static MemberMeta getMemberMeta(SharedEntityHandler<?> implHandler, String memberName) {
        // TODO Use single instance per  IEntity/Member
        try {
            return new MemberMetaImpl(implHandler.getObjectClass().getMethod(memberName, (Class[]) null));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
    }

    public static IObject<?, ?> lazyCreateMember(SharedEntityHandler<?> implHandler, String memberName) {
        try {
            return lazyCreateMember(implHandler, implHandler.getObjectClass().getMethod(memberName, (Class[]) null));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
    }

    private static IObject<?, ?> lazyCreateMember(SharedEntityHandler<?> implHandler, Method method) {
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
    private static <T extends IObject<?, ?>> IEntity<T> lazyCreateMemberIEntity(SharedEntityHandler<?> implHandler, String name, Class<?> valueClass) {
        String handlerClassName = valueClass.getName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        try {
            Class<?> handlerClass = Class.forName(handlerClassName, true, Thread.currentThread().getContextClassLoader());
            Constructor childConstructor = handlerClass.getConstructor(IEntity.class, String.class);
            return (IEntity<T>) childConstructor.newInstance(implHandler, name);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
