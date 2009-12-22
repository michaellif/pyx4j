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
import com.pyx4j.entity.shared.IEntityHandler;
import com.pyx4j.entity.shared.IObject;

public abstract class EntityHandler<T extends IEntity<?, ?>> implements IEntityHandler<T>, InvocationHandler {

    private final Class<T> clazz;

    private IObject<?> parent;

    private ObjectHandler<?> parentHandler;

    private String fieldName;

    public EntityHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    public EntityHandler(Class<T> clazz, ObjectHandler<?> parentHandler, IObject<?> parent, String fieldName) {
        this.clazz = clazz;
        this.parent = parent;
        this.parentHandler = parentHandler;
        this.fieldName = fieldName;
    }

    public IObject<?> getParent() {
        return parent;
    }

    public ObjectHandler<?> getParentHandler() {
        return parentHandler;
    }

    public Class<T> getEntityClass() {
        return clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

}
