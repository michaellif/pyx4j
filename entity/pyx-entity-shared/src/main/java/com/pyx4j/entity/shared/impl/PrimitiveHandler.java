/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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

    public PrimitiveHandler(IEntity<?> parent, String fieldName) {
        super(IPrimitive.class, parent, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TYPE getValue() {
        return (TYPE) getParent().getValue().get(getFieldName());
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

    @Override
    public String toString() {
        return getObjectClass().getSimpleName() + getValue();
    }

}