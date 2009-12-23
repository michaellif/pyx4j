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
import com.pyx4j.entity.shared.IOwnedMember;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class ObjectHandler<T extends IObject<?>> extends EntityHandler<T> implements IObject<T> {

    private Map<String, Object> data;

    private final HashMap<String, IEntity<?, ?>> meta = new HashMap<String, IEntity<?, ?>>();

    public ObjectHandler(Class<T> clazz) {
        super(clazz);
        data = new HashMap<String, Object>();
    }

    ObjectHandler(Class<T> clazz, ObjectHandler<?> parentHandler, IObject<?> parent, String fieldName) {
        super(clazz, parentHandler, parent, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IObject.class)) {
            return method.invoke(this, args);
        } else if (method.getDeclaringClass().equals(IOwnedMember.class)) {
            if ("getParent".equals(method.getName())) {
                return getParent();
            } else {
                return null;
            }

        } else if (!meta.containsKey(method.getName())) {
            IEntity<?, ?> entity = null;
            if (method.getReturnType().isAssignableFrom(IPrimitive.class)) {
                Class<?>[] interfaces = new Class[] { method.getReturnType() };
                entity = (IEntity<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new PrimitiveHandler(method
                        .getReturnType(), this, (IObject) proxy, method.getName()));

            } else if (method.getReturnType().isAssignableFrom(ISet.class)) {
                Class<?>[] interfaces = new Class[] { method.getReturnType() };
                entity = (IEntity<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new SetHandler(method.getReturnType(),
                        this, (IObject) proxy, method.getName()));

            } else {
                Class<?>[] interfaces = new Class[] { method.getReturnType() };
                entity = (IEntity<?, ?>) Proxy.newProxyInstance(method.getReturnType().getClassLoader(), interfaces, new ObjectHandler(method.getReturnType(),
                        this, (IObject) proxy, method.getName()));

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
        getParentHandler().getValue().put(getFieldName(), value);
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
        return getEntityClass().getSimpleName() + getValue();
    }
}