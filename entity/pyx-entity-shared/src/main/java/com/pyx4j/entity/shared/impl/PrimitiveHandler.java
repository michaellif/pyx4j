/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

public class PrimitiveHandler<TYPE> extends ObjectHandler<IPrimitive<TYPE>, TYPE> implements IPrimitive<TYPE> {

    private final Class<TYPE> valueClass;

    public PrimitiveHandler(IEntity<?> parent, String fieldName, Class<TYPE> valueClass) {
        super(IPrimitive.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TYPE getValue() {
        return (TYPE) getParent().getMemberValue(getFieldName());
    }

    @Override
    public void setValue(TYPE value) {
        Map<String, Object> data = getParent().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParent().setValue(data);
        }
        data.put(getFieldName(), value);
    }

    public Class<TYPE> getValueClass() {
        return valueClass;
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
    public void set(IPrimitive<TYPE> entity) {
        setValue(entity.getValue());

    }

    //TODO
    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }

}