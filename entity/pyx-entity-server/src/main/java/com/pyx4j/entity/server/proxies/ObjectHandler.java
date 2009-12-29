/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server.proxies;

import java.lang.reflect.InvocationHandler;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

public abstract class ObjectHandler<OBJECT_CLASS extends IObject, VALUE_TYPE> implements IObject<OBJECT_CLASS, VALUE_TYPE>, InvocationHandler {

    private final Class<? extends IObject> clazz;

    private IEntity<?> parent;

    private String fieldName;

    public ObjectHandler(Class<OBJECT_CLASS> clazz) {
        this.clazz = clazz;
    }

    public ObjectHandler(Class<? extends IObject> clazz, IEntity<?> parent, String fieldName) {
        this.clazz = clazz;
        this.parent = parent;
        this.fieldName = fieldName;
    }

    public IEntity<?> getParent() {
        return parent;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<? extends IObject> getObjectClass() {
        return clazz;
    }

}
