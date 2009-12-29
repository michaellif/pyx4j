/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

public abstract class SharedEntityHandler<OBJECT_TYPE extends IEntity<?>> extends ObjectHandler<OBJECT_TYPE, Map<String, Object>> implements
        IEntity<OBJECT_TYPE> {

    private Map<String, Object> data;

    protected final HashMap<String, IObject<?, ?>> meta = new HashMap<String, IObject<?, ?>>();

    public SharedEntityHandler(Class<OBJECT_TYPE> clazz) {
        super(clazz);
        data = new HashMap<String, Object>();
    }

    public SharedEntityHandler(Class<? extends IObject> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
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
    public void set(OBJECT_TYPE entity) {
        setValue(entity.getValue());
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }
}
