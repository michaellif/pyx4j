/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Oct 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;

@SuppressWarnings("unchecked")
public abstract class ObjectHandler<OBJECT_TYPE extends IObject, VALUE_TYPE> implements IObject<OBJECT_TYPE, VALUE_TYPE> {

    private final Class<? extends IObject> clazz;

    private IEntity<?> parent;

    private String fieldName;

    public ObjectHandler(Class<OBJECT_TYPE> clazz) {
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

    @Override
    public MemberMeta getMeta() {
        return getParent().getMemberMeta(getFieldName());
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<? extends IObject> getObjectClass() {
        return clazz;
    }

}
