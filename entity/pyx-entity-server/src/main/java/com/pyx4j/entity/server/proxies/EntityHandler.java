/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server.proxies;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class EntityHandler<T extends IEntity<?>> extends ObjectHandler<T, Map<String, Object>> implements IEntity<T> {

    private Map<String, Object> data;

    private final HashMap<String, IObject<?, ?>> meta = new HashMap<String, IObject<?, ?>>();

    public EntityHandler(Class<T> clazz) {
        super(clazz);
        data = new HashMap<String, Object>();
    }

    EntityHandler(Class<T> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
            return method.invoke(this, args);
        } else if ("getParent".equals(method.getName())) {
            return getParent();
        } else if (!meta.containsKey(method.getName())) {
            IObject<?, ?> entity = null;
            Class<?>[] interfaces = new Class[] { method.getReturnType() };
            if (IPrimitive.class.equals(method.getReturnType())) {
                entity = (IObject<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new PrimitiveHandler((IEntity) proxy,
                        method.getName()));

            } else if (ISet.class.equals(method.getReturnType())) {
                entity = (IObject<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new SetHandler((IEntity) proxy, method
                        .getName()));

            } else {
                entity = (IObject<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new EntityHandler(method.getReturnType(),
                        (IEntity) proxy, method.getName()));

            }
            meta.put(method.getName(), entity);
        }
        return meta.get(method.getName());

    }

    @Override
    public Map<String, Object> getValue() {
        return data;
    }

    @Override
    public void setValue(Map<String, Object> value) {
        this.data = value;
        getParent().getValue().put(getFieldName(), value);
    }

    @Override
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public boolean isNull() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void set(T entity) {
        setValue(entity.getValue());
    }

    @Override
    public String toString() {
        return getObjectClass().getSimpleName() + getValue();
    }
}