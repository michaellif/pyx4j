/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.StringLength;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public class MemberMetaImpl implements MemberMeta {

    private final String fieldName;

    private final boolean ownedRelationships;

    private final Class<?> valueClass;

    private final Class<? extends IObject<?, ?>> objectClass;

    private final String caption;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String description;

    /**
     * See com.pyx4j.entity.annotations.StringLength
     */
    private final int stringLength;

    public MemberMetaImpl(Method method) {
        objectClass = (Class<? extends IObject<?, ?>>) method.getReturnType();
        if (IPrimitive.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        } else if (ISet.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        } else if (IEntity.class.isAssignableFrom(objectClass)) {
            valueClass = objectClass;
        } else {
            throw new RuntimeException("Unknown member type" + objectClass);
        }
        fieldName = method.getName();

        // Read Annotations
        StringLength stringLengthAnnotation = method.getAnnotation(StringLength.class);
        if (String.class.equals(objectClass)) {
            if (stringLengthAnnotation != null) {
                stringLength = stringLengthAnnotation.value();
            } else {
                stringLength = 0;
            }
        } else if (stringLengthAnnotation != null) {
            throw new RuntimeException("Unexpected @StringLength annotation in  memeber " + fieldName);
        } else {
            stringLength = 0;
        }

        Caption captionAnnotation = method.getAnnotation(Caption.class);
        if ((captionAnnotation != null) && (CommonsStringUtils.isStringSet(captionAnnotation.name()))) {
            caption = captionAnnotation.name();
        } else {
            caption = fieldName;
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
        } else {
            description = null;
        }

        ownedRelationships = (method.getAnnotation(Owned.class) != null);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOwnedRelationships() {
        return ownedRelationships;
    }

    @Override
    public Class<?> getValueClass() {
        return valueClass;
    }

    @Override
    public Class<? extends IObject<?, ?>> getObjectClass() {
        return objectClass;
    }

    @Override
    public int getStringLength() {
        return stringLength;
    }

    @Override
    public List<Validator> getValidators() {
        // TODO Auto-generated method stub
        return null;
    }

}
