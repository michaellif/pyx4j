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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IEntityHandler;
import com.pyx4j.entity.shared.IEntity;

public abstract class ObjectHandler<T extends IObject<?, ?>> implements IEntityHandler<T>, InvocationHandler {

    private final Class<T> clazz;

    private IEntity<?> parent;

    private EntityHandler<?> parentHandler;

    private String fieldName;

    public ObjectHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectHandler(Class<T> clazz, EntityHandler<?> parentHandler, IEntity<?> parent, String fieldName) {
        this.clazz = clazz;
        this.parent = parent;
        this.parentHandler = parentHandler;
        this.fieldName = fieldName;
    }

    public IEntity<?> getParent() {
        return parent;
    }

    public EntityHandler<?> getParentHandler() {
        return parentHandler;
    }

    public Class<T> getEntityClass() {
        return clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

}
