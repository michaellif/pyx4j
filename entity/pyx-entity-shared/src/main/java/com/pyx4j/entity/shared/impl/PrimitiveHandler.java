/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public class PrimitiveHandler<TYPE> extends ObjectHandler<TYPE> implements IPrimitive<TYPE> {

    private static final long serialVersionUID = 5565143015625424503L;

    private final Class<TYPE> valueClass;

    public PrimitiveHandler(IEntity parent, String fieldName, Class<TYPE> valueClass) {
        super(IPrimitive.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TYPE getValue() {
        return (TYPE) getOwner().getMemberValue(getFieldName());
    }

    @Override
    public void setValue(TYPE value) {
        ((SharedEntityHandler) getOwner()).ensureValue().put(getFieldName(), value);
    }

    @Override
    public Class<TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public boolean isNull() {
        return (getValue() == null);
    }

    @Override
    public void set(IPrimitive<TYPE> entity) {
        setValue(entity.getValue());

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        TYPE thisValue = this.getValue();
        if ((other == null) || (thisValue == null) || (!(other instanceof IPrimitive<?>))
                || (!this.getValueClass().equals(((IPrimitive<?>) other).getValueClass()))) {
            return false;
        }
        return thisValue.equals(((IPrimitive<?>) other).getValue());
    }

    @Override
    public int hashCode() {
        TYPE thisValue = this.getValue();
        if (thisValue == null) {
            return super.hashCode();
        } else {
            return thisValue.hashCode();
        }
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }

}