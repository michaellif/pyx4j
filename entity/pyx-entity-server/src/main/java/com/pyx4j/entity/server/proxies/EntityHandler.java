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
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.impl.SetHandler;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class EntityHandler<OBJECT_TYPE extends IEntity<?>> extends SharedEntityHandler<OBJECT_TYPE> implements IEntity<OBJECT_TYPE>, InvocationHandler {

    public EntityHandler(Class<OBJECT_TYPE> clazz) {
        super(clazz);
    }

    EntityHandler(Class<OBJECT_TYPE> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
            return method.invoke(this, args);
        }

        IObject<?, ?> entity = meta.get(method.getName());
        if (entity == null) {
            Class<?>[] interfaces = new Class[] { method.getReturnType() };
            if (IPrimitive.class.equals(method.getReturnType())) {
                Class primitiveValueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                entity = new PrimitiveHandler((IEntity) proxy, method.getName(), primitiveValueClass);
            } else if (ISet.class.equals(method.getReturnType())) {
                entity = new SetHandler((IEntity) proxy, method.getName());

            } else {
                entity = (IObject<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new EntityHandler(method.getReturnType(),
                        (IEntity) proxy, method.getName()));
            }
            meta.put(method.getName(), entity);
        }
        return entity;

    }

}