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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.StringLength;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.Unindexed;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public class MemberMetaImpl implements MemberMeta {

    private final Method method;

    private final String fieldName;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private final boolean detached;

    private final boolean ownedRelationships;

    private final boolean owner;

    private final boolean embedded;

    private final boolean indexed;

    private final boolean entity;

    private final Class<?> valueClass;

    private final Class<? extends IObject<?>> objectClass;

    private final String caption;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String description;

    /**
     * See com.pyx4j.entity.annotations.StringLength
     */
    private final int stringLength;

    @SuppressWarnings("unchecked")
    public MemberMetaImpl(Method method) {
        this.method = method;
        objectClass = (Class<? extends IObject<?>>) method.getReturnType();
        if (IPrimitive.class.equals(objectClass)) {
            valueClass = EntityImplReflectionHelper.primitiveValueClass(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
            entity = false;
        } else if (ISet.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            entity = false;
        } else if (IList.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            entity = false;
        } else if (IEntity.class.isAssignableFrom(objectClass)) {
            valueClass = objectClass;
            entity = true;
        } else if (IPrimitiveSet.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            entity = false;
        } else {
            throw new RuntimeException("Unknown member type" + objectClass);
        }
        fieldName = method.getName();

        // Read Annotations
        StringLength stringLengthAnnotation = method.getAnnotation(StringLength.class);
        if (String.class.equals(valueClass)) {
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
            caption = EnglishGrammar.capitalize(fieldName);
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
        } else {
            description = null;
        }

        persistenceTransient = (method.getAnnotation(Transient.class) != null);
        rpcTransient = (method.getAnnotation(RpcTransient.class) != null);
        embedded = (valueClass.getAnnotation(EmbeddedEntity.class) != null) || (method.getAnnotation(EmbeddedEntity.class) != null);
        ownedRelationships = embedded || (method.getAnnotation(Owned.class) != null);
        owner = (method.getAnnotation(Owner.class) != null);
        detached = (method.getAnnotation(Detached.class) != null);
        indexed = (method.getAnnotation(Unindexed.class) == null);
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
    public boolean isTransient() {
        return persistenceTransient;
    }

    @Override
    public boolean isRpcTransient() {
        return rpcTransient;
    }

    @Override
    public boolean isDetached() {
        return detached;
    }

    @Override
    public boolean isOwnedRelationships() {
        return ownedRelationships;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    @Override
    public boolean isIndexed() {
        return indexed;
    }

    @Override
    public boolean isEntity() {
        return entity;
    }

    @Override
    public Class<?> getValueClass() {
        return valueClass;
    }

    @Override
    public Class<? extends IObject<?>> getObjectClass() {
        return objectClass;
    }

    @Override
    public int getStringLength() {
        return stringLength;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    @Override
    public String getFormat() {
        Format formatAnnotation = method.getAnnotation(Format.class);
        if (formatAnnotation != null) {
            return formatAnnotation.value();
        } else {
            return null;
        }
    }

    @Override
    public EditorType getEditorType() {
        Editor editorAnnotation = method.getAnnotation(Editor.class);
        if (editorAnnotation == null) {
            return null;
        } else {
            return editorAnnotation.type();
        }
    }

    @Override
    public List<Validator> getValidators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValidatorAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

}
