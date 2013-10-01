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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.CompareHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Pair;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.PasswordSerializable;

public class PrimitiveHandler<TYPE extends Serializable> extends ObjectHandler<TYPE> implements IPrimitive<TYPE> {

    private static final long serialVersionUID = 5565143015625424503L;

    private static final I18n i18n = I18n.get(PrimitiveHandler.class);

    //Avoid problems in some java code parsing implementations.
    public static final Class<?> BYTE_ARRAY_CLASS = byte[].class;

    private final Class<TYPE> valueClass;

    private static final String trueTextEnglish = "yes";

    private static final String falseTextEnglish = "no";

    private static final String dateFormat = defaultDateFormat();

    public PrimitiveHandler(IEntity parent, String fieldName, Class<TYPE> valueClass) {
        super(IPrimitive.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @I18nComment("As an answer to a question")
    private static final String i18nNoText() {
        return i18n.tr("No");
    }

    @I18nComment("As an answer to a question")
    private static final String i18nYesText() {
        return i18n.tr("Yes");
    }

    @I18nContext(javaFormatFlag = true)
    private static final String defaultDateFormat() {
        return i18n.tr("MM/dd/yyyy");
    }

    @SuppressWarnings("unchecked")
    @Override
    public TYPE getValue() {
        return (TYPE) getOwner().getMemberValue(getFieldName());
    }

    @Override
    public void setValue(TYPE value) {
        assert (value == null || isAssignableFrom(value)) : "IPrimitive of " + valueClass + " is not assignable from " + value.getClass();
        ((SharedEntityHandler) getOwner()).setMemberValue(getFieldName(), value);
    }

    @Override
    public AttachLevel getAttachLevel() {
        if (getOwner().isValueDetached()) {
            return AttachLevel.Detached;
        } else {
            return AttachLevel.Attached;
        }
    }

    @Override
    public void setAttachLevel(AttachLevel level) {
        throw new IllegalArgumentException("Not implemented");
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
            if (trueTextEnglish.equals(value) || i18nYesText().equals(value)) {
                converted = (TYPE) Boolean.TRUE;
            } else if (falseTextEnglish.equals(value) || i18nNoText().equals(value)) {
                converted = (TYPE) Boolean.FALSE;
            } else {
                converted = (TYPE) Boolean.valueOf(value);
            }
        } else if (valueClass.equals(Short.class)) {
            converted = (TYPE) Short.valueOf(value);
        } else if (valueClass.equals(BigDecimal.class)) {
            converted = (TYPE) new BigDecimal(value);
        } else if (valueClass.equals(Byte.class)) {
            converted = (TYPE) Byte.valueOf(value);
        } else {
            throw new RuntimeException("Unsupported type " + valueClass.getName());
        }
        return converted;
    }

    private boolean isAssignableFrom(Object value) {
        if (valueClass.equals(String.class)) {
            return (value instanceof String);
        } else if (valueClass.equals(Double.class)) {
            return (value instanceof Double);
        } else if (valueClass.equals(Float.class)) {
            return (value instanceof Float);
        } else if (valueClass.equals(Long.class)) {
            return (value instanceof Long);
        } else if (valueClass.equals(Key.class)) {
            return (value instanceof Key);
        } else if (valueClass.equals(Integer.class)) {
            return (value instanceof Integer);
        } else if (valueClass.equals(java.util.Date.class)) {
            return (value instanceof java.util.Date);
        } else if (valueClass.equals(java.sql.Date.class)) {
            return (value instanceof java.sql.Date);
        } else if (valueClass.equals(java.sql.Time.class)) {
            return (value instanceof java.sql.Time);
        } else if (valueClass.equals(LogicalDate.class)) {
            return (value instanceof LogicalDate);
        } else if (valueClass.isEnum()) {
            return (value instanceof Enum);
        } else if (valueClass.equals(Boolean.class)) {
            return (value instanceof Boolean);
        } else if (valueClass.equals(BigDecimal.class)) {
            return (value instanceof BigDecimal);
        } else if (valueClass.equals(Short.class)) {
            return (value instanceof Short);
        } else if (valueClass.equals(Byte.class)) {
            return (value instanceof Byte);
        } else if (valueClass.equals(BYTE_ARRAY_CLASS)) {
            return (value instanceof byte[]);
        } else if (valueClass.equals(GeoPoint.class)) {
            return (value instanceof GeoPoint);
        } else if (valueClass.equals(Pair.class)) {
            return (value instanceof Pair);
        } else if (valueClass.equals(PasswordSerializable.class)) {
            return (value instanceof PasswordSerializable);
        } else {
            throw new RuntimeException("Unsupported type " + valueClass.getName());
        }
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
    public void set(IPrimitive<TYPE> object) {
        if (object == null) {
            setValue(null);
        } else {
            setValue(object.getValue());
        }

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other == null) || (!(other instanceof IPrimitive<?>)) || (!this.getValueClass().equals(((IPrimitive<?>) other).getValueClass()))) {
            return false;
        } else if (isNull()) {
            //Assert value is not detached, simple trick just to call the function and discard result
            assert getValue() != this;
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
    public TYPE getValue(TYPE defaultValueIfNull) {
        if (isNull()) {
            return defaultValueIfNull;
        } else {
            return this.getValue();
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
            if (valueClass.equals(Boolean.class)) {
                return i18nYesText();
            } else {
                return mm.getNullString();
            }
        } else if (format == null) {
            if (valueClass.equals(Boolean.class)) {
                return isBooleanTrue() ? i18nYesText() : i18nNoText();
            } else if (thisValue instanceof Date) {
                // TODO Add global variable for user preference
                format = dateFormat;
            } else {
                return String.valueOf(thisValue);
            }
        }
        if (mm.useMessageFormat()) {
            return SimpleMessageFormat.format(format, thisValue);
        } else {
            if (thisValue instanceof Date) {
                return SimpleMessageFormat.format("{0,date," + format + "}", thisValue);
            } else if (thisValue instanceof Number) {
                return SimpleMessageFormat.format("{0,number," + format + "}", thisValue);
            } else if (valueClass.equals(Boolean.class)) {
                return SimpleMessageFormat.format("{0,choice," + format + "}", thisValue);
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