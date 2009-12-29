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
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

public class PrimitiveHandler<T extends IPrimitive<?, ?>> extends ObjectHandler<T> implements IPrimitive<Object, IEntity<?>> {

    PrimitiveHandler(Class<T> clazz, EntityHandler<?> parentHandler, IEntity<?> parent, String fieldName) {
        super(clazz, parentHandler, parent, fieldName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IPrimitive.class)) {
            return method.invoke(this, args);
        } else if ("getParent".equals(method.getName())) {
            return getParent();
        } else {
            return null;
        }
    }

    @Override
    public Object getValue() {
        return getParentHandler().getValue().get(getFieldName());
    }

    @Override
    public void setValue(Object value) {
        Map<String, Object> data = getParentHandler().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParentHandler().setValue(data);
        }
        data.put(getFieldName(), value);
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
    public void set(IPrimitive<Object, IEntity<?>> entity) {
        setValue(entity.getValue());

    }

    @Override
    public String toString() {
        return getEntityClass().getSimpleName() + getValue();
    }

}