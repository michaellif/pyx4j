/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityHandler<OBJECT_TYPE extends IEntity<?>> extends SharedEntityHandler<OBJECT_TYPE> implements IEntity<OBJECT_TYPE>, InvocationHandler {

    public EntityHandler(Class<OBJECT_TYPE> clazz) {
        super(clazz);
    }

    EntityHandler(Class<OBJECT_TYPE> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
    }

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        // TODO Use single instance per  IEntity/Member
        try {
            return new MemberMetaImpl(getObjectClass().getMethod(memberName, (Class[]) null));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
    }

    @Override
    protected void lazyCreateMembersNamesList() {
        for (Method method : getObjectClass().getMethods()) {
            if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
                continue;
            }
            createMemeber(method.getName());
        }
    }

    @Override
    protected IObject<?, ?> lazyCreateMember(String memberName) {
        try {
            return lazyCreateMember(getObjectClass().getMethod(memberName, (Class[]) null));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown member " + memberName);
        }
    }

    private IObject<?, ?> lazyCreateMember(Method method) {
        if (IPrimitive.class.equals(method.getReturnType())) {
            return lazyCreateMemberIPrimitive(method.getName(), (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
        } else if (ISet.class.equals(method.getReturnType())) {
            return lazyCreateMemberISet(method.getName());
        } else if (IEntity.class.isAssignableFrom(method.getReturnType())) {
            return lazyCreateMemberIEntity(method.getName(), method.getReturnType());
        } else {
            throw new RuntimeException("Unknown member type" + method.getReturnType());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IObject<?, ?>> IEntity<T> lazyCreateMemberIEntity(String name, Class<?> valueClass) {
        Class<?>[] interfaces = new Class[] { valueClass };
        return (IEntity<T>) Proxy.newProxyInstance(valueClass.getClassLoader(), interfaces, new EntityHandler(valueClass, this, name));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
            return method.invoke(this, args);
        }

        IObject<?, ?> member = members.get(method.getName());
        if (member == null) {
            member = lazyCreateMember(method);
            members.put(method.getName(), member);
        }
        return member;

    }

}