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

import java.util.Date;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompareHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class PrimitiveHandler<TYPE> extends ObjectHandler<TYPE> implements IPrimitive<TYPE> {

    private static final long serialVersionUID = 5565143015625424503L;

    //Avoid problems in some java code parsing implementations.
    public static final Class<?> BYTE_ARRAY_CLASS = byte[].class;

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
        ((SharedEntityHandler) getOwner()).setMemberValue(getFieldName(), value);
    }

    @Override
    public TYPE parse(String value) {
        return parsString(valueClass, value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <TYPE> TYPE parsString(Class<TYPE> valueClass, String value) {
        if (CommonsStringUtils.isEmpty(value)) {
            return null;
        }
        TYPE converted;
        if (valueClass.equals(String.class)) {
            converted = (TYPE) value;
        } else if (valueClass.equals(Double.class)) {
            converted = (TYPE) Double.valueOf(value);
        } else if (valueClass.equals(Float.class)) {
            converted = (TYPE) Float.valueOf(value);
        } else if (valueClass.equals(Long.class)) {
            converted = (TYPE) Long.valueOf(value);
        } else if (valueClass.equals(Integer.class)) {
            converted = (TYPE) Integer.valueOf(value);
        } else if (valueClass.equals(java.sql.Date.class)) {
            converted = (TYPE) java.sql.Date.valueOf(value);
        } else if (valueClass.equals(LogicalDate.class)) {
            converted = (TYPE) LogicalDate.valueOf(value);
        } else if (valueClass.isEnum()) {
            converted = (TYPE) Enum.valueOf((Class<Enum>) valueClass, value);
        } else if (valueClass.equals(Boolean.class)) {
            converted = (TYPE) Boolean.valueOf(value);
        } else if (valueClass.equals(Short.class)) {
            converted = (TYPE) Short.valueOf(value);
        } else if (valueClass.equals(Byte.class)) {
            converted = (TYPE) Byte.valueOf(value);
        } else {
            throw new RuntimeException("Unsupported type " + valueClass.getName());
        }
        return converted;
    }

    @Override
    public Class<TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public boolean isNull() {
        if (!getOwner().containsMemberValue(getFieldName())) {
            return true;
        } else {
            return (getValue() == null);
        }
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
        if ((other == null) || (!(other instanceof IPrimitive<?>)) || (!this.getValueClass().equals(((IPrimitive<?>) other).getValueClass()))) {
            return false;
        } else if (isNull()) {
            return (((IPrimitive<?>) other).isNull());
        } else {
            return this.getValue().equals(((IPrimitive<?>) other).getValue());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compareTo(IPrimitive<TYPE> other) {
        if (other == this) {
            return 0;
        }
        TYPE thisValue = this.getValue();
        if (thisValue == null) {
            return (other.getValue() == null) ? 0 : -1;
        } else if (thisValue instanceof Comparable) {
            return CompareHelper.compareTo((Comparable) thisValue, other.getValue());
        } else {
            throw new ClassCastException("Unsupported type " + thisValue.getClass().getName());
        }
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
    public boolean isBooleanTrue() {
        if (isNull()) {
            return false;
        } else {
            if (valueClass.equals(Boolean.class)) {
                return (Boolean) getValue();
            } else {
                TYPE thisValue = this.getValue();
                if (thisValue instanceof Number) {
                    return 0 != ((Number) thisValue).intValue();
                } else {
                    return Boolean.valueOf(thisValue.toString());
                }
            }
        }
    }

    @Override
    public String getStringView() {
        MemberMeta mm = getMeta();
        String format = mm.getFormat();
        TYPE thisValue = this.getValue();
        if (thisValue == null) {
            return mm.getNullString();
        } else if (format == null) {
            return String.valueOf(thisValue);
        }
        if (mm.useMessageFormat()) {
            return SimpleMessageFormat.format(format, thisValue);
        } else {
            if (thisValue instanceof Date) {
                return SimpleMessageFormat.format("{0,date," + format + "}", thisValue);
            } else if (thisValue instanceof Number) {
                return SimpleMessageFormat.format("{0,number," + format + "}", thisValue);
            } else {
                return String.valueOf(thisValue);
            }
        }
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + " " + getValue();
    }

}